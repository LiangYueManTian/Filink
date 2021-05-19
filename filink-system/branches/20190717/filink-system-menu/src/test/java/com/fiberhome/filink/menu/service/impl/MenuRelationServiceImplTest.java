package com.fiberhome.filink.menu.service.impl;

import com.fiberhome.filink.menu.dao.MenuRelationDao;
import com.fiberhome.filink.menu.dto.MenuInfoTree;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuRelationServiceImplTest
 */
@RunWith(JMockit.class)
public class MenuRelationServiceImplTest {
    /**
     * 要测试的
     */
    @Tested
    private MenuRelationServiceImpl menuRelationService;
    /**
     * 自动注入menuRelationDao
     */
    @Injectable
    private MenuRelationDao menuRelationDao;
    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
    }

    /**
     * 新增菜单关系数据
     */
    @Test
    public void addMenuRelations() {
        boolean b = menuRelationService.addMenuRelations(new ArrayList<>());
        Assert.assertTrue(b);
    }

    /**
     * 查询模板该id的菜单详细信息
     */
    @Test
    public void selectMenuRelationAndMenuInfo() {
        List<MenuInfoTree> menuInfoTreeList = menuRelationService.selectMenuRelationAndMenuInfo("a");
        Assert.assertTrue(menuInfoTreeList != null);
    }

    /**
     * 查询已经启用的模板菜单详细信息测试
     */
    @Test
    public void getShowMenuTemplate() {
        List<MenuInfoTree> aa = menuRelationService.getShowMenuTemplate("AA");
        Assert.assertTrue(aa != null);
    }
}
