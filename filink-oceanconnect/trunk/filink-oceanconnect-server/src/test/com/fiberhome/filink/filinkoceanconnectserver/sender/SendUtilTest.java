package com.fiberhome.filink.filinkoceanconnectserver.sender;

import com.fiberhome.filink.filinkoceanconnectserver.client.AuthClient;
import com.fiberhome.filink.filinkoceanconnectserver.client.HttpsClient;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.MethodBean;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.Platform;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.ServiceBean;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.utils.IpUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProfileUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.commons.collections.map.HashedMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class SendUtilTest {

    @Tested
    private SendUtil sendUtil;

    @Injectable
    private HttpsClient httpsClient;

    @Injectable
    private AuthClient authClient;

    @Injectable
    private ProfileUtil profileUtil;

    @Injectable
    private IpUtil ipUtil;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: sendData(FiLinkReqOceanConnectParams oceanConnectParams, String data)
     */
    @Test
    public void testSendData() throws Exception {
        FiLinkReqOceanConnectParams oceanConnectParams = new FiLinkReqOceanConnectParams();
        sendUtil.sendData(oceanConnectParams, "");
        oceanConnectParams.setOceanConnectId("testId");
        try {
            sendUtil.sendData(oceanConnectParams, "");
        } catch (Exception e) {
        }
        Platform profileConfig = new Platform();
        Map<String, ServiceBean> downMap = new HashedMap();
        ServiceBean serviceBean = new ServiceBean();
        serviceBean.setBase64(true);
        MethodBean methodBean = new MethodBean();
        methodBean.setLength("5");
        methodBean.setData("test");
        serviceBean.setMethod(methodBean);
        downMap.put("testId", serviceBean);
        profileConfig.setDownMap(downMap);
        oceanConnectParams.setAppId("testId");
        new Expectations() {
            {
                profileUtil.getProfileConfig();
                result = profileConfig;
            }
        };
        sendUtil.sendData(oceanConnectParams, "");
        profileConfig.getDownMap().get("testId").setMethod(null);
        try {
            sendUtil.sendData(oceanConnectParams, "");
        } catch (Exception e) {

        }
    }


    /**
     * Method: getParamCommand(String appId, String data)
     */
    @Test
    public void testGetParamCommand() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = SendUtil.getClass().getMethod("getParamCommand", String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
