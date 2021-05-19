package com.fiberhome.filink.dprotocol.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *     设施协议实体类
 * </p>
 *
 * @author chaofang@wistrontis.com
 * @since 2019-01-12
 */
@Data
@EqualsAndHashCode(callSuper=true)
@TableName("device_protocol")
public class DeviceProtocol extends Model<DeviceProtocol> {

    private static final long serialVersionUID = 1L;

    /**
     * 设施协议ID（UUID）
     */
    @TableId("protocol_id")
    private String protocolId;

    /**
     * 设施协议文件名称
     */
    @TableField("protocol_name")
    private String protocolName;

    /**
     * 设施协议文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 设施协议文件长度
     */
    @TableField("file_length")
    private String fileLength;

    /**
     * 文件下载路径
     */
    @TableField("file_download_url")
    private String fileDownloadUrl;

    /**
     * 硬件版本
     */
    @TableField("hardware_version")
    private String hardwareVersion;

    /**
     * 软件版本
     */
    @TableField("software_version")
    private String softwareVersion;

    /**
     * 是否删除（0未删除 1已删除）
     */
    @TableField("is_deleted")
    private String isDeleted;

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

    @Override
    protected Serializable pkVal() {
        return this.protocolId;
    }

    @Override
    public String toString() {
        return "DeviceProtocol{" +
                "protocolId=" + protocolId +
                ", protocolName=" + protocolName +
                ", fileName=" + fileName +
                ", fileLength=" + fileLength +
                ", fileDownloadUrl=" + fileDownloadUrl +
                ", hardwareVersion=" + hardwareVersion +
                ", softwareVersion=" + softwareVersion +
                ", isDeleted=" + isDeleted +
                ", createUser=" + createUser +
                ", createTime=" + createTime +
                ", updateUser=" + updateUser +
                ", updateTime=" + updateTime +
                "}";
    }

    /**
     * 校验参数是否为空
     * @return true是 false不是
     */
    public boolean checkIdAndName() {
        return StringUtils.isEmpty(protocolId) || StringUtils.isEmpty(protocolName);
    }
}
