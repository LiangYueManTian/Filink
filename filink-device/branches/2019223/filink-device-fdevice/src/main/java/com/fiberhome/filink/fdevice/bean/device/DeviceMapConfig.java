package com.fiberhome.filink.fdevice.bean.device;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 首页地图和设施类型配置类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-19
 */
@Data
@EqualsAndHashCode(callSuper=true)
@TableName("device_map_config")
public class DeviceMapConfig extends Model<DeviceMapConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID（UUID）
     */
    @TableId("config_id")
    private String configId;

    /**
     * 配置类型
     */
    @TableField("config_type")
    private String configType;

    /**
     * 设施类型
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置所属用户
     */
    @TableField("user_id")
    private String userId;

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
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    @Override
    protected Serializable pkVal() {
        return this.configId;
    }

    @Override
    public String toString() {
        return "DeviceMapConfig{" +
        "configId=" + configId +
        ", configType=" + configType +
        ", deviceType=" + deviceType +
        ", configValue=" + configValue +
        ", userId=" + userId +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
