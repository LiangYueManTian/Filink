package com.fiberhome.filink.bean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间处理工具类
 *
 * @author yuanyao@wistronits.com
 * create on 2019-07-12 16:11
 *
 * @see FiLinkTimeUtils#getLocalDate()              获取当前时间  LocalDateTime对象
 * @see FiLinkTimeUtils#getLocalYyyyMMdd()          获取当前时间  YyyyMMdd 字符串
 * @see FiLinkTimeUtils#getLocalHHmm()              获取当前时间  HH:mm 字符串
 * @see FiLinkTimeUtils#getLocalYyMMddHHmmss()      获取当前时间  YyMMddHHmmss 字符串
 * @see FiLinkTimeUtils#getLocalDateTimeForTimeStamp(java.lang.Long)   根据时间戳获取当前时间 LocalDateTime对象
 *
 * @see FiLinkTimeUtils#getUtcZeroDate()            获取utc 0    LocalDateTime对象
 * @see FiLinkTimeUtils#getUtcZeroYyyyMMdd()        获取UTC 0    YyyyMMdd 字符串
 * @see FiLinkTimeUtils#getUtcZeroHHmm()            获取utc 0    HH:mm 字符串
 * @see FiLinkTimeUtils#getUtcZeroYyMMddHHmmss()    获取utc 0    YyMMddHHmmss 字符串
 * @see FiLinkTimeUtils#getUtcZeroLocalDateTimeForTimeStamp(java.lang.Long) 根据时间戳获取UTC0 LocalDateTime对象
 *
 * @see FiLinkTimeUtils#getUtcZeroTimeStamp()       获取当前时间的UTC0 时间戳，当前时间戳System.currentTimeMillis()即可
 *
 * @see FiLinkTimeUtils#getUtcZeroDateForTimeStampAndTimeZone(java.util.TimeZone, java.lang.Long)
 * 根据时间戳，当前时区，转换成UTC0时间的LocalDateTime对象
 *
 */
public class FiLinkTimeUtils {


    /**
     * 获取UTC 0 时区date
     *
     * @return utc 0 date
     */
    public static LocalDateTime getUtcZeroDate() {
        LocalDateTime utc = LocalDateTime.now(ZoneId.of("UTC"));
        return utc;
//        // 获取本地时间
//        Calendar instance = Calendar.getInstance();
//        // 获取时间偏移量
//        int zoneOffset = instance.get(Calendar.ZONE_OFFSET);
//        // 获取夏令营时差
//        int dstOffset = instance.get(Calendar.DST_OFFSET);
//        //从本地时间里扣除这些差量，即可以取得UTC时间
//        instance.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
//        return instance.getTime();
    }

    /**
     * 获取本地当前 date
     *
     * @return 本地当前 date
     */
    public static LocalDateTime getLocalDate() {
        return LocalDateTime.now();
    }

    /**
     * 获取本地今天日期  yyyy-MM-dd
     *
     * @return 本地今天日期
     */
    public static String getLocalYyyyMMdd() {
        return getLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 获取utc当前日志 yyyy-MM-dd
     * @return utc今天日期
     */
    public static String getUtcZeroYyyyMMdd() {
        return getUtcZeroDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    /**
     * 获取本地 HH：mm格式时间
     *
     * @return String
     */
    public static String getLocalHHmm() {
        return getLocalDate().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * 获取utc 0 HH：mm格式时间
     *
     * @return String
     */
    public static String getUtcZeroHHmm() {
        return getUtcZeroDate().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * 获取utc 0  yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public static String getUtcZeroYyMMddHHmmss() {
        return getUtcZeroDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取本地  yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public static String getLocalYyMMddHHmmss() {
        return getLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 根据时间戳获取本地dLocalDateTime对象
     * @param timeStamp 当前时间戳
     * @return 本地LocalDateTime对象
     */
    public static LocalDateTime getLocalDateTimeForTimeStamp(Long timeStamp) {
        Date date = new Date(timeStamp);
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 根基时间戳获取utc 0 LocalDateTime对象
     *
     * @param timeStamp 当前时间戳
     * @return UTC 0 LocalDateTime对象
     */
    public static LocalDateTime getUtcZeroLocalDateTimeForTimeStamp(Long timeStamp) {
        ZoneId utc = ZoneId.of("UTC");
        Date date = new Date(timeStamp);
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, utc);
    }

    /**
     * 根据UTC时间戳，时区信息，转换成当前时间的LocalDateTime对象
     *
     * @param timeZone 例如传入 TimeZone.getTimeZone("GMT+0800")
     * @param timeStamp
     */
    public static LocalDateTime getUtcZeroDateForTimeStampAndTimeZone(TimeZone timeZone, Long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), timeZone.toZoneId());
    }


    /**
     * 获取当前UTC时间戳
     *
     * @return UTC时间戳
     */
    public static Long getUtcZeroTimeStamp() {
        return getUtcZeroDate().toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
