package com.fiberhome.filink.lockserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DeviceConfigFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.api.StatisticsFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.bean.SensorInfo;
import com.fiberhome.filink.deviceapi.bean.UpdateDeviceStatusPda;
import com.fiberhome.filink.deviceapi.util.DeployStatus;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinkoceanconnectapi.bean.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import com.fiberhome.filink.lockserver.bean.*;
import com.fiberhome.filink.lockserver.constant.ConstantParam;
import com.fiberhome.filink.lockserver.constant.FunctionCode;
import com.fiberhome.filink.lockserver.constant.cmd.CmdId;
import com.fiberhome.filink.lockserver.constant.cmd.FiLinkReqParamsDto;
import com.fiberhome.filink.lockserver.constant.control.ControlI18n;
import com.fiberhome.filink.lockserver.constant.control.ControlResultCode;
import com.fiberhome.filink.lockserver.constant.control.SyncStatusType;
import com.fiberhome.filink.lockserver.dao.ControlDao;
import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import com.fiberhome.filink.lockserver.exception.FiLinkControlIdReusedException;
import com.fiberhome.filink.lockserver.exception.FiLinkControlIsNullException;
import com.fiberhome.filink.lockserver.exception.FiLinkDeviceIsNullException;
import com.fiberhome.filink.lockserver.exception.FiLinkDeviceOperateException;
import com.fiberhome.filink.lockserver.exception.FiLinkSystemException;
import com.fiberhome.filink.lockserver.service.ControlService;
import com.fiberhome.filink.lockserver.service.LockService;
import com.fiberhome.filink.lockserver.stream.CodeSender;
import com.fiberhome.filink.lockserver.stream.ControlSender;
import com.fiberhome.filink.lockserver.util.AuthUtils;
import com.fiberhome.filink.lockserver.util.CheckUtil;
import com.fiberhome.filink.lockserver.util.ListUtils;
import com.fiberhome.filink.lockserver.util.OperateLogUtils;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.onenetapi.bean.DeleteDevice;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.fiberhome.filink.lockserver.constant.ConstantParam.ADMIN;
import static com.fiberhome.filink.lockserver.constant.ConstantParam.DATA;
import static com.fiberhome.filink.lockserver.constant.ConstantParam.DATA_CLASS;
import static com.fiberhome.filink.lockserver.constant.ConstantParam.DEPLOY_STATUS;
import static com.fiberhome.filink.lockserver.constant.ConstantParam.PARAMS;
import static com.fiberhome.filink.lockserver.constant.ConstantParam.PASSIVE_HOST;
import static com.fiberhome.filink.lockserver.enums.PlatFormType.OceanConnect;
import static com.fiberhome.filink.lockserver.enums.PlatFormType.OneNet;

/**
 * 主控信息实现类
 *
 * @author CongcaiYu
 */
@Service
@Slf4j
public class ControlServiceImpl implements ControlService {

    @Autowired
    private ControlDao controlDao;

    @Autowired
    private LockService lockService;

    @Autowired
    private DeviceFeign deviceFeign;


    @Autowired
    private DeviceConfigFeign deviceConfigFeign;

    @Autowired
    private CodeSender codeSender;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private OperateLogUtils logUtils;

    @Autowired
    private OceanConnectFeign oceanConnectFeign;

    @Autowired
    private OneNetFeign oneNetFeign;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StatisticsFeign statisticsFeign;

    @Autowired
    private ControlSender controlSender;

    /**
     * 根据设施id查询主控信息(不需要权限)
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @Override
    public List<ControlParam> getControlInfoByDeviceId(String deviceId) {
        return controlDao.getControlByDeviceId(deviceId);
    }

    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @Override
    public List<ControlParam> getControlInfoByDeviceIdToCall(String deviceId) {
        //数据权限校验
        List<ControlParam> controlParamList = new ArrayList<>();
        String userId = RequestInfoUtils.getUserId();
        if (userId != null && !ADMIN.equals(userId)) {
            try {
                authUtils.addAuth(deviceId);
            } catch (Exception e) {
                log.info("没有查看该设施的主控权限");
                return controlParamList;
            }
        }
        controlParamList = getControlInfoByDeviceId(deviceId);
        return controlParamList;
    }


    /**
     * 根据设施id和主控名称查询主控信息
     *
     * @param controlParam 主控信息参数
     * @return 主控信息
     */
    @Override
    public ControlParam getControlByDeviceIdAndControlName(ControlParam controlParam) {
        return controlDao.getControlByDeviceIdAndControlName(controlParam);
    }


