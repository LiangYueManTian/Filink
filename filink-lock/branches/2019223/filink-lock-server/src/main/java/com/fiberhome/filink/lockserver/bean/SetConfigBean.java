package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 配置设施策略实体
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
    private Map<String,String> configParams;

}
