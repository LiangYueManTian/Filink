package com.fiberhome.filink.userserver.utils;

import com.fiberhome.filink.bean.NineteenUUIDUtils;

/**
 * <p>
 *  UUID 生成类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 9:37 2019/1/9 0009
 */
public class UUIDUtil {

    /**
     * 单例模式
     */
    private static UUIDUtil instance = new UUIDUtil();

    /**
     * 防止外部实例化
     */
    private UUIDUtil() {
    }
    /**
     * 单例模式,获取单例
     *
     * @return SequenceUtils的单例对象
     */
    public static UUIDUtil getInstance() {
        return instance;
    }
    /**
     * 获取指定位数的UUID
     */
    public  String UUID32() {

        return NineteenUUIDUtils.uuid();
    }
}
