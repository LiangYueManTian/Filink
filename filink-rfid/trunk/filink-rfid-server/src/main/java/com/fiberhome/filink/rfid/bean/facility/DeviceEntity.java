package com.fiberhome.filink.rfid.bean.facility;

import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import lombok.Data;

import java.util.List;

/**
 * Created by Qing on 2019/6/6.
 * 设施实体
 */
@Data
public class DeviceEntity extends TemplateReqDto {
    /**
     * 箱架模板名称(下属框盘模板以此确定)
     */
    private String mouldName;

    /**
     * 框实体(一个设施实体下嵌套多个框实体)
     */
    private List<FrameEntity> frameEntity;

    /**
     * 箱架模板
     */
    private BaseMould boxMould;

    /**
     * 框模板
     */
    private BaseMould frameMould;

    /**
     * 盘模板
     */
    private BaseMould boardMould;

}
