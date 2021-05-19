package com.fiberhome.filink.userapi.bean;

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
public class Permission implements Serializable {


    private String id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 允许访问的url
     */
    private String interfaceUrl;

    private String description;

    private String menuId;

    /**
     * 0:有效  1：被删除
     */
    private Integer isDeleted;

    private Date createTime;

    private Date updateTime;

    /**
     * 前端路由地址
     */
    private String route_url;

    private String parentId;

    /**
     * 当前权限的子权限列表
     */
    private List<Permission> childPermissonList;

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
