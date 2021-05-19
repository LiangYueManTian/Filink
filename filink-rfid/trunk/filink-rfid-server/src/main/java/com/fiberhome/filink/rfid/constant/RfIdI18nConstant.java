package com.fiberhome.filink.rfid.constant;

/**
 * 返回码国际化定义
 *
 * @author chaofanrong
 * @date 2019/6/25
 */
public class RfIdI18nConstant {

    /**------------------------------------------光缆------------------------------------------ */
    /**
     * 保存光缆成功
     */
    public static final String SAVE_OPTIC_CABLE_SUCCESS = "SAVE_OPTIC_CABLE_SUCCESS";

    /**
     * 保存光缆失败
     */
    public static final String SAVE_OPTIC_CABLE_FAIL = "SAVE_OPTIC_CABLE_FAIL";

    /**
     * 修改光缆成功
     */
    public static final String UPDATE_OPTIC_CABLE_SUCCESS = "UPDATE_OPTIC_CABLE_SUCCESS";

    /**
     * 修改光缆失败
     */
    public static final String UPDATE_OPTIC_CABLE_FAIL = "UPDATE_OPTIC_CABLE_FAIL";

    /**
     * 删除光缆成功
     */
    public static final String DELETE_OPTIC_CABLE_SUCCESS = "DELETE_OPTIC_CABLE_SUCCESS";

    /**
     * 删除光缆失败
     */
    public static final String DELETE_OPTIC_CABLE_FAIL = "DELETE_OPTIC_CABLE_FAIL";

    /**
     * 光缆参数错误
     */
    public static final String OPTIC_CABLE_PARAM_ERROR = "OPTIC_CABLE_PARAM_ERROR";

    /**
     * 光缆名称重复
     */
    public static final String OPTIC_CABLE_NAME_SAME = "OPTIC_CABLE_NAME_SAME";

    /**
     * 光缆不存在
     */
    public static final String OPTIC_CABLE_IS_NOT_EXIST = "OPTIC_CABLE_IS_NOT_EXIST";

    /**
     * 包含光缆段信息
     */
    public static final String OPTIC_CABLE_CONTAINS_OPTIC_CABLE_SECTION = "OPTIC_CABLE_CONTAINS_OPTIC_CABLE_SECTION";

    /**
     * 光缆超过最大纤芯数
     */
    public static final String OPTIC_CABLE_EXCEED_THE_CORE_MAXIMUM = "OPTIC_CABLE_EXCEED_THE_CORE_MAXIMUM";

    /**------------------------------------------光缆级别------------------------------------------ */
    /**
     * 本地接入-主干光缆
     */
    public static final String OPTIC_CABLE_LEVEL_TRUNK = "OPTIC_CABLE_LEVEL_TRUNK";
    /**
     * 本地接入-末端光缆
     */
    public static final String OPTIC_CABLE_LEVEL_END = "OPTIC_CABLE_LEVEL_END";
    /**
     * 一级干线
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_ONE = "OPTIC_CABLE_LEVEL_LEVEL_ONE";
    /**
     * 二级干线
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_LEVEL_TWO = "OPTIC_CABLE_LEVEL_LEVEL_LEVEL_TWO";
    /**
     * 本地中继
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_REPEATER = "OPTIC_CABLE_LEVEL_LEVEL_REPEATER";
    /**
     * 本地核心
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_KERNEL = "OPTIC_CABLE_LEVEL_LEVEL_KERNEL";
    /**
     * 本地汇聚
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_CONVERGE = "OPTIC_CABLE_LEVEL_LEVEL_CONVERGE";
    /**
     * 汇接层光缆
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_JUNCTION = "OPTIC_CABLE_LEVEL_LEVEL_JUNCTION";
    /**
     * 联络光缆
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_CONTACT = "OPTIC_CABLE_LEVEL_LEVEL_CONTACT";
    /**
     * 局内光缆
     */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_INSIDE = "OPTIC_CABLE_LEVEL_LEVEL_INSIDE";
    /**------------------------------------------光缆级别------------------------------------------ */

