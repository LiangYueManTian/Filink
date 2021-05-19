package com.fiberhome.filink.parameter.fallback;


import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 安全策略远程调用
 * @author chaofang@wistronits.com
 * @since 2019-04-10
 */
@Slf4j
@Component
public class ParameterFeignFallback implements ParameterFeign {

    /**
     * 查询短信服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMessage() {
        log.info("query short message settings failed***********");
        return null;
    }

    /**
     * 查询邮箱服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMail() {
        log.info("query email settings failed***********");
        return null;
    }

    /**
     * 查询推送服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMobilePush() {
        log.info("query mobile push settings failed***********");
        return null;
    }

    /**
     * 查询ftp服务配置
     *
     * @return 结果
     */
    @Override
    public FtpSettings queryFtpSettings() {
        log.info("query ftp settings failed***********");
        return null;
    }
}
