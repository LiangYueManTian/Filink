package com.fiberhome.filink.onenetserver.bean.device;


import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;

/**
 * <p>
 *   oneNet平台读设备资源实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public class Read extends BaseCommonEntity {

    /**
     * @param imei 设备IMEI
     * @param objId 读对象ID
     * @param productId 产品ID，必填参数
     * @param accessKey 核心秘钥，必填参数
     *              其他可选参数Object Instance ID,Rescource ID可以通过相关set函数设置
     */
    public Read(String imei, Integer objId, String productId, String accessKey) {
        this.imei = imei;
        this.objId = objId;
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
        url.append("/nbiot?imei=").append(this.imei);
        url.append("&obj_id=").append(this.objId);
        if (this.objInstId != null) {
            url.append("&obj_inst_id=").append(this.objInstId);
        }
        if (this.resId != null) {
            url.append("&res_id=").append(this.resId);
        }
        return url.toString();
    }
}
