package com.fiberhome.filink.parameter.fallback;


import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
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
        log.info("查询短信服务配置调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 查询邮箱服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMail() {
        log.info("查询邮箱服务配置调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 查询推送服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMobilePush() {
        log.info("查询推送服务配置调用熔断》》》》》》》》》》");
        return null;
    }
}