    /**------------------------------------------布线类型------------------------------------------ */
    /**
     * 递减
     */
    public static final String WIRING_TYPE_DIMINISHING = "WIRING_TYPE_DIMINISHING";
    /**
     * 不递减
     */
    public static final String WIRING_TYPE_NOT_DIMINISH = "WIRING_TYPE_NOT_DIMINISH";
    /**------------------------------------------布线类型------------------------------------------ */

    /**------------------------------------------拓扑结构------------------------------------------ */
    /**
     * 环形
     */
    public static final String TOPOLOGY_CIRCULARITY = "TOPOLOGY_CIRCULARITY";
    /**
     * 非环形
     */
    public static final String TOPOLOGY_NON_CIRCULARITY = "TOPOLOGY_NON_CIRCULARITY";
    /**------------------------------------------拓扑结构------------------------------------------ */

    /**------------------------------------------光缆------------------------------------------ */

    /**------------------------------------------光缆段------------------------------------------ */
    /**
     * 上传光缆段成功
     */
    public static final String SAVE_OPTIC_CABLE_SECTION_SUCCESS = "SAVE_OPTIC_CABLE_SECTION_SUCCESS";

    /**
     * 上传光缆段失败
     */
    public static final String SAVE_OPTIC_CABLE_SECTION_FAIL = "SAVE_OPTIC_CABLE_SECTION_FAIL";

    /**
     * 删除光缆段成功
     */
    public static final String DELETE_OPTIC_CABLE_SECTION_SUCCESS = "DELETE_OPTIC_CABLE_SECTION_SUCCESS";

    /**
     * 删除光缆段失败
     */
    public static final String DELETE_OPTIC_CABLE_SECTION_FAIL = "DELETE_OPTIC_CABLE_SECTION_FAIL";

    /**
     * 光缆段的纤芯数不能大于光缆段的纤芯数
     */
    public static final String OPTIC_CABLE_SECTION_CORE_OVERRUN = "OPTIC_CABLE_SECTION_CORE_OVERRUN";
    /**
     * 所属光缆ID参数错误
     */
    public static final String BELONG_OPTIC_CABLE_ID_ERROR = "BELONG_OPTIC_CABLE_ID_ERROR";
    /**
     * 光缆id 为空
     */
    public static final String OPTIC_CABLE_SECTION_ID_IS_NULL = "BELONG_OPTIC_CABLE_ID_ERROR";

    /**
     * 光缆段包含业务
     */
    public static final String OPTIC_CABLE_SECTION_HAVE_SERVER = "OPTIC_CABLE_SECTION_HAVE_SERVER";

    /**
     * 光缆段名称重复
     */
    public static final String OPTIC_CABLE_SECTION_NAME_SAME = "OPTIC_CABLE_SECTION_NAME_SAME";

    /**
     * 光缆段不存在
     */
    public static final String OPTIC_CABLE_SECTION_NOT_EXIST = "OPTIC_CABLE_SECTION_NOT_EXIST";

    /**------------------------------------------状态------------------------------------------ */
    /**
     * 使用
     */
    public static final String STATUS_USE = "STATUS_USE";
    /**
     * 未使用
     */
    public static final String STATUS_UNUSED = "STATUS_UNUSED";
    /**------------------------------------------状态------------------------------------------ */

    /**------------------------------------------光缆段------------------------------------------ */

    /**
     * 该用户没有智能标签业务权限
     */
    public static final String NOT_HAVE_PERMISSION_FOR_RFID = "NOT_HAVE_PERMISSION_FOR_RFID";

    /*----------------------------成端----------------------------*/
    /**
     * 保存成端信息成功
     */
    public static final String SAVE_PORT_CORE_SUCCESS = "SAVE_PORT_CORE_SUCCESS";

