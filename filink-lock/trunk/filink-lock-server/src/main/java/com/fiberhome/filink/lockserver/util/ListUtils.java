package com.fiberhome.filink.lockserver.util;

import com.alibaba.fastjson.JSON;
import org.springframework.util.Assert;

import java.util.List;

/**
 * <p>
 * List工具类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/11
 */
public class ListUtils {
    /**
     * @param source the source list
     * @param clazz  class
     */
    public static <T> List copyList(List<T> source, Class clazz) {
        Assert.notNull(source, "Source must not be empty");
        return JSON.parseArray(JSON.toJSONString(source), clazz);
    }

}
