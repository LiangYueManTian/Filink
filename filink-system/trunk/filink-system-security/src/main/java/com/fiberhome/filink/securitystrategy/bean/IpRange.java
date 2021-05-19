package com.fiberhome.filink.securitystrategy.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *     访问控制实体类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-02-28
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ip_range")
public class IpRange extends Model<IpRange> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "range_id", type = IdType.UUID)
    private String rangeId;

    /**
     * IP类型
     */
    @TableField("ip_type")
    private String ipType;

    /**
     * 起始IP
     */
    @TableField("start_ip")
    private String startIp;

    /**
     * 终止IP
     */
    @TableField("end_ip")
    private String endIp;

    /**
     * 掩码
     */
    private String mask;

    /**
     * 启用状态,1是启用，0是禁用
     */
    @TableField("range_status")
    private String rangeStatus;

    /**
     * 是否删除，0没有，1已删除
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
        return this.rangeId;
    }

    /**
     * 校验输入IP范围参数是否为空
     * @return true是 false不是
     */
    public boolean checkIpRange() {
        if (StringUtils.isEmpty(ipType) || StringUtils.isEmpty(startIp)
                || StringUtils.isEmpty(endIp) || StringUtils.isEmpty(mask)) {
            return true;
        }
        return !ipType.matches(SecurityStrategyConstants.IP_TYPE_REGEX);
    }
    /**
     * 校验输入IP范围ID是否为空
     * @return true是 false不是
     */
    public boolean checkId() {
        return StringUtils.isEmpty(rangeId);
    }
    /**
     * 校验输入启用/禁用是否正确
     * @return true不正确 false正确
     */
    public boolean checkRangeStatus() {
        return StringUtils.isEmpty(rangeStatus) || !rangeStatus.matches(SecurityStrategyConstants.ONE_ZERO_REGEX);
    }
}
