package com.fiberhome.filink.oss.controller;


import com.fiberhome.filink.oss.bean.DeviceProtocolDto;
import com.fiberhome.filink.oss.exception.FilinkFdfsParamException;
import com.fiberhome.filink.oss.service.FdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * <p>
 *     FastDFS文件服务器前端实现类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/26
 */
@RestController
@RequestMapping("/oss")
public class FdfsController {

    /**自动注入FastDFS文件服务类*/
    @Autowired
    private FdfsService fdfsService;

    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @PostMapping(value = "/uploadFile", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart(value = "file") MultipartFile file){
        //校验参数
        if (file == null) {
            throw new FilinkFdfsParamException();
        }
        return fdfsService.uploadFile(file);
    }


    /**
     * 逻辑批量删除设施协议文件
     *
     * @param deviceProtocolDtos 设施协议List
     * @return 文件路径list
     */
    @PostMapping("/deleteFilesLogic")
    public List<DeviceProtocolDto> deleteFilesLogic(@RequestBody List<DeviceProtocolDto> deviceProtocolDtos) {
        //校验传入参数
        if (deviceProtocolDtos == null || deviceProtocolDtos.size() == 0) {
            throw new FilinkFdfsParamException();
        }
        return fdfsService.deleteFilesLogic(deviceProtocolDtos);
    }

    /**
     * 物理删除文件
     *
     * @param fileUrls 文件路径list
     */
    @PostMapping("/deleteFilesPhy")
    public void deleteFilesPhy(@RequestBody List<String> fileUrls) {
        //校验传入参数
        if (fileUrls == null || fileUrls.size() == 0) {
            throw new FilinkFdfsParamException();
        }
        fdfsService.deleteFilesPhy(fileUrls);
    }
}
