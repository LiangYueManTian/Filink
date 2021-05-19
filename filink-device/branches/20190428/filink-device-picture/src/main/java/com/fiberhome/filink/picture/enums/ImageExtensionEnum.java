package com.fiberhome.filink.picture.enums;
/**
 * <p>
 *     图片扩展名枚举类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/25
 */
public enum ImageExtensionEnum {
    /**
     * jpg
     */
    JPG("JPG"),
    /**
     * jpeg
     */
    JPEG("JPEG"),
    /**
     * png
     */
    PNG("PNG"),
    /**
     * gif
     */
    GIF("GIF");
    /**value*/
    private String value;

    ImageExtensionEnum(String value) {
        this.value = value;
    }

    /**
     * 判断是否支持此类型
     * @param extension 文件扩展名
     * @return true支持 false不支持
     */
    public static boolean containExtension(String extension) {
        for (ImageExtensionEnum extensionEnum : ImageExtensionEnum.values() ) {
            if (extensionEnum.value.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
