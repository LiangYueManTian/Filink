package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

import java.util.List;

/**
 * Created by Qing on 2019/6/6.
 * 设施信息实体
 */
@Data
public class FacilityInfoBean {

    // 箱架信息
    private List<BoxInfoBean> boxList;

    //盘信息
    private List<BoardInfoBean> boardList;

    //端口信息
    private List<PortInfoBean> portList;
}
