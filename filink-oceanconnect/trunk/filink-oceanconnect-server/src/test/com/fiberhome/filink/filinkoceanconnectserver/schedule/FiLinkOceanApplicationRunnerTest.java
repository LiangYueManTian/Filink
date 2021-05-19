package com.fiberhome.filink.filinkoceanconnectserver.schedule;

import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.filinkoceanconnectserver.client.DeviceClient;
import com.fiberhome.filink.filinkoceanconnectserver.client.HttpsClient;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProfileUtil;
import com.fiberhome.filink.scheduleapi.api.CmdResendTaskFeign;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * FiLinkOceanApplicationRunner测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkOceanApplicationRunnerTest {

    @Tested
    private FiLinkOceanApplicationRunner oceanApplicationRunner;

    @Injectable
    private HttpsClient httpsClient;

    @Injectable
    private CmdResendTaskFeign cmdResendTaskFeign;

    @Injectable
    private ProfileUtil profileUtil;

    @Injectable
    private RetryConfig oceanRetryConfig;

    @Injectable
    private DeviceClient deviceClient;

    @Test
    public void run() {
        try {
            oceanApplicationRunner.run(null);
        } catch (Exception e) {
        }
    }
}