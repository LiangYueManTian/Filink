package com.fiberhome.filink.userserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.DepartmentFeign;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门表 Mapper 接口
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Repository
public interface DepartmentFeignDao extends BaseMapper<DepartmentFeign> {

    /**
     * 查询所有的部门信息
     *
     * @return 部门信息
     */
    List<DepartmentFeign> queryAllDepartmentFeign();

    /**
     * 根据id查询部门信息，不包括上下级关系
     *
     * @param ids id集合
     * @return 部门信息
     */
    List<DepartmentFeign> queryDepartmentFeignByIds(List<String> ids);

    /**
     * 根据部门名模糊查询部门信息
     *
     * @param deptName 部门名称
     * @return 部门信息
     */
    List<DepartmentFeign> queryDepartmentFeignByName(@Param("deptName") String deptName);
}
