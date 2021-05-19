package com.fiberhome.filink.menu.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.menu.bean.MenuTemplate;

import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public interface MenuTemplateService extends IService<MenuTemplate> {
    /**
     * 查询菜单配置模板
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    Result queryListMenuTemplateByPage(QueryCondition<MenuTemplate> queryCondition);

    /**
     * 启用菜单配置模板
     *
     * @param menuTemplateId 要启用的id
     * @return 启用结果
     */
    Result openMenuTemplate(String menuTemplateId);

    /**
     * 验证菜单配置名称重复
     *
     * @param menuTemplate id  要验证的名称
     * @return 验证结果
     */
    boolean queryMenuTemplateNameIsExists(MenuTemplate menuTemplate);

    /**
     * 新增菜单模板
     *
     * @param menuTemplate 新增模板数据
     * @return 新增结果
     */
    boolean addMenuTemplate(MenuTemplate menuTemplate);

    /**
     * 查询菜单配置模板是否启用的接口
     *
     * @param menuTemplateIds 要查询的id数组
     * @return 查询结果
     */
    boolean queryMenuTemplateIsOpen(List<String> menuTemplateIds);

    /**
     * 将所有模板设置为禁用
     *
     * @return 操作结果
     */
    Integer updateAllMenuTemplate();
}
