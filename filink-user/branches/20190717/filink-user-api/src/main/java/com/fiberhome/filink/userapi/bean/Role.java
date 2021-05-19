package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private String id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 是否被删除,1没有，0已删除
     */
    private String deleted;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 是否为默认角色
     */
    private int defaultRole;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 角色能够管理的权限id
     */
    private List<String> permissionIds;

    /**
     * 角色能够管理的设施类型id
     */
    private List<String> deviceTypeIds;

    /**
     * 角色设施中间实体类
     */
    private List<RoleDeviceType> roleDevicetypeList;

    /**
     * 角色管理的权限列表
     */
    private List<Permission> permissionList;


    @Override
    public String toString() {
        return "Role{"
                + "id='" + id + '\''
                + ", roleName='" + roleName + '\''
                + ", roleDesc='" + roleDesc + '\''
                + ", deleted='" + deleted + '\''
                + ", remark='" + remark + '\''
                + ", createUser='" + createUser + '\''
                + ", createTime=" + createTime
                + ", updateUser='" + updateUser + '\''
                + ", updateTime=" + updateTime
                + '}';
    }
}
