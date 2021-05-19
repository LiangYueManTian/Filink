package com.fiberhome.filink.scheduleserver.bean.inspectiontask;

import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.UUID;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/3/31 8:16
 */
@Data
public class InspectionTaskInfo {

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
    private JobGroupEnum jobGroup;

    /**
     * 任务触发器名称
     */
    private String triggerName;

    /**
     * 任务触发器分组
     */
    private JobTriggerEnum triggerGroup;

    /**
     * 任务周期
     */
    private String cron;

    /**
     * 间隔周期
     */
    private int intervalSecond;

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
    public InspectionTaskInfo(Class<? extends QuartzJobBean> tClass) {
        this.tClass = tClass;
    }

    public InspectionTaskInfo() {
    }
}
