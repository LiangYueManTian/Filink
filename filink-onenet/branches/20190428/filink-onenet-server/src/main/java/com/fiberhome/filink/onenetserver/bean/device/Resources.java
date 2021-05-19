package com.fiberhome.filink.onenetserver.bean.device;


import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;

/**
 * <p>
 *   oneNet平台﻿获取资源列表实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public class Resources extends BaseCommonEntity {
    /**
     * @param imei 设备IMEI
     * @param productId 产品ID，必填参数
     * @param accessKey 核心秘钥，必填参数
     *             其他可选参数Object ID可以通过对应set函数设置
     */
    public Resources(String imei, String productId, String accessKey) {
        this.imei = imei;
        this.productId = productId;
        this.accessKey = accessKey;
    }

    /**
     * 输出 url
     * @return url
     */
    @Override
    public String toUrl() {
        StringBuilder url = new StringBuilder(host);
        url.append("/nbiot/resources");
        url.append("?imei=").append(this.imei);
        if (this.objId != null) {
            url.append("&obj_id=").append(this.objId);
        }
        return url.toString();
    }
}
