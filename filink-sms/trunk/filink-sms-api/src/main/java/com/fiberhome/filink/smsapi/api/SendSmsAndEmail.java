package com.fiberhome.filink.smsapi.api;

import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.smsapi.bean.AliyunMessage;

/**
 * 发送短信和邮件接口
 *
 * @author yuanyao@wistronits.com
 * create on 2019/2/16 11:14
 */
public interface SendSmsAndEmail {

    String ALIYUN_SMS = "aliyunSendSms";

    String ALIYUN_EMAIL = "aliyunSendEmail";

    String ALIYUN_MOBILE_PUSH = "aliyunMobilePush";

    /**
     * 发送短信和邮件
     * @param message
     * @throws ClientException
     */
    AcsResponse sendSmsAndEmail(AliyunMessage message) throws ClientException;
}
