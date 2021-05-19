package com.fiberhome.filink.license.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * License返回码
 *
 * @Author: zl
 * @Date: 2019/3/29 10:10
 * @Description: com.fiberhome.filink.license.util
 * @version: 1.0
 */
public class LicenseResultCode extends ResultCode {

    /**
     * 文件不合法
     */
    public static final Integer ILLEGAL_LICENSE_FILE = 210401;

    /**
     * License文件超长
     */
    public static final Integer LICENSE_FILE_TOO_LARGE = 210402;
}
