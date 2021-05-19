package com.fiberhome.filink.license.service.impl;

import com.fiberhome.filink.about.service.AboutService;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.bean.LicenseInfo;
import com.fiberhome.filink.license.bean.LicenseThreshold;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.constant.Constant;
import com.fiberhome.filink.license.constant.LicenseParameter;
import com.fiberhome.filink.license.constant.LicenseResultCode;
import com.fiberhome.filink.license.constant.LicenseType;
import com.fiberhome.filink.license.dao.LicenseInfoDao;
import com.fiberhome.filink.license.enums.OperationTarget;
import com.fiberhome.filink.license.enums.OperationWay;
import com.fiberhome.filink.license.exception.FilinkLicenseException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.UserCount;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * icenseInfo 服务测试类
 *
 * @Author: zl
 * @Date: 2019/3/28 15:43
 * @Description: com.fiberhome.filink.license.service.impl
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class LicenseInfoServiceImplTest {

    @InjectMocks
    private LicenseInfoServiceImpl licenseInfoService;

    @Mock
    private FdfsFeign fdfsFeign;

    @Mock
    private LicenseInfoDao licenseDao;

    @Mock
    private LogProcess logProcess;

    @Mock
    private DeviceFeign deviceFeign;

    @Mock
    private UserFeign userFeign;

    @Mock
    private AboutService aboutService;

    @Test
    public void uploadLicense() throws Exception {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = "demo";
            }
        };
        //参数为空
        Result result = licenseInfoService.uploadLicense(null);
        Assert.assertTrue(result.getCode() == LicenseResultCode.ILLEGAL_LICENSE_FILE);

        MultipartFile mockFile = mock(MultipartFile.class);
        //反射修改属性值
        Field field = licenseInfoService.getClass().getDeclaredField("maxFileSize");
        field.setAccessible(true);
        field.set(licenseInfoService, 1048576L);

        //测试文件体积过大
        when(mockFile.getSize()).thenReturn(1048576L + 100);
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.LICENSE_FILE_TOO_LARGE);

        //恢复文件大小
        when(mockFile.getSize()).thenReturn(1000L);
        when(mockFile.getBytes()).thenReturn(null);
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.ILLEGAL_LICENSE_FILE);

        String xmlContent = "error test";
        when(mockFile.getBytes()).thenReturn(xmlContent.getBytes());
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.ILLEGAL_LICENSE_FILE);

        //校验License对象的属性值不为空
        xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<License>\n" +
                "<TryRemark>Y</TryRemark>\n" +
                "<BeginTime>2018-12-0</BeginTime>\n" +
                "</License>";
        when(mockFile.getBytes()).thenReturn(xmlContent.getBytes());
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.ILLEGAL_LICENSE_FILE);

        //校验BeginTime或EndTime格式
        xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<License>\n" +
                "<TryRemark>Y</TryRemark>\n" +
                "<BeginTime>2018-12-0</BeginTime>\n" +
                "<EndTime>2020-02-20</EndTime>\n" +
                "<MaxUserNum>150</MaxUserNum>\n" +
                "<MaxOnline>200</MaxOnline>\n" +
                "<MaxDeviceNum>300000</MaxDeviceNum>\n" +
                "</License>";
        when(mockFile.getBytes()).thenReturn(xmlContent.getBytes());
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.ILLEGAL_LICENSE_FILE);

        //MaxDeviceNum为数字
        xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<License>\n" +
                "<TryRemark>Y</TryRemark>\n" +
                "<BeginTime>2018-12-12</BeginTime>\n" +
                "<EndTime>2020-02-20</EndTime>\n" +
                "<MaxUserNum>150</MaxUserNum>\n" +
                "<MaxOnline>200</MaxOnline>\n" +
                "<MaxDeviceNum>a</MaxDeviceNum>\n" +
                "</License>";
        when(mockFile.getBytes()).thenReturn(xmlContent.getBytes());
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.ILLEGAL_LICENSE_FILE);

        //校验TryRemark为Y或N
        xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<License>\n" +
                "<TryRemark>E</TryRemark>\n" +
                "<BeginTime>2018-12-12</BeginTime>\n" +
                "<EndTime>2020-02-20</EndTime>\n" +
                "<MaxUserNum>150</MaxUserNum>\n" +
                "<MaxOnline>200</MaxOnline>\n" +
                "<MaxDeviceNum>300000</MaxDeviceNum>\n" +
                "</License>";
        when(mockFile.getBytes()).thenReturn(xmlContent.getBytes());
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.ILLEGAL_LICENSE_FILE);

        //恢复xml内容
        xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<License>\n" +
                "<TryRemark>Y</TryRemark>\n" +
                "<BeginTime>2018-12-12</BeginTime>\n" +
                "<EndTime>2020-02-20</EndTime>\n" +
                "<MaxUserNum>150</MaxUserNum>\n" +
                "<MaxOnline>200</MaxOnline>\n" +
                "<MaxDeviceNum>300000</MaxDeviceNum>\n" +
                "</License>";
        when(mockFile.getBytes()).thenReturn(xmlContent.getBytes());

        // 存入缓存异常
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyString);
                result = new Exception("exception");
            }
        };
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == LicenseResultCode.FAIL);

        //恢复缓存
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyString);
                result = null;
            }
        };
        when(fdfsFeign.uploadFile(mockFile)).thenReturn("file_path");

        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "userId";
            }
        };
        when(licenseDao.findDefaultLicense()).thenReturn(null);
        when(logProcess.generateAddLogToCallParam("1")).thenReturn(new AddLogBean());
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        //licenseInfo不为空
        LicenseInfo licenseInfo = new LicenseInfo();
        licenseInfo.setLicenseId("licenseId");
        when(licenseDao.findDefaultLicense()).thenReturn(licenseInfo);
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        //保存非试用
        xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<License>\n" +
                "<TryRemark>N</TryRemark>\n" +
                "<BeginTime>2018-12-12</BeginTime>\n" +
                "<EndTime>2020-02-20</EndTime>\n" +
                "<MaxUserNum>150</MaxUserNum>\n" +
                "<MaxOnline>200</MaxOnline>\n" +
                "<MaxDeviceNum>300000</MaxDeviceNum>\n" +
                "</License>";
        when(mockFile.getBytes()).thenReturn(xmlContent.getBytes());
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        when(licenseDao.findNonDefaultLicense()).thenReturn(licenseInfo);
        result = licenseInfoService.uploadLicense(mockFile);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void refreshLicenseThreshold() {
        UserCount userCount = new UserCount();
        userCount.setOnlineUserNumber(50);
        userCount.setUserAccountNumber(50);
        when(userFeign.queryUserNumber()).thenReturn(userCount);
        when(deviceFeign.queryCurrentDeviceCount()).thenReturn(100);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyInt);
            }
        };
        LicenseThreshold licenseThreshold = licenseInfoService.refreshLicenseThreshold();
        Assert.assertNotNull(licenseThreshold);
    }

    @Test
    public void getLicenseDetail() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = "demo";
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(anyString, anyString);
                result = null;
            }
        };
        when(licenseDao.findNonDefaultLicense()).thenReturn(null);
        when(licenseDao.findDefaultLicense()).thenReturn(null);
        Result result = licenseInfoService.getLicenseDetail();
        Assert.assertTrue(result.getCode() == LicenseResultCode.FAIL);

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(anyString, anyString);
                result = "license1";
            }
        };
        result = licenseInfoService.getLicenseDetail();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        //redis没有查询到数据，查询Threshold存入redis
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(anyString, anyString);
                result = null;
            }
        };
        LicenseInfo licenseInfo = new LicenseInfo();
        licenseInfo.setPath("/a");
        when(licenseDao.findNonDefaultLicense()).thenReturn(licenseInfo);
        when(fdfsFeign.getBasePath()).thenReturn("");
        result = licenseInfoService.getLicenseDetail();
        Assert.assertTrue(result.getCode() == LicenseResultCode.FAIL);

        licenseInfo.setPath("file:///" + this.getClass().getResource("/").getPath() + "license.xml");
        // 存入缓存异常
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyString);
                result = new Exception("exception");
            }
        };
        result = licenseInfoService.getLicenseDetail();
        Assert.assertTrue(result.getCode() == LicenseResultCode.FAIL);

        // 恢复缓存
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyString);
                result = null;
            }
        };
        result = licenseInfoService.getLicenseDetail();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        //文件解析错误
        licenseInfo.setPath("file:///" + this.getClass().getResource("/").getPath() + "license-document-error.xml");
        result = licenseInfoService.getLicenseDetail();
        Assert.assertTrue(result.getCode() == LicenseResultCode.FAIL);
    }

