package com.fiberhome.filink.fdevice.service.statistics.impl;

import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;
import com.fiberhome.filink.fdevice.dao.statistics.PortStatisticsDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * PortStatisticsServiceImplTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/3
 */
@RunWith(MockitoJUnitRunner.class)
public class PortStatisticsServiceImplTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private PortStatisticsServiceImpl portStatisticsService;
    /**
     * Mock FiberOpticsAndCoreStatisticsDao
     */
    @Mock
    private PortStatisticsDao portStatisticsDao;
    @Test
    public void addPortStatistics() {
        List<DevicePortUtilizationRate> listDevicePortUtilizationRate = new ArrayList<>();
        DevicePortUtilizationRate devicePortUtilizationRate = new DevicePortUtilizationRate();
        devicePortUtilizationRate.setId("xx");
        listDevicePortUtilizationRate.add(devicePortUtilizationRate);
        when(portStatisticsDao.deletePortStatistics(listDevicePortUtilizationRate)).thenReturn(1);
        when(portStatisticsDao.addPortStatistics(any())).thenReturn(1);
        Assert.assertTrue(1==portStatisticsService.addPortStatistics(listDevicePortUtilizationRate));
    }

    @Test
    public void queryPortStatistics() {
        when(portStatisticsDao.queryPortStatisticsByDeviceId(anyString())).thenReturn(1d);
        Assert.assertEquals(0,portStatisticsService.queryPortStatistics("xx").getCode());
    }

    @Test
    public void queryPortTopNo() {
        TopNoPortStatisticsReq topNoPortStatisticsReq = new TopNoPortStatisticsReq();
        when(portStatisticsDao.queryPortStatistics(any())).thenReturn(new ArrayList<>());
        Assert.assertEquals(0,portStatisticsService.queryPortTopNo(topNoPortStatisticsReq).getCode());

    }
}