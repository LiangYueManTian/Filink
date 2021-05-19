package com.fiberhome.filink.map.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.map.bean.BaiduArea;
import com.fiberhome.filink.map.exception.MapParamsException;
import com.fiberhome.filink.map.service.BaiduAreaService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * 地区码表 前端控制器测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-07
 */
@RunWith(JMockit.class)
public class BaiduAreaControllerTest {
    /**
     * 测试对象
     */
    @Tested
    private BaiduAreaController baiduAreaController;
    /**
     * 注入地图逻辑层
     */
    @Injectable
    private BaiduAreaService baiduAreaService;

    private BaiduArea baiduArea;


    @Before
    public void setUp() {
        baiduArea = new BaiduArea();
        baiduArea.setAreaId(2);
        baiduArea.setAreaName("adsd");
    }

    /**
     * test 根据名称查询地区
     */
    @Test
    public void queryAreaByName() {
        try {
            baiduAreaController.queryAreaByName(null);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapParamsException.class);
        }
        new Expectations() {
            {
                baiduAreaService.queryAreaByName(anyString);
                result = ResultUtils.success();
            }
        };
        Result result = baiduAreaController.queryAreaByName(baiduArea);
        Assert.assertTrue(result.getCode() == 0);

    }

    /**
     * test 根据名称查询其子地区
     */
    @Test
    public void querySonAreaByName() {
        try {
            baiduAreaController.querySonAreaByName(null);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapParamsException.class);
        }
        new Expectations() {
            {
                baiduAreaService.querySonAreaByName(anyString);
                result = ResultUtils.success();
            }
        };
        Result result = baiduAreaController.querySonAreaByName(baiduArea);
        Assert.assertTrue(result.getCode() == 0);

    }

    /**
     * 测试 根据id更新地区
     */
    @Test
    public void updateAreaById() {
        BaiduArea area = null;
        try {
            baiduAreaController.updateAreaById(area);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapParamsException.class);
        }
        area = new BaiduArea();
        area.setAreaId(1);
        new Expectations() {
            {
                baiduAreaService.updateAreaById((BaiduArea) any);
                result = ResultUtils.success();
            }
        };
        Result result = baiduAreaController.updateAreaById(area);
        Assert.assertTrue(result.getCode() == 0);


    }

    @Test
    public void queryAllProvince() {
        baiduAreaController.queryAllProvince();
    }

    @Test
    public void queryAllCityInfo() {
        baiduAreaController.queryAllCityInfo();
    }


}
