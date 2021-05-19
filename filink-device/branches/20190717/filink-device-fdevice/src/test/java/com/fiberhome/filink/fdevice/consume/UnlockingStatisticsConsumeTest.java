package com.fiberhome.filink.fdevice.consume;

import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import mockit.Expectations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * UnlockingStatisticsConsumeTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/9
 */
@RunWith(MockitoJUnitRunner.class)
public class UnlockingStatisticsConsumeTest {
    @InjectMocks
    private UnlockingStatisticsConsume unlockingStatisticsConsume;
    @Mock
    private DeviceLogService deviceLogService;
    @Mock
    private DeviceInfoService deviceInfoService;

    @Test
    public void unlockingStatisticsConsume() {
        new Expectations() {
            {
                deviceLogService.synchronizeUnlockingStatistics();
            }
        };
        unlockingStatisticsConsume.unlockingStatisticsConsume("2");
    }

    @Test
    public void deviceTimedTaskConsume() {
        new Expectations() {
            {
                deviceInfoService.retryDelete();
            }
        };
        unlockingStatisticsConsume.deviceTimedTaskConsume(0);
    }
}