package com.fiberhome.filink.rfid.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 返回码常量定义
 *
 * @author liyj
 * @date 2019/5/28
 */
public class RfIdResultCodeConstant extends ResultCode {

    /**
     * 参数错误
     */
    public static final Integer PARAMS_ERROR = 150000;
    /**
     * 新增模板参数错误
     */
    public static final Integer CREATE_TEMPLATE_PARAM_ERROR = 150001;

    /**
     * 查询模板参数错误
     */
    public static final Integer QUERY_TEMPLATE_PARAM_ERROR = 150002;
    /**
     * 查询实景图参数错误
     */
    public static final Integer QUERY_REALPOSITION_PARAM_ERROR = 150003;
    /**
     * 框id 为空
     */
    public static final Integer QUERY_REALPOSITION_FRAMEID_ERROR = 150004;
    /**
     * 设施id 为空
     */
    public static final Integer QUERY_REALPOSITION_DEVICE_ID_ERROR = 150005;
    /**
     * 该设施下没有配置箱框信息
     */
    public static final Integer DEVICE_IS_NOT_CONFIGURE = 150006;
    /**
     * 保存模板和设施关系错误
     */
    public static final Integer SAVE_TEMPLATE_RELATION_ERROR = 150007;

    /**
     * 导出超过最大限制
     */
    public static final Integer EXPORT_DATA_TOO_LARGE = 150008;

    /**
     * 上传数据type错误
     */
    public static final Integer UPLOAD_DATA_TYPE_ERROR = 150009;

    /**
     * 设施对应模板信息不存在
     */
    public static final Integer DEVICE_TEMPLATE_INFO_NOT_EXIST = 150010;

    /**
     * 端口信息不存在
     */
    public static final Integer PORT_INFO_NOT_EXIST = 150011;
    /**
     * 端口id  为空
     */
    public static final Integer PORT_ID_IS_NULL = 150012;


    /**
     * 没有查询到数据
     */
    public static final Integer NOT_QUERY_DATA = 150013;
    /**
     * 编号规则为空
     */
    public static final Integer TEMPLATE_CODE_RULE_IS_NULL = 150014;
    /**
     * 查询坐标信息错误
     */
    public static final Integer QUERY_POSITION_INFO_ERROR = 150015;
    /**
     * 微调新增box param is null
     */
    public static final Integer ADD_DEVICE_ENTITY_PARAM_BOX_DATA_NULL = 150016;
    /**
     * 微调新增frame param is null
     */
    public static final Integer ADD_DEVICE_ENTITY_PARAM_NEW_FRAME_DATA_NULL = 150017;
    /**
     * 微调新增disc param is null
     */
    public static final Integer ADD_DEVICE_ENTITY_PARAM_NEW_DISC_DATA_NULL = 150018;
    /**
     * 修改绑定业务 端口为空
     */
    public static final Integer UPDATE_PORT_BINDING_PORT_IS_NULL = 150019;
    /**
     * 修改绑定业务 状态为空
     */
    public static final Integer UPDATE_PORT_BINDING_STATE_IS_NULL = 150020;
    /**
     * 修改绑定业务 业务类型为空
     */
    public static final Integer UPDATE_PORT_BINDING_BUS_TYPE_IS_NULL = 150021;
    /**
     * 修改业务状态 原来的数据为空
     */
    public static final Integer QUERY_BINDING_INFO_IS_NULL = 150022;
    /**
     * 修改端口信息没有发现业务类型
     */
    public static final Integer UPDATE_PORT_BINDING_BUS_TYPE_NOT_FIND = 150023;
    /**
     * 改设施以存在 请更换设施
     */
    public static final Integer DEVICE_IS_EXISTS_BUS = 150024;
    /**
     * 修改模板信息为空
     */
    public static final Integer UPDATE_TEMPLATE_PARAM_IS_NULL = 150025;
    /**
     * 删除模板参数为空
     */
    public static final Integer DELETE_TEMPLATE_PARAM_IS_NULL = 150026;
    /**
     * 设施不允许修改
     */
    public static final Integer DEVICE_IS_NOT_UPDATE = 150027;
    /**
     * 设施不允许删除
     */
    public static final Integer DEVICE_IS_NOT_DELETE = 150028;
    /**
     * 模板不允许删除
     */
    public static final Integer TEMPLATE_IS_NOT_DELETE = 150029;
    /**
     * 模板名称已存在
     */
    public static final Integer TEMPLATE_NAME_IS_EXITS = 150030;
    /**--------------------------成端start--------------------------*/

