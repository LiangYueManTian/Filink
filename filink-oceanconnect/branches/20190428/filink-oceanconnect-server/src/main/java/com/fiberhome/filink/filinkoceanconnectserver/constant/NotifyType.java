package com.fiberhome.filink.filinkoceanconnectserver.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅类型
 * @author CongcaiYu
 */
public class NotifyType {

    private static final String BIND_DEVICE = "bindDevice";
    private static final String DEVICE_ADDED = "deviceAdded";
    private static final String DEVICE_INFO_CHANGED = "deviceInfoChanged";
    private static final String DEVICE_DATA_CHANGED = "deviceDataChanged";
    private static final String DEVICE_DATAS_CHANGED = "deviceDatasChanged";
    private static final String DEVICE_DELETED = "deviceDeleted";
    private static final String SERVICE_INFO_CHANGED = "serviceInfoChanged";
    private static final String RULE_EVENT = "ruleEvent";

    /**
     * 获取订阅类型
     * @return 订阅类型集合
     */
    public static List<String> getServiceNotifyTypes () {
        List<String> notifyTypes = new ArrayList<>();
        notifyTypes.add(DEVICE_DATA_CHANGED);
        return notifyTypes;
    }
}
