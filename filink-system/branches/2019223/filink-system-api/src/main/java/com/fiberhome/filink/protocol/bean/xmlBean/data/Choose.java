package com.fiberhome.filink.protocol.bean.xmlBean.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * dataClass参数类
 * @author CongcaiYu
 */
@Data
public class Choose implements Serializable {

    /**
     * id
     */
    private String id;
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * data参数对象
     */
    private List<DataParamsChild> dataParams;

}
