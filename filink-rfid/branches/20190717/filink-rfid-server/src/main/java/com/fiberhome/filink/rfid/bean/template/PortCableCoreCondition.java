package com.fiberhome.filink.rfid.bean.template;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import lombok.Data;

/**
 * 用于本端端口查询条件构建
 *
 * @author liyj
 * @date 2019/7/12
 */
@Data
public class PortCableCoreCondition extends PortCableCoreInfo {
    /**
     * 端口状态
     */
    private Integer portState;
}
