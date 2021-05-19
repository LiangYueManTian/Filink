package com.fiberhome.filink.boxhead.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;


/**
 * <p>
 * 表头栏实体
 * </p>
 *
 * @author wanzhao zhang
 * @since 2019-05-21
 */
@Data
@TableName("box_head")
public class BoxHead extends Model<BoxHead> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 菜单id
     */
    @TableField("menu_id")
    private String menuId;

    /**
     * 表头定制
     */
    private String custom;


    /**
     * 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }



}
