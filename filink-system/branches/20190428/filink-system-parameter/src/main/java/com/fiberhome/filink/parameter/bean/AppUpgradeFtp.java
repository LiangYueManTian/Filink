package com.fiberhome.filink.parameter.bean;

import lombok.Data;
/**
 * <p>
 *   APP升级实体类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-30
 */
@Data
public class AppUpgradeFtp {
    /**
     * FTP信息
     */
    private FtpSettings ftpSettings;
    /**
     * 是否升级
     */
    private boolean upgrade;
    /**
     * 文件路径
     */
    private String filePath;
}
