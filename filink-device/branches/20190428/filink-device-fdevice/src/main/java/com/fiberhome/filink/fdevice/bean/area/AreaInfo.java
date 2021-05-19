package com.fiberhome.filink.fdevice.bean.area;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.bean.CheckInputString;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 区域信息
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@Data
@TableName("area_info")
public class AreaInfo extends Model<AreaInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 区域id(UUID)
     */
    @TableId("area_id")
    private String areaId;

    /**
     * 区域级别
     */
    private Integer level;

    /**
     * 区域名称
     */
    @TableField("area_name")
    private String areaName;

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
     * 详细地址
     */
    private String address;
    /**
     * 责任单位id
     */
    @TableField(exist = false)
    private Set<String> accountabilityUnit;
    /**
     * 责任单位名称
     */
    @TableField(exist = false)
    private StringBuilder accountabilityUnitName = new StringBuilder();
    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 所属区域id
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 是否删除 默认为0
     */
    @TableField("is_deleted")
    private String isDeleted = "0";

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 子类到父类的名称
     */
    @TableField(exist = false)
    private String areaAndPrentName;
    /**
     * 父类名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 是否有子区域
     */
    @TableField(exist = false)
    private boolean hasChild;
    /**
     * 是否有权限
     */
    @TableField(exist = false)
    private Boolean hasPermissions;

    public boolean checkAreaName() {
        areaName = CheckInputString.nameCheck(areaName);
        if (StringUtils.isEmpty(areaName)) {
            return false;
        }
        return true;
    }

    public boolean checkParameterFormat() {
        //如果备注不为空 进行正则验证
        if (!StringUtils.isEmpty(remarks)) {
            remarks = CheckInputString.markCheck(remarks);
            //如果验证后为null 字段格式不正确，返回false
            if (StringUtils.isEmpty(remarks)) {
                return false;
            }
        }
        //地址正则
        String addressRegex = "^[\\s\\da-zA-Z\\u4e00-\\u9fa5`\\-=\\[\\]\\\\;',./~!@#$%^&*\\(\\)_+{}|:\"<>?·【】、；'、‘’，。、！￥……（）——+｛｝：“”《》？]{0,255}$";
        if (!StringUtils.isEmpty(this.address) && !this.address.matches(addressRegex)) {
            return false;
        }
        return true;
    }

    /**
     * 格式化去空格
     */
    public void parameterFormat() {
        if (this.address != null) {
            this.address = address.replaceAll("(.*?)\\s+$", "$1");
        }
    }

    /**
     * 用于列表导出级别
     *
     * @return
     */
    @JsonIgnore
    public String getTranslationLevel() {
        switch (level) {
            case 1:
                return I18nUtils.getString(AreaI18nConstant.LEVEL_ONE);
            case 2:
                return I18nUtils.getString(AreaI18nConstant.LEVEL_TWO);
            case 3:
                return I18nUtils.getString(AreaI18nConstant.LEVEL_THREE);
            case 4:
                return I18nUtils.getString(AreaI18nConstant.LEVEL_FOUR);
            case 5:
                return I18nUtils.getString(AreaI18nConstant.LEVEL_FIVE);
            default:
                return null;
        }
    }

    /**
     * 设置用户权限
     */
    public void setAreaPermissions(List<String> permissionsId) {
        this.hasPermissions = permissionsId.contains(areaId);
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
