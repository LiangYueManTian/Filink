package com.fiberhome.filink.dump.constant;

/**
 * 列表导出常量类
 *
 * @author qiqizhu@wistronits.com
 * create on 2019-03-28 21:51
 */
public class ExportApiConstant {

    /**
     * 每个文件记录数据的最大条数的key值
     */
    public static final String LIST_EXCEL_SIZE = "${listExcelSize}";

    /**
     * 每个文件记录数据的最大条数的key值
     */
    public static final Integer MAX_SHEET_ROW_NUM = 50000;

    /**
     *临时文件的路径的key值
     */
    public static final String LIST_EXCEL_FILE_PATH = "${readListExcelPath}";
    /**
     *最大导出条数的key值
     */
    public static final String MAX_EXPORT_DATA_SIZE = "${maxExportDataSize}";


    /**
     * 启用转储
     */
    public static final String ENABLE_DUMP_DATA = "1";

    /**
     * 根据数量来进行转储
     */
    public static final String DUMP_DATA_BY_NUMBER = "0";

    /**
     * 根据时间来进行转储
     */
    public static final String DUMP_DATA_BY_MONTH = "1";

    /**
     * 告警查询的字段
     */
    public static final String ALARM_QUERY = "alarm_begin_time";

    /**
     *  日志查询的字段
     */
    public static final String LOG_QUERY = "createTime";

    /**
     *  设施日志的时间字段
     */
    public static final String DEVICE_LOG_QUERY = "currentTime";

    /**
     * 服务名
     */
    public static final String SERVER_NAME = "serverName";

    /**
     * 列名
     */
    public static final String LIST_NAME = "listName";

    /**
     * 给导出数据添加锁
     */
    public static final String LOCK_NAME = "EXPORT_API";

    /**
     * 告警列表
     */
    public static final String ALARM_LIST_NAME = "alarmList";

    /**
     * 操作日志
     */
    public static final String OPERA_LOG_LIST_NAME = "operLogList";

    /**
     * 系统日志
     */
    public static final String SYS_LOG_LIST_NAME = "sysLogList";

    /**
     * 安全日志
     */
    public static final String SEC_LOG_LIST_NAME = "secLogList";

    /**
     * 设施日志
     */
    public static final String DEVICE_LOG_LIST_NAME = "deviceLogList";

    /**
     * 转储告警日志
     */
    public static final Integer ALARM_LOG_TYPE = 1;

    /**
     * 转储日志
     */
    public static final Integer LOG_TYPE = 2;

    /**
     * 转储设施日志类型
     */
    public static final Integer DEVICE_LOG_TYPE = 3;

    /**
     * 转储所有日志
     */
    public static final Integer ALL_LOG_TYPE = 4;

    /**
     * 转储文件在上的路径
     */
    public static final String FILE_PATH = "/backup";

    /**
     * 转储操作删除
     */
    public static final String DUMP_OPERATION_DELETE = "0";

    /**
     * 转储操作保留
     */
    public static final String DUMP_OPERATION_RETAIN = "1";
}
