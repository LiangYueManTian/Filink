package com.fiberhome.filink.rfid.resp.fibercore.app;

import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import lombok.Data;

/**
 * <p>
 * app跳接信息返回类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-06-03
 */
@Data
public class JumpFiberInfoRespForApp extends JumpFiberInfo {
    /**
     * 本端跳纤标签状态（0、正常；1、异常）
     */
    private String rfidStatus;
    /**
     * 对端跳纤标签状态（0、正常；1、异常）
     */
    private String oppositeRfidStatus;

}
