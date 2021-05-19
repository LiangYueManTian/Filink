package com.fiberhome.filink.lockserver.constant;

import com.fiberhome.filink.lockserver.exception.FiLinkAccessDenyException;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 电子锁数据权限参数
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/25
 */
@Data
public class AuthParam {
    /**
     * 光交箱电子锁编号
     */
    public static final String ONU_EL = "001-1";

    /**
     * 光交箱类型编号
     */
    public static final String ONU = "001";

    /**
     * 人井电子锁编号
     */
    public static final String PW_EL = "030-1";

    /**
     * 人井类型编号
     */
    public static final String PW = "030";

    /**
     * 室外柜类型编号
     */
    public static final String OUT_CAB = "210";

    /**
     * 室外柜电子锁编号
     */
    public static final String OUT_CAB_LOCK = "210-1";

    /**
     * 检验是否具有数据权限
     *
     * @param deviceType     设施类型
     * @param deviceTypeList 设施类型集合
     */
    public static void checkLockPermission(String deviceType, List<String> deviceTypeList) {
        if (!deviceTypeList.contains(deviceType)) {
            //没有访问该设施类型访问权限
            throw new FiLinkAccessDenyException();
        }
        //没有访问电子锁的数据权限
        if (deviceType.equals(ONU)) {
            if (!deviceTypeList.contains(ONU_EL)) {
                //没有访问光交箱下电子锁的数据权限
                throw new FiLinkAccessDenyException();
            }
        } else if (deviceType.equals(PW)) {
            if (!deviceTypeList.contains(PW_EL)) {
                //没有访问人井下电子锁的数据权限
                throw new FiLinkAccessDenyException();
            }
        } else if (OUT_CAB.equals(deviceType)) {
            if (!deviceTypeList.contains(OUT_CAB_LOCK)) {
                //没有室外柜电子锁的数据权限
                throw new FiLinkAccessDenyException();
            }
        }
    }


}
