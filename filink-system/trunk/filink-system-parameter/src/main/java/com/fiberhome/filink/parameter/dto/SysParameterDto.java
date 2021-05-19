package com.fiberhome.filink.parameter.dto;

import com.fiberhome.filink.parameter.bean.DisplaySettings;
import com.fiberhome.filink.parameter.bean.MessageNotification;
import lombok.Data;

/**
 * <p>
 *    对前端显示实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-13
 */
@Data
public class SysParameterDto {
    /**
     * 显示设置
     */
    private DisplaySettings displaySettings;
    /**
     * 消息通知设置
     */
    private MessageNotification messageNotification;

    public SysParameterDto() {}

    public SysParameterDto(DisplaySettings displaySettings, MessageNotification messageNotification) {
        this.displaySettings = displaySettings;
        this.messageNotification = messageNotification;
    }

}
