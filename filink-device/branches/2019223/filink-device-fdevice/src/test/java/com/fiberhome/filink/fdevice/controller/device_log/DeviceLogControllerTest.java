package com.fiberhome.filink.fdevice.controller.device_log;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.fdevice.bean.DeviceI18n;
import com.fiberhome.filink.fdevice.bean.device_log.DeviceLog;
import com.fiberhome.filink.fdevice.service.device_log.DeviceLogService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 设施日志控制层测试方法
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class DeviceLogControllerTest {

    private QueryCondition queryCondition;

    /**
     * 测试对象
     */
    @Tested
    private DeviceLogController deviceLogController;

    /**
     * 模拟deviceLogService
     */
    @Injectable
    private DeviceLogService deviceLogService;

    /**
     * 初始化参数信息
     */
    @Before
    public void setUp(){
        queryCondition = new QueryCondition();
    }

    /**
     * 设施日志分页查询测试方法
     */
    @Test
    public void deviceLogListByPage() {
        //pageCondition为空场景
        new Expectations(I18nUtils.class){
            {
                I18nUtils.getString(DeviceI18n.PARAMETER_ERROR);
                result = anyString;
            }
        };
        new Expectations(ResultUtils.class){
            {
                Result success = new Result();
                ResultUtils.warn(anyInt,anyString);
                result = success;
            }
        };
        deviceLogController.deviceLogListByPage(queryCondition);
        //pageCondition不为空场景
        new Expectations(){
            {
                PageBean pageBean = new PageBean();
                deviceLogService.deviceLogListByPage(queryCondition);
                result = pageBean;
            }
        };
        new Expectations(ResultUtils.class){
            {
                PageBean pageBean = new PageBean();
                ResultUtils.pageSuccess(pageBean);
            }
        };
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(20);
        queryCondition.setPageCondition(pageCondition);
        deviceLogController.deviceLogListByPage(queryCondition);
    }

    /**
     * 保存设施日志测试方法
     */
    @Test
    public void saveDeviceLog() throws Exception{
        DeviceLog deviceLog = new DeviceLog();
        new Expectations(){
            {
                deviceLogService.saveDeviceLog(deviceLog);
            }
        };
        deviceLogController.saveDeviceLog(deviceLog);
    }
}
