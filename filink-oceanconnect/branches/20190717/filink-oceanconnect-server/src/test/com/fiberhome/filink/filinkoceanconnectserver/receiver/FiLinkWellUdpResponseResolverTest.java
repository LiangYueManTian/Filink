package com.fiberhome.filink.filinkoceanconnectserver.receiver;

import com.fiberhome.filink.commonstation.entity.param.AbstractResInputParams;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.utils.FiLinkProtocolResolver;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResInputParams;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class FiLinkWellUdpResponseResolverTest {
    /**
     * 注入 commonUtil
     */
    @Injectable
    private CommonUtil commonUtil;
    /**
     * 要测试的
     */
    @Tested
    private FiLinkWellUdpResponseResolver fiLinkWellUdpResponseResolver;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: resolveRes(AbstractResInputParams inputParams)
     */
    @Test
    public void testResolveRes() {
        AbstractResInputParams inputParams = new FiLinkOceanResInputParams();
        inputParams.setDataSource("FE8D003B00FCFFD806FF2F0000215AEF5C0000A300230F4D03301832001426D6195D534D435F4856332E30305F5356312E30355F4454373233332E3631313432000000000000000000000000000000000000A61B8813000001000000A0050500010000020000008A320000203335373531363038303234343936392C3436303131313137343732333039372BEF");
        FiLinkProtocolResolver fiLinkProtocolResolver = new FiLinkProtocolResolver();
        File file = new File("C:\\workFile\\zqq\\svn\\Filink_Project\\10-Sys Code\\filink-oceanconnect\\trunk\\filink-oceanconnect-server\\src\\main\\resources\\config\\protocolConfig.xml");
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FiLinkProtocolBean fiLinkProtocolBean =fiLinkProtocolResolver.resolve(input);
        inputParams.setProtocolBean(fiLinkProtocolBean);
        fiLinkWellUdpResponseResolver.resolveRes(inputParams);
    }


    /**
     * Method: resolveHeader(ByteBuf dataBuf, List<HeaderParam> responseHeaders, Object source)
     */
    @Test
    public void testResolveHeader() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = FiLinkWellUdpResponseResolver.getClass().getMethod("resolveHeader", ByteBuf.class, List<HeaderParam>.class, Object.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
    }

    /**
     * Method: resolveData(ByteBuf dataBuf, FiLinkCommonHeader commonHeader, FiLinkProtocolBean fiLinkProtocolBean)
     */
    @Test
    public void testResolveData() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = FiLinkWellUdpResponseResolver.getClass().getMethod("resolveData", ByteBuf.class, FiLinkCommonHeader.class, FiLinkProtocolBean.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
    }

    /**
     * Method: resolveDataParamsChild(ByteBuf dataBuf, Map<String, Object> dataResponseMap, List<DataParamsChild> dataParams)
     */
    @Test
    public void testResolveDataParamsChild() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = FiLinkWellUdpResponseResolver.getClass().getMethod("resolveDataParamsChild", ByteBuf.class, Map<String,.class, List<DataParamsChild>.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
    }

    /**
     * Method: resolveForEach(ForEach forEach, Map<String, Object> dataResponseMap, ByteBuf byteBuf)
     */
    @Test
    public void testResolveForEach() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = FiLinkWellUdpResponseResolver.getClass().getMethod("resolveForEach", ForEach.class, Map<String,.class, ByteBuf.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
    }

    /**
     * Method: resolveDataParam(DataParam dataParam, ByteBuf dataBuf, Map<String, Object> dataResponseMap)
     */
    @Test
    public void testResolveDataParam() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = FiLinkWellUdpResponseResolver.getClass().getMethod("resolveDataParam", DataParam.class, ByteBuf.class, Map<String,.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
    }

}
