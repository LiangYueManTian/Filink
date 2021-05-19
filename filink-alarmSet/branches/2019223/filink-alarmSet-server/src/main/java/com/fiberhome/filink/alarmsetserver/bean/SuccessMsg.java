package com.fiberhome.filink.alarmsetserver.bean;


import lombok.Data;

/**
 * <p>
 *  请求成功返回实体
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class SuccessMsg {

    private Object data;
    private String msg;

    public SuccessMsg(Object data, String msg) {
        this.data = data;
        this.msg = msg;
    }
}
