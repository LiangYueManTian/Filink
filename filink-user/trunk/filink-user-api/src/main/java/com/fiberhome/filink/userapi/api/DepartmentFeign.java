package com.fiberhome.filink.userapi.api;

import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.fallback.DepartmentFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 部门feign
 * @author  xuangong
 */
@FeignClient(name = "filink-user-server", fallback = DepartmentFeignFallback.class)
public interface DepartmentFeign {

    /**
     * 获取所有部门信息
     * @return  部门信息
     */
    @PostMapping("/department/queryAllDepartment")
    List<Department> queryAllDepartment();

    /**
     * 根据部门id查询部门信息，不包含上下级关系
     * @param ids    部门id
     * @return  部门信息
     */
    @PostMapping("/department/queryDepartmentFeignByIds")
    List<Department> queryDepartmentFeignById(@RequestBody List<String> ids);

    /**
     * 根据部门名称模糊查询部门信息
     * @param deptName  部门名称
     * @return  部门列表
     */
    @GetMapping("/department/queryDepartmentFeignByName/{deptName}")
    List<Department> queryDepartmentFeignByName(@PathVariable("deptName") String deptName);

    /**
     * 后台根据部门获取整颗部门树
     * @param userId 用户id
     * @return
     */
    @GetMapping("/department/queryDepartTreeByDeptId/{userId}")
    Department queryDepartTreeByDeptId(@PathVariable("userId") String userId);
}
