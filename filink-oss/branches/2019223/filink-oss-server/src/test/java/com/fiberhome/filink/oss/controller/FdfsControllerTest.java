package com.fiberhome.filink.oss.controller;

import com.fiberhome.filink.oss.bean.DeviceProtocolDto;
import com.fiberhome.filink.oss.exception.FilinkFdfsParamException;
import com.fiberhome.filink.oss.service.FdfsService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     FastDFS文件服务器前端实现类测试类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/26
 */
@RunWith(MockitoJUnitRunner.class)
public class FdfsControllerTest {

    /**
     * 测试对象 FdfsController
     */
    @Tested
    private FdfsController fdfsController;

    /**自动注入FastDFS文件服务类*/
    @Injectable
    private FdfsService fdfsService;

    /**
     * uploadFile
     */
    @Test
    public void uploadFile() throws IOException {
        File fileN = ResourceUtils.getFile("classpath:junit/newLock.xml");
        MultipartFile file = null;
        try {
            fdfsController.uploadFile(file);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
        file = new MockMultipartFile(
                "protocol.xml", //文件名
                "protocol.xml", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new FileInputStream(fileN) //文件流
        );
        String url = "ssss";
        new Expectations() {
            {
                fdfsService.uploadFile((MultipartFile) any);
                result = url;
            }
        };
        String result = fdfsController.uploadFile(file);
        Assert.assertTrue(result.equals(url));
    }

    /**
     * deleteFilesLogic
     */
    @Test
    public void deleteFilesLogic() {
        List<DeviceProtocolDto> deviceProtocolDtos = new ArrayList<>();
        try {
            fdfsController.deleteFilesLogic(deviceProtocolDtos);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
        DeviceProtocolDto deviceProtocolDto = new DeviceProtocolDto();
        deviceProtocolDtos.add(deviceProtocolDto);
        new Expectations() {
            {
                fdfsService.deleteFilesLogic((List<DeviceProtocolDto>) any);
                result = deviceProtocolDtos;
            }
        };
        List<DeviceProtocolDto> result = fdfsController.deleteFilesLogic(deviceProtocolDtos);
        Assert.assertTrue(result == deviceProtocolDtos);
    }

    /**
     * deleteFilesPhy
     */
    @Test
    public void deleteFilesPhy() {
        List<String> fileUrls = new ArrayList<>();
        try {
            fdfsController.deleteFilesPhy(fileUrls);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkFdfsParamException.class);
        }
        fileUrls.add("http://10.5.24.142:80/group1/M00/00/");
        fdfsController.deleteFilesPhy(fileUrls);
    }
}
