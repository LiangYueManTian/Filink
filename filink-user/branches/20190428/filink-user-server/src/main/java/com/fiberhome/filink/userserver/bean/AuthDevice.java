package com.fiberhome.filink.userserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 统一授权临时授权和设施对应的中间表
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@Data
@TableName("auth_device")
public class AuthDevice extends Model<AuthDevice> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 授权id
     */
    private String authId;

    /**
     * 设备id
     */
    private String deviceId;

    private Long createTime;

    private String createUser;

    /**
     * 门id
     */
    private String doorId;

    /**
     * 二维码
     */
    private String qrcode;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 设施类型
     */
    private String deviceType;

    @TableField(exist = false)
    private String deviceName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AuthDevice{" +
        "id=" + id +
        ", authId=" + authId +
        ", deviceId=" + deviceId +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        "}";
    }
}
