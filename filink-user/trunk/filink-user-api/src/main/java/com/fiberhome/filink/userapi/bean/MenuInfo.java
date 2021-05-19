package com.fiberhome.filink.userapi.bean;




import lombok.Data;

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
public class MenuInfo {


    /**
     * 菜单明细id(UUID)
     */

    private String menuId;

    /**
     * 是否展示
     */

    private String isShow;

    /**
     * 菜单名称
     */

    private String menuName;

    /**
     * 菜单指向（菜单请求路径）
     */

    private String menuHref;

    /**
     * 父级菜单编码
     */

    private String parentMenuId;

    /**
     * 菜单级别
     */
    private Integer menuLevel;

    /**
     * 菜单排序
     */

    private Integer menuSort;

    /**
     * 图片路径
     */

    private String imageUrl;

    /**
     * 创建人
     */

    private String createUser;

    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 更新人
     */

    private String updateUser;

    /**
     * 更新时间
     */

    private Date updateTime;
    /**
     * 模板id
     */

    private String menuTemplateId;


}
