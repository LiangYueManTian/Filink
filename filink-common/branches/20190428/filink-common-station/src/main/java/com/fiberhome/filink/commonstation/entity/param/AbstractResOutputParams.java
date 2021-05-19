package com.fiberhome.filink.commonstation.entity.param;

import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import lombok.Data;

import java.util.Map;

/**
 * 响应帧输出参数抽象类
 * @author CongcaiYu
 */
@Data
public abstract class AbstractResOutputParams {
    /**
     * 主控id
     */
    private String equipmentId;
    /**
     * 流水号
     */
    private Integer serialNumber;
    /**
     * 指令id
     */
    private String cmdId;
    /**
     * 请求类型
     */
    private String cmdType;
    /**
     * 解析后参数
     */
    private Map<String, Object> params;
    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 硬件版本
     */
    private String hardwareVersion;
    /**
     * 主控信息
     */
    private ControlParam controlParam;
    /**
     * 应答码
     */
    private Integer cmdOk;

}
