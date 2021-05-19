package com.fiberhome.filink.map.bean;

import com.fiberhome.filink.map.exception.MapExceptionHandler;
import com.fiberhome.filink.map.exception.MapParamsException;
import com.fiberhome.filink.map.exception.MapSystemException;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 *
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
@RunWith(JMockit.class)
public class BeanTest {

    @Test
    public void testBaiduArea(){
        BaiduArea baiduArea = new BaiduArea();
        baiduArea.getAreaId();
        baiduArea.getAreaName();
        baiduArea.getAreaCode();
        baiduArea.getBoundary();
        baiduArea.getCenter();
        baiduArea.getCityCode();
        baiduArea.getLevel();
        baiduArea.getParentId();
        baiduArea.pkVal();
    }

    @Test
    public void testMapExceptionHandler(){
        MapExceptionHandler mapExceptionHandler = new MapExceptionHandler();
        mapExceptionHandler.handlerMapParamsException(new MapParamsException());
        mapExceptionHandler.handlerMapSystemException(new MapSystemException());
    }

}
