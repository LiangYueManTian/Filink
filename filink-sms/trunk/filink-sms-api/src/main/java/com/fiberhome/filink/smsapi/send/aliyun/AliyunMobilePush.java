package com.fiberhome.filink.smsapi.send.aliyun;

import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.push.model.v20160801.PushRequest;
import com.aliyuncs.push.model.v20160801.PushResponse;
import com.aliyuncs.utils.ParameterHelper;
import com.fiberhome.filink.smsapi.bean.AliyunMessage;
import com.fiberhome.filink.smsapi.bean.AliyunPush;
import com.fiberhome.filink.smsapi.bean.Android;
import com.fiberhome.filink.smsapi.bean.Ios;
import com.fiberhome.filink.smsapi.send.AbstractSendSmsAndEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Mobile push
 *
 * @author yuanyao@wistronits.com
 * create on 2019/3/27 11:09
 */
@Slf4j
@Component("aliyunMobilePush")
public class AliyunMobilePush extends AbstractSendSmsAndEmail {

    private ThreadLocal<AliyunPush> aliyunPushThreadLocal = new ThreadLocal<>();

    /**
     * 具体发送
     */
    @Override
    protected AcsResponse send() throws ClientException {

        AliyunPush aliyunPush = aliyunPushThreadLocal.get();
        log.info("开始发送短信,发送消息为",aliyunPush.getBody());

        DefaultProfile profile = DefaultProfile.getProfile(
                aliyunPush.getArea(),
                aliyunPush.getAccessKeyId(),
                aliyunPush.getAccessKeySecret());

        DefaultAcsClient client = new DefaultAcsClient(profile);

        PushRequest pushRequest = new PushRequest();
        pushRequest.setProtocol(ProtocolType.HTTPS);
        pushRequest.setMethod(MethodType.POST);

        // TODO: 2019-03-27 ios和android是否需要区分

        // ios
        pushRequest.setAppKey(aliyunPush.getAppKey());
        // android
        pushRequest.setAppKey(aliyunPush.getAppKey());

        //推送目标: DEVICE:按设备推送 ALIAS : 按别名推送 ACCOUNT:按帐号推送  TAG:按标签推送; ALL: 广播推送
        pushRequest.setTarget(aliyunPush.getTarget());

        //根据Target来设定，如Target=DEVICE, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔.(帐号与设备有一次最多100个的限制)
        pushRequest.setTargetValue(aliyunPush.getIds());
        //推送目标: device:推送给设备; account:推送给指定帐号,tag:推送给自定义标签; all: 推送给全部
        // 根据Target来设定，如Target=device, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔.(帐号与设备有一次最多100个的限制)
//        pushRequest.setTarget("ALL");
        // pushRequest.setTargetValue("ALL");
        // 消息类型 MESSAGE NOTICE
        pushRequest.setPushType(aliyunPush.getPushType());

        // TODO: 2019-03-27 此处与appkey是否有关联冲突
        // 设备类型 ANDROID iOS ALL.
        pushRequest.setDeviceType(aliyunPush.getDeviceType());


        // 推送配置
        // 消息的标题
        pushRequest.setTitle(aliyunPush.getTitle());
        // 消息的内容
        pushRequest.setBody(aliyunPush.getBody());

        Ios ios = aliyunPush.getIos();
        // 推送配置: iOS
        // iOS应用图标右上角角标
        pushRequest.setIOSBadge(ios.getBadge());
        //开启静默通知
        pushRequest.setIOSSilentNotification(ios.getSilentNotification());
        // iOS通知声音
        pushRequest.setIOSMusic(ios.getMusic());
        //iOS10通知副标题的内容
        pushRequest.setIOSSubtitle(ios.getSubtitle());
        //指定iOS10通知Category
        pushRequest.setIOSNotificationCategory(ios.getNotificationCategory());
        //是否允许扩展iOS通知内容
        pushRequest.setIOSMutableContent(ios.getMutableContent());
        //iOS的通知是通过APNs中心来发送的，需要填写对应的环境信息。"DEV" : 表示开发环境 "PRODUCT" : 表示生产环境
        pushRequest.setIOSApnsEnv(ios.getApnsEnv());
        // 消息推送时设备不在线（既与移动推送的服务端的长连接通道不通），则这条推送会做为通知，
        // 通过苹果的APNs通道送达一次。注意：离线消息转通知仅适用于生产环境
        pushRequest.setIOSRemind(ios.getRemind());
        //iOS消息转通知时使用的iOS通知内容，仅当iOSApnsEnv=PRODUCT && iOSRemind为true时有效
        pushRequest.setIOSRemindBody(ios.getRemindBody());
        //通知的扩展属性(注意 : 该参数要以json map的格式传入,否则会解析出错)
        pushRequest.setIOSExtParameters(ios.getExtParameters());

        Android android = aliyunPush.getAndroid();
        // 推送配置: Android
        //通知的提醒方式 "VIBRATE" : 震动 "SOUND" : 声音 "BOTH" : 声音和震动 NONE : 静音
        pushRequest.setAndroidNotifyType(android.getNotifyType());
        //通知栏自定义样式0-100
        pushRequest.setAndroidNotificationBarType(android.getNotificationBarType());
        //通知栏自定义样式0-100
        pushRequest.setAndroidNotificationBarPriority(android.getNotificationBarPriority());
        //点击通知后动作 "APPLICATION" : 打开应用 "ACTIVITY" : 打开AndroidActivity "URL" : 打开URL "NONE" : 无跳转
        pushRequest.setAndroidOpenType(android.getOpenType());
        //Android收到推送后打开对应的url,仅当AndroidOpenType="URL"有效
        pushRequest.setAndroidOpenUrl(android.getOpenUrl());
        // 设定通知打开的activity，仅当AndroidOpenType="Activity"有效
        pushRequest.setAndroidActivity(android.getActivity());
        // Android通知音乐
        pushRequest.setAndroidMusic(android.getMusic());
        //设置该参数后启动小米托管弹窗功能,
        // 此处指定通知点击后跳转的Activity（托管弹窗的前提条件：1. 集成小米辅助通道；2. StoreOffline参数设为true）
        pushRequest.setAndroidXiaoMiActivity(android.getXiaoMiActivity());
        pushRequest.setAndroidXiaoMiNotifyTitle(android.getXiaoMiNotifyTitle());
        pushRequest.setAndroidXiaoMiNotifyBody(android.getXiaoMiNotifyBody());
        //设定通知的扩展属性。(注意 : 该参数要以 json map 的格式传入,否则会解析出错)
        pushRequest.setAndroidExtParameters(android.getExtParameters());


        // 推送控制
        // 30秒之间的时间点, 也可以设置成你指定固定时间
        Date pushDate = new Date(System.currentTimeMillis());
        String pushTime = ParameterHelper.getISO8601Time(pushDate);
        // 延后推送。可选，如果不设置表示立即推送
        pushRequest.setPushTime(pushTime);
        // 12小时后消息失效, 不会再发送
        String expireTime = ParameterHelper.getISO8601Time(new Date(System.currentTimeMillis() + 12 * 3600 * 1000));
        pushRequest.setExpireTime(expireTime);
        // 离线消息是否保存,若保存, 在推送时候，用户即使不在线，下一次上线则会收到
        pushRequest.setStoreOffline(true);


        PushResponse pushResponse = client.getAcsResponse(pushRequest);
        System.out.printf("RequestId: %s, MessageID: %s\n",
                pushResponse.getRequestId(), pushResponse.getMessageId());

        return pushResponse;
    }

    /**
     * 参数校验
     *
     * @param message 消息实体
     * @return
     */
    @Override
    protected boolean checkMessage(AliyunMessage message) {
        if (message instanceof AliyunPush) {
            aliyunPushThreadLocal.set((AliyunPush) message);
        }
        return true;
    }
}
