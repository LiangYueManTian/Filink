package com.fiberhome.filink.menu.dao;

import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * @return 禁用结果
     */
    Integer updateAllMenuTemplate();
}
