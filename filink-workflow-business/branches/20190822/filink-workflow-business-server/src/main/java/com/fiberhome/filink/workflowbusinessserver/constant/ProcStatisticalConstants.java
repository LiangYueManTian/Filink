package com.fiberhome.filink.workflowbusinessserver.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单报表常量类
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 14:54
 */

public class ProcStatisticalConstants {

    /**
     * 工单状态集合
     */
    public static List<String> statusList = new ArrayList<>();

    /**
     * 工单未完工状态集合
     */
    public static List<String> statusProcessingList = new ArrayList<>();

    /**
     * 工单历史状态集合
     */
    public static List<String> statusCompletedList = new ArrayList<>();

    /**
     * 设施类型集合
     */
    public static List<String> deviceTypeList = new ArrayList<>();

    /**
     * 故障原因集合
     */
    public static List<String> errorReasonList = new ArrayList<>();

    /**
     * 处理方案集合
     */
    public static List<String> processingSchemeList = new ArrayList<>();


    static {
        //初始化工单状态集合
        initStatusList();

        //初始化工单未完工状态list
        initStatusProcessingList();

        //初始化工单历史状态list
        initStatusCompleteList();

        //初始化设施类型集合
        initDeviceTypeList();

        //初始化故障原因集合
        initErrorReasonList();

        //初始化处理方案集合
        initProcessingSchemeList();
    }

    /**
     * 初始化工单状态list
     * @author hedongwei@wistronits.com
     * @date  2019/5/30 14:57
     */
    private static void initStatusList() {
        statusList.add(ProcBaseConstants.PROC_STATUS_ASSIGNED);
        statusList.add(ProcBaseConstants.PROC_STATUS_PENDING);
        statusList.add(ProcBaseConstants.PROC_STATUS_PROCESSING);
        statusList.add(ProcBaseConstants.PROC_STATUS_TURN_PROCESSING);
        statusList.add(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
        statusList.add(ProcBaseConstants.PROC_STATUS_COMPLETED);
    }

    /**
     * 初始化工单未完工状态list
     * @author hedongwei@wistronits.com
     * @date  2019/5/30 14:57
     */
    private static void initStatusProcessingList() {
        statusProcessingList.add(ProcBaseConstants.PROC_STATUS_ASSIGNED);
        statusProcessingList.add(ProcBaseConstants.PROC_STATUS_PENDING);
        statusProcessingList.add(ProcBaseConstants.PROC_STATUS_PROCESSING);
        statusProcessingList.add(ProcBaseConstants.PROC_STATUS_TURN_PROCESSING);
        statusProcessingList.add(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
    }

    /**
     * 初始化工单完工状态list
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 11:31
     */
    private static void initStatusCompleteList() {
        statusCompletedList.add(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
        statusCompletedList.add(ProcBaseConstants.PROC_STATUS_COMPLETED);
    }

    /**
     * 初始化设施类型list
     * @author hedongwei@wistronits.com
     * @date  2019/5/30 14:57
     */
    private static void initDeviceTypeList() {
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_001);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_030);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_060);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_090);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_210);
    }

    /**
     * 初始化故障原因
     */
    private static void initErrorReasonList() {
        errorReasonList.add(ProcBaseConstants.ERROR_REASON_4);
        errorReasonList.add(ProcBaseConstants.ERROR_REASON_3);
        errorReasonList.add(ProcBaseConstants.ERROR_REASON_2);
        errorReasonList.add(ProcBaseConstants.ERROR_REASON_1);
        errorReasonList.add(ProcBaseConstants.ERROR_REASON_0);
    }

    /**
     * 初始化处理方案
     */
    private static void initProcessingSchemeList() {
        processingSchemeList.add(ProcBaseConstants.PROCESSING_SCHEME_0);
        processingSchemeList.add(ProcBaseConstants.PROCESSING_SCHEME_1);
        processingSchemeList.add(ProcBaseConstants.PROCESSING_SCHEME_2);
    }
}
