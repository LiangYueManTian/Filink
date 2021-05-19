package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * <p>
 *   oneNet平台安全鉴权生成类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public class OneNetToken {
    /**
     * 生成Token
     * @param version 参数组版本号，日期格式，目前仅支持"2018-10-31"
     * @param resourceName 访问资源 resource
     * @param expirationTime 访问过期时间
     * @param accessKey 核心密钥
     * @return OneNetToken
     * @throws UnsupportedEncodingException 异常
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeyException 异常
     */
    public static String assembleToken(String version, String resourceName, String expirationTime, String accessKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = new StringBuilder();
        //含特殊符号的value进行URL编码
        String signatureMethod = OneNetHttpConstants.SIGNATURE_METHOD;
        String res = URLEncoder.encode(resourceName, "UTF-8");
        String sig = URLEncoder.encode(generatorSignature(version, resourceName, expirationTime, accessKey, signatureMethod), "UTF-8");
        sb.append("version=");
        sb.append(version);
        sb.append("&res=");
        sb.append(res);
        sb.append("&et=");
        sb.append(expirationTime);
        sb.append("&method=");
        sb.append(signatureMethod);
        sb.append("&sign=");
        sb.append(sig);
        return sb.toString();
    }

    /**
     * 生成签名结果字符串
     * @param version 参数组版本号，日期格式，目前仅支持"2018-10-31"
     * @param resourceName 访问资源 resource
     * @param expirationTime 访问过期时间
     * @param signatureMethod 签名方法 signatureMethod 支持md5、sha1、sha256
     * @param accessKey 核心密钥
     * @return 生成签名结果字符串
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeyException 异常
     */
    private static String generatorSignature(String version, String resourceName, String expirationTime,String accessKey, String signatureMethod) throws NoSuchAlgorithmException,InvalidKeyException {
        //按照et method res version的顺序
        String encryptText = expirationTime + "\n" + signatureMethod + "\n" + resourceName + "\n" + version;
        String signature;
        byte[] bytes = hmacEncrypt(encryptText, accessKey, signatureMethod);
        signature = Base64.getEncoder().encodeToString(bytes);
        return signature;
    }

    /**
     * 转换字节
     * @param data 内容
     * @param key  核心密钥
     * @param signatureMethod 签名方法 signatureMethod 支持md5、sha1、sha256
     * @return 转换字节
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeyException 异常
     */
    private static byte[] hmacEncrypt(String data, String key, String signatureMethod) throws NoSuchAlgorithmException, InvalidKeyException {
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKeySpec sigKey = new SecretKeySpec(Base64.getDecoder().decode(key),
                "Hmac" + signatureMethod.toUpperCase());

        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("Hmac" + signatureMethod.toUpperCase());
        //用给定密钥初始化 Mac 对象
        mac.init(sigKey);
        return mac.doFinal(data.getBytes());
    }
}
