package com.fiberhome.filink.dprotocol.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolFileBean;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.dprotocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.dprotocol.bean.xmlBean.header.RequestHeader;
import com.fiberhome.filink.dprotocol.dao.DeviceProtocolDao;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.oss_api.bean.DeviceProtocolDto;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
    /**自动注入 DAO*/
    @Injectable
    private DeviceProtocolFileBean deviceProtocolFileBean;
    /**
     * 自动注入文件服务远程调用
     */
    @Injectable
    private FdfsFeign fdfsFeign;

    /**
     * 自动注入日志服务
     */
    @Injectable
    private LogProcess logProcess;
    /**
     * Mock RequestContextHolder
     */
    @Mocked
    private RequestContextHolder requestContextHolder;
    /**
     * Mock ServletRequestAttributes
     */
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
        deviceProtocolFileBean = new DeviceProtocolFileBean();
        deviceProtocolFileBean.setSize(40000L);
    }

    @Test
    public void addDeviceProtocol() throws IOException {
        String protocolName = "        ";
        File fileN = ResourceUtils.getFile("classpath:junit/protocol.xml");
        MultipartFile file = new MockMultipartFile(
                "protocol.xml", //文件名
                "protocol.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //文件内容为空
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileContentException.class);
        }
        fileN = ResourceUtils.getFile("classpath:junit/t.png");
        file = new MockMultipartFile(
                "t.png", //文件名
                "thasssssssssssssssssssssssssssssssssssssssssssshhhss.png", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileNameException.class);
        }
        file = new MockMultipartFile(
                "t.png", //文件名
                "t.png", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileSizeException.class);
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
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolNameException.class);
        }
        protocolName = "  ccccc  ";
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByName((String) any);
                result = "ssssssssssss";
            }
        };
        //设施名称已存在
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolNameExistException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByName((String) any);
                result = null;
            }
        };
        //文件格式不正确
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileFormatException.class);
        }
        fileN = ResourceUtils.getFile("classpath:junit/newLock1.xml");
        file = new MockMultipartFile(
                "newLock1.xml", //文件名
                "newLock1.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //文件内容不正确
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileContentException.class);
        }
        fileN = ResourceUtils.getFile("classpath:junit/newLock2.xml");
        file = new MockMultipartFile(
                "newLock2.xml", //文件名
                "newLock2.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        //文件内容软硬件版本不正确
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileContentException.class);
        }
        fileN = ResourceUtils.getFile("classpath:junit/newLock.xml");
        file = new MockMultipartFile(
                "newLock.xml", //文件名
                "newLock.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        new Expectations(I18nUtils.class) {
            {
                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = "ssssssssssss";

                I18nUtils.getString(anyString);
                result = "设施协议文件软件版本：${hardwareVersion}，软件版本：${softwareVersion}已存在";
            }
        };
        //软硬件版本重复
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileExistException.class);
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
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileUploadException.class);
        }
        new Expectations() {
            {
                fdfsFeign.uploadFile((MultipartFile) any);
                result = "ssssssssssssssssssssss";

                deviceProtocolDao.addDeviceProtocol((DeviceProtocol) any);
                result = 0;

            }
        };
        //数据库操作异常
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolAddException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.addDeviceProtocol((DeviceProtocol) any);
                result = 1;
            }
        };
        //成功
        Result result = deviceProtocolService.addDeviceProtocol(protocolName, file);
        Assert.assertTrue(result.getCode() == 0);
    }

    @Test
    public void updateDeviceProtocol() throws IOException {
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolId("1111111");
        deviceProtocol.setProtocolName("ssssssssss");
        File fileN = ResourceUtils.getFile("classpath:junit/newLock.xml");
        MultipartFile file = new MockMultipartFile(
                "newLock.xml", //文件名
                "newLock.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolById((String) any);
                result = null;
            }
        };
        //设施协议不存在
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolNotExistException.class);
        }
        DeviceProtocol deviceProtocolDb = new DeviceProtocol();
        deviceProtocolDb.setProtocolId("111111111");
        deviceProtocolDb.setFileDownloadUrl("ssssssssssssssss");
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolById((String) any);
                result = deviceProtocolDb;

                deviceProtocolDao.queryDeviceProtocolByName((String) any);
                result = "ssssssssssss";
            }
        };
        //设施名称已存在
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolNameExistException.class);
        }
        FiLinkProtocolBean protocolBean = new FiLinkProtocolBean();
        protocolBean.setHardwareVersion("1.1");
        protocolBean.setSoftwareVersion("1.1");
        new Expectations(I18nUtils.class) {
            {
                deviceProtocolDao.queryDeviceProtocolByName((String) any);
                result = null;

                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = "ssssssssssss";

                I18nUtils.getString((String) any);
                result = "设施协议文件软件版本：${hardwareVersion}，软件版本：${softwareVersion}已存在";
            }
        };
        //软硬件版本重复
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolFileExistException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = null;

                fdfsFeign.uploadFile((MultipartFile) any);
                result = "ssssssssssss";

                deviceProtocolDao.updateById((DeviceProtocol) any);
                result = 0;
            }
        };
        //数据库操作异常
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol, file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolUpdateException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.updateById((DeviceProtocol) any);
                result = 1;
            }
        };
        //更新成功
        Result result = deviceProtocolService.updateDeviceProtocol(deviceProtocol, file);
        Assert.assertTrue(result.getCode() == 0);
    }

    @Test
    public void updateDeviceProtocolName() {
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolId("1111111");
        deviceProtocol.setProtocolName("ssssssssss");
        DeviceProtocol deviceProtocolDb = new DeviceProtocol();
        deviceProtocolDb.setProtocolId("111111111");
        deviceProtocolDb.setFileDownloadUrl("ssssssssssssssss");
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolById((String) any);
                result = deviceProtocolDb;

                deviceProtocolDao.queryDeviceProtocolByName((String) any);
                result = null;

                deviceProtocolDao.updateById((DeviceProtocol) any);
                result = 0;
            }
        };
        //数据库操作异常
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolUpdateException.class);
        }
        new Expectations(I18nUtils.class) {
            {
                deviceProtocolDao.updateById((DeviceProtocol) any);
                result = 1;

                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS);
                result = "更新设施协议成功";
            }
        };
        //更新成功
        Result result = deviceProtocolService.updateDeviceProtocol(deviceProtocol);
        Assert.assertTrue(result.getCode() == 0);
    }

    @Test
    public void deleteDeviceProtocol() {
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
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolNotExistException.class);
        }
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolId("1111");
        deviceProtocol.setFileDownloadUrl("sssssssssss");
        deviceProtocolDbs.add(deviceProtocol);
        List<DeviceProtocolDto> deviceProtocolDtos = new ArrayList<>();
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolListById((List<String>) any);
                result = deviceProtocolDbs;

                fdfsFeign.deleteFilesLogic((List<DeviceProtocolDto>) any);
                result = deviceProtocolDtos;
            }
        };
        //删除文件失败
        try {
            deviceProtocolService.deleteDeviceProtocol(protocolIds);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolDeleteException.class);
        }
        DeviceProtocolDto deviceProtocolDto = new DeviceProtocolDto();
        deviceProtocolDto.setProtocolId("1001");
        deviceProtocolDto.setFileDownloadUrl("sssssssssssss");
        deviceProtocolDtos.add(deviceProtocolDto);
        new Expectations() {
            {
                fdfsFeign.deleteFilesLogic((List<DeviceProtocolDto>) any);
                result = deviceProtocolDtos;

                deviceProtocolDao.batchDeleteDeviceProtocolList((List<DeviceProtocol>) any, (String) any);
                result = 0;
            }
        };
        //数据库操作异常
        try {
            deviceProtocolService.deleteDeviceProtocol(protocolIds);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceProtocolDeleteException.class);
        }
        new Expectations(RedisUtils.class, I18nUtils.class) {
            {
                deviceProtocolDao.batchDeleteDeviceProtocolList((List<DeviceProtocol>) any, (String) any);
                result = 1;

                RedisUtils.remove(anyString);


                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_SUCCESS);
                result = "删除设施协议成功";
            }
        };
        //删除成功
        Result result = deviceProtocolService.deleteDeviceProtocol(protocolIds);
        Assert.assertTrue(result.getCode() == 0);
    }

    @Test
    public void queryDeviceProtocolList() {
        List<DeviceProtocol> deviceProtocols = new ArrayList<>();
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setFileDownloadUrl("ssss");
        deviceProtocols.add(deviceProtocol);
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolList();
                result = deviceProtocols;
            }
        };
        Result result = deviceProtocolService.queryDeviceProtocolList();
        Assert.assertTrue(result.getCode() == 0);
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolList();
                result = null;
            }
        };
        result = deviceProtocolService.queryDeviceProtocolList();
        Assert.assertTrue(result.getCode() == 0);
    }

    @Test
    public void queryProtocolXmlBean() {
        ProtocolVersionBean protocolVersionBean = new ProtocolVersionBean();
        FiLinkProtocolBean fiLinkProtocolBean = new FiLinkProtocolBean();
        protocolVersionBean.setHardwareVersion("1.1");
        protocolVersionBean.setSoftwareVersion("1.1");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get((String) any);
                result = fiLinkProtocolBean;
            }
        };
        FiLinkProtocolBean result = deviceProtocolService.queryProtocolXmlBean(protocolVersionBean);
        Assert.assertTrue(result == fiLinkProtocolBean);
    }

    /**
     * queryFileLimit
     */
    @Test
    public void queryFileLimit() {
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
     * DeviceProtocol
     */
    @Test
    public void getBean() {
        String testStr = "test";
        Date testDate = new Date();
        RequestHeader requestHeader = new RequestHeader();
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolId(testStr);
        deviceProtocol.setProtocolName(testStr);
        deviceProtocol.setFileDownloadUrl(testStr);
        deviceProtocol.setSoftwareVersion(testStr);
        deviceProtocol.setHardwareVersion(testStr);
        deviceProtocol.setFileName(testStr);
        deviceProtocol.setFileLength(testStr);
        deviceProtocol.setIsDeleted(testStr);
        deviceProtocol.setCreateUser(testStr);
        deviceProtocol.setUpdateUser(testStr);
        deviceProtocol.setCreateTime(testDate);
        deviceProtocol.setUpdateTime(testDate)
        ;
        deviceProtocol.getProtocolId();
        deviceProtocol.getProtocolName();
        deviceProtocol.getFileDownloadUrl();
        deviceProtocol.getSoftwareVersion();
        deviceProtocol.getHardwareVersion();
        deviceProtocol.getFileName();
        deviceProtocol.getFileLength();
        deviceProtocol.getIsDeleted();
        deviceProtocol.getCreateUser();
        deviceProtocol.getUpdateUser();
        deviceProtocol.getCreateTime();
        deviceProtocol.getUpdateTime();
        deviceProtocol.toString();
    }

}
