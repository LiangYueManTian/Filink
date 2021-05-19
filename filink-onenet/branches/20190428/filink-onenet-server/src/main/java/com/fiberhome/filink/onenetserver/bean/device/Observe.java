package com.fiberhome.filink.onenetserver.bean.device;

import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

;

/**
 * <p>
 *   oneNet平台订阅实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Observe extends BaseCommonEntity {
    private Boolean cancel;
    private Integer pMax;
    private Integer pMin;
    private Double gt;
    private Double lt;
    private Double st;

    /**
     * @param imei 设备IMEI，必填
     * @param objId 订阅对象ID，必填
     * @param cancel 订阅或者取消订阅，必填
     * @param productId 产品ID，必填参数
     * @param accessKey 核心秘钥，必填参数
     *               其他可选参数，可以通过相关set函数设置
     */
    public Observe(String imei, Integer objId, Boolean cancel, String productId, String accessKey) {
        this.imei = imei;
        this.objId = objId;
        this.cancel = cancel;
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
        url.append("/nbiot/observe?imei=").append(this.imei);
        url.append("&obj_id=").append(this.objId);
        url.append("&cancel=").append(this.cancel);
        if (this.objInstId != null){
            url.append("&obj_inst_id=").append(this.objInstId);
        }
        if (this.resId != null) {
            url.append("&res_id=").append(this.resId);
        }
        if (this.pMax != null) {
            url.append("&pMax=").append(this.pMax);
        }
        if (this.pMin != null) {
            url.append("&pMin=").append(this.pMin);
        }
        if (this.gt != null) {
            url.append("&gt=").append(this.gt);
        }
        if (this.lt != null) {
            url.append("&lt=").append(this.lt);
        }
        if (this.st != null) {
            url.append("&st=").append(this.st);
        }

        return url.toString();
    }
}
