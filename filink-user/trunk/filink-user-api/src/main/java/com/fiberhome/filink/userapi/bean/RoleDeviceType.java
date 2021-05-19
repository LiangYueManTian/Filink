package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 角色和设施对应关系
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-04-03
 */
@Data
public class RoleDeviceType implements Serializable {


    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 设施类型id
     */
    private String deviceTypeId;

    /**
     * 上级类型id
     */
    private String parentId;

    /**
     * 设施类型名称
     */
    private String name;

    private List<RoleDeviceType> childDeviceTypeList;

    /**
     * 创建时间
     */
    private Long createTime;


    @Override
    public String toString() {
        return "RoleDevicetype{"
                + "id=" + id
                + ", roldId=" + roleId
                + ", deviceTypeId=" + deviceTypeId
                + ", createTime=" + createTime
                + "}";
    }
}
