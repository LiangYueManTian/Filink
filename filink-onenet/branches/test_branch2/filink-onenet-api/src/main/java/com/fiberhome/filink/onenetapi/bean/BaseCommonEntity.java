package com.fiberhome.filink.onenetapi.bean;

import lombok.Data;

/**
 * <p>
 *   oneNet平台通用实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
public abstract class BaseCommonEntity {
    /**设备imei号，平台唯一*/
    protected String imei;
    /**产品ID，必填参数*/
    protected String productId;
    /**ISPO标准中的Object ID*/
    protected Integer objId;
    /**ISPO标准中的Object Instance ID*/
    protected Integer objInstId;
    /**ISPO标准中的Resource ID*/
    protected Integer resId;
}
