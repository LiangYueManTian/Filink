package com.fiberhome.filink.smsapi.bean;

import lombok.Data;

/**
 * 邮件
 *
 * @author yuanyao@wistronits.com
 * create on 2019/3/18 10:36
 */
@Data
public class AliyunEmail extends AliyunMessage{



    /**
     * 控制台创建的发信地址
     */
    private String accountName;

    /**
     * 发信人昵称
     */
    private String fromAlias;

    /**
     *
     */
    private Integer addressType = 1;

    /**
     * 控制台创建的标签
     */
    private String tagName;

    /**
     * 回复地址
     */
    private Boolean replyToAddress = true;

    /**
     * 目标地址，多个地址之间使用逗号分开
     */
    private String toAddress;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件正文
     */
    private String htmlBody;
}
