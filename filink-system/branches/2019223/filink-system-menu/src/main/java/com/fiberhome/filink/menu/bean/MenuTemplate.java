package com.fiberhome.filink.menu.bean;


import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * menu_template实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@TableName("menu_template")
public class MenuTemplate extends Model<MenuTemplate> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单明细id(UUID)
     */
    @TableId("menu_template_id")
    private String menuTemplateId;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 模板的状态（状态为启用/禁用）
     */
    @TableField("template_status")
    private String templateStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 是否删除  0 未删除 1 删除
     */
    @TableField("is_deleted")
    private String isDeleted = "0";

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 创建时间时间戳
     */
    @TableField(exist = false)
    private Long createTimeTimestamp;
    /**
     * 更新时间时间戳
     */
    @TableField(exist = false)
    private Long updateTimeTimestamp;

    @Override
    protected Serializable pkVal() {
        return this.menuTemplateId;
    }

    public String getMenuTemplateId() {
        return menuTemplateId;
    }

    public void setMenuTemplateId(String menuTemplateId) {
        this.menuTemplateId = menuTemplateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateStatus() {
        return templateStatus;
    }

    public void setTemplateStatus(String templateStatus) {
        this.templateStatus = templateStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateTimeTimestamp() {
        if (StringUtils.isEmpty(createTime)) {
            return null;
        }
        return createTime.getTime();
    }

    public Long getUpdateTimeTimestamp() {
        if (StringUtils.isEmpty(updateTime)) {
            return null;
        }
        return updateTime.getTime();
    }

    /**
     * 检测必填参数
     *
     * @return
     */
    public boolean checkParameter() {
        boolean empty = StringUtils.isEmpty(this.version);
        boolean empty1 = StringUtils.isEmpty(this.menuTemplateId);
        if (empty || empty1) {
            return true;
        }
        return false;
    }

    /**
     * 检测参数格式
     *
     * @return 检测结果
     */
    public boolean checkParameterFormat() {
        String menuTemplateNameRegex = "^(?!_)[\\w\\s_\\u4e00-\\u9fa5]{1,255}$";
        String menuTemplateRemarkRegex = "^.{0,1024}$";
        if (StringUtils.isEmpty(this.templateName)) {
            return false;
        }
        if (!this.templateName.matches(menuTemplateNameRegex)) {
            return false;
        }
        if (!StringUtils.isEmpty(remark) && !this.remark.matches(menuTemplateRemarkRegex)) {
            return false;
        }
        return true;
    }

    /**
     * 参数格式化 去空格
     */
    public void parameterFormat() {
        if (this.templateName != null) {
            this.templateName = templateName.replaceAll("(.*?)\\s+$", "$1");
        }
        if (this.remark != null) {
            this.remark = remark.replaceAll("(.*?)\\s+$", "$1");
        }
    }
}
