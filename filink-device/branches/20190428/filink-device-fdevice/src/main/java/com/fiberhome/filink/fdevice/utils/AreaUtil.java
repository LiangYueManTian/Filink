package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * area工具类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class AreaUtil {
    /**
     * 无权限直接集合删除方法
     *
     * @param areaInfoForeignDtoList 传入数据集合
     * @param permissions            权限id
     */
    public static void permissionsFilter(List<AreaInfoForeignDto> areaInfoForeignDtoList, List<String> permissions) {
        Iterator<AreaInfoForeignDto> iterator = areaInfoForeignDtoList.iterator();
        while (iterator.hasNext()) {
            if (!permissions.contains(iterator.next().getAreaId())) {
                iterator.remove();
            }
        }
    }

    /**
     * area树结构权限过滤
     *
     * @param areaInfoList 传入树结构数据集合
     * @param permissions  权限id集合
     */
    public static void areaTreePermissionsFilter(List<AreaInfo> areaInfoList, List<String> permissions) {
        //权限过滤
        List<String> currentUserPermissionsId = permissions;
        areaInfoList.forEach(areaInfo -> {
            areaInfo.setAreaPermissions(currentUserPermissionsId);
        });
    }

    /**
     * 对外树结构权限过滤
     *
     * @param areaInfoForeignDtoList 传入数据集合
     * @param permissions            权限id
     */
    public static void areaForeignTreePermissionsFilter(List<AreaInfoForeignDto> areaInfoForeignDtoList, List<String> permissions) {
        //权限过滤
        List<String> currentUserPermissionsId = permissions;
        areaInfoForeignDtoList.forEach(areaInfoForeignDto -> {
            areaInfoForeignDto.setAreaPermissions(currentUserPermissionsId);
        });
    }
}
