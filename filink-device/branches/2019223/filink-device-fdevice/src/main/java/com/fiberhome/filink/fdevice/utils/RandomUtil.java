package com.fiberhome.filink.fdevice.utils;

/**
 * 随机数工具类
 * @author zepenggao@wistronits.com
 * create on  2019/1/10
 */
public class RandomUtil {

    /**
     * 生成范围内的随机数
     *
     * @param start
     * @param end
     * @return
     */
    public static int getRandom(int start, int end, int figure) {
        int num = 1;
        for (int i = 0; i < figure - 1; i++) {
            num = num * 10;
        }
        return (int) ((Math.random() * (end - start + 1)) * num);
    }

    /**
     * 生成7位流水号
     * @return 流水号
     */
    public static String getRandomOfServen() {
        return  String.format("%07d",getRandom(0, 9, 7));
    }

}
