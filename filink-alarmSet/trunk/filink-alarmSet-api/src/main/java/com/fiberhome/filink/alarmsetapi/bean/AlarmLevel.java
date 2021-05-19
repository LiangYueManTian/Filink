package com.fiberhome.filink.alarmsetapi.bean;



import lombok.Data;



/**
 * <p>
 *  当前告警级别设置实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data

public class AlarmLevel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 告警级别代码
     */
    private String alarmLevelCode;

    /**
     * 告警级别名称
     */
    private String alarmLevelName;

    /**
     * 告警级别颜色
     */
    private String alarmLevelColor;

    /**
     * 告警级别声音路径
     */
    private String alarmLevelSound;

    /**
     * 是否播放声音,1是，0否
     */
    private Integer isPlay;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 操作
     */
    private Integer orderField;


}
