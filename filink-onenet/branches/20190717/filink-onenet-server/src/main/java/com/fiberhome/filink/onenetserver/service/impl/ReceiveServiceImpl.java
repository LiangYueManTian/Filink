package com.fiberhome.filink.onenetserver.service.impl;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.onenetserver.bean.device.ReceiveBody;
import com.fiberhome.filink.onenetserver.constant.NBStatus;
import com.fiberhome.filink.onenetserver.exception.OneNetReceiveException;
import com.fiberhome.filink.onenetserver.service.ReceiveService;
import com.fiberhome.filink.onenetserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.onenetserver.utils.AnalysisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 *   oneNet平台HTTP推送接收服务层实现类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Slf4j
@Service
public class ReceiveServiceImpl implements ReceiveService {

    @Autowired
    private FiLinkKafkaSender kafkaSender;
    /**用户自定义token和OneNet第三方平台配置里的token一致*/
    @Value("${oneNet.http.token}")
    private String token;
    /**ok*/
    private static final String OK = "OK";
    /**
     * oneNet平台HTTP推送消息接收
     *
     * @param body 数据消息
     * @return 任意字符串
     */
    @Override
    public String receive(String body) {
        //解析数据推送请求，明文模式,
        ReceiveBody obj = AnalysisUtil.resolveBody(body, false);
        try {
            if (obj != null){
                boolean dataRight = AnalysisUtil.checkSignature(obj, token);
                String msgBody;
                if (dataRight){
                    msgBody = AnalysisUtil.resolveValue(obj.getMsg()) ;
                } else {
                    //数据的信息摘要错误
                    log.warn("data receive:  signature error ");
                    msgBody = null;
                }
                if (msgBody == null) {
                    //数据的信息为空
                    log.warn("data receive:  msgBody is null-------");
                    return OK;
                }
                DeviceMsg deviceMsg = new DeviceMsg();
                //将平台消息进行base64解密
                byte[] dataBytes = Base64.decodeBase64(msgBody);
                String hexData = HexUtil.bytesToHexString(dataBytes);
                log.info("receive plateForm msg : {}", hexData);
                deviceMsg.setHexData(hexData);
                kafkaSender.sendMsg(deviceMsg);
            }else {
                //数据推送请求为空
                log.warn("data receive: body empty error");
            }
        } catch (Exception e) {
            throw new OneNetReceiveException(NBStatus.RECEIVE_ERROR);
        }
        return OK;
    }

    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     *
     * @param msg       验证消息
     * @param nonce     随机串
     * @param signature 签名
     * @return msg值
     */
    @Override
    public String check(String msg, String nonce, String signature) {
        //推送验证
        if (AnalysisUtil.checkToken(msg,nonce,signature, token)){
            log.info("data receive: content--{}" , msg);
            return msg;
        } else {
            //验证失败
            throw new OneNetReceiveException(NBStatus.HTTP_REQUEST_ERROR);
        }
    }
}
