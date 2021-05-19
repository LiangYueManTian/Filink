package com.fiberhome.filink.securitystrategy.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 *     安全策略返回码
 * </p>
 * @author chaofang@wistronits.com
 * @since  2019/2/25
 */
public class SecurityStrategyResultCode extends ResultCode {


    /**安全策略数据库操作异常*/
    public static final Integer SECURITY_STRATEGY_DATABASE_ERROR = 210501;
    /**安全策略数据库数据异常*/
    public static final Integer SECURITY_STRATEGY_DATA_ERROR = 210502;
    /**安全策略请求参数错误*/
    public static final Integer SECURITY_STRATEGY_PARAM_ERROR = 210503;


    /**访问控制IP范围已删除*/
    public static final Integer IP_RANGE_NOT_EXIST_ERROR = 210504;
    /**访问控制IP范围与已有重叠*/
    public static final Integer IP_RANGE_OVERLAP_ERROR = 210505;
    /**访问控制IP范围包含已有IP范围*/
    public static final Integer IP_RANGE_CONTAIN_ERROR = 210506;
    /**访问控制IP或掩码格式不正确*/
    public static final Integer IP_RANGE_FORMAT_ERROR = 210507;
    /**访问控制IP范围全零段开始结束IP都是*/
    public static final Integer IP_RANGE_NOT_ZERO_ERROR = 210508;
    /**访问控制IP范围不能组成有效范围*/
    public static final Integer IP_RANGE_NOT_RANGE_ERROR = 210509;

}
