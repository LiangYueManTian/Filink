package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 区域查询实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019/3/14
 */
@Data
public class AreaInfoDto {
    /**
     * 区域名称
     */
    private String areaName;
    /**
     * 所属区域
     */
    private String parentName;
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
     * 备注
     */
    private String remarks;
    /**
     * 责任单位
     */
    private Set<String> accountabilityUnit;
    /**
     * 级别，默认为1
     */
    private Integer level;
    /**
     * 排序字段
     */
    private String sortField;
    /**
     * 排序规则
     */
    private String sortRule;
    /**
     * id集合
     */
    private List<String> areaIds;
    /**
     * 分页条数
     */
    private Integer pageSize;
    /**
     * 第几页
     */
    private Integer beginNum;

    public void setAreaName(String areaName) {
        this.areaName = alterLikeValue(areaName);
    }

    public void setParentName(String parentName) {
        this.parentName = alterLikeValue(parentName);
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = alterLikeValue(provinceName);
    }

    public void setCityName(String cityName) {
        this.cityName = alterLikeValue(cityName);
    }

    public void setDistrictName(String districtName) {
        this.districtName = alterLikeValue(districtName);
    }

    public void setAddress(String address) {
        this.address = alterLikeValue(address);
    }

    public void setRemarks(String remarks) {
        this.remarks = alterLikeValue(remarks);
    }

    public void setAccountabilityUnit(Set<String> accountabilityUnit) {
        this.accountabilityUnit = accountabilityUnit;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public void setSortRule(String sortRule) {
        this.sortRule = sortRule;
    }

    public void setAreaIds(List<String> areaIds) {
        this.areaIds = areaIds;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setBeginNum(Integer beginNum) {
        this.beginNum = beginNum;
    }

    /**
     * 替换特殊字符
     *
     * @param value
     * @return
     */
    private String alterLikeValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.replace("\\", "\\\\");
        value = value.replace("%", "\\%");
        value = value.replace("_", "\\_");
        value = value.replace("'", "\\'");
        return value;
    }
}
