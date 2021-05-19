package com.fiberhome.filink.stationserver.config;

import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.scheduleapi.api.CmdResendTaskFeign;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * StationApplicationRunner测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class StationApplicationRunnerTest {

    @Tested
    private StationApplicationRunner stationApplicationRunner;

    @Injectable
    private CmdResendTaskFeign cmdResendTaskFeign;

    @Injectable
    private RetryConfig stationRetryConfig;

    @Test
    public void run() {
        try {
            stationApplicationRunner.run(null);
        } catch (Exception e) {
        }
    }
}