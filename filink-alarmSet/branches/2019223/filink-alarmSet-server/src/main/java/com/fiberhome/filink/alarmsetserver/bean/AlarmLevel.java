package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  当前告警级别设置实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
@TableName("alarm_level")
public class AlarmLevel extends Model<AlarmLevel> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 告警级别代码
     */
    @TableField("alarm_level_code")
    private String alarmLevelCode;

    /**
     * 告警级别名称
     */
    @TableField("alarm_level_name")
    private String alarmLevelName;

    /**
     * 告警级别颜色
     */
    @TableField("alarm_level_color")
    private String alarmLevelColor;

    /**
     * 告警级别声音路径
     */
    @TableField("alarm_level_sound")
    private String alarmLevelSound;

    /**
     * 是否播放声音,1是，0否
     */
    @TableField("is_play")
    private Integer isPlay;

    /**
     * 播放次数
     */
    @TableField("play_count")
    private Integer playCount;

    /**
     * 排序字段
     */
    @TableField("order_field")
    private Integer orderField;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AlarmLevel{" +
                "id='" + id + '\'' +
                ", alarmLevelCode='" + alarmLevelCode + '\'' +
                ", alarmLevelName='" + alarmLevelName + '\'' +
                ", alarmLevelColor='" + alarmLevelColor + '\'' +
                ", alarmLevelSound='" + alarmLevelSound + '\'' +
                ", isPlay=" + isPlay +
                ", playCount=" + playCount +
                ", orderField=" + orderField +
                '}';
    }
}
