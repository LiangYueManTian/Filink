package com.fiberhome.filink.userserver.service;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.*;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 *   部门服务类
 *
 * @author xuangong
 * @since 2019-01-03
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 查询单个部门的信息
     * @param deptId 部门id
     * @return 查询结果
     */
    Result queryDeptInfoById(String deptId);

    /**
     * 新增部门
     * @param dept 部门信息
     * @return 新增结果
     * @throws Exception
     */
    Result addDept(Department dept);

    /**
     *  修改部门
     * @param dept 部门信息
     * @return 修改结果
     * @throws Exception
     */
    Result updateDept(Department dept);

    /**
     * 删除单个部门
     * @param parameters 部门id
     * @return 删除结果
     * @throws Exception
     */
    Result deleteDept(Parameters parameters);

    /**
     * 根据id查询部门全部信息
     * @param departId 部门id
     * @return 部门信息
     */
    List<Department> queryFullDepartMent(String departId);

    /**
     * 查询所有的部门信息
     * @return 部门信息
     */
    List<DepartmentFeign> queryAllDepartment();

    /**
     * 查询所有部门信息，前端调用
     * @return 部门信息列表
     */
    Result queryTotalDepartment();

    /**
     * 根据条件查询部门
     * @param departmentParamter    部门条件查询
     * @return  部门信息列表
     */
    Result queryDepartmentList(QueryCondition<DepartmentParamter> departmentParamter);

    /**
     * 根据条件进行过滤查询
     * @param departmentParamter 查询条件
     * @return  部门信息列表
     */
    List<Department> queryDepartmenttByField(DepartmentParamter departmentParamter);

    /**
     * 获取条件查询中所有部门信息
     * @return  部门信息列表
     */
    Result conditionDepartment();

    /**
     * 根据部门id查询部门信息，不包含上下级关系
     * @param ids 部门id
     * @return  部门信息
     */
    List<DepartmentFeign> queryDepartmentFeignById(List<String> ids);
    /**
     * 根据区域id查询所有的部门信息并设置是否可用
     * @param areaIds 区域id集合
     * @return 部门信息
     */
    Result queryAllDepartmentForPageSelection(List<String> areaIds);

    /**
     * 根据部门名称模糊查询部门信息
     * @param deptName  部门名称
     * @return  部门列表信息
     */
    List<DepartmentFeign> queryDepartmentFeignByName(String deptName);
}
