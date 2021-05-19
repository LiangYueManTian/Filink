package com.fiberhome.filink.menu.utils;


import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 * 菜单返回码
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public class MenuRusultCode extends ResultCode {
    /**
     * 菜单名称为null
     */
    public static final Integer MENU_NAME_NULL = 210201;
    /**
     * 必填参数为null
     */
    public static final Integer PARAM_NULL = 210203;
    /**
     * 数据异常
     */
    public static final Integer DIRTY_DATA = 210204;
    /**
     * 被改变的版本
     */
    public static final Integer INCONSISTENT_VERSION = 210205;
    /**
     * 数据库异常
     */
    public static final Integer DATE_BASE_ERROR = 210205;
    /**
     * 已启用的模板
     */
    public static final Integer MENU_TEMPLATE_IS_OPEN = 210206;
    /**
     * 名称已存在
     */
    public static final Integer NAME_IS_EXIST = 210207;
    /**
     * 参数格式不正确
     */
    public static final Integer PARAMETER_FORMAT_IS_INCORRECT = 210208;
    /**
     * 存在已删除的菜单模板
     */
    public static final Integer DELETED_MENU_TEMPLATE_EXISTS = 210209;
    /**
     * 该菜单模板已删除
     */
    public static final Integer MENU_TEMPLATE_HAS_BEEN_DELETED = 210210;
}
