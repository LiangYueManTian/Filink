package com.fiberhome.filink.export.utils;


import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 * 列表导出返回码
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public class ExportResultCode extends ResultCode {
    /**
     * 必填参数为null
     */
    public static final Integer PARAM_NULL = 210501;
    /**
     * 任务不存在
     */
    public static final Integer TASK_DOES_NOT_EXIST = 210502;
    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 210503;
}
