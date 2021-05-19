package com.fiberhome.filink.userserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
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
public class Role extends Model<Role> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private String id;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("role_desc")
    private String roleDesc;

    /**
     * 是否被删除,1没有，0已删除
     */
    @TableField("is_deleted")
    private String deleted;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 是否为默认角色
     */
    @TableField("default_role")
    private int defaultRole;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 角色能够管理的权限id
     */
    @TableField(exist = false)
    private List<String> permissionIds;

    /**
     * 角色能够管理的设施类型id
     */
    @TableField(exist = false)
    private List<String> deviceTypeIds;

    /**
     * 角色设施中间实体类
     */
    @TableField(exist = false)
    private List<RoleDeviceType> roleDevicetypeList;

    /**
     * 角色管理的权限列表
     */
    @TableField(exist = false)
    private List<Permission> permissionList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

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
