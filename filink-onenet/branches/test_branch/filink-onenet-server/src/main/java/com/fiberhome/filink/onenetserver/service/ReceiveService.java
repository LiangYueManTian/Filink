package com.fiberhome.filink.onenetserver.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *   oneNet平台HTTP推送接收服务层
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public interface ReceiveService {
    /**
     * oneNet平台HTTP推送消息接收
     * @param body 数据消息
     * @return 任意字符串
     */
    String receive(String body);

    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     * @param msg 验证消息
     * @param nonce 随机串
     * @param signature 签名
     * @return msg值
     */
    @GetMapping("/receive")
    @ResponseBody
    String check(String msg, String nonce, String signature);
}
