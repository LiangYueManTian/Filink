package com.fiberhome.filink.onenetserver.controller;

import com.fiberhome.filink.onenetserver.service.ReceiveService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class ReceiveControllerTest {
    /**测试对象 ReceiveController*/
    @Tested
    private ReceiveController receiveController;
    /**Mock ReceiveService*/
    @Injectable
    private ReceiveService receiveService;
    /**
     * receive
     */
    @Test
    public void receiveTest() {
        String result = receiveController.receive("");
        Assert.assertEquals(result, "stop");
        receiveController.receive("test");
    }
    /**
     * check
     */
    @Test
    public void checkTest() {
        receiveController.check(null, null, null);
    }
}
