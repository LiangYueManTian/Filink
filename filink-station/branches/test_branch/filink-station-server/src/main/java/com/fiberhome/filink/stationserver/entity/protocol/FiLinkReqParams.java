package com.fiberhome.filink.stationserver.entity.protocol;

import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import lombok.Data;

import java.io.Serializable;

/**
 * filink udp请求帧参数
 * @author CongcaiYu
 */
@Data
public class FiLinkReqParams extends AbstractReqParams implements Serializable {

    /**
     * 用户token
     */
    private String token;

    /**
     * 手机id
     */
    private String phoneId;

    /**
     * appKey
     */
    private Long appKey;
}
