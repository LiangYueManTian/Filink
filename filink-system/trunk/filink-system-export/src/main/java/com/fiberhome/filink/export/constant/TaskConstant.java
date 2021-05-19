package com.fiberhome.filink.export.constant;

/**
 * 导出服务常量
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 18:28
 */
public class TaskConstant {
    /**
     * 已经删除
     */
    public static final String DELETED = "1";
    /**
     * 删除过期任务消息码
     */
    public static final int DELETE_OVERDUE_TASK = 1;
    /**
     * TASK常量
     */
    public static final String TASK = "task";
    /**
     * 过期天数
     */
    public static final String DAYS_OVERDUE = "${daysOverdue}";
    /**
     * 最大任务数
     */
    public static final String MAX_TASK_NUM = "${maxTaskNum}";
}
