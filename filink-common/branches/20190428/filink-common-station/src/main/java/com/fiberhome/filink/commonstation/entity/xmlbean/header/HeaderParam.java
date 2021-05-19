package com.fiberhome.filink.commonstation.entity.xmlbean.header;

import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import lombok.Data;

/**
 * xml配置头信息
 * @author CongcaiYu
 */
@Data
public class HeaderParam {

    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 长度
     */
    private Integer length;
    /**
     * 是否保留
     */
    private boolean reserved;
    /**
     * 请求帧字段包含范围
     */
    private String reqScope;
    /**
     * 响应帧字段包含范围
     */
    private String resScope;
    /**
     * 数据处理
     */
    private DataFormat dataFormat;
}
