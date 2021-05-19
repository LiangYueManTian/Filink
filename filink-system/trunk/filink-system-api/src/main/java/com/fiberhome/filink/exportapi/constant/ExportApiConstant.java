package com.fiberhome.filink.exportapi.constant;

/**
 * 列表导出api常量类
 *
 * @author qiqizhu@wistronits.com
 * create on 2019-03-28 21:51
 */
public class ExportApiConstant {
    /**
     * 超过最大任务数返回码
     */
    public static final Integer TASK_NUM_TOO_BIG_CODE = 210903;
    /**
     * 每个文件记录数据的最大条数的key值
     */
    public static final String LIST_EXCEL_SIZE = "${listExcelSize}";
    /**
     * 临时文件的路径的key值
     */
    public static final String LIST_EXCEL_FILE_PATH = "${readListExcelPath}";
    /**
     * 最大导出条数的key值
     */
    public static final String MAX_EXPORT_DATA_SIZE = "${maxExportDataSize}";
    /**
     * 时区
     */
    public static final String TIME_ZONE = "timeZone";
    /**
     * 用户id
     */
    public static final String USER_ID = "userId";
    /**
     * 语言环境
     */
    public static final String LOCALES = "locales";
    /**
     * 数据集合
     */
    public static final String OBJECT_LIST = "objectList";
    /**
     * token
     */
    public static final String TOKEN = "token";
}
