package com.fiberhome.filink.fdevice.bean.area;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 部门实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-22
 */
@Data
@TableName("area_dept_info")
public class AreaDeptInfo extends Model<AreaDeptInfo> {

    private static final long serialVersionUID = 1L;


    /**
     * 部门id
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 主表id
     */
    @TableField("area_id")
    private String areaId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
