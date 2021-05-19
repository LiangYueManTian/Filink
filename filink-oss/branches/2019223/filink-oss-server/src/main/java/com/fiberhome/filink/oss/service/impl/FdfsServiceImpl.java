package com.fiberhome.filink.oss.service.impl;

import com.fiberhome.filink.oss.bean.DeviceProtocolDto;
import com.fiberhome.filink.oss.exception.FilinkFdfsParamException;
import com.fiberhome.filink.oss.service.FdfsService;
import com.fiberhome.filink.oss.wrapper.FdfsClientWrapper;
import com.github.tobato.fastdfs.exception.FdfsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 上传File文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @Override
    public String uploadFile(MultipartFile file){
        String url = null;
        //上传文件
        try {
            url = fdfsClientWrapper.uploadFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FilinkFdfsParamException();
        }
        return url;
    }


    /**
     * 逻辑批量删除设施协议文件
     *
     * @param deviceProtocolDtos 设施协议List
     * @return 设施协议List
     */
    @Override
    public List<DeviceProtocolDto> deleteFilesLogic(List<DeviceProtocolDto> deviceProtocolDtos) {
        List<String> urls = new ArrayList<>();
        //删除文件(修改为back后缀)
        for (DeviceProtocolDto deviceProtocolDto : deviceProtocolDtos) {
            try {
                //下载文件
                byte[] bytes = fdfsClientWrapper.downloadFile(deviceProtocolDto.getFileDownloadUrl());
                if(bytes != null) {
                    //上传文件改为.back
                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                    String url = fdfsClientWrapper.uploadFileStream(stream, bytes.length, DELETE_FILE_EXTENSION);
                    urls.add(url);
                    deviceProtocolDto.setFileDownloadUrl(url);
                }
            } catch (FdfsServerException fdfsEx) {
                //某文件节点或文件不存在，撤销
                log.warn(deviceProtocolDto.getFileDownloadUrl() + fdfsEx.getMessage());
                fdfsEx.printStackTrace();
                //删除已逻辑删除修改为back后缀的文件
                deleteFilesPhy(urls);
                deviceProtocolDtos = new ArrayList<>();
                break;
            }
        }
        return deviceProtocolDtos;
    }


    /**
     * 物理批量删除文件
     *
     * @param fileUrls 文件路径List
     */
    @Override
    public void deleteFilesPhy(List<String> fileUrls) {
        //遍历删除文件
        for (String fileUrl : fileUrls) {
            //找不到文件异常，继续删除其他文件
            try {
                fdfsClientWrapper.deleteFile(fileUrl);
            } catch (FdfsServerException fdfsEx) {
                log.warn(fileUrl + fdfsEx.getMessage());
                fdfsEx.printStackTrace();
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
        //根据路径下载文件
        byte[] bytes = fdfsClientWrapper.downloadFile(fileUrl);
        //判断路径是否正确
        if(bytes == null) {
            throw new FilinkFdfsParamException();
        }
        //上传.back文件
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return fdfsClientWrapper.uploadFileStream(stream, bytes.length, DELETE_FILE_EXTENSION);
    }
}
