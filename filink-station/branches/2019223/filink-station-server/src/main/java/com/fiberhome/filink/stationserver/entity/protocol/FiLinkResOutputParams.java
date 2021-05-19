package com.fiberhome.filink.stationserver.entity.protocol;

import com.fiberhome.filink.stationserver.entity.param.AbstractResOutputParams;
import lombok.Data;

import java.io.Serializable;

/**
 * filink响应输出参数
 * @author CongcaiYu
 */
@Data
public class FiLinkResOutputParams extends AbstractResOutputParams implements Serializable {

    private boolean heartBeat;
}
