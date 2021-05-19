package com.fiberhome.filink.fdevice.constant.device;

import lombok.Data;

/**
 * 设施国际化实体类
 *
 * @author WH1707069
 */
@Data
public class DeviceI18n {

    /**
     * 参数错误
     */
    public static final String PARAMETER_ERROR = "PARAMETER_ERROR";

    /**
     * 有必填字段为null
     */
    public static final String PARAM_NULL = "PARAM_NULL";

    //device
    /**
     * 有必填参数未填写
     */
    public static final String DEVICE_PARAM_ERROR = "DEVICE_PARAM_ERROR";
    /**
     * 设施名称格式不正确
     */
    public static final String DEVICE_NAME_ERROR = "DEVICE_NAME_ERROR";
    /**
     * 设施名称重复
     */
    public static final String DEVICE_NAME_SAME = "DEVICE_NAME_SAME";
    /**
     * 新增设施失败
     */
    public static final String ADD_DEVICE_FAIL = "ADD_DEVICE_FAIL";
    /**
     * 缺少设施id
     */
    public static final String DEVICE_ID_LOSE = "DEVICE_ID_LOSE";
    /**
     * 设施不存在
     */
    public static final String DEVICE_IS_NOT_EXIST = "DEVICE_IS_NOT_EXIST";
    /**
     * 设施数据传输失败
     */
    public static final String COPYING_PROPERTIES_FAILED = "COPYING_PROPERTIES_FAILED";
    /**
     * 修改设施失败
     */
    public static final String UPDATE_DEVICE_FAIL = "UPDATE_DEVICE_FAIL";
    /**
     * 设施类型不可修改
     */
    public static final String DEVICE_TYPE_NOT_MODIFY = "DEVICE_TYPE_NOT_MODIFY";
    /**
     * 查询设施成功
     */
    public static final String QUERY_DEVICE_SUCCESS = "QUERY_DEVICE_SUCCESS";
    /**
     * 设施名称为空
     */
    public static final String DEVICE_NAME_NULL = "DEVICE_NAME_NULL";
    /**
     * 设施名可用
     */
    public static final String DEVICE_NAME_AVAILABLE = "DEVICE_NAME_AVAILABLE";
    /**设施不可删除*/
//    public static final String DEVICE_NOT_DELETE = "DEVICE_NOT_DELETE";
    /**设施关联告警不可删除*/
//    public static final String DEVICE_NOT_DELETE_WITH_ALARM = "DEVICE_NOT_DELETE_WITH_ALARM";
    /**设施关联智能标签不可删除*/
    public static final String DEVICE_NOT_DELETE_WITH_LABEL = "DEVICE_NOT_DELETE_WITH_LABEL";
    /**
     * 删除设施失败
     */
    public static final String DELETE_DEVICE_FAIL = "DELETE_DEVICE_FAIL";
    /**
     * 设施无法更新
     */
    public static final String DEVICE_CANNOT_UPDATE = "DEVICE_CANNOT_UPDATE";
    /**
     * 版本信息不存在
     */
    public static final String VERSION_INFORMATION_NOT_EXIST = "VERSION_INFORMATION_NOT_EXIST";
    /**
     * 协议信息不存在
     */
    public static final String PROTOCOL_INFORMATION_NOT_EXIST = "PROTOCOL_INFORMATION_NOT_EXIST";

    /**
     * 新增设施成功
     */
    public static final String ADD_DEVICE_SUCCESS = "ADD_DEVICE_SUCCESS";
    /**
     * 更新设施成功
     */
    public static final String UPDATE_DEVICE_SUCCESS = "UPDATE_DEVICE_SUCCESS";
    /**
     * 删除设施成功
     */
    public static final String DELETE_DEVICE_SUCCESS = "DELETE_DEVICE_SUCCESS";

    /**
     * 新增了设施
     */
    public static final String DEVICE_WAS_ADDED = "DEVICE_WAS_ADDED";
    /**
     * 更新了设施
     */
    public static final String DEVICE_WAS_UPDATED = "DEVICE_WAS_UPDATED";
    /**
     * 删除了设施
     */
    public static final String DEVICES_WERE_DELETED = "DEVICES_WERE_DELETED";


    /**
     * 导出参数为空
     */
    public static final String EXPORT_PARAM_NULL = "EXPORT_PARAM_NULL";
    /**
     * 创建导出任务失败
     */
    public static final String FAILED_TO_CREATE_EXPORT_TASK = "FAILED_TO_CREATE_EXPORT_TASK";
    /**
     * 创建导出任务成功
     */
    public static final String THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY = "THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY";
    /**
     * 设施列表名称
     */
    public static final String DEVICE_INFO_LIST_NAME = "DEVICE_INFO_LIST_NAME";
    /**
     * 导出超过最大限制
     */
    public static final String EXPORT_DATA_TOO_LARGE = "EXPORT_DATA_TOO_LARGE";

    /**
     * 超过License限制值
     */
    public static final String DEVICE_COUNT_EXCEEDS_LICENSE_LIMIT = "DEVICE_COUNT_EXCEEDS_LICENSE_LIMIT";

    /**
     * 调用license feign失败
     */
    public static final String FAIL_TO_INVOKE_LICENSE_FEIGN = "FAIL_TO_INVOKE_LICENSE_FEIGN";

    /**
     * 没有数据导出
     */
    public static final String EXPORT_NO_DATA = "EXPORT_NO_DATA";

    /**
     * 用户服务异常
     */
    public static final String USER_SERVER_ERROR = "USER_SERVER_ERROR";

    /**
     * 获取用户权限信息异常
     */
    public static final String USER_AUTH_INFO_ERROR = "USER_AUTH_INFO_ERROR";

    /**
     * 用户设施未授权
     */
    public static final String USER_DEVICE_NOT_AUTHORIZED = "USER_DEVICE_NOT_AUTHORIZED";

    /**
     * 当前用户超过最大任务数量
     */
    public static final String EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = "EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS";


    /**
     * 删除关联工单出现异常
     */
    public static final String EXCEPTION_WHILE_DELETING_PROC_BASE = "EXCEPTION_WHILE_DELETING_PROC_BASE";

    /**
     * 删除关联的巡检任务出现异常
     */
    public static final String EXCEPTION_WHILE_DELETING_INSPECTION_TASK = "EXCEPTION_WHILE_DELETING_INSPECTION_TASK";

    /**
     * 删除关联告警出现异常
     */
    public static final String EXCEPTION_WHILE_DELETING_ALARM = "EXCEPTION_WHILE_DELETING_ALARM";

    /**
     * 删除关联图片出现异常
     */
    public static final String EXCEPTION_WHILE_DELETING_DEVICE_PIC = "EXCEPTION_WHILE_DELETING_DEVICE_PIC";

    /**
     * 删除关联主控出现异常
     */
    public static final String EXCEPTION_WHILE_DELETING_CONTROL = "EXCEPTION_WHILE_DELETING_CONTROL";

    /**
     * 保存设施模板异常
     */
    public static final String EXCEPTION_WHILE_SAVING_DEVICE_TEMPLATE = "EXCEPTION_WHILE_SAVING_DEVICE_TEMPLATE";

    public static final String RFID_CORE_KEY = "RFID_CORE";
    public static final String RFID_PORT_CABLE_KEY = "RFID_PORT_CABLE";
    public static final String RFID_JUMP_KEY = "RFID_JUMP";
    public static final String RFID_OPTIC_KEY = "RFID_OPTIC";

    /**
     * 设施类型未配置
     */
    public static final String DEVICE_TYPE_NOT_CONFIGURED = "DEVICE_TYPE_NOT_CONFIGURED";
}
