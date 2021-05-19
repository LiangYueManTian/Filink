package com.fiberhome.filink.stationserver.entity.param;

import lombok.Data;

import java.util.Map;

/**
 * 响应帧输出参数抽象类
 * @author CongcaiYu
 */
@Data
public abstract class AbstractResOutputParams {
    /**
     * 设施序列id
     */
    private String deviceId;
    /**
     * 指令id
     */
    private String cmdId;
    /**
     * 解析后参数
     */
    private Map<String, Object> params;

}
