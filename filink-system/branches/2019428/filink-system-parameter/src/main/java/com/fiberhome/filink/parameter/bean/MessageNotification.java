package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.utils.SystemParameterConstants;
import com.fiberhome.filink.parameter.utils.SystemParameterLimitEnum;
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
     * 校验启用/停用 消息提醒
     * @return true不符合 false符合
     */
    public boolean checkMessageRemind() {
        String regex = "^(0|1)$";
        return StringUtils.isEmpty(messageRemind) || !messageRemind.matches(regex);
    }
    /**
     * 校验消息保留时间
     * @return true不符合 false符合
     */
    public boolean checkRetentionTime() {
        return SystemParameterLimitEnum.RETENTION_TIME.checkValue(retentionTime);
    }
    /**
     * 校验启用/停用 声音提醒
     * @return true不符合 false符合
     */
    public boolean checkSoundRemind() {
        return StringUtils.isEmpty(soundRemind) || !soundRemind.matches(SystemParameterConstants.ONE_ZERO_REGEX);
    }
    /**
     * 校验选择提醒音
     * @return true不符合 false符合
     */
    public boolean checkSoundSelected() {
        return StringUtils.isEmpty(soundSelected) || !soundSelected.matches(SystemParameterConstants.SOUND_REGEX);
    }
}
