package com.fiberhome.filink.parameter.bean;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 *   消息通知配置修改接收实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
@Data
public class MessageNotificationParam {
    /**
     * 参数ID
     */
    private String paramId;
    /**
     * 消息通知配置实体类
     */
    private MessageNotification messageNotification;
    /**
     * 校验参数
     * @return true不符合 false符合
     */
    public boolean check() {
        if (StringUtils.isEmpty(paramId) || ObjectUtils.isEmpty(messageNotification)) {
            return true;
        }
        return messageNotification.checkMessageRemind();
    }
}
