package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.bean.TempAuthParameter;
import com.fiberhome.filink.userserver.bean.Tempauth;
import com.fiberhome.filink.userserver.service.TempauthService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class TempauthControllerTest {

    @Tested
    private TempauthController tempauthController;

    @Injectable
    private TempauthService tempauthService;


    @Test
    public void queryTempAuthByCondition() throws Exception {

        QueryCondition<TempAuthParameter> queryCondition = new QueryCondition<>();

        new Expectations(){
            {
                tempauthService.queryTempAuthByCondition(queryCondition);
                result = ResultUtils.success();
            }
        };
        Result result = tempauthController.queryTempAuthByCondition(queryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void audingTempAuthById() throws Exception {

        Tempauth tempauth = new Tempauth();
        new Expectations(){
            {
                tempauthService.audingTempAuthById(tempauth);
                result = ResultUtils.success();
            }
        };
        Result result = tempauthController.audingTempAuthById(tempauth);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void batchAudingTempAuthByIds() throws Exception {

        TempAuthParameter tempAuthParameter = new TempAuthParameter();
        tempAuthParameter.setAuditingDesc("123");
        tempAuthParameter.setAuthStatus(1);
        Tempauth tempauth = new Tempauth();
        tempauth.setAuthStatus(1);
        String[] idList = {"123"};
        tempAuthParameter.setIdList(idList);
        new Expectations(){
            {
                tempauthService.batchAudingTempAuthByIds((String[]) any,(Tempauth) any);
                result = ResultUtils.success();
            }
        };
        Result result = tempauthController.batchAudingTempAuthByIds(tempAuthParameter);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deleteTempAuthById() throws Exception {

        String id = "123";
        new Expectations(){
            {
                tempauthService.deleteTempAuthById(id);
                result = ResultUtils.success();
            }
        };
        Result result = tempauthController.deleteTempAuthById(id);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void batchDeleteTempAuth() throws Exception {

        String[] ids = {"123"};
        new Expectations(){
            {
                tempauthService.batchDeleteTempAuth(ids);
                result = ResultUtils.success();
            }
        };
        Result result = tempauthController.batchDeleteTempAuth(ids);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void addTempAuth() throws Exception {

        Tempauth tempauth = new Tempauth();
        new Expectations(){
            {
                tempauthService.addTempAuth(tempauth);
                result = ResultUtils.success();
            }
        };
        Result result = tempauthController.addTempAuth(tempauth);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryTempAuthById() throws Exception {

        String id = "123";
        new Expectations(){
            {
                tempauthService.queryTempAuthById(id);
                result = ResultUtils.success();
            }
        };
        Result result = tempauthController.queryTempAuthById(id);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

}