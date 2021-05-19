package com.fiberhome.filink.parameter.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 系统参数返回码
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-13
 */
public class SystemParameterResultCode extends ResultCode {

    /**系统参数请求参数错误*/
    public static final Integer SYSTEM_PARAMETER_PARAM_ERROR = 210801;
    /**系统参数数据库数据异常*/
    public static final Integer SYSTEM_PARAMETER_DATA_ERROR = 210802;
    /**系统参数数据库操作异常*/
    public static final Integer SYSTEM_PARAMETER_DATABASE_ERROR = 210803;
    /**显示设置系统Logo规格错误*/
    public static final Integer SYSTEM_LOGO_SIZE_ERROR = 210804;
    /**显示设置系统Logo格式错误*/
    public static final Integer SYSTEM_LOGO_FORMAT_ERROR = 210805;
    /**显示设置系统Logo上传至文件服务器错误*/
    public static final Integer SYSTEM_LOGO_UPLOAD_ERROR = 210806;
    /**系统参数服务设置测试失败*/
    public static final Integer SYSTEM_PARAMETER_TEST_FAIL = 210807;
    /**
     * FTP 登录名或密码不正确
     */
    public static final Integer FTP_LOGIN_ERROR = 210808;
    /**
     *APP FTP IP地址端口不正确
     */
    public static final Integer APP_FTP_HOST_PORT_ERROR = 210809;
    /**
     * WEB FTP 登录名或密码不正确
     */
    public static final Integer WEB_FTP_HOST_PORT_ERROR = 210810;
    /**
     * FTP服务器异常或FTP服务设置错误
     */
    public static final Integer FTP_GET_ERROR = 210811;
}
