package com.fiberhome.filink.oss.service.impl;

import com.fiberhome.filink.oss.bean.FileBean;
import com.fiberhome.filink.oss.bean.ImageUploadBean;
import com.fiberhome.filink.oss.bean.ImageUrl;
import com.fiberhome.filink.oss.exception.FilinkFdfsFileException;
import com.fiberhome.filink.oss.exception.FilinkFdfsParamException;
import com.fiberhome.filink.oss.service.FdfsService;
import com.fiberhome.filink.oss.utils.HexUtil;
import com.fiberhome.filink.oss.wrapper.FdfsClientWrapper;
import com.github.tobato.fastdfs.exception.FdfsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     FastDFS文件服务器服务实现类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/26
 */
@Slf4j
@Service
public class FdfsServiceImpl implements FdfsService {
    /**
     * 自动注入FastDFS工具类
     */
    @Autowired
    private FdfsClientWrapper fdfsClientWrapper;

    /**删除文件扩展名*/
    private static final String DELETE_FILE_EXTENSION = "BACK";
    /**FastDFS访问IP端口部分路径*/
    @Value("${fdfs.basePath}")
    private String basePath;


    /**
     * 获取fastdfs访问前缀IP端口地址
     *
     * @return 基础路径
     */
    @Override
    public String getBasePath() {
        return basePath;
    }

    /**
     * 上传File文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @Override
    public String uploadFile(MultipartFile file){
        //上传文件
        try {
            return fdfsClientWrapper.uploadFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FilinkFdfsFileException();
        }
    }

    /**
     * 批量上传图片
     *
     * @param imageUploadMap 上传图片Map
     * @return 图片路径
     */
    @Override
    public Map<String, ImageUrl> uploadFileImage(Map<String, ImageUploadBean> imageUploadMap) {
        Map<String, ImageUrl> imageUrlMap = new HashMap<>(16);
        //遍历多文件Map
        for (Map.Entry<String, ImageUploadBean> imageUpload : imageUploadMap.entrySet()) {
            //将16进制字符串转换成字节数组
            ImageUploadBean imageUploadBean = imageUpload.getValue();
            byte[] bytes = HexUtil.hexStringToByte(imageUploadBean.getFileHexData());
            //字节数组构建文件流
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            //上传/图片
            String url = fdfsClientWrapper.uploadFileImage(stream, bytes.length, imageUploadBean.getFileExtension());
            //图片路径对象
            ImageUrl imageUrl = new ImageUrl();
            imageUrl.setOriginalUrl(url);
            imageUrl.setThumbUrl(fdfsClientWrapper.getThumbImagePath(url));
            imageUrlMap.put(imageUpload.getKey(), imageUrl);
        }
        return imageUrlMap;
    }
    /**
     * 逻辑批量删除设施协议文件
     *
     * @param fileBeans 设施协议List
     * @return 设施协议List
     */
    @Override
    public List<FileBean> deleteFilesLogic(List<FileBean> fileBeans) {
        List<String> urls = new ArrayList<>();
        //删除文件(修改为back后缀)
        for (FileBean fileBean : fileBeans) {
            try {
                byte[] bytes = fdfsClientWrapper.downloadFile(fileBean.getFileUrl());
                if(bytes != null) {
                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                    String url = fdfsClientWrapper.uploadFileStream(stream, bytes.length, DELETE_FILE_EXTENSION);
                    urls.add(url);
                    fileBean.setFileUrl(url);
                }
            } catch (FdfsServerException fdfsEx) {
                //某文件节点或文件不存在，撤销
                log.error("the url: {}, error:{}", fileBean.getFileUrl(), fdfsEx);
                //删除已逻辑删除修改为back后缀的文件
                deleteFilesPhy(urls);
                fileBeans = new ArrayList<>();
                break;
            }
        }
        return fileBeans;
    }
    /**
     * 物理批量删除文件
     *
     * @param fileUrls 文件路径List
     */
    @Override
    public void deleteFilesPhy(List<String> fileUrls) {
        //删除文件
        for (String fileUrl : fileUrls) {
            try {
                fdfsClientWrapper.deleteFile(fileUrl);
            } catch (FdfsServerException fdfsEx) {
                log.warn("the url: {}, error:{}", fileUrl, fdfsEx);
            }
        }
    }

    /**
     * 逻辑删除文件
     *
     * @param fileUrl 文件路径
     * @return 文件路径
     */
    @Override
    public String deleteFileLogic(String fileUrl) {
        byte[] bytes = fdfsClientWrapper.downloadFile(fileUrl);
        //判断路径是否正确
        if(bytes == null) {
            throw new FilinkFdfsParamException();
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return fdfsClientWrapper.uploadFileStream(stream, bytes.length, DELETE_FILE_EXTENSION);
    }
}
