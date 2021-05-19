package com.fiberhome.filink.smsapi.bean.template;

import lombok.Data;

/**
 * 告警提示短信模板
 *
 * @author yuanyao@wistronits.com
 * create on 2019/2/18 10:08
 */
@Data
public class AlarmWarning {

    /**
     * 设施名称
     */
    private String devname;

    /**
     * 门号
     */
    private String doorno;

    /**
     * 告警信息
     */
    private String alarmdes;

}
