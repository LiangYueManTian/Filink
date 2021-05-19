package com.fiberhome.filink.filinkoceanconnectserver.sender;

import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.utils.FiLinkProtocolResolver;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class FiLinkOceanConnectRequestResolverTest {

    @Tested
    private FiLinkOceanConnectRequestResolver fiLinkOceanConnectRequestResolver;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: resolveUdpReq(AbstractReqParams udpParams)
     */
    @Test
    public void testResolveUdpReq() throws Exception {
        AbstractReqParams udpParams = new FiLinkReqOceanConnectParams();
        try {
            fiLinkOceanConnectRequestResolver.resolveUdpReq(udpParams);
        } catch (Exception e) {}
        File file = new File("C:\\workFile\\zqq\\svn\\Filink_Project\\10-Sys Code\\filink-oceanconnect\\trunk\\filink-oceanconnect-server\\src\\main\\resources\\config\\protocolConfig.xml");
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FiLinkProtocolResolver fiLinkProtocolResolver = new FiLinkProtocolResolver();
        FiLinkProtocolBean fiLinkProtocolBean =fiLinkProtocolResolver.resolve(input);
        udpParams.setProtocolBean(fiLinkProtocolBean);
        udpParams.setCmdType(CmdType.REQUEST_TYPE);
        udpParams.setCmdId(CmdId.UNLOCK);
        Map<String, Object> fiLinkReqParams = new HashMap<>();
        fiLinkReqParams.put("identificationNumber","test");
        udpParams.setParams(fiLinkReqParams);
        fiLinkOceanConnectRequestResolver.resolveUdpReq(udpParams);
    }


    /**
     * Method: setHeader(FiLinkReqOceanConnectParams fiLinkReqOceanConnectParams, int dataLength, List<HeaderParam> headerParams)
     */
    @Test
    public void testSetHeader() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("setHeader", FiLinkReqOceanConnectParams.class, int.class, List<HeaderParam>.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: resolveScope(int cmdType, HeaderParam headerParam, FiLinkCommonHeader commonHeader, int dataLength)
     */
    @Test
    public void testResolveScope() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("resolveScope", int.class, HeaderParam.class, FiLinkCommonHeader.class, int.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: setHeaderBuf(List<HeaderParam> headerParams, Object source, ByteBuf headerBuf)
     */
    @Test
    public void testSetHeaderBuf() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("setHeaderBuf", List<HeaderParam>.class, Object.class, ByteBuf.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: executeHeaderFormat(DataFormat dataFormat, ByteBuf headerBuf, String dataSource)
     */
    @Test
    public void testExecuteHeaderFormat() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("executeHeaderFormat", DataFormat.class, ByteBuf.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getDataBuf(FiLinkReqOceanConnectParams fiLinkReqOceanConnectParams, FiLinkProtocolBean fiLinkProtocolXmlBean)
     */
    @Test
    public void testGetDataBuf() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("getDataBuf", FiLinkReqOceanConnectParams.class, FiLinkProtocolBean.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: writeDataParamChild(List<DataParamsChild> dataParams, ByteBuf dataBuf, Map<String, Object> fiLinkReqParams)
     */
    @Test
    public void testWriteDataParamChild() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("writeDataParamChild", List<DataParamsChild>.class, ByteBuf.class, Map<String,.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: writeForeach(ForEach forEach, Map<String, Object> fiLinkReqParams, ByteBuf dataBuf)
     */
    @Test
    public void testWriteForeach() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("writeForeach", ForEach.class, Map<String,.class, ByteBuf.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: execute(DataFormat dataFormat, ByteBuf byteBuf)
     */
    @Test
    public void testExecute() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("execute", DataFormat.class, ByteBuf.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: setRandomLen(Integer length, ByteBuf dataBuf)
     */
    @Test
    public void testSetRandomLen() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("setRandomLen", Integer.class, ByteBuf.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: setBufData(ByteBuf dataBuf, Map<String, Object> fiLinkReqParams, DataParam dataParam)
     */
    @Test
    public void testSetBufData() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("setBufData", ByteBuf.class, Map<String,.class, DataParam.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getDataCodeByKey(String key, Map<String, Choose> chooseMap)
     */
    @Test
    public void testGetDataCodeByKey() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("getDataCodeByKey", String.class, Map<String,.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: setReservedLen(int length, ByteBuf dataBuf, int data)
     */
    @Test
    public void testSetReservedLen() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkOceanConnectRequestResolver.getClass().getMethod("setReservedLen", int.class, ByteBuf.class, int.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
