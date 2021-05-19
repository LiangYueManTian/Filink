package com.fiberhome.filink.fdevice.controller.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;
import com.fiberhome.filink.fdevice.service.statistics.PortStatisticsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * 测试PortStatisticsController
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/3
 */
@RunWith(MockitoJUnitRunner.class)
public class PortStatisticsControllerTest {

    /**
     * 被测试类
     */
    @InjectMocks
    private PortStatisticsController portStatisticsController;
    /**
     * Mock FiberOpticsAndCoreStatisticsDao
     */
    @Mock
    private PortStatisticsService portStatisticsService;

    @Test
    public void addPortStatistics() {
        when(portStatisticsService.addPortStatistics(any())).thenReturn(1);
        List<DevicePortUtilizationRate> listDevicePortUtilizationRate = new ArrayList<>();
        Integer result = portStatisticsController.addPortStatistics(listDevicePortUtilizationRate);
        Assert.assertTrue(result==1);
    }

    @Test
    public void queryPortStatistics() {
        String deviceId = "zz";
        Result result = new Result();
        when(portStatisticsService.queryPortStatistics(deviceId)).thenReturn(result);
        Assert.assertEquals(result, portStatisticsController.queryPortStatistics(deviceId));
    }

    @Test
    public void queryPortTopNo() {
        Result result = new Result();
        TopNoPortStatisticsReq topNoPortStatisticsReq = new TopNoPortStatisticsReq();
        when(portStatisticsService.queryPortTopNo(topNoPortStatisticsReq)).thenReturn(result);
        Assert.assertEquals(result, portStatisticsController.queryPortTopNo(topNoPortStatisticsReq));
    }
}