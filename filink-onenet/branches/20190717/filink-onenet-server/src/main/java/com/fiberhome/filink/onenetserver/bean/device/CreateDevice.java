package com.fiberhome.filink.onenetserver.bean.device;


import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.List;

/**
 * <p>
 * oneNet平台设施实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CreateDevice extends BaseCommonEntity {
    /**
     * 设备名称，字符和数字组成的字符串，必填参数
     */
    private String title;
    /**
     * 设备描述信息，可填参数
     */
    private String desc;
    /**
     * 设备标签，可填参数
     */
    private List<String> tags;
    /**
     * 设备地理位置，格式为：{"lon": 106, "lat": 29, "ele": 370}，可填参数
     */
    private JSONObject location;
    /**
     * 设备IMSI，必填参数
     */
    private String imsi;
    /**
     * 设备接入平台是否启用自动订阅功能，可填参数
     */
    private Boolean obsv;
    /**
     * 其他信息，可填参数
     */
    private JSONObject other;

    /**
     * 空构造函数
     */
    public CreateDevice() {
    }

    /**
     * 构造函数
     *
     * @param title     有字符或者数字组成，必填
     * @param imei      要求在OneNET平台唯一，必填
     * @param imsi      必填
     * @param productId 产品ID，必填参数
     * @param accessKey 核心秘钥，必填参数
     */
    public CreateDevice(String title, String imei, String imsi, String productId, String accessKey) {
        this.title = title;
        this.imei = imei;
        this.imsi = imsi;
        this.productId = productId;
        this.accessKey = accessKey;
    }

    /**
     * 校验必填参数是否有空值
     *
     * @return true有 false没有
     */
    public boolean check() {
        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(imei)) {
            return true;
        }
        return StringUtils.isEmpty(imsi) || StringUtils.isEmpty(productId);
    }

    /**
     * 输出Json
     *
     * @return JSONObject
     */
    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", this.title);
        if (StringUtils.isNotBlank(this.desc)) {
            jsonObject.put("desc", this.desc);
        }
        if (CollectionUtils.isNotEmpty(this.tags)) {
            jsonObject.put("tags", this.tags);
        }
        jsonObject.put("protocol", OneNetHttpConstants.PROTOCOL);
        if (this.location != null) {
            jsonObject.put("location", this.location);
        }
        JSONObject authInfo = new JSONObject();
        authInfo.put(imei, imsi);
        jsonObject.put("auth_info", authInfo);
        if (this.obsv != null) {
            jsonObject.put("obsv", this.obsv);
        }
        if (this.other != null) {
            jsonObject.put("other", this.other);
        }

        return jsonObject;
    }

    /**
     * 输出 url
     *
     * @return url
     */
    @Override
    public String toUrl() {
        return host + "/devices";
    }
}
