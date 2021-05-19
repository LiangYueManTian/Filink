package com.fiberhome.filink.server_common.utils;

import java.util.UUID;

/**
 * @author hedongwei@wistronits.com
 * description uuid生成类
 * date 9:33 2019/1/23
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
     * 获取32位UUID
     */
    public  String UUID32() {
        return UUID36().replace("-","");
    }
    /**
     * 获取UUID:默认36位(包含一些特殊字符)
     */
    public   String UUID36() {
        return UUID.randomUUID().toString();
    }

}
