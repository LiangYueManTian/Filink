package com.fiberhome.filink.user_api.fallback;

import com.fiberhome.filink.user_api.api.DepartmentFeign;
import com.fiberhome.filink.user_api.bean.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询部门信息
 * @author xuangong
 */

@Slf4j
@Component
public class DepartmentFeignFallback implements DepartmentFeign{

    /**
     * 查询部门熔断处理结果
     * @return  判断的结果
     */
    @Override
    public List<Department> queryAllDepartment() {
        log.info("queryAllDepartment feign调用熔断》》》》》》》》》》");
        return null;
    }
}
