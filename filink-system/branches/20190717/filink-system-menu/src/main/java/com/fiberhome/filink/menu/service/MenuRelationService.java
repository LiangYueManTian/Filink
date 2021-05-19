package com.fiberhome.filink.menu.service;

import com.fiberhome.filink.menu.bean.MenuRelation;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.menu.dto.MenuInfoTree;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public interface MenuRelationService extends IService<MenuRelation> {
    /**
     * 新增菜单关系数据
     *
     * @param menuRelationList 新增实体集合
     * @return 新增结果
     */
    boolean addMenuRelations(List<MenuRelation> menuRelationList);

    /**
     * 查询模板该id的菜单详细信息
     *
     * @param menuTemplateId 模板id
     * @return 查询出的数据
     */
    List<MenuInfoTree> selectMenuRelationAndMenuInfo(String menuTemplateId);

    /**
     * 查询当前已经启用的模板
     *
     * @param menuTemplateId   传入参数
     * @return 查询结果
     */
    List<MenuInfoTree> getShowMenuTemplate(String menuTemplateId);
}
