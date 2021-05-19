package com.fiberhome.filink.menu.service.impl;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.menu.bean.MenuRelation;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.dto.MenuInfoTree;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menu.exception.FilinkMenuDirtyDataException;
import com.fiberhome.filink.menu.service.MenuInfoService;
import com.fiberhome.filink.menu.service.MenuRelationService;
import com.fiberhome.filink.menu.service.MenuTemplateService;
import com.fiberhome.filink.menu.stream.MenuStreams;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
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
import java.util.Map;


@RunWith(JMockit.class)
public class MenuServiceImplTest {
    /**
     * 要测试的
     */
    @Tested
    private MenuServiceImpl menuService;
    /**
     * 菜单消息通道
     */
    @Injectable
    private MenuStreams menuStreams;
    /**
     * 自动注入menuTemplateService
     */
    @Injectable
    private MenuTemplateService menuTemplateService;
    /**
     * 自动注入menuInfoService
     */
    @Injectable
    private MenuInfoService menuInfoService;
    /**
     * 自动注入menuRelationService
     */
    @Injectable
    private MenuRelationService menuRelationService;
    /**
     * 注入日志
     */
    @Injectable
    private LogProcess logProcess;
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
     * 查询菜单配置模板
     */
    @Test
    public void queryListMenuTemplateByPage() {
        Result result = menuService.queryListMenuTemplateByPage(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == 0);
    }

    ;

    /**
     * 启用菜单接口
     */
    @Test
    public void openMenuTemplate() {
        Result result = menuService.openMenuTemplate("a");
        Assert.assertTrue(result.getCode() == 0);
    }

    ;

    /**
     * getDefaultMenuTemplate
     */
    @Test
    public void getDefaultMenuTemplate() {
        Result defaultMenuTemplate = menuService.getDefaultMenuTemplate();
        Assert.assertTrue(defaultMenuTemplate.getCode() == 0);
    }

    ;

    /**
     * 验证菜单配置名称重复
     */
    @Test
    public void queryMenuTemplateNameIsExists() {
        Result result = menuService.queryMenuTemplateNameIsExists(new MenuTemplate());
    }

    ;

    /**
     * 新增菜单配置模板
     */
    @Test
    public void addMenuTemplate() {
        List<MenuInfoTree> menuInfoTreeList = new ArrayList<>();
        MenuInfoTree menuInfoTree = new MenuInfoTree();
        menuInfoTree.setMenuId("A");
        menuInfoTree.setIsShow("a");
        menuInfoTree.setMenuSort(1);
        menuInfoTreeList.add(menuInfoTree);

        MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree = new MenuTemplateAndMenuInfoTree();
        menuTemplateAndMenuInfoTree.setMenuInfoTrees(menuInfoTreeList);
        menuService.addMenuTemplate(menuTemplateAndMenuInfoTree);
        new Expectations() {
            {
                menuTemplateService.queryMenuTemplateNameIsExists((MenuTemplate) any);
                result = true;
            }
        };
        new Expectations() {
            {
                menuInfoService.selectCountByIds((List) any);
                result = 1;
            }
        };
        menuService.addMenuTemplate(menuTemplateAndMenuInfoTree);
        new Expectations() {
            {
                menuTemplateService.addMenuTemplate((MenuTemplate) any);
                result = true;
            }
        };
        new Expectations() {
            {
                menuRelationService.addMenuRelations((List) any);
                result = true;
            }
        };
        menuService.addMenuTemplate(menuTemplateAndMenuInfoTree);
    }

    ;

    /**
     * 查询菜单配置模板详情
     */
    @Test
    public void getMenuTemplateByMenuTemplateId() {
        menuService.getMenuTemplateByMenuTemplateId("a");
        new Expectations() {
            {
                menuTemplateService.selectById(anyString);
                result = null;
            }
        };
        menuService.getMenuTemplateByMenuTemplateId("a");
    }

