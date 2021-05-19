package com.fiberhome.filink.fdevice.bean.area;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 区域信息
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@TableName("area_info")
public class AreaInfo extends Model<AreaInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 区域id(UUID)
     */
    @TableId("area_id")
    private String areaId;

    /**
     * 区域级别
     */
    private Integer level;

    /**
     * 区域名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 省
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 市
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 区
     */
    @TableField("district_name")
    private String districtName;

    /**
     * 详细地址
     */
    private String address;
    /**
     * 责任单位id
     */
    @TableField(exist = false)
    private Set<String> accountabilityUnit;
    /**
     * 责任单位名称
     */
    @TableField(exist = false)
    private StringBuilder accountabilityUnitName;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 所属区域id
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 是否删除 默认为0
     */
    @TableField("is_deleted")
    private String isDeleted = "0";

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 子类到父类的名称
     */
    @TableField(exist = false)
    private String areaAndPrentName;
    /**
     * 是否有子区域
     */
    @TableField(exist = false)
    private boolean hasChild;
    /**
     * 父类名称
     */
    @TableField(exist = false)
    private String parentName;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<String> getAccountabilityUnit() {
        return accountabilityUnit;
    }

    public void setAccountabilityUnit(Set<String> accountabilityUnit) {
        this.accountabilityUnit = accountabilityUnit;
    }

    public StringBuilder getAccountabilityUnitName() {
        return accountabilityUnitName;
    }

    public void setAccountabilityUnitName(StringBuilder accountabilityUnitName) {
        this.accountabilityUnitName = accountabilityUnitName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getAreaAndPrentName() {
        return areaAndPrentName;
    }

    public void setAreaAndPrentName(String areaAndPrentName) {
        this.areaAndPrentName = areaAndPrentName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    @Override
    protected Serializable pkVal() {
        return this.getAreaId();
    }

    /**
     * 名称校验
     *
     * @return 校验结果
     */
    public boolean checkAreaName() {
        String areaNameRegex = "^(?!_)[\\w\\s_\\u4e00-\\u9fa5]{1,128}$";
        return this.areaName.matches(areaNameRegex);
    }

    /**
     * 参数校验
     *
     * @return 校验结果
     */
    public boolean checkParameterFormat() {
        String remarkRegex = "^.{0,200}$";
        String addressRegex = "^.{0,64}$";
        if (!StringUtils.isEmpty(this.remarks) && !this.remarks.matches(remarkRegex)) {
            return false;
        }
        if (!StringUtils.isEmpty(this.address) && !this.address.matches(addressRegex)) {
            return false;
        }
        return true;
    }

    /**
     * 参数格式化 去空格
     */
    public void parameterFormat() {
        if (this.areaName != null) {
            this.areaName = areaName.replaceAll("(.*?)\\s+$", "$1");
        }
        if (this.address != null) {
            this.address = address.replaceAll("(.*?)\\s+$", "$1");
        }
        if (this.remarks != null) {
            this.remarks = remarks.replaceAll("(.*?)\\s+$", "$1");
        }
    }
}
