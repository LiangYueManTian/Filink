package com.fiberhome.filink.menu.service.impl;

import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.menu.dao.MenuInfoDao;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;


/**
 * MenuInfoServiceImplTest
 */
@RunWith(JMockit.class)
public class MenuInfoServiceImplTest {
    /**
     * 要测试的
     */
    @Tested
    private MenuInfoServiceImpl menuInfoService;
    /**
     * 自动注入menuInfoDao
     */
    @Injectable
    private MenuInfoDao menuInfoDao;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
    }

    /**
     * 查询默认菜单
     */
    @Test
    public void getDefaultMenuTemplate() {
        menuInfoService.getDefaultMenuTemplate();
    }

    /**
     * 查询数据库中包含的传入id集合的数据量
     */
    @Test
    public void selectCountByIds() {
        menuInfoService.selectCountByIds(new ArrayList<>());
    }
}
