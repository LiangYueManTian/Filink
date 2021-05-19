package com.fiberhome.filink.license.bean;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 数据库license类
 * </p>
 *
 * @author gzp
 * @since 2019-02-19
 */
@TableName("license_info")
public class LicenseInfo extends Model<LicenseInfo> {

    private static final long serialVersionUID = 1L;

    @TableId("license_id")
    private String licenseId;

    private String path;

    @TableField("is_default")
    private String isDefault;

    @TableField("create_time")
    private Date createTime;

    @TableField("create_user")
    private String createUser;

    @TableField("update_time")
    private Date updateTime;

    @TableField("update_user")
    private String updateUser;

    public LicenseInfo() {
    }

    public LicenseInfo(String licenseId, String path, String isDefault, Date createTime, String createUser,
            Date updateTime, String updateUser) {
        this.licenseId = licenseId;
        this.path = path;
        this.isDefault = isDefault;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    @Override
    protected Serializable pkVal() {
        return this.licenseId;
    }

    @Override
    public String toString() {
        return "LicenseInfo{" +
        "licenseId=" + licenseId +
        ", path=" + path +
        ", isDefault=" + isDefault +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        ", updateTime=" + updateTime +
        ", updateUser=" + updateUser +
        "}";
    }
}
