package com.fiberhome.filink.about.service.impl;

import com.fiberhome.filink.about.bean.About;
import com.fiberhome.filink.about.dao.AboutDao;
import com.fiberhome.filink.about.exception.AboutParamsException;
import com.fiberhome.filink.about.exception.AboutSystemException;
import com.fiberhome.filink.about.service.AboutService;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <p>
 * AboutServiceImplTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/6
 */
@RunWith(JMockit.class)
public class AboutServiceImplTest {
    /**
     * 测试对象
     */
    @Tested
    private AboutServiceImpl aboutService;
    /**
     * 注入 持久层
     */
    @Injectable
    private AboutDao aboutDao;

    @Mocked
    private About about;

    @Mocked
    private I18nUtils i18nUtils;

    @Mocked
    private RequestInfoUtils requestInfoUtils;

    /**
     * 测试getAbout()
     */
    @Test
    public void getAbout() {
        //查询不到
        new Expectations() {
            {
                aboutDao.getAbout();
                result = null;
            }
        };
        try {
            aboutService.getAbout();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getClass() == AboutSystemException.class);
        }

        //正常
        new Expectations() {
            {
                aboutDao.getAbout();
                result = about;
            }
        };
        Result result = aboutService.getAbout();
        Assert.assertTrue(!StringUtils.isEmpty(result));

    }

    /**
     * 测试updateAboutById()
     */
    @Test
    public void updateAboutById() {
        //查不到
        new Expectations() {
            {
                aboutDao.queryAboutById(anyString);
                result = null;
            }
        };
        try {
            aboutService.updateAboutById(about);
        } catch (Exception ex) {
            Assert.assertTrue(ex.getClass() == AboutSystemException.class);
        }

        //查到，更新出错
        new Expectations() {
            {
                aboutDao.queryAboutById(anyString);
                result = about;
                aboutDao.updateAboutById((About) any);
                result = 0;

            }
        };
        try {
            aboutService.updateAboutById(about);
        } catch (Exception ex) {
            Assert.assertTrue(ex.getClass() == AboutSystemException.class);
        }

        //一切正常
        new Expectations() {
            {
                aboutDao.queryAboutById(anyString);
                result = about;
                aboutDao.updateAboutById((About) any);
                result = 1;

            }
        };
        Result result = aboutService.updateAboutById(about);
        Assert.assertTrue(result.getCode() == 0);
    }

}
