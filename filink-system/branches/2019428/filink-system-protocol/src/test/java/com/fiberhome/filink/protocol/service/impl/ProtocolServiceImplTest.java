package com.fiberhome.filink.protocol.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.protocol.dto.CertificateFile;
import com.fiberhome.filink.protocol.dto.ProtocolField;
import com.fiberhome.filink.protocol.dto.ProtocolParams;
import com.fiberhome.filink.protocol.exception.ProtocolParamsErrorException;
import com.fiberhome.filink.protocol.exception.ProtocolSystemException;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.system_commons.bean.SysParam;
import com.fiberhome.filink.system_commons.dao.SysParamDao;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * ProtocolServiceImplTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/26
 */
@RunWith(JMockit.class)
public class ProtocolServiceImplTest {
    /**
     * 要测试的对象
     */
    @Tested
    private ProtocolServiceImpl protocolService;

    /**
     * 自动注入protocolDao
     */
    @Injectable
    private SysParamDao protocolDao;

    /**
     * 自动注入fdfsFeign
     */
    @Injectable
    private FdfsFeign fdfsFeign;

    @Mocked
    private I18nUtils i18nUtils;

    @Mocked
    private RedisUtils redisUtils;


    @Mocked
    private SysParam protocolDto;

    @Mocked
    private RequestContextHolder requestContextHolder;
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;


    /**
     * mock 一个文件，用于测试
     */
    @Mocked
    MultipartFile testFile;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
    }


    /**
     * updateProtocol()
     */

    @Rollback(value = true)
    @Transactional(transactionManager = "transactionManager")
    @Test
    public void updateProtocol() {
        ProtocolParams protocolParams = new ProtocolParams();
        ProtocolField protocolField = new ProtocolField();
        CertificateFile certificateFile = new CertificateFile();
        MultipartFile file = null;

        //正确的protocolParams设置
        protocolParams.setParamId("b184104134e411e992ed519d1fc57e29");
        protocolParams.setParamType("3");

        protocolField.setIp("10.5.33.71");
        protocolField.setPort("8080");
        protocolField.setMaxWait("90");
        protocolField.setEnabled("1");
        protocolField.setMaxActive("100");

        certificateFile.setFileName("012");
        certificateFile.setFileUrl("uri");
        protocolField.setCertificateFile(certificateFile);
        protocolParams.setProtocolField(protocolField);





        protocolParams.setParamType("3");
        //更新时，从数据根据id查不到协议，报系统异常
        new Expectations() {
            {
                protocolDao.queryParamById(anyString);
                result = null;
            }
        };
        try {
            protocolService.updateProtocol(protocolParams, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ProtocolSystemException.class);
        }

        //更新时，数据库出现宕机，报系统异常
        new Expectations() {
            {
                protocolDao.queryParamById(anyString);
                result = protocolDto;
                protocolDao.updateParamById((SysParam) any);
                result = 0;
            }
        };
        try {
            protocolService.updateProtocol(protocolParams, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ProtocolSystemException.class);
        }

        //正常 无文件
        new Expectations() {
            {
                protocolDao.queryParamById(anyString);
                result = protocolDto;

                protocolDao.updateParamById((SysParam) any);
                result = 1;


            }
        };

        Result result = protocolService.updateProtocol(protocolParams, file);
        Assert.assertTrue(result.getCode() == 0);

        //参数正常  有文件 fdfsFeign异常
        new Expectations() {
            {

                fdfsFeign.uploadFile((MultipartFile) any);
                result = null;

                protocolDao.queryParamById(anyString);
                result = protocolDto;

                protocolDao.updateParamById((SysParam) any);
                result = 1;


            }
        };

        try {
            protocolService.updateProtocol(protocolParams, testFile);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ProtocolSystemException.class);
        }

        //一切正常  有文件
        new Expectations() {
            {

                fdfsFeign.uploadFile((MultipartFile) any);
                result = "wwww";

                protocolDao.queryParamById(anyString);
                result = protocolDto;

                protocolDao.updateParamById((SysParam) any);
                result = 1;


            }
        };

        Result result2 = protocolService.updateProtocol(protocolParams, testFile);
        Assert.assertTrue(result2.getCode() == 0);


    }
}
