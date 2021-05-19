package com.fiberhome.filink.dprotocol.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolFileBean;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.dao.DeviceProtocolDao;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.dprotocol.utils.DeviceProtocolConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.oss_api.bean.FileBean;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.stationapi.bean.ProtocolDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
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
import java.util.Date;
import java.util.List;

/**
 * <P>
 *     设施协议服务类测试类
 * </P>
 * @author chaofang@wistronits.com
 * @since 2019/2/2
 */
@RunWith(JMockit.class)
public class DeviceProtocolServiceImplTest {
    /**测试对象 DeviceProtocolServiceImpl*/
    @Tested
    private DeviceProtocolServiceImpl deviceProtocolService;
    /**自动注入 DAO*/
    @Injectable
    private DeviceProtocolDao deviceProtocolDao;
    /**
     * 自动注入文件服务远程调用
     */
    @Injectable
    private FdfsFeign fdfsFeign;
    /**
     *自动注入station服务远程调用
     */
    @Injectable
    private FiLinkStationFeign stationFeign;
    /**
     * 自动注入文件限制
     */
    @Injectable
    private DeviceProtocolFileBean deviceProtocolFileBean;

    /**
     * 自动注入日志服务
     */
    @Injectable
    private LogProcess logProcess;

    /**MultipartFile*/
    private MultipartFile multiFile;
    /**DeviceProtocol*/
    private DeviceProtocol deviceProtocol;
    /**DeviceProtocolConstants*/
    private DeviceProtocolConstants deviceProtocolConstants;

    /**
     * 初始化
     */
    @Before
    public void setUp() throws IOException {
        deviceProtocolFileBean = new DeviceProtocolFileBean();
        deviceProtocolFileBean.setSize(40000L);

        File fileMulti = ResourceUtils.getFile("classpath:junit/newLock.mapper");
        multiFile = new MockMultipartFile(
                "newLock.mapper", //文件名
                "newLock.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileMulti) //文件流
        );
        deviceProtocolConstants = new DeviceProtocolConstants();
        String testStr = deviceProtocolConstants.toString();
        Date testDate = new Date();
        deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolId("1111111");
        deviceProtocol.setProtocolName("ssssssssss");
        deviceProtocol.setFileName(testStr);
        deviceProtocol.setFileLength(deviceProtocol.getFileName());
        deviceProtocol.setIsDeleted(deviceProtocol.getFileLength());
        deviceProtocol.setCreateUser(deviceProtocol.getIsDeleted());
        deviceProtocol.setUpdateUser(deviceProtocol.getCreateUser());
        deviceProtocol.setFileName(deviceProtocol.getUpdateUser());
        deviceProtocol.setCreateTime(testDate);
        deviceProtocol.setUpdateTime(deviceProtocol.getCreateTime());
        deviceProtocol.setCreateTime(deviceProtocol.getUpdateTime());

    }

