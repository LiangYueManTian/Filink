package com.fiberhome.filink.dprotocol.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolFileBean;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolConstants;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.dao.DeviceProtocolDao;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.dprotocol.utils.HexUtil;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.FileBean;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.stationapi.bean.ProtocolDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
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
import org.springframework.web.multipart.MultipartFile;

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
     *自动注入OceanConnect服务远程调用
     */
    @Injectable
    private OceanConnectFeign oceanConnectFeign;
    /**
     *自动注入OneNet服务远程调用
     */
    @Injectable
    private OneNetFeign oneNetFeign;
    /**
     * 自动注入日志服务
     */
    @Injectable
    private LogProcess logProcess;
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;
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
        String file = "3C70726F746F636F6C3E0D0A202020203C686172647761726556657273696F6E3E312E373C2F686172647761726556657273696F6E3E0D0A202020203C736F66747761726556657273696F6E3E312E373C2F736F66747761726556657273696F6E3E0D0A3C2F70726F746F636F6C3E";
        byte[] fileBytes = HexUtil.hexStringToByte(file);
        deviceProtocolFileBean = new DeviceProtocolFileBean();
        deviceProtocolFileBean.setSize(40000L);
        deviceProtocolFileBean.setSizeI18n(deviceProtocolFileBean.getNameI18n());
        deviceProtocolFileBean.setNameI18n(deviceProtocolFileBean.getSizeI18n());
        multiFile = new MockMultipartFile(
                "newLock.mapper", //文件名
                "newLock.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                fileBytes //文件流
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
     * checkDeviceProtocolNameRepeat
     */
    @Test
    public void checkDeviceProtocolNameRepeatTest() {
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByName(deviceProtocol);
                result = "skhakuhgdkuahwg";
            }
        };
        try {
            deviceProtocolService.checkDeviceProtocolNameRepeat(deviceProtocol);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolNameExistException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByName(deviceProtocol);
                result = null;
            }
        };
        Result result = deviceProtocolService.checkDeviceProtocolNameRepeat(deviceProtocol);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

    /**
     * queryFileLimit
     */
    @Test
    public void queryFileLimitTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_SIZE);
                result = "文件大小不能超过${size}KB";

                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_NAME);
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
        String protocolName = "人井";
        MultipartFile file = new MockMultipartFile(
                "protocol.mapper", //文件名
                "protocolsssssssssssssssssssssssssssssssssssssssssssss.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[0] //文件流
        );
        //文件内容为空
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolVersionException.class);
        }
        file = new MockMultipartFile(
                "protocol.mapper", //文件名
                "protocolsssssssssssssssssssssssssssssssssssssssssssss.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[59] //文件流
        );
        //文件名称过长
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileNameException.class);
        }
        file = new MockMultipartFile(
                "protocol.mapper", //文件名
                "protocol.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[400000000] //文件流
        );
        //文件内容过大
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileSizeException.class);
        }
        file = new MockMultipartFile(
                "protocol.mapper", //文件名
                "protocol.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[96] //文件流
        );
        //文件格式不正确
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileFormatException.class);
        }
        String str = "3C70726F746F636F6C3E0D0A202020203C686172647761726556657273696F6E3E312E373C2F686172647761726556657273696F6E3E0D0A202020203C736F66747761726556657273696F6E3E3C2F736F66747761726556657273696F6E3E0D0A3C2F70726F746F636F6C3E";
        file = new MockMultipartFile(
                "protocol.xml", //文件名
                "protocol.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                HexUtil.hexStringToByte(str) //文件流
        );
        //文件软硬件版本错误
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolVersionException.class);
        }
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_EXIST);
                result = "设施协议文件硬件版本：${hardwareVersion}软件版本：${softwareVersion}已存在";

                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = "sahidihawiuodhw";
            }
        };
        //文件已存在
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileExistException.class);
        }
        new Expectations() {
            {
                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = "";

                fdfsFeign.uploadFile(multiFile);
                result = "";
            }
        };
        //文件上传异常
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileUploadException.class);
        }
        new Expectations() {
            {
                fdfsFeign.uploadFile(multiFile);
                result = "group1/M00/00/08/rBrpT10HVpGAXfr0AACMmMG6z8U475.xml";

                stationFeign.addProtocol((ProtocolDto) any);
                result = false;
            }
        };
        //station服务异常
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolAddException.class);
        }
        new Expectations() {
            {
                stationFeign.addProtocol((ProtocolDto) any);
                result = true;

                deviceProtocolDao.addDeviceProtocol((DeviceProtocol) any);
                result = 0;
            }
        };
        //数据库操作异常
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolAddException.class);
        }
        new Expectations(I18nUtils.class) {
            {
                deviceProtocolDao.addDeviceProtocol((DeviceProtocol) any);
                result = 1;

                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_ADD_SUCCESS);
                result = "新增设施协议成功";
            }
        };
        Result result = deviceProtocolService.addDeviceProtocol(protocolName, multiFile);;
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
        new Expectations(HexUtil.class) {
            {
                HexUtil.bytesToHexString((byte[]) any);
                result = new IOException();
            }
        };
        //文件格式异常
        try {
            deviceProtocolService.addDeviceProtocol(protocolName, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolFileFormatException.class);
        }
    }

    /**
     *updateDeviceProtocol
     */
    @Test
    public void updateDeviceProtocolTest() {
        new Expectations() {
            {
                deviceProtocolDao.getDeviceProtocolById(deviceProtocol.getProtocolId());
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
                deviceProtocolDao.getDeviceProtocolById(deviceProtocol.getProtocolId());
                result = deviceProtocol;

                deviceProtocolDao.queryDeviceProtocolByName((DeviceProtocol) any);
                result = null;

                deviceProtocolDao.queryDeviceProtocolByVersion((DeviceProtocol) any);
                result = null;

                fdfsFeign.uploadFile((MultipartFile) any);
                result = "ssssssssssss";

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
        new Expectations(RequestInfoUtils.class) {
            {
                stationFeign.updateProtocol((ProtocolDto) any);
                result = true;

                RequestInfoUtils.getUserId();
                result = "1";

                deviceProtocolDao.updateById(deviceProtocol);
                result = 0;
            }
        };
        //数据库操作异常
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol, multiFile);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolUpdateException.class);
        }
        new Expectations(I18nUtils.class) {
            {
                deviceProtocolDao.updateById(deviceProtocol);
                result = 1;

                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS);
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
                deviceProtocolDao.getDeviceProtocolById(deviceProtocol.getProtocolId());
                result = deviceProtocol;

                deviceProtocolDao.queryDeviceProtocolByName((DeviceProtocol) any);
                result = null;

                RequestInfoUtils.getUserId();
                result = "1";

                deviceProtocolDao.updateById(deviceProtocol);
                result = 0;
            }
        };
        try {
            deviceProtocolService.updateDeviceProtocol(deviceProtocol);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDeviceProtocolUpdateException.class);
        }
        new Expectations(RequestInfoUtils.class, I18nUtils.class) {
            {
                deviceProtocolDao.updateById(deviceProtocol);
                result = 1;

                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS);
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
                result = 0;

                RequestInfoUtils.getUserId();
                result = "1";
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
                deviceProtocolDao.batchDeleteDeviceProtocolList((List<DeviceProtocol>) any, (String) any);
                result = fileBeans.size();

                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_SUCCESS);
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
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        new Expectations() {
            {
                deviceProtocolDao.selectPage((Page<DeviceProtocol>) any, (Wrapper<DeviceProtocol>)any);
                result = new ArrayList<>();
            }
        };new Expectations() {
            {
                deviceProtocolDao.selectCount((Wrapper<DeviceProtocol>) any);
                result = 0;
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
        String url = "/img/bd_logo1.png";
        String basePath = "https://www.baidu.com";
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setHardwareVersion("1.1");
        deviceProtocol.setSoftwareVersion("1.1");
        new Expectations() {
            {
                deviceProtocolDao.queryUrlByVersion(deviceProtocol);
                result = url;

                fdfsFeign.getBasePath();
                result = basePath;
            }
        };
        String result = deviceProtocolService.queryProtocol(deviceProtocol);
        Assert.assertTrue(result.length() > 0);
    }
}
