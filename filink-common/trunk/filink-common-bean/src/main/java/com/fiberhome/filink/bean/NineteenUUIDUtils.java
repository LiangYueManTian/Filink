package com.fiberhome.filink.bean;

import java.util.UUID;

/**
 * <p>
 *   生成19位62进制UUID
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-13
 */
public class NineteenUUIDUtils {

    /**
     * 调用进制转换
     * @param val 长整型数值
     * @param digits 进制数
     * @return 指定的进制
     */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return BinaryConversionUtils.toString(hi | (val & (hi - 1)), BinaryConversionUtils.MAX_RADIX)
                .substring(1);
    }

    /**
     * 以62进制（字母加数字）生成19位UUID，最短的UUID
     *
     * @return 字符串
     */
    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        stringBuilder.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        stringBuilder.append(digits(uuid.getMostSignificantBits(), 4));
        stringBuilder.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        stringBuilder.append(digits(uuid.getLeastSignificantBits(), 12));
        return stringBuilder.toString();
    }
}
