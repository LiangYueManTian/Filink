package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.cprotocol.api.ConnectProtocolFeign;
import com.fiberhome.filink.cprotocol.bean.ProtocolEnum;
import com.fiberhome.filink.cprotocol.bean.ProtocolField;
import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class HostUtilTest {

    /**测试对象 HostUtil*/
    @Tested
    private HostUtil hostUtil;
    /**Mock ConnectProtocolFeign*/
    @Injectable
    private ConnectProtocolFeign protocolFeign;
    /**
     * getOneNetHost
     */
    @Test
    public void getOneNetHostTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(RedisKey.ADDRESS_KEY);
                result = null;
                RedisUtils.set(RedisKey.ADDRESS_KEY, any);
                protocolFeign.queryProtocol(ProtocolEnum.HTTP_PROTOCOL.getType());
                result = null;
            }
        };
        String result = hostUtil.getOneNetHost();
        Assert.assertNull(result);
        ProtocolField protocolField = new ProtocolField();
        new Expectations() {
            {
                protocolFeign.queryProtocol(ProtocolEnum.HTTP_PROTOCOL.getType());
                result = protocolField;
            }
        };
        result = hostUtil.getOneNetHost();
        Assert.assertNull(result);
        String ip = "10.5.24.224";
        protocolField.setIp(ip);
        result = hostUtil.getOneNetHost();
        Assert.assertEquals(ip, result);
    }
}
