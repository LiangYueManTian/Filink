package com.fiberhome.filink.alarmcurrentserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警统计模板实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Data
@TableName("alarm_statistics_temp")
public class AlarmStatisticsTemp extends Model<AlarmStatisticsTemp> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private String id;

    /**
     * 模板编号
     */
    @TableField("code")
    private Integer code;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 统计条件
     */
    private String condition;

    /**
     * 统计类别
     */
    @TableField("page_type")
    private String pageType;

    /**
     * 统计开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 统计结束时间
     */
    @TableField("end_time")
    private Date endTime;

    /**
     * 是否删除，1没有，0已删除
     */
    @TableField("is_deleted")
    private String isDeleted;

    /**
     * 备注
     */
    private String remark;

    @TableField("create_time")
    private Date createTime;

    /**
     * 创建用户
     */
    @TableField("create_user")
    private String createUser;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AlarmStatisticsTemp{"
                + "id=" + id + ", code=" + code + ", templateName=" + templateName
                + ", condition=" + condition + ", graphType=" + pageType + ", startTime=" + startTime
                + ", endTime=" + endTime + ", isDeleted=" + isDeleted + ", remark=" + remark
                + ", createTime=" + createTime + ", createUser=" + createUser + "}";
    }
}
