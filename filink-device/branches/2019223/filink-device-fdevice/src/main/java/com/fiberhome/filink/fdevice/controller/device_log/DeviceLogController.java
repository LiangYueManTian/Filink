package com.fiberhome.filink.fdevice.controller.device_log;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.fdevice.bean.DeviceI18n;
import com.fiberhome.filink.fdevice.bean.device_log.DeviceLog;
import com.fiberhome.filink.fdevice.service.device_log.DeviceLogService;
import com.fiberhome.filink.fdevice.utils.DeviceLogResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 设施日志控制器
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
    public Result deviceLogListByPage(@RequestBody QueryCondition queryCondition){
        PageCondition pageCondition = queryCondition.getPageCondition();
        if(pageCondition == null || pageCondition.getPageNum() == null || pageCondition.getPageSize() == null){
            //返回pageCondition对象不能为空
            Result result = ResultUtils.warn(DeviceLogResultCode.PAGE_CONDITION_NULL ,
                    I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
            return result;
        }
        PageBean pageBean = deviceLogService.deviceLogListByPage(queryCondition);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 新增设施日志
     *
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result saveDeviceLog(@RequestBody DeviceLog deviceLog) throws Exception{

        return deviceLogService.saveDeviceLog(deviceLog);
    }

}
