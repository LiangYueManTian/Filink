package com.fiberhome.filink.boxhead.dao;

import com.fiberhome.filink.boxhead.bean.BoxHead;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wanzhao zhang
 * @since 2019-05-21
 */
public interface BoxHeadDao extends BaseMapper<BoxHead> {
    /**
     * 根据用户id和菜单id查询一条表头栏
     *
     * @param boxHead
     * @return  表头栏
     */
    BoxHead selectOneByUserAndMenu(BoxHead boxHead);

}
