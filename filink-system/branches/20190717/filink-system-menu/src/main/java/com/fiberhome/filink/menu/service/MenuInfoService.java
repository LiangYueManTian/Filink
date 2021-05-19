package com.fiberhome.filink.menu.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.menu.bean.MenuInfo;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
public interface MenuInfoService extends IService<MenuInfo> {
    /**
     * 查询默认菜单
     *
     * @return 查询结果
     */
    Result getDefaultMenuTemplate();

    /**
     * 查询数据库中包含的传入id集合的数据量
     *
     * @param menuIds 传入集合
     * @return 包含数量
     */
    int selectCountByIds(List<String> menuIds);
}
