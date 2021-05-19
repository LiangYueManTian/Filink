package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.cprotocol.api.ConnectProtocolFeign;
import com.fiberhome.filink.cprotocol.bean.ProtocolEnum;
import com.fiberhome.filink.cprotocol.bean.ProtocolField;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ipUtil测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class IpUtilTest {

    @Tested
    private IpUtil ipUtil;

    @Injectable
    private ConnectProtocolFeign protocolFeign;

    @Test
    public void getOceanConnectAddress() {
        //redis地址为空,查询为空
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(RedisKey.ADDRESS_KEY);
                result = null;
            }
        };
        new Expectations() {
            {
                protocolFeign.queryProtocol(ProtocolEnum.HTTPS_PROTOCOL.getType());
                result = null;
            }
        };
        try {
            ipUtil.getOceanConnectAddress();
        } catch (Exception e) {
        }

        //redis地址为空,查询不为空,ip为空
        ProtocolField nullId = new ProtocolField();
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(RedisKey.ADDRESS_KEY);
                result = null;
            }
        };
        new Expectations() {
            {
                protocolFeign.queryProtocol(ProtocolEnum.HTTPS_PROTOCOL.getType());
                result = nullId;
            }
        };
        try {
            ipUtil.getOceanConnectAddress();
        } catch (Exception e) {
        }

        //redis地址为空,查询不为空,ip不为空
        ProtocolField protocolField = new ProtocolField();
        protocolField.setIp("10.5.24.12");
        protocolField.setPort("5683");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(RedisKey.ADDRESS_KEY);
                result = null;
            }
            {
                RedisUtils.set(RedisKey.ADDRESS_KEY, anyString);
            }
        };
        new Expectations() {
            {
                protocolFeign.queryProtocol(ProtocolEnum.HTTPS_PROTOCOL.getType());
                result = protocolField;
            }
        };
        ipUtil.getOceanConnectAddress();
    }
}