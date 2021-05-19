package com.fiberhome.filink.license.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.service.impl.LicenseInfoServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * LicenseInfo 控制器测试类
 *
 * @Author: zl
 * @Date: 2019/3/28 15:28
 * @Description: com.fiberhome.filink.license.controller
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class LicenseInfoControllerTest {

    @InjectMocks
    private LicenseInfoController licenseInfoController;

    @Mock
    private LicenseInfoServiceImpl licenseInfoService;

    @Test
    public void uploadLicense() throws Exception{
        MultipartFile mockFile = mock(MultipartFile.class);
        when(licenseInfoService.uploadLicense(mockFile)).thenReturn(ResultUtils.success());
        Result result = licenseInfoController.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getLicenseDetail() throws Exception {
        when(licenseInfoService.getLicenseDetail()).thenReturn(ResultUtils.success());
        Result result = licenseInfoController.getLicenseDetail();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void validateLicenseTime() throws Exception {
        when(licenseInfoService.validateLicenseTime()).thenReturn(true);
        boolean b = licenseInfoController.validateLicenseTime();
        Assert.assertTrue(b);
    }

    @Test
    public void updateRedisLicenseThreshold() throws Exception {
        LicenseFeignBean licenseFeignBean = mock(LicenseFeignBean.class);
        when(licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean)).thenReturn(true);
        boolean b = licenseInfoController.updateRedisLicenseThreshold(licenseFeignBean);
        Assert.assertTrue(b);
    }

    @Test
    public void synchronousLicenseThreshold() {
        LicenseThresholdFeignBean licenseThresholdFeignBean = mock(LicenseThresholdFeignBean.class);
        when(licenseInfoService.synchronousLicenseThreshold(licenseThresholdFeignBean)).thenReturn(true);
        boolean b = licenseInfoController.synchronousLicenseThreshold(licenseThresholdFeignBean);
        Assert.assertTrue(b);
    }
}