package com.fiberhome.filink.menu.service.impl;

import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.dao.MenuTemplateDao;
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
public class MenuTemplateServiceImplTest {
    @Tested
    private MenuTemplateServiceImpl menuTemplateService;
    /**
     * 自动注入menuTemplateDao
     */
    @Injectable
    private MenuTemplateDao menuTemplateDao;
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
        QueryCondition<MenuTemplate> queryCondition = new QueryCondition<>();
        menuTemplateService.queryListMenuTemplateByPage(queryCondition);
        List<FilterCondition> filterConditionList = new ArrayList<>();
        queryCondition.setFilterConditions(filterConditionList);
        menuTemplateService.queryListMenuTemplateByPage(queryCondition);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        queryCondition.setPageCondition(pageCondition);
        Result result = menuTemplateService.queryListMenuTemplateByPage(queryCondition);
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 启用菜单配置模板
     */
    @Test
    public void openMenuTemplate() {
        menuTemplateService.openMenuTemplate("a");
        new Expectations() {
            {
                menuTemplateDao.updateById((MenuTemplate) any);
                result = 1;
            }
        };
        Result result = menuTemplateService.openMenuTemplate("a");
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 验证菜单配置名称重复
     */
    @Test
    public void queryMenuTemplateNameIsExists() {
        MenuTemplate menuTemplate = new MenuTemplate();
        menuTemplate.setMenuTemplateId("1");
        MenuTemplate menuTemplate2 = new MenuTemplate();
        menuTemplate2.setMenuTemplateId("2");
        new Expectations() {
            {
                menuTemplateDao.selectByMap((Map) any);
                result = new ArrayList<>();
            }
        };
        menuTemplateService.queryMenuTemplateNameIsExists(menuTemplate);
        new Expectations() {
            {
                menuTemplateDao.selectByMap((Map) any);
                result = new ArrayList<>();
            }
        };
        menuTemplateService.queryMenuTemplateNameIsExists(menuTemplate);
        List<MenuTemplate> list = new ArrayList();
        MenuTemplate menuTemplate1 = new MenuTemplate();
        menuTemplate1.setTemplateName("aaa");
        menuTemplate1.setMenuTemplateId("aaa");
        list.add(menuTemplate1);
        new Expectations() {
            {
                menuTemplateDao.selectByMap((Map) any);
                result = list;
            }
        };
        boolean b = menuTemplateService.queryMenuTemplateNameIsExists(menuTemplate);
    }

    /**
     * 新增菜单模板
     */
    @Test
    public void addMenuTemplate() {
        boolean b = menuTemplateService.addMenuTemplate(new MenuTemplate());
        Assert.assertTrue(!b);
    }

    ;

    /**
     * 查询菜单配置模板是否启用的接口
     */
    @Test
    public void queryMenuTemplateIsOpen() {
        menuTemplateService.queryMenuTemplateIsOpen(new ArrayList<>());
        ArrayList<String> objects1 = new ArrayList<>();
        objects1.add("1");
        ArrayList<MenuTemplate> objects = new ArrayList<>();
        MenuTemplate menuTemplate = new MenuTemplate();
        objects.add(menuTemplate);
        new Expectations() {
            {
                menuTemplateDao.selectBatchIds((List) any);
                result = objects;
            }
        };
        menuTemplateService.queryMenuTemplateIsOpen(new ArrayList<>());
        menuTemplate.setTemplateStatus("1");
        new Expectations() {
            {
                menuTemplateDao.selectBatchIds((List) any);
                result = objects;
            }
        };
        menuTemplateService.queryMenuTemplateIsOpen(objects1);
    }
    /**
     * 将所有模板设置为禁用测试
     */
    @Test
    public void updateAllMenuTemplate() {
        Integer integer = menuTemplateService.updateAllMenuTemplate();
        Assert.assertTrue(integer == 0);
    }
}
