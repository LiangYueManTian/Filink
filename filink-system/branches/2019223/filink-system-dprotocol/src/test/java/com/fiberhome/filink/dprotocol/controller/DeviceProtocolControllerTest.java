package com.fiberhome.filink.dprotocol.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.dprotocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.dprotocol.exception.FilinkDeviceProtocolParamException;
import com.fiberhome.filink.dprotocol.service.DeviceProtocolService;
import com.fiberhome.filink.dprotocol.utils.DeviceProtocolResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 *     设施协议前端控制器测试类
 * </P>
 * @author chaofang@wistronits.com
 * @since 2019/2/2
 */
@RunWith(JMockit.class)
public class DeviceProtocolControllerTest {
    /**测试对象 DeviceProtocolController*/
    @Tested
    private DeviceProtocolController deviceProtocolController;

    /**Mock DeviceProtocolService*/
    @Injectable
    private DeviceProtocolService deviceProtocolService;
    /**文件 file*/
    private MultipartFile file;
    /**设施协议 deviceProtocol*/
    private DeviceProtocol deviceProtocol;
    /**设施协议名称 protocolName*/
    private String protocolName;
    /**设施协议ID集合 protocolIds*/
    private List<String> protocolIds;
    /**设施协议软硬件版本 protocolVersionBean*/
    private ProtocolVersionBean protocolVersionBean;
    /**设施协议信息 fiLinkProtocolBean*/
    private FiLinkProtocolBean fiLinkProtocolBean;

    @Before
    public void init() throws IOException {
        DeviceProtocolI18n deviceProtocolI18n = new DeviceProtocolI18n();
        protocolName = "sssss";
        deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolName(protocolName);
        deviceProtocol.setProtocolId(protocolName);
        protocolIds = new ArrayList<>();
        protocolIds.add(protocolName);
        protocolVersionBean = new ProtocolVersionBean();
        protocolVersionBean.setSoftwareVersion(protocolName);
        protocolVersionBean.setHardwareVersion(protocolName);
        fiLinkProtocolBean = new FiLinkProtocolBean();
        File fileN = ResourceUtils.getFile("classpath:junit/protocol.xml");
        file = new MockMultipartFile(
                "protocol.xml", //文件名
                "protocol.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
    }
    /**
     * queryDeviceProtocolList
     */
    @Test
    public void queryDeviceProtocolList() {
        Result result = deviceProtocolController.queryDeviceProtocolList();
        Assert.assertTrue(result.getCode() == 0);
    }
    /**
     * queryFileLimit
     */
    @Test
    public void queryFileLimit() {
        Result result = deviceProtocolController.queryFileLimit();
        Assert.assertTrue(result.getCode() == 0);
    }
    /**
     * addDeviceProtocol
     */
    @Test
    public void addDeviceProtocol() {
        Result result = deviceProtocolController.addDeviceProtocol(protocolName, file);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        String name = null;
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
            }
        };
        int resultCode = DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR;
        Result result1 = deviceProtocolController.addDeviceProtocol(name, file);
        Assert.assertTrue(result1.getCode() == resultCode);
    }
    /**
     * updateDeviceProtocol
     */
    @Test
    public void updateDeviceProtocol() {
        Result result = deviceProtocolController.updateDeviceProtocol(deviceProtocol, file);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        DeviceProtocol deviceProtocol1 = null;
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
            }
        };
        int resultCode = DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR;
        Result result1 = deviceProtocolController.updateDeviceProtocol(deviceProtocol1, file);
        Assert.assertTrue(result1.getCode() == resultCode);
    }
    /**
     * updateProtocolName
     */
    @Test
    public void updateProtocolName() {
        Result result = deviceProtocolController.updateProtocolName(deviceProtocol);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        DeviceProtocol deviceProtocol1 = null;
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
            }
        };
        int resultCode = DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR;
        Result result1 = deviceProtocolController.updateProtocolName(deviceProtocol1);
        Assert.assertTrue(result1.getCode() == resultCode);
    }
    /**
     * deleteDeviceProtocol
     */
    @Test
    public void deleteDeviceProtocol() {
        Result result = deviceProtocolController.deleteDeviceProtocol(protocolIds);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        List<String> ids = new ArrayList<>();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
            }
        };
        int resultCode = DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR;
        Result result1 = deviceProtocolController.deleteDeviceProtocol(ids);
        Assert.assertTrue(result1.getCode() == resultCode);
    }

    @Test
    public void queryProtocolXmlBean() {
        new Expectations() {
            {
                deviceProtocolService.queryProtocolXmlBean((ProtocolVersionBean) any);
                result = fiLinkProtocolBean;
            }
        };
        FiLinkProtocolBean result = deviceProtocolController.queryProtocolXmlBean(protocolVersionBean);
        Assert.assertTrue(result == fiLinkProtocolBean);

        ProtocolVersionBean protocolVersionBean1 = null;
        try {
            deviceProtocolController.queryProtocolXmlBean(protocolVersionBean1);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolParamException.class);
        }
    }
}
