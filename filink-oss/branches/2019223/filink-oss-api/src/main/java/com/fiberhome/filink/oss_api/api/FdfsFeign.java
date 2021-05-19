package com.fiberhome.filink.oss_api.api;

import com.fiberhome.filink.oss_api.bean.DeviceProtocolDto;
import com.fiberhome.filink.oss_api.fallback.FdfsFeignFallback;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * <p>
 *     文件服务远程调用
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/28
 */
@FeignClient(name = "filink-oss-server", fallback = FdfsFeignFallback.class, configuration = FdfsFeign.MultipartSupportConfig.class)
public interface FdfsFeign {
    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @PostMapping(value = "/oss/uploadFile", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart(value = "file") MultipartFile file);

    /**
     * 逻辑批量删除设施协议文件
     *
     * @param deviceProtocolDtos 设施协议List
     * @return 文件路径List
     */
    @PostMapping("/oss/deleteFilesLogic")
    List<DeviceProtocolDto> deleteFilesLogic(@RequestBody List<DeviceProtocolDto> deviceProtocolDtos);

    /**
     * 批量物理删除文件
     *
     * @param fileUrls 文件路径List
     */
    @PostMapping("/oss/deleteFilesPhy")
    void deleteFilesPhy(@RequestBody List<String> fileUrls);

    /**
     * 更改feign编码格式
     */
    @Configuration
    class MultipartSupportConfig {
        /**
         * 注入工厂ObjectFactory<HttpMessageConverters>
         */
        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;
        /**
         * 更改feign编码格式，支持上传文件
         */
        @Bean
        public Encoder feignEncoder() {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }
    }
}
