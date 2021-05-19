package com.fiberhome.filink.menu.service.impl;


import com.fiberhome.filink.menu.bean.MenuRelation;
import com.fiberhome.filink.menu.dao.MenuRelationDao;
import com.fiberhome.filink.menu.dto.MenuInfoTree;
import com.fiberhome.filink.menu.service.MenuRelationService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Service
public class MenuRelationServiceImpl extends ServiceImpl<MenuRelationDao, MenuRelation> implements MenuRelationService {
    /**
     * 自动注入menuRelationDao
     */
    @Autowired
    private MenuRelationDao menuRelationDao;

    /**
     * 新增菜单关系数据
     *
     * @param menuRelationList 新增实体集合
     * @return 新增结果
     */
    @Override
    public boolean addMenuRelations(List<MenuRelation> menuRelationList) {
        Integer result = menuRelationDao.insertMenuRelations(menuRelationList);
        return result == menuRelationList.size();
    }

    /**
     * 查询模板该id的菜单详细信息
     *
     * @param menuTemplateId 模板id
     * @return 查询结果
     */
    @Override
    public List<MenuInfoTree> selectMenuRelationAndMenuInfo(String menuTemplateId) {
        return menuRelationDao.selectMenuRelationAndMenuInfo(menuTemplateId);
    }

    /**
     * 查询已经启用的模板菜单详细信息
     *
     * @param menuTemplateId 模板id
     * @return 查询结果
     */
    @Override
    public List<MenuInfoTree> getShowMenuTemplate(String menuTemplateId) {
        return menuRelationDao.getShowMenuTemplate(menuTemplateId);
    }
}
