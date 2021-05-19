package com.fiberhome.filink.oss.service;

import com.fiberhome.filink.oss.bean.DeviceProtocolDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *     FastDFS文件服务器服务类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/26
 */
public interface FdfsService {
    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    String uploadFile(MultipartFile file);

    /**
     * 逻辑批量删除设施协议文件
     *
     * @param deviceProtocolDtos 设施协议List
     * @return 设施协议List
     */
    List<DeviceProtocolDto> deleteFilesLogic(List<DeviceProtocolDto> deviceProtocolDtos);
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
