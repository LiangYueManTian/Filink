package com.fiberhome.filink.oss.service;

import com.fiberhome.filink.oss.bean.FileBean;
import com.fiberhome.filink.oss.bean.ImageUploadBean;
import com.fiberhome.filink.oss.bean.ImageUrl;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *     FastDFS文件服务器服务类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/26
 */
public interface FdfsService {
    /**
     * 上传图片调试
     * TODO 留作fastdfs出错时调试测试,以后删除
     * @return 图片路径
     */
    ImageUrl test(MultipartFile file);

    /**
     * 获取fastdfs访问前缀IP端口地址
     * @return 基础路径
     */
    String getBasePath();
    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    String uploadFile(MultipartFile file);
    /**
     * 批量上传图片
     *
     * @param imageUploadMap 上传图片Map
     * @return 图片路径
     */
    Map<String, ImageUrl> uploadFileImage(Map<String, ImageUploadBean> imageUploadMap);
    /**
     * 逻辑批量删除设施协议文件
     *
     * @param fileBeans 设施协议List
     * @return 设施协议List
     */
    List<FileBean> deleteFilesLogic(List<FileBean> fileBeans);
    /**
     * 物理批量删除文件
     *
     * @param fileUrls 文件路径List
     */
    void deleteFilesPhy(List<String> fileUrls);
    /**
     * 逻辑删除文件
     *
     * @param fileUrl 文件路径
     * @return 文件路径
     */
    String deleteFileLogic(String fileUrl);
}
