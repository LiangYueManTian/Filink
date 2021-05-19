package com.fiberhome.filink.menu.dto;


import com.fiberhome.filink.menu.bean.MenuInfo;
import lombok.Data;


import java.util.List;

/**
 * <p>
 * 菜单树状实体
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
