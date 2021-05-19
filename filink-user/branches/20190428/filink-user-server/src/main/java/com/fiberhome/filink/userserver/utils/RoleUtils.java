package com.fiberhome.filink.userserver.utils;

import com.fiberhome.filink.userserver.bean.Permission;
import com.fiberhome.filink.userserver.bean.RoleDeviceType;
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

    public static final String GJX = "001";
    public static final String GJXNAME = "光交箱";
    public static final String DZS = "001-1";
    public static final String DZSNAME = "电子锁";
    public static final String ZNBQ = "001-2";
    public static final String ZNBQNAME = "智能标签";
    public static final String RJ = "030";
    public static final String RJNAME = "人井";
    public static final String RJDZS = "030-1";
    public static final String RJDZSNAME = "电子锁";
    public static final String PXJ = "060";
    public static final String PXJNAME = "配线架";
    public static final String PXJZNBQ = "060-1";
    public static final String PXJZNBQNAME = "智能标签";
    public static final String JTH = "090";
    public static final String JTHNAME = "接头盒";
    public static final String JTHZNBQ = "090-1";
    public static final String JTHZNBQNAME = "智能标签";
    public static final String FQX = "150";
    public static final String FQXNAME = "分纤箱";
    public static final String FQXZNBQ = "150-1";
    public static final String FQXZNBQNAME = "智能标签";


    private static final String PERMISSION_ID_SPLIE = "-";

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
                if(parentPermission != null) {
                    childrenPermission = parentPermission.getChildPermissonList();
                    if (childrenPermission == null) {
                        childrenPermission = new ArrayList<>();
                    }
                    childrenPermission.add(permissionMap.get(key));
                    sortPermissionList(childrenPermission, 0, childrenPermission.size() - 1);
                    parentPermission.setChildPermissonList(childrenPermission);
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
                List<RoleDeviceType> childrenRoleDeviceType = parentRoleDeviceType.getChildDevieTypeList();
                if (childrenRoleDeviceType == null) {
                    childrenRoleDeviceType = new ArrayList<>();
                }
                childrenRoleDeviceType.add(deviceMap.get(key));
                parentRoleDeviceType.setChildDevieTypeList(childrenRoleDeviceType);
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

        if (DZS.equals(deviceType) || ZNBQ.equals(deviceType)) {
            return GJX;
        }

        if (RJDZS.equals(deviceType)) {
            return RJ;
        }

        if (PXJZNBQ.equals(deviceType)) {
            return PXJ;
        }

        if (JTHZNBQ.equals(deviceType)) {
            return JTH;
        }

        if (FQXZNBQ.equals(deviceType)) {
            return FQX;
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
                return GJXNAME;
            case DZS:
                return DZSNAME;
            case ZNBQ:
                return ZNBQNAME;
            case RJ:
                return RJNAME;
            case RJDZS:
                return RJDZSNAME;
            case PXJ:
                return PXJNAME;
            case PXJZNBQ:
                return PXJZNBQNAME;
            case JTH:
                return JTHNAME;
            case JTHZNBQ:
                return JTHZNBQNAME;
            case FQX:
                return FQXNAME;
            case FQXZNBQ:
                return FQXZNBQNAME;
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
        int base = Integer.parseInt(baseId.substring(baseId.lastIndexOf(PERMISSION_ID_SPLIE) + 1, baseId.length()));
        Permission baseMenu = permissionList.get(start);
        while (i != j) {
            String baseJ = permissionList.get(j).getId();
            while (Integer.parseInt(baseJ.substring(baseJ.lastIndexOf(PERMISSION_ID_SPLIE) + 1, baseJ.length())) >= base && j > i) {
                j--;
                baseJ = permissionList.get(j).getId();
            }

            String baseI = permissionList.get(i).getId();
            while (Integer.parseInt(baseI.substring(baseI.lastIndexOf(PERMISSION_ID_SPLIE) + 1, baseI.length())) <= base && i < j) {
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
