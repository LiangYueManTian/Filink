package com.fiberhome.filink.onenetapi.bean;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * oneNet平台删除设备实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DeleteDevice extends BaseCommonEntity {
    /**
     * 设备ID 必填
     */
    private String deviceId;

    /**
     * 空构造函数
     */
    public DeleteDevice() {
    }

    /**
     * 构造函数
     *
     * @param deviceId  设备ID 必填
     * @param productId 产品ID，必填参数
     */
    public DeleteDevice(String deviceId, String productId) {
        this.deviceId = deviceId;
        this.productId = productId;
    }
}
