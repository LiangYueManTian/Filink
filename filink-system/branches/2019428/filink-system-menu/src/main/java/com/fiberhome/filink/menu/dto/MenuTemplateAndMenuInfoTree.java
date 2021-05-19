package com.fiberhome.filink.menu.dto;


import com.fiberhome.filink.menu.bean.MenuTemplate;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 模板表以及树结构，用于接收前端数据
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Data
public class MenuTemplateAndMenuInfoTree extends MenuTemplate {
    /**
     * 树结构
     */
    private List<MenuInfoTree> menuInfoTrees;
}
