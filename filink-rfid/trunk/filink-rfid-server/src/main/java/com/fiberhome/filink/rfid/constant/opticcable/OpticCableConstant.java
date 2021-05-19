package com.fiberhome.filink.rfid.constant.opticcable;

/**
 * <p>
 * 光缆 常量类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019/5/20
 */
public class OpticCableConstant {
    /**
     * 删除标记
     */
    public static final String IS_DELETED = "1";

    /**
     * 未删除标记
     */
    public static final String NOT_DELETED = "0";

    /**------------------------------------------光缆级别------------------------------------------ */
    /**本地接入-主干光缆 */
    public static final String OPTIC_CABLE_LEVEL_TRUNK = "0";
    /**本地接入-末端光缆 */
    public static final String OPTIC_CABLE_LEVEL_END = "1";
    /**一级干线 */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_ONE = "2";
    /**二级干线*/
    public static final String OPTIC_CABLE_LEVEL_LEVEL_LEVEL_TWO = "3";
    /**本地中继 */
    public static final String OPTIC_CABLE_LEVEL_LEVEL_REPEATER = "4";
    /**本地核心*/
    public static final String OPTIC_CABLE_LEVEL_LEVEL_KERNEL = "5";
    /**本地汇聚*/
    public static final String OPTIC_CABLE_LEVEL_LEVEL_CONVERGE = "6";
    /**汇接层光缆*/
    public static final String OPTIC_CABLE_LEVEL_LEVEL_JUNCTION = "7";
    /**联络光缆*/
    public static final String OPTIC_CABLE_LEVEL_LEVEL_CONTACT = "8";
    /**局内光缆*/
    public static final String OPTIC_CABLE_LEVEL_LEVEL_INSIDE = "9";
    /**------------------------------------------光缆级别------------------------------------------ */

    /**------------------------------------------布线类型------------------------------------------ */
    /**递减 */
    public static final String WIRING_TYPE_DIMINISHING = "0";
    /**不递减 */
    public static final String WIRING_TYPE_NOT_DIMINISH = "1";
    /**------------------------------------------布线类型------------------------------------------ */

    /**------------------------------------------拓扑结构------------------------------------------ */
    /**环形 */
    public static final String TOPOLOGY_CIRCULARITY = "0";
    /**非环形 */
    public static final String TOPOLOGY_NON_CIRCULARITY = "1";
    /**------------------------------------------拓扑结构------------------------------------------ */

}
