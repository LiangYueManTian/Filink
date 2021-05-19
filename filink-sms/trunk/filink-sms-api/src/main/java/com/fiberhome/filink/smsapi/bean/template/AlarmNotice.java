package com.fiberhome.filink.smsapi.bean.template;

import lombok.Data;

/**
 * 告警通知短信模板
 *
 * @author yuanyao@wistronits.com
 * create on 2019/2/18 9:56
 */
@Data
public class AlarmNotice {

    /**
     * 告警名称
     */
    private String alarmname;

    /**
     * 附加信息
     */
    private String alarmdes;

    /**
     * 设施名称
     */
    private String devname;

    /**
     * 设施类型
     */
    private String devtype;

    /**
     * 布放区域
     */
    private String region;

    /**
     * 告警时间
     */
    private String alarmtime;


}
