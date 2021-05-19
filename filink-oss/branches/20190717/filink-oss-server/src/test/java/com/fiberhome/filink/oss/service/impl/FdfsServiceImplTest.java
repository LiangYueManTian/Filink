package com.fiberhome.filink.oss.service.impl;

import com.fiberhome.filink.oss.bean.FileBean;
import com.fiberhome.filink.oss.bean.ImageUploadBean;
import com.fiberhome.filink.oss.bean.ImageUrl;
import com.fiberhome.filink.oss.exception.FilinkFdfsFileException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * getBasePath
     */
    @Test
    public void getBasePathTest() {
        String basePath = fdfsService.getBasePath();
        Assert.assertEquals(basePath, "${fdfs.basePath}");
    }

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
        new Expectations() {
            {
                fdfsClientWrapper.uploadFile((MultipartFile) any);
                result = new IOException();
            }
        };
        try {
            fdfsService.uploadFile(file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsFileException.class);
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

    @Test
    public void uploadFileImage() {
        Map<String, ImageUploadBean> imageUploadMap = new HashMap<>();
        ImageUploadBean imageUploadBean = new ImageUploadBean();
        imageUploadBean.setFileHexData("534663453435485742857425742222222857428574852745827");
        imageUploadBean.setFileExtension("png");
        imageUploadMap.put("1", imageUploadBean);
        new Expectations() {
            {
                fdfsClientWrapper.uploadFileImage((InputStream) any, anyLong, anyString);
                result = url;
                fdfsClientWrapper.getThumbImagePath(anyString);
                result = url;
            }
        };
        Map<String, ImageUrl> result = fdfsService.uploadFileImage(imageUploadMap);
        Assert.assertEquals(result.size(), imageUploadMap.size());
    }

    /**
     * deleteFilesLogic
     */
    @Test
    public void deleteFilesLogic() {
        List<FileBean> fileBeans = new ArrayList<>();
        FileBean fileBean = new FileBean();
        fileBean.setFileId("111111111");
        fileBean.setFileUrl(fileBean.getFileId());
        fileBeans.add(fileBean);
        fileBeans.add(fileBean);
        fileBeans.add(fileBean);
        fileBeans.add(fileBean);
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
        List<FileBean> result = fdfsService.deleteFilesLogic(fileBeans);
        Assert.assertTrue(result.size() == 0);
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