    /**
     * 保存成端信息失败
     */
    public static final String SAVE_PORT_CORE_FAIL = "SAVE_PORT_CORE_FAIL";


    /**
     * 修改成端信息成功
     */
    public static final String UPDATE_PORT_CORE_SUCCESS = "UPDATE_PORT_CORE_SUCCESS";

    /**
     * 修改成端信息失败
     */
    public static final String UPDATE_PORT_CORE_FAIL = "UPDATE_PORT_CORE_FAIL";


    /**
     * 删除成端信息成功
     */
    public static final String DELETE_PORT_CORE_SUCCESS = "DELETE_PORT_CORE_SUCCESS";

    /**
     * 删除成端信息失败
     */
    public static final String DELETE_PORT_CORE_FAIL = "DELETE_PORT_CORE_FAIL";
    /*----------------------------成端----------------------------*/

    /*----------------------------熔纤----------------------------*/
    /**
     * 保存熔纤信息成功
     */
    public static final String SAVE_CORE_CORE_SUCCESS = "SAVE_CORE_CORE_SUCCESS";

    /**
     * 保存熔纤信息失败
     */
    public static final String SAVE_CORE_CORE_FAIL = "SAVE_CORE_CORE_FAIL";


    /**
     * 修改熔纤信息成功
     */
    public static final String UPDATE_CORE_CORE_SUCCESS = "UPDATE_CORE_CORE_SUCCESS";

    /**
     * 修改熔纤信息失败
     */
    public static final String UPDATE_CORE_CORE_FAIL = "UPDATE_CORE_CORE_FAIL";


    /**
     * 删除熔纤信息成功
     */
    public static final String DELETE_CORE_CORE_SUCCESS = "DELETE_CORE_CORE_SUCCESS";

    /**
     * 删除熔纤信息失败
     */
    public static final String DELETE_CORE_CORE_FAIL = "DELETE_CORE_CORE_FAIL";
    /*----------------------------熔纤----------------------------*/

    /*----------------------------跳接----------------------------*/
    /**
     * 保存跳接信息成功
     */
    public static final String SAVE_JUMP_CORE_SUCCESS = "SAVE_JUMP_CORE_SUCCESS";

    /**
     * 保存跳接信息失败
     */
    public static final String SAVE_JUMP_CORE_FAIL = "SAVE_JUMP_CORE_FAIL";


    /**
     * 修改跳接信息成功
     */
    public static final String UPDATE_JUMP_CORE_SUCCESS = "UPDATE_JUMP_CORE_SUCCESS";

    /**
     * 修改跳接信息失败
     */
    public static final String UPDATE_JUMP_CORE_FAIL = "UPDATE_JUMP_CORE_FAIL";


    /**
     * 删除跳接信息成功
     */
    public static final String DELETE_JUMP_CORE_SUCCESS = "DELETE_JUMP_CORE_SUCCESS";

    /**
     * 删除跳接信息失败
     */
    public static final String DELETE_JUMP_CORE_FAIL = "DELETE_JUMP_CORE_FAIL";

    /**
     * 端口已被跳接
     */
    public static final String PORT_HAVE_JUMP_CORE = "PORT_HAVE_JUMP_CORE";

    /**
     * 端口已被跳接
     */
    public static final String UPDATE_RFID_CODE_SUCCESS = "UPDATE_RFID_CODE_SUCCESS";

    /**
     * 超过最大跳接数
     */
    public static final String EXCEED_JUMP_CORE_MAX_NUM = "EXCEED_JUMP_CORE_MAX_NUM";

    /**
     * 跳接信息不存在
     */
    public static final String JUMP_CORE_NOT_EXIST = "JUMP_CORE_NOT_EXIST";

    /*----------------------------跳接----------------------------*/

