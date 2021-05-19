package com.fiberhome.filink.logserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logserver.bean.FilterTemplate;
import com.fiberhome.filink.logserver.dao.TemplateDao;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
@RunWith(JMockit.class)
public class TemplateServiceImplTest {
    @Tested
    private TemplateServiceImpl templateService;

    @Injectable
    private TemplateDao templateDao;
    @Injectable
    private LogProcess logProcess;
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    private FilterTemplate filterTemplate;
    private QueryCondition queryCondition;

    @Mocked
    private MpQueryHelper mpQueryHelper;

    @Injectable
    private UserFeign userFeign;


    @Before
    public void init() {
        filterTemplate = new FilterTemplate();
        filterTemplate.setId("id");
        filterTemplate.setName("模板1");
        filterTemplate.setRemark("备注");
        filterTemplate.setFilterValue("filterValue");

        queryCondition = new QueryCondition();
        queryCondition.setPageCondition(new PageCondition());
        queryCondition.setFilterConditions(new ArrayList<>());
        SortCondition sortCondition = new SortCondition();
        sortCondition.setSortField("aa");
        queryCondition.setSortCondition(sortCondition);

    }


    @Test
    public void insertTemplate() {
        templateService.insertTemplate(filterTemplate);
    }

    @Test
    public void updateTemplate() {
        templateService.updateTemplate(filterTemplate);

        new Expectations() {
            {
                templateDao.queryOne(anyString);
                result = null;
            }
        };
        templateService.updateTemplate(filterTemplate);
    }

    @Test
    public void deleteTemplate() {
        templateService.deleteTemplate(filterTemplate);
        new Expectations() {
            {
                templateDao.queryOne(anyString);
                result = null;
            }
        };
        templateService.deleteTemplate(filterTemplate);

    }

    @Test
    public void queryTemplate() {
        templateService.queryTemplate("id");
    }

    @Test
    public void queryList() {

        new Expectations() {
            {
                templateDao.selectPage((Page) any, (EntityWrapper) any);
                List<FilterTemplate> filterTemplateList = new ArrayList<>();
                FilterTemplate template = new FilterTemplate();
                template.setCreateUser("1");
                filterTemplateList.add(template);

                template = new FilterTemplate();
                template.setCreateUser("2");
                filterTemplateList.add(template);
                result = filterTemplateList;


                userFeign.queryUserByIdList((List<String>) any);
                List<User> userInfoList = new ArrayList<>();
                User userInfo = new User();
                userInfo.setId("1");
                userInfo.setUserName("admin");
                userInfoList.add(userInfo);
                userInfo = new User();
                userInfo.setId("2");
                userInfo.setUserName("admin");
                userInfoList.add(userInfo);
                result = userInfoList;
            }
        };
        templateService.queryList(queryCondition);
    }
}
