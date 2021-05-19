package com.fiberhome.filink.lockserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;
import com.fiberhome.filink.lockserver.dao.MasterControlDao;
import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import com.fiberhome.filink.lockserver.service.MasterControlService;
import com.fiberhome.filink.lockserver.util.ControlI18n;
import com.fiberhome.filink.lockserver.util.ControlResultCode;
import com.fiberhome.filink.lockserver.util.constant.CmdId;
import com.fiberhome.filink.lockserver.util.constant.FunctionCode;
import com.fiberhome.filink.lockserver.util.constant.SyncStatusType;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.stationapi.bean.FiLinkReqParamsDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主控信息实现类
 *
 * @author CongcaiYu
 */
@Service
@Log4j
public class MasterControlServiceImpl implements MasterControlService {

    @Autowired
    private MasterControlDao controlDao;

    @Autowired
    private FiLinkStationFeign stationFeign;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private LogCastProcess logCastProcess;

    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @Override
    public Control getControlInfoByDeviceId(String deviceId) {
        Control controlByDeviceId = controlDao.getControlByDeviceId(deviceId);
        log.info("control info : " + JSONObject.toJSONString(controlByDeviceId));
        return controlByDeviceId;
    }


    /**
     * 保存主控参数信息
     *
     * @param control 主控信息
     * @return 操作结果
     */
    @Override
    public Result saveControlParams(Control control) {
        controlDao.saveControlParams(control);
        return ResultUtils.success();
    }

    /**
     * 根据设施id修改主控信息
     *
     * @param control 主控信息
     * @return 操作结果
     */
    @Override
    public Result updateControlParamsByDeviceId(Control control) {
        controlDao.updateControlParamsByDeviceId(control);
        //记录日志
        addUpdateControlLog(control);
        return ResultUtils.success();
    }

    /**
     * 记录更新主控操作日志
     *
     * @param control 主控信息
     */
    private void addUpdateControlLog(Control control) {
        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(control.getDeviceId());
        AddOperateLogReq controlLogBean = getControlLogBean(deviceInfo, FunctionCode.UPDATE_CONTROL_INFO);
        //记录日志
        logProcess.addOperateLogInfoToAutoCall(controlLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
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
        //todo 校验配置策略参数值
        List<String> deviceIds = setConfigBean.getDeviceIds();
        //判断所选设施中是否包含没有主控信息的设施
        if (!checkDeviceHasControl(deviceIds)) {
            return ResultUtils.warn(ControlResultCode.DEVICE_HAS_NO_CONTROL_INFO,
                    I18nUtils.getString(ControlI18n.DEVICE_HAS_NO_CONTROL_INFO));
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
            param.put("dataClass", entry.getKey());
            param.put("data", value);
            configList.add(param);
        }
        Map<String, Object> params = new HashMap<>(64);
        params.put("params", configList);
        //循环构造指令所需参数
        List<DeviceInfoDto> deviceInfoList = new ArrayList<>();
        for (String deviceId : deviceIds) {
            //根据序列id查询设施id
            DeviceInfoDto deviceInfoDto = findDeviceByDeviceId(deviceId);
            deviceInfoList.add(deviceInfoDto);
            String serialNum = deviceInfoDto.getSerialNum();
            FiLinkReqParamsDto reqParamsDto = new FiLinkReqParamsDto();
            reqParamsDto.setSerialNum(serialNum);
            reqParamsDto.setCmdId(CmdId.SET_CONFIG);
            reqParamsDto.setParams(params);
            stationFeign.sendUdpReq(reqParamsDto);
        }

        //记录操作日志
        batchAddSetConfigOperationLog(deviceInfoList, FunctionCode.SET_CONFIG_CODE);
        return ResultUtils.success(ControlResultCode.SUCCESS,
                I18nUtils.getString(ControlI18n.SET_CONFIG_SUCCESS));
    }

    /**
     * 校验设施是否都有主控信息
     *
     * @param deviceIds 设施id集合
     * @return boolean
     */
    private boolean checkDeviceHasControl(List<String> deviceIds) {
        long controlCount = controlDao.getControlCountByDeviceIds(deviceIds);
        return controlCount == deviceIds.size();
    }

    /**
     * 记录配置策略操作日志
     *
     * @param functionCode   操作编码
     * @param deviceInfoList 设施信息集合
     */
    private void batchAddSetConfigOperationLog(List<DeviceInfoDto> deviceInfoList, String functionCode) {
        List<AddOperateLogReq> addOperateLogReqList = new ArrayList<>();
        for (DeviceInfoDto deviceInfo : deviceInfoList) {
            AddOperateLogReq controlLogBean = getControlLogBean(deviceInfo, functionCode);
            addOperateLogReqList.add(controlLogBean);
        }
        //批量新增操作日志信息
        logProcess.addOperateLogBatchInfoToAutoCall(addOperateLogReqList, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 获取主控日志信息
     *
     * @param deviceInfo   设施信息
     * @param functionCode 功能码
     * @return 日志对象
     */
    private AddOperateLogReq getControlLogBean(DeviceInfoDto deviceInfo, String functionCode) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        AddOperateLogReq addOperateLogReq = new AddOperateLogReq();
        BeanUtils.copyProperties(addLogBean, addOperateLogReq);
        //操作对象
        addOperateLogReq.setOptObj(deviceInfo.getDeviceName());
        //对象id
        addOperateLogReq.setOptObjId(deviceInfo.getDeviceId());
        addOperateLogReq.setFunctionCode(functionCode);
        //获取语言
        String environment = ((LanguageConfig) SpringUtils.getBean(LanguageConfig.class)).getEnvironment();
        //获取详细信息模板
        String detailTemplateInfo = "";
        String optName = "";
        try {
            XmlParseBean xmlParseBean = logCastProcess.dom4jParseXml(addOperateLogReq.getFunctionCode(), environment);
            //获得日志详细信息模板对象
            detailTemplateInfo = xmlParseBean.getDetailInfoTemplate();
            //替换日志详细信息,替换xml中的占位符
            if (null != detailTemplateInfo) {
                // 获取用户
                detailTemplateInfo = detailTemplateInfo.replace("${optUserName}", addOperateLogReq.getOptUserName());
                detailTemplateInfo = detailTemplateInfo.replace("${deviceName}", deviceInfo.getDeviceName());
            }
            optName = xmlParseBean.getOptName();
        } catch (Exception e) {
            throw new FiLinkLockException("write update control operation log failed>>>>>");
        }
        //获得日志详细信息
        addOperateLogReq.setDetailInfo(detailTemplateInfo);
        //获得日志操作名称
        addOperateLogReq.setOptName(optName);
        //需要调用失败后新增日志本地文件
        addOperateLogReq.setAddLocalFile(LogConstants.ADD_LOG_LOCAL_FILE);
        return addOperateLogReq;
    }


    /**
     * 根据设施id查询设施信息
     *
     * @param deviceId 设施id
     */
    private DeviceInfoDto findDeviceByDeviceId(String deviceId) {
        DeviceInfoDto deviceInfoDto;
        try {
            deviceInfoDto = deviceFeign.getDeviceById(deviceId);
        } catch (Exception e) {
            throw new FiLinkLockException("device info is null>>>>>");
        }
        if (deviceInfoDto == null) {
            throw new FiLinkLockException("device info is null>>>>>");
        }
        return deviceInfoDto;
    }
}