    /*----------------------------智能标签----------------------------*/
    /**
     * 保存智能标签信息成功
     */
    public static final String SAVE_RFID_SUCCESS = "SAVE_RFID_SUCCESS";

    /**
     * 保存智能标签信息失败
     */
    public static final String SAVE_RFID_FAIL = "SAVE_RFID_FAIL";


    /**
     * 修改智能标签信息成功
     */
    public static final String UPDATE_RFID_SUCCESS = "UPDATE_RFID_SUCCESS";

    /**
     * 修改智能标签信息失败
     */
    public static final String UPDATE_RFID_FAIL = "UPDATE_RFID_FAIL";


    /**
     * 删除智能标签信息成功
     */
    public static final String DELETE_RFID_SUCCESS = "DELETE_RFID_SUCCESS";

    /**
     * 删除智能标签信息失败
     */
    public static final String DELETE_RFID_FAIL = "DELETE_RFID_FAIL";
    /*----------------------------智能标签----------------------------*/


    /*----------------------------光缆段gis----------------------------*/
    /**
     * 修改光缆段gis成功
     */
    public static final String UPDATE_OPTIC_CABLE_SECTION_RFID_SUCCESS = "UPDATE_OPTIC_CABLE_SECTION_RFID_SUCCESS";

    /**
     * 修改光缆段gis失败
     */
    public static final String UPDATE_OPTIC_CABLE_SECTION_RFID_FAIL = "UPDATE_OPTIC_CABLE_SECTION_RFID_FAIL";

    /**
     * 光缆段gis标签信息不存在
     */
    public static final String OPTIC_CABLE_SECTION_RFID_NOT_EXIST = "OPTIC_CABLE_SECTION_RFID_NOT_EXIST";

    /**
     * 删除光缆段gis成功
     */
    public static final String DELETE_OPTIC_CABLE_SECTION_RFID_SUCCESS = "DELETE_OPTIC_CABLE_SECTION_RFID_SUCCESS";
    /*----------------------------光缆段gis----------------------------*/


    /*----------------------------common----------------------------*/
    /**
     * 参数错误
     */
    public static final String PARAMS_ERROR = "PARAMS_ERROR";

    /**
     * 获取用户信息异常
     */
    public static final String USER_SERVER_ERROR = "USER_SERVER_ERROR";
    /**
     * 新增模板参数错误
     */
    public static final String CREATE_TEMPLATE_PARAM_ERROR = "CREATE_TEMPLATE_PARAM_ERROR";
    /**
     * 保存模板成功
     */
    public static final String CREATE_TEMPLATE_PARAM_SUCCESS = "CREATE_TEMPLATE_PARAM_SUCCESS";
    /**
     * 查询模板参数错误
     */
    public static final String QUERY_TEMPLATE_PARAM_ERROR = "QUERY_TEMPLATE_PARAM_ERROR";
    /**
     * 查询实景图参数错误
     */
    public static final String QUERY_REALPOSITION_PARAM_ERROR = "QUERY_REALPOSITION_PARAM_ERROR";
    /**
     * 框id 为空
     */
    public static final String QUERY_REALPOSITION_FRAMEID_ERROR = "QUERY_REALPOSITION_FRAMEID_ERROR";
    /**
     * 设施id 为空
     */
    public static final String QUERY_REALPOSITION_DEVICE_ID_ERROR = "QUERY_REALPOSITION_DEVICE_ID_ERROR";
    /**
     * 端口Id 为空
     */
    public static final String PORT_ID_IS_NULL = "PORT_ID_IS_NULL";
    /**
     * 编号规则为空
     */
    public static final String TEMPLATE_CODE_RULE_IS_NULL = "TEMPLATE_CODE_RULE_IS_NULL";
    /**
     * 该设施下没有配置箱框信息
     */
    public static final String DEVICE_IS_NOT_CONFIGURE = "DEVICE_IS_NOT_CONFIGURE";
    /**
     * 保存模板和设施关系错误
     */
    public static final String SAVE_TEMPLATE_RELATION_ERROR = "SAVE_TEMPLATE_RELATION_ERROR";
    /**
     * 保存模板和设施关系成功
     */
    public static final String SAVE_TEMPLATE_RELATION_SUCCESS = "SAVE_TEMPLATE_RELATION_SUCCESS";
    /**
     * 查询坐标信息错误
     */
    public static final String QUERY_POSITION_INFO_ERROR = "QUERY_POSITION_INFO_ERROR";

