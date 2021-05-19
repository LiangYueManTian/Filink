package com.fiberhome.filink.alarmcurrentserver.enums;

/**
 * 告警类型枚举
 *
 * @author liyj
 * @date 2019/5/6
 */
public enum AlarmTypeEnum {

    /**
     * 设施告警
     */
    DEVICE_ALARM(1),

    /**
     * 工单告警
     */
    ORDER_ALARM(2);

    /**
     * 告警类型 index
     */
    private int indexType;

    AlarmTypeEnum(int indexType) {
        this.indexType = indexType;
    }

    public int getIndexType() {
        return indexType;
    }


}