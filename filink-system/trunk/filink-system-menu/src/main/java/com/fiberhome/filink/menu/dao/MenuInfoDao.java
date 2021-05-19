package com.fiberhome.filink.menu.dao;

import com.fiberhome.filink.menu.bean.MenuInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.menu.dto.MenuInfoTree;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public interface MenuInfoDao extends BaseMapper<MenuInfo> {
    /**
     * 查询默认菜单
     *
     * @return 查询结果
     */
    List<MenuInfoTree> selectMenuInfoTree();

    /**
     * 查询数据库中包含的传入id集合的数据量
     *
     * @param menuIds 传入集合
     * @return 包含数量
     */
    int selectCountByMenuIds(List<String> menuIds);
}
