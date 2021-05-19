package com.fiberhome.filink.userserver.service;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.device_api.api.AreaFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.DepartmentFeign;
import com.fiberhome.filink.userserver.bean.DepartmentParamter;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.DepartmentFeignDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.service.impl.DepartmentServiceImpl;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    @Test
    public void queryDeptInfoById() throws Exception {

        String deptId = "1";
        Department department = new Department();
        department.setId("1");

        new Expectations(){
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
        department1.setDeptFatherid("2");


        new Expectations(){
            {
                departmentDao.selectById("1");
                result = department1;
            }
        };

        new Expectations(){
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

        new Expectations(){
            {
                departmentDao.insert(department);
                result = 1;
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

        new Expectations(){
            {
                departmentDao.selectById("123");
                result = department;
            }
        };

        department.setDeptName("234");
        department.setDeptFatherid("456");
        new Expectations(){
            {
                departmentDao.updateById(department);
                result = 1;
            }
        };

        new Expectations(){
            {
                departmentDao.updateDepartmentParentId(department.getDeptFatherid(),department.getId());
                result = 1;
            }
        };

        departmentService.updateDept(department);
    }

    @Test
    public void deleteDept() throws Exception {

        Parameters parameters = new Parameters();
        String[] ids = {"123"};
        parameters.setFirstArrayParamter(ids);

        Department department = new Department();
        department.setId("123");
        List<Department> deptList = new ArrayList<>();
        deptList.add(department);

        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "DELETE_DEPART_SUCCESS";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };


        new Expectations(){
            {
                departmentDao.queryDeptByParentIds(ids);
                result = null;
            }
        };

        new Expectations(){
            {
                userDao.queryUserByDepts(ids);
                result = null;
            }
        };

        new Expectations(){
            {
                departmentDao.selectBatchIds(Arrays.asList(ids));
                result = deptList;
            }
        };

        new Expectations(){
            {
                departmentDao.deleteDepartment(ids);
                result = 1;
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

        new Expectations(){
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

        new Expectations(){
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

        new Expectations(){
            {
                departmentDao.queryAllDepartment();
                result = deptList;
            }
        };
        departmentService.queryTotalDepartment();
    }

    @Test
    public void queryDepartmentList() throws Exception {

        QueryCondition<DepartmentParamter> objectQueryCondition = new QueryCondition<DepartmentParamter>();
        DepartmentParamter departmentParamter = new DepartmentParamter();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(5);
        objectQueryCondition.setPageCondition(pageCondition);
        objectQueryCondition.setBizCondition(departmentParamter);
        departmentParamter.setPage(1);
        departmentParamter.setPageSize(5);
        departmentParamter.setAddress("123");

        Department department = new Department();
        List<Department> departmentList = new ArrayList<>();
        department.setId("123");
        department.setAddress("123");
        departmentList.add(department);

        new Expectations(){
            {
                departmentDao.queryDepartmentByField(departmentParamter);
                result = departmentList;
            }
        };

        new Expectations(){
            {
                departmentDao.queryDepartmentNumber(departmentParamter);
                result = 1;
            }
        };
        departmentService.queryDepartmentList(objectQueryCondition);
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
        new Expectations(){
            {
                departmentDao.verifyDepartmentByField(departmentParamter);
                result = departmentList;
            }
        };

        departmentService.queryDepartmenttByField(departmentParamter);
    }

    @Test
    public void conditionDepartment(){

        Department department = new Department();
        List<Department> departmentList = new ArrayList<>();
        department.setId("123");
        department.setAddress("123");
        departmentList.add(department);

        new Expectations(){
            {
                departmentDao.queryToltalDepartment();
                result = departmentList;
            }
        };

        departmentService.conditionDepartment();
    }
}