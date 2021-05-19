package com.fiberhome.filink.logserver.controller;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.logserver.bean.FilterTemplate;
import com.fiberhome.filink.logserver.service.TemplateService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * <p>
 * TemplateControllerTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
@RunWith(JMockit.class)
public class TemplateControllerTest {
    /**
     * 测试对象
     */
    @Tested
    private TemplateController templateController;


    @Injectable
    private TemplateService templateService;


    private FilterTemplate filterTemplateEmpty;
    private FilterTemplate filterTemplate;


    private QueryCondition queryCondition;

    @Before
    public void init() {
        filterTemplateEmpty = new FilterTemplate();

        filterTemplate = new FilterTemplate();
        filterTemplate.setId("id");
        filterTemplate.setName("模板1");
        filterTemplate.setRemark("备注");
        filterTemplate.setFilterValue("filterValue");

        queryCondition = new QueryCondition();
        queryCondition.setPageCondition(new PageCondition());
        queryCondition.setFilterConditions(new ArrayList<>());
    }

    @Test
    public void insertTemplate() {
        templateController.insertTemplate(filterTemplateEmpty);

        templateController.insertTemplate(filterTemplate);
    }

    @Test
    public void updateTemplate() {
        templateController.updateTemplate(filterTemplateEmpty);

        templateController.updateTemplate(filterTemplate);
    }

    @Test
    public void deleteTemplate() {
        templateController.deleteTemplate(filterTemplateEmpty);

        templateController.deleteTemplate(filterTemplate);
    }

    @Test
    public void queryTemplate() {
        templateController.queryTemplate("");

        templateController.queryTemplate("id");
    }

    @Test
    public void queryList() {
        templateController.queryList(null);

        templateController.queryList(queryCondition);
    }
}