    /**
     * 保存成端信息成功
     */
    public static final Integer SAVE_PORT_CORE_SUCCESS = 150401;

    /**
     * 保存成端信息失败
     */
    public static final Integer SAVE_PORT_CORE_FAIL = 150402;


    /**
     * 修改成端信息成功
     */
    public static final Integer UPDATE_PORT_CORE_SUCCESS = 150403;

    /**
     * 修改成端信息失败
     */
    public static final Integer UPDATE_PORT_CORE_FAIL = 150404;


    /**
     * 删除成端信息成功
     */
    public static final Integer DELETE_PORT_CORE_SUCCESS = 150405;

    /**
     * 删除成端信息失败
     */
    public static final Integer DELETE_PORT_CORE_FAIL = 150406;

    /**--------------------------成端end--------------------------*/

    /**--------------------------熔纤start--------------------------*/
    /**
     * 保存熔纤信息成功
     */
    public static final Integer SAVE_CORE_CORE_SUCCESS = 150301;

    /**
     * 保存熔纤信息失败
     */
    public static final Integer SAVE_CORE_CORE_FAIL = 150302;


    /**
     * 修改熔纤信息成功
     */
    public static final Integer UPDATE_CORE_CORE_SUCCESS = 150303;

    /**
     * 修改熔纤信息失败
     */
    public static final Integer UPDATE_CORE_CORE_FAIL = 150304;


    /**
     * 删除熔纤信息成功
     */
    public static final Integer DELETE_CORE_CORE_SUCCESS = 150305;

    /**
     * 删除熔纤信息失败
     */
    public static final Integer DELETE_CORE_CORE_FAIL = 150306;

    /**--------------------------熔纤end--------------------------*/

    /**--------------------------跳接start--------------------------*/
    /**
     * 保存跳接信息成功
     */
    public static final Integer SAVE_JUMP_CORE_SUCCESS = 150501;

    /**
     * 保存跳接信息失败
     */
    public static final Integer SAVE_JUMP_CORE_FAIL = 150502;

    /**
     * 端口已被跳接
     */
    public static final Integer PORT_HAVE_JUMP_CORE = 150503;

    /**
     * 超过最大跳接数
     */
    public static final Integer EXCEED_JUMP_CORE_MAX_NUM = 150504;

    /**
     * 删除跳接信息成功
     */
    public static final Integer DELETE_JUMP_CORE_SUCCESS = 150505;

    /**
     * 跳接信息不存在
     */
    public static final Integer JUMP_CORE_NOT_EXIST = 150506;

    /**--------------------------跳接end--------------------------*/

    /**--------------------------光缆start--------------------------*/
    /**
     * 保存光缆成功
     */
    public static final Integer SAVE_OPTIC_CABLE_SUCCESS = 150101;

    /**
     * 保存光缆失败
     */
    public static final Integer SAVE_OPTIC_CABLE_FAIL = 150104;

    /**
     * 修改光缆成功
     */
    public static final Integer UPDATE_OPTIC_CABLE_SUCCESS = 150102;

    /**
     * 修改光缆失败
     */
    public static final Integer UPDATE_OPTIC_CABLE_FAIL = 150105;

    /**
     * 删除光缆成功
     */
    public static final Integer DELETE_OPTIC_CABLE_SUCCESS = 150103;

    /**
     * 删除光缆失败
     */
    public static final Integer DELETE_OPTIC_CABLE_FAIL = 150106;

    /**
     * 光缆参数错误
     */
    public static final Integer OPTIC_CABLE_PARAM_ERROR = 150107;

    /**
     * 光缆名称重复
     */
    public static final Integer OPTIC_CABLE_NAME_SAME = 150108;

    /**
     * 光缆不存在
     */
    public static final Integer OPTIC_CABLE_IS_NOT_EXIST = 150109;

    /**
     * 包含光缆段信息
     */
    public static final Integer OPTIC_CABLE_CONTAINS_OPTIC_CABLE_SECTION = 150110;

    /**
     * 光缆超过最大纤芯数
     */
    public static final Integer OPTIC_CABLE_EXCEED_THE_CORE_MAXIMUM = 150111;

    /**
     * 设施不存在
     */
    public static final Integer DEVICE_NODE_NOT_EXISTS = 150111;
    /**
     * 设施服务异常
     */
    public static final Integer DEVICE_SERVER_ERROR = 150112;
    /**
     * 设施存在
     */
    public static final Integer DEVICE_NODE_EXISTS = 150113;
    /**--------------------------光缆end--------------------------*/

