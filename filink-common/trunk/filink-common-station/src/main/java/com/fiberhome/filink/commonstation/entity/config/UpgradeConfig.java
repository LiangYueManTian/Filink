package com.fiberhome.filink.commonstation.entity.config;

import lombok.Data;

/**
 * 升级包校验类
 *
 * @author CongcaiYu
 */
@Data
public class UpgradeConfig {

    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 依赖硬件版本
     */
    private String dependentHardVersion;
    /**
     * 依赖软件版本
     */
    private String dependentSoftVersion;
    /**
     * 唯一码
     */
    private String sha256;
    /**
     * 生成时间
     */
    private Long generationTime;
    /**
     * bin文件16进制
     */
    private String hexBinFile;
}
