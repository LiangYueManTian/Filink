package com.fiberhome.filink.workflowbusinessserver.utils.procinspection;

/**
 * 巡检工单返回提示信息
 * @author hedongwei@wistronits.com
 * @date 2019/4/1 20:32
 */
public class ProcInspectionResultCode {

    /**
     * 巡检设施为空
     */
    public static final Integer INSPECTION_DEVICE_IS_EMPTY = 163210;

    /**
     * 巡检设施失败
     */
    public static final Integer INSPECTION_DEVICE_FAILED = 163214;

    /**
     * 用户编号为空
     */
    public static final Integer USER_ID_IS_EMPTY = 163218;

    /**
     * 提交巡检失败
     */
    public static final Integer COMMIT_INSPECTION_FAILED = 163222;

    /**
     * 设施没有巡检完成
     */
    public static final Integer INSPECTION_DEVICE_NOT_COMPLETE = 163226;
}
