package com.fiberhome.filink.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 进制转换
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-13
 */
public class BinaryConversionUtils {

    /**
     * 进制字符（62）
     */
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'};
    /**
     * 进制Map
     */
    private static final Map<Character, Integer> DIGIT_MAP = new HashMap<Character, Integer>();
    /**
     * 数字10
     */
    private static final int TEN = 10;
    /**
     * '-'
     */
    private static final char SUB = '-';
    /**
     * '0'
     */
    private static final char ZERO = '0';
    /**
     * '+'
     */
    private static final char ADD = '+';

    static {
        for (int i = 0; i < DIGITS.length; i++) {
            DIGIT_MAP.put(DIGITS[i], (int) i);
        }
    }

    /**
     * 支持的最大进制数
     */
    static final int MAX_RADIX = DIGITS.length;

    /**
     * 支持的最小进制数
     */
    private static final int MIN_RADIX = 2;

    /**
     * 将长整型数值转换为指定的进制数（最大支持62进制，字母数字已经用尽）
     *
     * @param i     长整型数值
     * @param radix 进制数
     * @return 指定的进制
     */
    public static String toString(long i, int radix) {
        if (radix < MIN_RADIX || radix > MAX_RADIX) {
            radix = TEN;
        }
        if (radix == TEN) {
            return Long.toString(i);
        }
        final int size = 65;
        int charPos = 64;

        char[] buf = new char[size];
        boolean negative = (i < 0);

        if (!negative) {
            i = -i;
        }

        while (i <= -radix) {
            buf[charPos--] = DIGITS[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = DIGITS[(int) (-i)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (size - charPos));
    }

    /**
     * 抛异常
     *
     * @param s 字符串
     * @return 异常
     */
    private static NumberFormatException forInputString(String s) {
        return new NumberFormatException("For input string: \"" + s + "\"");
    }

    /**
     * 将字符串转换为长整型数字
     *
     * @param s     数字字符串
     * @param radix 进制数
     * @return 长整型数字
     */
    public static long toNumber(String s, int radix) {
        if (s == null) {
            throw new NumberFormatException("null");
        }

        if (radix < MIN_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " less than Numbers.MIN_RADIX");
        }
        if (radix > MAX_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " greater than Numbers.MAX_RADIX");
        }

        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -Long.MAX_VALUE;
        long multiMin;
        Integer digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < ZERO) {
                if (firstChar == SUB) {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != ADD) {
                    throw forInputString(s);
                }
                if (len == 1) {
                    throw forInputString(s);
                }
                i++;
            }
            multiMin = limit / radix;
            while (i < len) {
                digit = DIGIT_MAP.get(s.charAt(i++));
                if (digit == null) {
                    throw forInputString(s);
                }
                if (digit < 0) {
                    throw forInputString(s);
                }
                if (result < multiMin) {
                    throw forInputString(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw forInputString(s);
                }
                result -= digit;
            }
        } else {
            throw forInputString(s);
        }
        return negative ? result : -result;
    }
}
