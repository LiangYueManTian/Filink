package com.fiberhome.filink.fdevice.controller.device;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.fdevice.bean.device.DeviceCollecting;
import com.fiberhome.filink.fdevice.dto.DeviceCollectingForPda;
import com.fiberhome.filink.fdevice.exception.FilinkAttentionRequestParamException;
import com.fiberhome.filink.fdevice.service.device.DeviceCollectingService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * DeviceCollectingController 测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/18
 */
@RunWith(JMockit.class)
public class DeviceCollectingControllerTest {
    /**
     * 测试对象
     */
    @Tested
    private DeviceCollectingController deviceCollectingController;
    /**
     * 注入逻辑层
     */
    @Injectable
    private DeviceCollectingService deviceCollectingService;

    @Mocked
    private FilterCondition filterCondition;
    @Mocked
    private PageCondition pageCondition;
    @Mocked
    private SortCondition sortCondition;

    @Mocked
    private RequestInfoUtils requestInfoUtils;

    /**
     * 注入分页条件
     */
    @Injectable
    private QueryCondition<DeviceCollecting> queryCondition;


    @Test
    public void queryDeviceCollectingCountByTypeTest() {
        deviceCollectingController.queryDeviceCollectingCountByType();
    }

    @Test
    public void attentionListByPageTest() {

        try {
            deviceCollectingController.attentionListByPage(null);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRequestParamException.class);
        }

        new Expectations() {
            {
                queryCondition.getFilterConditions();
                result = filterCondition;
                queryCondition.getPageCondition();
                result = pageCondition;
                queryCondition.getSortCondition();
                result = null;
            }
        };
        deviceCollectingController.attentionListByPage(queryCondition);
    }

    @Test
    public void addDeviceCollectingTest() {
        String deviceId = null;
        try {
            deviceCollectingController.addDeviceCollecting(deviceId);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRequestParamException.class);
        }
        deviceId = "00073215373411e9aaf5f48e38f46893";
        deviceCollectingController.addDeviceCollecting(deviceId);

    }

    @Test
    public void delDeviceCollectingTest() {
        String deviceId = null;
        try {
            deviceCollectingController.delDeviceCollecting(deviceId);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRequestParamException.class);
        }
        deviceId = "00073215373411e9aaf5f48e38f46893";
        deviceCollectingController.delDeviceCollecting(deviceId);
    }

    @Test
    public void delDeviceCollectingByIdTest() {
        DeviceCollecting deviceCollecting = null;
        try {
            deviceCollectingController.delDeviceCollectingById(deviceCollecting);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRequestParamException.class);
        }
        deviceCollecting = new DeviceCollecting();
        try {
            deviceCollectingController.delDeviceCollectingById(deviceCollecting);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRequestParamException.class);
        }
        deviceCollecting.setUserId("123");
        deviceCollecting.setDeviceId("31ed");
        try {
            deviceCollectingController.delDeviceCollectingById(deviceCollecting);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRequestParamException.class);
        }

        deviceCollecting.setUserId(null);
        deviceCollectingController.delDeviceCollectingById(deviceCollecting);

    }

    @Test
    public void attentionListTest() {
        deviceCollectingController.attentionList();
    }

    @Test
    public void optDeviceCollectingTest() {
        DeviceCollectingForPda deviceCollectingForPda = new DeviceCollectingForPda();
        try {
            deviceCollectingController.optDeviceCollecting(deviceCollectingForPda);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRequestParamException.class);
        }
        deviceCollectingForPda.setDeviceId("qads");
       //收藏
        deviceCollectingForPda.setOptState("0");
        deviceCollectingController.optDeviceCollecting(deviceCollectingForPda);
        //取消收藏
        deviceCollectingForPda.setOptState("1");
        deviceCollectingController.optDeviceCollecting(deviceCollectingForPda);

    }
}
