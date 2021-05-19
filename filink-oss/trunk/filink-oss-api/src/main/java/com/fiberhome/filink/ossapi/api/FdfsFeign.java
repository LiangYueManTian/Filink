package com.fiberhome.filink.ossapi.api;

import com.fiberhome.filink.ossapi.bean.FileBean;
import com.fiberhome.filink.ossapi.bean.ImageUploadBean;
import com.fiberhome.filink.ossapi.bean.ImageUrl;
import com.fiberhome.filink.ossapi.fallback.FdfsFeignFallback;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *     文件服务
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/28
 */
@FeignClient(name = "filink-oss-server", path = "/oss", fallback = FdfsFeignFallback.class, configuration = FdfsFeign.MultipartSupportConfig.class)
public interface FdfsFeign {
    /**
     * 获取fastdfs访问前缀IP端口地址
     * @return 基础路径
     */
    @GetMapping("/getBasePath")
    String getBasePath();
    /**
     * 上传MultipartFile文件
     *
     * @param file 上传文件
     * @return URL访问路径
     */
    @PostMapping(value = "/uploadFile", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart MultipartFile file);

    /**
     * 批量上传图片
     *
     * @param imageUploadMap 上传图片Map
     * @return 图片路径
     */
    @PostMapping("/uploadImage")
    Map<String, ImageUrl> uploadFileImage(@RequestBody Map<String, ImageUploadBean> imageUploadMap);

    /**
     * 逻辑批量删除设施协议文件
     *
     * @param fileBeans 设施协议List
     * @return 文件路径List
     */
    @PostMapping("/deleteFilesLogic")
    List<FileBean> deleteFilesLogic(@RequestBody List<FileBean> fileBeans);

    /**
     * 批量物理删除文件
     *
     * @param fileUrls 文件路径List
     */
    @PostMapping("/deleteFilesPhy")
    void deleteFilesPhy(@RequestBody List<String> fileUrls);

    @Configuration
    class MultipartSupportConfig {
        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        public Encoder feignEncoder() {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }
    }
}
