package com.fiberhome.filink.menu.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.menu.bean.MenuInfo;
import com.fiberhome.filink.menu.dao.MenuInfoDao;
import com.fiberhome.filink.menu.service.MenuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Service
public class MenuInfoServiceImpl extends ServiceImpl<MenuInfoDao, MenuInfo> implements MenuInfoService {
    /**
     * 自动注入menuInfoDao
     */
    @Autowired
    private MenuInfoDao menuInfoDao;

    /**
     * 查询默认菜单
     *
     * @return 查询结果
     */
    @Override
    public Result getDefaultMenuTemplate() {
        return ResultUtils.success(menuInfoDao.selectMenuInfoTree());
    }

    /**
     * 查询数据库中包含的传入id集合的数据量
     *
     * @param menuIds 传入集合
     * @return 包含数量
     */
    @Override
    public int selectCountByIds(List<String> menuIds) {
        return menuInfoDao.selectCountByMenuIds(menuIds);
    }


}
