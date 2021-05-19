package com.fiberhome.filink.userserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 权限相关的信息
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
@Data
public class Permission extends Model<Permission> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 允许访问的url
     */
    @TableField("interface_url")
    private String interfaceUrl;

    private String description;

    @TableField("menu_id")
    private String menuId;

    /**
     * 0:有效  1：被删除
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    /**
     * 前端路由地址
     */
    private String route_url;

    private String parentId;

    /**
     * 权限类型，1：pc权限  2：app权限
     */
    private Integer type;

    /**
     * 当前权限的子权限列表
     */
    @TableField(exist = false)
    private List<Permission> childPermissonList;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Permission{" +
        "id=" + id +
        ", name=" + name +
        ", interfaceUrl=" + interfaceUrl +
        ", description=" + description +
        ", menuId=" + menuId +
        "}";
    }
}