    /**
     * 修改菜单配置模板
     */
    @Test
    public void updateMenuTemplate() {
        List<MenuInfoTree> menuInfoTreeList = new ArrayList<>();
        MenuInfoTree menuInfoTree = new MenuInfoTree();
        menuInfoTree.setMenuId("A");
        menuInfoTree.setIsShow("a");
        menuInfoTree.setMenuSort(1);
        menuInfoTreeList.add(menuInfoTree);

        MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree = new MenuTemplateAndMenuInfoTree();
        menuTemplateAndMenuInfoTree.setMenuInfoTrees(menuInfoTreeList);
        menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        menuTemplateAndMenuInfoTree.setMenuTemplateId("A");
        menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        menuTemplateAndMenuInfoTree.setVersion(0);
        menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        new Expectations() {
            {
                menuTemplateService.queryMenuTemplateNameIsExists((MenuTemplate) any);
                result = true;
            }
        };
        new Expectations() {
            {
                menuRelationService.addMenuRelations((List) any);
                result = true;
            }
        };
        new Expectations() {
            {
                menuInfoService.selectCountByIds((List) any);
                result = 1;
            }
        };
        menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        new Expectations() {
            {
                menuRelationService.deleteByMap((Map) any);
                result = true;
            }
        };
        new Expectations() {
            {
                menuTemplateService.updateById((MenuTemplate) any);
                result = true;
            }
        };
        menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        MenuTemplate menuTemplate = new MenuTemplate();
        menuTemplate.setTemplateStatus("1");
        menuTemplate.setIsDeleted("0");
        menuTemplate.setVersion(0);
        new Expectations() {
            {
                menuTemplateService.selectById(anyString);
                result = menuTemplate;
            }
        };
        menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
    }

    /**
     * 查询菜单配置模板是否启用的接口
     */
    @Test
    public void queryMenuTemplateIsOpen() {
        Result result = menuService.queryMenuTemplateIsOpen(new ArrayList<>());
        Assert.assertTrue(result.getCode() == 0);
    }

    ;

    /**
     * 删除菜单配置模板
     */
    @Test
    public void deleteMenuTemplate() throws Exception {
        List<MenuTemplate> menuTemplates = new ArrayList<>();
        MenuTemplate menuTemplate = new MenuTemplate();
        menuTemplate.setIsDeleted("1");
        menuTemplates.add(menuTemplate);
        List<MenuRelation> menuRelations = new ArrayList<>();
        MenuRelation menuRelation = new MenuRelation();
        menuRelations.add(menuRelation);
        List<String> ids = new ArrayList<>();
        ids.add("001");
        new Expectations() {
            {
                menuTemplateService.selectBatchIds((List) any);
                result = null;
            }
        };
        menuService.deleteMenuTemplate(new ArrayList<>());
        new Expectations() {
            {
                menuTemplateService.selectBatchIds((List) any);
                result = menuTemplates;
            }
        };
        menuService.deleteMenuTemplate(ids);
        menuTemplate.setIsDeleted("0");
        new Expectations() {
            {
                menuTemplateService.selectBatchIds((List) any);
                result = menuTemplates;
            }
        };
        menuService.deleteMenuTemplate(ids);
        new Expectations() {
            {
                menuTemplateService.queryMenuTemplateIsOpen((List) any);
                result = true;
            }
        };
        new Expectations() {
            {
                menuRelationService.deleteByMap((Map) any);
                result = true;
            }
        };
        new Expectations() {
            {
                menuTemplateService.updateBatchById((List) any);
                result = true;
            }
        };
        menuService.deleteMenuTemplate(ids);
    }

    /**
     * 获取已启用模板方法测试
     */
    @Test
    public void getShowMenuTemplate() {
        try {
            menuService.getShowMenuTemplate();
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkMenuDirtyDataException.class);
        }

        List<MenuTemplate> list = new ArrayList<>();
        MenuTemplate menuTemplate = new MenuTemplate();
        list.add(menuTemplate);
        new Expectations() {
            {
                menuTemplateService.selectByMap((Map) any);
                result = list;
            }
        };
        menuService.getShowMenuTemplate();
    }
}
