package com.fiberhome.filink.fdevice.controller.area;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDateFormatException;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.server_common.utils.I18nUtils;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 区域信息Controller测试类
 *
 * @author WH1807088
 * create on  2019/1/12
 */
@RunWith(JMockit.class)
public class AreaInfoControllerTest {
    /**
     * 测试对象 areaInfoController
     */
    @Tested
    private AreaInfoController areaInfoController;
    /**
     * Mock areaInfoService
     */
    @Injectable
    private AreaInfoService areaInfoService;
    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;
    @Mocked
    private RequestContextHolder requestContextHolder;
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;
    /**
     * areaInfo
     */
    private AreaInfo areaInfo;
    /**
     * map
     */
    private Map map = new HashMap();
    /**
     * list
     */
    private List list = new ArrayList();

    /**
     * 初始化
     */
    @Before
    public void setUp() {
        areaInfo = new AreaInfo();
        areaInfo.setAreaName("a");

        map.put(1, 1);

        list.add(1);

    }

    /**
     * 查询区域名是否存在测试
     */
    @Test
    public void queryAreaNameIsExist() {
        new Expectations() {
            {
                areaInfoService.queryAreaNameIsExist((AreaInfo) any);
                result = ResultUtils.success();
            }
        };
        Result result = areaInfoController.queryAreaNameIsExist(areaInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        areaInfoController.queryAreaNameIsExist(new AreaInfo());
    }

    /**
     * 区域详情是否可修改测试
     */
    @Test
    public void queryAreaDetailsCanChange() {
        areaInfoController.queryAreaDetailsCanChange("");
        areaInfoController.queryAreaDetailsCanChange("1");
        new Expectations() {
            {
                areaInfoService.queryAreaDetailsCanChange(anyString);
                result = true;
            }
        };
        areaInfoController.queryAreaDetailsCanChange("1");
    }

    /**
     * 新增区域测试
     */
    @Test
    public void addArea() {
        areaInfoController.addArea(new AreaInfo());
        new Expectations() {
            {
                areaInfoService.addArea((AreaInfo) any);
                result = ResultUtils.success();
            }
        };
        Result result = areaInfoController.addArea(areaInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        String lengthStr = "";
        for (int i = 0; i < 201; i++) {
            lengthStr += "a";
        }
        areaInfo.setAreaName(lengthStr);
        Result result1 = areaInfoController.addArea(areaInfo);
        Assert.assertTrue(result1.getCode() != 0);
        areaInfo.setAreaName("test");
        areaInfo.setRemarks(lengthStr);
        try {
            areaInfoController.addArea(areaInfo);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDateFormatException.class);
        }
    }

    /**
     * 查询分页区域列表测试
     */
    @Test
    public void areaListByPage() {
        new Expectations() {
            {
                areaInfoService.queryAreaListByItem((QueryCondition) any);
                result = ResultUtils.success();
            }
        };
        Result result = areaInfoController.areaListByPage(new QueryCondition());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * ID查询区域测试
     */
    @Test
    public void queryAreaById() {
        areaInfoController.queryAreaById("");
        new Expectations() {
            {
                areaInfoService.queryAreaById(anyString);
                result = ResultUtils.success();
            }
        };
        Result result = areaInfoController.queryAreaById("a");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 区域更新测试
     */
    @Test
    public void updateAreaById() {
        areaInfoController.updateAreaById(new AreaInfo());
        areaInfoController.updateAreaById(areaInfo);
        areaInfo.setAreaId("11");
        new Expectations() {
            {
                areaInfoService.updateAreaInfo((AreaInfo) any);
                result = ResultUtils.success();
            }
        };
        Result result = areaInfoController.updateAreaById(areaInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }


    /**
     * 设置区域设施测试
     */
    @Test
    public void setAreaDevice() {
        areaInfoController.setAreaDevice(new HashMap<>());
        new Expectations() {
            {
                areaInfoService.setAreaDevice((Map) any);
                result = true;
            }
        };
        Result result = areaInfoController.setAreaDevice(map);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    /**
     * 删除区域测试
     */
    @Test
    public void deleteAreaByIds() {
        areaInfoController.deleteAreaByIds(new ArrayList<>());
        new Expectations() {
            {
                areaInfoService.deleteAreaByIds((List) any);
                result = ResultUtils.success();
            }
        };
        Result result = areaInfoController.deleteAreaByIds(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 首页获取区域列表测试
     */
    @Test
    public void queryAreaListAll() {
        Result result = areaInfoController.queryAreaListAll();
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 删除区域部门关系
     */
    @Test
    public void deleteAreaDeptRelation() {
        Boolean aBoolean = areaInfoController.deleteAreaDeptRelation(new ArrayList<>());
        Assert.assertTrue(!aBoolean);
    }
}