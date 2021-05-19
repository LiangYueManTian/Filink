package com.fiberhome.filink.filinkoceanconnectserver.sender;

import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import com.fiberhome.filink.commonstation.sender.RequestResolver;
import com.fiberhome.filink.deviceapi.util.DeployStatus;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProtocolUtil;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class FiLinkOceanConnectWellSenderTest {
    @Tested
    private FiLinkOceanConnectWellSender fiLinkOceanConnectWellSender;
    @Injectable
    private Map<String, RequestResolver> requestHandlerMap;

    @Injectable
    private ProtocolUtil protocolUtil;

    @Injectable
    private SendUtil sendUtil;
    @Injectable
    private CommonUtil commonUtil;
    @Mocked
    private RedisUtils redisUtils;
    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: send(AbstractReqParams abstractReqParams, String data)
     */
    @Test
    public void testSend() throws Exception {
        AbstractReqParams abstractReqParams = new FiLinkReqOceanConnectParams();
        fiLinkOceanConnectWellSender.send(abstractReqParams,"");
    }

    /**
     * Method: getReqData(AbstractReqParams abstractReqParams)
     */
    @Test
    public void testGetReqData() throws Exception {
        AbstractReqParams abstractReqParams = new FiLinkReqOceanConnectParams();
        fiLinkOceanConnectWellSender.getReqData(abstractReqParams);
        abstractReqParams.setCmdId(CmdId.UNLOCK);
        fiLinkOceanConnectWellSender.getReqData(abstractReqParams);
        Map<String, Object> params = new HashMap<>();
        params.put(ParamsKey.DEPLOY_STATUS, DeployStatus.DEPLOYING.getCode());
        abstractReqParams.setParams(params);
        abstractReqParams.setCmdId(CmdId.DEPLOY_STATUS);
        fiLinkOceanConnectWellSender.getReqData(abstractReqParams);
        abstractReqParams.setCmdId(CmdId.SET_CONFIG);
        fiLinkOceanConnectWellSender.getReqData(abstractReqParams);
    }


} 
