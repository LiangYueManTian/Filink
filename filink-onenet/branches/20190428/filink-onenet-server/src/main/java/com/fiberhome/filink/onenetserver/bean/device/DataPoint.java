package com.fiberhome.filink.onenetserver.bean.device;

import lombok.Data;
/**
 * <p>
 *   oneNet平台数据点消息实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
public class DataPoint {
    /**
     * 标识消息类型
     */
    private Integer type;
    /**
     * 设备ID
     */
    private Integer devId;
    /**
     * 数据流名称
     */
    private String dsId;
    /**
     * 平台时间戳,单位ms
     */
    private Integer at;
    /**
     * 具体数据部分，为设备上传至平台或触发的相关数据
     */
    private Object value;
}
