package com.fiberhome.filink.parts.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fiberhome.filink.bean.CheckInputString;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 配件实体类
 * </p>
 *
 * @author gzp
 * @since 2019-02-12
 */
@Data
@TableName("part_info")
public class PartInfo extends Model<PartInfo> {

    private static final long serialVersionUID = 1L;

    @TableId("part_id")
    private String partId;

    @TableField("part_name")
    private String partName;

    @TableField("part_type")
    private String partType;

    @TableField("part_code")
    private String partCode;

    private String remark;

    private String trustee;

    @TableField("is_deleted")
    private String isDeleted;

    @TableField("create_time")
    private Timestamp createTime;

    @TableField("create_user")
    private String createUser;

    @TableField("update_time")
    private Timestamp updateTime;

    @TableField("update_user")
    private String updateUser;

    @TableField("level_one_dept_id")
    private String levelOneDeptId;

    /**
     * 责任单位id
     */
    @TableField(exist = false)
    private List<String> accountabilityUnit;


    @Override
    protected Serializable pkVal() {
        return this.partId;
    }

    @Override
    public String toString() {
        return "PartInfo{" +
                "partId=" + partId +
                ", partName=" + partName +
                ", partType=" + partType +
                ", partCode=" + partCode +
                ", remark=" + remark +
                ", trustee=" + trustee +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", createUser=" + createUser +
                ", updateTime=" + updateTime +
                ", updateUser=" + updateUser +
                "}";
    }

    public boolean checkName() {
//        String deviceNameRegex = "^[a-zA-Z0-9_\\-\\u4e00-\\u9fa5\\\" \"]{0,128}$";
//        return this.partName.matches(deviceNameRegex);

        String nameRegex = CheckInputString.nameCheck(this.partName);
        return !StringUtils.isEmpty(nameRegex);
    }

    /**
     * 参数格式化
     */
    public void parameterFormat() {
        this.partName = charactersFormat(this.partName);
        this.remark = charactersFormat(this.remark);
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
}
