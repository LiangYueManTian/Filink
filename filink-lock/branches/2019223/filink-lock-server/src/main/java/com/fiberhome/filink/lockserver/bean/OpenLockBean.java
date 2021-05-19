package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

import java.util.List;

/**
 * 开锁实体
 * @author CongcaiYu
 */
@Data
public class OpenLockBean {

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 锁具编号集合
     */
    private List<String> slotNumList;
}
