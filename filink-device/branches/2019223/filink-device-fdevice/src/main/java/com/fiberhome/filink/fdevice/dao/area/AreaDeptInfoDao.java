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
     * @param areaDeptInfoList 新增信息
     * @return 新增数量
     */
    Integer addAreaDeptInfoBatch(List<AreaDeptInfo> areaDeptInfoList);
}
