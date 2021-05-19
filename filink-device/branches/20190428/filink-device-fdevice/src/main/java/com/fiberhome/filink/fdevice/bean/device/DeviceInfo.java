package com.fiberhome.filink.fdevice.bean.device;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fiberhome.filink.bean.CheckInputString;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * <p>
 * 设施实体类
 * </p>
 *
 * @author zepenggao@wistronits.com
 * @since 2019-01-07
 */
@Data
@TableName("device_info")
public class DeviceInfo extends Model<DeviceInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 设施id
     */
    @TableId(value = "device_id", type = IdType.UUID)
    private String deviceId;

    /**
     * 设施类型
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 设施名称
     */
    @TableField("device_name")
    private String deviceName;

    /**
     * 设施状态
     */
    @TableField("device_status")
    private String deviceStatus;

    /**
     * 设施编号
     */
    @TableField("device_code")
    private String deviceCode;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 部署状态
     */
    @TableField("deploy_status")
    private String deployStatus;

    /**
     * 省
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 市
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 区
     */
    @TableField("district_name")
    private String districtName;


    /**
     * gps定位
     */
    @TableField("position_gps")
    private String positionGps;

    /**
     * 基础定位
     */
    @TableField("position_base")
    private String positionBase;

    /**
     * 关联区域id
     */
    @TableField("area_id")
    private String areaId;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 关联特性字段表id
     */
    @TableField("specific_field_id")
    private String specificFieldId;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Timestamp updateTime;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    private String isDeleted;

    private static final int POSITION_NUM_COUNT = 2;

    @Override
    protected Serializable pkVal() {
        return this.deviceId;
    }

    /**
     * 校验设施名合法性
     *
     * @return
     */
    public boolean checkDeviceName() {
        String checkName = CheckInputString.nameCheck(this.deviceName);
        return  !StringUtils.isEmpty(checkName);
    }

    /**
     * 校验参数格式合法性
     *
     * @return
     */
    public boolean checkParameterFormat() {
        String checkRemark = CheckInputString.markCheck(this.remarks);
        String checkAddress = CheckInputString.markCheck(this.address);
        return (StringUtils.isEmpty(remarks) || !StringUtils.isEmpty(checkRemark))
                && (StringUtils.isEmpty(address) || !StringUtils.isEmpty(checkAddress));
    }

    /**
     * 校验格式
     *
     * @param string
     * @param regex
     * @return
     */
    private boolean checkStringRegex(String string, String regex) {
        if (!StringUtils.isEmpty(string) && !string.matches(regex)) {
            return false;
        }
        return true;
    }

    /**
     * 参数格式化
     */
    public void parameterFormat() {
        this.deviceName = charactersFormat(this.deviceName);
        this.address = charactersFormat(this.address);
        this.remarks = charactersFormat(this.remarks);
    }

    /**
     * 修改特殊字符
     *
     * @param characters
     * @return
     */
    private String charactersFormat(String characters) {
        if (characters != null) {
            characters = characters.replaceAll("(.*?)\\s+$", "$1").trim();
        }
        return characters;
    }

    /**
     * 校验设施基础定位参数格式
     *
     * @return
     */
    public boolean checkDevicePositionBase() {
        return checkPosition(this.positionBase);
    }

    /**
     * 校验设施GPS定位参数格式
     *
     * @return
     */
    public boolean checkDevicePositionGps() {
        return checkPosition(this.positionGps);
    }

    /**
     * 校验设施定位参数格式信息格式
     *
     * @param position
     * @return
     */
    private boolean checkPosition(String position) {
        String[] strings = position.split(",");
        if (ArrayUtils.isEmpty(strings) || strings.length != POSITION_NUM_COUNT) {
            return false;
        }
        return (isInteger(strings[0]) || isDouble(strings[0])) && (isInteger(strings[1]) || isDouble(strings[1]));
    }

    /**
     * 判断整数（int）
     *
     * @param str 待验证字符串
     * @return true-整数 false-非整数
     */
    public boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断浮点数（double和float）
     *
     * @param str 待验证字符串
     * @return true-浮点数 false-非浮点数
     */
    public boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

}
