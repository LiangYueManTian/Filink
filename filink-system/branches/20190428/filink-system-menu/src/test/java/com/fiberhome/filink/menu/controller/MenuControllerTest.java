package com.fiberhome.filink.menu.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menu.service.MenuService;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuControllerTest
 */
@RunWith(JMockit.class)
public class MenuControllerTest {
    /**
     * menuController
     */
    @Tested
    private MenuController menuController;
    /**
     * 自动注入menu服务层
     */
    @Injectable
    private MenuService menuService;
    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;
    @Mocked
    private RequestContextHolder requestContextHolder;
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
    }

    /**
     * queryListMenuTemplateByPage
     */
    @Test
    public void queryListMenuTemplateByPage() {
        Result result = menuController.queryListMenuTemplateByPage(new QueryCondition<>());
        Assert.assertTrue(result != null);
    }

    /**
     * openMenuTemplate
     */
    @Test
    public void openMenuTemplate() {
        Result result = menuController.openMenuTemplate("a");
        Assert.assertTrue(result != null);
        Result result2 = menuController.openMenuTemplate("");
        Assert.assertTrue(result2.getCode() != 0);
    }

    /**
     * getDefaultMenuTemplate
     */
    @Test
    public void getDefaultMenuTemplate() {
        Result result = menuController.getDefaultMenuTemplate();
        Assert.assertTrue(result != null);
    }

    /**
     * queryMenuTemplateNameIsExists
     */
    @Test
    public void queryMenuTemplateNameIsExists() {
        MenuTemplate menuTemplate = new MenuTemplate();
        Result result = menuController.queryMenuTemplateNameIsExists(menuTemplate);
        Assert.assertTrue(result.getCode() != 0);
        menuTemplate.setTemplateName("a");
        Result result2 = menuController.queryMenuTemplateNameIsExists(menuTemplate);
        Assert.assertTrue(result2.getCode() == 0);
    }

    /**
     * addMenuTemplate
     */
    @Test
    public void addMenuTemplate() {
        MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree = new MenuTemplateAndMenuInfoTree();
        Result result = menuController.addMenuTemplate(menuTemplateAndMenuInfoTree);
        Assert.assertTrue(result.getCode() != 0);
        menuTemplateAndMenuInfoTree.setTemplateName("a");
        Result result2 = menuController.addMenuTemplate(menuTemplateAndMenuInfoTree);
        Assert.assertTrue(result2.getCode() == 0);
    }

    /**
     * getMenuTemplateByMenuTemplateId
     */
    @Test
    public void getMenuTemplateByMenuTemplateId() {
        Result a = menuController.getMenuTemplateByMenuTemplateId("a");
        Assert.assertTrue(a.getCode() == 0);
        Result b = menuController.getMenuTemplateByMenuTemplateId("");
        Assert.assertTrue(b.getCode() != 0);
    }

    /**
     * updateMenuTemplate
     */
    @Test
    public void updateMenuTemplate() {
        MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree = new MenuTemplateAndMenuInfoTree();
        Result result = menuController.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        Assert.assertTrue(result.getCode() != 0);
        menuTemplateAndMenuInfoTree.setTemplateName("a");
        Result result2 = menuController.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        Assert.assertTrue(result2.getCode() == 0);

    }

    /**
     * queryMenuTemplateIsOpen
     */
    @Test
    public void queryMenuTemplateIsOpen() {
        List<String> menuTemplateIds = new ArrayList<>();
        Result result = menuController.queryMenuTemplateIsOpen(menuTemplateIds);
        Assert.assertTrue(result.getCode() != 0);
        menuTemplateIds.add("aa");
        Result result2 = menuController.queryMenuTemplateIsOpen(menuTemplateIds);
        Assert.assertTrue(result2.getCode() == 0);
    }

    /**
     * deleteMenuTemplate
     */
    @Test
    public void deleteMenuTemplate() throws Exception {
        List<String> menuTemplateIds = new ArrayList<>();
        Result result = menuController.deleteMenuTemplate(menuTemplateIds);
        Assert.assertTrue(result.getCode() != 0);
        menuTemplateIds.add("aa");
        Result result2 = menuController.deleteMenuTemplate(menuTemplateIds);
        Assert.assertTrue(result2.getCode() == 0);
        new Expectations() {
            {
                menuService.deleteMenuTemplate((List) any);
                result = new Exception();
            }
        };
        Result result3 = menuController.deleteMenuTemplate(menuTemplateIds);
        Assert.assertTrue(result3.getCode() != 0);
    }

    /**
     * 查找已经启用的模板测试
     */
    @Test
    public void getShowMenuTemplate() {
        menuController.getShowMenuTemplate();
    }
}
