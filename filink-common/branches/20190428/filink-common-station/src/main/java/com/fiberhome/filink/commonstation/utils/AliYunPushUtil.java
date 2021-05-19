package com.fiberhome.filink.commonstation.utils;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.AliYunPushConstant;
import com.fiberhome.filink.bean.AliYunPushMsgBean;
import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.entity.config.UnlockPushBean;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.smsapi.bean.AliyunPush;
import com.fiberhome.filink.smsapi.bean.Android;
import com.fiberhome.filink.smsapi.bean.Ios;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * app推送
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class AliYunPushUtil {

    @Resource(name = SendSmsAndEmail.ALIYUN_MOBILE_PUSH)
    private SendSmsAndEmail aliYunPush;

    /**
     * 阿里云app推送
     *
     * @param pushBean    推送实体
     */
    public void pushMsg(UnlockPushBean pushBean) {
        try {
            AliyunPush aliyunPush = new AliyunPush();
            aliyunPush.setAccessKeyId(pushBean.getAccessKeyId());
            aliyunPush.setAccessKeySecret(pushBean.getAccessKeySecret());
            //构造推送参数
            AliYunPushMsgBean aliYunPushMsgBean = new AliYunPushMsgBean();
            aliYunPushMsgBean.setType(AliYunPushConstant.UNLOCK_TYPE);
            aliYunPushMsgBean.setData(pushBean.getMsg());
            //将推送参数序列化成json
            aliyunPush.setBody(JSONObject.toJSONString(aliYunPushMsgBean));
            aliyunPush.setIds(pushBean.getPhoneId());
            aliyunPush.setTitle(pushBean.getTitle());
            aliyunPush.setAppKey(pushBean.getAppKey());
            aliyunPush.setAndroid(new Android());
            aliyunPush.setIos(new Ios());
            aliyunPush.setPushType(Constant.MESSAGE_TYPE);
            aliyunPush.setTarget("DEVICE");
            aliYunPush.sendSmsAndEmail(aliyunPush);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("unlock result aliyun push failed>>>>>>>>>");
        }
    }


}
