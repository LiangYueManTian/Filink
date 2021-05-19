package com.fiberhome.filink.cprotocol.fallback;

import com.fiberhome.filink.cprotocol.api.ConnectProtocolFeign;
import com.fiberhome.filink.cprotocol.bean.ProtocolField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 通信协议API实现
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/31
 */
@Slf4j
@Component
public class ConnectConnectProtocolFallback implements ConnectProtocolFeign {
    /**
     * 查询协议内容
     *
     * @param type 协议类型
     * @return 协议内容
     */
    @Override
    public ProtocolField queryProtocol(String type) {
        log.error("通信协议调用失败");
        return null;
    }
}
