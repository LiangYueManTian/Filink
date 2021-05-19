package com.fiberhome.filink.rfid.req.rfid;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

/**
 * 删除熔纤信息
 *
 * @author liyj
 * @date 2019/7/25
 */
@Data
public class DeleteCoreInfoReq {

    /**
     * 中间节点信息
     */
    private String intermediateNodeDeviceId;

    /**
     * 所属光缆段
     */
    private String resource;

    /**
     * 所属纤芯号
     */
    private String cableCoreNo;

}
