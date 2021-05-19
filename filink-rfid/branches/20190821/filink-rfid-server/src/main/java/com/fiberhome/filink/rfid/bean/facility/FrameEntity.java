package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

import java.util.List;

/**
 * Created by Qing on 2019/6/6.
 * 框实体
 */
@Data
public class FrameEntity extends BaseEntity {

    /**
     * 框所属AB面(true是B面)
     */
    private Integer frameDouble;

    /**
     * 盘实体(一个框实体下嵌套多个盘实体)
     */
    private List<BoardEntity> boardEntity;
}