    /**
     * 微调新增box param is null
     */
    public static final String ADD_DEVICE_ENTITY_PARAM_BOX_DATA_NULL = "ADD_DEVICE_ENTITY_PARAM_BOX_DATA_NULL";
    /**
     * 微调新增frame param is null
     */
    public static final String ADD_DEVICE_ENTITY_PARAM_NEW_FRAME_DATA_NULL = "ADD_DEVICE_ENTITY_PARAM_NEW_FRAME_DATA_NULL";
    /**
     * 微调新增disc param is null
     */
    public static final String ADD_DEVICE_ENTITY_PARAM_NEW_DISC_DATA_NULL = "ADD_DEVICE_ENTITY_PARAM_NEW_DISC_DATA_NULL";


    /**
     * 导出没有数据
     */
    public static final String EXPORT_NO_DATA = "EXPORT_NO_DATA";

    /**
     * 导出超过最大限制
     */
    public static final String EXPORT_DATA_TOO_LARGE = "EXPORT_DATA_TOO_LARGE";

    /**
     * 当前用户超过最大任务数量
     */
    public static final String EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = "EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS";

    /**
     * 创建导出任务失败
     */
    public static final String FAILED_TO_CREATE_EXPORT_TASK = "FAILED_TO_CREATE_EXPORT_TASK";

    /**
     * 上传数据type错误
     */
    public static final String UPLOAD_DATA_TYPE_ERROR = "UPLOAD_DATA_TYPE_ERROR";

    /**
     * 设施对应模板信息不存在
     */
    public static final String DEVICE_TEMPLATE_INFO_NOT_EXIST = "DEVICE_TEMPLATE_INFO_NOT_EXIST";

    /**
     * 端口信息不存在
     */
    public static final String PORT_INFO_NOT_EXIST = "PORT_INFO_NOT_EXIST";

    /**
     * 数据上传类型异常
     */
    public static final String UPDATE_TYPE_IS_WRONG = "UPDATE_TYPE_IS_WRONG";

    /**
     * 标签已存在
     */
    public static final String LABEL_IS_EXISTED = "LABEL_IS_EXISTED";

    /**
     * 目标端口已经绑定标签
     */
    public static final String THE_TARGET_IS_ALREADY_BOUND_TO_THE_LABEL = "THE_TARGET_IS_ALREADY_BOUND_TO_THE_LABEL";
    /**
     * 标签无信息
     */
    public static final String CAN_NOT_FIND_INFO_WITH_THIS_LABEL = "CAN_NOT_FIND_INFO_WITH_THIS_LABEL";
    /**
     * 修改绑定业务 端口为空
     */
    public static final String UPDATE_PORT_BINDING_PORT_IS_NULL = "UPDATE_PORT_BINDING_PORT_IS_NULL";
    /**
     * 修改绑定业务 状态为空
     */
    public static final String UPDATE_PORT_BINDING_STATE_IS_NULL = "UPDATE_PORT_BINDING_STATE_IS_NULL";
    /**
     * 修改绑定业务 业务类型为空
     */
    public static final String UPDATE_PORT_BINDING_BUS_TYPE_IS_NULL = "UPDATE_PORT_BINDING_BUS_TYPE_IS_NULL";
    /**
     * 修改业务状态 原来的数据为空
     */
    public static final String QUERY_BINDING_INFO_IS_NULL = "QUERY_BINDING_INFO_IS_NULL";
    /**
     * 改设施以存在 请更换设施
     */
    public static final String DEVICE_IS_EXISTS_BUS = "DEVICE_IS_EXISTS_BUS";

