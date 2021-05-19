package com.fiberhome.filink.userserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.RoleDeviceType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-04-03
 */
public interface RoleDeviceTypeDao extends BaseMapper<RoleDeviceType> {

    /**
     * 批量添加角色和设施类型的对应关系
     * @param roleDevicetypeList    角色设施对应实体
     * @return  添加的数量
     */
    Integer batchAddRoleDeviceType(List<RoleDeviceType> roleDevicetypeList);

    /**
     * 批量删除角色和设施类型的对应关系
     * @param roleId    角色id
     * @return  删除的数量
     */
    Integer batchDeleteByRoleId(@Param("roleId") String roleId);
}
