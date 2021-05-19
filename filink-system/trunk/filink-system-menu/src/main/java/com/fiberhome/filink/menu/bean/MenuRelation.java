package com.fiberhome.filink.menu.bean;


import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * menu_relation 实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Data
@TableName("menu_relation")
public class MenuRelation extends Model<MenuRelation> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId("menu_relation_id")
    private String menuRelationId;

    /**
     * 菜单模板编号
     */
    @TableField("menu_template_id")
    private String menuTemplateId;

    /**
     * 菜单详情id
     */
    @TableField("menu_id")
    private String menuId;


    /**
     * 菜单排序
     */
    @TableField("menu_sort")
    private Integer menuSort;
    /**
     * 是否展示
     */
    @TableField("is_show")
    private String isShow;
    /**
     * 是否删除  0 未删除 1 删除
     */
    @TableField("is_deleted")
    private int isDeleted = 0;

    /**
     * 检测必填参数
     *
     * @return
     */
    public boolean checkParameter() {
        boolean empty = StringUtils.isEmpty(this.menuId);
        boolean empty1 = StringUtils.isEmpty(this.menuSort);
        boolean empty2 = StringUtils.isEmpty(this.isShow);
        if (empty || empty1 || empty2) {
            return true;
        }
        return false;
    }

    @Override
    protected Serializable pkVal() {
        return this.getMenuRelationId();
    }
}
