package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.cprotocol.api.ConnectProtocolFeign;
import com.fiberhome.filink.cprotocol.bean.ProtocolEnum;
import com.fiberhome.filink.cprotocol.bean.ProtocolField;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.exception.OceanException;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 平台地址工具类
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class IpUtil {

    @Autowired
    private ConnectProtocolFeign protocolFeign;

    /**
     * 获取电信平台地址
     *
     * @return 平台地址
     */
    public String getOceanConnectAddress() {
        //从redis查询https配置
        String oceanConnectAddress = (String) RedisUtils.get(RedisKey.ADDRESS_KEY);
        //如果缓存为空,查询系统服务
        if (StringUtils.isEmpty(oceanConnectAddress)) {
            oceanConnectAddress = queryOceanConnectAddress();
        }
        RedisUtils.set(RedisKey.ADDRESS_KEY, oceanConnectAddress);
        return oceanConnectAddress;
    }


    /**
     * 查询https地址
     *
     * @return https地址
     */
    private String queryOceanConnectAddress() {
        //查询https地址
        ProtocolField protocolField = protocolFeign.queryProtocol(ProtocolEnum.HTTPS_PROTOCOL.getType());
        if (protocolField == null) {
            String msg = "https config is null>>>>>>>>>>>";
            log.error(msg);
            throw new OceanException(msg);
        }
        String ip = protocolField.getIp();
        if (StringUtils.isEmpty(ip)) {
            String msg = "https ip or port is null>>>>>>>>>>>";
            log.error(msg);
            throw new OceanException(msg);
        }
        return ip;
    }

}
