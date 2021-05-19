package com.fiberhome.filink.about.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *   关于  实体类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-06
 */
@Data
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
}
