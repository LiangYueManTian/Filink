package com.fiberhome.filink.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类@wistronits.com
 *
 * @author chaofang
 * create on 2018/12/18
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    private static RedisUtils redisUtils;

    private static final Long SUCCESS = 1L;

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
        return redisUtils.redisTemplate.expire(key, time, TimeUnit.SECONDS);
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
     *
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
        return redisUtils.redisTemplate.hasKey(key);
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
     */
    public static void set(String key, Object value) {
        redisUtils.redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存放入并设置缓存失效时间
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public static void set(String key, Object value, long time) {
        if (time > 0) {
            redisUtils.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
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
    public static void hSetMap(String key, Map<String, Object> map) {
        redisUtils.redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * HashSet 并设置缓存失效时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public static void hSetMap(String key, Map<String, Object> map, long time) {
        redisUtils.redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            expire(key, time);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     */
    public static void hSet(String key, String item, Object value) {
        redisUtils.redisTemplate.opsForHash().put(key, item, value);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建,并设置缓存失效时间
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     */
    public static void hSet(String key, String item, Object value, long time) {
        redisUtils.redisTemplate.opsForHash().put(key, item, value);
        if (time > 0) {
            expire(key, time);
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
        return redisUtils.redisTemplate.opsForSet().members(key);
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @return true 存在 false不存在
     */
    public static boolean sHasKey(String key, Object value) {
        return redisUtils.redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键 不能为null
     * @param values 值 可以是多个 不能为null
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        return redisUtils.redisTemplate.opsForSet().add(key, values);
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
        Long count = redisUtils.redisTemplate.opsForSet().add(key, values);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键  不能为null
     * @return 对应缓存的长度
     */
    public static long sGetSetSize(String key) {
        return redisUtils.redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除值为value的数据
     *
     * @param key    键 不能为null
     * @param values 值 可以是多个 不能为null
     * @return 移除的个数
     */
    public static long sRemove(String key, Object... values) {
        return redisUtils.redisTemplate.opsForSet().remove(key, values);
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键 不能为null
     * @param start 开始 不能为null
     * @param end   结束  0 到 -1代表所有值 不能为null
     */
    public static List<Object> lGet(String key, long start, long end) {
        return redisUtils.redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键 不能为null
     * @return 对应缓存长度
     */
    public static long lGetListSize(String key) {
        return redisUtils.redisTemplate.opsForList().size(key);
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键 不能为null
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public static Object lGetIndex(String key, long index) {
        return redisUtils.redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将数据放入list缓存
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     */
    public static void lSet(String key, Object value) {
        redisUtils.redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将数据放入list缓存并设置缓存失效时间
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     * @param time  时间(秒) 不能为null
     * @return true成功 false失败
     */
    public static void lSet(String key, Object value, long time) {
        redisUtils.redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键 不能为null
     * @param value 值 不能为null
     */
    public static void lSetList(String key, List<Object> value) {
        redisUtils.redisTemplate.opsForList().rightPushAll(key, value);
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
        Long count =  redisUtils.redisTemplate.opsForList().rightPushAll(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键 不能为null
     * @param index 索引 不能为null
     * @param value 值 不能为null
     * @return true成功 false失败
     */
    public static void lUpdateIndex(String key, long index, Object value) {
        redisUtils.redisTemplate.opsForList().set(key, index, value);
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
        return redisUtils.redisTemplate.opsForList().remove(key, count, value);
    }


    /**
     * 获取锁
     *
     * @param lockKey
     * @param value
     * @param expireTime：单位-秒
     * @return 成功猎取锁返回true
     */
    public static boolean getLock(String lockKey, String value, int expireTime) {
        try {
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
            RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);
            Object result = redisUtils.redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value, expireTime);
            if (SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey
     * @param value
     * @return
     */
    public static boolean releaseLock(String lockKey, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);
        Object result = redisUtils.redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        if (SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 获取查询锁,返回锁标识
     *
     * @param lockName       锁的key
     * @param acquireTimeout 获取锁超时时间 ms
     * @param timeout        锁的超时时间 ms
     * @return 锁标识UUID
     */
    public static String lockWithTimeout(String lockName, int acquireTimeout, int timeout) {
        // 随机生成一个value
        String value = UUID.randomUUID().toString();
        if(lockWithTimeout(lockName, value, acquireTimeout, timeout)) {
            return value;
        }
        return null;
    }

    /**
     * 获取查询锁
     * @param lockName      锁的key
     * @param requestId     请求标识码
     * @param acquireTimeout    获取锁超时时间 ms
     * @param timeout       锁的超时时间 ms
     * @return
     */
    public static boolean lockWithTimeout(String lockName, String requestId, int acquireTimeout, int timeout) {
        // 超时时间，上锁后超过此时间则自动释放锁
        int lockExpire = timeout / 1000;

        // 获取锁的超时时间，超过这个时间则放弃获取锁
        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            if (getLock(lockName, requestId, lockExpire)) {
                return true;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

}
