package com.fiberhome.filink.fdevice.export;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

/**
 * @Author: zhaoliang
 * @Date: 2019/7/2 16:04
 * @Description: com.fiberhome.filink.fdevice.export
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceLogExportTest {
    @InjectMocks
    private DeviceLogExport deviceLogExport;

    @Mock
    private DeviceLogService deviceLogService;

    @Mock
    private UserFeign userFeign;

    @Test
    public void queryData() {
        new Expectations(ExportApiUtils.class) {
            {
                ExportApiUtils.getCurrentUserId();
                result = "1";
            }
        };
        User user = new User();
        user.setId("1");
        QueryCondition queryCondition = new QueryCondition();
        when(userFeign.queryUserInfoById("1")).thenReturn(ResultUtils.success(JSON.toJSON(user)));
        PageBean pageBean = new PageBean();
        List<DeviceLog> deviceLogList = new ArrayList<>();
        pageBean.setData(deviceLogList);
        when(deviceLogService.deviceLogListByPage(any(), any(), anyBoolean())).thenReturn(pageBean);
        List list = deviceLogExport.queryData(queryCondition);
        Assert.assertEquals(list, deviceLogList);
    }

    @Test
    public void queryCount() {
        Integer integer1 = new Integer(1);
        QueryCondition queryCondition = new QueryCondition();
        when(deviceLogService.deviceLogCount(queryCondition, null)).thenReturn(integer1);
        Integer integer = deviceLogExport.queryCount(queryCondition);
        Assert.assertEquals(integer, integer1);
    }
}
