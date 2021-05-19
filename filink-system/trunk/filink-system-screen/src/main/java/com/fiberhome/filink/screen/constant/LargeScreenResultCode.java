package com.fiberhome.filink.screen.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 *     大屏管理 返回码11
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-28
 */
public class LargeScreenResultCode extends ResultCode {
    /**
     * 请求参数错误
     */
    public static final Integer LARGE_SCREEN_PARAM_ERROR = 211101;
    /**
     * 大屏名称重复
     */
    public static final Integer LARGE_SCREEN_NAME_REPEAT = 211102;
    /**
     * 大屏名称修改失败
     */
    public static final Integer LARGE_SCREEN_NAME_UPDATE_FAIL = 211103;
}
