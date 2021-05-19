package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * <p>
 * 区域信息类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@Data
public class AreaInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 区域id(UUID)
     */
    private String areaId;

    /**
     * 区域级别
     */
    private Integer level;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 省
     */
    private String provinceName;

    /**
     * 市
     */
    private String cityName;

    /**
     * 区
     */
    private String districtName;

    /**
     * 详细地址
     */
    private String address;
    /**
     *关联设施id
     * @return
     */

    /**
     * 责任单位id
     */
    private Set<String> accountabilityUnit;
    /**
     * 责任单位名称
     */
    private StringBuilder accountabilityUnitName;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 所属区域id
     */
    private String parentId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 子类到父类的名称
     */
    private String areaAndPrentName;
    /**
     * 父类名称
     */
    private String parentName;


}
