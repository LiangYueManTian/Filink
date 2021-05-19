package com.fiberhome.filink.protocol.bean.xmlBean.data;


import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import lombok.Data;

import java.util.Map;

/**
 * xml data参数对象
 * @author CongcaiYu
 */
@Data
public class DataParam extends DataParamsChild {

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
     * 是否随机
     */
    private boolean random;
    /**
     * 是否是循环字段
     */
    private boolean foreach;
    /**
     * 数值
     */
    private String data;
    /**
     * 引用长度id
     */
    private String lengthRef;
    /**
     * 结果集
     */
    private Map<String, String> resultMap;
    /**
     * 数据处理
     */
    private DataFormat dataFormat;
    /**
     * dataClass处理对象
     */
    private Map<String, Choose> chooseMap;

}
