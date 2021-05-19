package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 设施实体信息上传
 */
@Data
public class DeviceUploadDto extends DeviceEntity {
    /**
     * 上传类型(0新增1删除2修改)
     */
    private Integer uploadType;
    /**
     * 在位不在位
     */
    private Integer state;
}
