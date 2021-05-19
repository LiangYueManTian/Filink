package com.fiberhome.filink.oss.wrapper;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * FastDFS客户端包装类
 *
 * @author chaofang@wistronits.com
 * @since 2019/1/22
 */
@Component
public class FdfsClientWrapper {
    /**
     * 自动注入FastDFS Client
     */
    @Autowired
    private FastFileStorageClient storageClient;
    /**
     *自动注入FastDFS 图片处理
     */
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**访问路径通用组名*/
    private static final String GROUP = "group";


    /**
     * MultipartFile文件上传文件
     *
     * @param file 文件流MultipartFile
     * @return 存储路径
     * @throws IOException 流转换异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtension, null);
        return getResAccessUrl(storePath);
    }

    /**
     * 输入流形式的文件上传
     *
     * @param is            文件输入流
     * @param size          文件大小
     * @param fileExtension 文件扩展名
     * @return 存储路径
     */
    public String uploadFileStream(InputStream is, long size, String fileExtension) {
        StorePath path = storageClient.uploadFile(is, size, fileExtension, null);
        return getResAccessUrl(path);
    }

    /**
     * 图片上传并产生缩略图
     *
     * @param is            文件输入流
     * @param size          文件大小
     * @param fileExtension 文件扩展名
     * @return 存储路径
     */
    public String uploadFileImage(InputStream is, long size, String fileExtension){
        StorePath path = storageClient.uploadImageAndCrtThumbImage(is, size, fileExtension, null);
        return getResAccessUrl(path);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件路径
     */
    public void deleteFile(String fileUrl) {
        StorePath storePath = parseFromUrl(fileUrl);
        if (ObjectUtils.isEmpty(storePath)) {
            return;
        }
        storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
    }


    /**
     * 返回文件路径
     *
     * @param storePath FastDFS存储路径
     * @return 文件存储路径
     */
    private String getResAccessUrl(StorePath storePath) {
        return storePath.getFullPath();
    }

    /**
     * 返回缩略图路径
     *
     * @param masterFilename 原图路径
     * @return 缩略图路径
     */
    public String getThumbImagePath(String masterFilename) {
        return thumbImageConfig.getThumbImagePath(masterFilename);
    }

    /**
     * 下载文件
     * 返回文件字节流
     *
     * @param fileUrl 文件URL
     * @return 文件字节流
     */
    public byte[] downloadFile(String fileUrl) {
        StorePath storePath = parseFromUrl(fileUrl);
        if (ObjectUtils.isEmpty(storePath)) {
            return null;
        }
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        return storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), downloadByteArray);
    }

    /**
     * 解析url文件路径
     *
     * @param fileUrl 文件路径
     * @return FastDFS文件路径
     */
    private StorePath parseFromUrl(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return null;
        }
        int groupStartPos = fileUrl.indexOf(GROUP);
        if (groupStartPos == -1) {
            return null;
        }
        String groupAndPath = fileUrl.substring(groupStartPos);
        int pos = groupAndPath.indexOf("/");
        if (pos > 0 && pos != groupAndPath.length() - 1) {
            String group = groupAndPath.substring(0, pos);
            String path = groupAndPath.substring(pos + 1);
            return new StorePath(group, path);
        }
        return null;
    }
}
