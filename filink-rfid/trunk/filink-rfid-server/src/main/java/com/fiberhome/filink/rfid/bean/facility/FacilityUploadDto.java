package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 设施标签信息上传实体
 */
@Data
public class FacilityUploadDto extends FacilityInfoBean {

    /**
     * 上传类型(0新增1删除2修改)
     */
    private Integer uploadType;
    /**
     * 设施类型
     */
    private String deviceType;
}
