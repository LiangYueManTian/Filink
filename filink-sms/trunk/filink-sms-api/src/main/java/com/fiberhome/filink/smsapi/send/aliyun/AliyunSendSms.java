package com.fiberhome.filink.smsapi.send.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fiberhome.filink.smsapi.bean.AliyunMessage;
import com.fiberhome.filink.smsapi.bean.AliyunSms;
import com.fiberhome.filink.smsapi.send.AbstractSendSmsAndEmail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 阿里云发送短信
 *
 * @author yuanyao@wistronits.com
 * create on 2019/2/16 11:19
 */
@Slf4j
@Component("aliyunSendSms")
public class AliyunSendSms extends AbstractSendSmsAndEmail {

    private static final String PRODUCT = "Dysmsapi";
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";


    private AliyunSms sms;


    /**
     * 具体发送短信
     */
    private SendSmsResponse sendDemo() throws ClientException, ClientException {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile(sms.getArea(),
                sms.getAccessKeyId(),
                sms.getAccessKeySecret());
        DefaultProfile.addEndpoint(sms.getArea(), sms.getArea(), PRODUCT, DOMAIN);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(sms.getPhone());
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(sms.getSignName());
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(sms.getTemplateCode());
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(sms.getTemplateParam());
//        request.setTemplateParam("{\"alarmname\":\"告警名称\", \"alarmdes\":\"告警啥\", \"devname\":\"设施名称\",\"devtype\":\"告警类型\",\"region\":\"告警原因\",\"alarmtime\":\"告警时间\"}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }

    private QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", sms.getAccessKeyId(), sms.getAccessKeySecret());
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber("15000000000");
        //可选-流水号
        request.setBizId(bizId);
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //必填-页大小
        request.setPageSize(10L);
        //必填-当前页码从1开始计数
        request.setCurrentPage(1L);

        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

        return querySendDetailsResponse;
    }

    /**
     * 具体发送
     */
    @Override
    protected SendSmsResponse send() throws ClientException {
        SendSmsResponse response = sendDemo();
        log.info("短信接口返回的数据----------------");
        log.info("Code={}", response.getCode());
        log.info("Message={}", response.getMessage());
        log.info("RequestId={}", response.getRequestId());
        log.info("BizId={}", response.getBizId());
        return response;
    }

    /**
     * 参数校验
     *
     * @param message 消息实体
     * @return
     */
    @Override
    protected boolean checkMessage(AliyunMessage message) {
        AliyunSms sms = null;
        if (message instanceof AliyunSms) {
            sms = (AliyunSms) message;
        }
        if (sms == null) {
            log.info("短信参数为空");
            return false;
        }
        if (StringUtils.isEmpty(sms.getPhone())) {
            log.info("电话号码为空");
            return false;
        }
        if (StringUtils.isEmpty(sms.getSignName())) {
            log.info("短信签名为空");
            return false;
        }
        this.sms = sms;
        return true;
    }
}
