package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *  清除告警 实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/1
 */
@Data
public class RemoveAlarm {
    /**
     * 主控id
     */
    private String controlId;
    /**
     * 告警类型
     */
    private List<String> alarmType;
}