    /**
     * 根据设施id查询主控信息 for lock
     *
     * @param deviceId 设施id
     * @return 主控信息 for device
     */
    @Override
    public List<ControlParamForControl> getControlParamForControlByDeviceId(String deviceId) {
        //查询主控信息
        List<ControlParam> controlParamList = getControlInfoByDeviceIdToCall(deviceId);
        //pda需要的主控信息集合
        return ListUtils.copyList(controlParamList, ControlParamForControl.class);
    }

    /**
     * 根据主控id查询电子锁主控信息
     *
     * @param controlReq 主控信息请求对象
     * @return 查询结果
     */
    @Override
    public Result queryControlByControlId(ControlReq controlReq) {
        //获得主控关联的设施信息
        DeviceInfoForLock deviceInfo = new DeviceInfoForLock();
        //pda 查询电子锁主控信息 所需的数据结构
        ControlParamForControl controlParamForControl = new ControlParamForControl();
        //查询主控信息
        List<ControlParam> controlParamList = controlDao.queryControlParam(controlReq);
        if (controlParamList != null && controlParamList.size() > 0) {
            //根据主控id查询电子锁主控信息,最多有一条记录
            ControlParam controlParam = controlParamList.get(0);
            //判断是否有该电子锁设施查看权限
            authUtils.hasControlDeviceAuth(controlParam.getDeviceId());
            //获得主控关联的设施信息
            deviceInfo = findDeviceInfoForLockByDeviceId(controlParam.getDeviceId());
            BeanUtils.copyProperties(controlParam, controlParamForControl);
            //根据主控id查询锁信息
            List<Lock> locks = lockService.queryLockByControlId(controlParam.getHostId());
            List<DoorForPda> doorForPdaList = new ArrayList<>(64);
            for (Lock lock : locks) {
                //所需锁信息的数据结构，转换
                DoorForPda doorForPda = new DoorForPda();
                BeanUtils.copyProperties(lock, doorForPda);
                doorForPdaList.add(doorForPda);
            }
            //设施映射关系
            controlParamForControl.setLockList(doorForPdaList);
            deviceInfo.setControlList(new ArrayList<ControlParamForControl>(16) {{
                add(controlParamForControl);
            }});
        }

        return ResultUtils.success(deviceInfo);
    }


    /**
     * 根据id查询主控信息
     *
     * @param controlId
     * @return 主控信息
     */
    @Override
    public ControlParam getControlParamById(String controlId) {
        ControlParam controlParam = controlDao.getControlParamById(controlId);
        if (controlParam == null) {
            throw new FiLinkControlIsNullException();
        }
        return controlParam;
    }


    /**
     * 新增主控信息
     *
     * @param controlParam 主控信息
     */
    @Override
    public void addControlParam(ControlParam controlParam) {
        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(controlParam.getDeviceId());

        //获得主控类型
        String hostType = controlParam.getHostType();
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId(controlParam.getDeviceId());
        if (hostType.equals(PASSIVE_HOST)) {
            //无源锁，主控设施状态为正常，部署状态为无，
            //需要修改设施状态为正常(设施有其他主控，不用更改，之前改过)，部署状态不用更改
            controlParam.setDeviceStatus(DeviceStatus.Normal.getCode());
            controlParam.setDeployStatus(DeployStatus.NONE.getCode());
            deviceInfoDto.setDeviceStatus(DeviceStatus.Normal.getCode());

        } else {
            //有源锁，主控设施状态为未配置，部署状态为布防，
            // 不需要修改设施的设施状态，要修改部署状态为最高显示级别的部署状态
            controlParam.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
            controlParam.setDeployStatus(DeployStatus.DEPLOYED.getCode());
            Set<String> deployStatusSet = new TreeSet<>();
            deployStatusSet.add(DeployStatus.DEPLOYED.getCode());
            List<ControlParam> controlParamList = controlDao.getDeployStatusById(controlParam.getHostId());
            for (ControlParam item : controlParamList) {
                deployStatusSet.add(item.getDeployStatus());
            }
            List<String> deployStatusList = new ArrayList<>(deployStatusSet);
            //获得最高显示级别的部署状态
            String deployStatus = deployStatusList.get(deployStatusList.size() - 1);
            deviceInfoDto.setDeployStatus(deployStatus);
        }
        //更新设施的设施状态及部署状态
        updateDeviceStatus(deviceInfoDto);
        //新增
        controlDao.addControlParams(controlParam);
        //记录日志
        logUtils.addSimpleOperateLog(deviceInfo, FunctionCode.ADD_CONTROL_INFO, LogConstants.OPT_TYPE_PDA);

    }

