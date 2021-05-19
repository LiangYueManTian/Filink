package com.fiberhome.filink.fdevice.controller.area;


import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.constant.area.AreaResultCodeConstant;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * AreaInfoControllerTest
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
     * queryAreaNameIsExist()
     */
    @Test
    public void queryAreaNameIsExist() {
        AreaInfo areaInfo = new AreaInfo();
        Result result1 = areaInfoController.queryAreaNameIsExist(areaInfo);
        Assert.assertTrue(result1.getCode() == AreaResultCodeConstant.AREA_NAME_NULL);
        areaInfo.setAreaName("dsad!@#$   ++__  ~~~");
        areaInfoController.queryAreaNameIsExist(areaInfo);
        new Expectations() {
            {
                areaInfoService.queryAreaNameIsExist((AreaInfo) any);
                result = ResultUtils.success();
            }
        };
        Result result = areaInfoController.queryAreaNameIsExist(this.areaInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

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
     * addArea
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
    }

    /**
     * areaListByPage
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
     * queryAreaById
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
     * updateAreaById
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
     * setAreaDevice
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
     * deleteAreaByIds
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

    @Test
    public void queryAreaListForPageSelection() {
        Result result = areaInfoController.queryAreaListForPageSelection();
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
    }

    /**
     * 删除区域部门关系
     */
    @Test
    public void deleteAreaDeptRelation() {
        List<String> deptIds = new ArrayList<>();
        Boolean aBoolean = areaInfoController.deleteAreaDeptRelation(deptIds);
        Assert.assertTrue(!aBoolean);
        deptIds.add("aa");
        Assert.assertTrue(!areaInfoController.deleteAreaDeptRelation(deptIds));
    }

    @Test
    public void selectAreaInfoByIds() {
        List<String> areaIds = new ArrayList<>();
        List<AreaInfoForeignDto> areaInfoForeignDtoList = areaInfoController.selectAreaInfoByIds(areaIds);
        Assert.assertTrue(areaInfoForeignDtoList == null);
        areaIds.add("111");
        List<AreaInfoForeignDto> areaInfoForeignDtoList2 = areaInfoController.selectAreaInfoByIds(areaIds);
        Assert.assertTrue(areaInfoForeignDtoList2 != null);
    }

    @Test
    public void selectAreaInfoByIdsForView() {
        List<String> areaIds = new ArrayList<>();
        Result result = areaInfoController.selectAreaInfoByIdsForView(areaIds);
        Assert.assertTrue(result == null);
        areaIds.add("111");
        Result result1 = areaInfoController.selectAreaInfoByIdsForView(areaIds);
        Assert.assertTrue(result1 != null);
    }

    @Test
    public void selectAreaInfoByDeptIds() {
        List<String> areaIds = new ArrayList<>();
        List areaInfoForeignDtoList = areaInfoController.selectAreaInfoByDeptIds(areaIds);
        Assert.assertTrue(areaInfoForeignDtoList == null);
        areaIds.add("111");
        List areaInfoForeignDtoList2 = areaInfoController.selectAreaInfoByDeptIds(areaIds);
        Assert.assertTrue(areaInfoForeignDtoList2 != null);
    }

    @Test
    public void exportData() {
        ExportDto exportDto = new ExportDto();
        Result result = areaInfoController.exportData(exportDto);
        Assert.assertTrue(result.getCode() == AreaResultCodeConstant.PARAM_NULL);
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditions = new ArrayList<>();
        queryCondition.setFilterConditions(filterConditions);
        List<ColumnInfo> columnInfos = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfos.add(columnInfo);
        exportDto.setColumnInfoList(columnInfos);
        exportDto.setExcelType(1);
        exportDto.setQueryCondition(queryCondition);
        Result result2 = areaInfoController.exportData(exportDto);
        Assert.assertTrue(result2.getCode() == ResultCode.SUCCESS);
    }

    @Test
    public void selectForeignAreaInfoForPageSelection() {
        Result result = areaInfoController.selectForeignAreaInfoForPageSelection(new ArrayList<>());
        Assert.assertTrue(result != null);
    }

    @Test
    public void selectSimultaneousForPageSelection() {
        Result result = areaInfoController.selectSimultaneousForPageSelection();
        Assert.assertTrue(result != null);
    }

    @Test
    public void selectAreaIdsByDeptIds() {
        List<String> areaIds = new ArrayList<>();
        List list = areaInfoController.selectAreaIdsByDeptIds(areaIds);
        Assert.assertTrue(list == null);
        areaIds.add("111");
        List list2 = areaInfoController.selectAreaIdsByDeptIds(areaIds);
        Assert.assertTrue(list2 != null);
    }

    @Test
    public void selectAreaDeptInfoByDeptIds() {
        List<String> areaIds = new ArrayList<>();
        List list = areaInfoController.selectAreaDeptInfoByDeptIds(areaIds);
        Assert.assertTrue(list == null);
        areaIds.add("111");
        List list2 = areaInfoController.selectAreaDeptInfoByDeptIds(areaIds);
        Assert.assertTrue(list2 != null);
    }

    @Test
    public void selectAreaDeptInfoByAreaIdsForPageSelection() {
        List<String> areaIds = new ArrayList<>();
        Result result = areaInfoController.selectAreaDeptInfoByAreaIdsForPageSelection(areaIds);
        Assert.assertTrue(result.getCode() == AreaResultCodeConstant.PARAM_NULL);
        areaIds.add("111");
        Result result1 = areaInfoController.selectAreaDeptInfoByAreaIdsForPageSelection(areaIds);
        Assert.assertTrue(result1 != null);
    }
}