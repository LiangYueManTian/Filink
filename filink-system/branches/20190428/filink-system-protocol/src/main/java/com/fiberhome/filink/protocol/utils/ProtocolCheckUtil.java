package com.fiberhome.filink.protocol.utils;

import com.fiberhome.filink.protocol.dto.ProtocolParams;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 通信协议  参数检验类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/28
 */
public class ProtocolCheckUtil {

    /**
     * 检验协议参数格式是否正确
     *
     * @param protocolParams
     * @return true 格式正确 false 格式错误
     */
    public static boolean checkProtocolParams(ProtocolParams protocolParams) {
        //检验ProtocolParams属性是否为空
        if (StringUtils.isEmpty(protocolParams) || StringUtils.isEmpty(protocolParams.getParamId()) || StringUtils.isEmpty(protocolParams.getParamType()) || StringUtils.isEmpty(protocolParams.getProtocolField())) {
            //为空，返回false
            return false;
        }
        String protocolType = protocolParams.getParamType();
        if (!checkProtocolName(protocolType)) {
            return false;
        }
        return true;
    }


    /**
     * 检查协议类型是否存在
     *
     * @param protocolName 协议类型
     * @return false -不存在 ，true -存在
     */
    public static boolean checkProtocolName(String protocolName) {

        if (StringUtils.isEmpty(protocolName)) {
            return false;
        }

        return ParamTypeRedisEnum.hasType(protocolName);


    }


}