    /**
     * 更新主控信息
     *
     * @param controlParam 主控信息
     * @param type         操作类型
     */
    @Override
    public void updateControlParam(ControlParam controlParam, String type) {
        //更新主控信息
        controlDao.updateControlParams(controlParam);
        addSensor(controlParam);
        //记录日志,仅在pda操作时执行
        if (type.equals(LogConstants.OPT_TYPE_PDA)) {
            //检验主控是否存在
            ControlParam oldControlParam = getControlParamById(controlParam.getHostId());
            //主控名称
            String hostName = oldControlParam.getHostName();
            String newHostName = controlParam.getHostName();
            if (!StringUtils.isEmpty(hostName)
                    && !StringUtils.isEmpty(newHostName)
                    && !hostName.equals(newHostName)) {
                //修改主控名称，需要删除缓存
                List<String> controlIdList = new ArrayList<>();
                controlIdList.add(controlParam.getHostId());
                ControlDel controlDel = new ControlDel();
                controlDel.setHostIdList(controlIdList);
                controlDel.setOperate(ConstantParam.UPDATE_STATE);
                controlSender.send(JSON.toJSONString(controlDel));
            }
            //查询设施信息
            String deviceName = deviceFeign.queryDeviceNameById(oldControlParam.getDeviceId());
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            deviceInfoDto.setDeviceId(oldControlParam.getDeviceId());
            deviceInfoDto.setDeviceName(deviceName);
            logUtils.addSimpleOperateLog(deviceInfoDto, FunctionCode.UPDATE_CONTROL_INFO, type);
        }
    }


