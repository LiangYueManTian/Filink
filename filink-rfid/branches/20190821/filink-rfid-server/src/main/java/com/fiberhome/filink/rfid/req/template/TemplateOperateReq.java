package com.fiberhome.filink.rfid.req.template;

import lombok.Data;

/**
 * 模板修改和删除req
 *
 * @author liyj
 * @date 2019/7/1
 */
@Data
public class TemplateOperateReq {

    /**
     * 设施 id
     */
    private String deviceId;
    /**
     * 模板id
     */
    private String templateId;
    /**
     * 模板类型 templateTypeEnum
     */
    private Integer templateType;

}
