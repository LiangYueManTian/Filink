package com.fiberhome.filink.onenetserver.controller;


import com.fiberhome.filink.onenetserver.service.ReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *   oneNet平台HTTP推送接收控制层
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/receive")
@Slf4j
public class ReceiveController {
    /**STOP*/
    private static final String STOP = "stop";
    /**
     * 自动注入ReceiveService
     */
    @Autowired
    private ReceiveService receiveService;
    /**
     * 功能描述：第三方平台数据接收。<p>
     *           <ul>注:
     *               <li>1.OneNet平台为了保证数据不丢失，有重发机制，如果重复数据对业务有影响，数据接收端需要对重复数据进行排除重复处理。</li>
     *               <li>2.OneNet每一次post数据请求后，等待客户端的响应都设有时限，在规定时限内没有收到响应会认为发送失败。
     *                    接收程序接收到数据时，尽量先缓存起来，再做业务逻辑处理。</li>
     *           </ul>
     * @param body 数据消息
     * @return 任意字符串。OneNet平台接收到http 200的响应，才会认为数据推送成功，否则会重发。
     */
    @PostMapping("/receive")
    @ResponseBody
    public String receive(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return STOP;
        }
        return receiveService.receive(body);
    }

    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     * @param msg 验证消息
     * @param nonce 随机串
     * @param signature 签名
     * @return msg值
     */
    @GetMapping("/receive")
    @ResponseBody
    public String check(@RequestParam(value = "msg") String msg,
                        @RequestParam(value = "nonce") String nonce,
                        @RequestParam(value = "signature") String signature) {
        log.info("url&token check: msg:{} nonce{} signature:{}",msg,nonce,signature);
        return receiveService.check(msg, nonce, signature);
    }
}
