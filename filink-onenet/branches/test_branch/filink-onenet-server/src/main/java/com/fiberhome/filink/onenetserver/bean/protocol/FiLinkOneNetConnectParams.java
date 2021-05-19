package com.fiberhome.filink.onenetserver.bean.protocol;

import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import lombok.Data;

import java.io.Serializable;

/**
 * filink udp请求帧参数
 * @author CongcaiYu
 */
@Data
public class FiLinkOneNetConnectParams extends AbstractReqParams implements Serializable {

    /**
     * 用户token
     */
    private String token;
    /**
     * 手机id
     */
    private String phoneId;
    /**
     * 产品id
     */
    private String appId;
    /**
     * appKey
     */
    private Long appKey;
    /**
     * imei
     */
    private String imei;
}
