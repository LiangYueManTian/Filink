//package com.fiberhome.filink.dprotocol.controller;
//
//import com.fiberhome.filink.bean.FilterCondition;
//import com.fiberhome.filink.bean.QueryCondition;
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.bean.ResultUtils;
//import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
//import com.fiberhome.filink.dprotocol.constant.DeviceProtocolI18n;
//import com.fiberhome.filink.dprotocol.dto.ProtocolVersionBean;
//import com.fiberhome.filink.dprotocol.exception.FilinkDeviceProtocolParamException;
//import com.fiberhome.filink.dprotocol.service.DeviceProtocolService;
//import com.fiberhome.filink.dprotocol.constant.DeviceProtocolResultCode;
//import com.fiberhome.filink.server_common.utils.I18nUtils;
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import org.apache.http.entity.ContentType;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.util.ResourceUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * <P>
// *     设施协议前端控制器测试类
// * </P>
// * @author chaofang@wistronits.com
// * @since 2019/2/2
// */
//@RunWith(JMockit.class)
//public class DeviceProtocolControllerTest {
//    /**测试对象 DeviceProtocolController*/
//    @Tested
//    private DeviceProtocolController deviceProtocolController;
//
//    /**Mock DeviceProtocolService*/
//    @Injectable
//    private DeviceProtocolService deviceProtocolService;
//    /**文件 file*/
//    private MultipartFile file;
//    /**设施协议 deviceProtocol*/
//    private DeviceProtocol deviceProtocol;
//    /**设施协议名称 protocolName*/
//    private String protocolName;
//    /**设施协议ID集合 protocolIds*/
//    private List<String> protocolIds;
//    /**设施协议软硬件版本 protocolVersionBean*/
//    private ProtocolVersionBean protocolVersionBean;
//
//    @Before
//    public void init() throws IOException {
//        DeviceProtocolI18n deviceProtocolI18n = new DeviceProtocolI18n();
//        protocolName = "sssss";
//        deviceProtocol = new DeviceProtocol();
//        deviceProtocol.setProtocolName(protocolName);
//        deviceProtocol.setProtocolId(protocolName);
//        protocolIds = new ArrayList<>();
//        protocolIds.add(protocolName);
//        protocolVersionBean = new ProtocolVersionBean();
//        protocolVersionBean.setSoftwareVersion(protocolName);
//        protocolVersionBean.setHardwareVersion(protocolVersionBean.getSoftwareVersion());
//        File fileN = ResourceUtils.getFile("classpath:junit/protocol.mapper");
//        file = new MockMultipartFile(
//                "protocol.mapper", //文件名
//                "protocol.mapper", //originalName 相当于上传文件在客户机上的文件名
//                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
//                new FileInputStream(fileN) //文件流
//        );
//    }
//    /**
//     * queryDeviceProtocolList
//     */
//    @Test
//    public void queryDeviceProtocolListTest() {
//        QueryCondition<DeviceProtocol> queryCondition = new QueryCondition<>();
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
//                result = "请求参数错误";
//            }
//        };
//        Result result = deviceProtocolController.queryDeviceProtocolList(queryCondition);
//        Assert.assertEquals(result.getCode(), (int) DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR);
//        List<FilterCondition> filterConditions = new ArrayList<>();
//        queryCondition.setFilterConditions(filterConditions);
//        result = deviceProtocolController.queryDeviceProtocolList(queryCondition);
//        Assert.assertEquals(result.getCode(), (int) DeviceProtocolResultCode.SUCCESS);
//    }
//    /**
//     * addDeviceProtocol
//     */
//    @Test
//    public void addDeviceProtocolTest() {
//        Result result = deviceProtocolController.addDeviceProtocol(protocolName, file);
//        Assert.assertEquals(result.getCode(), ResultUtils.success().getCode());
//
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
//                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
//            }
//        };
//        Result result1 = deviceProtocolController.addDeviceProtocol(null, file);
//        Assert.assertEquals(result1.getCode(), (int) DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR);
//    }
//
//    /**
//     * queryFileLimit
//     */
//    @Test
//    public void queryFileLimitTest() {
//        Result result = deviceProtocolController.queryFileLimit();
//        Assert.assertEquals(result.getCode(), ResultUtils.success().getCode());
//    }
//    /**
//     * updateDeviceProtocol
//     */
//    @Test
//    public void updateDeviceProtocolTest() {
//        Result result = deviceProtocolController.updateDeviceProtocol(deviceProtocol, file);
//        Assert.assertEquals(result.getCode(), ResultUtils.success().getCode());
//
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
//                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
//            }
//        };
//        Result result1 = deviceProtocolController.updateDeviceProtocol(null, file);
//        Assert.assertEquals(result1.getCode(), (int)DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR);
//    }
//    /**
//     * updateProtocolName
//     */
//    @Test
//    public void updateProtocolNameTest() {
//        Result result = deviceProtocolController.updateProtocolName(deviceProtocol);
//        Assert.assertEquals(result.getCode(), ResultUtils.success().getCode());
//
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
//                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
//            }
//        };
//        Result result1 = deviceProtocolController.updateProtocolName(null);
//        Assert.assertEquals(result1.getCode(), (int)DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR);
//    }
//    /**
//     * deleteDeviceProtocol
//     */
//    @Test
//    public void deleteDeviceProtocolTest() {
//        Result result = deviceProtocolController.deleteDeviceProtocol(protocolIds);
//        Assert.assertEquals(result.getCode(), ResultUtils.success().getCode());
//
//        List<String> ids = new ArrayList<>();
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR);
//                result = DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR;
//            }
//        };
//        Result result1 = deviceProtocolController.deleteDeviceProtocol(ids);
//        Assert.assertEquals(result1.getCode(), (int)DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR);
//    }
//
//    /**
//     * queryProtocol
//     */
//    @Test
//    public void queryProtocolTest() {
//        String filePath = "4545454555555524555555555555555555824";
//        new Expectations() {
//            {
//                deviceProtocolService.queryProtocol((DeviceProtocol) any);
//                result = filePath;
//            }
//        };
//        String result = deviceProtocolController.queryProtocol(protocolVersionBean);
//        Assert.assertEquals(result, filePath);
//
//        ProtocolVersionBean protocolVersionBean1 = new ProtocolVersionBean();
//        try {
//            deviceProtocolController.queryProtocol(protocolVersionBean1);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolParamException.class);
//        }
//        protocolVersionBean1.setHardwareVersion("            ");
//        protocolVersionBean1.setSoftwareVersion(protocolVersionBean1.getHardwareVersion());
//        try {
//            deviceProtocolController.queryProtocol(protocolVersionBean1);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolParamException.class);
//        }
//    }
//}
