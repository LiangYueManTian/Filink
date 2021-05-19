package com.fiberhome.filink.parameter.constant;
/**
 * <p>
 *     图片规格枚举类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/28
 */
public enum ImageSizeEnum {
    /**
     * 长
     */
    HEIGHT(50),
    /**
     * 宽
     */
    WIDTH(50),
    /**
     * 大小
     */
    SIZE(3145728);
    /**value*/
    private Integer value;

    public Integer getValue() {
        return value;
    }

    ImageSizeEnum(Integer value) {
        this.value = value;
    }
}
