package com.fiberhome.filink.userserver.utils;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.Permission;
import com.fiberhome.filink.userserver.bean.RoleDeviceType;
import com.fiberhome.filink.userserver.constant.UserI18n;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xgong
 */
public class RoleUtils {

    /**
     * 光交箱编号
     */
    private static final String GJX = "001";
    /**
     * 光交箱名称
     */
    private static final String G_J_X_NAME = I18nUtils.getSystemString(UserI18n.G_J_X_NAME);
    /**
     * 光交箱下电子锁编号
     */
    private static final String DZS = "001-1";
    /**
     * 光交箱电子锁名称
     */
    private static final String D_Z_S_NAME = I18nUtils.getSystemString(UserI18n.D_Z_S_NAME);
    /**
     * 光交箱下智能标签编号
     */
    private static final String Z_N_B_Q = "001-2";
    /**
     * 光交箱下智能标签名称
     */
    private static final String Z_N_B_Q_NAME = I18nUtils.getSystemString(UserI18n.Z_N_B_Q_NAME);
    /**
     * 人井编号
     */
    private static final String RJ = "030";
    /**
     * 人井名称
     */
    private static final String R_J_NAME = I18nUtils.getSystemString(UserI18n.R_J_NAME);
    /**
     * 人井下电子锁编号
     */
    private static final String R_J_D_Z_S = "030-1";
    /**
     * 人井下电子锁名称
     */
    private static final String R_J_D_Z_S_NAME = I18nUtils.getSystemString(UserI18n.R_J_D_Z_S_NAME);
    /**
     * 配线架编号
     */
    private static final String PXJ = "060";
    /**
     * 配线架名称
     */
    private static final String P_X_J_NAME = I18nUtils.getSystemString(UserI18n.P_X_J_NAME);
    /**
     * 配线架下智能标签编号
     */
    private static final String P_X_J_Z_N_B_Q = "060-2";
    /**
     * 配线架下智能标签名称
     */
    private static final String P_X_J_Z_N_B_Q_NAME = I18nUtils.getSystemString(UserI18n.P_X_J_Z_N_B_Q_NAME);
    /**
     * 接头盒编号
     */
    private static final String JTH = "090";
    /**
     * 接头盒名称
     */
    private static final String J_T_H_NAME = I18nUtils.getSystemString(UserI18n.J_T_H_NAME);
    /**
     * 接头盒下智能标签编号
     */
    private static final String J_T_H_Z_N_B_Q = "090-2";
    /**
     * 接头盒下智能标签名称
     */
    private static final String J_T_H_Z_N_B_Q_NAME = I18nUtils.getSystemString(UserI18n.J_T_H_Z_N_B_Q_NAME);
    /**
     * 室外柜编号
     */
    private static final String SWG = "210";
    /**
     * 室外柜名称
     */
    private static final String S_W_G_NAME = I18nUtils.getSystemString(UserI18n.S_W_G_NAME);
    /**
     * 室外柜下电子锁编号
     */
    private static final String S_W_G_D_Z_S = "210-1";
    /**
     * 室外柜下电子锁名称
     */
    private static final String S_W_G_D_Z_S_NAME = I18nUtils.getSystemString(UserI18n.S_W_G_D_Z_S_NAME);


    private static final String PERMISSION_ID_SPLIT = "-";

    /**
     * String
     * 将角色修改为树结构
     *
     * @param permissionList
     */
    public static List<Permission> getTreeRole(List<Permission> permissionList) {


        Map<String, Permission> permissionMap = new HashMap<>();
        permissionList.forEach(permission -> {
            permissionMap.put(permission.getId(), permission);
        });

        Set<String> keyS = permissionMap.keySet();
        keyS.forEach(key -> {
            if (permissionMap.get(key).getParentId() != null) {
                Permission parentPermission = permissionMap.get(permissionMap.get(key).getParentId());
                List<Permission> childrenPermission = new ArrayList<>();
                if (parentPermission != null) {
                    childrenPermission = parentPermission.getChildPermissionList();
                    if (childrenPermission == null) {
                        childrenPermission = new ArrayList<>();
                    }
                    childrenPermission.add(permissionMap.get(key));
                    sortPermissionList(childrenPermission, 0, childrenPermission.size() - 1);
                    parentPermission.setChildPermissionList(childrenPermission);
                    permissionMap.put(permissionMap.get(key).getParentId(), parentPermission);
                }
            }
        });

        Collection<Permission> values = permissionMap.values();
        permissionList = new ArrayList<>(values);
        //筛选出级别为1的菜单
        return permissionList.stream().filter(permission -> permission.getParentId() == null).collect(Collectors.toList());
    }

