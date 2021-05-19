package com.fiberhome.filink.onenetserver.bean.device;

import lombok.Data;
import org.json.JSONObject;

/**
 * <p>
 * oneNet平台通用实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
public abstract class BaseCommonEntity {
    /**
     * oneNet域名
     */
    protected String host;
    /**
     * 设备imei号，平台唯一
     */
    protected String imei;
    /**
     * 产品ID，必填参数
     */
    protected String productId;
    /**
     * 核心秘钥，必填参数
     */
    protected String accessKey;
    /**
     * ISPO标准中的Object ID
     */
    protected Integer objId;
    /**
     * ISPO标准中的Object Instance ID
     */
    protected Integer objInstId;
    /**
     * ISPO标准中的Resource ID
     */
    protected Integer resId;

    /**
     * 输出Json
     *
     * @return JSONObject
     */
    public JSONObject toJsonObject() {
        return null;
    }

    /**
     * 输出 url
     *
     * @return url
     */
    public abstract String toUrl();
}
