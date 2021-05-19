package com.fiberhome.filink.menu.dto;


import com.fiberhome.filink.menu.bean.MenuInfo;
import lombok.Data;


import java.util.List;

/**
 * <p>
 * MenuInfoTree实体 菜单信息树状结构
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Data
public class MenuInfoTree extends MenuInfo {
    /**
     * 子数据
     */
    private List<MenuInfoTree> children;
}
