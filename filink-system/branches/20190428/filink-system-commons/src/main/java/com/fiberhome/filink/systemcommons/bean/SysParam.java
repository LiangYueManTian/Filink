package com.fiberhome.filink.systemcommons.bean;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * <p>
 *   系统服务统一参数实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
@TableName("sys_param")
public class SysParam extends Model<SysParam> {

    private static final long serialVersionUID = 1L;

    /**
     * 参数ID
     */
    @TableId("param_id")
    private String paramId;

    /**
     * 参数类型
     */
    @TableField("param_type")
    private String paramType;

    /**
     * 当前值
     */
    @TableField("present_value")
    private String presentValue;

    /**
     * 默认值
     */
    @TableField("default_value")
    private String defaultValue;

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

    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getPresentValue() {
        return presentValue;
    }

    public void setPresentValue(String presentValue) {
        this.presentValue = presentValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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


    /**
     * 校验参数信息是否为空
     * @return true是 false不是
     */
    public boolean checkValue() {
        return StringUtils.isEmpty(paramId) || StringUtils.isEmpty(paramType)
                || StringUtils.isEmpty(presentValue) || StringUtils.isEmpty(defaultValue);
    }

    @Override
    protected Serializable pkVal() {
        return this.paramId;
    }
}
