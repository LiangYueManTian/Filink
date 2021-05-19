package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
import com.fiberhome.filink.parameter.constant.SystemParameterLimitEnum;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   消息通知配置实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
@Data
public class MessageNotification {
    /**
     * 启用/停用 消息提醒
     */
    private String messageRemind;
    /**
     * 消息保留时间
     */
    private Integer retentionTime;
    /**
     * 启用/停用 声音提醒
     */
    private String soundRemind;
    /**
     * 选择提醒音
     */
    private String soundSelected;

    /**
     * 校验参数是否符合规范
     * @return true不符合 false符合
     */
    public boolean checkMessageRemind() {
        if (StringUtils.isEmpty(messageRemind) || StringUtils.isEmpty(soundRemind)
                || StringUtils.isEmpty(soundSelected)) {
            return true;
        }
        if (SystemParameterLimitEnum.RETENTION_TIME.checkValue(retentionTime)
                || !soundSelected.matches(SystemParameterConstants.SOUND_REGEX)) {
            return true;
        }
        return !messageRemind.matches(SystemParameterConstants.ONE_ZERO_REGEX)
                || !soundRemind.matches(SystemParameterConstants.ONE_ZERO_REGEX);
    }
}