    /**
     * 根据设施id更新主控的部署状态
     *
     * @param deviceIds    设施id集合
     * @param deployStatus 部署状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeployStatusByDeviceId(List<String> deviceIds, String deployStatus) {
        //判断所选设施中是否包含没有主控信息的设施
        if (!checkDeviceHasControl(deviceIds)) {
            throw new FiLinkDeviceOperateException();
        }
        //构造指令开锁参数
        List<FiLinkReqParamsDto> reqParamsDtoList = new ArrayList<>();

        //查询设施信息
        List<DeviceInfoDto> deviceInfoDtoList = queryDeviceListByIds(deviceIds);

        for (String deviceId : deviceIds) {
            List<ControlParam> controlInfoList = getControlInfoByDeviceId(deviceId);
            //部署状态的构造指令
            for (ControlParam controlParam : controlInfoList) {
                FiLinkReqParamsDto fiLinkReqParamsDto = new FiLinkReqParamsDto();
                fiLinkReqParamsDto.setCmdId(CmdId.SET_DEPLOY_STATUS);
                fiLinkReqParamsDto.setEquipmentId(controlParam.getHostId());
                fiLinkReqParamsDto.setHardwareVersion(controlParam.getHardwareVersion());
                fiLinkReqParamsDto.setSoftwareVersion(controlParam.getSoftwareVersion());
                fiLinkReqParamsDto.setImei(controlParam.getImei());
                fiLinkReqParamsDto.setPlateFormId(controlParam.getPlatformId());
                fiLinkReqParamsDto.setPlateForm(controlParam.getCloudPlatform());
                fiLinkReqParamsDto.setAppId(controlParam.getProductId());
                Map<String, Object> param = new HashMap<>(64);
                param.put(DEPLOY_STATUS, deployStatus);
                fiLinkReqParamsDto.setParams(param);
                reqParamsDtoList.add(fiLinkReqParamsDto);
            }

        }

        controlDao.updateDeployStatusByDeviceId(deviceIds, DeployStatus.DEPLOYING.getCode());

        // 更新设施的部署状态为未配置
        UpdateDeviceStatusPda updateDeviceStatusPda = new UpdateDeviceStatusPda();
        updateDeviceStatusPda.setDeviceIdList(deviceIds);
        updateDeviceStatusPda.setDeployStatus(DeployStatus.DEPLOYING.getCode());
        Result result = deviceFeign.updateDeviceListStatus(updateDeviceStatusPda);
        if (result == null) {
            throw new FiLinkSystemException("调用deviceFeign更新设施的部署状态失败，事务回滚");
        }

        //发送指令
        codeSender.send(reqParamsDtoList);

        String functionCode = null;
        if (deployStatus.equals(DeployStatus.DEPLOYED.getCode())) {
            functionCode = FunctionCode.DEPLOY_CONTROL;
        } else if (deployStatus.equals(DeployStatus.NODEFENCE.getCode())) {
            functionCode = FunctionCode.NODEFENCE_CONTROL;
        } else if (deployStatus.equals(DeployStatus.NOTUSED.getCode())) {
            functionCode = FunctionCode.NOTUSED_CONTROL;
        } else if (deployStatus.equals(DeployStatus.MAINTENANCE.getCode())) {
            functionCode = FunctionCode.MAINTENANCE_CONTROL;
        }
        //记录日志
        logUtils.batchAddOperationLog(deviceInfoDtoList, functionCode, LogConstants.OPT_TYPE_WEB);
    }

    /**
     * 根据设施id删除主控
     *
     * @param deviceIds 设施id集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteControlByDeviceIds(List<String> deviceIds) {
        //有主控的设施集合
        List<String> deviceIdList = new ArrayList<>();
        //OceanConnect主控集合
        List<ControlParam> oceanConnectControl = new ArrayList<>();
        //OneNet主控集合
        List<ControlParam> oneNetControl = new ArrayList<>();
        //全部主控
        List<ControlParam> controlList = new ArrayList<>();

        for (String deviceId : deviceIds) {
            //获取设施所有主控
            List<ControlParam> controlParamList = getControlInfoByDeviceId(deviceId);
            if (!ObjectUtils.isEmpty(controlParamList)) {
                deviceIdList.add(deviceId);
                controlList.addAll(controlParamList);
                for (ControlParam controlParam : controlParamList) {
                    //根据云平台类型，甄选主控
                    String cloudPlatform = controlParam.getCloudPlatform();
                    if (OceanConnect.getCode().equals(cloudPlatform)) {
                        oceanConnectControl.add(controlParam);
                    } else if (OneNet.getCode().equals(cloudPlatform)) {
                        oneNetControl.add(controlParam);
                    }
                }
                List<Lock> lockList = lockService.queryLockByDeviceId(deviceId);
                if (!ObjectUtils.isEmpty(lockList)) {
                    List<String> doorNumList = lockList.stream().map(Lock::getDoorNum).collect(Collectors.toList());
                    //删除授权
                    lockService.deleteAuth(deviceId, doorNumList);
                }

            }
        }
        // 删除平台上的设备
        if (!ObjectUtils.isEmpty(oceanConnectControl)) {
            deleteFromOceanConnect(oceanConnectControl);
        }
        if (!ObjectUtils.isEmpty(oneNetControl)) {
            deleteFromOneNet(oneNetControl);
        }
        if (!ObjectUtils.isEmpty(deviceIdList)) {
            List<DeviceInfoDto> deviceInfoDtos = queryDeviceListByIds(deviceIdList);
            //删除主控
            controlDao.deleteControlByDeviceIds(deviceIdList);
            //记录日志
            logUtils.batchAddOperationLog(deviceInfoDtos, FunctionCode.DELETE_CONTROL_INFO, LogConstants.OPT_TYPE_WEB);
        }
        //清除缓存
        List<String> controlIdList = controlList.stream().map(ControlParam::getHostId).collect(Collectors.toList());
        ControlDel controlDel = new ControlDel();
        controlDel.setHostIdList(controlIdList);
        controlDel.setOperate(ConstantParam.DELETE_STATE);
        controlSender.send(JSON.toJSONString(controlDel));
    }

    /**
     * 根据主控id更新主控的部署状态
     *
     * @param controlParam 主控信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeployStatusById(ControlParam controlParam) {
        updateControlStatus(controlParam);
        addSensor(controlParam);
        //根据主控id获取该主控关联设施的最高级别的设施状态
        List<ControlParam> controlParamList = controlDao.getDeployStatusById(controlParam.getHostId());
        if (controlParamList != null && controlParamList.size() == 1) {
            ControlParam controlParam1 = controlParamList.get(0);
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            deviceInfoDto.setDeviceId(controlParam1.getDeviceId());
            deviceInfoDto.setDeployStatus(controlParam1.getDeployStatus());
            updateDeviceStatus(deviceInfoDto);
        }
    }


    /**
     * 根据主控id更新主控的设施状态及部署状态
     *
     * @param controlParam 主控信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateControlStatusById(ControlParam controlParam) {
        updateControlStatus(controlParam);
        addSensor(controlParam);
        //根据主控id获取该主控关联设施的最高级别的设施状态
        ControlParam newControlParam = controlDao.getDeviceStatusById(controlParam.getHostId());
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId(newControlParam.getDeviceId());
        deviceInfoDto.setDeviceStatus(newControlParam.getDeviceStatus());
        //根据主控id获取该主控关联设施的部署状态
        List<ControlParam> controlParamList = controlDao.getDeployStatusById(controlParam.getHostId());
        if (controlParamList != null && controlParamList.size() == 1) {
            ControlParam controlParam1 = controlParamList.get(0);
            deviceInfoDto.setDeployStatus(controlParam1.getDeployStatus());
        }
        updateDeviceStatus(deviceInfoDto);
    }

    /**
     * 根据主控id更新主控的设施状态
     *
     * @param controlParam 主控信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeviceStatusById(ControlParam controlParam) {
        updateControlStatus(controlParam);
        addSensor(controlParam);
        //根据主控id获取该主控关联设施的最高级别的设施状态
        controlParam = controlDao.getDeviceStatusById(controlParam.getHostId());
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId(controlParam.getDeviceId());
        deviceInfoDto.setDeviceStatus(controlParam.getDeviceStatus());
        updateDeviceStatus(deviceInfoDto);
    }

    /**
     * 配置设施策略
     *
     * @param setConfigBean 参数信息
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result setConfig(SetConfigBean setConfigBean) {
        //配置策略值
        Map<String, String> configParams = setConfigBean.getConfigParams();

        String configParamsJson = JSONObject.toJSONString(configParams);
        //配置策略值的检验正则表达式
        Map<String, String> configPatterns;
        try {
            configPatterns = deviceConfigFeign.getConfigPatterns();
        } catch (Exception e) {
            //微服务调用熔断
            throw new RuntimeException("deviceConfig feign call failed>>>>");
        }
        //校验配置策略参数值
        if (!CheckUtil.checkConfig(configParams, configPatterns)) {
            //校验失败
            throw new FiLinkControlException("config params error");
        }

        List<String> deviceIds = setConfigBean.getDeviceIds();
        //判断所选设施中是否包含没有主控信息的设施
        if (!checkDeviceHasControl(deviceIds)) {
            return ResultUtils.warn(ControlResultCode.DEVICE_HAS_NO_CONTROL_INFO,
                    I18nUtils.getSystemString(ControlI18n.DEVICE_HAS_NO_CONTROL_INFO));
        }
        //将同步状态设置为未同步,将配置参数保存到数据库中
        controlDao.batchSetConfig(deviceIds, configParamsJson, SyncStatusType.NOT_SYNC);
        //将配置转换成指令所需参数
        List<Map<String, Object>> configList = new ArrayList<>();
        for (Map.Entry<String, String> entry : configParams.entrySet()) {
            String value = entry.getValue();
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            Map<String, Object> param = new HashMap<>(64);
            param.put(DATA_CLASS, entry.getKey());
            param.put(DATA, value);
            configList.add(param);
        }
        Map<String, Object> params = new HashMap<>(64);
        params.put(PARAMS, configList);
        //循环构造指令所需参数
        List<DeviceInfoDto> deviceInfoList = new ArrayList<>();
        List<FiLinkReqParamsDto> reqParamsDtoList = new ArrayList<>();
        List<String> hostIds = new ArrayList<>();
        for (String deviceId : deviceIds) {
            //根据序列id查询设施id
            DeviceInfoDto deviceInfoDto = findDeviceByDeviceId(deviceId);
            deviceInfoList.add(deviceInfoDto);
            List<ControlParam> controlParamList = getControlInfoByDeviceIdToCall(deviceId);
            for (ControlParam controlParam : controlParamList) {
                FiLinkReqParamsDto reqParamsDto = new FiLinkReqParamsDto();
                reqParamsDto.setCmdId(CmdId.SET_CONFIG);
                reqParamsDto.setEquipmentId(controlParam.getHostId());
                reqParamsDto.setHardwareVersion(controlParam.getHardwareVersion());
                reqParamsDto.setSoftwareVersion(controlParam.getSoftwareVersion());
                reqParamsDto.setImei(controlParam.getImei());
                reqParamsDto.setPlateFormId(controlParam.getPlatformId());
                reqParamsDto.setPlateForm(controlParam.getCloudPlatform());
                reqParamsDto.setAppId(controlParam.getProductId());
                reqParamsDto.setParams(params);
                reqParamsDtoList.add(reqParamsDto);
                hostIds.add(controlParam.getHostId());
            }
        }
        //发送指令
        codeSender.send(reqParamsDtoList);
        //记录操作日志
        logUtils.batchAddOperationLog(deviceInfoList, FunctionCode.SET_CONFIG_CODE, LogConstants.OPT_TYPE_WEB);
        return ResultUtils.success(ControlResultCode.SUCCESS,
                I18nUtils.getSystemString(ControlI18n.SET_CONFIG_SUCCESS));
    }

    /**
     * 根据主控id删除电子锁主控
     *
     * @param controlReq
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLockAndControlByControlId(ControlReq controlReq) {
        String controlId = controlReq.getControlId();
        //检验主控是否存在
        ControlParam oldControlParam = getControlParamById(controlId);
        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(oldControlParam.getDeviceId());
        controlDao.deleteLockAndContorlByControlId(controlId);
        lockService.deleteFromPlatForm(oldControlParam);
        //记日志
        logUtils.addSimpleOperateLog(deviceInfo, FunctionCode.DELETE_CONTROL_INFO, LogConstants.OPT_TYPE_PDA);
        //清除缓存
        List<String> controlIdList = new ArrayList<>(Arrays.asList(controlId));
        ControlDel controlDel = new ControlDel();
        controlDel.setHostIdList(controlIdList);
        controlDel.setOperate(ConstantParam.DELETE_STATE);
        controlSender.send(JSON.toJSONString(controlDel));

    }

    /**
     * 根据分页条件查询出有主控信息及门锁信息的设施的d集合
     *
     * @param pageCondition 分页条件
     * @param deviceIds     设施id集合
     * @return 查询结果
     */
    @Override
    public List<String> deviceIdListByPage(PageCondition pageCondition, List<String> deviceIds) {
        //开始页码
        Integer begin = (pageCondition.getPageNum() - 1) * pageCondition.getPageSize();
        pageCondition.setBeginNum(begin);
        return controlDao.deviceIdListByPage(pageCondition, deviceIds);
    }


