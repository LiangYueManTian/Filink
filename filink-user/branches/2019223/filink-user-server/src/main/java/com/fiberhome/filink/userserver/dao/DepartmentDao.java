package com.fiberhome.filink.userserver.dao;

import com.fiberhome.filink.userserver.bean.Department;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.DepartmentParamter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *   部门表 Mapper 接口
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Repository
public interface DepartmentDao extends BaseMapper<Department> {

    /**
     * 根据id查询部门全部信息
     * @param departId 用户部门id
     * @return 部门信息
     */
    List<Department> selectFullDepartMent(String departId);

    /**
     * 查询所有的部门信息
     * @return 部门信息
     */
    List<Department> queryAllDepartment();

    /**
     * 批量删除部门
     * @param departIdArray
     * @return
     */
    Integer deleteDepartment(@Param("departIdArray") String[] departIdArray);

    /**
     * 根据上级id查询部门信息
     * @param deptIdArray
     * @return
     */
    List<Department> queryDeptByParentIds(@Param("deptIdArray") String[] deptIdArray);

    /**
     * 根据条件查询部门信息
     * @param departmentParamter
     * @return
     */
    List<Department> queryDepartmentByField(DepartmentParamter departmentParamter);

    /**
     * 查询部门数量
     * @param departmentParamter
     * @return
     */
    Long queryDepartmentNumber(DepartmentParamter departmentParamter);

    /**
     * 校验部门信息
     * @param departmentParamter
     * @return
     */
    List<Department> verifyDepartmentByField(DepartmentParamter departmentParamter);

    /**
     * 查询所有部门信息
     * @return
     */
    List<Department> queryToltalDepartment();

    /**
     * 更新部门的父节点信息，可能存在为空的情况
     * @param parentId  父节点部门id
     * @param id    当前部门id
     * @return  更新的数量
     */
    Integer updateDepartmentParentId(@Param("parentId") String parentId,@Param("id") String id);
}
