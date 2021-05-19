package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 配置设施策略实体
 *
 * @author CongcaiYu
 */
@Data
public class SetConfigBean {
    /**
     * 设施序列id集合
     */
    private List<String> deviceIds;
    /**
     * 配置参数信息
     */
    private Map<String, String> configParams;

    /**
     * 校验参数
     */
    public void checkParams() {
        if (this == null
                || this.getDeviceIds() == null
                || this.getDeviceIds().size() == 0
                || this.getConfigParams() == null
                || this.getConfigParams().size() == 0) {
            throw new FiLinkControlException("deviceId or config value is null>>>>>>>>>");
        }
    }

}
