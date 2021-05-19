package com.fiberhome.filink.smsapi.bean;

import lombok.Data;

/**
 * 短信实体
 *
 * @author yuanyao@wistronits.com
 * create on 2019/2/15 17:14
 */
@Data
public class AliyunSms extends AliyunMessage{

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 短信签名,目前可选值如下:
     *      基础设施
     *
     * 后续签名在云平台控制台修改
     */
    private String signName;

    /**
     * 模板code,目前可选如下:
     *      告警通知:           SMS_155370970
     *      告警提示无门号:     SMS_154591434
     *      告警提示:           SMS_153885498
     */
    private String templateCode;

    /**
     * 模板参数,为json字符串,例如:
     *      "{\"alarmname\":\"告警名称\", \"alarmdes\":\"告警啥\", \"devname\":\"设施名称\",\"devtype\":\"告警类型\"}"
     *      可使用template中的bean json化
     *  需要转义
     */
    private String templateParam;

}
