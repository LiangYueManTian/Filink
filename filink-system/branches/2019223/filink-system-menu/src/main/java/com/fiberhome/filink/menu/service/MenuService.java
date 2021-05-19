package com.fiberhome.filink.menu.service;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;



import java.util.List;
/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public interface MenuService {
    /**
     * 查询菜单配置模板
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    Result queryListMenuTemplateByPage(QueryCondition<MenuTemplate> queryCondition);

    /**
     * 启用菜单接口
     * @param menuTemplateId 要启用的ID
     * @return 启用结果
     */
    Result openMenuTemplate(String menuTemplateId);
    /**
     * 查询默认菜单
     * @return 查询结果
     */
    Result  getDefaultMenuTemplate();
    /**
     * 验证菜单配置名称重复
     * @param menuTemplate id  要验证的名称
     * @return 验证结果
     */
    Result queryMenuTemplateNameIsExists(MenuTemplate menuTemplate);
    /**
     * 新增菜单配置模板
     * @param menuTemplateAndMenuInfoTree 传入的菜单模板及对应的菜单树形结构
     * @return 增加结果
     */
    Result addMenuTemplate(MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree);
    /**
     * 查询菜单配置模板详情
     * @param menuTemplateId 查询id
     * @return 查询结果
     */
    Result getMenuTemplateByMenuTemplateId(String menuTemplateId);

    /**
     * 修改菜单配置模板
     * @param menuTemplateAndMenuInfoTree 修改信息
     * @return 修改结果
     */
    Result updateMenuTemplate( MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree);
    /**
     * 查询菜单配置模板是否启用的接口
     * @param menuTemplateIds 要查询的id数组
     * @return 查询结果
     */
    Result queryMenuTemplateIsOpen(List<String> menuTemplateIds);
    /**
     * 删除菜单配置模板
     * @param menuTemplateIds 要删除的模板id
     * @return 删除结果
     */
    Result deleteMenuTemplate( List<String> menuTemplateIds) ;
    /**
     * 查询展示的模板
     * @return 查询结果
     */
    MenuTemplateAndMenuInfoTree getShowMenuTemplate();
}
