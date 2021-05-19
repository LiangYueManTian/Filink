package com.fiberhome.filink.server_common.bean;

import com.fiberhome.filink.server_common.utils.I18nUtils;

import java.util.Locale;

/**
 * 系统返回消息枚举定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/14 15:02
 */
public enum  ResultMessageEnum {

    /**
     * 方法参数无效
     */
    METHOD_ARGUMENT_NOT_VALID("methodArgumentNotValid","方法参数无效"),

    /**
     * 未定义异常
     */
    UNDEFINED_EXCEPTION("undefinedException","未定义异常"),

    /**
     * 用户未登录
     */
    USER_NOT_LOGIN("userNotLogin","用户未登录");

    private String usMsg;
    private String cnMsg;

    ResultMessageEnum(String usMsg, String cnMsg) {
        this.usMsg = usMsg;
        this.cnMsg = cnMsg;
    }

    public String getMsg() {
        Locale local = I18nUtils.getLocal();
        if (local == Locale.CHINA) {
            return getCnMsg();
        } else if (local == Locale.US) {
            return getUsMsg();
        } else {
            return getCnMsg();
        }
    }

    public String getUsMsg() {
        return usMsg;
    }

    public void setUsMsg(String usMsg) {
        this.usMsg = usMsg;
    }

    public String getCnMsg() {
        return cnMsg;
    }

    public void setCnMsg(String cnMsg) {
        this.cnMsg = cnMsg;
    }
}
