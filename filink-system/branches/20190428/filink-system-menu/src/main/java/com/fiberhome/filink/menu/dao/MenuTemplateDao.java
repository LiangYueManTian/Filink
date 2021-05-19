package com.fiberhome.filink.menu.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.menu.bean.MenuTemplate;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public interface MenuTemplateDao extends BaseMapper<MenuTemplate> {
    /**
     * 将所有模板设置为禁用
     *
     * @return 操作结果
     */
    Integer updateAllMenuTemplate();
}
