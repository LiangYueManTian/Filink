package com.fiberhome.filink.securitystrategy.bean;

/**
 * 安全策略国际化实体类
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-20
 */
public class SecurityStrategyI18n {

    /**密码安全策略*/
    public static final String PASSWORD_SECURITY_STRATEGY = "passwordSecurityStrategy";
    /**账号安全策略*/
    public static final String ACCOUNT_SECURITY_STRATEGY = "accountSecurityStrategy";
    /**访问控制列表*/
    public static final String IP_RANGE_ALL = "ipRangeAll";


    /**安全策略数据库操作异常*/
    public static final String SECURITY_STRATEGY_DATABASE_ERROR = "securityStrategyDatabaseError";
    /**安全策略数据库数据异常*/
    public static final String SECURITY_STRATEGY_DATA_ERROR = "securityStrategyDataError";
    /**安全策略请求参数错误*/
    public static final String SECURITY_STRATEGY_PARAM_ERROR = "securityStrategyParamError";


    /**密码安全策略更新成功*/
    public static final String PASSWORD_STRATEGY_UPDATE_SUCCESS = "passwordStrategyUpdateSuccess";
    /**账号安全策略更新成功*/
    public static final String ACCOUNT_STRATEGY_UPDATE_SUCCESS = "accountStrategyUpdateSuccess";


    /**访问控制IP范围已删除*/
    public static final String IP_RANGE_NOT_EXIST_ERROR = "ipRangeNotExistError";
    /**访问控制IP范围与已有重叠*/
    public static final String IP_RANGE_OVERLAP_ERROR = "ipRangeOverlapError";
    /**访问控制IP范围包含已有IP范围*/
    public static final String IP_RANGE_CONTAIN_ERROR = "ipRangeContainError";
    /**访问控制IP或掩码格式不正确*/
    public static final String IP_RANGE_FORMAT_ERROR = "ipRangeFormatError";
    /**访问控制IP范围全零段开始结束IP都是*/
    public static final String IP_RANGE_NOT_ZERO_ERROR = "ipRangeNotZeroError";
    /**访问控制IP范围不能组成有效范围*/
    public static final String IP_RANGE_NOT_RANGE_ERROR = "ipRangeNotRangeError";
    /**访问控制IP范围新增成功*/
    public static final String IP_RANGE_ADD_SUCCESS = "ipRangeAddSuccess";
    /**访问控制IP范围更新成功*/
    public static final String IP_RANGE_UPDATE_SUCCESS = "ipRangeUpdateSuccess";
    /**访问控制IP范围删除成功*/
    public static final String IP_RANGE_DELETE_SUCCESS = "ipRangeDeleteSuccess";
    /**访问控制IP范围启用禁用成功*/
    public static final String IP_RANGE_ENABLE_DISABLE_SUCCESS = "ipRangeEnableOrDisableSuccess";

}
