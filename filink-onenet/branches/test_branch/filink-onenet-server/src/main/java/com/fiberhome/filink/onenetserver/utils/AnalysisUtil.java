package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.onenetserver.bean.device.ReceiveBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

/**
 * <p>
 * OneNet数据推送接收解析数据工具类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Slf4j
public class AnalysisUtil {

    /**
     * 算法
     */
    private static MessageDigest mdInst;
    /**
     * nonce
     */
    private static final String NONCE = "nonce";
    /**
     * msg_signature
     */
    private static final String MSG_SIGNATURE = "msg_signature";
    /**
     * enc_msg
     */
    private static final String ENC_MSG = "enc_msg";
    /**
     * msg
     */
    private static final String MSG = "msg";
    /**
     * value
     */
    private static final String VALUE = "value";

    static {
        try {
            //获取解密算法（MD5）
            mdInst = MessageDigest.getInstance("MD5");
            Security.addProvider(new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * 功能描述:在OneNet平台配置数据接收地址时，平台会发送URL&token验证请求<p>
     * 使用此功能函数验证token
     *
     * @param msg       请求参数 <msg>的值
     * @param nonce     请求参数 <nonce>的值
     * @param signature 请求参数 <signature>的值
     * @param token     OneNet平台配置页面token的值
     * @return token检验成功返回true；token校验失败返回false
     */
    public static boolean checkToken(String msg, String nonce, String signature, String token) {
        byte[] paramB = new byte[token.length() + 8 + msg.length()];
        System.arraycopy(token.getBytes(), 0, paramB, 0, token.length());
        System.arraycopy(nonce.getBytes(), 0, paramB, token.length(), 8);
        System.arraycopy(msg.getBytes(), 0, paramB, token.length() + 8, msg.length());
        String sig = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(mdInst.digest(paramB));
        log.info("url&token validation: result {},  detail receive:{} calculate:{}", sig.equals(signature.replace(' ', '+')), signature, sig);
        return sig.equals(signature.replace(' ', '+'));
    }

    /**
     * 功能描述: 检查接收数据的信息摘要是否正确。<p>
     * 方法非线程安全。
     *
     * @param obj   消息体对象
     * @param token OneNet平台配置页面token的值
     * @return boolean
     */
    public static boolean checkSignature(ReceiveBody obj, String token) {
        //计算接受到的消息的摘要
        //token长度 + 8B随机字符串长度 + 消息长度
        byte[] signature = new byte[token.length() + 8 + obj.getMsg().toString().length()];
        System.arraycopy(token.getBytes(), 0, signature, 0, token.length());
        System.arraycopy(obj.getNonce().getBytes(), 0, signature, token.length(), 8);
        System.arraycopy(obj.getMsg().toString().getBytes(), 0, signature, token.length() + 8, obj.getMsg().toString().length());
        String calSig = Base64.encodeBase64String(mdInst.digest(signature));
        log.info("check signature: result:{}  receive sig:{},calculate sig: {}", calSig.equals(obj.getMsgSignature()), obj.getMsgSignature(), calSig);
        return calSig.equals(obj.getMsgSignature());
    }

    /**
     * 功能描述 解密消息
     *
     * @param obj       消息体对象
     * @param encodeKey OneNet平台第三方平台配置页面为用户生成的AES的BASE64编码格式秘钥
     * @return 消息
     * @throws NoSuchPaddingException             异常
     * @throws NoSuchAlgorithmException           异常
     * @throws InvalidAlgorithmParameterException 异常
     * @throws InvalidKeyException                异常
     * @throws BadPaddingException                异常
     * @throws IllegalBlockSizeException          异常
     */
    public static String decryptMsg(ReceiveBody obj, String encodeKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] encMsg = Base64.decodeBase64(obj.getMsg().toString());
        byte[] aesKey = Base64.decodeBase64(encodeKey + "=");
        SecretKey secretKey = new SecretKeySpec(aesKey, 0, 32, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(aesKey, 0, 16));
        byte[] allMsg = cipher.doFinal(encMsg);
        byte[] msgLenBytes = new byte[4];
        System.arraycopy(allMsg, 16, msgLenBytes, 0, 4);
        int msgLen = getMsgLen(msgLenBytes);
        byte[] msg = new byte[msgLen];
        System.arraycopy(allMsg, 20, msg, 0, msgLen);
        return new String(msg);
    }

    /**
     * 功能描述 解析数据推送请求，生成code>ReceiveBody</code>消息对象
     *
     * @param body      数据推送请求body部分
     * @param encrypted 表征是否为加密消息
     * @return 生成的<code>ReceiveBody</code>消息对象
     */
    public static ReceiveBody resolveBody(String body, boolean encrypted) {
        JSONObject jsonMsg = new JSONObject(body);
        ReceiveBody obj = new ReceiveBody();
        obj.setNonce(jsonMsg.getString(NONCE));
        obj.setMsgSignature(jsonMsg.getString(MSG_SIGNATURE));
        if (encrypted) {
            if (!jsonMsg.has(ENC_MSG)) {
                return null;
            }
            obj.setMsg(jsonMsg.getString(ENC_MSG));
        } else {
            if (!jsonMsg.has(MSG)) {
                return null;
            }
            obj.setMsg(jsonMsg.get(MSG));
        }
        return obj;
    }

    /**
     * 获取具体数据部分，为设备上传至平台或触发的相关数据
     *
     * @param msg 消息体
     * @return 具体数据部分
     */
    public static String resolveValue(Object msg) {
        JSONObject jsonMsg = new JSONObject(msg.toString());
        log.info("data resolve:  jsonMsg------- " + jsonMsg);
        if (!jsonMsg.has(VALUE)) {
            return null;
        }
        return jsonMsg.getString(VALUE);
    }

    private static int getMsgLen(byte[] arrays) {
        int len = 0;
        len += (arrays[0] & 0xFF) << 24;
        len += (arrays[1] & 0xFF) << 16;
        len += (arrays[2] & 0xFF) << 8;
        len += (arrays[3] & 0xFF);
        return len;
    }
}
