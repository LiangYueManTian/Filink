package com.fiberhome.filink.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类@wistronits.com
 * @author  chaofang
 * create on 2018/12/18
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    private static RedisUtils redisUtils;

    @PostConstruct
    public void init() {
        redisUtils = this;
        redisUtils.redisTemplate = this.redisTemplate;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisUtils.redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static long getExpire(String key) {
        return redisUtils.redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 模糊查询key值
     * @param pattern 匹配key值
     * @return key集合Set
     */
    public static Set<String> keys(String pattern) {
        return redisUtils.redisTemplate.keys(pattern);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        try {
            return redisUtils.redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值或多个 不能为null
     */
    public static void removes(String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键 不能为null
     */
    public static void remove(String key) {
        if (hasKey(key)) {
            redisUtils.redisTemplate.delete(key);
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键 不能为null
     * @return 值
     */
    public static Object get(String key) {
        Object result = null;
        if (key != null && key != "") {
            result = redisUtils.redisTemplate.opsForValue().get(key);
        }
        return result;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value) {
        try {
            redisUtils.redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置缓存失效时间
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisUtils.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static Object hGet(String key, String item) {
        return redisUtils.redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hGetMap(String key) {
        return redisUtils.redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static boolean hSetMap(String key, Map<String, Object> map) {
        try {
            redisUtils.redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置缓存失效时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false失败
     */
    public static boolean hSetMap(String key, Map<String, Object> map, long time) {
        try {
            redisUtils.redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static boolean hSet(String key, String item, Object value) {
        try {
            redisUtils.redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建,并设置缓存失效时间
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static boolean hSet(String key, String item, Object value, long time) {
        try {
            redisUtils.redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以是多个 不能为null
     */
    public static void hRemove(String key, Object... item) {
        redisUtils.redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String item) {
        return redisUtils.redisTemplate.opsForHash().hasKey(key, item);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键 不能为null
     * @return 对应set集合
     */
    public static Set<Object> sGet(String key) {
        try {
            return redisUtils.redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @return true 存在 false不存在
     */
    public static boolean sHasKey(String key, Object value) {
        try {
            return redisUtils.redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键 不能为null
     * @param values 值 可以是多个 不能为null
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        try {
            return redisUtils.redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将数据放入set缓存并设置缓存失效时间
     *
     * @param key    键 不能为null
     * @param time   时间(秒) 不能为null
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, long time, Object... values) {
        Long count = 0L;
        try {
            count = redisUtils.redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return count;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键  不能为null
     * @return 对应缓存的长度
     */
    public static long sGetSetSize(String key) {
        try {
            return redisUtils.redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的数据
     *
     * @param key    键 不能为null
     * @param values 值 可以是多个 不能为null
     * @return 移除的个数
     */
    public static long sRemove(String key, Object... values) {
        Long count = 0L;
        try {
            count = redisUtils.redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return count;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键 不能为null
     * @param start 开始 不能为null
     * @param end   结束  0 到 -1代表所有值 不能为null
     * @return
     */
    public static List<Object> lGet(String key, long start, long end) {
        try {
            return redisUtils.redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键 不能为null
     * @return 对应缓存长度
     */
    public static long lGetListSize(String key) {
        Long count = 0L;
        try {
            count = redisUtils.redisTemplate.opsForList().size(key);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return count;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键 不能为null
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public static Object lGetIndex(String key, long index) {
        try {
            return redisUtils.redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将数据放入list缓存
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @return true成功 false失败
     */
    public static boolean lSet(String key, Object value) {
        try {
            redisUtils.redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入list缓存并设置缓存失效时间
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @param time  时间(秒) 不能为null
     * @return true成功 false失败
     */
    public static boolean lSet(String key, Object value, long time) {
        try {
            redisUtils.redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @return true成功 false失败
     */
    public static long lSetList(String key, List<Object> value) {
        Long count = 0L;
        try {
            count = redisUtils.redisTemplate.opsForList().rightPushAll(key, value);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return count;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @param time  时间(秒) 不能为null
     * @return true成功 false失败
     */
    public static long lSetList(String key, List<Object> value, long time) {
        Long count = 0L;
        try {
            count = redisUtils.redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return count;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键 不能为null
     * @param index 索引 不能为null
     * @param value 值 不能为null
     * @return true成功 false失败
     */
    public static boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisUtils.redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键 不能为null
     * @param count 移除多少个 不能为null
     * @param value 值 不能为null
     * @return 移除的个数
     */
    public static long lRemove(String key, long count, Object value) {
        Long removeCount = 0L;
        try {
            removeCount = redisUtils.redisTemplate.opsForList().remove(key, count, value);
            return removeCount;
        } catch (Exception e) {
            e.printStackTrace();
            return removeCount;
        }
    }
}
