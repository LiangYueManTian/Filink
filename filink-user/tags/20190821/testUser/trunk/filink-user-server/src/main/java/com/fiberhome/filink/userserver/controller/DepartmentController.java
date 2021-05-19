package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.DepartmentFeign;
import com.fiberhome.filink.userserver.bean.DepartmentParamter;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import com.fiberhome.filink.userserver.constant.UserI18n;
import com.fiberhome.filink.userserver.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 部门Controller层
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    /**
     * 查询单个部门的信息
     *
     * @param deptId 部门id
     * @return 查询结果
     */
    @PostMapping("/queryDeptInfoById/{deptId}")
    public Result queryDeptInfoById(@PathVariable("deptId") String deptId) {
        return departmentService.queryDeptInfoById(deptId);
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 新增结果
     * @throws Exception
     */
    @PostMapping("/insert")
    public Result addDept(@RequestBody Department dept) throws Exception {

        return departmentService.addDept(dept);
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 修改结果
     * @throws Exception
     */
    @PutMapping("/update")
    public Result updateDept(@RequestBody Department dept) throws Exception {
        if (dept.getId() == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.DEPARTMENT_ID_NOT_NULL));
        }

        return departmentService.updateDept(dept);
    }

    /**
     * 删除单个部门
     *
     * @param parameters 条件参数，里面传入用户id
     * @return 删除结果
     * @throws Exception
     */
    @PostMapping("/deleteByIds")
    public Result deleteDept(@RequestBody Parameters parameters) throws Exception {
        return departmentService.deleteDept(parameters);
    }

    /**
     * 查询部门的所有信息，包括下级信息
     *
     * @param departId
     * @return
     */
    @PostMapping("/queryFullDepartMent/{departId}")
    public Result queryFullDepartMent(@PathVariable("departId") String departId) {

        List<Department> departList = departmentService.queryFullDepartMent(departId);
        return ResultUtils.success(UserResultCode.QUERY_DEPARTMENT_SUCCESS,
                I18nUtils.getSystemString(UserI18n.QUERY_DEPARTMENT_SUCCESS), departList);
    }

    /**
     * 查询所有的部门名字，后台远程调用
     *
     * @return 所有部门的名字
     */
    @PostMapping("/queryAllDepartment")
    public List<DepartmentFeign> queryAllDepartment() {
        List<DepartmentFeign> departmentList = departmentService.queryAllDepartment();
        return departmentList;
    }

    /**
     * 查询所有一级部门信息，前端调用
     *
     * @return 所有一级部门信息
     */
    @PostMapping("/queryTotalDepartment")
    public Result queryTotalDepartment() {
        Result result = departmentService.queryTotalDepartment();
        return result;
    }

    /**
     * 条件查询部门信息，包含条件，排序，分页等参数信息
     *
     * @param departmentParamter 部门参数实体类
     * @return 部门列表信息
     */
    @PostMapping("/queryDepartmentList")
    public Result queryDepartmentList(@RequestBody QueryCondition<DepartmentParamter> departmentParamter) {
        Result result = departmentService.queryDepartmentList(departmentParamter);
        return result;
    }

    /**
     * 校验单位信息
     *
     * @param departmentParamter 校验的参数信息
     * @return 根据校验信息查询的部门信息
     */
    @PostMapping("/verifyDeptInfo")
    public Result verifyRoleInfo(@RequestBody DepartmentParamter departmentParamter) {
        List<Department> roleList = departmentService.queryDepartmenttByField(departmentParamter);
        return ResultUtils.success(ResultCode.SUCCESS, null, roleList);
    }

    /**
     * 获取条件查询中所有级别的部门信息
     *
     * @return 所有存在的部门信息
     */
    @PostMapping("/conditionDepartment")
    public Result conditionDepartment() {
        return departmentService.conditionDepartment();
    }

    /**
     * 根据部门id查询部门信息，不包含上下级关系，后台远程调用
     *
     * @param ids 部门id
     * @return 部门信息
     */
    @PostMapping("/queryDepartmentFeignByIds")
    public List<DepartmentFeign> queryDepartmentFeignById(@RequestBody List<String> ids) {

        return departmentService.queryDepartmentFeignById(ids);
    }

    /**
     * 根据区域id查询所有的部门信息并设置是否可用
     *
     * @param areaIds 区域id集合
     * @return 部门信息
     */
    @PostMapping("/queryAllDepartmentForPageSelection")
    public Result queryAllDepartmentForPageSelection(@RequestBody List<String> areaIds) {
        return departmentService.queryAllDepartmentForPageSelection(areaIds);

    }

    /**
     * 根据部门名称模糊查询部门信息,前端调用
     *
     * @param deptName 部门名称
     * @return 部门列表
     */
    @GetMapping("/queryDepartmentFeignByName/{deptName}")
    public List<DepartmentFeign> queryDepartmentFeignByName(@PathVariable("deptName") String deptName) {

        return departmentService.queryDepartmentFeignByName(deptName);
    }

    /**
     * 根据部门名称模糊查询部门信息,后台调用
     *
     * @param deptName 部门名称
     * @return 部门列表
     */
    @GetMapping("/queryDepartmentByName/{deptName}")
    public Result queryDepartmentByName(@PathVariable("deptName") String deptName) {

        List<DepartmentFeign> departmentFeignList = departmentService.queryDepartmentFeignByName(deptName);
        return ResultUtils.success(departmentFeignList);
    }

    /**
     * 后台根据部门获取整颗部门树，后台使用
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/queryDepartTreeByDeptId/{userId}")
    public Department queryDepartTreeByDeptId(@PathVariable("userId") String userId) {

        return departmentService.queryDepartTree(userId);
    }


    /**
     * 根据id获取部门树，前端使用
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/queryDepartTree/{userId}")
    public Result queryDepartTree(@PathVariable("userId") String userId) {

        Department department = departmentService.queryDepartTree(userId);
        return ResultUtils.success(department);
    }

    @GetMapping("/test")
    public String addTestDept() {
        Department dept = new Department();
        for (int i = 1; i < 3001; i++) {
            dept.setDeptName("大数据测试部门" + i);
            dept.setRemark("大数据测试部门");
            departmentService.addDept(dept);
            System.out.println("新增大数据测试部门" + i);
        }
        return "新增设施成功";
    }
}
