package com.fiberhome.filink.log_server.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.log_server.service.FunctionDangerLevelConfigService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * FunctionDangerLevelConfigController
 *
 * @author hedongwei@wistronits.com
 * create on  2019/1/24
 */
@RunWith(JMockit.class)
public class FunctionDangerLevelConfigControllerTest {

    /**
     * 测试对象 FunctionDangerLevelConfigController
     */
    @Tested
    private FunctionDangerLevelConfigController functionDangerLevelConfigController;

    /**
     * 自动注入functionDangerLevelConfigService
     */
    @Injectable
    private FunctionDangerLevelConfigService functionDangerLevelConfigService;

    /**
     * 获取危险级别
     */
    @Test
    public void getDangerLevel() {
        Result resultInfo = functionDangerLevelConfigController.getDangerLevel("1000");
        Assert.assertTrue(resultInfo.getCode() == 0);

        Result resultInfo1 = functionDangerLevelConfigController.getDangerLevel("");
        Assert.assertTrue(resultInfo1.getCode() == 0);
    }
}
