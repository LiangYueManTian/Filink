package com.fiberhome.filink.commonstation.utils;

/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/5/14
 */
public class VerifyUtil {

    /**
     * @param datas
     * @return
     * @Description: 异或和校验，校验值占1个字节
     * @return: String
     * @author: wy
     * @date: 2017年5月12日
     */
    public static String xorVerify(byte[] datas) {
        if (datas == null || datas.length == 0) {
            return null;
        }
        int sum = 0x00;
        for (int i = 0; i < datas.length; i++) {
            sum ^= (datas[i]);
            sum = sum & 0xff;
        }
        String result = Integer.toHexString(sum);
        return result;
    }

    /**
     * @param hexStr
     * @return
     * @Description: 将16进制字符串进行异或和校验计算，返回16进制校验码
     * @return: String
     * @author: wy
     * @date: 2017年5月12日
     */
    public static String xorVerify(String hexStr) {
        byte[] bytes = hexString2Bytes(hexStr);
        return xorVerify(bytes);
    }

    /**
     * @param bytes
     * @return
     * @Description: 异或和校验，以byte数组返回校验码
     * @return: byte[]
     * @author: wy
     * @date: 2017年5月12日
     */
    public static byte[] xorVerifyByte(byte[] bytes) {
        String str = VerifyUtil.xorVerify(bytes);
        byte[] result = hexString2Bytes(str);
        return result;
    }

    /**
     * @return
     * @Description: 异或和校验，以byte数组返回校验码
     * @return: byte[]
     * @author: wy
     * @date: 2017年5月12日
     */
    public static byte[] xorVerifyByte(String hexStr) {
        String verifyStr = VerifyUtil.xorVerify(hexStr);
        byte[] result = hexString2Bytes(verifyStr);
        return result;
    }

    /**
     * @Title: crc16Calc
     * @Description: 对数据进行CRC16计算
     * @param: @param bytes
     * @param: @return
     * @return: int
     * @author:luowei
     * @date:2018年8月31日 下午8:38:44
     */
    public static int crc16Calc(byte[] bytes) {
        int crcVa = 0xFFFF;

        if ((bytes == null) || (bytes.length <= 0)) {
            return crcVa;
        }

        for (int i = 0; i < bytes.length; i++) {
            crcVa = (((crcVa >> 8) & 0xFF) | (crcVa << 8)) & 0xFFFF;
            crcVa ^= (int) (bytes[i] & 0xFF);
            crcVa ^= ((crcVa & 0xFF) >> 4) & 0xFFFF;
            crcVa ^= ((crcVa << 8) << 4) & 0xFFFF;
            crcVa ^= (((crcVa & 0xFF) << 4) << 1) & 0xFFFF;
        }

        return crcVa;
    }

    /**
     * 将16进制的字符串转成byte数组
     *
     * @param src
     * @return
     */
    public static byte[] hexString2Bytes(String src) {
        int length = -1;
        if ((src.length() % 2) == 1) {
            length = src.length() + 1;
            src = "0" + src;
        } else {
            length = src.length();
        }

        byte[] ret = new byte[length / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < tmp.length / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

}
