package com.fiberhome.filink.about.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;

import java.io.Serializable;

/**
 * <p>
 * 关于  实体类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-06
 */
public class About extends Model<About> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 uuid
     */
    @TableField("about_id")
    private String aboutId;

    /**
     * 系统版本信息
     */
    private String version;

    /**
     * 版权信息
     */
    private String copyright;

    /**
     * License授权情况
     * 0 未授权  1 已授权
     */
    @TableField("license_authorize")
    private String licenseAuthorize;

    /**
     * 公司情况
     */
    @TableField("company_info")
    private String companyInfo;

    /**
     * 安卓APP下载地址
     */
    @TableField("android_address")
    private String androidAddress;

    /**
     * IOSAPP下载地址
     */
    @TableField("ios_address")
    private String iosAddress;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private String updateTime;


    @Override
    protected Serializable pkVal() {
        return this.aboutId;
    }

    public String getAboutId() {
        return aboutId;
    }

    public void setAboutId(String aboutId) {
        this.aboutId = aboutId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getLicenseAuthorize() {
        return licenseAuthorize;
    }

    public void setLicenseAuthorize(String licenseAuthorize) {
        this.licenseAuthorize = licenseAuthorize;
    }

    public String getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(String companyInfo) {
        this.companyInfo = companyInfo;
    }

    public String getAndroidAddress() {
        return androidAddress;
    }

    public void setAndroidAddress(String androidAddress) {
        this.androidAddress = androidAddress;
    }

    public String getIosAddress() {
        return iosAddress;
    }

    public void setIosAddress(String iosAddress) {
        this.iosAddress = iosAddress;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
