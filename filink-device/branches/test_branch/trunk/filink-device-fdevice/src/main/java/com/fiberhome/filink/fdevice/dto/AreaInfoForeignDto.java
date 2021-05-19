package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *对外区域实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019/3/14
 */
@Data
public class AreaInfoForeignDto {
    /**
     * 区域id(UUID)
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 所属区域id
     */
    private String parentAreaId;

    /**
     * 区域级别
     */
    private Integer areaLevel;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 子区域
     */
    private List<AreaInfoForeignDto> children = new ArrayList<>();
    /**
     * 是否有权限
     */
    private Boolean hasPermissions;
    /**
     * 设置用户权限
     */
    public void setAreaPermissions(List<String> permissionsId) {
        this.hasPermissions = permissionsId.contains(areaId);
    }
}
