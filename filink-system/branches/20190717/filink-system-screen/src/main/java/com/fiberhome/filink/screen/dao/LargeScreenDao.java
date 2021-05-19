package com.fiberhome.filink.screen.dao;

import com.fiberhome.filink.screen.bean.LargeScreen;

import java.util.List;

/**
 * <p>
 *     大屏管理 Mapper 接口
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-28
 */
public interface LargeScreenDao {
    /**
     * 查询所有大屏
     * @return 大屏信息 List
     */
    List<LargeScreen> queryLargeScreenAll();

    /**
     * 查询大屏名称是否重复
     * @param largeScreen 大屏信息
     * @return 大屏名称重复ID
     */
    String queryLargeScreenNameRepeat(LargeScreen largeScreen);

    /**
     * 根据大屏ID修改大屏名称
     * @param largeScreen 大屏信息
     * @return 更新数据数
     */
    Integer updateLargeScreenNameById(LargeScreen largeScreen);
}
