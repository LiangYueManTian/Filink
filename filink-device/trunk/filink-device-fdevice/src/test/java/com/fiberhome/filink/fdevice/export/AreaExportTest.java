package com.fiberhome.filink.fdevice.export;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dto.AreaInfoDto;
import com.fiberhome.filink.fdevice.service.area.impl.AreaInfoServiceImpl;
import com.fiberhome.filink.userapi.bean.Department;
import mockit.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


/**
 * AreaExportTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/8
 */
@RunWith(MockitoJUnitRunner.class)
public class AreaExportTest {

    @InjectMocks
    private AreaExport areaExport;
    @Mock
    private AreaInfoDao areaInfoDao;
    @Mock
    private AreaInfoServiceImpl areaInfoService;

    private QueryCondition<AreaInfoDto> queryCondition = new QueryCondition<>();


    @Before
    public void setUp() {
        SortCondition sortCondition = new SortCondition();
        sortCondition.setSortField("1");
        queryCondition.setSortCondition(sortCondition);

        List<FilterCondition> filterConditions = new ArrayList<>();
        List<String> filterValue = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setOperator("gt");
        filterCondition.setFilterField("areaId");
        filterValue.add("beijing");
        filterCondition.setFilterValue(filterValue);
        filterConditions.add(filterCondition);

        FilterCondition filterCondition1 = new FilterCondition();
        filterCondition1.setOperator("gt");
        filterCondition1.setFilterField("deviceType");
        filterValue.clear();
        filterValue.add("030");
        filterCondition1.setFilterValue(filterValue);
        filterConditions.add(filterCondition1);
        queryCondition.setFilterConditions(filterConditions);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(6);
        pageCondition.setBeginNum(0);
        queryCondition.setPageCondition(pageCondition);

        AreaInfoDto areaInfoDto = new AreaInfoDto();
        queryCondition.setBizCondition(areaInfoDto);
    }

    @Test
    public void queryData() {
        List<Department> departments = new ArrayList<>();
        TransmittableThreadLocal<List<Department>> tlDepartmentLists = new TransmittableThreadLocal<>();
        AreaInfoServiceImpl.tlDepartmentList = tlDepartmentLists;
        new Expectations(AreaInfoServiceImpl.class) {
            {
                AreaInfoServiceImpl.tlDepartmentList.set((List<Department>) any);
            }
        };
        new Expectations() {
            {
                areaInfoService.getDepartmentList();
                result = departments;
            }
        };
        List<AreaInfo> areaInfos = new ArrayList<>();
        when(areaInfoDao.queryAreaListByItem(any())).thenReturn(areaInfos);
        List<AreaInfoTree> areaInfoTreeList = new ArrayList<>();
        AreaInfoTree areaInfoTree = new AreaInfoTree();
        Set<String> stringSet = new HashSet<>();
        stringSet.add("x");
        areaInfoTree.setAccountabilityUnit(stringSet);
        StringBuilder accountabilityUnitName = new StringBuilder();
        accountabilityUnitName.append("X");
        areaInfoTree.setAccountabilityUnitName(accountabilityUnitName);
        areaInfoTreeList.add(areaInfoTree);
        when(areaInfoService.setDeptIdByAreaIds(any())).thenReturn(areaInfoTreeList);
        areaExport.queryData(queryCondition);
    }

    @Test
    public void queryCount() {
        when(areaInfoDao.queryAreaListByItemCount(any())).thenReturn(new ArrayList<>());
        areaExport.queryCount(queryCondition);
    }
}