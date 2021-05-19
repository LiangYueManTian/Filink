package com.fiberhome.filink.commonstation.utils;

import java.util.Map;

;


/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/5/14
 */
public class WellDataUtil {
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToByte4(int value) {
        byte[] targets = new byte[4];
        targets[0] = (byte) ((value >> 0) & 0xff);// 最低位
        targets[1] = (byte) ((value >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((value >> 16) & 0xff);// 次高位
        targets[3] = (byte) (value >> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * 将int数值转换为占两个个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToByte2(int value) {
        byte[] targets = new byte[2];
        targets[0] = (byte) ((value >> 0) & 0xff);// 低位
        targets[1] = (byte) ((value >> 8) & 0xff);// 高位
        return targets;
    }

    /**
     * 将int数值转换为占一个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToByte1(int value) {
        byte[] targets = new byte[1];
        targets[0] = (byte) ((value >> 0) & 0xff);// 低位
        return targets;
    }

    /**
     * @return 结果
     * @Description: ip地址的转化
     * @return: byte[]
     * @author: huating
     * @date: 2017年6月9日
     */
    public static byte[] Ip2byte(String ip) {
        byte[] ipInfo = new byte[4];
        String[] ips = ip.split("\\.");
        for (int i = 0; i < ips.length; i++) {
            byte[] ip2 = intToByte1(Integer.parseInt(ips[i]));
            System.arraycopy(ip2, 0, ipInfo, i, ip2.length);
        }
        return ipInfo;
    }

    /**
     * 字节转ip
     *
     * @param bytes 传入数组
     * @return 结果
     */
    public static String byte2Ip(byte[] bytes) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int ip = WellByteBufUtil.bytes1ToInt(bytes[i]);
            str.append(ip);
            if (i < bytes.length - 1) {
                str.append(".");
            }
        }
        return str.toString();
    }

    /**
     * 计算开锁密码
     *
     * @param idenId idenId
     * @param ctrlId 主控id
     * @return 结果
     */
    public static byte[] getOpenDoorPsd(byte[] idenId, byte[] ctrlId) {
        byte[] psdBytes = new byte[4];
        byte[] algConst = new byte[]{'H', 'C', 'L', 'Z'};
        byteCopy(ctrlId, 4, psdBytes, 0, 4);

        for (int i = 0; i < 2; i++) {
            psdBytes[0] = (byte) (idenId[4 * i + 0] ^ psdBytes[0]);
            psdBytes[1] = (byte) (idenId[4 * i + 1] ^ psdBytes[1]);
            psdBytes[2] = (byte) (idenId[4 * i + 2] ^ psdBytes[2]);
            psdBytes[3] = (byte) (idenId[4 * i + 3] ^ psdBytes[3]);
        }

        psdBytes[0] = (byte) (psdBytes[0] + algConst[0]);
        psdBytes[1] = (byte) (psdBytes[1] + algConst[1]);
        psdBytes[2] = (byte) (psdBytes[2] + algConst[2]);
        psdBytes[3] = (byte) (psdBytes[3] + algConst[3]);

        return psdBytes;
    }

    /**
     * byte数组复制，将src里面的数据复制到tar
     *
     * @param src      传入数组
     * @param srcStart 开始地址
     * @param tar      目标数组
     * @param tarStart 复制地址
     * @param length   复制长度
     */
    public static void byteCopy(byte[] src, Integer srcStart,
                                byte[] tar, Integer tarStart, Integer length) {
        assert (length != null);
        if (tarStart >= tar.length ||
                (srcStart + length) > src.length ||
                (tarStart + length) > tar.length) {
            assert (false);
            return;
        }
        if (srcStart == null) {
            srcStart = 0;
        }

        int index = 0;
        while (index < length) {
            tar[tarStart + index] = src[srcStart + index];
            index++;
        }
    }

    /**
     * Byte转Bit
     *
     * @param byteData 字节数据
     * @return 结果
     */
    public static String byteToBit(byte byteData) {
        StringBuffer sb = new StringBuffer();
        sb.append((byteData >> 7) & 0x1)
                .append((byteData >> 6) & 0x1)
                .append((byteData >> 5) & 0x1)
                .append((byteData >> 4) & 0x1)
                .append((byteData >> 3) & 0x1)
                .append((byteData >> 2) & 0x1)
                .append((byteData >> 1) & 0x1)
                .append((byteData >> 0) & 0x1);
        return sb.toString();
    }

    /**
     * @Description: 转换controllerId
     * ID编号规则：A-BBBB-CCCC-DDDD-EEEEEEE
     * A: 设备类型3Bit表示：1->控制器，2->电子钥匙，3->锁芯，4->APP，5->调测工具
     * BBBB：项目流水号13bit表示，从0开始填，最大值为8191(0x1FFF)
     * CCCC：生产日期16bit表示，格式为年周，如2017年50周，则数据填1750
     * DDDD：区域码12bit表示：区域码分为二段，对应市6bit[0,63]、区6bit[0,63]
     * EEEEEEE：生产流水号20bit表示，从0开始，最大值为1048575(0xFFFFF)
     * @return: byte
     * @date: 2018年2月5日
     */
    public static byte[] controllerIdToByte(String controllerId) {
        byte[] id = new byte[8];
        String A = controllerId.substring(0, 1);
        String B = controllerId.substring(1, 5);
        String C = controllerId.substring(5, 9);
        String D1 = controllerId.substring(9, 11);
        String D2 = controllerId.substring(11, 13);
        String E = controllerId.substring(13);
        byte byteA = (byte) Integer.parseInt(A);
        String bitA = byteToBit(byteA).substring(5);
        byte[] byteB = int2Byte2(Integer.parseInt(B));
        String bitB1 = byteToBit(byteB[1]).substring(3);
        id[0] = bitToByte(bitB1 + bitA);
        id[1] = bitToByte(byteToBit(byteB[0]).substring(3) + byteToBit(byteB[1]).substring(0, 3));
        byte[] byteC = int2Byte2(Integer.parseInt(C));
        id[2] = byteC[1];
        id[3] = byteC[0];
        byte byteD1 = (byte) Integer.parseInt(D1);
        byte byteD2 = (byte) Integer.parseInt(D2);
        String bitD1 = byteToBit(byteD1).substring(2);
        String bitD2 = byteToBit(byteD2).substring(2);
        id[4] = bitToByte(bitD2.substring(4, 6) + bitD1);
        byte[] byteE = int2Byte4(Integer.parseInt(E));
        String bitE = byteToBit(byteE[1]).substring(4) + byteToBit(byteE[2]) + byteToBit(byteE[3]);
        id[5] = bitToByte(bitE.substring(16) + bitD2.substring(0, 4));
        id[6] = bitToByte(bitE.substring(8, 16));
        id[7] = bitToByte(bitE.substring(0, 8));
        return id;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在后，高位在前)的顺序。
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] int2Byte4(int value) {
        byte[] targets = new byte[4];
        targets[3] = (byte) ((value >> 0) & 0xff);// 最低位
        targets[2] = (byte) ((value >> 8) & 0xff);// 次低位
        targets[1] = (byte) ((value >> 16) & 0xff);// 次高位
        targets[0] = (byte) (value >> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * 将int数值转换为占两个字节的byte数组，本方法适用于(低位在后，高位在前)的顺序。
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] int2Byte2(int value) {
        byte[] targets = new byte[2];
        targets[1] = (byte) ((value >> 0) & 0xff);// 低位
        targets[0] = (byte) ((value >> 8) & 0xff);// 高位
        return targets;
    }

    /**
     * @param bit 传入bit字符串
     * @return 结果
     * @Description: bit转byte
     * @return: byte
     * @author: wy
     * @date: 2017年5月16日
     */
    public static byte bitToByte(String bit) {
        int re, len;
        if (null == bit) {
            return 0;
        }
        len = bit.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (bit.charAt(0) == '0') {// 正数
                re = Integer.parseInt(bit, 2);
            } else {// 负数
                re = Integer.parseInt(bit, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(bit, 2);
        }
        return (byte) re;
    }

    /**
     * @Description: 转换controllerId
     * ID编号规则：A-BBBB-CCCC-DDDD-EEEEEEE
     * A: 设备类型3Bit表示：1->控制器，2->电子钥匙，3->锁芯，4->APP，5->调测工具
     * BBBB：项目流水号13bit表示，从0开始填，最大值为8191(0x1FFF)
     * CCCC：生产日期16bit表示，格式为年周，如2017年50周，则数据填1750
     * DDDD：区域码12bit表示：区域码分为二段，对应市6bit[0,63]、区6bit[0,63]
     * EEEEEEE：生产流水号20bit表示，从0开始，最大值为1048575(0xFFFFF)
     * @return: String
     * @author: huating
     * @date: 2018年2月5日
     */
    public static String byteToControllerId(byte[] controllerId) {
        String id = "";
        String bit1 = byteToBit(controllerId[0]);
        String bit2 = byteToBit(controllerId[1]);
        String bit3 = byteToBit(controllerId[2]);
        String bit4 = byteToBit(controllerId[3]);
        String bit5 = byteToBit(controllerId[4]);
        String bit6 = byteToBit(controllerId[5]);
        String bit7 = byteToBit(controllerId[6]);
        String bit8 = byteToBit(controllerId[7]);
        Integer A = Integer.parseInt("0" + bit1.substring(5, 8), 2);
        Integer B = Integer.parseInt(bit2 + bit1.substring(0, 5), 2);
        Integer C = Integer.parseInt(bit4 + bit3, 2);
        Integer D1 = Integer.parseInt("00" + bit5.substring(2, 8), 2);
        Integer D2 = Integer.parseInt("00" + bit6.substring(4, 8) + bit5.substring(0, 2), 2);
        Integer E = Integer.parseInt(bit8 + bit7 + bit6.substring(0, 4), 2);
        id = id + plusPreZero(A.toString(), 1);
        id = id + plusPreZero(B.toString(), 4);
        id = id + plusPreZero(C.toString(), 4);
        id = id + plusPreZero(D1.toString(), 2);
        id = id + plusPreZero(D2.toString(), 2);
        id = id + plusPreZero(E.toString(), 7);
        return id;
    }

    /**
     * str的长度如果不足len，则前缀补0
     *
     * @param str 传入字符串
     * @param len 长度
     * @return 结果
     */
    public static String plusPreZero(String str, int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len - str.length(); i++) {
            sb.append("0");
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    字节数组
     * @param offset 偏移地址
     * @return 结果
     */
    public static int bytes4ToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF << 0)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 通过三轴坐标计算角度
     *
     * @param lean 倾斜数据
     * @return 角度
     */
    public static double getZAngle(Map<String, Integer> lean) {
        double x = lean.get("xCoordinate");
        double y = lean.get("yCoordinate");
        double z = lean.get("zCoordinate");
        double A = 0;
        x *= 10.0 / 1024;
        y *= 10.0 / 1024;
        z *= 10.0 / 1024;

        //Z方向
        A = x * x + y * y;
        A = Math.sqrt(A);
        A = (double) A / z;
        A = Math.atan(A);
        A = A * 180 / Math.PI;

        return A;
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes 传入数组
     * @return 返回结果
     */
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return "0x" + Integer.toHexString(CRC);
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes 传入数组
     * @return 返回结果
     */
    public static int crc16Calc(byte[] bytes) {
        int crcVa = 0xFFFF;

        if ((bytes == null) || (bytes.length <= 0)) return crcVa;

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
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src
     * @param offset
     * @return
     */
    public static int bytes2ToInt(byte[] src, int offset) {
        int value;
        value =  ((src[offset] & 0xFF << 0)
                | ((src[offset + 1] & 0xFF) << 8));
        return value;
    }
}
