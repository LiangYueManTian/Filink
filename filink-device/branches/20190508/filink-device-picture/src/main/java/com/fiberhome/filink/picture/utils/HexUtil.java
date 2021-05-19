package com.fiberhome.filink.picture.utils;

/**
 * 解析文件信息工具类
 * @author ChaoFanRong
 */
public class HexUtil {
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




}
