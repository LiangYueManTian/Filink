package com.fiberhome.filink.commonstation.utils;

import io.netty.buffer.ByteBuf;

/**
 * 16进制工具类
 *
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
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 数组转换成十六进制字符串
     *
     * @param bArray byte[]
     * @return HexString
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str String
     * @return String
     */
    public static String strToHexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr String
     * @return String
     */
    public static String hexStrToStr(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 十六进制ByteBuf转十进制数
     *
     * @param hexBuf ByteBuf
     * @return int
     */
    public static int bufToInt(ByteBuf hexBuf) {
        byte[] hexArray = new byte[hexBuf.readableBytes()];
        hexBuf.readBytes(hexArray);
        String hexStr = HexUtil.bytesToHexString(hexArray);
        return Integer.valueOf(hexStr, 16);
    }

    /**
     * byteBuf转16进制数字
     *
     * @param byteBuf ByteBuf
     * @return String
     */
    public static String bufToHexInt(ByteBuf byteBuf) {
        return intToHexInt(bufToInt(byteBuf));
    }

    /**
     * byteBuf转十六进制字符串
     *
     * @param byteBuf 字节流
     * @return 16进制字符串
     */
    public static String bufToHexStr(ByteBuf byteBuf) {
        byte[] hexArray = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(hexArray);
        return bytesToHexString(hexArray);
    }

    /**
     * ByteBuf转字符串
     *
     * @param hexBuf ByteBuf
     * @return String
     */
    public static String bufToStr(ByteBuf hexBuf) {
        byte[] hexArray = new byte[hexBuf.readableBytes()];
        hexBuf.readBytes(hexArray);
        return new String(hexArray);
    }


    /**
     * 十进制转十六进制数字
     *
     * @return
     */
    public static String intToHexInt(int num) {
        String hexInt = Integer.toHexString(num);
        if (hexInt.length() == 1) {
            return "0x0" + hexInt;
        } else {
            return "0x" + hexInt;
        }

    }

}