    /**
     * 查询出有主控信息及门锁信息的设施的id集合
     *
     * @return 查询结果
     */
    @Override
    public List<String> deviceIdList() {
        return controlDao.deviceIdList();
    }

    /**
     * 校验设施是否都有主控信息
     *
     * @param deviceIds 设施id集合
     * @return boolean
     */
    private boolean checkDeviceHasControl(List<String> deviceIds) {
        //设施跟主控一对多关系,需要遍历设施id查询主控
        for (String deviceId : deviceIds) {
            List<ControlParam> controlInfoList = getControlInfoByDeviceId(deviceId);
            if (controlInfoList == null || controlInfoList.size() < 1) {
                return false;
            }
            //同一个设施下的主控类型一样,检验主控类型是否是无源锁
            ControlParam controlParam = controlInfoList.get(0);
            if (controlParam.getHostType().equals(PASSIVE_HOST)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 根据主控id获取设施id
     *
     * @param controlId 主控id
     * @return 设施id
     */
    @Override
    public String getDeviceIdByControlId(String controlId) {
        String deviceId = controlDao.getDeviceIdByControlId(controlId);
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkControlException("the master is not bound to a device");
        }
        return deviceId;
    }

    /**
     * 检查主控id是否重复
     *
     * @param controlId 主控id
     */
    @Override
    public void checkControlIdReused(String controlId) {
        ControlParam controlParam = controlDao.getControlParamById(controlId);
        if (controlParam != null) {
            throw new FiLinkControlIdReusedException("主控id已被使用》》》》");
        }
    }


    /**
     * 删除OneNet平台注册记录
     *
     * @param controlParamList 主控参数
     */
    @Override
    public void deleteFromOneNet(List<ControlParam> controlParamList) {

        //需要删除的设备
        List<DeleteDevice> list = new ArrayList<>();
        for (ControlParam controlParam : controlParamList) {
            DeleteDevice deleteDevice = new DeleteDevice(controlParam.getPlatformId(),
                    controlParam.getProductId());
            list.add(deleteDevice);
        }
        //todo 批量删除
        list.forEach(deleteDevice -> oneNetFeign.deleteDevice(deleteDevice));

    }

    /**
     * 删除OceanConnect平台注册记录
     *
     * @param controlParamList 主控参数
     */
    @Override
    public void deleteFromOceanConnect(List<ControlParam> controlParamList) {
        //需要删除的设备
        List<OceanDevice> list = new ArrayList<>();
        for (ControlParam controlParam : controlParamList) {
            OceanDevice oceanDevice = new OceanDevice();
            oceanDevice.setAppId(controlParam.getProductId());
            oceanDevice.setDeviceId(controlParam.getPlatformId());
            list.add(oceanDevice);
        }
        //todo 批量删除
        list.forEach(oceanDevice -> oceanConnectFeign.deleteDevice(oceanDevice));
    }

    /**
     * 新增传感值
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    @Override
    public void addSensor(ControlParam controlParam) {
        String actualValue = controlParam.getActualValue();
        if (ObjectUtils.isEmpty(actualValue)) {
            log.info("主控没有传感值上来，不记录");
            return;
        }
        Map<String, Object> actualMap = JSONObject.parseObject(actualValue, Map.class);
        Map<String, Integer> sensorMap = new HashMap<>(64);
        for (Map.Entry<String, Object> item : actualMap.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            putSensorValue(sensorMap, key, value);
        }
        //构造传感值实体
        Sensor sensor = new Sensor();
        sensor.setDeviceId(controlParam.getDeviceId());
        sensor.setControlId(controlParam.getHostId());
        sensor.setCurrentTime(controlParam.getCurrentTime());
        try {
            //提高性能修改
            sensor.setTemperature(sensorMap.get("temperature"));
            sensor.setHumidity(sensorMap.get("humidity"));
            sensor.setElectricity(sensorMap.get("electricity"));
            sensor.setLean(sensorMap.get("lean"));
            sensor.setLeach(sensorMap.get("leach"));
            //org.apache.commons.beanutils.BeanUtils.populate(sensor, sensorMap);
            Integer humidity = sensor.getHumidity();
            Integer temperature = sensor.getTemperature();
            mongoTemplate.save(sensor);
            SensorInfo sensorInfo = new SensorInfo();
            sensorInfo.setCurrentTime(sensor.getCurrentTime());
            sensorInfo.setDeviceId(sensor.getDeviceId());
            sensorInfo.setHumidity(humidity);
            sensorInfo.setTemperature(temperature);
            //若湿度和温度为空则不更新max
            if (humidity != null || temperature != null) {
                //更新设施传感器极限值
                statisticsFeign.updateSensorLimit(sensorInfo);
            }
        } catch (Exception e) {
            log.error("sensorMap转成sensorBean过程出错:", e);
        }
    }

    /**
     * set传感器值
     *
     * @param sensorMap 传感器参数
     * @param key       key值
     * @param value     数值
     */
    private void putSensorValue(Map<String, Integer> sensorMap, String key, Object value) {
        try {
            if (value != null && value instanceof Map) {
                Map<String, String> valueMap = (Map<String, String>) value;
                String dataString = valueMap.get("data");
                if (!StringUtils.isEmpty(dataString)) {
                    Integer data = Integer.valueOf(dataString);
                    sensorMap.put(key, data);
                }
            }
        } catch (Exception e) {
            log.info("the data of key : {} is error", key);
        }
    }


    /**
     * 根据主控id及告警类型清除告警显示状态
     *
     * @param removeAlarmList 告警清除集合
     */
    @Override
    public void removeAlarm(List<RemoveAlarm> removeAlarmList) {
        for (RemoveAlarm removeAlarm : removeAlarmList) {
            //新建一个主控，用于存放属性值
            ControlParam controlParam = new ControlParam();
            String controlId = removeAlarm.getControlId();
            controlParam.setHostId(controlId);
            List<String> alarmTypeList = removeAlarm.getAlarmType();
            //得到之前的主控参数
            ControlParam oldControlParam = controlDao.getControlParamById(controlId);
            //获得告警清除之前的传感器值，并清除告警状态
            String actualValue = oldControlParam.getActualValue();
            Map<String, Object> actualMap = JSONObject.parseObject(actualValue, Map.class);
            for (Map.Entry<String, Object> item : actualMap.entrySet()) {
                String key = item.getKey();
                Object value = item.getValue();
                if (alarmTypeList.contains(key)) {
                    if (value != null && value instanceof Map) {
                        Map<String, String> valueMap = (Map<String, String>) value;
                        valueMap.put("alarmFlag", "1");
                    }
                }
            }
            //转为字符串，赋值给主控,并更新
            String defineActualValue = JSONObject.toJSONString(actualMap);
            controlParam.setActualValue(defineActualValue);
            controlDao.updateControlParams(controlParam);
        }

    }

    /**
     * 根据设施id查询锁具所需的设施信息
     *
     * @param deviceId 设施id
     * @return deviceInfoForLock
     */
    private DeviceInfoForLock findDeviceInfoForLockByDeviceId(String deviceId) {
        DeviceInfoForLock deviceInfoForLock = new DeviceInfoForLock();
        BeanUtils.copyProperties(findDeviceByDeviceId(deviceId), deviceInfoForLock);
        return deviceInfoForLock;
    }

    /**
     * 根据设施id查询设施信息
     *
     * @param deviceId 设施id
     */
    private DeviceInfoDto findDeviceByDeviceId(String deviceId) {
        DeviceInfoDto deviceInfoDto = deviceFeign.getDeviceById(deviceId);
        if (deviceInfoDto == null || deviceInfoDto.getDeviceId() == null) {
            throw new FiLinkDeviceIsNullException("设施为空");
        }
        return deviceInfoDto;
    }

    /**
     * 更新主控的设施状态或部署状态
     *
     * @param controlParam 主控信息
     */
    private void updateControlStatus(ControlParam controlParam) {
        //更新主控状态
        controlDao.updateControlStatus(controlParam);
    }

    /**
     * 更新设施的状态
     *
     * @param deviceInfoDto 设施实体
     */
    private void updateDeviceStatus(DeviceInfoDto deviceInfoDto) {
        try {
            Result result = deviceFeign.updateDeviceStatus(deviceInfoDto);
            if (result == null) {
                throw new FiLinkSystemException("device feign call failed>>>");
            }
        } catch (Exception e) {
            throw new FiLinkSystemException("device feign call failed>>>");
        }
    }

    /**
     * 根据设施id集合查询设施集合
     *
     * @param deviceIds
     * @return 设施集合
     */
    private List<DeviceInfoDto> queryDeviceListByIds(List<String> deviceIds) {
        String[] deviceIdArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceIdArray);
        List<DeviceInfoDto> deviceInfoDtos = deviceFeign.getDeviceByIds(deviceIdArray);
        if (deviceInfoDtos.size() != deviceIds.size()) {
            throw new FiLinkDeviceIsNullException("批量操作时缺少设施id");
        }
        return deviceInfoDtos;
    }


}
