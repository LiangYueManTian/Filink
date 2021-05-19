package com.fiberhome.filink.rfid.req.template;

import lombok.Data;

/**
 * 实景图Req
 *
 * @author liyj
 * @date 2019/6/2
 */
@Data
public class RealReqDto {
    /**
     * 主键id
     */
    private String id;
    /**
     * A/B面 templateSideEnum
     */
    private Integer side;
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 设施类型 TemplateDeviceTypeEnum
     */
    private Integer deviceType;
    /**
     * 标签id  App 端预留
     */
    private Integer labelId;
    /**
     * 实际框号 给页面显示用 App 和前端都用这个字段
     */
    private Integer businessNum;
    /**
     * 模板编号 可能会改变
     */
    private Integer realNo;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 关系id
     */
    private String relationId;
}
