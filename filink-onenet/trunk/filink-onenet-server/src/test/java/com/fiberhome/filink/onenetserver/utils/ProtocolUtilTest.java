package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.exception.ProtocolException;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetConnectParams;
import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.protocol.api.ProtocolFeign;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class ProtocolUtilTest {

    /**测试对象 ProtocolUtil*/
    @Tested
    private ProtocolUtil protocolUtil;
    /**Mock ControlFeign*/
    @Injectable
    private ControlFeign controlFeign;
    /**Mock ProtocolFeign*/
    @Injectable
    private ProtocolFeign protocolFeign;

    /**
     * getProtocolBean
     */
    @Test
    public void getProtocolBeanTest() {
        AbstractReqParams abstractReqParams = new FiLinkOneNetConnectParams();
        abstractReqParams.setEquipmentId("545445");
        abstractReqParams.setSoftwareVersion("RP9003.004A.bin");
        new Expectations(RedisUtils.class) {
            {
                controlFeign.getControlParamById(anyString);
                result = null;

                RedisUtils.hGet(RedisKey.CONTROL_INFO, anyString);
                result = null;
            }
        };
        try {
            protocolUtil.getProtocolBean(abstractReqParams);
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), ProtocolException.class);
        }
        ControlParam control = new ControlParam();
        control.setSoftwareVersion("RP9003.004A.bin");
        new Expectations() {
            {
                controlFeign.getControlParamById(anyString);
                result = control;
            }
        };
        try {
            protocolUtil.getProtocolBean(abstractReqParams);
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), ProtocolException.class);
        }
        control.setHardwareVersion("NRF52840_elock");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.PROTOCOL_KEY, anyString);
                result = null;
            }
        };
        try {
            protocolUtil.getProtocolBean(abstractReqParams);
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), ProtocolException.class);
        }
        abstractReqParams.setHardwareVersion("NRF52840_elock");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.PROTOCOL_KEY, anyString);
                result = new FiLinkProtocolBean();
            }
        };
        FiLinkProtocolBean result = protocolUtil.getProtocolBean(abstractReqParams);
        Assert.assertNull(result.getSoftwareVersion());
    }


    /**
     * deleteProtocolToRedis
     */
    @Test
    public void deleteProtocolToRedisTest() {
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setSoftwareVersion("RP9003.004A.bin");
        protocolDto.setHardwareVersion("NRF52840_elock");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(anyString, anyString);
            }
        };
        protocolUtil.deleteProtocolToRedis(protocolDto);
    }

}
