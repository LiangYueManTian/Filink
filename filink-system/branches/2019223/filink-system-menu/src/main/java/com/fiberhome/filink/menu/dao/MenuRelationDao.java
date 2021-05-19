package com.fiberhome.filink.menu.dao;


import com.fiberhome.filink.menu.bean.MenuRelation;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.menu.dto.MenuInfoTree;


import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public interface MenuRelationDao extends BaseMapper<MenuRelation> {
    /**
     * 新增菜单关系数据
     *
     * @param menuRelationList 新增实体集合
     * @return 新增结果
     */
    Integer insertMenuRelations(List<MenuRelation> menuRelationList);

    /**
     * 查询模板该id的菜单详细信息
     *
     * @param menuTemplateId 模板id
     * @return 查询结果
     */
    List<MenuInfoTree> selectMenuRelationAndMenuInfo(String menuTemplateId);

    /**
     * 查询已经启用的模板信息
     *
     * @param menuTemplateId 模板id
     * @return 查询结果
     */
    List<MenuInfoTree> getShowMenuTemplate(String menuTemplateId);
}
