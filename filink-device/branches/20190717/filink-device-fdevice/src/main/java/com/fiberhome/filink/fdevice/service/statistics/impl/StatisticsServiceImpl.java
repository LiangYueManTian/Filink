package com.fiberhome.filink.fdevice.service.statistics.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.fdevice.bean.Sensor.SensorLimit;
import com.fiberhome.filink.fdevice.bean.devicelog.UnlockingStatistics;
import com.fiberhome.filink.fdevice.constant.device.*;
import com.fiberhome.filink.fdevice.dao.statistics.SensorLimitDao;
import com.fiberhome.filink.fdevice.dao.statistics.StatisticsDao;
import com.fiberhome.filink.fdevice.dao.statistics.UnlockingStatisticsDao;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.export.DeployStatusCountExport;
import com.fiberhome.filink.fdevice.export.DeviceCountExport;
import com.fiberhome.filink.fdevice.export.DeviceInfoTopNumExport;
import com.fiberhome.filink.fdevice.export.DeviceStatusCountExport;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.fdevice.service.statistics.StatisticsService;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/23 19:33
 * @Description: com.fiberhome.filink.fdevice.service.statistics.impl
 * @version: 1.0
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    private SensorLimitDao sensorLimitDao;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UnlockingStatisticsDao unlockingStatisticsDao;

    @Autowired
    private DeviceCountExport deviceCountExport;

    @Autowired
    private DeviceStatusCountExport deviceStatusCountExport;

    @Autowired
    private DeployStatusCountExport deployStatusCountExport;

    @Autowired
    private DeviceInfoTopNumExport deviceInfoTopNumExport;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private DeviceLogService deviceLogService;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 服务名
     */
    private static String SERVER_NAME = "filink-device-server";

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * 根据区域ID和设施类型统计设施数量
     * @param deviceParam
     * @return
     */
    @Override
    public Result queryDeviceCount(DeviceParam deviceParam) {
        //校验参数
        if (deviceParam == null || deviceParam.getAreaIds() == null || deviceParam.getDeviceTypes() == null) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }

        //参数查询集合为空
        if(deviceParam.getAreaIds().size() == 0 || deviceParam.getDeviceTypes().size() == 0) {
            return ResultUtils.success(new ArrayList<>());
        }
        List<DeviceNumDto> deviceNumDtoList = statisticsDao.queryDeviceCount(deviceParam);

        //List转为Map
        Map<String, Integer> deviceCountMap = new HashMap<>(32);
        for(DeviceNumDto deviceNumDto : deviceNumDtoList) {
            deviceCountMap.put(deviceNumDto.getAreaId()+deviceNumDto.getDeviceType(), deviceNumDto.getDeviceNum());
        }

        //返回List
        List<Map> returnMapList = new ArrayList<>();
        for(String areaId : deviceParam.getAreaIds()) {
            Map<String, Object> areaIdMap = new LinkedHashMap();
            areaIdMap.put("areaId", areaId);
            for(String deviceType : deviceParam.getDeviceTypes()) {
                Object deviceNum = deviceCountMap.get(areaId + deviceType);
                if(ObjectUtils.isEmpty(deviceNum)) {
                    areaIdMap.put(deviceType, 0);
                } else {
                    areaIdMap.put(deviceType, deviceNum);
                }
            }
            returnMapList.add(areaIdMap);
        }
        return ResultUtils.success(returnMapList);
    }

    /**
     * 根据区域ID和设施类型统计设施状态数量
     * @param deviceParam
     * @return
     */
    @Override
    public Result queryDeviceStatusCount(DeviceParam deviceParam) {
        //校验参数
        if (deviceParam == null || deviceParam.getAreaIds() == null || deviceParam.getDeviceType() == null) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }

        //参数查询集合为空
        if(deviceParam.getAreaIds().size() == 0 || ObjectUtils.isEmpty(deviceParam.getDeviceType())) {
            ResultUtils.success(new ArrayList<>());
        }
        List<DeviceNumDto> deviceNumDtoList = statisticsDao.queryDeviceStatusCount(deviceParam);

        //List转为Map
        Map<String, Integer> countMap = new HashMap<>(32);
        for(DeviceNumDto deviceNumDto : deviceNumDtoList) {
            countMap.put(deviceNumDto.getAreaId()+deviceNumDto.getDeviceStatus(), deviceNumDto.getDeviceNum());
        }

        //返回List
        List<Map> returnMapList = new ArrayList<>();
        for(String areaId : deviceParam.getAreaIds()) {
            Map<String, Object> areaIdMap = new LinkedHashMap();
            areaIdMap.put("areaId", areaId);
            for(DeviceStatus deviceStatus : DeviceStatus.values()) {
                Object deviceNum = countMap.get(areaId + deviceStatus.getCode());
                if(ObjectUtils.isEmpty(deviceNum)) {
                    areaIdMap.put(deviceStatus.getCode(), 0);
                } else {
                    areaIdMap.put(deviceStatus.getCode(), deviceNum);
                }
            }
            returnMapList.add(areaIdMap);
        }
        return ResultUtils.success(returnMapList);
    }

    /**
     * 根据区域ID和设施类型统计设施部署状态数量
     * @param deviceParam
     * @return
     */
    @Override
    public Result queryDeployStatusCount(DeviceParam deviceParam) {
        //校验参数
        if (deviceParam == null || deviceParam.getAreaIds() == null || deviceParam.getDeviceType() == null) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }

        //参数查询集合为空
        if(deviceParam.getAreaIds().size() == 0 || ObjectUtils.isEmpty(deviceParam.getDeviceType())) {
            ResultUtils.success(new ArrayList<>());
        }
        List<DeviceNumDto> deviceNumDtoList = statisticsDao.queryDeployStatusCount(deviceParam);

        //List转为Map
        Map<String, Integer> countMap = new HashMap<>(32);
        for(DeviceNumDto deviceNumDto : deviceNumDtoList) {
            countMap.put(deviceNumDto.getAreaId()+deviceNumDto.getDeployStatus(), deviceNumDto.getDeviceNum());
        }

        //返回List
        List<Map> returnMapList = new ArrayList<>();
        for(String areaId : deviceParam.getAreaIds()) {
            Map<String, Object> areaIdMap = new LinkedHashMap();
            areaIdMap.put("areaId", areaId);
            for(DeployStatus deployStatus : DeployStatus.values()) {
                Object deviceNum = countMap.get(areaId + deployStatus.getCode());
                deviceNum = deviceNum==null ? 0 : deviceNum;
                areaIdMap.put(deployStatus.getCode(), deviceNum);
            }
            returnMapList.add(areaIdMap);
        }
        return ResultUtils.success(returnMapList);
    }

    /**
     * 查询当前用户各类设施数量
     * @return
     */
    @Override
    public Result queryDeviceTypeCount() {
        //查询当前用户
        User user = getCurrentUser();
        //用户管理区域
        List<String> areaIds = getUserAreaIds(user);
        //用户管理设施类型
        Set<String> deviceTypes = getDeviceTypes(user);

        if (DeviceConstant.ADMIN.equals(user.getId())) {
            areaIds = null;
        } else if (areaIds == null || areaIds.size() == 0) {
            return ResultUtils.success(new ArrayList<>(0));
        }

        List<DeviceNumDto> deviceNumDtoList = statisticsDao.queryDeviceTypeCount(areaIds);
        if (DeviceConstant.ADMIN.equals(user.getId())) {
            return ResultUtils.success(deviceNumDtoList);
        }

        //只返回用户包含权限的设施类型
        List<DeviceNumDto> dtoList = new ArrayList<>();
        for(DeviceNumDto deviceNumDto : deviceNumDtoList) {
            if(deviceTypes.contains(deviceNumDto.getDeviceType())) {
                dtoList.add(deviceNumDto);
            }
        }
        return ResultUtils.success(dtoList);
    }

    /**
     * 查询当前用户信息
     *
     * @return
     */
    private User getCurrentUser() {
        //查询当前用户权限信息
        Object userObj = userFeign.queryCurrentUser(RequestInfoUtils.getUserId(), RequestInfoUtils.getToken());
        //转换为User
        return DeviceInfoService.convertObjectToUser(userObj);
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
    private static Set<String> getDeviceTypes(User user) {
        Set<String> deviceTypes = new HashSet<>();
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }
        return deviceTypes;
    }

    /**
     * 查询当前用户设施状态数量
     * @param deviceParam
     * @return
     */
    @Override
    public Result queryUserDeviceStatusCount(DeviceParam deviceParam) {
        deviceParam = mergeUserAuth(deviceParam);
        if(deviceParam == null) {
            return ResultUtils.success(new ArrayList());
        }
        List<DeviceNumDto> deviceNumDtoList = statisticsDao.queryUserDeviceStatusCount(deviceParam);
        return ResultUtils.success(deviceNumDtoList);
    }

    /**
     * 查询当前用户设施数量和设施状态数量
     * @return
     */
    @Override
    public Result queryUserDeviceAndStatusCount() {
        //查询用户权限
        DeviceParam deviceParam = mergeUserAuth(null);
        if(deviceParam == null) {
            return ResultUtils.success(new ArrayList<>());
        }

        List<DeviceNumDto> deviceNumDtoList = statisticsDao.queryUserDeviceAndStatusCount(deviceParam);
        return ResultUtils.success(deviceNumDtoList);
    }

    /**
     * 查询用户权限下的区域和设施类型
     * @param deviceParam
     * @return
     */
    private DeviceParam mergeUserAuth(DeviceParam deviceParam) {
        //查询当前用户
        User user = getCurrentUser();

        if(deviceParam == null) {
            deviceParam = new DeviceParam();
        }
        if(deviceParam.getDeviceTypes() == null) {
            deviceParam.setDeviceTypes(new ArrayList<>());
        }
        if(deviceParam.getAreaIds() == null) {
            deviceParam.setAreaIds(new ArrayList<>());
        }
        if (!DeviceConstant.ADMIN.equals(user.getId())) {
            //用户管理区域
            List<String> areaIds = getUserAreaIds(user);
            //用户管理设施类型
            List<String> deviceTypes = new ArrayList<>(getDeviceTypes(user));

            if(ObjectUtils.isEmpty(areaIds) || ObjectUtils.isEmpty(deviceTypes)) {
                return null;
            }
            if(ObjectUtils.isEmpty(deviceParam.getAreaIds())) {
                deviceParam.setAreaIds(areaIds);
            } else {
                deviceParam.getAreaIds().retainAll(areaIds);
            }

            if(ObjectUtils.isEmpty(deviceParam.getDeviceTypes())) {
                deviceParam.setDeviceTypes(deviceTypes);
            } else {
                deviceParam.getDeviceTypes().retainAll(deviceTypes);
            }
        }
        return deviceParam;
    }

    /**
     * 查询单个设施一段时间内的开锁次数
     * @param unlockingReq
     * @return
     */
    @Override
    public Result queryUnlockingTimesByDeviceId(UnlockingReq unlockingReq) {
        //校验参数
        if(StringUtils.isEmpty(unlockingReq.getDeviceId()) || StringUtils.isEmpty(unlockingReq.getStartDate())
                || StringUtils.isEmpty(unlockingReq.getEndDate())) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        List<UnlockingStatistics> unlockingStatisticsList = unlockingStatisticsDao.queryUnlockingTimesByDeviceId(unlockingReq);

        //如果查询日期包含今天，需要单独查询该设施当天的开锁次数
        LocalDate nowDate = LocalDateTime.now().toLocalDate();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = nowDate.format(df);
        if(unlockingReq.getStartDate().compareTo(todayString)<=0 && unlockingReq.getEndDate().compareTo(todayString)>=0) {
            //查询今天的开锁次数
            Long startTime = Timestamp.valueOf(LocalDateTime.of(nowDate, LocalTime.MIN)).getTime();
            Long endTime = Timestamp.valueOf(LocalDateTime.of(nowDate, LocalTime.MAX)).getTime();
            Integer unlockingCount = deviceLogService.queryUnlockingCountByDeviceId(unlockingReq.getDeviceId(), startTime, endTime);
            if(unlockingCount != null && unlockingCount>0) {
                UnlockingStatistics us = new UnlockingStatistics();
                us.setStatisticsDate(todayString);
                us.setDeviceId(unlockingReq.getDeviceId());
                us.setUnlockingCount(unlockingCount);
                unlockingStatisticsList.add(us);
            }
        }
        return ResultUtils.success(unlockingStatisticsList);
    }

    /**
     * 创建导出任务
     * @param abstractExport
     * @param exportDto
     * @param listName
     * @param functionCode
     * @return
     */
    private Result createExportTask(AbstractExport abstractExport, ExportDto exportDto, String listName, String functionCode) {
        systemLanguageUtil.querySystemLanguage();
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = abstractExport.insertTask(exportDto, SERVER_NAME, listName);
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(DeviceI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            fe.printStackTrace();
            String dataCount = fe.getMessage();
            Object[] params = {dataCount, maxExportDataSize};
            String msg = MessageFormat.format(I18nUtils.getSystemString(DeviceI18n.EXPORT_DATA_TOO_LARGE), params);
            return ResultUtils.warn(DeviceResultCode.EXPORT_DATA_TOO_LARGE, msg);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(DeviceI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(DeviceResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(DeviceI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto, functionCode);
        abstractExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 列表导出记录日志
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto, String functionCode) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setDataOptType("export");
        addLogBean.setFunctionCode(functionCode);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 导出设施统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportDeviceCount(ExportDto exportDto) {
        return createExportTask(deviceCountExport, exportDto,
                I18nUtils.getSystemString(StatisticsI18n.DEVICE_COUNT_LIST_NAME),
                LogFunctionCodeConstant.EXPORT_DEVICE_COUNT_FUNCTION_CODE);
    }

    /**
     * 导出设施状态统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportDeviceStatusCount(ExportDto exportDto) {
        return createExportTask(deviceStatusCountExport, exportDto,
                I18nUtils.getSystemString(StatisticsI18n.DEVICE_STATUS_COUNT_LIST_NAME),
                LogFunctionCodeConstant.EXPORT_DEVICE_STATUS_COUNT_FUNCTION_CODE);
    }

    /**
     * 导出部署状态统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportDeployStatusCount(ExportDto exportDto) {
        return createExportTask(deployStatusCountExport, exportDto,
                I18nUtils.getSystemString(StatisticsI18n.DEPLOY_STATUS_COUNT_LIST_NAME),
                LogFunctionCodeConstant.EXPORT_DEPLOY_STATUS_COUNT_FUNCTION_CODE);
    }

    /**
     * 导出指定区域，设施类型开锁次数最多的几条设施
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportUnlockingTopNum(ExportDto exportDto) {
        return createExportTask(deviceInfoTopNumExport, exportDto,
                I18nUtils.getSystemString(StatisticsI18n.UNLOCKING_TOP_NUM_LIST_NAME),
                LogFunctionCodeConstant.EXPORT_UNLOCKING_TOP_NUM_FUNCTION_CODE);
    }

    /**
     * 导出指定区域，设施类型的前几条传感器数据
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportDeviceSensorTopNum(ExportDto exportDto) {
        return createExportTask(deviceInfoTopNumExport, exportDto,
                I18nUtils.getSystemString(StatisticsI18n.DEVICE_SENSOR_TOP_NUM_LIST_NAME),
                LogFunctionCodeConstant.EXPORT_DEVICE_SENSOR_TOP_NUM_FUNCTION_CODE);
    }

    /**
     * 查询指定区域，设施类型开锁次数最多的几条设施
     * @param deviceLogTopNumReq
     * @return
     */
    @Override
    public Result queryUnlockingTopNum(DeviceLogTopNumReq deviceLogTopNumReq) {
        //校验必填参数
        if(deviceLogTopNumReq == null || deviceLogTopNumReq.getAreaIdList() == null
                || ObjectUtils.isEmpty(deviceLogTopNumReq.getDeviceType()) || deviceLogTopNumReq.getTopTotal() == null
                || deviceLogTopNumReq.getStartDate() == null || deviceLogTopNumReq.getEndDate() == null) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        //区域为空，topNum为0时返回空
        if(deviceLogTopNumReq.getAreaIdList().size() == 0 || deviceLogTopNumReq.getTopTotal() == 0) {
            return ResultUtils.success(new ArrayList<>());
        }

        List<DeviceLogTopNum> deviceLogTopNumList = unlockingStatisticsDao.queryUnlockingTopNum(deviceLogTopNumReq);
        return ResultUtils.success(deviceLogTopNumList);
    }

    /**
     * 查询当前用户权限内开锁次数最多的设施
     * @return
     */
    @Override
    public Result queryUserUnlockingTopNum() {
        //查询用户权限
        DeviceParam deviceParam = mergeUserAuth(null);
        if(deviceParam == null) {
            return ResultUtils.success(new ArrayList<>());
        }

        //查询开锁次数最多的前10条
        DeviceLogTopNumReq deviceLogTopNumReq = new DeviceLogTopNumReq();
        deviceLogTopNumReq.setAreaIdList(deviceParam.getAreaIds());
        deviceLogTopNumReq.setDeviceTypeList(deviceParam.getDeviceTypes());
        deviceLogTopNumReq.setTopTotal(10);
        List<DeviceLogTopNum> deviceLogTopNumList = unlockingStatisticsDao.queryUnlockingTopNum(deviceLogTopNumReq);
        return ResultUtils.success(deviceLogTopNumList);
    }


    /**
     * 查询指定区域，设施类型的前几条传感器数据
     * @param sensorTopNumReq
     * @return
     */
    @Override
    public Result queryDeviceSensorTopNum(SensorTopNumReq sensorTopNumReq) {
        //校验参数
        if(sensorTopNumReq == null || ObjectUtils.isEmpty(sensorTopNumReq.getSensorType())
                || sensorTopNumReq.getAreaIdList() == null || ObjectUtils.isEmpty(sensorTopNumReq.getDeviceType())
                || sensorTopNumReq.getTopTotal() == null) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        //区域为空，topNum为0时返回空
        if(sensorTopNumReq.getAreaIdList().size() == 0 || sensorTopNumReq.getTopTotal() == 0) {
            return ResultUtils.success(new HashMap<>(0));
        }

        //查询最大值
        sensorTopNumReq.setTop(true);
        List<SensorTopNum> topMapList = sensorLimitDao.queryDeviceSensorTopNum(sensorTopNumReq);
        //查询最小值
        sensorTopNumReq.setTop(false);
        List<SensorTopNum> bottomMapList = sensorLimitDao.queryDeviceSensorTopNum(sensorTopNumReq);

        Map<String, List<SensorTopNum>> topNumMap = new HashMap<>(2);
        topNumMap.put("top", topMapList);
        topNumMap.put("bottom", bottomMapList);
        return ResultUtils.success(topNumMap);
    }

    /**
     * 更新设施传感器极限值
     * @param sensorInfo
     * @return
     */
    @Override
    public Result updateSensorLimit(SensorInfo sensorInfo) {
        //校验参数
        if(sensorInfo == null || sensorInfo.getDeviceId() == null || sensorInfo.getCurrentTime() == null) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR, I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }

        //查询设施传感器当前记录
        SensorLimit sensorLimit = sensorLimitDao.selectById(sensorInfo.getDeviceId());
        Timestamp currentTimeStamp = new Timestamp(sensorInfo.getCurrentTime());
        if(sensorLimit == null || sensorLimit.getDeviceId() == null) {
            sensorLimit = new SensorLimit();
            sensorLimit.setDeviceId(sensorInfo.getDeviceId());
            sensorLimit.setTemperatureMax(sensorInfo.getTemperature());
            sensorLimit.setTemperatureMin(sensorInfo.getTemperature());
            sensorLimit.setHumidityMax(sensorInfo.getHumidity());
            sensorLimit.setHumidityMin(sensorInfo.getHumidity());
            sensorLimit.setTemperatureMaxTime(currentTimeStamp);
            sensorLimit.setTemperatureMinTime(currentTimeStamp);
            sensorLimit.setHumidityMaxTime(currentTimeStamp);
            sensorLimit.setHumidityMinTime(currentTimeStamp);
            sensorLimitDao.insert(sensorLimit);
        } else {
            boolean isChanged = false;
            if(!ObjectUtils.isEmpty(sensorInfo.getTemperature())) {
                if (sensorLimit.getTemperatureMax() == null || sensorLimit.getTemperatureMax() < sensorInfo.getTemperature()) {
                    sensorLimit.setTemperatureMax(sensorInfo.getTemperature());
                    sensorLimit.setTemperatureMaxTime(currentTimeStamp);
                    isChanged = true;
                }
                if (sensorLimit.getTemperatureMin() == null || sensorLimit.getTemperatureMin() > sensorInfo.getTemperature()) {
                    sensorLimit.setTemperatureMin(sensorInfo.getTemperature());
                    sensorLimit.setTemperatureMinTime(currentTimeStamp);
                    isChanged = true;
                }
            }
            if(!ObjectUtils.isEmpty(sensorInfo.getHumidity())) {
                if(sensorLimit.getHumidityMax() == null || sensorLimit.getHumidityMax() < sensorInfo.getHumidity()) {
                    sensorLimit.setHumidityMax(sensorInfo.getHumidity());
                    sensorLimit.setHumidityMaxTime(currentTimeStamp);
                    isChanged = true;
                }
                if(sensorLimit.getHumidityMin() == null || sensorLimit.getHumidityMin() > sensorInfo.getHumidity()) {
                    sensorLimit.setHumidityMin(sensorInfo.getHumidity());
                    sensorLimit.setHumidityMinTime(currentTimeStamp);
                    isChanged = true;
                }
            }
            if(isChanged) {
                sensorLimitDao.updateById(sensorLimit);
            }
        }
        return ResultUtils.success();
    }

}
