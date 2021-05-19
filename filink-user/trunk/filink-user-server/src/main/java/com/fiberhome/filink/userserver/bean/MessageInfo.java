package com.fiberhome.filink.userserver.bean;
import lombok.Data;

/**
 * 短信实体类
 * @author xgong
 */
@Data
public class MessageInfo {

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 短信内容
     */
    private String content;
}
