package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.DepartmentFeign;
import com.fiberhome.filink.userserver.bean.DepartmentParamter;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.service.DepartmentService;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class DepartmentControllerTest {

    @Tested
    private DepartmentController departmentController;

    @Injectable
    private DepartmentService departmentService;

    @Test
    public void queryDeptInfoById() throws Exception {

        new Expectations(){
            {
                departmentService.queryDeptInfoById("123");
                result = ResultUtils.success();
            }
        };

        Result result = departmentController.queryDeptInfoById("123");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void addDept() throws Exception {

        Department department = new Department();

        new Expectations(){
            {
                departmentService.addDept(department);
                result = ResultUtils.success();
            }
        };

        Result result = departmentController.addDept(department);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateDept() throws Exception {

        Department department = new Department();
        department.setId("123");

        new Expectations(){
            {
                departmentService.updateDept(department);
                result = ResultUtils.success();
            }
        };

        Result result = departmentController.updateDept(department);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deleteDept() throws Exception {

        Parameters parameters = new Parameters();

        new Expectations(){
            {
                departmentService.deleteDept(parameters);
                result = ResultUtils.success();
            }
        };

        Result result = departmentController.deleteDept(parameters);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryFullDepartMent() throws Exception {

        List<Department> departmentList = new ArrayList<>();
        Department department = new Department();
        departmentList.add(department);
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "QUERY_DEPARTMENT_SUCCESS";
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
                departmentService.queryFullDepartMent("123");
                result = ResultUtils.success();
            }
        };

        Result result = departmentController.queryFullDepartMent("123");
    }

    @Test
    public void queryAllDepartment() throws Exception {

        new Expectations(){
            {
                List<DepartmentFeign> departmentFeignList1 = departmentService.queryAllDepartment();
                result = departmentFeignList1;
            }
        };

        departmentController.queryAllDepartment();
    }

    @Test
    public void queryTotalDepartment() throws Exception {

        new Expectations(){
            {
                departmentService.queryTotalDepartment();
                result = ResultUtils.success();
            }
        };
        departmentController.queryTotalDepartment();
    }

    @Test
    public void queryDepartmentList() throws Exception {

        QueryCondition<DepartmentParamter> departmentParamterQueryCondition = new QueryCondition<>();
        DepartmentParamter departmentParamter = new DepartmentParamter();
        departmentParamterQueryCondition.setBizCondition(departmentParamter);

        new Expectations(){
            {
                departmentService.queryDepartmentList(departmentParamterQueryCondition);
                result = ResultUtils.success();;
            }
        };
        departmentController.queryDepartmentList(departmentParamterQueryCondition);

    }

    @Test
    public void verifyDeptInfo() throws Exception {

        DepartmentParamter departmentParamter = new DepartmentParamter();
        List<Department> deptList = new ArrayList<>();
        Department department = new Department();
        department.setId("123");
        deptList.add(department);

        new Expectations(){
            {
                departmentService.queryDepartmenttByField(departmentParamter);
                result = deptList;;
            }
        };
        Result  result = departmentController.verifyRoleInfo(departmentParamter);

    }

    @Test
    public void conditionDepartment(){

        new Expectations(){
            {
                departmentService.conditionDepartment();
                result = ResultUtils.success();
            }
        };

        departmentController.conditionDepartment();
    }

}