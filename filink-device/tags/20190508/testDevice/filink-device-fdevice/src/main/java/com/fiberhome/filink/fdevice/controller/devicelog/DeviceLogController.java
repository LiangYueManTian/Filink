package com.fiberhome.filink.fdevice.controller.devicelog;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
import com.fiberhome.filink.fdevice.constant.device.ConstantParam;
import com.fiberhome.filink.fdevice.constant.device.DeviceI18n;
import com.fiberhome.filink.fdevice.constant.device.DeviceLogResultCode;
import com.fiberhome.filink.fdevice.dto.DeviceLogReqtForPda;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设施日志控制器
 *
 * @author CongcaiYu@wistronits.com
 */

@RestController
@RequestMapping("/deviceLog")
public class DeviceLogController {

    @Autowired
    private DeviceLogService deviceLogService;


    /**
     * 分页查询设施日志
     *
     * @param queryCondition 查询条件
     * @return 分页数据
     */
    @PostMapping("/deviceLogListByPage")
    public Result deviceLogListByPage(@RequestBody QueryCondition queryCondition) {
        PageCondition pageCondition = queryCondition.getPageCondition();
        if (pageCondition == null || pageCondition.getPageNum() == null || pageCondition.getPageSize() == null) {
            //返回pageCondition对象不能为空
            return ResultUtils.warn(DeviceLogResultCode.PAGE_CONDITION_NULL, I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        PageBean pageBean = deviceLogService.deviceLogListByPage(queryCondition, null, true);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 分页查询设施日志
     *
     * @param deviceLogReqtForPda 查询条件
     * @return 分页数据
     */
    @PostMapping("/pda/deviceLogListByPage")
    public Result deviceLogListByPageForPda(@RequestBody DeviceLogReqtForPda deviceLogReqtForPda) {
        //根据pda传的参数构造queryCondition
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = deviceLogReqtForPda.getPageCondition();
        if (pageCondition == null || pageCondition.getPageNum() == null || pageCondition.getPageSize() == null) {
            //返回pageCondition对象不能为空
            Result result = ResultUtils.warn(DeviceLogResultCode.PAGE_CONDITION_NULL,
                    I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
            return result;
        }
        queryCondition.setPageCondition(pageCondition);
        //构造筛选条件
        List<FilterCondition> filterConditions = generateFilterConditionWithParams(deviceLogReqtForPda);
        queryCondition.setFilterConditions(filterConditions);
        //按时间倒序
        SortCondition condition = new SortCondition();
        condition.setSortField(ConstantParam.CURRENT_TIME);
        condition.setSortRule(ConstantParam.DESC);
        queryCondition.setSortCondition(condition);
        Map<String, Long> time = new HashMap(64);
        if (deviceLogReqtForPda.getStartTime() != null && deviceLogReqtForPda.getEndTime() != null) {
            time.put(ConstantParam.START_TIME, deviceLogReqtForPda.getStartTime());
            time.put(ConstantParam.END_TIME, deviceLogReqtForPda.getEndTime());
        } else {
            time = null;
        }
        queryCondition.setBizCondition(time);

        PageBean pageBean = deviceLogService.deviceLogListByPageForPda(queryCondition);
        return ResultUtils.success(pageBean);
    }

    /**
     * 新增设施日志
     *
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result saveDeviceLog(@RequestBody DeviceLog deviceLog) throws Exception {
        return deviceLogService.saveDeviceLog(deviceLog);
    }

    /**
     * 删除设施日志
     *
     * @return 操作结果
     */
    @PostMapping("/deleteDeviceLogByDeviceIds")
    public Result deleteDeviceLogByDeviceIds(@RequestBody List<String> deviceLogList) {
        return deviceLogService.deleteDeviceLogByDeviceIds(deviceLogList);
    }

    /**
     * 导出操作日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportDeviceLogList")
    public Result exportDeviceLogList(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(DeviceLogResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(DeviceI18n.EXPORT_PARAM_NULL));
        }
        return deviceLogService.exportDeviceLogList(exportDto);
    }

    /**
     * 查询最近一次操作日志时间
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/queryRecentDeviceLogTime/{deviceId}")
    public Result queryRecentDeviceLogTime(@PathVariable String deviceId) {
        return deviceLogService.queryRecentDeviceLogTime(deviceId);
    }

    /**
     * 根据查询参数生成查询筛选条件
     *
     * @param deviceLogReqtForPda
     * @return
     */
    private List<FilterCondition> generateFilterConditionWithParams(DeviceLogReqtForPda deviceLogReqtForPda) {

        List<FilterCondition> filterConditions = new ArrayList<>();
        List<String> logType = deviceLogReqtForPda.getLogType();
        if (!ObjectUtils.isEmpty(logType)) {
            filterConditions.add(setProperties(ConstantParam.LOG_TYPE, logType));
        }
        List<String> type = deviceLogReqtForPda.getType();
        if (!ObjectUtils.isEmpty(type)) {
            filterConditions.add(setProperties(ConstantParam.TYPE, type));
        }
        List<String> deviceType = deviceLogReqtForPda.getDeviceType();
        if (!ObjectUtils.isEmpty(deviceType)) {
            filterConditions.add(setProperties(ConstantParam.DEVICE_TYPE, deviceType));
        }
        List<String> areaId = deviceLogReqtForPda.getAreaId();
        if (!ObjectUtils.isEmpty(areaId)) {
            filterConditions.add(setProperties(ConstantParam.AREA_ID, areaId));
        }
        List<String> deviceId = deviceLogReqtForPda.getDeviceId();
        if (!ObjectUtils.isEmpty(deviceId)) {
            filterConditions.add(setProperties(ConstantParam.DEVICE_ID, deviceId));
        }


        return filterConditions;
    }

    /**
     * 生成对应的筛选条件
     *
     * @param field 键
     * @param value 值
     * @return FilterCondition
     */
    private FilterCondition setProperties(String field, Object value) {
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField(field);
        filterCondition.setOperator(ConstantParam.IN_STRING);
        filterCondition.setFilterValue(value);
        return filterCondition;
    }

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestAsync testAsync;

    @GetMapping("/testAdd")
    public Result testDeviceLog() throws Exception {
        for (int i = 0; i < 665; i++) {
            if (i % 50 == 0 && i != 0) {
                System.out.println("已经有50个线程在跑 休息一下");
                Thread.sleep(30000);
            }
            testAsync.addTest();
            System.out.println("启动" + (i + 1) + "个线程");
        }
        return ResultUtils.success();
    }
}