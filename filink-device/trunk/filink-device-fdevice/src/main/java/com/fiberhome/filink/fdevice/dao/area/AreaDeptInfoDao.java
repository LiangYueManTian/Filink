package com.fiberhome.filink.fdevice.dao.area;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.fdevice.bean.area.AreaDeptInfo;



import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-22
 */
public interface AreaDeptInfoDao extends BaseMapper<AreaDeptInfo> {
    /**
     * 批量新增关系表
     *
     * @param areaDeptInfoList
     * @return
     */
    Integer addAreaDeptInfoBatch(List<AreaDeptInfo> areaDeptInfoList);

    /**
     *根据区域id获取区域部门关联对象
     * @param areaIds
     * @return
     */
    List<AreaDeptInfo> selectAreaDeptInfoByAreaIds(List<String> areaIds);

    /**
     * 根据部门id集合查询区域id
     * @param deptIds
     * @return
     */
    List<String> selectAreaIdByDeptIds(List<String> deptIds);
    /**
     *根据部门id获取关系信息
     * @param deptIds
     * @return
     */
    List<AreaDeptInfo> selectAreaDeptInfoByDeptIds(List<String> deptIds);
}
