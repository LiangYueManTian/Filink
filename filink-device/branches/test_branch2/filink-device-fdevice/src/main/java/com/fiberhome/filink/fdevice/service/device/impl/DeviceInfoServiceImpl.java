package com.fiberhome.filink.fdevice.service.device.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.bean.DevicePicReq;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.async.DeviceInfoAsync;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.bean.device.RetryDeleteInfo;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.fdevice.constant.device.*;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceCollectingDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dao.device.RetryDeleteInfoDao;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.exception.*;
import com.fiberhome.filink.fdevice.export.DeviceExport;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.fdevice.utils.AreaCreateTimeComparator;
import com.fiberhome.filink.fdevice.utils.DeviceRedisUtil;
import com.fiberhome.filink.fdevice.utils.RandomUtil;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.enums.OperationTarget;
import com.fiberhome.filink.license.enums.OperationWay;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.rfidapi.api.DeleteDeviceFeign;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zepenggao@wistronits.com
 * @since 2019-01-07
 */
@Slf4j
@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoDao, DeviceInfo>
        implements DeviceInfoService {

    @Autowired
    private DeviceInfoDao deviceInfoDao;

    @Autowired
    private AreaInfoService areaInfoService;

    @Autowired
    private DeviceConfigService deviceConfigService;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;

    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private LicenseFeign licenseFeign;

    @Autowired
    private LockFeign lockFeign;

    @Autowired
    private UserFeign userFeign;


    @Autowired
    private DeleteDeviceFeign deleteDeviceFeign;

    @Autowired
    private DeviceLogService deviceLogService;

    /**
     * 我的关注DAO
     */
    @Autowired
    private DeviceCollectingDao deviceCollectingDao;

    /**
     * 区域信息Dao
     */
    @Autowired
    private AreaInfoDao areaInfoDao;

    /**
     * 设施信息导出类
     */
    @Autowired
    private DeviceExport deviceExport;

    @Autowired
    private DeviceInfoAsync deviceInfoAsync;
    /**
     * 远程调用工单服务
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;

    /**
     * 系统语言工具
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 重试删除任务
     */
    @Autowired
    private RetryDeleteInfoDao retryDeleteInfoDao;
    /**
     * 系统参数
     */
    @Autowired
    private ParameterFeign parameterFeign;
    /**
     * 服务名
     */
    private static String SERVER_NAME = "filink-device-server";

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;


    @Value("${unitCode}")
    private String unitCode;

    /**
     * 新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     */
    @Transactional(rollbackFor = Exception.class)
    public Result addDeviceInfo(DeviceInfo deviceInfo) {
        //语言
        systemLanguageUtil.querySystemLanguage();

        // 校验参数
        validateDeviceParam(deviceInfo);


        // 校验设施名是否重复
        if (checkDeviceName(null, deviceInfo.getDeviceName())) {
            throw new FilinkDeviceNameSameException();
        }

        // 校验区域ID是否存在
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId());
        if (areaInfo == null) {
            throw new FilinkAreaDoesNotExistException();
        }
        // 校验权限
        checkAuthorization(deviceInfo.getAreaId(), deviceInfo.getDeviceType());
        // 获取编号
        String deviceCode;
        while (!checkDeviceCode(deviceCode = serialNumber(unitCode, deviceInfo.getDeviceType()))) {
            log.info("Repeated deviceCode: " + deviceCode);
        }
        log.info("The deviceCode is: " + deviceCode);
        deviceInfo.setDeviceCode(deviceCode);

        //默认赋值
        fillDeviceDefault(deviceInfo);

        //查询license设施最大值
        License currentLicense = licenseFeign.getCurrentLicense();
        //查询设施总数
        Integer deviceCount = queryCurrentDeviceCount();
        boolean permission = false;
        if (deviceCount != null && currentLicense != null && currentLicense.maxDeviceNum != null
                && Integer.parseInt(currentLicense.maxDeviceNum) > deviceCount) {
            permission = true;
        }

        if (permission) {
            //生成主键
            deviceInfo.setDeviceId(NineteenUUIDUtils.uuid());

            // 存入数据库
            if (deviceInfoDao.insertDevice(deviceInfo) != 1) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.ADD_DEVICE_FAIL));
            }
        } else {
            return ResultUtils.warn(DeviceResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.DEVICE_COUNT_EXCEEDS_LICENSE_LIMIT));
        }

        // 存入缓存
        saveDeviceRedis(deviceInfo);

        // 修改设施总数
        deviceCount++;
        LicenseThresholdFeignBean licenseThresholdFeignBean = new LicenseThresholdFeignBean();
        licenseThresholdFeignBean.setName("device");
        licenseThresholdFeignBean.setNum(1);
        licenseFeign.synchronousLicenseThreshold(licenseThresholdFeignBean);

        //返回设施ID
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", deviceInfo.getDeviceId());
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.ADD_DEVICE_SUCCESS), jsonObject);
    }

    /**
     * 存入缓存
     *
     * @param deviceInfo
     */
    private void saveDeviceRedis(DeviceInfo deviceInfo) {
        try {
            //查询信息
            HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
            DeviceRedisUtil.addDeviceRedis(homeDeviceInfoDto);

            //WebSocket消息异步推送
            deviceInfoAsync.sendDeviceMsg(DeviceWebSocketCode.ADD_DEVICE_CHANNEL_ID,
                    DeviceWebSocketCode.ADD_DEVICE_CHANNEL_KEY, homeDeviceInfoDto);
        } catch (Exception e) {
            log.error("新增时缓存错误");
        }
    }

    /**
     * 添加设施默认信息
     *
     * @param deviceInfo
     * @return
     */
    private DeviceInfo fillDeviceDefault(DeviceInfo deviceInfo) {
        //从缓存中获取当前用户信息
        String userId = RequestInfoUtils.getUserId();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        deviceInfo.setCreateUser(userId);
        deviceInfo.setCreateTime(timestamp);
        deviceInfo.setUpdateUser(userId);
        deviceInfo.setUpdateTime(timestamp);

        // 光交箱，人井，室外柜设为未配置，其他为正常
        if (DeviceType.Optical_Box.getCode().equals(deviceInfo.getDeviceType())
                || DeviceType.Well.getCode().equals(deviceInfo.getDeviceType())
                || DeviceType.Outdoor_Cabinet.getCode().equals(deviceInfo.getDeviceType())) {
            deviceInfo.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        } else {
            deviceInfo.setDeviceStatus(DeviceStatus.Normal.getCode());
        }

        // 默认部署状态：无
        deviceInfo.setDeployStatus(DeployStatus.NONE.getCode());
        return deviceInfo;
    }

    /**
     * 网管新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     * @throws Exception 异常
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE,
            functionCode = LogFunctionCodeConstant.INSERT_DEVICE_FUNCTION_CODE,
            dataGetColumnName = "deviceName", dataGetColumnId = "deviceId")
    @Override
    public Result addDevice(DeviceInfo deviceInfo) {
        return addDeviceInfo(deviceInfo);
    }

    /**
     * PDA新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     * @throws Exception 异常
     */
    @Override
    public Result addDeviceForPda(DeviceInfo deviceInfo) {
        Result result = addDeviceInfo(deviceInfo);
        //添加日志
        addDeviceOperateLog(deviceInfo, LogConstants.OPT_TYPE_PDA, LogConstants.DATA_OPT_TYPE_ADD,
                LogFunctionCodeConstant.INSERT_DEVICE_FUNCTION_CODE);
        return result;
    }

    /**
     * 校验设施参数
     *
     * @param deviceInfo 设施参数
     * @throws FiLinkDeviceException 参数验证不通过抛出异常
     */
    private void validateDeviceParam(DeviceInfo deviceInfo) throws FiLinkDeviceException {
        //校验必填参数
        if (checkDeviceParams(deviceInfo)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        //参数格式化
        deviceInfo.parameterFormat();

        //校验坐标信息是否合法
        if (!deviceInfo.checkDevicePositionBase() || !deviceInfo.checkDevicePositionGps()) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        // 校验设施名合法性
        if (!deviceInfo.checkDeviceName()) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_NAME_ERROR));
        }
        //检查字符超长，非法字符等问题
        if (!deviceInfo.checkParameterFormat()) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
    }

    /**
     * 校验设施名称是否重复
     *
     * @param deviceId   设施id
     * @param deviceName 设施名称
     * @return boolean
     */
    @Override
    public boolean checkDeviceName(String deviceId, String deviceName) {
        DeviceInfo deviceInfo = deviceInfoDao.selectByName(deviceName);
        // deviceId为空时，新增校验；不为空，修改校验
        if (StringUtils.isEmpty(deviceId)) {
            if (!ObjectUtils.isEmpty(deviceInfo)) {
                return true;
            }
        } else {
            if (!ObjectUtils.isEmpty(deviceInfo)) {
                if (!deviceInfo.getDeviceId().equals(deviceId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改设施信息
     *
     * @param deviceInfo 设施基本信息
     * @return Result
     */
    public Result updateDeviceInfo(DeviceInfo deviceInfo) {

        //语言
        systemLanguageUtil.querySystemLanguage();

        // 校验设施id
        if (deviceInfo == null || StringUtils.isEmpty(deviceInfo.getDeviceId())) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_ID_LOSE));
        }

        // 校验参数
        validateDeviceParam(deviceInfo);

        // TODO 目前不需要校验权限
        checkAuthorization(deviceInfo.getAreaId(), deviceInfo.getDeviceType());

        // 校验设施是否存在
        DeviceInfo oriDeviceInfo = checkDeviceIsExist(deviceInfo.getDeviceId());

        // 校验设施名是否重复
        if (checkDeviceName(deviceInfo.getDeviceId(), deviceInfo.getDeviceName())) {
            throw new FilinkDeviceNameSameException();
        }

        // 校验区域ID是否存在
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId());
        if (areaInfo == null) {
            throw new FilinkAreaDoesNotExistException();
        }

        // 设施不让修改类型
        deviceInfo.setDeviceType(oriDeviceInfo.getDeviceType());

        // 从缓存中获取用户信息
        deviceInfo.setUpdateUser(RequestInfoUtils.getUserId());
        deviceInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

        // 更新数据库
        int result = deviceInfoDao.updateById(deviceInfo);
        if (result != 1) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.UPDATE_DEVICE_FAIL));
        }

        // 修改缓存
        try {
            HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
            updateDeviceRedis(oriDeviceInfo, homeDeviceInfoDto);
            //WebSocket消息异步推送
            if (oriDeviceInfo.getAreaId().equals(deviceInfo.getAreaId())) {
                deviceInfoAsync.sendDeviceMsg(DeviceWebSocketCode.UPDATE_DEVICE_CHANNEL_ID,
                        DeviceWebSocketCode.UPDATE_DEVICE_CHANNEL_KEY, homeDeviceInfoDto);
            } else {
                deviceInfoAsync.sendUpdatingAreaMsg(oriDeviceInfo.getAreaId(), oriDeviceInfo.getDeviceType(), homeDeviceInfoDto);
            }
        } catch (Exception e) {
            log.error("修改时缓存错误");
        }

        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.UPDATE_DEVICE_SUCCESS));
    }

    /**
     * 修改设施信息
     *
     * @param deviceInfo 设施基本信息
     * @return Result
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_OPERATE,
            functionCode = LogFunctionCodeConstant.UPDATE_DEVICE_FUNCTION_CODE,
            dataGetColumnName = "deviceName", dataGetColumnId = "deviceId")
    @Override
    public Result updateDevice(DeviceInfo deviceInfo) {
        return updateDeviceInfo(deviceInfo);
    }

    /**
     * 修改PDA设施信息
     *
     * @param deviceInfo
     * @return
     */
    @Override
    public Result updateDeviceForPda(DeviceInfo deviceInfo) {
        Result result = updateDeviceInfo(deviceInfo);
        //添加日志
        addDeviceOperateLog(deviceInfo, LogConstants.OPT_TYPE_PDA, LogConstants.DATA_OPT_TYPE_UPDATE,
                LogFunctionCodeConstant.UPDATE_DEVICE_FUNCTION_CODE);
        return result;
    }

    /**
     * 添加设施日志
     *
     * @param optType
     * @param dataOptType
     * @param functionCode
     */
    private void addDeviceOperateLog(DeviceInfo deviceInfo, String optType, String dataOptType, String functionCode) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("deviceId");
        addLogBean.setDataName("deviceName");
        addLogBean.setFunctionCode(functionCode);
        addLogBean.setOptObjId(deviceInfo.getDeviceId());
        addLogBean.setOptObj(deviceInfo.getDeviceName());
        addLogBean.setOptType(optType);
        addLogBean.setDataOptType(dataOptType);
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     * @throws Exception 异常
     */
    @Override
    public Result listDevice(QueryCondition<DeviceInfo> queryCondition) {
        return listDevice(queryCondition, getCurrentUser(), true);
    }

    /**
     * 分页查询设施列表（用户权限）
     *
     * @param queryCondition 查询条件
     * @param user           操作用户
     * @param needsAuth      是否需要控制权限
     * @return
     */
    @Override
    public Result listDevice(QueryCondition<DeviceInfo> queryCondition, User user, boolean needsAuth) {
        //校验参数
        if (queryCondition == null || queryCondition.getPageCondition() == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAM_NULL));
        }

        //添加当前用户权限
        if (needsAuth) {
            addAuth(queryCondition.getFilterConditions(), user);
        }

        //若排序条件为空,默认时间排序
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (sortCondition == null || StringUtils.isEmpty(sortCondition.getSortField())
                || StringUtils.isEmpty(sortCondition.getSortRule())) {
            sortCondition = new SortCondition();
            sortCondition.setSortField("createTime");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }

        Integer begin = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(begin);

        //修改like过滤条件值
        alterLikeFilterCondition(queryCondition.getFilterConditions());

        // 构造分页条件
        Page page = MpQueryHelper.myBatiesBuildPage(queryCondition);

        //查询范围为空时直接返回
        for (FilterCondition fc : queryCondition.getFilterConditions()) {
            if ("in".equalsIgnoreCase(fc.getOperator()) && ObjectUtils.isEmpty(fc.getFilterValue())) {
                PageBean pageBean = MpQueryHelper.myBatiesBuildPageBean(page, 0, new ArrayList<DeviceInfoDto>());
                return ResultUtils.pageSuccess(pageBean);
            }
        }

        //根据查询条件查询设施数据
        List<DeviceInfoDto> deviceInfoDtoList = deviceInfoDao.selectDevicePage(queryCondition.getPageCondition(),
                queryCondition.getFilterConditions(), queryCondition.getSortCondition());

        // 添加Long类型时间戳
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
            fillCTimeUTime(deviceInfoDto, deviceInfoDto.getCreateTime(), deviceInfoDto.getUpdateTime());
        }

        //查询当前用户是否关注该设施
        updateDeviceIsCollecting(deviceInfoDtoList, user.getId());

        //查询所有门锁
        updateDeviceLock(deviceInfoDtoList, user);

        Integer count = deviceInfoDao.selectDeviceCount(queryCondition.getFilterConditions());

        PageBean pageBean = MpQueryHelper.myBatiesBuildPageBean(page, count, deviceInfoDtoList);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 条件查询设施数量
     *
     * @param queryCondition
     * @param user
     * @return
     */
    @Override
    public Integer queryDeviceCount(QueryCondition<DeviceInfo> queryCondition, User user) {
        //校验参数
        if (queryCondition == null || queryCondition.getPageCondition() == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAM_NULL));
        }

        if (ObjectUtils.isEmpty(user)) {
            user = getCurrentUser();
        }

        //添加当前用户权限
        addAuth(queryCondition.getFilterConditions(), user);

        //查询范围为空时直接返回
        for (FilterCondition fc : queryCondition.getFilterConditions()) {
            if ("in".equalsIgnoreCase(fc.getOperator()) && ObjectUtils.isEmpty(fc.getFilterValue())) {
                return 0;
            }
        }

        //修改like过滤条件值
        alterLikeFilterCondition(queryCondition.getFilterConditions());

        return deviceInfoDao.selectDeviceCount(queryCondition.getFilterConditions());
    }

    /**
     * 查询当前设施数
     *
     * @return
     */
    @Override
    public Integer queryCurrentDeviceCount() {
        QueryCondition queryCondition = new QueryCondition();
        List<FilterCondition> filterConditionList = new ArrayList<>();
        filterConditionList.add(DeviceInfoService.generateDeleteFilterCondition());
        queryCondition.setFilterConditions(filterConditionList);
        Wrapper<DeviceInfo> wrapper = MpQueryHelper.myBatiesBuildQuery(queryCondition);
        return deviceInfoDao.selectCount(wrapper);
    }

    /**
     * 转换sql中的特殊字符
     *
     * @param filterConditionList
     */
    private void alterLikeFilterCondition(List<FilterCondition> filterConditionList) {
        for (FilterCondition filterCondition : filterConditionList) {
            if (StringUtils.equalsIgnoreCase("like", filterCondition.getOperator())) {
                String value = (String) filterCondition.getFilterValue();
                value = value.replace("\\", "\\\\");
                value = value.replace("%", "\\%");
                value = value.replace("_", "\\_");
                value = value.replace("'", "\\'");
                filterCondition.setFilterValue(value);
            }
        }
    }

    /**
     * 查询用户的区域和设施类型权限
     *
     * @param user
     * @return
     */
    @Override
    public DeviceParam getUserAuth(User user) {
        if (ObjectUtils.isEmpty(user)) {
            user = getCurrentUser();
        }
        //用户管理区域
        List<String> areaIds = getUserAreaIds(user);
        //用户管理设施类型
        List<String> deviceTypes = getDeviceTypes(user);
        DeviceParam deviceParam = new DeviceParam();
        deviceParam.setAreaIds(areaIds);
        deviceParam.setDeviceTypes(deviceTypes);
        return deviceParam;
    }

    /**
     * 根据区域ID集合查询设施类型集合
     *
     * @param areaIds 区域ID集合
     * @return 设施类型集合
     */
    @Override
    public List<String> queryDeviceTypesByAreaIds(List<String> areaIds) {
        //查询当前用户
        User user = getCurrentUser();
        //用户管理设施类型
        List<String> deviceTypes = getDeviceTypes(user);

        if (ObjectUtils.isEmpty(areaIds)) {
            return new ArrayList<>();
        }
        List<String> deviceTypeList = deviceInfoDao.queryDeviceTypesByAreaIds(areaIds);

        if (!DeviceConstant.ADMIN.equals(user.getId())) {
            deviceTypeList.retainAll(deviceTypes);
        }
        return deviceTypeList;
    }

    /**
     * 根据区域id和设施类型查询设施信息
     *
     * @param deviceParam
     * @return
     */
    @Override
    public List<DeviceInfoBase> queryDeviceInfoBaseByParam(DeviceParam deviceParam) {
        //校验区域ID不能为空
        if (deviceParam == null || ObjectUtils.isEmpty(deviceParam.getAreaIds())) {
            return null;
        }
        return deviceInfoDao.queryDeviceInfoBaseByParam(deviceParam);
    }

    /**
     * 重试删除
     */
    @Override
    public void retryDelete() {
        List<RetryDeleteInfo> retryDeleteInfos = retryDeleteInfoDao.selectAllRetryDeleteInfo();
        if (retryDeleteInfos != null && retryDeleteInfos.size() > 0) {
            for (RetryDeleteInfo retryDeleteInfo : retryDeleteInfos) {
                List<String> data = JSONObject.parseArray(retryDeleteInfo.getData(), String.class);
                switch (retryDeleteInfo.getFunctionCode()) {
                    case DeviceRetryDeleteConstant.PROC_BASE:
                        deviceInfoAsync.deleteProcBaseForDevice(data, retryDeleteInfo);
                        log.info("正在重新删除设施关联工单");
                        break;
                    case DeviceRetryDeleteConstant.ALARM_CURRENT:
                        deviceInfoAsync.deleteAlarmCurrent(data, retryDeleteInfo);
                        log.info("正在重新删除设施关联告警");
                        break;
                    case DeviceRetryDeleteConstant.DEVICE_COLLECTING:
                        deviceInfoAsync.deleteCollecting(data, retryDeleteInfo);
                        log.info("正在重新删除设施我的关注");
                        break;
                    case DeviceRetryDeleteConstant.DEVICE_PIC:
                        deviceInfoAsync.deleteDevicePic(data, retryDeleteInfo);
                        log.info("正在重新删除设施图片");
                        break;
                    case DeviceRetryDeleteConstant.INSPECTION_TASK:
                        deviceInfoAsync.deleteInspectionTask(data, retryDeleteInfo);
                        log.info("正在重新删除设施巡检任务");
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public String queryDeviceNameById(String deviceId) {
        return deviceInfoDao.queryDeviceNameById(deviceId);
    }

    /**
     * 根据设施id  查看设施类型
     *
     * @param deviceId deviceId
     * @return
     */
    @Override
    public String queryDeviceTypeById(String deviceId) {
        return deviceInfoDao.queryDeviceTypeById(deviceId);
    }


    /**
     * 添加当前用户权限过滤
     *
     * @param filterConditions
     */
    private static void addAuth(List<FilterCondition> filterConditions, User user) {
        if (user == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.USER_AUTH_INFO_ERROR));
        }
        if (DeviceConstant.ADMIN.equals(user.getId())) {
            return;
        }
        //用户管理区域
        List<String> areaIds = getUserAreaIds(user);
        //用户管理设施类型
        List<String> deviceTypes = getDeviceTypes(user);
        filterConditions.add(DeviceInfoService.generateFilterCondition("areaId", "in", areaIds));
        filterConditions.add(DeviceInfoService.generateFilterCondition("deviceType", "in", deviceTypes));
    }


    /**
     * 获取用户区域信息
     *
     * @param user
     * @return
     */
    private static List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * 获取用户设施类型信息
     *
     * @param user
     * @return
     */
    private static List<String> getDeviceTypes(User user) {
        List<String> deviceTypes = new ArrayList<>();
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }
        return deviceTypes;
    }

    /**
     * 校验当前用户是否有区域和设施类型的权限
     *
     * @param areaId
     * @param deviceType
     * @return
     */
    private boolean isAuthorized(String areaId, String deviceType) {
        User user = getCurrentUser();
        if (DeviceConstant.ADMIN.equals(user.getId())) {
            return true;
        }
        //用户管理区域
        List<String> areaIds = getUserAreaIds(user);
        //用户管理设施类型
        List<String> deviceTypes = getDeviceTypes(user);
        if (areaIds.contains(areaId) && deviceTypes.contains(deviceType)) {
            return true;
        }
        return false;
    }

    private void checkAuthorization(String areaId, String deviceType) {
        if (!isAuthorized(areaId, deviceType)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.USER_DEVICE_NOT_AUTHORIZED));
        }
    }

    /**
     * 返回时间戳
     *
     * @param deviceInfoDto
     */
    private void fillCTimeUTime(DeviceInfoDto deviceInfoDto, Timestamp createTime, Timestamp updateTime) {
        if (!ObjectUtils.isEmpty(createTime)) {
            deviceInfoDto.setCTime(createTime.getTime());
        }
        if (!ObjectUtils.isEmpty(updateTime)) {
            deviceInfoDto.setUTime(updateTime.getTime());
        }
    }

    /**
     * 类型转换
     *
     * @param filterConditions
     * @return
     */
    @Override
    public Map convertFilterConditionsToMap(List<FilterCondition> filterConditions) {
        Map map = new HashMap(64);
        for (FilterCondition filterCondition : filterConditions) {
            String key = filterCondition.getFilterField();
            String value = String.valueOf(filterCondition.getFilterValue());
            map.put(key, value);
        }
        return map;
    }


    /**
     * 根据区域id查询设施
     *
     * @param areaId 查询id
     * @return 查询结果
     */
    @Override
    public List<DeviceInfo> queryDeviceByAreaId(String areaId) {
        List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
        return deviceInfoList;
    }

    /**
     * 根据区域id查询设施Dto
     *
     * @param areaId 查询id
     * @return 查询结果
     */
    @Override
    public List<DeviceInfoDto> queryDeviceDtoByAreaId(String areaId) {
        List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
        return convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());
    }

    /**
     * 根据区域ids查询绑定的设施Dto
     *
     * @param areaIds
     * @return
     */
    @Override
    public List<DeviceInfoDto> queryDeviceDtoByAreaIds(List<String> areaIds) {
        //添加权限
        List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaIds(areaIds);
        return convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());
    }

    /**
     * 关联设施
     *
     * @param map 关联设施信息
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean setAreaDevice(Map<String, List<String>> map, AreaInfo areaInfo) {
        //每次成功后数据库返回的条数，成功一次加一
        int a = 0;
        //要关联的设施数量
        int b = 0;
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> deviceIds = entry.getValue();
            if (deviceIds.size() == 0) {
                return true;
            }
            List<String> deviceIdList = queryAlarmAndWorkOrderSource(deviceIds);
            if (deviceIdList.size() > 0) {
                String deviceName = deviceInfoDao.selectDeviceById(deviceIdList.get(0)).getDeviceName();
                if (StringUtils.isEmpty(deviceName)) {
                    throw new FiLinkAreaDirtyDataException(I18nUtils.getSystemString(DeviceI18n.DEVICE_IS_NOT_EXIST));
                }
                throw new FiLinkAreaDirtyDataException(
                        deviceName + I18nUtils.getSystemString(AreaI18nConstant.HAVE_A_WORK_ORDER_OR_AN_ALARM));
            }
            List<HomeDeviceInfoDto> homeDeviceInfoDtoList = deviceInfoDao.queryDeviceAreaByIds(deviceIds);

            // TODO 目前不需要校验权限
//            User user = getCurrentUser();
//            List<String> userAreaIds = getUserAreaIds(user);
//            if(!userAreaIds.contains(areaInfo.getAreaId())) {
//                throw new FilinkDeviceException(I18nUtils.getSystemString(DeviceI18n.USER_DEVICE_NOT_AUTHORIZED));
//            }


            if (homeDeviceInfoDtoList.size() != deviceIds.size()) {
                return false;
            }
            b += entry.getValue().size();
            Integer i = deviceInfoDao.setAreaDevice(entry.getKey(), deviceIds);
            if (deviceIds.size() == i) {
                // 修改缓存
                updateRedisBatch(homeDeviceInfoDtoList, entry.getKey(), areaInfo);
                //webSocket推送
                deviceInfoAsync.sendAreaDeviceMsg(entry.getKey(), areaInfo.getAreaName(), homeDeviceInfoDtoList);
            }
            a += i;
        }
        if (a == b) {
            return true;
        }
        throw new FiLinkAreaDateBaseException();
    }


    /**
     * 获取设施详情
     *
     * @param id 设施id
     * @return Result
     * @throws Exception 异常
     */
    @Override
    public Result getDeviceById(String id, String userId) {
        // 校验设施id
        if (StringUtils.isEmpty(id)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_ID_LOSE));
        }
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
        if (ObjectUtils.isEmpty(deviceInfo)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_IS_NOT_EXIST));
        }
        // TODO 目前不需要校验权限
