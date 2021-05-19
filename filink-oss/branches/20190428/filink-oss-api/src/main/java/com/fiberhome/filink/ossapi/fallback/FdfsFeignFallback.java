package com.fiberhome.filink.ossapi.fallback;


import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.FileBean;
import com.fiberhome.filink.ossapi.bean.ImageUploadBean;
import com.fiberhome.filink.ossapi.bean.ImageUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *     调用失败熔断
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/28
 */
@Slf4j
@Component
public class FdfsFeignFallback implements FdfsFeign {

    /**
     * 获取fastdfs访问前缀IP端口地址
     *
     * @return 基础路径
     */
    @Override
    public String getBasePath() {
        log.info("oss server get fastdfs path failed************");
        return null;
    }

    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @Override
    public String uploadFile(MultipartFile file) {
        log.info("upload file to oss server exception**********");
        return null;
    }

    /**
     * 批量上传图片
     *
     * @param imageUploadMap 上传图片Map
     * @return 图片路径
     */
    @Override
    public Map<String, ImageUrl> uploadFileImage(Map<String, ImageUploadBean> imageUploadMap) {
        log.info("upload file to oss server exception**********");
        return null;
    }

    /**
     * 逻辑批量删除设施协议文件
     *
     * @param fileBeans 文件路径list
     * @return 文件路径list
     */
    @Override
    public List<FileBean> deleteFilesLogic(List<FileBean> fileBeans) {
        log.info("delete files from oss server exception**********");
        return null;
    }

    /**
     * 批量物理删除文件
     *
     * @param fileUrls 文件路径list
     */
    @Override
    public void deleteFilesPhy(List<String> fileUrls) {
        log.info("delete files from oss server exception**********");
    }

}
