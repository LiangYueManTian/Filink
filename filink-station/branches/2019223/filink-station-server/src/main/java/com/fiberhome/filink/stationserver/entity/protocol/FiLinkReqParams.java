package com.fiberhome.filink.stationserver.entity.protocol;

import com.fiberhome.filink.stationserver.entity.param.AbstractReqParams;
import lombok.Data;

import java.io.Serializable;

/**
 * filink udp请求帧参数
 * @author CongcaiYu
 */
@Data
public class FiLinkReqParams extends AbstractReqParams implements Serializable {

    /**
     * 请求类型
     */
    private Integer cmdType;
}
