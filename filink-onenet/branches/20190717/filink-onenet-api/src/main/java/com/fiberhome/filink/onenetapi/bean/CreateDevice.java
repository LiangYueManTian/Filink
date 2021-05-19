package com.fiberhome.filink.onenetapi.bean;



import lombok.Data;
import lombok.EqualsAndHashCode;
import org.json.JSONObject;

import java.util.List;

/**
 * <p>
 *   oneNet平台设施实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CreateDevice extends BaseCommonEntity {
    /**设备名称，字符和数字组成的字符串，必填参数*/
    private String title;
    /**设备描述信息，可填参数*/
    private String desc;
    /**设备标签，可填参数*/
    private List<String> tags;
    /** 设备地理位置，格式为：{"lon": 106, "lat": 29, "ele": 370}，可填参数*/
    private JSONObject location;
    /**设备IMSI，必填参数*/
    private String imsi;
    /**设备接入平台是否启用自动订阅功能，可填参数*/
    private Boolean obsv;
    /**其他信息，可填参数*/
    private JSONObject other;
    /**空构造函数*/
    public CreateDevice() {}
    /**
     * 构造函数
     * @param title 有字符或者数字组成，必填
     * @param imei 要求在OneNET平台唯一，必填
     * @param imsi 必填
     * @param productId 产品ID，必填参数
     */
    public CreateDevice(String title, String imei, String imsi, String productId) {
        this.title = title;
        this.imei = imei;
        this.imsi = imsi;
        this.productId = productId;
    }
}
