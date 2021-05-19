package com.fiberhome.filink.rfid.constant;

/**
 * 应用常量
 *
 * @author liyj
 * @date 2019/5/28
 */
public class AppConstant {
    /**
     * 分隔符 ,
     */
    public static final String SEPARATOR = ",";
    /**
     * 业务配置信息 下划线
     */
    public static final String SEPARATOR_INDEX_UP_LINE = "-";
    /**
     * 下划线
     */
    public static final String SEPARATOR_INDEX_DOWN_LINE = "_";
    /**
     * '*'
     */
    public static final String SEPARATOR_INDEX_ASTERISK = "*";
    /**
     * mybatis 隔离符号
     */
    public static final String SEPARATOR_INDEX_MYBATIS_SPIT = "#";
    /**
     * 光缆id
     */
    public static final String OPTICAL_CABLE_ID = "opticCableId";
    /**
     * 光缆名称
     */
    public static final String OPTICAL_CABLE_NAME = "opticCableName";
    /**
     * 光缆段ID
     */
    public static final String OPTICAL_CABLE_SECTION_ID = "opticCableSectionId";
    /**
     * 光缆段名称
     */
    public static final String OPTICAL_CABLE_SECTION__NAME = "opticCableSectionName";
    /**
     * 光缆段智能标签ID
     */
    public static final String OPTICAL_CABLE_SECTION_RFID_ID = "opticStatusId";
    /**
     * 光缆段标签CODE
     */
    public static final String OPTICAL_CABLE_SECTION__RFID_NAME = "rfidCode";
    /**
     * 操作符(新增)
     */
    public static final String OPERATOR_TYPE_SAVE = "0";

    /**
     * 操作符(删除)
     */
    public static final String OPERATOR_TYPE_DELETE = "1";

    /**
     * 操作符(修改)
     */
    public static final String OPERATOR_TYPE_UPDATE = "2";
    /**
     * 端口状态
     */
    public static final String PORT_STATE = "state";
    /**
     * 设施id
     */
    public static final String DEVICE_ID = "id";
    /**
     * 纤芯数
     */
    public static final String CORE_NUM = "coreNum";

    /**
     * 数量
     */
    public static final String PORT_NUMBER = "number";

    /**
     * 操作符(更换智能标签code)
     */
    public static final String OPERATOR_TYPE_UPDATE_RFID_CODE = "3";
    /**
     * 端口绑定业务默认为0
     */
    public static final String BUS_BINDING_DEFAULT = "0-0-0";
    /**
     * 端口默认状态为 0/1 (无业务/有业务)
     */
    public static final Integer BUS_BINDING_DEFAULT_STATE = 0;
    /**
     * 端口默认状态为 0/1 (无业务/有业务)
     */
    public static final Integer BUS_BINDING_STATE_NO_OPERATOR = 1;
}
