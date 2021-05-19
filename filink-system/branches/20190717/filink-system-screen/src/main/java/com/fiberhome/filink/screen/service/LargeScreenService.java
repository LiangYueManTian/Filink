package com.fiberhome.filink.screen.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.screen.bean.LargeScreen;

/**
 * <p>
 *     大屏管理 服务类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-28
 */
public interface LargeScreenService {
    /**
     * 查询所有大屏
     * @return 大屏信息 List
     */
    Result queryLargeScreenAll();

    /**
     * 查询大屏名称是否重复
     * @param largeScreen 大屏信息
     * @return 结果
     */
    Result queryLargeScreenNameRepeat(LargeScreen largeScreen);

    /**
     * 根据大屏ID修改大屏名称
     * @param largeScreen 大屏信息
     * @return 结果
     */
    Result updateLargeScreenNameById(LargeScreen largeScreen);
}
