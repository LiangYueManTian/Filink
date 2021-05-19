package com.fiberhome.filink.filinkoceanconnectserver.constant;

/**
 * 平台请求地址
 * @author CongcaiYu
 */
public class OceanConnectUrl {

    public static final String REQUEST_HEADER = "https://";
    public static final String CALLBACK_HEADER = "https://";
    public static final String CALLBACK_RECEIVE = "/receive";

    public static final String BIND_DEVICE_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/bindDevice";
    public static final String DEVICE_ADDED_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/addDevice";
    public static final String DEVICE_INFO_CHANGED_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/updateDeviceInfo";
    public static final String DEVICE_DATA_CHANGED_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/updateDeviceData";
    public static final String DEVICE_DELETED_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/deletedDevice";
    public static final String SERVICE_INFO_CHANGED_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/updateServiceInfo";
    public static final String RULE_EVENT_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/RulEevent";
    public static final String DEVICE_DATAS_CHANGED_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/updateDeviceDatas";
    public static final String SW_UPGRADE_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/upgradeSW";
    public static final String FW_UPGRADE_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/upgradeFW";


    public static final String REPORT_CMD_EXEC_RESULT_CALLBACK_URL = "/na/iocm/devNotify/v1.1.0/reportCmdExecResult";


    public static final String APP_AUTH = "/iocm/app/sec/v1.1.0/login";
    public static final String REFRESH_TOKEN = "/iocm/app/sec/v1.1.0/refreshToken";


    public static final String REGISTER_DIRECT_CONNECTED_DEVICE = "/iocm/app/reg/v1.1.0/deviceCredentials";
    public static final String MODIFY_DEVICE_INFO = "/iocm/app/dm/v1.4.0/devices/";
    public static final String QUERY_DEVICE_ACTIVATION_STATUS = "/iocm/app/reg/v1.1.0/deviceCredentials";
    public static final String DELETE_DIRECT_CONNECTED_DEVICE = "/iocm/app/dm/v1.4.0/devices";

    public static final String CREATE_BATCH_TASK = "/iocm/app/batchtask/v1.1.0/tasks";
    public static final String QUERY_SPECIFY_BATCH_TASK = "/iocm/app/batchtask/v1.1.0/tasks";
    public static final String QUERY_BATCH_TASK_DETAILS = "/iocm/app/batchtask/v1.1.0/taskDetails";

    public static final String QUERY_SPECIFY_DEVICE = "/iocm/app/dm/v1.4.0/devices";
    public static final String QUERY_BATCH_DEVICES = "/iocm/app/dm/v1.4.0/devices";
    public static final String QUERY_DEVICE_HISTORY_DATA = "/iocm/app/data/v1.2.0/deviceDataHistory";
    public static final String QUERY_DEVICE_CAPABILITIES = "/iocm/app/data/v1.1.0/deviceCapabilities";

    public static final String SUBSCRIBE_SERVICE_NOTIFYCATION = "/iocm/app/sub/v1.2.0/subscriptions";
    public static final String SUBSCRIBE_MANAGEMENT_NOTIFYCATION = "/iodm/app/sub/v1.1.0/subscribe";
    public static final String QUERY_SPECIFY_SUBSCRIPTION = "/iocm/app/sub/v1.2.0/subscriptions";
    public static final String QUERY_BATCH_SUBSCRIPTIONS = "/iocm/app/sub/v1.2.0/subscriptions";
    public static final String DELETE_SPECIFY_SUBSCRIPTION = "/iocm/app/sub/v1.2.0/subscriptions";
    public static final String DELETE_BATCH_SUBSCRIPTIONS = "/iocm/app/sub/v1.2.0/subscriptions";

    public static final String CREATE_DEVICE_CMD = "/iocm/app/cmd/v1.4.0/deviceCommands";
    public static final String QUERY_DEVICE_CMD = "/iocm/app/cmd/v1.4.0/deviceCommands";
    public static final String MODIFY_DEVICE_COMMAND = "/iocm/app/cmd/v1.4.0/deviceCommands";
    public static final String CREATE_DEVICECMD_CANCEL_TASK = "/iocm/app/cmd/v1.4.0/deviceCommandCancelTasks";
    public static final String QUERY_DEVICECMD_CANCEL_TASK = "/iocm/app/cmd/v1.4.0/deviceCommandCancelTasks";

    public static final String CREATE_DEVICE_GROUP = "/iocm/app/devgroup/v1.3.0/devGroups";
    public static final String MODIFY_DEVICE_GROUP = "/iocm/app/devgroup/v1.3.0/devGroups";
    public static final String DELETE_DEVICE_GROUP = "/iocm/app/devgroup/v1.3.0/devGroups";
    public static final String QUERY_SPECIFY_DEVICE_GROUP = "/iocm/app/devgroup/v1.3.0/devGroups";
    public static final String QUERY_DEVICE_GROUPS = "/iocm/app/devgroup/v1.3.0/devGroups";
    public static final String QUERY_DEVICE_GROUP_MEMBERS = "/iocm/app/dm/v1.2.0/devices/ids";
    public static final String ADD_DEVICE_GROUP_MEMBER = "/iocm/app/dm/v1.1.0/devices/addDevGroupTagToDevices";
    public static final String DELETE_DEVICE_GROUP_MEMBER = "/iocm/app/dm/v1.1.0/devices/deleteDevGroupTagFromDevices";


    public static final String CREATE_SW_UPGRADE_TASK = "/iodm/northbound/v1.5.0/operations/softwareUpgrade";
    public static final String CREATE_FW_UPGRADE_TASK = "/iodm/northbound/v1.5.0/operations/firmwareUpgrade";
    public static final String QUERY_SPECIFY_PACKAGE = "/iodm/northbound/v1.5.0/category";
    public static final String QUERY_BATCH_PACKAGES = "/iodm/northbound/v1.5.0/category";
    public static final String DELETE_SPECIFY_PACKAGE = "/iodm/northbound/v1.5.0/category";
    public static final String QUERY_SPECIFY_UPGRADE_TASK_RESULT = "/iodm/northbound/v1.5.0/operations";
    public static final String QUERY_UPGRADE_TASKS = "/iodm/northbound/v1.5.0/operations";


    public static final String BIND_DEVICE = "bindDevice";
    public static final String DEVICE_ADDED = "deviceAdded";
    public static final String DEVICE_INFO_CHANGED = "deviceInfoChanged";
    public static final String DEVICE_DATA_CHANGED = "deviceDataChanged";
    public static final String DEVICE_DELETED = "deviceDeleted";
    public static final String SERVICE_INFO_CHANGED = "serviceInfoChanged";
    public static final String RULE_EVENT = "ruleEvent";
    public static final String DEVICE_DATAS_CHANGED = "deviceDatasChanged";

    public static final String SW_UPGRADE_STATE_CHANGED = "swUpgradeStateChangeNotify";
    public static final String SW_UPGRADE_RESULT = "swUpgradeResultNotify";
    public static final String FW_UPGRADE_STATE_CHANGED = "fwUpgradeStateChangeNotify";
    public static final String FW_UPGRADE_RESULT = "fwUpgradeResultNotify";

}
