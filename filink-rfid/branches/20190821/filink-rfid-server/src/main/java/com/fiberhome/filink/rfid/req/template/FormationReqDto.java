package com.fiberhome.filink.rfid.req.template;

import lombok.Data;

/**
 * 成端选择req
 *
 * @author liyj
 * @date 2019/6/4
 */
@Data
public class FormationReqDto {

    /**
     * 框id
     */
    private String frameId;
    /**
     * 箱的A/B 面
     */
    private Integer boxSide;


}
