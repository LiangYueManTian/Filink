package com.fiberhome.filink.systemcommons.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.systemcommons.bean.SysParam;

/**
 * <p>
 *  系统服务统一参数Mapper 接口
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
public interface SysParamDao extends BaseMapper<SysParam> {
    /**
     * 根据类型查询相应参数信息
     * @param paramType 类型
     * @return 参数信息
     */
    SysParam queryParamByType(String paramType);
    /**
     * 根据ID查询相应参数信息
     * @param paramId ID
     * @return 参数信息
     */
    SysParam queryParamById(String paramId);
    /**
     * 根据ID更新相应参数信息
     * @param sysParam 参数信息
     * @return Integer
     */
    Integer updateParamById(SysParam sysParam);
}