    /**
     * queryFileLimit
     */
    @Test
    public void queryFileLimitTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_SIZE);
                result = "文件大小不能超过${size}KB";

                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_NAME);
                result = "文件名称长度不能超过${nameLength}位";
            }
        };
        Result result = deviceProtocolService.queryFileLimit();
        Assert.assertEquals(0, result.getCode());
    }

    /**
     * addDeviceProtocol
     * @throws IOException IO
     */
    @Test
    public void addDeviceProtocolTest() throws IOException {
        String protocolName = "        ";
        File fileN = ResourceUtils.getFile("classpath:junit/protocol.mapper");
        MultipartFile file = new MockMultipartFile(
                "protocol.mapper", //文件名
                "protocol.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //文件内容为空
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolVersionException.class);
        }
        fileN = ResourceUtils.getFile("classpath:junit/t.png");
        file = new MockMultipartFile(
                "t.png", //文件名
                "5554444444444444454444444444444444444444.png", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //文件名称过长
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileNameException.class);
        }
        file = new MockMultipartFile(
                "t.png", //文件名
                "t.png", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //文件内容过大
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileSizeException.class);
        }
        fileN = ResourceUtils.getFile("classpath:junit/tt.png");
        file = new MockMultipartFile(
                "tt.png", //文件名
                "tt.png", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //名称格式不符合规范
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileSizeException.class);
        }
        protocolName = "  ccccc  ";
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByName((DeviceProtocol) any);
                result = "ssssssssssss";
            }
        };
        //设施名称已存在
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolNameExistException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByName((DeviceProtocol) any);
                result = null;
            }
        };
        //文件格式不正确
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileFormatException.class);
        }
        fileN = ResourceUtils.getFile("classpath:junit/newLock1.mapper");
        file = new MockMultipartFile(
                "newLock1.mapper", //文件名
                "newLock1.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //文件内容不正确
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolVersionException.class);
        }
        new Expectations(I18nUtils.class) {
            {
                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = "ssssssssssss";

                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_EXIST);
                result = "设施协议文件软件版本：${hardwareVersion}，软件版本：${softwareVersion}已存在";
            }
        };
        //软硬件版本重复
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileExistException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = null;

                fdfsFeign.uploadFile((MultipartFile) any);
                result = null;
            }
        };
        //上传文件失败
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileUploadException.class);
        }
        new Expectations() {
            {
                fdfsFeign.uploadFile((MultipartFile) any);
                result = "ssssssssssssssssssssss";

                stationFeign.addProtocol((ProtocolDto) any);
                result = false;
            }
        };
        //远程调用异常
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolAddException.class);
        }
        new Expectations(RequestInfoUtils.class, I18nUtils.class) {
            {
                stationFeign.addProtocol((ProtocolDto) any);
                result = true;

                RequestInfoUtils.getUserId();
                result = "1";

                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_ADD_SUCCESS);
                result = "新增设施协议成功";
            }
        };
        //成功
        Result result = deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        Assert.assertEquals(0, result.getCode());
    }

    /**
     *updateDeviceProtocol
     */
    @Test
    public void updateDeviceProtocolTest() {
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolById((String) any);
                result = null;
            }
        };
        //设施协议不存在
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolNotExistException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolById((String) any);
                result = deviceProtocol;
            }
            {
                deviceProtocolDao.queryDeviceProtocolByName((DeviceProtocol) any);
                result = null;

                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = null;

                fdfsFeign.uploadFile((MultipartFile) any);
                result = "ssssssssssss";
            }
            {
                stationFeign.updateProtocol((ProtocolDto) any);
                result = false;
            }
        };
        //远程调用异常
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolUpdateException.class);
        }
        new Expectations(RequestInfoUtils.class, I18nUtils.class) {
            {
                stationFeign.updateProtocol((ProtocolDto) any);
                result = true;

                RequestInfoUtils.getUserId();
                result = "1";

                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS);
                result = "修改设施协议成功";
            }
        };
        //更新成功
        Result result = deviceProtocolService.updateDeviceProtocol(deviceProtocol, multiFile);
        Assert.assertEquals(0, result.getCode());
    }

    /**
     * updateDeviceProtocolName
     */
    @Test
    public void updateDeviceProtocolNameTest() {
        new Expectations(RequestInfoUtils.class, I18nUtils.class) {
            {
                deviceProtocolDao.getDeviceProtocolById((String) any);
                result = deviceProtocol;
            }
            {
                deviceProtocolDao.queryDeviceProtocolByName((DeviceProtocol) any);
                result = null;
                RequestInfoUtils.getUserId();
                result = "1";

                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS);
                result = "修改设施协议成功";
            }
        };
        //更新成功
        Result result = deviceProtocolService.updateDeviceProtocol(deviceProtocol);
        Assert.assertEquals(0, result.getCode());
    }

    /**
     * deleteDeviceProtocol
     */
    @Test
    public void deleteDeviceProtocolTest() {
        List<String> protocolIds = new ArrayList<>();
        protocolIds.add("1111");
        List<DeviceProtocol> deviceProtocolDbs = new ArrayList<>();
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolListById((List<String>) any);
                result = deviceProtocolDbs;
            }
        };
        //设施协议不存在
        try {
            deviceProtocolService.deleteDeviceProtocol(protocolIds);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolNotExistException.class);
        }
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolId("1111");
        deviceProtocol.setFileDownloadUrl("sssssssssss");
        deviceProtocolDbs.add(deviceProtocol);
        List<FileBean> fileBeans = new ArrayList<>();
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolListById((List<String>) any);
                result = deviceProtocolDbs;

                fdfsFeign.deleteFilesLogic((List<FileBean>) any);
                result = fileBeans;
            }
        };
        //删除文件失败
        try {
            deviceProtocolService.deleteDeviceProtocol(protocolIds);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileUploadException.class);
        }
        FileBean fileBean = new FileBean();
        fileBean.setFileId("1001");
        fileBean.setFileUrl("sssssssssssss");
        fileBeans.add(fileBean);
        new Expectations(RequestInfoUtils.class) {
            {
                fdfsFeign.deleteFilesLogic((List<FileBean>) any);
                result = fileBeans;

                deviceProtocolDao.batchDeleteDeviceProtocolList((List<DeviceProtocol>) any, (String) any);
                result = fileBeans.size();

                RequestInfoUtils.getUserId();
                result = "1";

                stationFeign.deleteProtocol((List<ProtocolDto>) any);
                result = false;
            }
        };
        //远程调用异常
        try {
            deviceProtocolService.deleteDeviceProtocol(protocolIds);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolDeleteException.class);
        }
        new Expectations(I18nUtils.class) {
            {
                stationFeign.deleteProtocol((List<ProtocolDto>) any);
                result = true;

                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_SUCCESS);
                result = "删除设施协议成功";
            }
        };
        //删除成功
        Result result = deviceProtocolService.deleteDeviceProtocol(protocolIds);
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * queryDeviceProtocolList
     */
    @Test
    public void queryDeviceProtocolListTest() {
        QueryCondition<DeviceProtocol> queryCondition = new QueryCondition<>();
        List<FilterCondition> filterConditions = new ArrayList<>();
        queryCondition.setFilterConditions(filterConditions);
        List<DeviceProtocol> deviceProtocols = new ArrayList<>();
        new Expectations() {
            {
                deviceProtocolDao.selectList((Wrapper<DeviceProtocol>) any);
                result = deviceProtocols;
            }
        };
        Result result = deviceProtocolService.queryDeviceProtocolList(queryCondition);
        Assert.assertEquals(0, result.getCode());
    }

    /**
     * queryProtocol
     */
    @Test
    public void queryProtocolTest() {
        String url = "http://10.5.24.142:80/group1/M00/00/00/CgUYjlxJt0yASRKDAABRI5QY7ZQ431.PNG";
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setHardwareVersion("1.1");
        deviceProtocol.setSoftwareVersion("1.1");
        new Expectations() {
            {
                deviceProtocolDao.queryUrlByVersion(deviceProtocol);
                result = url;
            }
        };
        String result = deviceProtocolService.queryProtocol(deviceProtocol);
        Assert.assertTrue(result.length() > 0);
    }
}
