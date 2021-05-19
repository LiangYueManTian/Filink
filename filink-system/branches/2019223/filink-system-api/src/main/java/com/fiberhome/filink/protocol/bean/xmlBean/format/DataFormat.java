package com.fiberhome.filink.protocol.bean.xmlBean.format;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * xml数据转换类
 * @author CongcaiYu
 */
@Data
public class DataFormat implements Serializable {

    /**
     * id
     */
    private String id;
    /**
     * 入参
     */
    private Object param;
    /**
     * 数据处理配置对象
     */
    private List<DataFormatParam> dataFormatParams;
    /**
     * 处理类类名
     */
    private String className;
    /**
     * 结果集
     */
    private Map<String, String> resultMap;
}
