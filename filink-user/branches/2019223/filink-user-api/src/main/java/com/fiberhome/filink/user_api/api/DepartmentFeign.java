package com.fiberhome.filink.user_api.api;

import com.fiberhome.filink.user_api.bean.Department;
import com.fiberhome.filink.user_api.fallback.DepartmentFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 获取部门信息
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
}
