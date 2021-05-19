package com.fiberhome.filink.onenetserver.service.impl;

import com.fiberhome.filink.onenetserver.exception.OneNetReceiveException;
import com.fiberhome.filink.onenetserver.stream.FiLinkKafkaSender;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(JMockit.class)
public class ReceiveServiceImplTest {
    /**测试对象 ReceiveServiceImpl*/
    @Tested
    private ReceiveServiceImpl receiveService;
    /**Mock FiLinkKafkaSender*/
    @Injectable
    private FiLinkKafkaSender kafkaSender;

    /**
     * receive
     */
    @Test
    public void receiveTest() {
        String body = "{\"msg\":{\"at\":1558486876054,\"type\":1,\"ds_id\":\"3348_0_5750\",\"value\":\"/+8BAeP45ZQ/aaFXAB5CR01QAAAAACIGAAEAAAAAAAwAAAAAAAAAAAAAAAA=\",\"dev_id\":527128657},\"msg_signature\":\"hedn/igeys1DZdY2rWlI8g==\",\"nonce\":\"4zLac5xY\"}";
        String bodyNull = "{\"msg_signature\":\"hedn/igeys1DZdY2rWlI8g==\",\"nonce\":\"4zLac5xY\"}";
        String bodyValueNull = "{\"msg\":{\"at\":1558486876054,\"type\":1,\"ds_id\":\"3348_0_5750\",\"dev_id\":527128657},\"msg_signature\":\"hedn/igeys1DZdY2rWlI8g==\",\"nonce\":\"4zLac5xY\"}";
        String result = receiveService.receive(bodyNull);
        Assert.assertEquals(result, "OK");
        result = receiveService.receive(bodyValueNull);
        Assert.assertEquals(result, "OK");
        ReflectionTestUtils.setField(receiveService, "token", null);
        try {
            receiveService.receive(body);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), OneNetReceiveException.class);
        }
        ReflectionTestUtils.setField(receiveService, "token", "zhsgdjagywdukgadbhagb");
        result = receiveService.receive(body);
        Assert.assertEquals(result, "OK");
    }

    /**
     * check
     */
    @Test
    public void checkTest() {
        String msg = "ujxw5i";
        String nonce = "mokys68f";
        String signature = "KSxvsUgBmffRHIOoxHHpfQ==";
        try {
            receiveService.check(msg, nonce, signature);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), OneNetReceiveException.class);
        }
        ReflectionTestUtils.setField(receiveService, "token", "zhsgdjagywdukgadbhagb");
        String result = receiveService.check(msg, nonce, signature);
        Assert.assertEquals(result, msg);
    }
}
