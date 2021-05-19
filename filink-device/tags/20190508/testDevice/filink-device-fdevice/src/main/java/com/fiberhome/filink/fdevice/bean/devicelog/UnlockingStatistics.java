package com.fiberhome.filink.fdevice.bean.devicelog;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/12 17:26
 * @Description: com.fiberhome.filink.fdevice.bean.devicelog
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName("unlocking_statistics")
public class UnlockingStatistics {
    /**
     * 开锁统计id
     */
    @TableId(value = "unlocking_statistics_id")
    private String unlockingStatisticsId;

    /**
     * 设施ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 统计日期
     */
    @TableField("statistics_date")
    private String statisticsDate;

    /**
     * 开锁次数
     */
    @TableField("unlocking_count")
    private Integer unlockingCount;

    /**
     * 当前时间
     */
    @TableField("current_time")
    private Timestamp currentTime;
}
