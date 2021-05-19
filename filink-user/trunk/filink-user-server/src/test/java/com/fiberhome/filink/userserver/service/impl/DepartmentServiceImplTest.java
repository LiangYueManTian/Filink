package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaDeptInfo;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.DepartmentFeignDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.stream.UpdateUserStream;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JMockit.class)
public class DepartmentServiceImplTest {

    @Tested
    private DepartmentServiceImpl departmentService;


    @Injectable
    private DepartmentDao departmentDao;

    @Injectable
    private DepartmentFeignDao departmentFeignDao;

    @Injectable
    private AreaFeign areaFeign;

    @Injectable
    private LogProcess logProcess;

    @Injectable
    private UserDao userDao;

    @Injectable
    private UpdateUserStream updateUserStream;

    @Injectable
    private ProcBaseFeign procBaseFeign;

    @Injectable
    private AlarmCurrentFeign alarmCurrentFeign;

    @Injectable
    private InspectionTaskFeign inspectionTaskFeign;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Mocked
    private RedisUtils redisUtils;

    @Test
    public void queryDeptInfoById() throws Exception {

        String deptId = "1";
        Department department = new Department();
        department.setId("1");

        new Expectations() {
            {
                departmentDao.selectById("1");
                result = department;
            }
        };
        departmentService.queryDeptInfoById("1");
    }

    @Test
    public void queryDeptInfoById1() throws Exception {

        String deptId = "1";
        Department department = new Department();
        department.setId("1");

        Department department2 = new Department();
        department.setId("2");

        Department department1 = new Department();
        department1.setId("1");
        department1.setDeptFatherId("2");


        new Expectations() {
            {
                departmentDao.selectById("1");
                result = department1;
            }
        };

        new Expectations() {
            {
                departmentDao.selectById("2");
                result = department2;
            }
        };

        departmentService.queryDeptInfoById("1");
    }

    @Test
    public void addDept() throws Exception {

        Department department = new Department();
        department.setId("123");

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new Expectations() {
            {
                departmentDao.insert(department);
                result = 1;
            }
        };
        try {
            departmentService.addDept(department);
        } catch (Exception e) {
        }
        new Expectations() {
            {
                RedisUtils.lockWithTimeout(anyString, anyInt, anyInt);
                result = "test";
            }
        };
        departmentService.addDept(department);
    }

    @Test
    public void updateDept() throws Exception {
        Department department = new Department();
        department.setId("123");
        department.setDeptLevel("1");


        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new Expectations() {
            {
                departmentDao.selectById(anyString);
                result = null;
            }
        };
        departmentService.updateDept(department);

        new Expectations() {
            {
                departmentDao.selectById("123");
                result = department;
            }
        };

        department.setDeptName("234");
        new Expectations() {
            {
                departmentDao.updateById(department);
                result = 1;
            }
        };

        try {
            departmentService.updateDept(department);
        } catch (Exception e) {
        }
        Department department2 = new Department();
        department2.setId("123");
        department2.setDeptLevel("1");
        department2.setDeptFatherId("testId2");
        department.setDeptFatherId("testId");
        List<Department> departmentList = new ArrayList<>();
        departmentList.add(department);
        new Expectations() {
            {
                departmentDao.queryDeptByParentIds((String[]) any);
                result = departmentList;
            }
        };
        departmentService.updateDept(department2);
        department2.setDeptFatherId(null);
        departmentService.updateDept(department2);
        department2.setDeptFatherId("testId");
        department.setDeptFatherId(null);
        departmentService.updateDept(department2);
    }

    @Test
    public void deleteDept() throws Exception {
        Parameters parameters = new Parameters();
        departmentService.deleteDept(parameters);
        String[] test = new String[]{"1", "2", "3"};
        parameters.setFirstArrayParameter(test);
        List<Department> departments = new ArrayList<>();
        Department department = new Department();
        departments.add(department);
        new Expectations() {
            {
                departmentDao.queryDeptByParentIds((String[]) any);
                result = departments;
            }
        };
        departmentService.deleteDept(parameters);
        departments.clear();
        List<User> userList = new ArrayList<>();
        User user = new User();
        userList.add(user);
        new Expectations() {
            {
                userDao.queryUserByDepts((String[]) any);
                result = userList;
            }
        };
        departmentService.deleteDept(parameters);
        userList.clear();
        Map map = new HashMap();
        map.put("test", "test");
        new Expectations() {
            {
                procBaseFeign.queryProcIdListByDeptIds((List) any);
                result = map;
            }
        };
        departmentService.deleteDept(parameters);
        map.clear();
        boolean hasAlarmCurrent = true;
        new Expectations() {
            {
                alarmCurrentFeign.queryAlarmDepartmentFeign((List) any);
                result = hasAlarmCurrent;
            }
        };
        departmentService.deleteDept(parameters);
        new Expectations() {
            {
                alarmCurrentFeign.queryAlarmDepartmentFeign((List) any);
                result = false;
            }
        };
        Map mapHasInspection = new HashMap();
        mapHasInspection.put("test", "test");
        new Expectations() {
            {
                inspectionTaskFeign.queryInspectionTaskListByDeptIds((List) any);
                result = mapHasInspection;
            }
        };
        departmentService.deleteDept(parameters);
        mapHasInspection.clear();
        try {
            departmentService.deleteDept(parameters);
        } catch (Exception e) {
        }
        new Expectations() {
            {
                departmentDao.deleteDepartment((String[]) any);
                result = 3;
            }
        };
        departmentService.deleteDept(parameters);
        List<Department> departList = new ArrayList<>();
        departList.add(department);
        new Expectations() {
            {
                departmentDao.selectBatchIds((List) any);
                result = departList;
            }
        };
        departmentService.deleteDept(parameters);
    }

