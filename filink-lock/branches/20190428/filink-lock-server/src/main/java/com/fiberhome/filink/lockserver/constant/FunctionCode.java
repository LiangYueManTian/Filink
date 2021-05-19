package com.fiberhome.filink.lockserver.constant;

/**
 * 电子锁功能码
 *
 * @author CongcaiYu
 */
public class FunctionCode {
    /**
     * 开锁
     */
    public static final String OPEN_LOCK_CODE = "1402001";
    /**
     * 更新电子锁
     */
    public static final String UPDATE_LOCK_INFO = "1402002";
    /**
     * 新增电子锁
     */
    public static final String ADD_LOCK_INFO = "1402003";
    /**
     * 删除电子锁
     */
    public static final String DELETE_LOCK_INFO = "1402004";
    /**
     * 配置设施策略
     */
    public static final String SET_CONFIG_CODE = "1401001";
    /**
     * 更新主控信息
     */
    public static final String UPDATE_CONTROL_INFO = "1401002";
    /**
     * 新增主控信息
     */
    public static final String ADD_CONTROL_INFO = "1401003";
    /**
     * 删除主控(及电子锁)
     */
    public static final String DELETE_CONTROL_INFO = "1401004";
    /**
     * 布防主控
     */
    public static final String DEPLOY_CONTROL= "1401005";
    /**
     * 撤防主控
     */
    public static final String NODEFENCE_CONTROL= "1401006";
    /**
     * 停用主控
     */
    public static final String NOTUSED_CONTROL= "1401007";
    /**
     * 维护主控
     */
    public static final String MAINTENANCE_CONTROL= "1401008";
}
