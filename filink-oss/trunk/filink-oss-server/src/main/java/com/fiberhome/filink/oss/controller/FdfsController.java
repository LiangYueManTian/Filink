package com.fiberhome.filink.oss.controller;


import com.fiberhome.filink.oss.bean.FileBean;
import com.fiberhome.filink.oss.bean.ImageUploadBean;
import com.fiberhome.filink.oss.bean.ImageUrl;
import com.fiberhome.filink.oss.exception.FilinkFdfsParamException;
import com.fiberhome.filink.oss.service.FdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


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
     * 获取fastdfs访问前缀IP端口地址
     * @return 基础路径
     */
    @GetMapping("/getBasePath")
    public String getBasePath() {
        return fdfsService.getBasePath();
    }

    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart MultipartFile file){
        //校验参数
        if (file == null) {
            throw new FilinkFdfsParamException();
        }
        return fdfsService.uploadFile(file);
    }

    /**
     * 批量上传图片
     *
     * @param imageUploadMap 上传图片Map
     * @return 图片路径
     */
    @PostMapping("/uploadImage")
    public Map<String, ImageUrl> uploadFileImage(@RequestBody Map<String, ImageUploadBean> imageUploadMap) {
        //校验参数
        if (ObjectUtils.isEmpty(imageUploadMap)) {
            throw new FilinkFdfsParamException();
        }
        for (Map.Entry<String, ImageUploadBean> imageUpload : imageUploadMap.entrySet()) {
            if (StringUtils.isEmpty(imageUpload.getKey()) || ObjectUtils.isEmpty(imageUpload.getValue())
                    || imageUpload.getValue().check()) {
                throw new FilinkFdfsParamException();
            }
        }
        return fdfsService.uploadFileImage(imageUploadMap);
    }
    /**
     * 逻辑批量删除设施协议文件
     *
     * @param fileBeans 设施协议List
     * @return 文件路径list
     */
    @PostMapping("/deleteFilesLogic")
    public List<FileBean> deleteFilesLogic(@RequestBody List<FileBean> fileBeans) {
        //校验传入参数
        if (fileBeans == null || fileBeans.size() == 0) {
            throw new FilinkFdfsParamException();
        }
        return fdfsService.deleteFilesLogic(fileBeans);
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
