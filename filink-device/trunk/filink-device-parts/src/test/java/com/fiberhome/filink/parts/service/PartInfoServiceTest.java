package com.fiberhome.filink.parts.service;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/28 14:04
 * @Description: com.fiberhome.filink.parts.service
 * @version: 1.0
 */
public class PartInfoServiceTest {
    @InjectMocks
    private PartInfoService partInfoService;

    public static final String DEFAULT_RESULT = "i18n_result";

    @Test
    public void convertObjectToUser() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = DEFAULT_RESULT;
            }
        };
        try {
            PartInfoService.convertObjectToUser(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertSame(e.getMessage(), DEFAULT_RESULT);
        }

        User user = new User();
        Object userObj = JSONObject.toJSON(user);
        try {
            PartInfoService.convertObjectToUser(userObj);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertSame(e.getMessage(), DEFAULT_RESULT);
        }

        user.setRole(new Role());
        User user1 = PartInfoService.convertObjectToUser(user);
        Assert.assertTrue(user1 != null);
    }
}
