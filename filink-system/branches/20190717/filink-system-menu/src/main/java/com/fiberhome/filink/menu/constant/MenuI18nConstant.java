package com.fiberhome.filink.menu.constant;

import lombok.Data;

/**
 * 设施国际化实体类
 *
 * @author WH1707069
 */
@Data
public class MenuI18nConstant {
    /**
     * 菜单名称为null
     */
    public static final String MENU_NAME_NULL = "MENU_NAME_NULL";
    /**
     * 有必填字段为null
     */
    public static final String PARAM_NULL = "PARAM_NULL";
    /**
     * 选择要操作的模板
     */
    public static final String SELECT_THE_MENU_TEMPLATE = "SELECT_THE_MENU_TEMPLATE";
    /**
     * 数据异常
     */
    public static final String DIRTY_DATA = "DIRTY_DATA";
    /**
     * 版本不一致
     */
    public static final String INCONSISTENT_VERSION = "INCONSISTENT_VERSION";
    /**
     * 数据库异常
     */
    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    /**
     * 名称已经存在
     */
    public static final String NAME_IS_EXIST = "NAME_IS_EXIST";
    /**
     * 名称不存在
     */
    public static final String NAME_IS_AVAILABLE = "NAME_IS_AVAILABLE";
    /**
     * 增加数据成功
     */
    public static final String INCREASE_DATA_SUCCESS = "INCREASE_DATA_SUCCESS";
    /**
     * 增加数据失败
     */
    public static final String INCREASE_DATA_FAILURE = "INCREASE_DATA_FAILURE";
    /**
     * 更新数据成功
     */
    public static final String UPDATE_SUCCESS = "UPDATE_SUCCESS";
    /**
     * 模板已经启用
     */
    public static final String MENU_TEMPLATE_IS_OPEN = "MENU_TEMPLATE_IS_OPEN";
    /**
     * 正在使用的模板
     */
    public static final String TEMPLATE_IN_USE = "TEMPLATE_IN_USE";
    /**
     * 删除成功
     */
    public static final String SUCCESSFULLY_DELETED = "SUCCESSFULLY_DELETED";
    /**
     * 正在使用的菜单模板已经被修改
     */
    public static final String USED_MENU_TEMPLATE_UPDATE = "USED_MENU_TEMPLATE_UPDATE";
    /**
     * 参数格式不正确
     */
    public static final String PARAMETER_FORMAT_IS_INCORRECT = "PARAMETER_FORMAT_IS_INCORRECT";
    /**
     * 该菜单模板已删除
     */
    public static final String MENU_TEMPLATE_HAS_BEEN_DELETED = "MENU_TEMPLATE_HAS_BEEN_DELETED";
    /**
     * 存在已删除的菜单模板
     */
    public static final String DELETED_MENU_TEMPLATE_EXISTS = "DELETED_MENU_TEMPLATE_EXISTS";
    /**
     * 服务器繁忙
     */
    public static final String BUSY_SERVER = "BUSY_SERVER";

}
