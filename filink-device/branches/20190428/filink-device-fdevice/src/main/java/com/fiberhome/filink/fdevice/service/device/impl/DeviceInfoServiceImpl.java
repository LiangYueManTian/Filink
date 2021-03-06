package com.fiberhome.filink.fdevice.service.device.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePicReq;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.async.DeviceInfoAsync;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.fdevice.constant.device.*;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceCollectingDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.exception.*;
import com.fiberhome.filink.fdevice.export.DeviceExport;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.utils.RandomUtil;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.enums.OperationTarget;
import com.fiberhome.filink.license.enums.OperationWay;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.txlcntc.annotation.LcnTransaction;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.fiberhome.filink.workflowbusinessapi.req.inspectiontask.DeleteInspectionTaskForDeviceReq;
import com.fiberhome.filink.workflowbusinessapi.req.procbase.DeleteProcBaseForDeviceReq;
import lombok.extern.slf4j.Slf4j;
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

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.*;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

/**
 * <p>
 * ???????????????
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
    private DevicePicFeign devicePicFeign;

    @Autowired
    private FdfsFeign fdfsFeign;

    /**
     * ????????????DAO
     */
    @Autowired
    private DeviceCollectingDao deviceCollectingDao;

    /**
     * ????????????Dao
     */
    @Autowired
    private AreaInfoDao areaInfoDao;

    /**
     * ?????????????????????
     */
    @Autowired
    private DeviceExport deviceExport;

    @Autowired
    private DeviceInfoAsync deviceInfoAsync;
    /**
     * ????????????????????????
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;

    /**
     * ????????????????????????
     */
    @Autowired
    private InspectionTaskFeign inspectionTaskFeign;


    /**
     * ?????????
     */
    private static String SERVER_NAME = "filink-device-server";

    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;


    @Value("${unitCode}")
    private String unitCode;

    /**
     * ????????????
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     */
    public Result addDeviceInfo(DeviceInfo deviceInfo) {
        // ????????????
        validateDeviceParam(deviceInfo);

        // TODO ???????????????????????????
//        checkAuthorization(deviceInfo.getAreaId(),deviceInfo.getDeviceType());

        // ???????????????????????????
        if (checkDeviceName(null, deviceInfo.getDeviceName())) {
            throw new FilinkDeviceNameSameException();
        }
        // ????????????ID????????????
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId());
        if (areaInfo == null) {
            throw new FilinkAreaDoesNotExistException();
        }
        // ????????????
        String deviceCode;
        while (!checkDeviceCode(deviceCode = serialNumber(unitCode, deviceInfo.getDeviceType()))) {
            log.info("Repeated deviceCode: " + deviceCode);
        }
        log.info("The deviceCode is: " + deviceCode);
        deviceInfo.setDeviceCode(deviceCode);

        //????????????????????????????????????
        String userId = RequestInfoUtils.getUserId();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        deviceInfo.setCreateUser(userId);
        deviceInfo.setCreateTime(timestamp);
        deviceInfo.setUpdateUser(userId);
        deviceInfo.setUpdateTime(timestamp);

        // ???????????????????????????????????????????????????
        if (DeviceType.Optical_Box.getCode().equals(deviceInfo.getDeviceType())
                || DeviceType.Well.getCode().equals(deviceInfo.getDeviceType())) {
            deviceInfo.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        } else {
            deviceInfo.setDeviceStatus(DeviceStatus.Normal.getCode());
        }

        // ???????????????
        deviceInfo.setDeployStatus(DeployStatus.DEPLOYED.getCode());

        //???????????????????????????????????????????????????????????????????????????License????????????
        LicenseFeignBean licenseFeignBean = new LicenseFeignBean(1, OperationTarget.DEVICE, OperationWay.ADD);
        boolean permission;
        try {
            permission = licenseFeign.updateRedisLicenseThreshold(licenseFeignBean);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.FAIL_TO_INVOKE_LICENSE_FEIGN));
        }

        if (permission) {
            //????????????
            deviceInfo.setDeviceId(NineteenUUIDUtils.uuid());

            // ???????????????
            if (deviceInfoDao.insertDevice(deviceInfo) != 1) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.ADD_DEVICE_FAIL));
            }
        } else {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.DEVICE_COUNT_EXCEEDS_LICENSE_LIMIT));
        }

        // ????????????
        try {
            //????????????
            HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
            addDeviceRedis(homeDeviceInfoDto);

            //WebSocket??????????????????
            deviceInfoAsync.sendDeviceMsg(DeviceWebSocketCode.ADD_DEVICE_CHANNEL_ID,
                    DeviceWebSocketCode.ADD_DEVICE_CHANNEL_KEY, homeDeviceInfoDto);
        } catch (Exception e) {
            log.error("?????????????????????");
        }
        //????????????ID
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", deviceInfo.getDeviceId());
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.ADD_DEVICE_SUCCESS), jsonObject);
    }

    /**
     * ??????????????????
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     * @throws Exception ??????
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE,
            functionCode = LogFunctionCodeConstant.INSERT_DEVICE_FUNCTION_CODE,
            dataGetColumnName = "deviceName", dataGetColumnId = "deviceId")
    @Override
    public Result addDevice(DeviceInfo deviceInfo) {
        return addDeviceInfo(deviceInfo);
    }

    /**
     * PDA????????????
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     * @throws Exception ??????
     */
    @Override
    public Result addDeviceForPda(DeviceInfo deviceInfo) {
        Result result = addDeviceInfo(deviceInfo);
        //????????????
        addDeviceOperateLog(deviceInfo, LogConstants.OPT_TYPE_PDA, LogConstants.DATA_OPT_TYPE_ADD,
                LogFunctionCodeConstant.INSERT_DEVICE_FUNCTION_CODE);
        return result;
    }

    /**
     * ??????????????????
     *
     * @param deviceInfo ????????????
     * @throws FilinkDeviceException ?????????????????????????????????
     */
    private void validateDeviceParam(DeviceInfo deviceInfo) throws FilinkDeviceException {
        //??????????????????
        if (checkDeviceParams(deviceInfo)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        //???????????????
        deviceInfo.parameterFormat();

        //??????????????????????????????
        if (!deviceInfo.checkDevicePositionBase() || !deviceInfo.checkDevicePositionGps()) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
        }
        // ????????????????????????
        if (!deviceInfo.checkDeviceName()) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_NAME_ERROR));
        }
        //??????????????????????????????????????????
        if (!deviceInfo.checkParameterFormat()) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param deviceId   ??????id
     * @param deviceName ????????????
     * @return boolean
     */
    @Override
    public boolean checkDeviceName(String deviceId, String deviceName) {
        DeviceInfo deviceInfo = deviceInfoDao.selectByName(deviceName);
        // deviceId???????????????????????????????????????????????????
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
     * ??????????????????
     *
     * @param deviceInfo ??????????????????
     * @return Result
     */
    public Result updateDeviceInfo(DeviceInfo deviceInfo) {

        // ????????????id
        if (deviceInfo == null || StringUtils.isEmpty(deviceInfo.getDeviceId())) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
        }

        // ????????????
        validateDeviceParam(deviceInfo);

        // TODO ???????????????????????????
//        checkAuthorization(deviceInfo.getAreaId(),deviceInfo.getDeviceType());

        // ????????????????????????
        DeviceInfo oriDeviceInfo = checkDeviceIsExist(deviceInfo.getDeviceId());

        // ???????????????????????????
        if (checkDeviceName(deviceInfo.getDeviceId(), deviceInfo.getDeviceName())) {
            throw new FilinkDeviceNameSameException();
        }

        // ????????????ID????????????
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId());
        if (areaInfo == null) {
            throw new FilinkAreaDoesNotExistException();
        }

        // ????????????????????????
        deviceInfo.setDeviceType(oriDeviceInfo.getDeviceType());

        // ??????????????????????????????
        deviceInfo.setUpdateUser(RequestInfoUtils.getUserId());
        deviceInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

        // ???????????????
        int result = deviceInfoDao.updateById(deviceInfo);
        if (result != 1) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.UPDATE_DEVICE_FAIL));
        }

        // ????????????
        try {
            HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
            updateDeviceRedis(oriDeviceInfo, homeDeviceInfoDto);
            //WebSocket??????????????????
            if (oriDeviceInfo.getAreaId().equals(deviceInfo.getAreaId())) {
                deviceInfoAsync.sendDeviceMsg(DeviceWebSocketCode.UPDATE_DEVICE_CHANNEL_ID,
                        DeviceWebSocketCode.UPDATE_DEVICE_CHANNEL_KEY, homeDeviceInfoDto);
            } else {
                deviceInfoAsync.sendUpdatingAreaMsg(oriDeviceInfo.getAreaId(), oriDeviceInfo.getDeviceType(), homeDeviceInfoDto);
            }
        } catch (Exception e) {
            log.error("?????????????????????");
        }

        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.UPDATE_DEVICE_SUCCESS));
    }

    /**
     * ??????????????????
     *
     * @param deviceInfo ??????????????????
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
     * ??????PDA????????????
     * @param deviceInfo
     * @return
     */
    @Override
    public Result updateDeviceForPda(DeviceInfo deviceInfo) {
        Result result = updateDeviceInfo(deviceInfo);
        //????????????
        addDeviceOperateLog(deviceInfo, LogConstants.OPT_TYPE_PDA, LogConstants.DATA_OPT_TYPE_UPDATE,
                LogFunctionCodeConstant.UPDATE_DEVICE_FUNCTION_CODE);
        return result;
    }

    /**
     * ??????????????????
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
     * ????????????????????????
     *
     * @param queryCondition ???????????????
     * @return Result
     * @throws Exception ??????
     */
    @Override
    public Result listDevice(QueryCondition<DeviceInfo> queryCondition) {
        return listDevice(queryCondition, getCurrentUser(), true);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param queryCondition ????????????
     * @param user           ????????????
     * @param needsAuth      ????????????????????????
     * @return
     */
    @Override
    public Result listDevice(QueryCondition<DeviceInfo> queryCondition, User user, boolean needsAuth) {
        //????????????
        if (queryCondition == null || queryCondition.getPageCondition() == null) {
            throw new FilinkAreaException(I18nUtils.getString(DeviceI18n.PARAM_NULL));
        }

        //????????????????????????
        if (needsAuth) {
            addAuth(queryCondition.getFilterConditions(), user);
        }

        //?????????????????????,??????????????????
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

        //??????like???????????????
        alterLikeFilterCondition(queryCondition.getFilterConditions());

        // ??????????????????
        Page page = myBatiesBuildPage(queryCondition);

        //?????????????????????????????????
        for (FilterCondition fc : queryCondition.getFilterConditions()) {
            if ("in".equalsIgnoreCase(fc.getOperator()) && ObjectUtils.isEmpty(fc.getFilterValue())) {
                PageBean pageBean = myBatiesBuildPageBean(page, 0, new ArrayList<DeviceInfoDto>());
                return ResultUtils.pageSuccess(pageBean);
            }
        }

        //????????????????????????????????????
        List<DeviceInfoDto> deviceInfoDtoList = deviceInfoDao.selectDevicePage(queryCondition.getPageCondition(),
                queryCondition.getFilterConditions(), queryCondition.getSortCondition());

        // ??????Long???????????????
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
            fillCTimeUTime(deviceInfoDto, deviceInfoDto.getCreateTime(), deviceInfoDto.getUpdateTime());
        }

        //???????????????????????????????????????
        updateDeviceIsCollecting(deviceInfoDtoList, user.getId());

        //??????????????????
        updateDeviceLock(deviceInfoDtoList);

        Integer count = deviceInfoDao.selectDeviceCount(queryCondition.getFilterConditions());

        PageBean pageBean = myBatiesBuildPageBean(page, count, deviceInfoDtoList);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * ????????????????????????
     *
     * @param queryCondition
     * @param user
     * @return
     */
    @Override
    public Integer queryDeviceCount(QueryCondition<DeviceInfo> queryCondition, User user) {
        //????????????
        if (queryCondition == null || queryCondition.getPageCondition() == null) {
            throw new FilinkAreaException(I18nUtils.getString(DeviceI18n.PARAM_NULL));
        }

        if (ObjectUtils.isEmpty(user)) {
            user = getCurrentUser();
        }

        //????????????????????????
        addAuth(queryCondition.getFilterConditions(), user);

        //?????????????????????????????????
        for (FilterCondition fc : queryCondition.getFilterConditions()) {
            if ("in".equalsIgnoreCase(fc.getOperator()) && ObjectUtils.isEmpty(fc.getFilterValue())) {
                return 0;
            }
        }

        //??????like???????????????
        alterLikeFilterCondition(queryCondition.getFilterConditions());

        return deviceInfoDao.selectDeviceCount(queryCondition.getFilterConditions());
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @Override
    public Integer queryCurrentDeviceCount() {
        QueryCondition queryCondition = new QueryCondition();
        List<FilterCondition> filterConditionList = new ArrayList<>();
        filterConditionList.add(DeviceInfoService.generateDeleteFilterCondition());
        queryCondition.setFilterConditions(filterConditionList);
        Wrapper<DeviceInfo> wrapper = myBatiesBuildQuery(queryCondition);
        return deviceInfoDao.selectCount(wrapper);
    }

    /**
     * ??????sql??????????????????
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
     * ??????????????????????????????????????????
     *
     * @param user
     * @return
     */
    @Override
    public DeviceParam getUserAuth(User user) {
        if (ObjectUtils.isEmpty(user)) {
            user = getCurrentUser();
        }
        //??????????????????
        List<String> areaIds = getUserAreaIds(user);
        //????????????????????????
        List<String> deviceTypes = getDeviceTypes(user);
        DeviceParam deviceParam = new DeviceParam();
        deviceParam.setAreaIds(areaIds);
        deviceParam.setDeviceTypes(deviceTypes);
        return deviceParam;
    }

    /**
     * ????????????ID??????????????????????????????
     *
     * @param areaIds ??????ID??????
     * @return ??????????????????
     */
    @Override
    public List<String> queryDeviceTypesByAreaIds(List<String> areaIds) {
        //??????????????????
        User user = getCurrentUser();
        //????????????????????????
        List<String> deviceTypes = getDeviceTypes(user);

        if (ObjectUtils.isEmpty(areaIds)) {
            return new ArrayList<>();
        }
        List<String> deviceTypeList = deviceInfoDao.queryDeviceTypesByAreaIds(areaIds);

        if(!DeviceConstant.ADMIN.equals(user.getId())) {
            deviceTypeList.retainAll(deviceTypes);
        }
        return deviceTypeList;
    }

    /**
     * ??????????????????????????????
     *
     * @param filterConditions
     */
    private static void addAuth(List<FilterCondition> filterConditions, User user) {
        if (user == null) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.USER_AUTH_INFO_ERROR));
        }
        if (DeviceConstant.ADMIN.equals(user.getId())) {
            return;
        }
        //??????????????????
        List<String> areaIds = getUserAreaIds(user);
        //????????????????????????
        List<String> deviceTypes = getDeviceTypes(user);
        filterConditions.add(DeviceInfoService.generateFilterCondition("areaId", "in", areaIds));
        filterConditions.add(DeviceInfoService.generateFilterCondition("deviceType", "in", deviceTypes));
    }


    /**
     * ????????????????????????
     *
     * @param user
     * @return
     */
    private static List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * ??????????????????????????????
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
     * ?????????????????????????????????????????????????????????
     *
     * @param areaId
     * @param deviceType
     * @return
     */
    private boolean isAuthorized(String areaId, String deviceType) {
        User user = getCurrentUser();
        //??????????????????
        List<String> areaIds = getUserAreaIds(user);
        //????????????????????????
        List<String> deviceTypes = getDeviceTypes(user);
        if (areaIds.contains(areaId) && deviceTypes.contains(deviceType)) {
            return true;
        }
        return false;
    }

    private void checkAuthorization(String areaId, String deviceType) {
        if (!isAuthorized(areaId, deviceType)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.USER_DEVICE_NOT_AUTHORIZED));
        }
    }

    /**
     * ???????????????
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
     * ????????????
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
     * ????????????id????????????
     *
     * @param areaId ??????id
     * @return ????????????
     */
    @Override
    public List<DeviceInfo> queryDeviceByAreaId(String areaId) {
        List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
        return deviceInfoList;
    }

    /**
     * ????????????id????????????Dto
     *
     * @param areaId ??????id
     * @return ????????????
     */
    @Override
    public List<DeviceInfoDto> queryDeviceDtoByAreaId(String areaId) {
        List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
        return convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());
    }

    /**
     * ????????????ids?????????????????????Dto
     *
     * @param areaIds
     * @return
     */
    @Override
    public List<DeviceInfoDto> queryDeviceDtoByAreaIds(List<String> areaIds) {
        //????????????

        List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaIds(areaIds);
        return convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());
    }

    /**
     * ????????????
     *
     * @param map ??????????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean setAreaDevice(Map<String, List<String>> map, AreaInfo areaInfo) {
        //????????????????????????????????????????????????????????????
        int a = 0;
        //????????????????????????
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
                    throw new FilinkAreaDirtyDataException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
                }
                throw new FilinkAreaDirtyDataException(
                        deviceName + I18nUtils.getString(AreaI18nConstant.HAVE_A_WORK_ORDER_OR_AN_ALARM));
            }
            List<HomeDeviceInfoDto> homeDeviceInfoDtoList = deviceInfoDao.queryDeviceAreaByIds(deviceIds);

            // TODO ???????????????????????????
//            User user = getCurrentUser();
//            List<String> userAreaIds = getUserAreaIds(user);
//            if(!userAreaIds.contains(areaInfo.getAreaId())) {
//                throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.USER_DEVICE_NOT_AUTHORIZED));
//            }


            if (homeDeviceInfoDtoList.size() != deviceIds.size()) {
                return false;
            }
            b += entry.getValue().size();
            Integer i = deviceInfoDao.setAreaDevice(entry.getKey(), deviceIds);
            if (deviceIds.size() == i) {
                // ????????????
                updateRedisBatch(homeDeviceInfoDtoList, entry.getKey(), areaInfo);
                //webSocket??????
                deviceInfoAsync.sendAreaDeviceMsg(entry.getKey(), areaInfo.getAreaName(), homeDeviceInfoDtoList);
            }
            a += i;
        }
        if (a == b) {
            return true;
        }
        throw new FilinkAreaDateBaseException();
    }


    /**
     * ??????????????????
     *
     * @param id ??????id
     * @return Result
     * @throws Exception ??????
     */
    @Override
    public Result getDeviceById(String id, String userId) {
        // ????????????id
        if (StringUtils.isEmpty(id)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
        }
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
        if (ObjectUtils.isEmpty(deviceInfo)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
        }
        // TODO ???????????????????????????
//        checkAuthorization(deviceInfo.getAreaId(),deviceInfo.getDeviceType());

        // ?????????????????????????????????
        try {
            DeviceInfoDto dto = convertDeviceInfoToDeviceInfoDto(deviceInfo, userId);
            return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.QUERY_DEVICE_SUCCESS), dto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.COPYING_PROPERTIES_FAILED));
        }
    }


    /**
     * ??????????????????
     *
     * @param deviceReq ??????????????????
     * @return Result ????????????
     * @throws Exception ??????
     */
    @Override
    public Result queryDeviceByBean(DeviceReq deviceReq) throws Exception {
        //????????????????????????
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByBean(deviceReq);
        List<DeviceInfoForPda> dtoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            //??????pda???????????????????????????
            DeviceInfoForPda deviceInfoForPda = new DeviceInfoForPda();
            copyProperties(deviceInfoForPda, deviceInfo);
            //???????????????????????????
            AreaInfo areaInfo = areaInfoService.queryAreaByIdForPda(deviceInfo.getAreaId());
            deviceInfoForPda.setAreaName(areaInfo.getAreaName());
            //???????????????????????????
            List<ControlParam> controlParamList = controlFeign.getControlParams(deviceInfo.getDeviceId());
            deviceInfoForPda.setControlParamList(controlParamList);
            dtoList.add(deviceInfoForPda);
        }
        return ResultUtils.success(dtoList);
    }

    /**
     * ??????????????????
     *
     * @param ids ??????ID??????
     * @return
     * @throws Exception
     */
    @Override
    public List<DeviceInfoDto> getDeviceByIds(String[] ids) throws Exception {
        //????????????ID????????????
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
            }
        }
        //??????ID???????????????????????????
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByIds(ids);
        //??????dtoList
        return convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());
    }

    /**
     * ??????????????????????????????
     *
     * @param ids
     * @return
     */
    @Override
    public Result getDeviceResultByIds(String[] ids) {
        List<DeviceInfoDto> dtoList;
        try {
            dtoList = getDeviceByIds(ids);
            return ResultUtils.success(dtoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtils.warn(DeviceResultCode.FAIL, I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
    }

    /**
     * ????????????????????????????????????
     *
     * @param deviceInfo ?????????????????????
     * @return DeviceInfoDto ??????????????????
     * @throws Exception ??????
     */
    private DeviceInfoDto convertDeviceInfoToDeviceInfoDto(DeviceInfo deviceInfo, String userId) throws Exception {
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        copyProperties(deviceInfoDto, deviceInfo);
        // ????????????
        Result result = areaInfoService.queryAreaById(deviceInfo.getAreaId());
//        AreaInfo areaInfo = new AreaInfo();
        if (result.getData() instanceof AreaInfo) {
            AreaInfo areaInfo = (AreaInfo) result.getData();
            deviceInfoDto.setAreaInfo(areaInfo);
        } else {
            deviceInfoDto.setAreaInfo(new AreaInfo());
        }

        //??????Long???????????????
        fillCTimeUTime(deviceInfoDto, deviceInfo.getCreateTime(), deviceInfo.getUpdateTime());

        //???????????????????????????????????????
        if (!ObjectUtils.isEmpty(userId)) {
            deviceInfoDto.setIsCollecting(isCollectingDevice(deviceInfo.getDeviceId(), userId));
        }
        return deviceInfoDto;
    }

    /**
     * ?????????????????????????????????
     *
     * @param deviceId ??????Id
     * @return 0-????????? 1-?????????
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
     * ??????Device??????????????????????????????????????????????????????Dto??????
     *
     * @param deviceInfoList Device??????????????????
     * @return ????????????Dto??????
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private List<DeviceInfoDto> convertDeviceInfoListToDtoList(List<DeviceInfo> deviceInfoList, String userId) {

        if (ObjectUtils.isEmpty(deviceInfoList)) {
            return new ArrayList<>();
        }

        //?????????????????????areaId?????????
        Set<String> areaIdSet = new HashSet<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            areaIdSet.add(deviceInfo.getAreaId());
        }

        //??????????????????
        List<String> areaIdList = new ArrayList<>(areaIdSet);
        List<AreaInfo> areaInfoList = areaInfoDao.selectAreaInfoByIds(areaIdList);
        Map<String, AreaInfo> areaMap = new HashMap<>(64);
        for (AreaInfo areaInfo : areaInfoList) {
            areaMap.put(areaInfo.getAreaId(), areaInfo);
        }

        //??????DeviceInfoDtoList
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            //??????????????????
            try {
                copyProperties(deviceInfoDto, deviceInfo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FilinkDeviceException(e.getMessage());
            }

            //??????????????????
            deviceInfoDto.setAreaInfo(areaMap.get(deviceInfo.getAreaId()));

            // ??????Long???????????????
            fillCTimeUTime(deviceInfoDto, deviceInfo.getCreateTime(), deviceInfo.getUpdateTime());

            //??????????????????
            deviceInfoDtoList.add(deviceInfoDto);
        }

        //???????????????????????????????????????
        updateDeviceIsCollecting(deviceInfoDtoList, userId);

        //??????????????????
        updateDeviceLock(deviceInfoDtoList);

        return deviceInfoDtoList;
    }

    /**
     * ????????????????????????
     *
     * @param deviceInfoDtoList
     */
    private void updateDeviceIsCollecting(List<DeviceInfoDto> deviceInfoDtoList, String userId) {
        //???????????????????????????
        Set<String> deviceIdSet = queryUserAttention(userId);
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
            //?????????????????????????????????
            if (deviceIdSet.contains(deviceInfoDto.getDeviceId())) {
                deviceInfoDto.setIsCollecting(DeviceConstant.DEVICE_IS_COLLECTED);
            } else {
                deviceInfoDto.setIsCollecting(DeviceConstant.DEVICE_IS_NOT_COLLECTED);
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @param userId
     * @return
     */
    private Set<String> queryUserAttention(String userId) {
        //?????????????????????????????????????????????????????????????????????????????????
        List<DeviceAttentionDto> deviceAttentionDtoList = deviceCollectingDao.selectAttentionList(userId, new DeviceParam());
        Set<String> deviceIdSet = new HashSet<>();
        for (DeviceAttentionDto dto : deviceAttentionDtoList) {
            deviceIdSet.add(dto.getDeviceId());
        }
        return deviceIdSet;
    }

    /**
     * ??????????????????
     *
     * @param deviceInfoDtoList
     */
    private void updateDeviceLock(List<DeviceInfoDto> deviceInfoDtoList) {
        List<String> deviceIdList = new ArrayList<>();
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
            deviceIdList.add(deviceInfoDto.getDeviceId());
            //??????deviceInfoDto??????lockLIst??????
            if (deviceInfoDto.getLockList() == null) {
                deviceInfoDto.setLockList(new ArrayList<>());
            }
        }
        List<Lock> lockLIst = lockFeign.lockListByDeviceIds(deviceIdList);
        if (lockLIst == null) {
            return;
        }
        for (Lock lock : lockLIst) {
            for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
                if (deviceInfoDto.getDeviceId().equals(lock.getDeviceId())) {
                    deviceInfoDto.getLockList().add(lock);
                    break;
                }
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param id ??????id
     * @return boolean
     */
    public DeviceInfo checkDeviceIsExist(String id) {
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
        if (ObjectUtils.isEmpty(deviceInfo)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
        }
        return deviceInfo;
    }

    /**
     * ??????????????????(????????????+??????????????????+7????????????)
     *
     * @param unitCode   ??????
     * @param deviceType ????????????
     * @return ????????????
     */
    public String serialNumber(String unitCode, String deviceType) {
        return unitCode + deviceType + RandomUtil.getRandomOfServen();
    }

    /**
     * ????????????????????????
     *
     * @param deviceCode ????????????
     * @return boolean
     */
    private boolean checkDeviceCode(String deviceCode) {
        List<DeviceInfo> deviceInfo = deviceInfoDao.checkDeviceCode(deviceCode);
        return deviceInfo == null || deviceInfo.size() == 0;
    }

    /**
     * ????????????????????????
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
     * ????????????????????????
     *
     * @return
     */
    private User getCurrentUser() {
        //??????????????????????????????
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        Object userObj = userFeign.queryCurrentUser(userId, token);
        //?????????User
        return DeviceInfoService.convertObjectToUser(userObj);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryDeviceAreaList() {
        return ResultUtils.success(queryDeviceAreaListByUser());
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryDeviceAreaListBase() {
        List<HomeDeviceInfoBase> deviceInfoBases = new ArrayList<>();
        List<HomeDeviceInfoDto> deviceInfoDtoList = queryDeviceAreaListByUser();
        if (deviceInfoDtoList.size() > 0) {
            for (HomeDeviceInfoDto homeDeviceInfoDto : deviceInfoDtoList) {
                HomeDeviceInfoBase deviceInfoBase = new HomeDeviceInfoBase();
                deviceInfoBase.setA(homeDeviceInfoDto.getDeviceId());
                deviceInfoBase.setB(homeDeviceInfoDto.getDeviceType());
                deviceInfoBase.setC(homeDeviceInfoDto.getDeviceStatus());
                deviceInfoBase.setD(homeDeviceInfoDto.getPositionBase());
                deviceInfoBase.setE(homeDeviceInfoDto.getAreaId());
                deviceInfoBases.add(deviceInfoBase);
            }
        }
        return ResultUtils.success(deviceInfoBases);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryDeviceAreaListSimple() {
        List<HomeDeviceInfoSimple> deviceInfoSimples = new ArrayList<>();
        List<HomeDeviceInfoDto> deviceInfoDtoList = queryDeviceAreaListByUser();
        if (deviceInfoDtoList.size() > 0) {
            for (HomeDeviceInfoDto homeDeviceInfoDto : deviceInfoDtoList) {
                HomeDeviceInfoSimple deviceInfoSimple = new HomeDeviceInfoSimple();
                deviceInfoSimple.setA(homeDeviceInfoDto.getDeviceId());
                deviceInfoSimple.setB(homeDeviceInfoDto.getDeviceType());
                deviceInfoSimple.setC(homeDeviceInfoDto.getDeviceStatus());
                deviceInfoSimple.setD(homeDeviceInfoDto.getPositionBase());
                deviceInfoSimple.setE(homeDeviceInfoDto.getAreaId());
                deviceInfoSimple.setF(homeDeviceInfoDto.getDeviceName());
                deviceInfoSimple.setG(homeDeviceInfoDto.getDeviceCode());
                deviceInfoSimple.setH(homeDeviceInfoDto.getAddress());
                deviceInfoSimple.setI(homeDeviceInfoDto.getAreaName());
                deviceInfoSimples.add(deviceInfoSimple);
            }
        }
        return ResultUtils.success(deviceInfoSimples);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return ????????????????????????????????????
     */
    private List<HomeDeviceInfoDto> queryDeviceAreaListByUser() {
        if (DeviceConstant.ADMIN.equals(RequestInfoUtils.getUserId())) {
            return queryAdminDevice();
        }
        //??????????????????????????????
        User user = getCurrentUser();
        //??????????????????
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //????????????????????????
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        //???????????????????????????
        if (!(ObjectUtils.isEmpty(areaIds) || ObjectUtils.isEmpty(roleDeviceTypes))) {
            List<String> deviceTypes = new ArrayList<>();
            for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
                deviceTypes.add(roleDeviceType.getDeviceTypeId());
            }
            //????????????????????????????????????
            homeDeviceInfoDtoList = queryUserDeviceRedis(areaIds, deviceTypes);
        }
        return homeDeviceInfoDtoList;
    }

    /**
     * ????????????????????????????????????
     *
     * @param areaIdList  ??????ID List
     * @param deviceTypes ????????????List
     * @return ??????????????????????????????
     */
    private List<HomeDeviceInfoDto> queryUserDeviceRedis(List<String> areaIdList, List<String> deviceTypes) {
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        //????????????????????????
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        Map<String, HomeDeviceInfoDto> deviceMap = new HashMap<>(128);
        //??????????????????????????????
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
     * Admin????????????????????????
     *
     * @return ??????
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
     * ????????????????????????
     * ???????????? ?????????ID????????????ID??? ?????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Map<String, Map<String, HomeDeviceInfoDto>> queryDeviceAreaAll() {
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = new HashMap<>(128);
        // ??????Redis??????
        if (RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP)) {
            // Redis?????????????????????Redis??????
            deviceInfoRedis = (Map) RedisUtils.hGetMap(DeviceConstant.DEVICE_GIS_MAP);
        } else {
            // Redis????????????,?????????????????????????????????
            List<HomeDeviceInfoDto> deviceInfoDtoList = deviceInfoDao.queryDeviceAreaList();
            if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
                deviceInfoDtoList = new ArrayList<>();
            }
            if (deviceInfoDtoList.size() > 0) {
                //??????????????????????????????????????????
                deviceInfoRedis = setDeviceRedis(deviceInfoDtoList);
            }
        }
        return deviceInfoRedis;
    }

    /**
     * ??????????????????
     *
     * @param homeDeviceInfoDto ??????
     */
    private void addDeviceRedis(HomeDeviceInfoDto homeDeviceInfoDto) {
        // Redis?????????????????????Redis??????
        Map<String, HomeDeviceInfoDto> deviceMap;
        String areaId = homeDeviceInfoDto.getAreaId();
        //???????????????????????????
        if (RedisUtils.hHasKey(DeviceConstant.DEVICE_GIS_MAP, areaId)) {
            deviceMap = (Map) RedisUtils.hGet(DeviceConstant.DEVICE_GIS_MAP, areaId);
        } else {
            deviceMap = new HashMap<>(128);
        }
        deviceMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
        //??????
        RedisUtils.hSet(DeviceConstant.DEVICE_GIS_MAP, areaId, deviceMap);
    }

    /**
     * ??????????????????
     *
     * @param deviceInfoOld     ???????????????????????????
     * @param homeDeviceInfoDto ?????????????????????
     */
    private void updateDeviceRedis(DeviceInfo deviceInfoOld, HomeDeviceInfoDto homeDeviceInfoDto) {
        //??????????????????????????????
        if (!deviceInfoOld.getAreaId().equals(homeDeviceInfoDto.getAreaId())) {
            //???????????????????????????????????????????????????
            String areaId = deviceInfoOld.getAreaId();
            //???????????????????????????
            if (RedisUtils.hHasKey(DeviceConstant.DEVICE_GIS_MAP, areaId)) {
                Map<String, HomeDeviceInfoDto> deviceMap = (Map) RedisUtils.hGet(DeviceConstant.DEVICE_GIS_MAP, areaId);
                deviceMap.remove(deviceInfoOld.getDeviceId());
                RedisUtils.hSet(DeviceConstant.DEVICE_GIS_MAP, areaId, deviceMap);
            }
        }
        //???????????????????????????
        addDeviceRedis(homeDeviceInfoDto);
    }

    /**
     * ????????????
     *
     * @param deviceInfoList ??????????????????List
     */
    private void deleteDeviceRedis(List<DeviceInfo> deviceInfoList) {
        //????????????????????????
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        //????????????????????????
        for (DeviceInfo deviceInfo : deviceInfoList) {
            removeDeviceRedis(deviceInfo.getAreaId(), deviceInfo.getDeviceId(), deviceInfoRedis);
        }
        // ??????Redis
        RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, (Map) deviceInfoRedis);
    }

    /**
     * ??????????????????
     *
     * @param areaId          ??????ID
     * @param deviceId        ??????ID
     * @param deviceInfoRedis ????????????
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
     * ????????????????????????Redis??????
     *
     * @param deviceInfoDtoList ??????????????????
     * @return Redis????????????
     */
    private Map<String, Map<String, HomeDeviceInfoDto>> setDeviceRedis(List<HomeDeviceInfoDto> deviceInfoDtoList) {
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = new HashMap<>(128);
        //????????????
        for (HomeDeviceInfoDto homeDeviceInfoDto : deviceInfoDtoList) {
            String areaId = homeDeviceInfoDto.getAreaId();
            //???????????????????????????
            Map<String, HomeDeviceInfoDto> deviceMap = getDeviceMap(deviceInfoRedis, areaId);
            deviceMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
            deviceInfoRedis.put(areaId, deviceMap);
        }
        // ??????Redis
        RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, (Map) deviceInfoRedis);
        return deviceInfoRedis;
    }


    /**
     * ????????????
     *
     * @param ids ??????ids
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public Result deleteDeviceByIds(String[] ids) {
        //????????????????????????
        for (String id : ids) {
            checkDeviceIsExist(id);
        }

        //????????????????????????????????????
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByIds(ids);

        /*
         * ?????????????????????????????????,???????????????????????????????????????,
         * ?????????????????????????????????????????????????????????,?????????,RFID,??????,??????????????????
         */
        deviceInfoDao.deleteDeviceByIds(ids);

        List<String> idList = new ArrayList<>(Arrays.asList(ids));
        //??????????????????
        deviceCollectingDao.deleteAttentionByDeviceIds(idList);

        //??????????????????
        DeleteProcBaseForDeviceReq deleteProcBaseForDeviceReq = new DeleteProcBaseForDeviceReq();
        deleteProcBaseForDeviceReq.setDeviceIdList(idList);
        Result result = procBaseFeign.deleteProcBaseForDeviceList(deleteProcBaseForDeviceReq);
        if (ObjectUtils.isEmpty(result) || !ResultCode.SUCCESS.equals(result.getCode())) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.EXCEPTION_WHILE_DELETING_PROC_BASE));
        }
        //???????????????????????????
        DeleteInspectionTaskForDeviceReq deleteInspectionTaskForDeviceReq = new DeleteInspectionTaskForDeviceReq();
        deleteInspectionTaskForDeviceReq.setDeviceIdList(idList);
        result = inspectionTaskFeign.deleteInspectionTaskForDeviceList(deleteInspectionTaskForDeviceReq);
        if (ObjectUtils.isEmpty(result) || !ResultCode.SUCCESS.equals(result.getCode())) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.EXCEPTION_WHILE_DELETING_INSPECTION_TASK));
        }
        //??????????????????
        result = alarmCurrentFeign.batchDeleteAlarmsFeign(idList);
        if (ObjectUtils.isEmpty(result)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.EXCEPTION_WHILE_DELETING_ALARM));
        }
        //??????????????????
        result = devicePicFeign.deleteImageByDeviceIds(new HashSet<>(Arrays.asList(ids)));
        if (ObjectUtils.isEmpty(result)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.EXCEPTION_WHILE_DELETING_DEVICE_PIC));
        }
        //??????????????????
        result = controlFeign.deleteControlByDeviceIds(idList);
        if (ObjectUtils.isEmpty(result)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.EXCEPTION_WHILE_DELETING_CONTROL));
        }

        //????????????
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
        //????????????
        deleteDeviceRedis(deviceInfoList);

        //????????????????????????License????????????
        boolean permission;
        try {
            permission = licenseFeign.updateRedisLicenseThreshold(new LicenseFeignBean(ids.length, OperationTarget.DEVICE, OperationWay.DELETE));
        } catch (Exception e) {
            permission = false;
        }
        if (!permission) {
            return ResultUtils.warn(DeviceResultCode.FAIL, I18nUtils.getString(DeviceI18n.DELETE_DEVICE_FAIL));
        }

        //WebSocket??????????????????
        deviceInfoAsync.sendDeleteDevicesMsg(deviceInfoList);
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.DELETE_DEVICE_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param deviceId ???????????????
     * @return
     */
    @Override
    public boolean deviceCanChangeDetail(String deviceId) {
        List list = new ArrayList(Arrays.asList(deviceId));
        return hasAlarmOrOrder(list);
    }

    /**
     * ?????????????????????Gis map ??????
     *
     * @param areaId ??????ID
     */
    @Override
    public void refreshDeviceAreaRedis(String areaId) {
        //???????????????????????????ID
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = deviceInfoDao.queryDeviceAreaByAreaId(areaId);
        if (RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP)) {
            Map<String, HomeDeviceInfoDto> deviceMap;
            //???????????????????????????
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
     * ??????????????????
     *
     * @param exportDto ??????????????????
     * @return ??????????????????
     */
    @Override
    public Result exportDeviceList(ExportDto exportDto) {
        Export export;
        try {
            export = deviceExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getString(DeviceI18n.DEVICE_INFO_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.EXPORT_NO_DATA, I18nUtils.getString(DeviceI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            fe.printStackTrace();
            String string = I18nUtils.getString(DeviceI18n.EXPORT_DATA_TOO_LARGE);
            String dataCount = fe.getMessage();
            Object[] params = {dataCount, maxExportDataSize};
            String msg = MessageFormat.format(string, params);
            return ResultUtils.warn(DeviceResultCode.EXPORT_DATA_TOO_LARGE, msg);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getString(DeviceI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getString(DeviceI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto);
        deviceExport.exportData(export);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ????????????????????????
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //??????????????????id
        addLogBean.setOptObjId("export");
        //???????????????
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setDataOptType("export");
        addLogBean.setFunctionCode(LogFunctionCodeConstant.EXPORT_DEVICE_FUNCTION_CODE);
        //??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ????????????????????????
     *
     * @param deviceReqPda ????????????
     * @return ????????????
     */
    @Override
    public Result queryNearbyDeviceListForPda(DeviceReqPda deviceReqPda) {
        //????????????
        if (deviceReqPda == null || deviceReqPda.getPageCondition() == null) {
            throw new FilinkAreaException(I18nUtils.getString(DeviceI18n.PARAM_NULL));
        }

        User user = getCurrentUser();
        //????????????
        boolean hasCount = addAuth(deviceReqPda, user);

        Integer begin = (deviceReqPda.getPageCondition().getPageNum() - 1) * deviceReqPda.getPageCondition().getPageSize();
        deviceReqPda.getPageCondition().setBeginNum(begin);

        // ??????????????????
        PageCondition pageCondition = deviceReqPda.getPageCondition();
        Page page = new Page(pageCondition.getPageNum(), pageCondition.getPageSize());
        page.setCurrent(pageCondition.getPageNum());

        //???????????????????????????
        if (!hasCount) {
            PageBean pageBean = myBatiesBuildPageBean(page, 0, new ArrayList<DeviceInfoForPda>());
            return ResultUtils.success(pageBean);
        }

        Integer count = deviceInfoDao.queryNearbyDeviceCount(deviceReqPda, user.getId());
        //????????????
        List<DeviceInfoForPda> devicePdaList = deviceInfoDao.queryNearbyDeviceList(deviceReqPda, user.getId());

        //?????????????????????????????????
        updateContainsLocks(devicePdaList);

        //????????????????????????
        updateDevicePic(devicePdaList);

        PageBean pageBean = myBatiesBuildPageBean(page, count, devicePdaList);
        return ResultUtils.success(pageBean);
    }

    /**
     * ????????????????????????
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
        //?????????????????????3???????????????
        devicePicReq.setResource(DeviceConstant.DEVICE_PIC_RESOURCE_REALISTIC);
        //??????????????????
        devicePicReq.setPicNum("1");
        List<DevicePicDto> devicePicDtoList = deviceInfoDao.queryPicInfoByDeviceIds(devicePicReq);


        //????????????????????????
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

        //????????????????????????
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
     * ?????????????????????????????????
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
        //??????????????????
        List<String> areaIds = getUserAreaIds(user);
        //????????????????????????
        List<String> deviceTypes = getDeviceTypes(user);

        //??????????????????
        if (ObjectUtils.isEmpty(deviceReqPda.getAreaId())) {
            deviceReqPda.setAreaId(areaIds);
        } else {
            deviceReqPda.getAreaId().retainAll(areaIds);
        }
        //????????????????????????
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
     * ????????????????????????????????????????????????
     *
     * @param deviceInfoDto ??????????????????
     * @throws Exception
     */
    @Override
    public Result updateDeviceStatus(DeviceInfoDto deviceInfoDto) throws Exception {
        // ????????????deviceId????????????
        if (deviceInfoDto == null || StringUtils.isEmpty(deviceInfoDto.getDeviceId())) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
        }

        // ????????????????????????
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(deviceInfoDto.getDeviceId());
        if (ObjectUtils.isEmpty(deviceInfo)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
        }

        //??????????????????
        if (!StringUtils.isEmpty(deviceInfoDto.getDeviceStatus())) {
            deviceInfo.setDeviceStatus(deviceInfoDto.getDeviceStatus());
        }
        //??????????????????
        if (!StringUtils.isEmpty(deviceInfoDto.getDeployStatus())) {
            deviceInfo.setDeployStatus(deviceInfoDto.getDeployStatus());
        }
        return updateDevice(deviceInfo);
    }

    /**
     * ?????????????????????????????????
     *
     * @param updateDeviceStatusPda ??????????????????
     * @return
     * @throws Exception
     */
    @Override
    public Result updateDeviceListStatus(UpdateDeviceStatusPda updateDeviceStatusPda) {
        if (updateDeviceStatusPda == null || ObjectUtils.isEmpty(updateDeviceStatusPda.getDeviceIdList())
                || StringUtils.isEmpty(updateDeviceStatusPda.getDeployStatus())) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR, I18nUtils.getString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        List<String> deviceIdList = updateDeviceStatusPda.getDeviceIdList();
        String[] deviceIds = new String[deviceIdList.size()];
        deviceIdList.toArray(deviceIds);
        //????????????????????????
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByIds(deviceIds);
        if (deviceInfoList.size() < deviceIds.length) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_ID_LOSE, I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
        }
        //??????????????????
        deviceInfoDao.updateDeviceListStatus(updateDeviceStatusPda);

        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.UPDATE_DEVICE_SUCCESS));
    }

    /**
     * ?????????????????????
     *
     * @param deviceId ??????id
     * @return???????????????
     */
    @Override
    public Map<String, String> getDefaultParamsByDeviceType(String deviceId) {
        DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(deviceId);
        return deviceConfigService.getDefaultParams(deviceInfo.getDeviceType());
    }

    /**
     * ????????????id???????????????????????????
     *
     * @param deviceParam
     * @return
     */
    @Override
    public Result queryDeviceDtoForPageSelection(DeviceParam deviceParam) {
        //????????????ID????????????
        if (deviceParam == null || ObjectUtils.isEmpty(deviceParam.getAreaIds())) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR, I18nUtils.getString(DeviceI18n.DEVICE_PARAM_ERROR));
        }

        List<FilterCondition> filterConditionList = new ArrayList<>();
        //????????????????????????
        filterConditionList.add(DeviceInfoService.generateDeleteFilterCondition());
        //????????????ID????????????
        filterConditionList.add(DeviceInfoService.generateFilterCondition("areaId", "in", deviceParam.getAreaIds()));

        //???????????????????????????
        if (!ObjectUtils.isEmpty(deviceParam.getDeviceTypes())) {
            filterConditionList.add(DeviceInfoService.generateFilterCondition("deviceType", "in", deviceParam.getDeviceTypes()));
        }

        QueryCondition<DeviceInfo> queryCondition = new QueryCondition<>();
        queryCondition.setFilterConditions(filterConditionList);

        // ??????????????????????????????
        EntityWrapper<DeviceInfo> wrapper = myBatiesBuildQuery(queryCondition);

        // ????????????
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectList(wrapper);
        //??????
        List<DeviceInfoDto> deviceInfoDtoList = convertDeviceInfoListToDtoList(deviceInfoList, RequestInfoUtils.getUserId());

        return ResultUtils.success(deviceInfoDtoList);
    }

    /**
     * ????????????ID???????????????????????????
     *
     * @param deviceInfoRedis ????????????
     * @param areaId          ??????ID
     * @return ?????????????????????
     */
    private Map<String, HomeDeviceInfoDto> getDeviceMap
    (Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis, String areaId) {
        Map<String, HomeDeviceInfoDto> deviceMap;
        //???????????????????????????
        if (deviceInfoRedis.containsKey(areaId)) {
            deviceMap = deviceInfoRedis.get(areaId);
        } else {
            deviceMap = new HashMap<>(128);
        }
        return deviceMap;
    }

    /**
     * ??????????????????
     *
     * @param deviceIds
     * @return
     */
    public boolean hasAlarmOrOrder(List<String> deviceIds) {
        List<String> list = alarmCurrentFeign.queryAlarmSourceForFeign(deviceIds);
        if (list == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_ALARM_INFORMATION));
        }

        Result result = procBaseFeign.queryProcExitsForDeviceIds(deviceIds);
        if (result == null || result.getCode() != ResultCode.SUCCESS) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_WORK_ORDER_INFORMATION));
        }
        List procBaseList = (List) result.getData();
        if (ObjectUtils.isEmpty(list) && ObjectUtils.isEmpty(procBaseList)) {
            return true;
        }
        return false;
    }

    private void updateRedisBatch(List<HomeDeviceInfoDto> homeDeviceInfoDtoList, String areaId, AreaInfo areaInfo) {
        //????????????????????????
        Map<String, Map<String, HomeDeviceInfoDto>> deviceInfoRedis = queryDeviceAreaAll();
        //????????????????????????
        for (HomeDeviceInfoDto homeDeviceInfoDto : homeDeviceInfoDtoList) {
            removeDeviceRedis(homeDeviceInfoDto.getAreaId(), homeDeviceInfoDto.getDeviceId(), deviceInfoRedis);
            // Redis?????????????????????Redis??????,???????????????????????????
            Map<String, HomeDeviceInfoDto> deviceMap = getDeviceMap(deviceInfoRedis, areaId);
            homeDeviceInfoDto.setAreaId(areaId);
            homeDeviceInfoDto.setAreaName(areaInfo.getAreaName());
            deviceMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
            deviceInfoRedis.put(areaId, deviceMap);
        }
        // ??????Redis
        RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, (Map) deviceInfoRedis);
    }

    /**
     * ??????????????????????????????
     *
     * @param deviceIds
     * @return
     */
    private List<String> queryAlarmAndWorkOrderSource(List<String> deviceIds) {
        Result<List<String>> result = procBaseFeign.queryProcExitsForDeviceIds(deviceIds);
        if (result == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_WORK_ORDER_INFORMATION));
        }
        List<String> proDeviceIds = (List<String>) result.getData();
        if (proDeviceIds != null && proDeviceIds.size() > 0) {
            return proDeviceIds;
        }
        List<String> list = alarmCurrentFeign.queryAlarmSourceForFeign(deviceIds);
        if (list == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_ALARM_INFORMATION));
        }
        return list;
    }

}
