package com.fiberhome.filink.dprotocol.utils;

import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 *     设施协议返回码
 * </p>
 * @author chaofang@wistronits.com
 * @since  2019/1/30
 */
public class DeviceProtocolResultCode extends ResultCode {

    /**上传文件格式错误*/
    public static final Integer DEVICE_PROTOCOL_READ_FAIL = 210301;
    /**上传文件内容错误*/
    public static final Integer DEVICE_PROTOCOL_VERSION_ERROR = 210302;
    /**设施协议名称已存在*/
    public static final Integer DEVICE_PROTOCOL_NAME_EXIST = 210303;
    /**设施协议文件已存在*/
    public static final Integer DEVICE_PROTOCOL_FILE_EXIST = 210304;
    /**设施协议不存在*/
    public static final Integer DEVICE_PROTOCOL_NOT_EXIST = 210305;
    /**新增设施协议失败*/
    public static final Integer DEVICE_PROTOCOL_ADD_FAIL = 210306;
    /**修改设施协议失败*/
    public static final Integer DEVICE_PROTOCOL_UPDATE_FAIL = 210307;
    /**删除设施协议失败*/
    public static final Integer DEVICE_PROTOCOL_DELETE_FAIL = 210308;
    /**传入参数错误*/
    public static final Integer DEVICE_PROTOCOL_PARAM_ERROR = 210309;
    /**文件上传至文件服务器异常*/
    public static final Integer DEVICE_PROTOCOL_FILE_UPLOAD_ERROR = 210310;
    /**上传文件名称过长*/
    public static final Integer DEVICE_PROTOCOL_FILE_NAME_ERROR = 210311;
    /**上传文件尺寸过大*/
    public static final Integer DEVICE_PROTOCOL_FILE_SIZE_ERROR = 210312;
}
