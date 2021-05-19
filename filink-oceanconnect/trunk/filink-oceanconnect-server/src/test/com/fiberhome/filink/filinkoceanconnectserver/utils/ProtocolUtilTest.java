package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.utils.FiLinkProtocolResolver;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.protocol.api.ProtocolFeign;
import com.fiberhome.filink.protocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

/**
 * protocolUtil
 */
@RunWith(JMockit.class)
public class ProtocolUtilTest {

    @Tested
    private ProtocolUtil protocolUtil;

    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private ProtocolFeign protocolFeign;

    @Mocked
    private RedisUtils redisUtils;

    private FiLinkProtocolBean protocolBean;

    @Before
    public void setUp() {
        protocolBean = new FiLinkProtocolBean();
    }


    @Test
    public void getProtocolBean() {
        //软硬件版本不为空
        FiLinkReqOceanConnectParams fiLinkReqParams = new FiLinkReqOceanConnectParams();
        fiLinkReqParams.setHardwareVersion("NRF52840_elock");
        fiLinkReqParams.setSoftwareVersion("RP9003.004A.bin");
        new MockUp<ProtocolUtil>() {
            @Mock
            private FiLinkProtocolBean getProtocolBeanInRedis(String softwareVersion, String hardwareVersion) {
                return protocolBean;
            }
        };
        protocolUtil.getProtocolBean(fiLinkReqParams);

        //软硬件版本为空
        FiLinkReqOceanConnectParams nullFiLinkReqParams = new FiLinkReqOceanConnectParams();
        ControlParam controlParam = new ControlParam();
        new MockUp<ProtocolUtil>() {
            @Mock
            public ControlParam getControlById(String equipmentId) {
                return controlParam;
            }

            @Mock
            public FiLinkProtocolBean getProtocolBeanByControl(ControlParam control) {
                return protocolBean;
            }
        };
        protocolUtil.getProtocolBean(nullFiLinkReqParams);
    }

    @Test
    public void getControlById() {
        //controlParam不为空
        ControlParam controlParam = new ControlParam();
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.CONTROL_INFO, anyString);
                result = controlParam;
            }

            {
                RedisUtils.hSet(RedisKey.CONTROL_INFO, anyString, controlParam);
            }
        };

        try {
            protocolUtil.getControlById("2313213");
        } catch (Exception e) {
        }

        //controlParam为空
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.CONTROL_INFO, anyString);
                result = null;
            }

            {
                RedisUtils.hSet(RedisKey.CONTROL_INFO, anyString, controlParam);
            }

            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }
        };
        try {
            protocolUtil.getControlById("2313213");
        } catch (Exception e) {
        }

        //controlParam不为空
        controlParam.setHostId("123123");
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.CONTROL_INFO, anyString);
                result = null;
            }

            {
                RedisUtils.hSet(RedisKey.CONTROL_INFO, anyString, controlParam);
            }

            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }
        };
        try {
            protocolUtil.getControlById("2313213");
        } catch (Exception e) {
        }

    }

    @Test
    public void getProtocolBeanByControl() {
        //软硬件版本不为空,protocolBean不为空
        FiLinkProtocolBean fiLinkProtocolBean = new FiLinkProtocolBean();
        ControlParam notNullControl = new ControlParam();
        notNullControl.setHardwareVersion("NRF52840_elock");
        notNullControl.setSoftwareVersion("RP9003.004A.bin");
        String key = notNullControl.getSoftwareVersion() + notNullControl.getHardwareVersion();
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.PROTOCOL_KEY, key);
                result = fiLinkProtocolBean;
            }
        };
        protocolUtil.getProtocolBeanByControl(notNullControl);

        //软硬件版本不为空,protocolBean为空
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.PROTOCOL_KEY, key);
                result = null;
            }

            {
                protocolFeign.queryProtocol((ProtocolVersionBean) any);
                result = "hexFile";
            }
        };
        new MockUp<ProtocolUtil>() {
            @Mock
            public FiLinkProtocolBean setProtocolToRedis(String fileHexData) {
                return fiLinkProtocolBean;
            }
        };
        protocolUtil.getProtocolBeanByControl(notNullControl);

        //软硬件版本为空
        ControlParam nullControl = new ControlParam();
        try {
            protocolUtil.getProtocolBeanByControl(nullControl);
        } catch (Exception e) {

        }

    }

    @Test
    public void deleteProtocolToRedis() {
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setHardwareVersion("NRF52840_elock");
        protocolDto.setSoftwareVersion("RP9003.004A.bin");
        String key = protocolDto.getSoftwareVersion() + protocolDto.getHardwareVersion();
        new Expectations() {
            {
                RedisUtils.hRemove(RedisKey.PROTOCOL_KEY, key);
            }
        };
        protocolUtil.deleteProtocolToRedis(protocolDto);
    }

    @Test
    public void setProtocolToRedis() {
        FiLinkProtocolBean fiLinkProtocolBean = new FiLinkProtocolBean();
        fiLinkProtocolBean.setHardwareVersion("NRF52840_elock");
        fiLinkProtocolBean.setSoftwareVersion("RP9003.004A.bin");
        String hexFile = "hexFile";
        String key = fiLinkProtocolBean.getSoftwareVersion() + fiLinkProtocolBean.getHardwareVersion();
        byte[] bytes = {24, 11, 23, 43};
        new Expectations(HexUtil.class) {
            {
                HexUtil.hexStringToByte(hexFile);
                result = bytes;
            }
        };
        new MockUp<FiLinkProtocolResolver>() {
            @Mock
            public FiLinkProtocolBean resolve(InputStream inputStream) {
                return fiLinkProtocolBean;
            }
        };
        new Expectations() {
            {
                RedisUtils.hSet(RedisKey.PROTOCOL_KEY, key, fiLinkProtocolBean);
            }
        };
        protocolUtil.setProtocolToRedis(hexFile);
    }
}