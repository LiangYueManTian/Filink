package com.fiberhome.filink.system_commons.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.system_commons.bean.SysParam;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  系统服务统一参数服务类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
public interface SysParamService extends IService<SysParam> {
    /**
     * 根据类型查询相应参数信息
     * @param paramType 类型
     * @return 参数信息
     */
    Result queryParam(String paramType);

    /**
     * 根据类型查询相应参数信息
     *
     * @param paramType 类型
     * @return 参数信息
     */
    SysParam queryParamByType(String paramType);
}
