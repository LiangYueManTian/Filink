package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.bean.DeviceInfo;
import com.fiberhome.filink.userserver.bean.UnifyAuthParameter;
import com.fiberhome.filink.userserver.bean.UnifyAuth;
import com.fiberhome.filink.userserver.bean.UserAuthInfo;
import com.fiberhome.filink.userserver.service.UnifyAuthService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class UnifyauthControllerTest {

    @Tested
    private UnifyAuthController unifyauthController;

    @Injectable
    private UnifyAuthService unifyauthService;


    @Test
    public void addUnifyAuth() throws Exception {

        UnifyAuth unifyauth = new UnifyAuth();
        new Expectations(){
            {
                unifyauthService.addUnifyAuth(unifyauth);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.addUnifyAuth(unifyauth);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUnifyAuthByCondition() throws Exception {

        QueryCondition<UnifyAuthParameter> queryCondition = new QueryCondition<>();
        new Expectations(){
            {
                unifyauthService.queryUnifyAuthByCondition(queryCondition);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.queryUnifyAuthByCondition(queryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void modifyUnifyAuth() throws Exception {

        UnifyAuth unifyauth = new UnifyAuth();
        new Expectations(){
            {
                unifyauthService.modifyUnifyAuth(unifyauth);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.modifyUnifyAuth(unifyauth);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUnifyAuthById() throws Exception {

        String id = "123";
        new Expectations(){
            {
                unifyauthService.queryUnifyAuthById(id);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.queryUnifyAuthById(id);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deleteUnifyAuthById() throws Exception {

        String id = "123";
        new Expectations(){
            {
                unifyauthService.deleteUnifyAuthById(id);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.deleteUnifyAuthById(id);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void batchDeleteUnifyAuth() throws Exception {

        String[] ids = {"123"};
        new Expectations(){
            {
                unifyauthService.batchDeleteUnifyAuth(ids);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.batchDeleteUnifyAuth(ids);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void batchModifyUnifyAuthStatus() throws Exception {

        UnifyAuthParameter unifyAuthParameter = new UnifyAuthParameter();
        new Expectations(){
            {
                unifyauthService.batchModifyUnifyAuthStatus(unifyAuthParameter);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.batchModifyUnifyAuthStatus(unifyAuthParameter);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserAuthInfoById() throws Exception {

        new Expectations(){
            {
                unifyauthService.queryUserAuthInfoById();
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.queryUserAuthInfoById();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryAuthInfoByUserIdAndDeviceAndDoor() throws Exception {

        UserAuthInfo userAuthInfo = new UserAuthInfo();
        new Expectations(){
            {
                unifyauthService.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
            }
        };
        unifyauthController.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
    }

    @Test
    public void queryAuthByUserAndDeviceAndDoorInfo() throws Exception {

        UserAuthInfo userAuthInfo = new UserAuthInfo();
        new Expectations(){
            {
                unifyauthService.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
            }
        };
        unifyauthController.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
    }

    @Test
    public void deleteAuthByDevice() throws Exception {

        DeviceInfo deviceInfo = new DeviceInfo();
        new Expectations(){
            {
                unifyauthService.deleteAuthByDevice(deviceInfo);
            }
        };
        unifyauthController.deleteAuthByDevice(deviceInfo);
    }

    @Test
    public void queryAuthByName() throws Exception {

        UnifyAuth unifyauth = new UnifyAuth();
        new Expectations(){
            {
                unifyauthService.queryAuthByName(unifyauth);
                result = ResultUtils.success();
            }
        };
        Result result = unifyauthController.queryAuthByName(unifyauth);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

}