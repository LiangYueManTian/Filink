package com.fiberhome.filink.fdevice.export;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
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
 * @Date: 2019/5/28 17:54
 * @Description: com.fiberhome.filink.fdevice.export
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceExportTest {

    @InjectMocks
    private DeviceExport deviceExport;

    @Mock
    private DeviceInfoService deviceInfoService;

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
        when(userFeign.queryUserInfoById("1")).thenReturn(ResultUtils.success(JSON.toJSON(user)));
        List<DeviceInfoDto> dtoList = new ArrayList<>();
        when(deviceInfoService.listDevice(any(),any(),anyBoolean())).thenReturn(ResultUtils.success(dtoList));
        List list = deviceExport.queryData(new QueryCondition());
        Assert.assertEquals(list, dtoList);
    }

    @Test
    public void queryCount() {
        Integer integer1 = new Integer(1);
        QueryCondition queryCondition = new QueryCondition();
        when(deviceInfoService.queryDeviceCount(queryCondition, null)).thenReturn(integer1);
        Integer integer = deviceExport.queryCount(queryCondition);
        Assert.assertEquals(integer, integer1);
    }
}
