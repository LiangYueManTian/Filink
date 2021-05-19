package com.fiberhome.filink.rfid.controller.facility;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.rfid.bean.facility.*;
import com.fiberhome.filink.rfid.service.mobile.MobileService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

/**
 * Created by Qing on 2019/7/1.
 * FacilityController test
 */
@RunWith(JMockit.class)
public class FacilityControllerTest {

    @Tested
    private FacilityController facilityController;

    /**
     * 移动端交互service
     */
    @Injectable
    private MobileService mobileService;

    /**
     * templateService
     */
    @Injectable
    private TemplateService templateService;

    /**
     * 请求设施标签信息
     * @throws Exception 异常
     */
    @Test
    public void queryFacilityInfo() throws Exception {
        new Expectations() {
            {
                mobileService.queryFacilityINfo((FacilityQueryBean) any);
                result = ResultUtils.success();
            }
        };
        Result result = facilityController.queryFacilityInfo(new FacilityQueryBean());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 设施标签信息上传
     * @throws Exception 异常
     */
    @Test
    public void uploadFacilityInfo() throws Exception {
        new Expectations() {
            {
                mobileService.uploadFacilityInfo((FacilityUploadDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = facilityController.uploadFacilityInfo(new FacilityUploadDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 设施标签信息更改
     * @throws Exception 异常
     */
    @Test
    public void changeFacilityLabel() throws Exception {
        new Expectations() {
            {
                mobileService.changeFacilityLabel(anyString, anyString, anyInt);
                result = ResultUtils.success();
            }
        };

        ChangeLabelBean bean = new ChangeLabelBean();
        bean.setOldLabel("oldLabel");
        bean.setNewLabel("newLabel");
        bean.setDeviceType(1);
        Result result = facilityController.changeFacilityLabel(bean);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 请求设施实体信息
     * @throws Exception 异常
     */
    @Test
    public void queryDeviceInfo() throws Exception {
        new Expectations() {
            {
                mobileService.queryDeviceInfo((DeviceQueryBean) any);
                result = new DeviceEntity();
            }
        };
        Result result = facilityController.queryDeviceInfo(new DeviceQueryBean());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 设施实体信息上传
     * @throws Exception 异常
     */
    @Test
    public void uploadDeviceInfo() throws Exception {
        new Expectations() {
            {
                mobileService.uploadDeviceInfo((DeviceUploadDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = facilityController.uploadDeviceInfo(new DeviceUploadDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 请求设施模板信息(全模板下载)
     * @throws Exception 异常
     */
    @Test
    public void queryAllDeviceTemp() throws Exception {
        new Expectations() {
            {
                mobileService.queryAllTemplate();
                result = ResultUtils.success();
            }
        };
        Result result = facilityController.queryAllDeviceTemp();
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 设施模板信息上传
     * @throws Exception 异常
     */
    @Test
    public void uploadDeviceTemp() throws Exception {
        new Expectations() {
            {
                mobileService.uploadDeviceTemplate((DeviceEntity) any);
                result = ResultUtils.success();
            }
        };
        Result result = facilityController.uploadDeviceTemp(new DeviceEntity());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 设施业务信息上传
     * @throws Exception 异常
     */
    @Test
    public void uploadFacilityBusInfo() throws Exception {
        new Expectations() {
            {
                mobileService.uploadFacilityBusInfo((FacilityBusUploadDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = facilityController.uploadFacilityBusInfo(new FacilityBusUploadDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 查询设施业务信息
     * @throws Exception 异常
     */
    @Test
    public void queryFacilityBusInfoById() throws Exception {
        new Expectations() {
            {
                templateService.getRfIdDataAuthInfo(anyString);
                result = true;

                mobileService.queryBusInfoByDeviceId(anyString, anyString);
                result = ResultUtils.success();
            }
        };
        Result result = facilityController.queryFacilityBusInfoById("deviceId", "deviceType");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 删除设施--回收标签
     * @throws Exception 异常
     */
    @Test
    public void recoverLabelWithDeviceId() throws Exception {
        new Expectations() {
            {
               mobileService.deleteDeviceEntity(anyString);
               result = ResultUtils.success();
            }
        };
        Result result = facilityController.recoverLabelWithDeviceId(Collections.singletonList("deviceId"));
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }


}