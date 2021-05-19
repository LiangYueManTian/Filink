package com.fiberhome.filink.parameter.dao;

import com.fiberhome.filink.parameter.bean.SysLanguage;

import java.util.List;

/**
 * <p>
 *  系统语言Mapper 接口
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-11
 */
public interface SysLanguageDao{
    /**
     * 查询所有系统语言
     * @return 系统语言List
     */
    List<SysLanguage> queryLanguageAll();
}
