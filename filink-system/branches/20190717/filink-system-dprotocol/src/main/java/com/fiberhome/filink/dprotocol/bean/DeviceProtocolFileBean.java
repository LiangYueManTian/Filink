package com.fiberhome.filink.dprotocol.bean;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 *     设施协议文件大小和名称长度实体类
 * </p>
 *
 * @author chaofang@wistrontis.com
 * @since 2019-03-29
 */
@Component
@Data
public class DeviceProtocolFileBean {

    /**
     * 文件大小
     */
    @Value("${DeviceProtocolFileSize}")
    private Long size;
    /**
     * 文件大小国际化提示信息
     */
    private String sizeI18n;
    /**
     * 文件名称长度
     */
    private final Integer nameLength = 32;
    /**
     * 文件名称长度国际化提示信息
     */
    private String nameI18n;
}
