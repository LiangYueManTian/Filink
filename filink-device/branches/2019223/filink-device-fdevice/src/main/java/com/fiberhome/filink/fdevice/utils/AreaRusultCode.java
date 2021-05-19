package com.fiberhome.filink.fdevice.utils;


import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 * AreaRusultCode 区域返回码
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class AreaRusultCode extends ResultCode {
    /**
     * 区域名称为null
     */
    public static final Integer AREA_NAME_NULL = 130101;
    /**
     * 区域id为null
     */
    public static final Integer AREA_ID_NULL = 130102;
    /**
     * 必填参数为null
     */
    public static final Integer PARAM_NULL = 130103;
    /**
     * 数据异常
     */
    public static final Integer DIRTY_DATA = 130104;
    /**
     * 数据库操作异常
     */
    public static final Integer DATE_BASE_ERROR = 130105;
    /**
     * 存在子区域
     */
    public static final Integer HAVE_CHILD = 130106;
    /**
     * 存在关联设施
     */
    public static final Integer HAVE_DEVICE = 130107;
    /**
     * 区域名称格式不正确
     */
    public static final Integer AREA_NAME_FORMAT_IS_INCORRECT = 130108;
    /**
     * 该区域不存在
     */
    public static final Integer THIS_AREA_DOES_NOT_EXIST =130109;
    /**
     * 名称已经存在
     */
    public static final Integer NAME_IS_EXIST =130110;
    /**
     * 关联设施失败
     */
    public static final Integer RELATED_FACILITIES_FAIL = 130111;
    /**
     * 不能
     */
    public static final Integer FALSE = 130112;
    /**
     * 有工单或者告警
     */
    public static final Integer HAVE_A_WORK_ORDER_OR_AN_ALARM = 130113;
    /**
     * 参数格式不正确
     */
    public static final Integer PARAMETER_FORMAT_IS_INCORRECT =130114 ;
}
