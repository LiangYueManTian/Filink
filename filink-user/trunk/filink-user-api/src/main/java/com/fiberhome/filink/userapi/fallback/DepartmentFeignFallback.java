package com.fiberhome.filink.userapi.fallback;

import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询部门信息
 *
 * @author xuangong
 */

@Slf4j
@Component
public class DepartmentFeignFallback implements DepartmentFeign {

    /**
     * 查询部门熔断处理结果
     *
     * @return 判断的结果
     */
    @Override
    public List<Department> queryAllDepartment() {

        log.info("queryAllDepartment feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public List<Department> queryDepartmentFeignById(List<String> ids) {

        log.info("queryDepartmentFeignById feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public List<Department> queryDepartmentFeignByName(String deptName) {

        log.info("queryDepartmentFeignByName feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Department queryDepartTreeByDeptId(String userId) {

        log.info("queryDepartTreeByDeptId feign调用熔断》》》》》》》》》》");
        return null;
    }


}
