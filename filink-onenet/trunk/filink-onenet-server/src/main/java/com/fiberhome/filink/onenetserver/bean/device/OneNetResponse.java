package com.fiberhome.filink.onenetserver.bean.device;

import lombok.Data;

/**
 * <p>
 *   oneNet平台返回参数实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
public class OneNetResponse {
    /**
     * 错误描述
     */
    private String error;
    /**
     * 调用错误码
     */
    private int errorno;
    /**
     * 返回参数
     */
    private Object data;
}
