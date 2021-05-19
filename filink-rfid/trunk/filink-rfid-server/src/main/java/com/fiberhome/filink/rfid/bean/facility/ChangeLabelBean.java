package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/7/2.
 * 标签更改bean
 */
@Data
public class ChangeLabelBean {

    /**
     * 旧标签
     */
    private String oldLabel;

    /**
     * 新标签
     */
    private String newLabel;

    /**
     * 标签类型
     */
    private Integer deviceType;
}
