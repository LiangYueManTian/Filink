package com.fiberhome.filink.userserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-04-03
 */
@Data
@TableName("role_devicetype")
public class RoleDeviceType extends Model<RoleDeviceType> {

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
    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private List<RoleDeviceType> childDeviceTypeList;

    /**
     * 创建时间
     */
    private Long createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

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
