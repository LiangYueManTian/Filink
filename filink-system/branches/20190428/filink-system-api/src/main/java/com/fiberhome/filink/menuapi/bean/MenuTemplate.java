package com.fiberhome.filink.menuapi.bean;


import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * <p>
 * menu_template实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */

public class MenuTemplate  {


    /**
     * 菜单明细id(UUID)
     */

    private String menuTemplateId;

    /**
     * 模板名称
     */

    private String templateName;

    /**
     * 模板的状态（状态为启用/禁用）
     */

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

    private String isDeleted = "0";

    /**
     * 创建人
     */

    private String createUser;

    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 更新人
     */

    private String updateUser;

    /**
     * 更新时间
     */

    private Date updateTime;
    /**
     * 创建时间时间戳
     */

    private Long createTimeTimestamp;
    /**
     * 更新时间时间戳
     */

    private Long updateTimeTimestamp;



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
}
