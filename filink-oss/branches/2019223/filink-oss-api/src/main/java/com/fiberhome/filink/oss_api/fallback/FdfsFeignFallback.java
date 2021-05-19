package com.fiberhome.filink.oss_api.fallback;


import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.oss_api.bean.DeviceProtocolDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *     文件服务调用失败熔断
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/28
 */
@Slf4j
@Component
public class FdfsFeignFallback implements FdfsFeign {

    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @Override
    public String uploadFile(MultipartFile file) {
        log.info("上传文件服务操作异常》》》》》》》》》》");
        return null;
    }

    /**
     * 逻辑批量删除设施协议文件
     *
     * @param fileUrls 文件路径list
     * @return 文件路径list
     */
    @Override
    public List<DeviceProtocolDto> deleteFilesLogic(List<DeviceProtocolDto> deviceProtocolDtos) {
        log.info("批量逻辑删除文件服务操作异常》》》》》》》》》》");
        return null;
    }

    /**
     * 批量物理删除文件
     *
     * @param fileUrls 文件路径list
     */
    @Override
    public void deleteFilesPhy(List<String> fileUrls) {
        log.info("批量物理删除文件服务操作异常》》》》》》》》》》");
    }

}
