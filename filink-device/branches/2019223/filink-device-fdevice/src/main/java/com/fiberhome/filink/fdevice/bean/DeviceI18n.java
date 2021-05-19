package com.fiberhome.filink.fdevice.bean;

import lombok.Data;

/**
 * 设施国际化实体类
 *
 * @author WH1707069
 */
@Data
public class DeviceI18n {

    /**参数错误*/
    public static final String PARAMETER_ERROR = "parameterError";

    //device
    /**有必填参数未填写*/
    public static final String DEVICE_PARAM_ERROR = "deviceParamError";
    /**设施名称格式不正确*/
    public static final String DEVICE_NAME_ERROR =  "deviceNameError";
    /**设施名称重复*/
    public static final String DEVICE_NAME_SAME = "deviceNameSame";
    /**新增设施失败*/
    public static final String ADD_DEVICE_FAIL = "addDeviceFail";
    /**缺少设施id*/
    public static final String DEVICE_ID_LOSE = "deviceIdLose";
    /**设施不存在*/
    public static final String DEVICE_IS_NOT_EXIST = "deviceIsNotExist";
    /**修改设施失败*/
    public static final String UPDATE_DEVICE_FAIL = "updateDeviceFail";
    /**设施类型不可修改*/
    public static final String DEVICE_TYPE_NOT_MODIFY = "deviceTypeNotModify";
    /**查询设施成功*/
    public static final String QUERY_DEVICE_SUCCESS = "queryDeviceSuccess";
    /**设施名称为空*/
    public static final String DEVICE_NAME_NULL = "deviceNameNull";
    /**设施名可用*/
    public static final String DEVICE_NAME_AVAILABLE = "deviceNameAvailable";
    /**删除设施失败*/
    public static final String DELETE_DEVICE_FAIL = "deleteDeviceFail";
    /**更新设施失败*/
    public static final String DEVICE_CANNOT_UPDATE = "deviceCannotUpdate";
    /**版本信息不存在*/
    public static final String VERSION_INFOMATION_NOT_EXIST = "versionInformationNotExist";
    /**协议信息不存在*/
    public static final String PROTOCOL_INFORMATION_NOT_EXIST = "protocolInformationNotExist";
    /**添加设施成功*/
    public static final String ADD_DEVICE_SUCCESS = "addDeviceSucces";
    /**更新设施成功*/
    public static final String UPDATE_DEVICE_SUCCESS = "updateDeviceSucces";
    /**删除设施成功*/
    public static final String DELETE_DEVICE_SUCCESS = "deleteDeviceSucces";


}
