package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 盘实体
 */
@Data
public class BoardEntity extends BaseEntity {

    /**
     * // 端口所属AB面(true是B面)
     */
    private Integer portDouble;
}
