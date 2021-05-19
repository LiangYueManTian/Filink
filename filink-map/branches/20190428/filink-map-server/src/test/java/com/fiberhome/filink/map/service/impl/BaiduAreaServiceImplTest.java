package com.fiberhome.filink.map.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.map.bean.BaiduArea;
import com.fiberhome.filink.map.dao.BaiduAreaDao;
import com.fiberhome.filink.map.exception.MapSystemException;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * <p>
 * 地区码表 服务实现测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-07
 */
@RunWith(JMockit.class)
public class BaiduAreaServiceImplTest {
    /**
     * 测试对象
     */
    @Tested
    private BaiduAreaServiceImpl baiduAreaService;
    /**
     * 注入地图持久层
     */
    @Injectable
    private BaiduAreaDao baiduAreaDao;

    @Mocked
    private I18nUtils i18nUtils;


    @Mocked
    private BaiduArea baiduArea;

    @Mocked
    List<BaiduArea> baiduAreas;

    /**
     * 测试根据名称查看地区
     */
    @Test
    public void queryAreaByName() {
         String areaName ="湖北省";
        //系统异常
        new Expectations() {
            {
                baiduAreaDao.queryAreaByName(anyString);
                result = null;
            }
        };
        try {
            baiduAreaService.queryAreaByName(areaName);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapSystemException.class);
        }

        //正常
        new Expectations() {
            {
                baiduAreaDao.queryAreaByName(anyString);
                result = baiduArea;
            }
        };
        Result result = baiduAreaService.queryAreaByName(areaName);
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 测试根据名称查看其子地区
     */
    @Test
    public void querySonAreaByName() {
         String areaName ="武汉市";
        //系统异常
        new Expectations() {
            {
                baiduAreaDao.querySonAreaByName(anyString);
                result = null;
            }
        };
        try {
            baiduAreaService.querySonAreaByName(areaName);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapSystemException.class);
        }

        //正常
        new Expectations() {
            {
                baiduAreaDao.querySonAreaByName(anyString);
                result = baiduArea;
            }
        };
        Result result = baiduAreaService.querySonAreaByName(areaName);
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 测试 根据id更新地区

     */
    @Test
    public void updateAreaById(){
        //系统异常
        new Expectations() {
            {
                baiduAreaDao.updateById((BaiduArea)any);
                result = 0;
            }
        };
        try {
            baiduAreaService.updateAreaById(baiduArea);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapSystemException.class);
        }

        //正常
        new Expectations() {
            {
                baiduAreaDao.updateById((BaiduArea)any);
                result = 1;
            }
        };
        Result result = baiduAreaService.updateAreaById(baiduArea);
        Assert.assertTrue(result.getCode() == 0);
    }


    @Test
    public void queryAllProvince(){
        //系统异常
        new Expectations() {
            {
                baiduAreaDao.queryAllProvince();
                result = null;
            }
        };
        try {
            baiduAreaService.queryAllProvince();
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapSystemException.class);
        }

        new Expectations() {
            {
                baiduAreaDao.queryAllProvince();
                result = baiduAreas;
            }
        };
            baiduAreaService.queryAllProvince();

    }

    @Test
    public void queryAllCityInfo(){
        //系统异常
        new Expectations() {
            {
                baiduAreaDao.queryAllCityInfo();
                result = null;
            }
        };
        try {
            baiduAreaService.queryAllCityInfo();
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == MapSystemException.class);
        }

        new Expectations() {
            {
                baiduAreaDao.queryAllCityInfo();
                result = baiduAreas;
            }
        };
            baiduAreaService.queryAllCityInfo();

    }
}
