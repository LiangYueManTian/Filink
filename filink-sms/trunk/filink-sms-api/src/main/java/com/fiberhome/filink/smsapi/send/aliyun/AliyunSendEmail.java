package com.fiberhome.filink.smsapi.send.aliyun;

import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fiberhome.filink.smsapi.bean.AliyunEmail;
import com.fiberhome.filink.smsapi.bean.AliyunMessage;
import com.fiberhome.filink.smsapi.send.AbstractSendSmsAndEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2019-03-18 11:14
 */
@Slf4j
@Component("aliyunSendEmail")
public class AliyunSendEmail extends AbstractSendSmsAndEmail {

    private AliyunEmail email;

    /**
     * 具体发送
     *
     */
    @Override
    protected AcsResponse send() throws ClientException {
        log.info("开始发送邮件");
        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        IClientProfile profile = DefaultProfile.getProfile(email.getArea(), email.getAccessKeyId(), email.getAccessKeySecret());
        // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理
        //try {
        //DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm",  "dm.ap-southeast-1.aliyuncs.com");
        //} catch (ClientException e) {
        //e.printStackTrace();
        //}
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        //  BatchSendMailRequest batchSendMailRequest = new BatchSendMailRequest();
        try {
            //request.setVersion("2017-06-22");// 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
            request.setAccountName(email.getAccountName());
            request.setFromAlias(email.getFromAlias());
            request.setAddressType(email.getAddressType());
            request.setTagName(email.getTagName());
            request.setReplyToAddress(email.getReplyToAddress());
            request.setToAddress(email.getToAddress());
            //可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
            //request.setToAddress("邮箱1,邮箱2");
            request.setSubject(email.getSubject());
            request.setHtmlBody(email.getHtmlBody());
            return client.getAcsResponse(request);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 校验邮件相关参数
     * @param message 消息实体
     * @return
     */
    @Override
    protected boolean checkMessage(AliyunMessage message) {
        AliyunEmail email = null;
        if (message instanceof AliyunEmail) {
            email = (AliyunEmail) message;
        }
        if (null == email) {
            return false;
        }

        // TODO: 2019-03-18 参数校验 

        this.email = email;
        return true;
    }
}
