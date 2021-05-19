package com.fiberhome.filink.filinkoceanconnectserver.entity.protocol;

import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import lombok.Data;

import java.io.Serializable;

/**
 * filink响应输出参数
 * @author CongcaiYu
 */
@Data
public class FiLinkOceanResOutputParams extends AbstractResOutputParams implements Serializable {

    /**
     * 是否是心跳
     */
    private boolean heartBeat;
}
