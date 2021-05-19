package com.fiberhome.filink.smsapi.send;

import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.smsapi.bean.AliyunMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信发送抽象类
 *
 * @author yuanyao@wistronits.com
 * create on 2019/2/16 11:15
 */
@Slf4j
public abstract class AbstractSendSmsAndEmail implements SendSmsAndEmail {


    /**
     * 发送短信和邮件
     * @param message
     * @return
     * @throws ClientException
     */
    @Override
    public AcsResponse sendSmsAndEmail(AliyunMessage message) throws ClientException {
        if (!checkMessage(message)) {
            log.info("短信参数校验未通过，返回null");
            return null;
        }
        return send();
    }

    /**
     * 具体发送
     */
    protected abstract AcsResponse send() throws ClientException;

    /**
     * 参数校验
     * @param message 消息实体
     * @return
     */
    protected abstract boolean checkMessage(AliyunMessage message);

}
