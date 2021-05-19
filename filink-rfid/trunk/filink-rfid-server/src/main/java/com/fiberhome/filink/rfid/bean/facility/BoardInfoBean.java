package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 盘信息bean
 */
@Data
public class BoardInfoBean extends BaseInfoBean{
    // 实际盘号
    private Integer boardNo;
    // 盘标签ID
    private String boardLabel;
    // 盘名称
    private String boardName;
}
