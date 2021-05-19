package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.cprotocol.api.ConnectProtocolFeign;
import com.fiberhome.filink.cprotocol.bean.ProtocolEnum;
import com.fiberhome.filink.cprotocol.bean.ProtocolField;
import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * <p>
 * OneNet域名获取工具类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Slf4j
@Component
public class HostUtil {

    @Autowired
    private ConnectProtocolFeign protocolFeign;

    /**
     * 获取移动平台地址
     *
     * @return 平台地址
     */
    public String getOneNetHost() {
        //从redis查询https配置
        String host = (String) RedisUtils.get(RedisKey.ADDRESS_KEY);
        //如果缓存为空,查询系统服务
        if (StringUtils.isEmpty(host)) {
            host = queryOneNetHost();
        }
        RedisUtils.set(RedisKey.ADDRESS_KEY, host);
        return host;
    }
    /**
     * 查询http地址
     *
     * @return http地址
     */
    private String queryOneNetHost() {
        //查询https地址
        ProtocolField protocolField = protocolFeign.queryProtocol(ProtocolEnum.HTTP_PROTOCOL.getType());
        if (protocolField == null) {
            log.error("http config is null>>>>>>>>>>>");
            return null;
        }
        String ip = protocolField.getIp();
        if (StringUtils.isEmpty(ip)) {
            log.error("http ip is null>>>>>>>>>>>");
            return null;
        }
        return ip;
    }
}