    /**
     * 将设施类型修改为树结构
     *
     * @param roleDeviceTypeList 角色设施列表
     */
    public static List<RoleDeviceType> getTreeDeviceType(List<RoleDeviceType> roleDeviceTypeList) {


        Map<String, RoleDeviceType> deviceMap = new HashMap<>();
        roleDeviceTypeList.forEach(deviceType -> {
            String deviceTypeName = getDeviceTypeName(deviceType.getDeviceTypeId());
            deviceType.setName(deviceTypeName);
            deviceMap.put(deviceType.getRoleId() + deviceType.getDeviceTypeId(), deviceType);
        });

        //在数据库中，parentid中填写的是设施类型id，所以需要用角色id和设施类型id一起来做唯一标识
        Set<String> keyS = deviceMap.keySet();
        keyS.forEach(key -> {
            if (deviceMap.get(key).getParentId() != null) {
                RoleDeviceType parentRoleDeviceType = deviceMap.get(deviceMap.get(key).getRoleId() + deviceMap.get(key).getParentId());
                List<RoleDeviceType> childrenRoleDeviceType = parentRoleDeviceType.getChildDeviceTypeList();
                if (childrenRoleDeviceType == null) {
                    childrenRoleDeviceType = new ArrayList<>();
                }
                childrenRoleDeviceType.add(deviceMap.get(key));
                parentRoleDeviceType.setChildDeviceTypeList(childrenRoleDeviceType);
                deviceMap.put(deviceMap.get(key).getRoleId() + deviceMap.get(key).getParentId(), parentRoleDeviceType);
            }
        });

        Collection<RoleDeviceType> values = deviceMap.values();
        roleDeviceTypeList = new ArrayList<>(values);
        //筛选
        return roleDeviceTypeList.stream().filter(deviceType -> deviceType.getParentId() == null).collect(Collectors.toList());
    }

    public static void dealDeviceType(RoleDeviceType roleDeviceType, String deviceId) {

        String deviceTypeParent = getDeviceTypeParent(deviceId);
        String deviceTypeName = getDeviceTypeName(deviceId);
        if (StringUtils.isNotEmpty(deviceTypeParent)) {
            roleDeviceType.setParentId(deviceTypeParent);
        }
        if (StringUtils.isNotEmpty(deviceTypeName)) {
            roleDeviceType.setName(deviceTypeName);
        }

    }

    public static String getDeviceTypeParent(String deviceType) {

        if (DZS.equals(deviceType) || Z_N_B_Q.equals(deviceType)) {
            return GJX;
        }

        if (R_J_D_Z_S.equals(deviceType)) {
            return RJ;
        }

        if (P_X_J_Z_N_B_Q.equals(deviceType)) {
            return PXJ;
        }

        if (J_T_H_Z_N_B_Q.equals(deviceType)) {
            return JTH;
        }

        if (S_W_G_D_Z_S.equals(deviceType)) {
            return SWG;
        }

        return null;
    }

    /**
     * 根据设施类型获取设施名称
     *
     * @param deviceTypeId
     * @return
     */
    public static String getDeviceTypeName(String deviceTypeId) {

        switch (deviceTypeId) {
            case GJX:
                return G_J_X_NAME;
            case DZS:
                return D_Z_S_NAME;
            case Z_N_B_Q:
                return Z_N_B_Q_NAME;
            case RJ:
                return R_J_NAME;
            case R_J_D_Z_S:
                return R_J_D_Z_S_NAME;
            case PXJ:
                return P_X_J_NAME;
            case P_X_J_Z_N_B_Q:
                return P_X_J_Z_N_B_Q_NAME;
            case JTH:
                return J_T_H_NAME;
            case J_T_H_Z_N_B_Q:
                return J_T_H_Z_N_B_Q_NAME;
            case SWG:
                return S_W_G_NAME;
            case S_W_G_D_Z_S:
                return S_W_G_D_Z_S_NAME;
            default:
                break;
        }
        return null;
    }

    /**
     * 对权限列表进行排序,快速排序
     *
     * @param permissionList 权限列表
     * @return
     */
    private static void sortPermissionList(List<Permission> permissionList, int start, int end) {
        if (start >= end) {
            return;
        }
        int i = start;
        int j = end;
        String baseId = permissionList.get(start).getId();
        int base = Integer.parseInt(baseId.substring(baseId.lastIndexOf(PERMISSION_ID_SPLIT) + 1, baseId.length()));
        Permission baseMenu = permissionList.get(start);
        while (i != j) {
            String baseJ = permissionList.get(j).getId();
            while (Integer.parseInt(baseJ.substring(baseJ.lastIndexOf(PERMISSION_ID_SPLIT) + 1, baseJ.length())) >= base && j > i) {
                j--;
                baseJ = permissionList.get(j).getId();
            }

            String baseI = permissionList.get(i).getId();
            while (Integer.parseInt(baseI.substring(baseI.lastIndexOf(PERMISSION_ID_SPLIT) + 1, baseI.length())) <= base && i < j) {
                i++;
                baseI = permissionList.get(i).getId();
            }

            if (i < j) {
                Permission temp = permissionList.get(i);
                permissionList.set(i, permissionList.get(j));
                permissionList.set(j, temp);
            }
        }
        permissionList.set(start, permissionList.get(i));
        permissionList.set(i, baseMenu);

        sortPermissionList(permissionList, start, i - 1);
        sortPermissionList(permissionList, i + 1, end);
    }

}
