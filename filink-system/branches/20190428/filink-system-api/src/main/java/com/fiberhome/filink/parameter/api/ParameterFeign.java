package com.fiberhome.filink.parameter.api;


import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.parameter.fallback.ParameterFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * 系统参数远程调用
 * @author chaofang@wistronits.com
 * @since 2019-04-16
 */
@FeignClient(name = "filink-system-server", path = "/systemParameter", fallback = ParameterFeignFallback.class)
public interface ParameterFeign {
    /**
     * 查询短信服务配置
     * @return 结果
     */
    @GetMapping("/queryMessage")
    AliAccessKey queryMessage();
    /**
     * 查询邮箱服务配置
     * @return 结果
     */
    @GetMapping("/queryMail")
    AliAccessKey queryMail();
    /**
     * 查询推送服务配置
     * @return 结果
     */
    @GetMapping("/queryMobilePush")
    AliAccessKey queryMobilePush();
    /**
     * 查询ftp服务配置
     * @return 结果
     */
    @GetMapping("/queryFtpSettings")
    FtpSettings queryFtpSettings();
}
