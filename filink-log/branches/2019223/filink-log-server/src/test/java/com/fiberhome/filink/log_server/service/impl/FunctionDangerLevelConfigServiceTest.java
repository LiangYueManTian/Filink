package com.fiberhome.filink.log_server.service.impl;


import com.fiberhome.filink.log_server.dao.FunctionDangerLevelConfigDao;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * FunctionDangerLevelConfigService
 *
 * @author hedongwei@wistronits.com
 * create on  2019/1/24
 */
@RunWith(JMockit.class)
public class FunctionDangerLevelConfigServiceTest {
    /**
     * 自动注入 FunctionDangerLevelConfigDao
     */
    @Injectable
    private FunctionDangerLevelConfigDao functionDangerLevelConfigService;

    /**
     * 测试对象 FunctionDangerLevelConfigServiceImpl
     */
    @Tested
    private FunctionDangerLevelConfigServiceImpl functionDangerLevelConfigServiceImpl;

    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;


    /**
     * 获得危险级别
     */
    @Test
    public void getDangerLevelConfigByFunctionCode() {
        String functionCode = "";
        functionDangerLevelConfigServiceImpl.getDangerLevelConfigByFunctionCode(functionCode);

        String functionCodeInfo = "1000";
        functionDangerLevelConfigServiceImpl.getDangerLevelConfigByFunctionCode(functionCodeInfo);
    }


}