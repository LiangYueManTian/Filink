package com.fiberhome.filink.picture.utils;

import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import com.fiberhome.filink.picture.constant.TaskStatusConstant;
import com.fiberhome.filink.picture.resp.DevicePicResp;
import com.fiberhome.filink.redis.RedisUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;


@RunWith(JMockit.class)
public class DevicePictureUtilsTests {

    /**
     * 测试对象 handleFile
     */
    @Tested
    private HandleFile handleFile;

    /**
     * 临时文件的路径
     */
    @Injectable
    private String listExcelFilePath;

    /**
     * 上传文件服务类实体
     */
    @Injectable
    private UploadFile uploadFile;

    /**
     * 导出服务feign
     */
    @Injectable
    private ExportFeign exportFeign;

    /**
     * 远程调用oss服务
     */
    @Injectable
    private FdfsFeign fdfsFeign;

    /**
     * Mock redisUtils
     */
    @Mocked
    private RedisUtils redisUtils;

    /**
     * Mock File
     */
    @Mocked
    private File file;

    /**
     * 初始化
     */
    @Before
    public void setUp() {

    }

    /**
     * deleteDir
     * */
    @Test
    public void deleteDir() throws Exception{
        MultipartFile multipartFile = new MockMultipartFile(
                "s.png",
                "s.png",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new byte[23]
        );

        file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        handleFile.deleteDir(file);
    }

    /**
     * updateProgressByZip
     * */
    @Test
    public void updateProgressByZip() throws Exception{
        ExportDto exportDto = new ExportDto();
        exportDto.setTaskId("dfsafdsafdsdafd");
        MultipartFile multipartFile = new MockMultipartFile(
                "s.png",
                "s.png",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new byte[23]
        );

        //预计执行方法
        new Expectations() {
            {
                RedisUtils.get(anyString);
                result = TaskStatusConstant.START;
            }
        };

        handleFile.updateProgressByZip(exportDto);
    }

    /**
     * generatedPic
     * */
    /*@Test
    public void generatedPic() throws Exception {
        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        Export export = new Export();
        export.setTaskFolderPath("fdsafdsafdsafdsafd");
        export.setExcelFolderName("fdsafdsafdsafdsafd");
        ExportDto exportDto = new ExportDto();
        exportDto.setFileNum(9);

        MultipartFile multipartFile = new MockMultipartFile(
                "s.png",
                "s.png",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new byte[23]
        );

        file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);

        //预计执行方法
        new Expectations() {
            {
                exportFeign.changeTaskStatusToUnusual(anyString);
                result = true;
            }
        };
        handleFile.changeTaskStatusToUnusual("fdsafdsafdafd");

        //预计执行方法
        new Expectations() {
            {
                exportFeign.updateTaskFileNumById((ExportDto) any);
                result = true;
            }
        };
        Boolean flag = handleFile.updateTaskFileNumById(exportDto,"fdsafdsafds");
        Assert.assertTrue(flag);

        //预计执行方法
        new Expectations() {
            {
                file.exists();
                result = true;
                uploadFile.uploadFile((File)any);
                result = "fdsafdsfas";
                RedisUtils.get(anyString);
                result = "1";
            }
        };
        handleFile.uploadToFastDfs("fdsafdsafdafd",export,exportDto);

        //预计执行方法
        new Expectations() {
            {
                handleFile.generatePicInfo(anyString,anyString,anyString,export,(ExportDto)any);
                result = true;
                fdfsFeign.getBasePath();
                result = "fdsafdsfsdafd";
            }
        };

        //判断是否正确
        Boolean b1 = handleFile.generatedPic(devicePicRespList,export,exportDto);
        Assert.assertTrue(b1);
    }*/

}

