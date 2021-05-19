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
import java.net.URLDecoder;

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
    private String testFile;
    @Before
    public void before() throws Exception {
        String classPath = URLDecoder.decode(this.getClass().getResource("/").getPath(),"utf-8");
        String[] split = classPath.split("filink-oceanconnect-server");
        testFile = split[0]+"filink-oceanconnect-server\\src\\test\\com\\fiberhome\\filink\\filinkoceanconnectserver\\testFilie\\";
    }
    //C:\workFile\zqq\svn\Filink_Project\10-Sys Code\filink-oceanconnect\trunk\filink-oceanconnect-server\src\test\com\fiberhome\filink\filinkoceanconnectserver\testFilie\protocolConfig.xml
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
        File file = new File(testFile+"protocolConfig.xml");
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

}
