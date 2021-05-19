package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 主控实体类
 *
 * @author CongcaiYu
 */
@Data
public class Control {
    /**
     * 操作类型 0-新增 1-更新
     */
    private String operateCode;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;


    /**
     * 主控信息
     */
    private ControlParam controlParam;

    /**
     * 参数校验
     */
    public void checkParams() {
        if (null == this
                || StringUtils.isEmpty(this.getOperateCode())
                || StringUtils.isEmpty(this.getDeviceId())
                || StringUtils.isEmpty(this.getDeviceName())
                || null == this.getControlParam()) {
            throw new FiLinkLockException("deviceId or controlParam is null>>>>>>>");
        }
    }
}
