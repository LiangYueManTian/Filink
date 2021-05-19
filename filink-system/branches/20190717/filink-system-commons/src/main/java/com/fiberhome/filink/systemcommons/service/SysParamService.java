package com.fiberhome.filink.systemcommons.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.systemcommons.bean.SysParam;

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

    /**
     * 查询系统语言
     * @return 系统语言
     */
    String querySystemLanguage();

}
