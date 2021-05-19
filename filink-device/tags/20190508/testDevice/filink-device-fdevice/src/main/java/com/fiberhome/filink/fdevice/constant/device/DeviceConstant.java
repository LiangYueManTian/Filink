package com.fiberhome.filink.fdevice.constant.device;

/**
 * 自定义常量类
 *
 * @Author zepenggao@wistronits.com
 * @Date 2019/1/8
 */
public class DeviceConstant {

    /**
     * 设施缓存区
     */
    public static final String DEVICE_GIS_MAP = "deviceGisMap";
    /**
     * 设施配置
     */
    public static final String DEVICE_CONFIG_KEY = "deviceConfig";

    /**
     * 设施配置编码
     */
    public static final String DEVICE_CONFIG_CODE = "filink";

    /**
     * 用户已关注设施
     */
    public static final String DEVICE_IS_COLLECTED = "1";
    /**
     * 用户未关注设施
     */
    public static final String DEVICE_IS_NOT_COLLECTED = "0";

    /**
     * 收藏设施
     */
    public static final String FOCUS_DEVICE = "0";

    /**
     * 取消收藏设施
     */
    public static final String UNFOLLOW_DEVICE = "1";


    /**
     * 区域缓存区
     */
    public static final String AREAINFO_FOREIGN = "areaInfoForeign";

    /**
     * ADMIN用户
     */
    public static final String ADMIN = "1";

    /**
     * UPDATE_USER_INFO
     */
    public static final String UPDATE_USER_INFO = "UPDATE_USER_INFO";

    /**
     * 删除标记
     */
    public static final String DELETE_TAG = "1";

    /**
     * 非删除标记
     */
    public static final String NON_DELETE_TAG = "0";

    /**
     * 设施包含电子锁
     */
    public static final String DEVICE_CONTAINS_LOCKS = "1";

    /**
     * 设施不包含电子锁
     */
    public static final String DEVICE_DOES_NOT_CONTAIN_LOCKS = "0";

    /**
     * 实景图标识
     */
    public static final String DEVICE_PIC_RESOURCE_REALISTIC = "3";
    /**
     * 首页首次加载阈值（设施数量）
     */
    public static final Integer DEFAULT_HOME_DEVICE_LIMIT = 20000;
    /**
     * 首页首次加载阈值（设施数量）
     */
    public static final String HOME_DEVICE_LIMIT = "HOME_DEVICE_LIMIT";
    /**
     * 首页首次加载是大数据
     */
    public static final String HOME_HUGE_DATA = "1";
    /**
     * 首页首次加载不是大数据
     */
    public static final String HOME_HUGE_NOT_DATA = "0";
    /**
     * 事件 用于设施日志的导出
     */
    public static final String EVENT = "EVENT";
}
