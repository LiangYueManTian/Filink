package com.fiberhome.filink.rfid.constant.opticcable;

import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 * 光缆段 常量类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019/6/17
 */
public class OpticCableSectionConstant extends ResultCode {

    /**------------------------------------------状态------------------------------------------ */
    /**使用 */
    public static final String STATUS_USE = "0";
    /**未使用 */
    public static final String STATUS_UNUSED = "1";
    /**------------------------------------------状态------------------------------------------ */

    /**------------------------------------------设施节点状态------------------------------------------ */
    /**设施不存在 */
    public static final String DEVICE_NODE_NOT_EXISTS = "0";
    /**设施服务异常 */
    public static final String DEVICE_SERVER_ERROR = "-1";
    /**设施存在 */
    public static final String DEVICE_NODE_EXISTS = "1";
    /**------------------------------------------设施节点状态------------------------------------------ */

}
