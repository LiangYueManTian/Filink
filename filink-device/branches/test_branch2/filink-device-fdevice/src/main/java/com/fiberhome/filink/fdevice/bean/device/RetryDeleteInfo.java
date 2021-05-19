package com.fiberhome.filink.fdevice.bean.device;

import lombok.Data;

/**
 * @Author: qiqizhu@wistronits.com
 * Date:2019/6/14
 */
@Data
public class RetryDeleteInfo {
    /**
     * 主键id
     */
    private String deleteId;
    /**
     * 方法code
     */
    private Integer functionCode;
    /**
     * 重试次数
     */
    private Integer retryNum = 0;
    /**
     * 重试参数
     */
    private String data;
}


