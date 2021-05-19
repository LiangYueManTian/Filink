package com.fiberhome.filink.onenetserver.bean.device;

import lombok.Data;

/**
 * <p>
 *   oneNet平台HTTP推送接收实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
public class ReceiveBody {
    /**
     * 消息
     */
    private Object msg;
    /**
     * 用于计算消息摘要的随机串
     */
    private String nonce;
    /**
     * 消息摘要
     */
    private String msgSignature;

    @Override
    public String toString(){
        return "[ \"msg\":"+this.msg+",\"nonce\":"+this.nonce+",\"signature\":"+this.msgSignature+"]";
    }
}
