package com.fiberhome.filink.cprotocol.api;

import com.fiberhome.filink.cprotocol.bean.ProtocolField;
import com.fiberhome.filink.cprotocol.fallback.ConnectProtocolFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 *  通信协议API
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/31
 */
@FeignClient(name = "filink-system-server", path = "/protocol", fallback =ConnectProtocolFallback.class)
public interface ConnectProtocolFeign {
    /**
     * 查询协议内容
     *
     * @param type 协议类型
     * @return 协议内容
     */
    @GetMapping("/feign/{type}")
     ProtocolField queryProtocol(@PathVariable("type") String type);
}
