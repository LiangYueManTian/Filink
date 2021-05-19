package com.fiberhome.filink.menu.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * menu_info 实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Data
@TableName("menu_info")
public class MenuInfo extends Model<MenuInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单明细id(UUID)
     */
    @TableId("menu_id")
    private String menuId;

    /**
     * 是否展示
     */
    @TableId("is_show")
    private String isShow;

    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单指向（菜单请求路径）
     */
    @TableField("menu_href")
    private String menuHref;

    /**
     * 父级菜单编码
     */
    @TableField("parent_menu_id")
    private String parentMenuId;

    /**
     * 菜单级别
     */
    @TableField("menu_level")
    private Integer menuLevel;

    /**
     * 菜单排序
     */
    @TableField("menu_sort")
    private Integer menuSort;

    /**
     * 图片路径
     */
    @TableField("image_url")
    private String imageUrl;

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
    /**
     * 模板id
     */
    @TableField(exist = false)
    private String menuTemplateId;

    @Override
    protected Serializable pkVal() {
        return this.menuId;
    }
}
