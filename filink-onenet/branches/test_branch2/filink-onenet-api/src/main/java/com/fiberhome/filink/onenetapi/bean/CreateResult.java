package com.fiberhome.filink.onenetapi.bean;

import lombok.Data;

/**
 * <p>
 *   oneNet平台新增设施返回实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
public class CreateResult {
    /**
     * 设施ID
     */
    private String deviceId;
    /**
     * 在NB协议开启DTLS加密功能时返回该字段，其他情况没有该字段
     */
    private String psk;
}