//    @Test
//    public void getCurrentLicense() {
//    }

    @Test
    public void updateLicenseToRedis() {
    }

    @Test
    public void validateLicenseTime() {
        boolean b = licenseInfoService.validateLicenseTime();
        Assert.assertTrue(!b);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(anyString, anyString);
                result = format;
            }
        };
        b = licenseInfoService.validateLicenseTime();
        Assert.assertTrue(!b);

        getCurrentLicense("license.xml");
        b = licenseInfoService.validateLicenseTime();
        Assert.assertTrue(b);
    }

    @Test
    public void updateRedisLicenseThreshold() throws Exception {
        getCurrentLicense("license.xml");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyInt);
                result = null;
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.lockWithTimeout(anyString, anyInt, anyInt);
                result = null;
            }
        };
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = "demo";
            }
        };
        LicenseFeignBean licenseFeignBean = new LicenseFeignBean(1, OperationTarget.DEVICE, OperationWay.ADD);
        try {
            licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
            Assert.fail();
        } catch (FilinkLicenseException e) {
            Assert.assertTrue(e.getMessage() == "demo");
        }
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.lockWithTimeout(anyString, anyInt, anyInt);
                result = "lockIdentifier";
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.releaseLock(anyString, anyString);
            }
        };
        try {
            licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
            Assert.fail();
        } catch (FilinkLicenseException e) {
            Assert.assertTrue(e.getMessage() == "demo");
        }

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(Constant.LICENSE, LicenseParameter.TRY_REMARK);
                result = "Y";
            }

            {
                RedisUtils.hGet(Constant.LICENSE, LicenseParameter.BEGIN_TIME);
                result = "2018-12-12";
            }

            {
                RedisUtils.hGet(Constant.LICENSE, LicenseParameter.END_TIME);
                result = "2020-01-01";
            }

            {
                RedisUtils.hGet(Constant.LICENSE, LicenseParameter.MAX_USER_NUM);
                result = "200";
            }

            {
                RedisUtils.hGet(Constant.LICENSE, LicenseParameter.MAX_ONLINE);
                result = "200";
            }

            {
                RedisUtils.hGet(Constant.LICENSE, LicenseParameter.MAX_DEVICE_NUM);
                result = "300000";
            }

            {
                RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM);
                result = "a";
            }

            {
                RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM);
                result = 200;
            }

            {
                RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_ONLINE);
                result = 200;
            }
        };
        try {
            licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
            Assert.fail();
        } catch (FilinkLicenseException e) {
            Assert.assertTrue(e.getMessage() == "demo");
        }

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM);
                result = 1.1;
            }
        };
        try {
            licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
            Assert.fail();
        } catch (FilinkLicenseException e) {
            Assert.assertTrue(e.getMessage() == "demo");
        }

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM);
                result = "100";
            }
        };
        boolean b = licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
        Assert.assertTrue(b);

        licenseFeignBean.setOperationWay(OperationWay.DELETE);
        licenseFeignBean.setOperationTarget(OperationTarget.ONLINE);
        b = licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
        Assert.assertTrue(b);

        licenseFeignBean.setOperationWay(OperationWay.ADD);
        licenseFeignBean.setOperationTarget(OperationTarget.USER);
        b = licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
        Assert.assertTrue(!b);

    }

    @Test
    public void synchronousLicenseThreshold() {
        LicenseThresholdFeignBean licenseThresholdFeignBean = new LicenseThresholdFeignBean();
        boolean b = licenseInfoService.synchronousLicenseThreshold(licenseThresholdFeignBean);
        Assert.assertTrue(!b);

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyInt);
                result = null;
            }
        };
        licenseThresholdFeignBean.setNum(100);
        licenseThresholdFeignBean.setName(LicenseType.USER);
        b = licenseInfoService.synchronousLicenseThreshold(licenseThresholdFeignBean);
        Assert.assertTrue(b);

        licenseThresholdFeignBean.setName(LicenseType.DEVICE);
        b = licenseInfoService.synchronousLicenseThreshold(licenseThresholdFeignBean);
        Assert.assertTrue(b);

        licenseThresholdFeignBean.setName(LicenseType.ONLINE);
        b = licenseInfoService.synchronousLicenseThreshold(licenseThresholdFeignBean);
        Assert.assertTrue(b);

        licenseThresholdFeignBean.setName("notExisted");
        b = licenseInfoService.synchronousLicenseThreshold(licenseThresholdFeignBean);
        Assert.assertTrue(!b);
    }

    private void getCurrentLicense(String path) {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(anyString, anyString);
                result = null;
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, anyString);
                result = null;
            }
        };
        LicenseInfo licenseInfo = new LicenseInfo();
        licenseInfo.setPath("file:///" + this.getClass().getResource("/").getPath() + path);
        when(licenseDao.findNonDefaultLicense()).thenReturn(licenseInfo);
        when(fdfsFeign.getBasePath()).thenReturn("");
    }
}