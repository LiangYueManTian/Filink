package com.fiberhome.filink.oss.wrapper;

import com.fiberhome.filink.oss.config.FdfsConfiguration;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *     FastDFS工具栏测试类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/7
 */
@RunWith(JMockit.class)
public class FdfsClientWrapperTest {
    /**
     * 测试对象
     */
    @Tested
    private FdfsClientWrapper clientWrapper;

    /**
     * 注入FastDFS工具
     */
    @Injectable
    private FastFileStorageClient storageClient;
    /**
     * 注入FastDFS工具
     */
    @Injectable
    private ThumbImageConfig thumbImageConfig;
    /**
     * 上传文件
     */
    private MultipartFile file;
    /**
     * FastDFS文件路径
     */
    private StorePath storePath;
    /**
     * 文件路径
     */
    private String url;
    /**
     * 文件流
     */
    private InputStream inputStream;
    /**
     * 文件扩展名
     */
    private String fileExtension;
    /**
     * FastDFS配置类
     */
    private FdfsConfiguration fdfsConfiguration;
    /**
     * 初始化
     */
    @Before
    public void setUp() throws IOException {
        file = new MockMultipartFile(
                "protocol.xml", //文件名
                "protocol.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[256] //文件流
        );
        storePath = new StorePath("group1", "M00/00/a.xml");
        url =  "group1/M00/00/a.xml";
        fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        inputStream = file.getInputStream();
        fdfsConfiguration = new FdfsConfiguration();
    }
    /**
     * uploadFile
     */
    @Test
    public void uploadFile() throws IOException {
        new Expectations() {
            {
                storageClient.uploadFile((InputStream) any, anyLong, anyString, null);
                result = storePath;
            }
        };
        String result = clientWrapper.uploadFile(file);
        Assert.assertEquals(url, result);
    }

    /**
     * uploadFileStream
     */
    @Test
    public void uploadFileStream() {
        new Expectations() {
            {
                storageClient.uploadFile((InputStream) any, anyLong, anyString, null);
                result = storePath;
            }
        };
        String result = clientWrapper.uploadFileStream(inputStream, file.getSize(), fileExtension);
        Assert.assertEquals(url, result);
    }

    /**
     * uploadFileImage
     */
    @Test
    public void uploadFileImage() {
        new Expectations() {
            {
                storageClient.uploadImageAndCrtThumbImage((InputStream) any, anyLong, anyString, null);
                result = storePath;
            }
        };
        String result = clientWrapper.uploadFileImage(inputStream, file.getSize(), fileExtension);
        Assert.assertEquals(url, result);
    }

    /**
     * deleteFile
     */
    @Test
    public void deleteFile() {
        String fileUrl = "";
        clientWrapper.deleteFile(fileUrl);
        fileUrl = "group";
        clientWrapper.deleteFile(fileUrl);
        clientWrapper.deleteFile(url);
    }

    /**
     * getThumbImagePath
     */
    @Test
    public void getThumbImagePath() {
        new Expectations() {
            {
                thumbImageConfig.getThumbImagePath(anyString);
                result =  url;
            }
        };
        String result = clientWrapper.getThumbImagePath(url);
        Assert.assertSame(result, url);
    }
    /**
     * downloadFile
     */
    @Test
    public void downloadFile() {
        String fileUrl = fdfsConfiguration.toString();
        byte[] result = clientWrapper.downloadFile(fileUrl);
        Assert.assertNull(result);
        byte[] bytes = new byte[4];
        new Expectations() {
            {
                storageClient.downloadFile(anyString, anyString, (DownloadByteArray) any);
                result = bytes;
            }
        };
        result = clientWrapper.downloadFile(url);
        Assert.assertSame(result, bytes);
    }
}