    @Test
    public void queryFullDepartMent() throws Exception {

        Department department = new Department();
        department.setId("123");
        List<Department> deptList = new ArrayList<>();
        deptList.add(department);

        new Expectations() {
            {
                departmentDao.selectFullDepartMent("123");
                result = deptList;
            }
        };
        departmentService.queryFullDepartMent("123");
    }

    @Test
    public void queryAllDepartment() throws Exception {

        DepartmentFeign departmentFeign = new DepartmentFeign();
        departmentFeign.setId("123");
        List<DepartmentFeign> deptList = new ArrayList<>();
        deptList.add(departmentFeign);

        new Expectations() {
            {
                departmentFeignDao.queryAllDepartmentFeign();
                result = deptList;
            }
        };
        departmentService.queryAllDepartment();

    }

    @Test
    public void queryTotalDepartment() throws Exception {

        Department dept = new Department();
        dept.setId("456");
        List<Department> deptList = new ArrayList<>();
        deptList.add(dept);

        new Expectations() {
            {
                departmentDao.queryAllDepartment();
                result = deptList;
            }
        };
        departmentService.queryTotalDepartment();
    }

    @Test
    public void queryDepartmentList() throws Exception {
        QueryCondition<DepartmentParamter> departmentParamterQueryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        departmentParamterQueryCondition.setPageCondition(pageCondition);
        departmentService.queryDepartmentList(departmentParamterQueryCondition);
    }

    @Test
    public void queryDepartmenttByField() throws Exception {

        DepartmentParamter departmentParamter = new DepartmentParamter();
        departmentParamter.setAddress("123");
        Department department = new Department();
        List<Department> departmentList = new ArrayList<>();
        department.setId("123");
        department.setAddress("123");
        departmentList.add(department);
        new Expectations() {
            {
                departmentDao.verifyDepartmentByField(departmentParamter);
                result = departmentList;
            }
        };

        departmentService.queryDepartmenttByField(departmentParamter);
    }

    @Test
    public void queryDepartmentFeignByIdTest() {
        departmentService.queryDepartmentFeignById(new ArrayList<>());
    }

    @Test
    public void conditionDepartment() {

        Department department = new Department();
        List<Department> departmentList = new ArrayList<>();
        department.setId("123");
        department.setAddress("123");
        departmentList.add(department);

        new Expectations() {
            {
                departmentDao.queryToltalDepartment();
                result = departmentList;
            }
        };

        departmentService.conditionDepartment();
    }

    @Test
    public void queryAllDepartmentForPageSelectionTest() {
        List<String> areaIds = new ArrayList<>();
        Result<List<Object>> sADResult = new Result<>();
        List<Object> data = new ArrayList<>();
        AreaDeptInfo areaDeptInfo = new AreaDeptInfo();
        areaDeptInfo.setDeptId("testId");
        data.add(areaDeptInfo);
        sADResult.setData(data);
        new Expectations() {
            {
                areaFeign.selectAreaDeptInfoByAreaIdsForPageSelection((List) any);
                result = sADResult;
            }
        };
        List<DepartmentFeign> departmentFeigns = new ArrayList<>();
        DepartmentFeign departmentFeign = new DepartmentFeign();
        departmentFeign.setId("testId");
        departmentFeigns.add(departmentFeign);
        new Expectations(){
            {
                departmentFeignDao.queryAllDepartmentFeign();
                result = departmentFeigns;
            }
        };
        departmentService.queryAllDepartmentForPageSelection(areaIds);

    }
    @Test
    public void queryDepartmentFeignByNameTest() {
        departmentService.queryDepartmentFeignByName("test");
    }

    @Test
    public void queryDepartTreeTest() {
        departmentService.queryDepartTree("test");
    }

    @Test
    public void departmentListToTreeTest() {
        List<Department> departmentList = new ArrayList<>();
        Department department = new Department();
        departmentList.add(department);
        List<Department> departmentListAll  = new ArrayList<>();
        departmentListAll.add(department);
        Department department1 = new Department();
        departmentListAll.add(department1);
        department.setId("testId");
        department.setDeptFatherId("testFatherId");
        department1.setId("testFatherId");
        new Expectations(){
            {
                departmentDao.queryTotalDepartment();
                result = departmentListAll;
            }
        };
        departmentService.departmentListToTree(departmentList);
    }
}