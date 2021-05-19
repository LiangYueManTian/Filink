package com.fiberhome.filink.about.controller;

import com.fiberhome.filink.about.bean.About;
import com.fiberhome.filink.about.exception.AboutParamsException;
import com.fiberhome.filink.about.service.AboutService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * AboutControllerTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/6
 */
@RunWith(JMockit.class)
public class AboutControllerTest {

    /**
     * 测试对象protocolController
     */
    @Tested
    private AboutController aboutController;

    /**
     * about逻辑层
     */
    @Injectable
    private AboutService aboutService;

    /**
     * 测试getAbout()
     */
    @Test
    public void getAbout(){
        new Expectations() {
            {
                aboutService.getAbout();
                result = ResultUtils.success();
            }
        };
        Result result = aboutController.getAbout();
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 测试updateAbout()
     */
    @Test
    public void updateAbout(){

        About about= new About();
        try {
            aboutController.updateAbout(about);
        }catch (Exception e){
            Assert.assertTrue(e.getClass() == AboutParamsException.class);
        }
        about.setAboutId("dasd22154024dascdsvah1231dsa");
        about.setLicenseAuthorize("1");
        new Expectations() {
            {
                aboutService.updateAboutById((About) any);
                result = ResultUtils.success();
            }
        };
        Result result =aboutController.updateAbout(about);
        Assert.assertTrue(result.getCode() == 0);
    }
}