//        checkAuthorization(deviceInfo.getAreaId(),deviceInfo.getDeviceType());

        // 转换为界面显示字段信息
        try {
            DeviceInfoDto dto = convertDeviceInfoToDeviceInfoDto(deviceInfo, userId);
            return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.QUERY_DEVICE_SUCCESS), dto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.COPYING_PROPERTIES_FAILED));
        }
    }


    /**
     * 获取设施详情
     *
     * @param deviceReq 参数请求对象
     * @return Result 查询结果
     * @throws Exception 异常
     */
    @Override
    public Result queryDeviceByBean(DeviceReq deviceReq) throws Exception {
        //查询设施基础信息
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByBean(deviceReq);
        List<DeviceInfoForPda> dtoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            //构造pda需要的设施数据结构
            DeviceInfoForPda deviceInfoForPda = new DeviceInfoForPda();
            copyProperties(deviceInfoForPda, deviceInfo);
            //设置设施的区域信息
            AreaInfo areaInfo = areaInfoService.queryAreaByIdForPda(deviceInfo.getAreaId());
            deviceInfoForPda.setAreaName(areaInfo.getAreaName());
            //设置设施的主控总数
            List<ControlParam> controlParamList = controlFeign.getControlParams(deviceInfo.getDeviceId());
            deviceInfoForPda.setControlParamList(controlParamList);
            dtoList.add(deviceInfoForPda);
        }
        return ResultUtils.success(dtoList);
    }

    /**
     * 查询设施详情
     *
     * @param ids 设施ID数组
     * @return
     * @throws Exception
     */
    @Override
    public List<DeviceInfoDto> getDeviceByIds(String[] ids) throws Exception {
        if (ids == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_ID_LOSE));
        }
        if (ids.length == 0) {
            return new ArrayList<>();
        }
        //校验设施ID不能为空
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_ID_LOSE));
            }
        }
        //查询ID数组的基本信息列表
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByIds(ids);
        //返回dtoList
        return convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());
    }

    /**
     * 查询设施详情（前端）
     *
     * @param ids
     * @return
     */
    @Override
    public Result getDeviceResultByIds(String[] ids) {
        //校验参数
        if (ids == null) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR, I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        if (ids.length == 0) {
            return ResultUtils.success(new ArrayList<>());
        }
        List<DeviceInfoDto> dtoList;
        try {
            dtoList = getDeviceByIds(ids);
            return ResultUtils.success(dtoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtils.warn(DeviceResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.DEVICE_ID_LOSE));
    }

    /**
     * 转换界面所需要的设施信息
     *
     * @param deviceInfo 数据库基本信息
     * @return DeviceInfoDto 界面展示信息
     * @throws Exception 异常
     */
    private DeviceInfoDto convertDeviceInfoToDeviceInfoDto(DeviceInfo deviceInfo, String userId) throws Exception {
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        copyProperties(deviceInfoDto, deviceInfo);
        // 返回区域
        Result result = areaInfoService.queryAreaById(deviceInfo.getAreaId());
//        AreaInfo areaInfo = new AreaInfo();
        if (result.getData() instanceof AreaInfo) {
            AreaInfo areaInfo = (AreaInfo) result.getData();
            deviceInfoDto.setAreaInfo(areaInfo);
        } else {
            deviceInfoDto.setAreaInfo(new AreaInfo());
        }

        //添加Long类型时间戳
        fillCTimeUTime(deviceInfoDto, deviceInfo.getCreateTime(), deviceInfo.getUpdateTime());

        //查询当前用户是否关注该设施
        if (!ObjectUtils.isEmpty(userId)) {
            deviceInfoDto.setIsCollecting(isCollectingDevice(deviceInfo.getDeviceId(), userId));
        }
        return deviceInfoDto;
    }

    /**
     * 当前用户是否关注该设施
     *
     * @param deviceId 设施Id
     * @return 0-未关注 1-已关注
     */
    private String isCollectingDevice(String deviceId, String userId) {
        String isCollecting = DeviceConstant.DEVICE_IS_NOT_COLLECTED;
        if (!StringUtils.isEmpty(userId)) {
            int count = deviceCollectingDao.selectAttentionDeviceCount(deviceId, userId);
            if (count > 0) {
                isCollecting = DeviceConstant.DEVICE_IS_COLLECTED;
            }
        }
        return isCollecting;
    }

    /**
     * 根据Device基本信息列表，转换为包含区域等信息的Dto列表
     *
     * @param deviceInfoList Device基本信息列表
     * @return 转换后的Dto列表
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private List<DeviceInfoDto> convertDeviceInfoListToDtoList(List<DeviceInfo> deviceInfoList, String userId) {

        if (ObjectUtils.isEmpty(deviceInfoList)) {
            return new ArrayList<>();
        }

        //保存所有设施的areaId并去重
        Set<String> areaIdSet = new HashSet<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            areaIdSet.add(deviceInfo.getAreaId());
        }

        //查询区域集合
        List<String> areaIdList = new ArrayList<>(areaIdSet);
        List<AreaInfo> areaInfoList = areaInfoService.queryAreaInfoByIds(areaIdList);
        Map<String, AreaInfo> areaMap = new HashMap<>(64);
        for (AreaInfo areaInfo : areaInfoList) {
            areaMap.put(areaInfo.getAreaId(), areaInfo);
        }

        //返回DeviceInfoDtoList
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            //转换基本字段
            try {
                copyProperties(deviceInfoDto, deviceInfo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FiLinkDeviceException(e.getMessage());
            }

            //填入区域信息
            deviceInfoDto.setAreaInfo(areaMap.get(deviceInfo.getAreaId()));
            if (deviceInfoDto.getAreaInfo() != null) {
                deviceInfoDto.setAreaName(deviceInfoDto.getAreaInfo().getAreaName());
            }

            // 添加Long类型时间戳
            fillCTimeUTime(deviceInfoDto, deviceInfo.getCreateTime(), deviceInfo.getUpdateTime());

            //加入到返回值
            deviceInfoDtoList.add(deviceInfoDto);
        }

        //查询当前用户是否关注该设施
        updateDeviceIsCollecting(deviceInfoDtoList, userId);

        //查询所有门锁
        updateDeviceLock(deviceInfoDtoList, null);

        return deviceInfoDtoList;
    }

    /**
     * 设施添加是否关注
     *
     * @param deviceInfoDtoList
     */
    private void updateDeviceIsCollecting(List<DeviceInfoDto> deviceInfoDtoList, String userId) {
        //查询用户关注的设施
        Set<String> deviceIdSet = queryUserAttention(userId);
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
            //当前用户是否关注该设施
            if (deviceIdSet.contains(deviceInfoDto.getDeviceId())) {
                deviceInfoDto.setIsCollecting(DeviceConstant.DEVICE_IS_COLLECTED);
            } else {
                deviceInfoDto.setIsCollecting(DeviceConstant.DEVICE_IS_NOT_COLLECTED);
            }
        }
    }

    /**
     * 查询用户关注的设施
     *
     * @param userId
     * @return
     */
    private Set<String> queryUserAttention(String userId) {
        //看用户是否关注设施，不需要权限，因为查询设施已控制权限
        List<DeviceAttentionDto> deviceAttentionDtoList = deviceCollectingDao.selectAttentionList(userId, new DeviceParam());
        Set<String> deviceIdSet = new HashSet<>();
        for (DeviceAttentionDto dto : deviceAttentionDtoList) {
            deviceIdSet.add(dto.getDeviceId());
        }
        return deviceIdSet;
    }

    /**
     * 设施添加门锁
     *
     * @param deviceInfoDtoList
     */
    private void updateDeviceLock(List<DeviceInfoDto> deviceInfoDtoList, User user) {
        if (ObjectUtils.isEmpty(user)) {
            return;
        }
        //用户管理设施类型
        Set<String> deviceTypes = new HashSet<>(getDeviceTypes(user));


        List<String> deviceIdList = new ArrayList<>();
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
            //电子锁过滤权限（“设施类型-1”为电子锁）
            if (deviceTypes.contains(deviceInfoDto.getDeviceType() + "-1")) {
                deviceIdList.add(deviceInfoDto.getDeviceId());
            }
            //每个deviceInfoDto添加lockLIst对象
            if (deviceInfoDto.getLockList() == null) {
                deviceInfoDto.setLockList(new ArrayList<>());
            }
        }
        List<Lock> lockList = lockFeign.lockListByDeviceIds(deviceIdList);
        if (lockList == null) {
            return;
        }
        for (Lock lock : lockList) {
            for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
                if (deviceInfoDto.getDeviceId().equals(lock.getDeviceId())) {
                    deviceInfoDto.getLockList().add(lock);
                    break;
                }
            }
        }
    }

    /**
     * 查询设施是否存在
     *
     * @param id 设施id
     * @return boolean
     */
    public DeviceInfo checkDeviceIsExist(String id) {
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
        if (ObjectUtils.isEmpty(deviceInfo)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_IS_NOT_EXIST));
        }
        return deviceInfo;
    }

    /**
     * 生成设施编号(单位编号+设施类型编号+7位流水号)
     *
     * @param unitCode   单位
     * @param deviceType 设施类型
     * @return 设施编号
     */
    public String serialNumber(String unitCode, String deviceType) {
        return unitCode + deviceType + RandomUtil.getRandomOfServen();
    }

    /**
     * 校验设施编号唯一
     *
     * @param deviceCode 设施编号
     * @return boolean
     */
    private boolean checkDeviceCode(String deviceCode) {
        List<DeviceInfo> deviceInfo = deviceInfoDao.checkDeviceCode(deviceCode);
        return deviceInfo == null || deviceInfo.size() == 0;
    }

    /**
     * 校验设施基本参数
     *
     * @param deviceInfo
     * @return
     */
    private boolean checkDeviceParams(DeviceInfo deviceInfo) {
        if (StringUtils.isBlank(deviceInfo.getDeviceName()) || StringUtils.isBlank(deviceInfo.getDeviceType())
                || StringUtils.isBlank(deviceInfo.getAreaId())
                || StringUtils.isBlank(deviceInfo.getPositionBase())
                || StringUtils.isBlank(deviceInfo.getPositionGps())
                || StringUtils.isBlank(deviceInfo.getAddress())) {
            return true;
        }
        return false;
    }

    /**
     * 查询当前用户信息
     *
     * @return
     */
    private User getCurrentUser() {
        //查询当前用户权限信息
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        Object userObj = userFeign.queryCurrentUser(userId, token);
        //转换为User
        return DeviceInfoService.convertObjectToUser(userObj);
    }

    /**
     * 查询用户所有设施基本信息
     *
     * @return 设施基本信息
     */
    @Override
    public Result queryDeviceAreaList() {
        return ResultUtils.success(queryDeviceAreaListByUser());
    }

    /**
     * 首页根据设施Id查询设施基本信息
     *
     * @param deviceId 设施Id
     * @return 设施基本信息
     */
    @Override
    public Result queryHomeDeviceById(String deviceId) {
        HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceId);
        if (homeDeviceInfoDto == null) {
            throw new FiLinkAreaDirtyDataException(I18nUtils.getSystemString(DeviceI18n.DEVICE_IS_NOT_EXIST));
        }
        return ResultUtils.success(homeDeviceInfoDto);
    }

    /**
     * 首页根据设施IdList查询设施基本信息
     *
     * @param deviceIds 设施IdList
     * @return 设施基本信息
     */
    @Override
    public Result queryHomeDeviceByIds(List<String> deviceIds) {
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = deviceInfoDao.queryDeviceAreaByIds(deviceIds);
        return ResultUtils.success(homeDeviceInfoDtoList);
    }

    /**
     * 修改首页首次加载阈值（设施数量）
     *
     * @param homeDeviceLimit 首页首次加载阈值
     */
    @Override
    public void updateHomeDeviceLimit(Integer homeDeviceLimit) {
        RedisUtils.set(DeviceConstant.HOME_DEVICE_LIMIT, homeDeviceLimit);
    }

    /**
     * 首页查询用户所有设施信息
     *
     * @return 首页设施信息
     */
    @Override
    public Result queryHomeDeviceArea() {
        //获取首页首次加载设施数量阈值
        Integer homeDeviceLimit = getHomeDeviceLimit();
        HomeDeviceAreaInfo homeDeviceAreaInfo = queryHomeDeviceAreaByType(homeDeviceLimit, null);
        return ResultUtils.success(homeDeviceAreaInfo);
    }

    /**
     * 首页刷新用户所有设施信息
     *
     * @return 首页设施信息
     */
    @Override
    public Result refreshHomeDeviceArea() {
        HomeDeviceAreaInfo homeDeviceAreaInfo = queryHomeDeviceAreaByType(null, null);
        return ResultUtils.success(homeDeviceAreaInfo);
    }

    /**
     * 首页刷新用户指定设施信息（大数据）
     *
     * @param areaIdList 首页刷新的区域
     * @return 首页设施信息
     */
    @Override
    public Result refreshHomeDeviceAreaHuge(List<String> areaIdList) {
        HomeDeviceAreaInfo homeDeviceAreaInfo = queryHomeDeviceAreaByType(null, areaIdList);
        return ResultUtils.success(homeDeviceAreaInfo);
    }

    /**
     * 首页查询或刷新用户所有设施信息
     *
     * @param homeDeviceLimit 首页首次加载阈值
     * @param areaIdList      首页刷新的区域
     * @return 首页设施信息
     */
    private HomeDeviceAreaInfo queryHomeDeviceAreaByType(Integer homeDeviceLimit, List<String> areaIdList) {
        HomeDeviceAreaInfo homeDeviceAreaInfo;
        if (DeviceConstant.ADMIN.equals(RequestInfoUtils.getUserId())) {
            homeDeviceAreaInfo = queryAdminHomeDevice(homeDeviceLimit, areaIdList);
        } else {
            homeDeviceAreaInfo = queryUserHomeDevice(homeDeviceLimit, areaIdList);
        }
        return homeDeviceAreaInfo;
    }

    /**
     * 查询首页非admin用户所有设施
     *
     * @return 设施
     */
    private HomeDeviceAreaInfo queryUserHomeDevice(Integer homeDeviceLimit, List<String> areaIdList) {
        //查询当前用户权限信息
        User user = getCurrentUser();
        //用户管理区域
        List<String> areaIds = user.getDepartment().getAreaIdList();
        if (CollectionUtils.isEmpty(areaIds)) {
            return setNoAreaHomeDevice();
        }
        //区域设施数量
        List<AreaDeviceNum> areaDeviceNumList = new ArrayList<>();
        //用户管理设施类型
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        if (CollectionUtils.isEmpty(roleDeviceTypes)) {
            HomeDeviceAreaInfo homeDeviceAreaInfo = setNoAreaHomeDevice();
            for (String areaId : areaIds) {
                AreaDeviceNum areaDeviceNum = new AreaDeviceNum();
                areaDeviceNum.setAreaId(areaId);
                areaDeviceNumList.add(areaDeviceNum);
            }
            homeDeviceAreaInfo.setAreaDeviceNumList(areaDeviceNumList);
            return homeDeviceAreaInfo;
        }
        //设施信息
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        //查询所有设施缓存
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        //获取设施类型权限
        List<String> deviceTypes = new ArrayList<>();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }
        for (String areaId : areaIds) {
            getHomeDeviceFromRedisByArea(areaId, areaIdList, homeDeviceLimit, deviceTypes,
                    deviceInfoRedis, homeDeviceInfoDtoList, areaDeviceNumList);
        }
        return setHomeDeviceAreaInfo(homeDeviceInfoDtoList, areaDeviceNumList, homeDeviceLimit,
                null, areaIds, deviceInfoRedis);
    }

    /**
     * 查询首页Admin用户查询所有设施
     *
     * @return 设施
     */
    private HomeDeviceAreaInfo queryAdminHomeDevice(Integer homeDeviceLimit, List<String> areaIdList) {
        //查询所有设施缓存
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        //获取所有区域信息
        List<AreaInfoForeignDto> areaInfoForeignDtoList = areaInfoService.getAreaInfoFromRedis();
        //如果区域信息或设施信息为空
        if (ObjectUtils.isEmpty(deviceInfoRedis) || CollectionUtils.isEmpty(areaInfoForeignDtoList)) {
            return setNoAreaHomeDevice();
        }
        areaInfoForeignDtoList.sort(new AreaCreateTimeComparator());
        //设施信息
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        //区域设施数量
        List<AreaDeviceNum> areaDeviceNumList = new ArrayList<>();
        //组装首页设施信息
        //循环区域，获取区域设施和数量
        for (AreaInfoForeignDto areaInfoForeignDto : areaInfoForeignDtoList) {
            String areaId = areaInfoForeignDto.getAreaId();
            getHomeDeviceFromRedisByArea(areaId, areaIdList, homeDeviceLimit, null,
                    deviceInfoRedis, homeDeviceInfoDtoList, areaDeviceNumList);
        }
        return setHomeDeviceAreaInfo(homeDeviceInfoDtoList, areaDeviceNumList, homeDeviceLimit,
                areaInfoForeignDtoList, null, deviceInfoRedis);
    }

    /**
     * 组装首页设施信息
     *
     * @param homeDeviceInfoDtoList  设施信息
     * @param areaDeviceNumList      区域下设施数量
     * @param homeDeviceLimit        首页首次加载阈值
     * @param areaInfoForeignDtoList 区域信息
     * @param deviceInfoRedis        设施缓存
     * @return 首页设施信息
     */
    private HomeDeviceAreaInfo setHomeDeviceAreaInfo(List<HomeDeviceInfoDto> homeDeviceInfoDtoList, List<AreaDeviceNum> areaDeviceNumList,
                                                     Integer homeDeviceLimit, List<AreaInfoForeignDto> areaInfoForeignDtoList, List<String> areaIds,
                                                     Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis) {
        HomeDeviceAreaInfo homeDeviceAreaInfo = new HomeDeviceAreaInfo();
        //设施数量超过限制,只加载第一个区域设施
        boolean exceedLimit = homeDeviceLimit != null && homeDeviceInfoDtoList.size() > homeDeviceLimit;
        if (exceedLimit) {
            //第一个区域
            String areaId = getFirstAreaId(areaInfoForeignDtoList, areaIds);
            Map<String, HomeDeviceInfoDto> deviceInfoDtoMap = deviceInfoRedis.get(areaId);
            homeDeviceInfoDtoList = new ArrayList<>();
            //区域下存在设施
            if (deviceInfoDtoMap != null) {
                homeDeviceInfoDtoList.addAll(deviceInfoDtoMap.values());
            }
            //大数据
            homeDeviceAreaInfo.setHugeData(DeviceConstant.HOME_HUGE_DATA);
        } else {
            //非大数据
            homeDeviceAreaInfo.setHugeData(DeviceConstant.HOME_HUGE_NOT_DATA);
        }
        homeDeviceAreaInfo.setAreaDeviceNumList(areaDeviceNumList);
        homeDeviceAreaInfo.setHomeDeviceInfoDtoList(homeDeviceInfoDtoList);
        //是否超过设施限制
        exceedLimit = homeDeviceLimit != null && homeDeviceInfoDtoList.size() > homeDeviceLimit;
        homeDeviceAreaInfo.setExceedLimit(exceedLimit);
        return homeDeviceAreaInfo;
    }

    /**
     * 根据用户设施类型权限获取区域下有权限的设施信息、计算设施数量
     *
     * @param areaId                区域Id
     * @param homeDeviceLimit       首页首次加载阈值
     * @param deviceTypes           设施类型权限
     * @param deviceInfoRedis       设施缓存
     * @param homeDeviceInfoDtoList 首页设施信息
     * @param areaDeviceNumList     首页设施信息
     */
    private void getHomeDeviceFromRedisByArea(String areaId, List<String> areaIdList, Integer homeDeviceLimit, List<String> deviceTypes,
                                              Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis,
                                              List<HomeDeviceInfoDto> homeDeviceInfoDtoList, List<AreaDeviceNum> areaDeviceNumList) {
        AreaDeviceNum areaDeviceNum = new AreaDeviceNum();
        areaDeviceNum.setAreaId(areaId);
        //获取对应区域设施
        Map<String, HomeDeviceInfoDto> deviceInfoDtoMap = deviceInfoRedis.get(areaId);
        //该区域下有设施
        if (deviceInfoDtoMap != null) {
            if (deviceTypes == null) {
                areaDeviceNum.setDeviceNum(deviceInfoDtoMap.size());
                //设施数量超过限制时停止获取设施信息
                if (homeDeviceLimit == null || homeDeviceInfoDtoList.size() <= homeDeviceLimit) {
                    //大数据刷新时，刷新选定区域信息
                    if (CollectionUtils.isEmpty(areaIdList) || areaIdList.contains(areaId)) {
                        homeDeviceInfoDtoList.addAll(deviceInfoDtoMap.values());
                    }
                }
            } else {
                getHomeDeviceAndNumForUser(homeDeviceLimit, deviceTypes,
                        areaDeviceNum, deviceInfoDtoMap, homeDeviceInfoDtoList);
            }
        }
        areaDeviceNumList.add(areaDeviceNum);
    }

    /**
     * 根据用户设施类型权限获取区域下有权限的设施信息、计算设施数量
     *
     * @param homeDeviceLimit       首页首次加载阈值
     * @param deviceTypes           设施类型权限
     * @param areaDeviceNum         区域设施数量
     * @param deviceInfoDtoMap      区域下设施信息
     * @param homeDeviceInfoDtoList 首页设施信息
     */
    private void getHomeDeviceAndNumForUser(Integer homeDeviceLimit, List<String> deviceTypes, AreaDeviceNum areaDeviceNum,
                                            Map<String, HomeDeviceInfoDto> deviceInfoDtoMap, List<HomeDeviceInfoDto> homeDeviceInfoDtoList) {
        //记录区域下有权限设施数量
        int count = 0;
        for (HomeDeviceInfoDto homeDeviceInfoDto : deviceInfoDtoMap.values()) {
            //有设施类型权限
            if (deviceTypes.contains(homeDeviceInfoDto.getDeviceType())) {
                count++;
                //设施数量超过限制时停止获取设施信息
                if (homeDeviceLimit == null || homeDeviceInfoDtoList.size() <= homeDeviceLimit) {
                    homeDeviceInfoDtoList.add(homeDeviceInfoDto);
                }
            }
        }
        areaDeviceNum.setDeviceNum(count);
    }

    /**
     * 大数据获取有权限第一个区域ID
     *
     * @param areaInfoForeignDtoList 区域缓存
     * @param areaIds                区域权限ID
     * @return 第一个区域ID
     */
    private String getFirstAreaId(List<AreaInfoForeignDto> areaInfoForeignDtoList, List<String> areaIds) {
        if (CollectionUtils.isEmpty(areaInfoForeignDtoList)) {
            return areaInfoService.queryAreaListAllForPermission(areaIds);
        } else {
            return areaInfoForeignDtoList.get(0).getAreaId();
        }
    }

    /**
     * 组装没有区域或区域权限时首页设施信息
     *
     * @return 首页设施信息
     */
    private HomeDeviceAreaInfo setNoAreaHomeDevice() {
        HomeDeviceAreaInfo homeDeviceAreaInfo = new HomeDeviceAreaInfo();
        homeDeviceAreaInfo.setAreaDeviceNumList(new ArrayList<>());
        //设施数量非大数据
        homeDeviceAreaInfo.setHugeData(DeviceConstant.HOME_HUGE_NOT_DATA);
        //设施数量没有超限
        homeDeviceAreaInfo.setExceedLimit(false);
        homeDeviceAreaInfo.setHomeDeviceInfoDtoList(new ArrayList<>());
        return homeDeviceAreaInfo;
    }


    /**
     * 获取首页首次加载阈值（设施数量）
     *
     * @return 首页首次加载阈值（设施数量）
     */
    private Integer getHomeDeviceLimit() {
        Integer homeDeviceLimit = (Integer) RedisUtils.get(DeviceConstant.HOME_DEVICE_LIMIT);
        if (homeDeviceLimit == null) {
            homeDeviceLimit = parameterFeign.queryHomeDeviceLimit();
            if (homeDeviceLimit != null) {
                RedisUtils.set(DeviceConstant.HOME_DEVICE_LIMIT, homeDeviceLimit);
            } else {
                homeDeviceLimit = DeviceConstant.DEFAULT_HOME_DEVICE_LIMIT;
            }
        }
        return homeDeviceLimit;
    }

    /**
     * 查询当前用户所有设施基本信息
     *
     * @return 当前用户所有设施基本信息
     */
    private List<HomeDeviceInfoDto> queryDeviceAreaListByUser() {
        if (DeviceConstant.ADMIN.equals(RequestInfoUtils.getUserId())) {
            return queryAdminDevice();
        }
        //查询当前用户权限信息
        User user = getCurrentUser();
        //用户管理区域
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //用户管理设施类型
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        //判断是否有数据管理
        if (!(ObjectUtils.isEmpty(areaIds) || ObjectUtils.isEmpty(roleDeviceTypes))) {
            List<String> deviceTypes = new ArrayList<>();
            for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
                deviceTypes.add(roleDeviceType.getDeviceTypeId());
            }
            //查询用户所有设施基本信息
            homeDeviceInfoDtoList = queryUserDeviceRedis(areaIds, deviceTypes);
        }
        return homeDeviceInfoDtoList;
    }

    /**
     * 查询用户所有设施基本信息
     *
     * @param areaIdList  区域ID List
     * @param deviceTypes 设施类型List
     * @return 用户所有设施基本信息
     */
    private List<HomeDeviceInfoDto> queryUserDeviceRedis(List<String> areaIdList, List<String> deviceTypes) {
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        //查询所有设施信息
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        Map<String, HomeDeviceInfoDto> deviceMap = new HashMap<>(128);
        //通过数据权限筛选数据
        for (String areaId : areaIdList) {
            if (deviceInfoRedis.containsKey(areaId)) {
                deviceMap.putAll(deviceInfoRedis.get(areaId));
            }
        }
        if (!ObjectUtils.isEmpty(deviceMap)) {
            for (HomeDeviceInfoDto homeDeviceInfoDto : deviceMap.values()) {
                if (deviceTypes.contains(homeDeviceInfoDto.getDeviceType())) {
                    homeDeviceInfoDtoList.add(homeDeviceInfoDto);
                }
            }
        }
        return homeDeviceInfoDtoList;
    }

    /**
     * Admin用户查询所有设施
     *
     * @return 设施
     */
    private List<HomeDeviceInfoDto> queryAdminDevice() {
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        if (ObjectUtils.isEmpty(deviceInfoRedis)) {
            return homeDeviceInfoDtoList;
        }
        for (Map<String, HomeDeviceInfoDto> deviceInfoDtoMap : deviceInfoRedis.values()) {
            homeDeviceInfoDtoList.addAll(deviceInfoDtoMap.values());
        }
        return homeDeviceInfoDtoList;
    }


    /**
     * 查询所有设施信息
     * 数据结构 《区域ID，《设施ID， 设施信息》》》
     *
     * @return 所有设施信息
     */
    @Override
    public Map<String, Map<String, HomeDeviceInfoDto>> queryDeviceAreaAll() {
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = new HashMap<>(128);
        // 判断Redis缓存
        if (RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP)) {
            // Redis有缓存信息，从Redis获取
            deviceInfoRedis = (Map) RedisUtils.hGetMap(DeviceConstant.DEVICE_GIS_MAP);
        } else {
            // Redis没有缓存,从数据库查询并放入缓存
            List<HomeDeviceInfoDto> deviceInfoDtoList = deviceInfoDao.queryDeviceAreaList();
            if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
                deviceInfoDtoList = new ArrayList<>();
            }
            if (deviceInfoDtoList.size() > 0) {
                //如果数据库有设施数据放入缓存
                deviceInfoRedis = setDeviceRedis(deviceInfoDtoList);
            }
        }
        return deviceInfoRedis;
    }



    /**
     * 修改设施缓存
     *
     * @param deviceInfoOld     数据库原来设施信息
     * @param homeDeviceInfoDto 修改的设施信息
     */
    private void updateDeviceRedis(DeviceInfo deviceInfoOld, HomeDeviceInfoDto homeDeviceInfoDto) {
        //是否更改设施所属区域
        if (!deviceInfoOld.getAreaId().equals(homeDeviceInfoDto.getAreaId())) {
            //更改设施所属区域，移除原来设施信息
            String areaId = deviceInfoOld.getAreaId();
            //缓存是否有这个区域
            if (RedisUtils.hHasKey(DeviceConstant.DEVICE_GIS_MAP, areaId)) {
                Map<String, HomeDeviceInfoDto> deviceMap = (Map) RedisUtils.hGet(DeviceConstant.DEVICE_GIS_MAP, areaId);
                deviceMap.remove(deviceInfoOld.getDeviceId());
                RedisUtils.hSet(DeviceConstant.DEVICE_GIS_MAP, areaId, deviceMap);
            }
        }
        //存入修改的设施信息
        DeviceRedisUtil.addDeviceRedis(homeDeviceInfoDto);
    }

    /**
     * 删除缓存
     *
     * @param deviceInfoList 设施信息信息List
     */
    private void deleteDeviceRedis(List<DeviceInfo> deviceInfoList) {
        //查询所有设施信息
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        //删除缓存设施信息
        for (DeviceInfo deviceInfo : deviceInfoList) {
            removeDeviceRedis(deviceInfo.getAreaId(), deviceInfo.getDeviceId(), deviceInfoRedis);
        }
        // 存入Redis
        RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, (Map) deviceInfoRedis);
    }

    /**
     * 移除设施缓存
     *
     * @param areaId          区域ID
     * @param deviceId        设施ID
     * @param deviceInfoRedis 设施缓存
     */
    private void removeDeviceRedis(String areaId, String deviceId,
                                   Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis) {
        if (deviceInfoRedis.containsKey(areaId)) {
            Map<String, HomeDeviceInfoDto> deviceMap = deviceInfoRedis.get(areaId);
            deviceMap.remove(deviceId);
            deviceInfoRedis.put(areaId, deviceMap);
        }
    }

    /**
     * 所有设施信息放入Redis缓存
     *
     * @param deviceInfoDtoList 所有设施信息
     * @return Redis组装信息
     */
    private Map<String, Map<String, HomeDeviceInfoDto>> setDeviceRedis(List<HomeDeviceInfoDto> deviceInfoDtoList) {
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = new HashMap<>(128);
        //封装数据
        for (HomeDeviceInfoDto homeDeviceInfoDto : deviceInfoDtoList) {
            String areaId = homeDeviceInfoDto.getAreaId();
            //是否已经有这个区域
            Map<String, HomeDeviceInfoDto> deviceMap = getDeviceMap(deviceInfoRedis, areaId);
            deviceMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
            deviceInfoRedis.put(areaId, deviceMap);
        }
        // 存入Redis
        RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, (Map) deviceInfoRedis);
        return deviceInfoRedis;
    }


    /**
     * 删除设施
     *
     * @param ids 设施ids
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDeviceByIds(String[] ids) {
        //语言
        systemLanguageUtil.querySystemLanguage();

        //校验设施是否存在
        for (String id : ids) {
            checkDeviceIsExist(id);
        }

        List<String> idList = new ArrayList<>(Arrays.asList(ids));

        //查询要删除的数据记录日志
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByIds(ids);

        //如果设施关联智能标签,提示该设施有智能标签业务信息，不能删除
        Boolean isExisted = deleteDeviceFeign.checkDevice(idList);
        if (isExisted == null || isExisted) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_NOT_DELETE_WITH_LABEL,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_NOT_DELETE_WITH_LABEL));
        }

        //删除设施会删除与该设施关联的工单，告警,电子锁,RFID,图片,主控，电子锁
        log.info("delete devices step 1: delete devices");
        deviceInfoDao.deleteDeviceByIds(ids);

        //删除设施日志
        log.info("delete devices step 2: delete device logs");
        deviceLogService.deleteDeviceLogByDeviceIds(idList);

        //删除关联主控
        log.info("delete devices step 3: delete controls");
        Result result = controlFeign.deleteControlByDeviceIds(idList);
        if (ObjectUtils.isEmpty(result)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.EXCEPTION_WHILE_DELETING_CONTROL));
        }
        //添加日志
        List list = new ArrayList();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("deviceId");
            addLogBean.setDataName("deviceName");
            addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_DEVICE_FUNCTION_CODE);
            addLogBean.setOptObjId(deviceInfo.getDeviceId());
            addLogBean.setOptObj(deviceInfo.getDeviceName());
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            list.add(addLogBean);
        }
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
        //删除缓存
        deleteDeviceRedis(deviceInfoList);
        //删除设施需要修改License设施总数
        boolean permission;
        try {
            log.info("delete devices step 4: update license");
            permission = licenseFeign.updateRedisLicenseThreshold(new LicenseFeignBean(ids.length, OperationTarget.DEVICE, OperationWay.DELETE));
        } catch (Exception e) {
            permission = false;
        }
        if (!permission) {
            return ResultUtils.warn(DeviceResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.DELETE_DEVICE_FAIL));
        }
        //回收标签
        log.info("delete devices step 5: recover label");
        for (String id : idList) {
            List<String> deviceIdsOne = new ArrayList<>();
            deviceIdsOne.add(id);
            Result rLDResult = deleteDeviceFeign.recoverLabelWithDeviceId(deviceIdsOne);
            if (rLDResult == null || !ResultCode.SUCCESS.equals(rLDResult.getCode())) {
                log.error("回收标签失败，设施id：{}", id);
            }
        }
        //删除我的关注
        log.info("delete devices step 6: delete device collecting");
        deviceInfoAsync.deleteCollecting(idList, null);
        //删除关联工单
        log.info("delete devices step 7: delete workflow business");
        deviceInfoAsync.deleteProcBaseForDevice(idList, null);
        //删除关联的巡检任务
        log.info("delete devices step 8: delete inspection tasks");
        deviceInfoAsync.deleteInspectionTask(idList, null);
        //删除关联告警
        log.info("delete devices step 9: delete alarms");
        deviceInfoAsync.deleteAlarmCurrent(idList, null);
        //删除关联图片
        log.info("delete devices step 10: delete device pictures");
        deviceInfoAsync.deleteDevicePic(idList, null);
        //WebSocket消息异步推送
        deviceInfoAsync.sendDeleteDevicesMsg(deviceInfoList);
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.DELETE_DEVICE_SUCCESS));
    }

    /**
     * 设施是否可以修改
     *
     * @param deviceId 设施序列号
     * @return
     */
    @Override
    public boolean deviceCanChangeDetail(String deviceId) {
        List list = new ArrayList(Arrays.asList(deviceId));
        return hasAlarmOrOrder(list);
    }

    /**
     * 刷新指定区域的Gis map 信息
     *
     * @param areaId 区域ID
     */
    @Override
    public void refreshDeviceAreaRedis(String areaId) {
        //查询区域内所有设施ID
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = deviceInfoDao.queryDeviceAreaByAreaId(areaId);
        if (RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP)) {
            Map<String, HomeDeviceInfoDto> deviceMap;
            //缓存是否有这个区域
            if (RedisUtils.hHasKey(DeviceConstant.DEVICE_GIS_MAP, areaId)) {
                deviceMap = (Map) RedisUtils.hGet(DeviceConstant.DEVICE_GIS_MAP, areaId);
                for (HomeDeviceInfoDto homeDeviceInfoDto : homeDeviceInfoDtoList) {
                    deviceMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
                }
                RedisUtils.hSet(DeviceConstant.DEVICE_GIS_MAP, areaId, deviceMap);
            }
        }
    }

    /**
     * 导出设施列表
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportDeviceList(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo;
        systemLanguageUtil.querySystemLanguage();
        try {
            exportRequestInfo = deviceExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(DeviceI18n.DEVICE_INFO_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(DeviceI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            fe.printStackTrace();
            String string = I18nUtils.getSystemString(DeviceI18n.EXPORT_DATA_TOO_LARGE);
            String dataCount = fe.getMessage();
            Object[] params = {dataCount, maxExportDataSize};
            String msg = MessageFormat.format(string, params);
            return ResultUtils.warn(DeviceResultCode.EXPORT_DATA_TOO_LARGE, msg);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(DeviceI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(DeviceI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto);
        deviceExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 列表导出记录日志
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setDataOptType("export");
        addLogBean.setFunctionCode(LogFunctionCodeConstant.EXPORT_DEVICE_FUNCTION_CODE);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 请求附近设施列表
     *
     * @param deviceReqPda 请求参数
     * @return 返回结果
     */
    @Override
    public Result queryNearbyDeviceListForPda(DeviceReqPda deviceReqPda) {
        //校验参数
        if (deviceReqPda == null || deviceReqPda.getPageCondition() == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAM_NULL));
        }

        User user = getCurrentUser();
        //添加权限
        boolean hasCount = addAuth(deviceReqPda, user);

        Integer begin = (deviceReqPda.getPageCondition().getPageNum() - 1) * deviceReqPda.getPageCondition().getPageSize();
        deviceReqPda.getPageCondition().setBeginNum(begin);

        // 构造分页条件
        PageCondition pageCondition = deviceReqPda.getPageCondition();
        Page page = new Page(pageCondition.getPageNum(), pageCondition.getPageSize());
        page.setCurrent(pageCondition.getPageNum());

        //用户权限下没有数据
        if (!hasCount) {
            PageBean pageBean = MpQueryHelper.myBatiesBuildPageBean(page, 0, new ArrayList<DeviceInfoForPda>());
            return ResultUtils.success(pageBean);
        }

        Integer count = deviceInfoDao.queryNearbyDeviceCount(deviceReqPda, user.getId());
        //查询设施
        List<DeviceInfoForPda> devicePdaList = deviceInfoDao.queryNearbyDeviceList(deviceReqPda, user.getId());

        //添加设施是否包含电子锁
        updateContainsLocks(devicePdaList);

        //添加设施实景图片
        updateDevicePic(devicePdaList);

        PageBean pageBean = MpQueryHelper.myBatiesBuildPageBean(page, count, devicePdaList);
        return ResultUtils.success(pageBean);
    }

    /**
     * 添加设施实景图片
     *
     * @param devicePdaList
     */
    private void updateDevicePic(List<DeviceInfoForPda> devicePdaList) {
        if (ObjectUtils.isEmpty(devicePdaList)) {
            return;
        }
        Set<String> deviceIdSet = new HashSet<>();
        for (DeviceInfoForPda devicePicReq : devicePdaList) {
            deviceIdSet.add(devicePicReq.getDeviceId());
        }
        DevicePicReq devicePicReq = new DevicePicReq();
        devicePicReq.setDeviceIds(deviceIdSet);
        //设置图片来源，3表示实景图
        devicePicReq.setResource(DeviceConstant.DEVICE_PIC_RESOURCE_REALISTIC);
        //返回图片张数
        devicePicReq.setPicNum("1");
        List<DevicePicDto> devicePicDtoList = deviceInfoDao.queryPicInfoByDeviceIds(devicePicReq);


        //合并所有设施图片
        Map<String, Set<DevicePicDto>> devicePicMap = new HashMap<>(64);
        if (!ObjectUtils.isEmpty(devicePicDtoList)) {

            for (DevicePicDto devicePicDto : devicePicDtoList) {
                devicePicDto.setPicUrlBase(devicePicDto.getPicUrlBase());
                devicePicDto.setPicUrlThumbnail(devicePicDto.getPicUrlThumbnail());

                String deviceId = devicePicDto.getDeviceId();
                devicePicDto.setDeviceId(null);
                if (devicePicMap.containsKey(deviceId)) {
                    devicePicMap.get(deviceId).add(devicePicDto);
                } else {
                    Set<DevicePicDto> picSet = new HashSet<>();
                    picSet.add(devicePicDto);
                    devicePicMap.put(deviceId, picSet);
                }
            }
        }

        //添加设施图片集合
        for (DeviceInfoForPda deviceInfoForPda : devicePdaList) {
            Set<DevicePicDto> picSet = devicePicMap.get(deviceInfoForPda.getDeviceId());
            if (ObjectUtils.isEmpty(picSet)) {
                deviceInfoForPda.setPicList(new ArrayList<>());
            } else {
                deviceInfoForPda.setPicList(new ArrayList<>(picSet));
            }
        }

    }

    /**
     * 更新设施是否包含电子锁
     *
     * @param devicePdaList
     */
    private void updateContainsLocks(List<DeviceInfoForPda> devicePdaList) {
        if (ObjectUtils.isEmpty(devicePdaList)) {
            return;
        }
        List<String> deviceIdList = new ArrayList<>();
        for (DeviceInfoForPda deviceInfoForPda : devicePdaList) {
            deviceIdList.add(deviceInfoForPda.getDeviceId());
        }
        List<Lock> lockList = lockFeign.lockListByDeviceIds(deviceIdList);

        Set<String> deviceIdSet = new HashSet<>();
        if (!ObjectUtils.isEmpty(lockList)) {
            for (Lock lock : lockList) {
                deviceIdSet.add(lock.getDeviceId());
            }
        }
        for (DeviceInfoForPda deviceInfoForPda : devicePdaList) {
            if (deviceIdSet.contains(deviceInfoForPda.getDeviceId())) {
                deviceInfoForPda.setContainsLocks(DeviceConstant.DEVICE_CONTAINS_LOCKS);
            } else {
                deviceInfoForPda.setContainsLocks(DeviceConstant.DEVICE_DOES_NOT_CONTAIN_LOCKS);
            }
        }
    }

    private boolean addAuth(DeviceReqPda deviceReqPda, User user) {
        if (user == null) {
            user = getCurrentUser();
        }
        if (DeviceConstant.ADMIN.equals(user.getId())) {
            return true;
        }
        //用户管理区域
        List<String> areaIds = getUserAreaIds(user);
        //用户管理设施类型
        List<String> deviceTypes = getDeviceTypes(user);

        //添加区域权限
        if (ObjectUtils.isEmpty(deviceReqPda.getAreaId())) {
            deviceReqPda.setAreaId(areaIds);
        } else {
            deviceReqPda.getAreaId().retainAll(areaIds);
        }
        //添加设施类型权限
        if (ObjectUtils.isEmpty(deviceReqPda.getDeviceType())) {
            deviceReqPda.setDeviceType(deviceTypes);
        } else {
            deviceReqPda.getDeviceType().retainAll(deviceTypes);
        }

        if (deviceReqPda.getAreaId() != null && deviceReqPda.getAreaId().size() == 0) {
            return false;
        }
        if (deviceReqPda.getDeviceType() != null && deviceReqPda.getDeviceType().size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 更改指定设施的设施状态，部署状态
     *
     * @param deviceInfoDto 更新信息参数
     * @throws Exception
     */
    @Override
    public Result updateDeviceStatus(DeviceInfoDto deviceInfoDto) throws Exception {
        // 检查参数deviceId不能为空
        if (deviceInfoDto == null || StringUtils.isEmpty(deviceInfoDto.getDeviceId())) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_ID_LOSE));
        }

        // 校验设施是否存在
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(deviceInfoDto.getDeviceId());
        if (ObjectUtils.isEmpty(deviceInfo)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.DEVICE_IS_NOT_EXIST));
        }

        //更新设施状态
        String newDeviceStatus = deviceInfoDto.getDeviceStatus();
        String oldDeviceStatus = deviceInfo.getDeviceStatus();
        if (!StringUtils.isEmpty(newDeviceStatus)) {
            if(newDeviceStatus.equals(oldDeviceStatus)){
                return ResultUtils.success();
            }
            deviceInfo.setDeviceStatus(newDeviceStatus);
        }
        //更新部署状态
        String newDeployStatus = deviceInfoDto.getDeployStatus();
        String oldDeployStatus = deviceInfo.getDeployStatus();
        if (!StringUtils.isEmpty(newDeployStatus)) {
            if(newDeployStatus.equals(oldDeployStatus)){
                return ResultUtils.success();
            }
            deviceInfo.setDeployStatus(deviceInfoDto.getDeployStatus());
        }
        deviceInfoDao.updateById(deviceInfo);
        deviceInfoAsync.sendUpdateMsg(deviceInfo);
        return ResultUtils.success();
    }

    /**
     * 更改指定设施的部署状态
     *
     * @param updateDeviceStatusPda 更新信息参数
     * @return
     * @throws Exception
     */
    @Override
    public Result updateDeviceListStatus(UpdateDeviceStatusPda updateDeviceStatusPda) {
        if (updateDeviceStatusPda == null || ObjectUtils.isEmpty(updateDeviceStatusPda.getDeviceIdList())
                || StringUtils.isEmpty(updateDeviceStatusPda.getDeployStatus())) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR, I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        List<String> deviceIdList = updateDeviceStatusPda.getDeviceIdList();
        String[] deviceIds = new String[deviceIdList.size()];
        deviceIdList.toArray(deviceIds);
        //查询设施是否存在
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByIds(deviceIds);
        if (deviceInfoList.size() < deviceIds.length) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_ID_LOSE, I18nUtils.getSystemString(DeviceI18n.DEVICE_ID_LOSE));
        }
        //更新部署状态
        deviceInfoDao.updateDeviceListStatus(updateDeviceStatusPda);
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.UPDATE_DEVICE_SUCCESS));
    }

    /**
     * 获取配置默认值
     *
     * @param deviceId 设施id
     * @return配置默认值
     */
    @Override
    public Map<String, String> getDefaultParamsByDeviceType(String deviceId) {
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(deviceId);
        return deviceConfigService.getDefaultParams(deviceInfo.getDeviceType());
    }

    /**
     * 根据区域id和设施类型查询设施
     *
     * @param deviceParam
     * @return
     */
    @Override
    public Result queryDeviceDtoForPageSelection(DeviceParam deviceParam) {
        //校验区域ID不能为空
        if (deviceParam == null || ObjectUtils.isEmpty(deviceParam.getAreaIds())) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR, I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }

        List<FilterCondition> filterConditionList = new ArrayList<>();
        //添加删除查询条件
        filterConditionList.add(DeviceInfoService.generateDeleteFilterCondition());
        //添加区域ID查询条件
        filterConditionList.add(DeviceInfoService.generateFilterCondition("areaId", "in", deviceParam.getAreaIds()));

        //添加设施类型过滤器
        if (!ObjectUtils.isEmpty(deviceParam.getDeviceTypes())) {
            filterConditionList.add(DeviceInfoService.generateFilterCondition("deviceType", "in", deviceParam.getDeviceTypes()));
        }

        QueryCondition<DeviceInfo> queryCondition = new QueryCondition<>();
        queryCondition.setFilterConditions(filterConditionList);

        // 构造过滤、排序等条件
        EntityWrapper<DeviceInfo> wrapper = MpQueryHelper.myBatiesBuildQuery(queryCondition);

        // 查询数据
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectList(wrapper);
        //转换
        List<DeviceInfoDto> deviceInfoDtoList = convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());

        return ResultUtils.success(deviceInfoDtoList);
    }

    /**
     * 通过区域ID获取区域下设施缓存
     *
     * @param deviceInfoRedis 设施缓存
     * @param areaId          区域ID
     * @return 区域下设施缓存
     */
    private Map<String, HomeDeviceInfoDto> getDeviceMap
    (Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis, String areaId) {
        Map<String, HomeDeviceInfoDto> deviceMap;
        //缓存是否有这个区域
        if (deviceInfoRedis.containsKey(areaId)) {
            deviceMap = deviceInfoRedis.get(areaId);
        } else {
            deviceMap = new HashMap<>(128);
        }
        return deviceMap;
    }

    /**
     * 有告警或工单
     *
     * @param deviceIds
     * @return
     */
    public boolean hasAlarmOrOrder(List<String> deviceIds) {
        List<String> list = alarmCurrentFeign.queryAlarmSourceForFeign(deviceIds);
        if (list == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_ALARM_INFORMATION));
        }

        Result result = procBaseFeign.queryProcExitsForDeviceIds(deviceIds);
        if (result == null || result.getCode() != ResultCode.SUCCESS) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_WORK_ORDER_INFORMATION));
        }
        List procBaseList = (List) result.getData();
        if (ObjectUtils.isEmpty(list) && ObjectUtils.isEmpty(procBaseList)) {
            return true;
        }
        return false;
    }

    private void updateRedisBatch(List<HomeDeviceInfoDto> homeDeviceInfoDtoList, String areaId, AreaInfo areaInfo) {
        //查询所有设施信息
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        //删除缓存设施信息
        for (HomeDeviceInfoDto homeDeviceInfoDto : homeDeviceInfoDtoList) {
            removeDeviceRedis(homeDeviceInfoDto.getAreaId(), homeDeviceInfoDto.getDeviceId(), deviceInfoRedis);
            // Redis有缓存信息，从Redis获取,缓存是否有这个区域
            Map<String, HomeDeviceInfoDto> deviceMap = getDeviceMap(deviceInfoRedis, areaId);
            homeDeviceInfoDto.setAreaId(areaId);
            homeDeviceInfoDto.setAreaName(areaInfo.getAreaName());
            deviceMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
            deviceInfoRedis.put(areaId, deviceMap);
        }
        // 存入Redis
        RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, (Map) deviceInfoRedis);
    }

    /**
     * 查询有当前告警的设施
     *
     * @param deviceIds
     * @return
     */
    private List<String> queryAlarmAndWorkOrderSource(List<String> deviceIds) {
        Result<List<String>> result = procBaseFeign.queryProcExitsForDeviceIds(deviceIds);
        if (result == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_WORK_ORDER_INFORMATION));
        }
        List<String> proDeviceIds = (List<String>) result.getData();
        if (proDeviceIds != null && proDeviceIds.size() > 0) {
            return proDeviceIds;
        }
        List<String> list = alarmCurrentFeign.queryAlarmSourceForFeign(deviceIds);
        if (list == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_ALARM_INFORMATION));
        }
        return list;
    }

}
