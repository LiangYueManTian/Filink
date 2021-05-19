package com.fiberhome.filink.menuapi.bean;


import lombok.Data;

import java.util.List;

/**
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 * 树状结果实体
 */
@Data
public class MenuInfoTree extends MenuInfo {
    /**
     * 子数据
     */
    private List<MenuInfoTree> children;
}
