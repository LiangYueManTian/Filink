package com.fiberhome.filink.oss.utils;

/**
 * 16进制工具类
 * @author CongcaiYu
 */
public class HexUtil {

    /**
     * 把16进制字符串转换成字节数组
     *
     * @param hex String
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] charA = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(charA[pos]) << 4 | toByte(charA[pos + 1]));
        }
        return result;
    }

    /**
     * char转字节
     * @param c char
     * @return 字节
     */
    private static int toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
