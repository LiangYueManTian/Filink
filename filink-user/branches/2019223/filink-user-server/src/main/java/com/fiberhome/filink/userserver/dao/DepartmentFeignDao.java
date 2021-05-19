package com.fiberhome.filink.userserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.DepartmentFeign;
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
public interface DepartmentFeignDao extends BaseMapper<DepartmentFeign> {

    /**
     * 查询所有的部门信息
     * @return 部门信息
     */
    List<DepartmentFeign> queryAllDepartmentFeign();
}