    /**
     * 修改模板信息为空
     */
    public static final String UPDATE_TEMPLATE_PARAM_IS_NULL = "UPDATE_TEMPLATE_PARAM_IS_NULL";
    /**
     * 删除模板参数为空
     */
    public static final String DELETE_TEMPLATE_PARAM_IS_NULL = "DELETE_TEMPLATE_PARAM_IS_NULL";
    /**
     * 删除模板成功
     */
    public static final String DELETE_TEMPLATE_SUCCESS = "DELETE_TEMPLATE_SUCCESS";
    /**
     * 更新模板成功
     */
    public static final String UPDATE_TEMPLATE_SUCCESS = "UPDATE_TEMPLATE_SUCCESS";
    /**
     * 设施不允许修改
     */
    public static final String DEVICE_IS_NOT_UPDATE = "DEVICE_IS_NOT_UPDATE";
    /**
     * 设施不允许删除
     */
    public static final String DEVICE_IS_NOT_DELETE = "DEVICE_IS_NOT_DELETE";
    /**
     * 模板不允许删除
     */
    public static final String TEMPLATE_IS_NOT_DELETE = "TEMPLATE_IS_NOT_DELETE";

    /**
     * 修改端口信息没有发现业务类型
     */
    public static final String UPDATE_PORT_BINDING_BUS_TYPE_NOT_FIND = "UPDATE_PORT_BINDING_BUS_TYPE_NOT_FIND";
    /**
     * 配置业务信息成功
     */
    public static final String DEPLOY_BUS_INFO_SUCCESS = "DEPLOY_BUS_INFO_SUCCESS";
    /**
     * 没有查询到对应的数据
     */
    public static final String NOT_QUERY_DATA = "NOT_QUERY_DATA";
    /**
     * 模板名称已存在
     */
    public static final String TEMPLATE_NAME_IS_EXITS = "TEMPLATE_NAME_IS_EXITS";
    /**
     * 光缆列表
     */
    public static final String OPTIC_CABLE_LIST = "OPTIC_CABLE_LIST";

    /**
     * 光缆段列表
     */
    public static final String OPTIC_CABLE_SECTION_LIST = "OPTIC_CABLE_SECTION_LIST";

    /**
     * 跳接列表
     */
    public static final String JUMP_FIBER_LIST = "JUMP_FIBER_LIST";

    /**
     * 创建导出任务成功
     */
    public static final String THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY = "THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY";

    /**------------------------------------------设施节点状态------------------------------------------ */
    /**
     * 设施不存在
     */
    public static final String DEVICE_NODE_NOT_EXISTS = "DEVICE_NODE_NOT_EXISTS";
    /**
     * 设施服务异常
     */
    public static final String DEVICE_SERVER_ERROR = "DEVICE_SERVER_ERROR";
    /**
     * 设施存在
     */
    public static final String DEVICE_NODE_EXISTS = "DEVICE_NODE_EXISTS";
    /**------------------------------------------设施节点状态------------------------------------------ */


    /*----------------设施类型----------------*/
    /**
     * 光交箱
     */
    public static final String DEVICE_TYPE_001 = "DEVICE_TYPE_001";
    /**
     * 人井
     */
    public static final String DEVICE_TYPE_030 = "DEVICE_TYPE_030";
    /**
     * 配线架
     */
    public static final String DEVICE_TYPE_060 = "DEVICE_TYPE_060";
    /**
     * 接头盒
     */
    public static final String DEVICE_TYPE_090 = "DEVICE_TYPE_090";
    /**
     * 分纤箱
     */
    public static final String DEVICE_TYPE_150 = "DEVICE_TYPE_150";
    /*----------------设施类型----------------*/

