package com.fiberhome.filink.userapi.bean;


import lombok.Data;

import java.util.List;

/**
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 * 模板表以及树结构，用于接收前端数据
 */
@Data
public class MenuTemplateAndMenuInfoTree extends MenuTemplate {
    /**
     * 树结构
     */
    private List<MenuInfoTree> menuInfoTrees;
}