    /**--------------------------光缆段start--------------------------*/
    /**
     * 保存光缆段成功
     */
    public static final Integer SAVE_OPTIC_CABLE_SECTION_SUCCESS = 150201;

    /**
     * 保存光缆段失败
     */
    public static final Integer SAVE_OPTIC_CABLE_SECTION_FAIL = 150202;

    /**
     * 光缆段参数错误
     */
    public static final Integer OPTIC_CABLE_SECTION_PARAM_ERROR = 150203;

    /**
     * 光缆段名称重复
     */
    public static final Integer OPTIC_CABLE_SECTION_NAME_SAME = 150204;

    /**
     * 光缆段的纤芯数不能大于光缆段的纤芯数
     */
    public static final Integer OPTIC_CABLE_SECTION_CORE_OVERRUN = 150205;

    /**
     * 更新光缆段成功
     */
    public static final Integer UPDATE_OPTIC_CABLE_SECTION_SUCCESS = 150206;

    /**
     * 更新光缆段失败
     */
    public static final Integer UPDATE_OPTIC_CABLE_SECTION_FAIL = 150207;

    /**
     * 删除光缆段成功
     */
    public static final Integer DELETE_OPTIC_CABLE_SECTION_SUCCESS = 150208;

    /**
     * 删除光缆段失败
     */
    public static final Integer DELETE_OPTIC_CABLE_SECTION_FAIL = 150209;

    /**
     * 光缆段包含业务信息
     */
    public static final Integer OPTIC_CABLE_SECTION_HAVE_SERVER = 150210;

    /**
     * 光缆段不存在
     */
    public static final Integer OPTIC_CABLE_SECTION_NOT_EXIST = 150211;

    /**--------------------------光缆段end--------------------------*/

    /**--------------------------光缆段gisStart--------------------------*/
    /**
     * 修改光缆段gis标签信息成功
     */
    public static final Integer UPDATE_OPTIC_CABLE_SECTION_RFID_SUCCESS = 1508101;

    /**
     * 修改光缆段gis标签信息失败
     */
    public static final Integer UPDATE_OPTIC_CABLE_SECTION_RFID_FAIL = 1508102;

    /**
     * 光缆段gis标签信息不存在
     */
    public static final Integer OPTIC_CABLE_SECTION_RFID_NOT_EXIST = 1508103;
    /**--------------------------光缆段gisEnd--------------------------*/

    /**--------------------------智能标签start--------------------------*/
    /**
     * 保存智能标签信息成功
     */
    public static final Integer SAVE_RFID_SUCCESS = 1507101;

    /**
     * 保存智能标签信息失败
     */
    public static final Integer SAVE_RFID_FAIL = 1507102;


    /**
     * 修改智能标签信息成功
     */
    public static final Integer UPDATE_RFID_SUCCESS = 1507103;

    /**
     * 修改智能标签信息失败
     */
    public static final Integer UPDATE_RFID_FAIL = 1507104;


    /**
     * 删除智能标签信息成功
     */
    public static final Integer DELETE_RFID_SUCCESS = 1507105;

    /**
     * 删除智能标签信息失败
     */
    public static final Integer DELETE_RFID_FAIL = 1507106;

    /**
     * 标签已存在
     */
    public static final Integer LABEL_IS_EXISTED = 1507107;
    /**
     * 数据上传类型异常
     */
    public static final Integer UPDATE_TYPE_IS_WRONG = 1507108;
    /**
     * 目标已经绑定标签
     */
    public static final Integer THE_TARGET_IS_ALREADY_BOUND_TO_THE_LABEL = 1507109;
    /**
     * 标签无信息
     */
    public static final Integer CAN_NOT_FIND_INFO_WITH_THIS_LABEL = 1507110;

    /**--------------------------智能标签end--------------------------*/

    /**--------------------------common--------------------------*/
    /**
     * 获取用户信息异常
     */
    public static final Integer USER_SERVER_ERROR = 1500101;

    /**
     * 导出没有数据
     */
    public static final Integer EXPORT_NO_DATA = 1500102;

    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 1500103;

    /**
     * 创建导出任务失败
     */
    public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 1500104;

    /**
     * 该用户没有智能标签业务权限
     */
    public static final Integer NOT_HAVE_PERMISSION_FOR_RFID = 1500105;

    /**
     * 智能标签id已存在
     */
    public static final Integer RFID_CODE_IS_EXISTS = 1500106;
    /**--------------------------common--------------------------*/
}
