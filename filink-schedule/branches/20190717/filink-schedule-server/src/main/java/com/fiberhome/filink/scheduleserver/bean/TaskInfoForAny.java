package com.fiberhome.filink.scheduleserver.bean;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.UUID;

/**
 * 任务实体类  传入对象不受限制
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:00
 */
@Data
public class TaskInfoForAny {

    /**
     * 任务ID , 默认为UUID
     */
    private String jobId = UUID.randomUUID().toString().replace("-", "");

    /**
     * 任务名称
     */
    @NotBlank
    private String jobName;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 任务触发器名称
     */
    private String triggerName;

    /**
     * 任务触发器分组
     */
    private String triggerGroup;

    /**
     * 任务周期
     */
    private String cron;

    /**
     * 间隔周期
     */
    private Integer intervalSecond;

    /**
     * 任务执行class
     */
    private Class<? extends QuartzJobBean> tClass;

    /**
     * 传入任务的对象
     */
    private Object data;

    /**
     * 任务创建时间
     */
    private Long createTime;

    /**
     * 传入class对象 构造对象
     * @param tClass
     */
    public TaskInfoForAny(Class<? extends QuartzJobBean> tClass) {
        this.tClass = tClass;
    }

    public TaskInfoForAny() {
    }
}
