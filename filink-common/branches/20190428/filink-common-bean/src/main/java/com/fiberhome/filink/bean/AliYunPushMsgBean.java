package com.fiberhome.filink.bean;

import lombok.Data;

/**
 * 阿里云推送对象
 * @author CongcaiYu
 */
@Data
public class AliYunPushMsgBean {

    /**
     * 推送类型
     */
    private String type;
    /**
     * 推送数据
     */
    private Object data;
}
