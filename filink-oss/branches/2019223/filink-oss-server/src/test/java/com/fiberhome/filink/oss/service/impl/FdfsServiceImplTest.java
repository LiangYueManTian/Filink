package com.fiberhome.filink.oss.service.impl;

import com.fiberhome.filink.oss.bean.DeviceProtocolDto;
import com.fiberhome.filink.oss.exception.FilinkFdfsParamException;
import com.fiberhome.filink.oss.wrapper.FdfsClientWrapper;
import com.github.tobato.fastdfs.exception.FdfsServerException;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     FastDFS文件服务器服务实现类测试类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/26
 */
@RunWith(JMockit.class)
public class FdfsServiceImplTest {

    /**
     * 测试对象
     */
    @Tested
    private FdfsServiceImpl fdfsService;
    /**
     * 注入FastDFS工具
     */
    @Injectable
    private FdfsClientWrapper fdfsClientWrapper;
    /**
     * 上传文件
     */
    private MultipartFile file;
    /**
     * 文件路径
     */
    private String url;

    /**
     * 初始化
     */
    @Before
    public void setUp() throws IOException {
        File fileN = ResourceUtils.getFile("classpath:junit/newLock.xml");
        file = new MockMultipartFile(
                "protocol.xml", //文件名
                "protocol.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        url = "sssss";
    }

    /**
     * uploadFile
     */
    @Test
    public void uploadFile() throws IOException {
        MultipartFile file1 = null;
        try {
            fdfsService.uploadFile(file1);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
        new Expectations() {
            {
                fdfsClientWrapper.uploadFile((MultipartFile) any);
                result = new IOException();
            }
        };
        try {
            fdfsService.uploadFile(file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
        new Expectations() {
            {
                fdfsClientWrapper.uploadFile((MultipartFile) any);
                result = url;
            }
        };
        String result = fdfsService.uploadFile(file);
        Assert.assertTrue(result == url);
    }

    /**
     * deleteFilesLogic
     */
    @Test
    public void deleteFilesLogic() {
        List<DeviceProtocolDto> deviceProtocolDtos = new ArrayList<>();
        try {
            fdfsService.deleteFilesLogic(deviceProtocolDtos);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
        DeviceProtocolDto deviceProtocolDto = new DeviceProtocolDto();
        deviceProtocolDto.setProtocolId("111111111");
        deviceProtocolDto.setFileDownloadUrl(deviceProtocolDto.getProtocolId());
        deviceProtocolDtos.add(deviceProtocolDto);
        deviceProtocolDtos.add(deviceProtocolDto);
        deviceProtocolDtos.add(deviceProtocolDto);
        deviceProtocolDtos.add(deviceProtocolDto);
        byte[] bytes = new byte[4];
        new Expectations() {
            {
                fdfsClientWrapper.downloadFile(anyString);
                returns(bytes, bytes, bytes);
                result = FdfsServerException.byCode(0);
                fdfsClientWrapper.uploadFileStream((InputStream) any, anyLong, anyString);
                returns(url, url, url);
                fdfsClientWrapper.deleteFile(anyString);
                returns(url, url);
                result = FdfsServerException.byCode(0);
            }
        };
        List<DeviceProtocolDto> result = fdfsService.deleteFilesLogic(deviceProtocolDtos);
        Assert.assertTrue(result.size() == 0);
    }

    /**
     * deleteFilesPhy
     */
    @Test
    public void deleteFilesPhy() {
        List<String> fileUrls = new ArrayList<>();
        try {
            fdfsService.deleteFilesPhy(fileUrls);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
    }

    /**
     * deleteFileLogic
     */
    @Test
    public void deleteFileLogic() {
        byte[] bytes = new byte[4];
        new Expectations() {
            {
                fdfsClientWrapper.downloadFile(anyString);
                result = null;
            }
        };
        try {
            fdfsService.deleteFileLogic(url);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
        new Expectations() {
            {
                fdfsClientWrapper.downloadFile(anyString);
                result = bytes;

                fdfsClientWrapper.uploadFileStream((InputStream) any, anyLong, anyString);
                result = url;
            }
        };
        String result = fdfsService.deleteFileLogic(url);
        Assert.assertTrue(result == url);
    }
}
