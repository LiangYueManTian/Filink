package com.fiberhome.filink.oss.constant;

import com.fiberhome.filink.bean.ResultCode;
/**
 * <p>
 *     文件服务器返回码
 * </p>
 * @author chaofang@wistronits.com
 * @since  2019/1/30
 */
public class FdfsResultCode extends ResultCode {
    /**传入参数错误*/
    public static final Integer OSS_PARAM_ERROR = 220301;
    /**文件流异常*/
    public static final Integer OSS_FILE_ERROR = 220302;
}