    /*----------------------------端口状态----------------------------*/
    /**
     * 端口状态(预占用)
     */
    public static final String PORT_STATUS_PRE_OCCUPY = "PORT_STATUS_PRE_OCCUPY";
    /**
     * 端口状态(占用)
     */
    public static final String PORT_STATUS_OCCUPY = "PORT_STATUS_OCCUPY";
    /**
     * 端口状态(空闲)
     */
    public static final String PORT_STATUS_FREE = "PORT_STATUS_FREE";
    /**
     * 端口状态(异常)
     */
    public static final String PORT_STATUS_EXCEPTION = "PORT_STATUS_EXCEPTION";
    /**
     * 端口状态(虚占)
     */
    public static final String PORT_STATUS_VIRTUAL_OCCUPY = "PORT_STATUS_VIRTUAL_OCCUPY";
    /*----------------------------端口状态----------------------------*/

    /*----------------------------标签类型----------------------------*/
    /**
     * 标签类型(rfid)
     */
    public static final String RFID_TYPE_RFID = "RFID_TYPE_RFID";
    /**
     * 标签类型(二维码)
     */
    public static final String RFID_TYPE_QR_CODE = "RFID_TYPE_QR_CODE";
    /*----------------------------标签类型----------------------------*/

    /*----------------------------标签状态----------------------------*/
    /**
     * 标签状态(正常)
     */
    public static final String RFID_STATUS_NORMAL = "RFID_STATUS_NORMAL";
    /**
     * 标签状态(异常)
     */
    public static final String RFID_STATUS_ABNORMAL = "RFID_STATUS_ABNORMAL";
    /*----------------------------标签类型----------------------------*/

    /**
     * 智能标签id已存在
     */
    public static final String RFID_CODE_IS_EXISTS = "RFID_CODE_IS_EXISTS";

    /*----------------------------common----------------------------*/


    /*----------------------------统计----------------------------*/
    /**
     * 光缆列表
     */
    public static final String EXPORT_OPTICAL_FIBER_STATISTICS = "EXPORT_OPTICAL_FIBER_STATISTICS";
    /**
     * 光缆段列表
     */
    public static final String EXPORT_OPTICAL_FIBER_SECTION_STATISTICS = "EXPORT_OPTICAL_FIBER_SECTION_STATISTICS";
    /**
     * 纤芯列表
     */
    public static final String EXPORT_CORE_STATE_STATISTICS = "EXPORT_CORE_STATE_STATISTICS";
    /**
     * 柜内跳接
     */
    public static final String EXPORT_JUMP_CONNECTION_IN_CABINET = "EXPORT_JUMP_CONNECTION_IN_CABINET";
    /**
     * 柜间跳接
     */
    public static final String EXPORT_JUMP_CONNECTION_OUT_CABINET = "EXPORT_JUMP_CONNECTION_OUT_CABINET";
    /**
     * 跳纤侧端口统计
     */
    public static final String EXPORT_JUMP_FIBER_PORT_STATISTICS = "EXPORT_JUMP_FIBER_PORT_STATISTICS";
    /**
     * 熔纤纤侧端口统计
     */
    public static final String EXPORT_MELT_FIBER_PORT_STATISTICS = "EXPORT_MELT_FIBER_PORT_STATISTICS";
    /**
     * 盘端口统计
     */
    public static final String EXPORT_DISC_PORT_STATISTICS = "EXPORT_DISC_PORT_STATISTICS";
    /**
     * 框端口统计
     */
    public static final String EXPORT_FRAME_PORT_STATISTICS = "EXPORT_FRAME_PORT_STATISTICS";
    /**
     * TopN端口使用率统计
     */
    public static final String EXPORT_TOP_NUMBER_PORT_STATISTICS = "EXPORT_TOP_NUMBER_PORT_STATISTICS";
    /*----------------------------统计----------------------------*/


}
