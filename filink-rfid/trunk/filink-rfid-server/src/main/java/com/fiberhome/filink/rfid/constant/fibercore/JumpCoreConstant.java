package com.fiberhome.filink.rfid.constant.fibercore;

/**
 * <p>
 * 跳接 常量类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019/6/11
 */
public class JumpCoreConstant {
    /**
     * 柜内跳
     */
    public static final String IS_INNER_DEVICE = "0";

    /**
     * 柜间跳
     */
    public static final String NOT_INNER_DEVICE = "1";

    /**----------------------------适配器类型----------------------------*/
    /**适配器类型(FC)*/
    public static final String ADAPTER_TYPE_FC = "FC";
    /**适配器类型(SC)*/
    public static final String ADAPTER_TYPE_SC = "SC";
    /**----------------------------适配器类型----------------------------*/

    /**----------------------------分路器----------------------------*/
    /**否*/
    public static final String BRANCHING_UNIT_0 = "0";
    /**是*/
    public static final String BRANCHING_UNIT_1 = "1";
    /**----------------------------分路器----------------------------*/

    /**----------------------------返回类型----------------------------*/
    /**正常*/
    public static final String RESULT_TYPE_NORMAL = "0";
    /**设施id是否存在*/
    public static final String RESULT_TYPE_QUERY_REALPOSITION_DEVICE_ID_ERROR = "1";
    /**当前跳接数据是否已被跳接*/
    public static final String RESULT_TYPE_PORT_HAVE_JUMP_CORE = "2";
    /**智能标签code已存在*/
    public static final String RESULT_TYPE_RFID_CODE_IS_EXISTS = "3";
    /**----------------------------返回类型----------------------------*/
}
