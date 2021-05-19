package com.fiberhome.filink.parameter.bean;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 *   阿里云邮箱短信等测试信息实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-16
 */
@Data
@Component
public class AliSendTest {
    /**
     * 短信签名
     * 后续签名在云平台控制台修改
     */
    @Value(value = "${send.message.signName}")
    private String signName;
    /**
     * 模板code
     */
    @Value(value = "${send.message.templateCode}")
    private String templateCode;
    /**
     * 设施名称
     */
    @Value(value = "${send.message.devName}")
    private String devName;
    /**
     * 门号
     */
    @Value(value = "${send.message.doorNo}")
    private String doorNo;
    /**
     * 告警信息
     */
    @Value(value = "${send.message.alarmDes}")
    private String alarmDes;
    /**
     * 控制台创建的发信地址
     */
    @Value(value = "${send.mail.accountName}")
    private String accountName;
    /**
     * 发信人昵称
     */
    @Value(value = "${send.mail.fromAlias}")
    private String fromAlias;
    /**
     * 控制台创建的标签
     */
    @Value(value = "${send.mail.tagName}")
    private String tagName;
    /**
     * 邮件主题
     */
    @Value(value = "${send.mail.subject}")
    private String subject;
    /**
     * 邮件正文
     */
    @Value(value = "${send.mail.htmlBody}")
    private String htmlBody;
}
