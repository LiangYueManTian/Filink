package com.fiberhome.filink.parameter.bean;

import lombok.Data;

/**
 * <p>
 *   APP升级数据实体类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-30
 */
@Data
public class AppUpgrade {
    /**
     * APP软件版本
     */
    private String appSoftwareVersion;
    /**
     * APP硬件版本
     */
    private String appHardwareVersion;
}
