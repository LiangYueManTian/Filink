package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/20 17:04
 * @Description: com.fiberhome.filink.fdevice.bean.device
 * @version: 1.0
 */
@Data
public class DeployStatusDto {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 无
     */
    private String type0;

    /**
     * 已布防
     */
    private String type1;

    /**
     * 未布防
     */
    private String type2;

    /**
     * 停用
     */
    private String type3;

    /**
     * 维护
     */
    private String type4;

    /**
     * 拆除
     */
    private String type5;

    /**
     * 部署中
     */
    private String type6;
}
