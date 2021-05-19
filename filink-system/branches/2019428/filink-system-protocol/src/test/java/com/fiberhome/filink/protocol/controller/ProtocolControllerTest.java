package com.fiberhome.filink.protocol.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.protocol.dto.CertificateFile;
import com.fiberhome.filink.protocol.dto.ProtocolField;
import com.fiberhome.filink.protocol.dto.ProtocolParams;
import com.fiberhome.filink.protocol.exception.ProtocolParamsErrorException;
import com.fiberhome.filink.protocol.service.ProtocolService;
import com.fiberhome.filink.protocol.utils.ProtocolCheckUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * ProtocolControllerTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/26
 */
@RunWith(JMockit.class)
public class ProtocolControllerTest {

    /**
     * 测试对象protocolController
     */
    @Tested
    private ProtocolController protocolController;

    /**
     * Mock protocolService
     */
    @Injectable
    private ProtocolService protocolService;

    /**
     * 通信协议参数
     */





    /**
     * 证书文件
     */
    @Mocked
    private MultipartFile file;


    @Before
    public void setUp() {
    }




    /**
     * updateProtocol()
     */
    @Test
    public void updateProtocol() {

        ProtocolParams protocolParams = new ProtocolParams();
        ProtocolField protocolField = new ProtocolField();
        CertificateFile certificateFile = new CertificateFile();
        MultipartFile file = null;
        try {
            protocolController.updateProtocol(protocolParams, protocolField, certificateFile, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ProtocolParamsErrorException.class);
    }



        //正确的protocolParams设置
        protocolParams.setParamId("b1840da734e411e992ed519d1fc57e29");
        protocolParams.setParamType("2");

        protocolField.setIp("10.5.33.71");
        protocolField.setPort("8080");
        protocolField.setMaxWait("90");
        protocolField.setEnabled("1");
        protocolField.setMaxActive("100");

        certificateFile.setFileName("012");
        certificateFile.setFileUrl("uri");
        protocolField.setCertificateFile(certificateFile);
        protocolParams.setProtocolField(protocolField);
        new Expectations() {
            {
                //ProtocolCheckUtil.checkProtocolParams((ProtocolParams)any);
                //result = true;
                protocolService.updateProtocol((ProtocolParams) any, (MultipartFile) any);
                result = ResultUtils.success();
            }
        };
        Result result = protocolController.updateProtocol(protocolParams, protocolField, certificateFile, file);
        Assert.assertTrue(result.getCode()==0);

        protocolField.setEnabled("0");
        new Expectations() {
            {
                protocolService.updateProtocol((ProtocolParams) any, (MultipartFile) any);
                result = ResultUtils.success();
            }
        };
        result = protocolController.updateProtocol(protocolParams, protocolField, certificateFile, file);
        Assert.assertTrue(result.getCode()==0);

        //测试webservice
        protocolParams.setParamId("b184114634e411e992ed519d1fc57e29");
        protocolParams.setParamType("4");
        new Expectations() {
            {
                //ProtocolCheckUtil.checkProtocolParams((ProtocolParams)any);
                //result = true;
                protocolService.updateProtocol((ProtocolParams) any, (MultipartFile) any);
                result = ResultUtils.success();
            }
        };
        result = protocolController.updateProtocol(protocolParams, protocolField, certificateFile, file);
        Assert.assertTrue(result.getCode()==0);


        //测试参数有问题的
        protocolParams.setParamType(null);
        try {
            protocolService.updateProtocol(protocolParams, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ProtocolParamsErrorException.class);
        }
        protocolParams.setParamType("-1");
        try {
            protocolService.updateProtocol(protocolParams, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ProtocolParamsErrorException.class);
        }



    }
}
