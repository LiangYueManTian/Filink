package com.fiberhome.filink.ossapi.upload;

import com.fiberhome.filink.ossapi.api.FdfsFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 *     上传文件
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/2/27
 */
@Slf4j
@Component
public class UploadFile {
    /**
     * 自动注入文件服务器远程调用
     */
    @Autowired
    private FdfsFeign fdfsFeign;
    /**
     * 上传File文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    public String uploadFile(File file) {
        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.TEXT_PLAIN_VALUE, true, file.getName());
        try (InputStream input = new FileInputStream(file); OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }
        MultipartFile multiFile = new CommonsMultipartFile(fileItem);
        return fdfsFeign.uploadFile(multiFile);
    }
}
