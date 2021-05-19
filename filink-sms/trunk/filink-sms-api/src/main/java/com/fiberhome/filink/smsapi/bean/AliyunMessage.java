package com.fiberhome.filink.smsapi.bean;

import lombok.Data;

/**
 * 阿里云信息推送实体
 *
 * @author yuanyao@wistronits.com
 * create on 2019-03-18 10:51
 */
@Data
public class AliyunMessage {

    private String accessKeyId;

    private String accessKeySecret;

    /**
     * 区域信息
     * 如果是除杭州region外的其它region（如新加坡、澳洲Region），
     * 需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
     */
    private String area = "cn-hangzhou";

}
