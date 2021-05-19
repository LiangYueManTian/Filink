package com.fiberhome.filink.onenetserver.bean.device;


import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *   oneNet平台及时命令下发实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Execute extends BaseCommonEntity {
    /**args 命令字符串 必填*/
    private String args;
    /**
     *
     * @param imei 设备IMEI号，必填
     * @param productId 产品ID，必填参数
     * @param accessKey 核心秘钥，必填参数
     */
    public Execute(String imei, String args, String productId, String accessKey) {
        this.imei = imei;
        this.args = args;
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
        url.append("/nbiot/write?imei=").append(this.imei);
        url.append("&obj_id=").append(this.objId);
        url.append("&obj_inst_id=").append(this.objInstId);
        url.append("&res_id=").append(this.resId);
        return url.toString();
    }
}
