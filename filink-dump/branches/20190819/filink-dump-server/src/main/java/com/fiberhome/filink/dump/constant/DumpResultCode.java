package com.fiberhome.filink.dump.constant;

/**
 * 转储返回code码
 * @author hedongwei@wistronits.com
 * @date 2019/8/8 9:42
 */
public class DumpResultCode {

    /**
     * 没有足够的转储数据
     */
    public static final Integer NOT_ENOUGH_DUMP_DATA = 280010;

    /**
     * 指定时间内已经进行数据转储了
     */
    public static final Integer SPECIFIED_TIME_HAS_DUMP_DATA = 280020;


    /**
     * 未到达转储时间
     */
    public static final Integer DUMP_TIME_NOT_REACHED = 280030;

    /**
     * 未开启转储功能
     */
    public static final Integer DISABLE_DUMP_DATA = 280000;

    /**
     * 超过最大任务数返回码
     */
    public static final Integer TASK_NUM_TOO_BIG_CODE = 210503;

    /**
     * 必填参数为null
     */
    public static final Integer PARAM_NULL = 210901;

    /**
     * 任务不存在
     */
    public static final Integer TASK_DOES_NOT_EXIST = 210902;

    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 210903;

    /**
     * 异常code
     */
    public static final Integer ERROR_CODE = -1;
}
