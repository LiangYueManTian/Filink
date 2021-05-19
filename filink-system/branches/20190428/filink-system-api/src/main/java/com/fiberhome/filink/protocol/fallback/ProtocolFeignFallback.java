package com.fiberhome.filink.protocol.fallback;

import com.fiberhome.filink.protocol.api.ProtocolFeign;
import com.fiberhome.filink.protocol.bean.ProtocolVersionBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 设施协议远程调用
 * @author chaofang@wistronits.com
 * @since 2019-02-14
 */
@Slf4j
@Component
public class ProtocolFeignFallback implements ProtocolFeign {

    /**
     * 查询缓存设施协议文件信息
     *
     * @param protocolVersionBean 设施协议文件信息
     * @return 设施协议文件信息
     */
    @Override
    public String queryProtocol(ProtocolVersionBean protocolVersionBean) {
        log.info("query device protocol hystrix*****************");
        return null;
    }
}
