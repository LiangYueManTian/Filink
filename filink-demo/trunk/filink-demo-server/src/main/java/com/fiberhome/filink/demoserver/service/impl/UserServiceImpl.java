package com.fiberhome.filink.demoserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.demoserver.bean.User;
import com.fiberhome.filink.demoserver.dao.UserDao;
import com.fiberhome.filink.demoserver.service.UserService;
import com.fiberhome.filink.mysql.MpQueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author 姚远
 * @since 2019-09-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 示例方法---根据条件查询mysql数据库
     *
     *
     * @param queryCondition 分页以及条件查询
     * @return 查询结果 封装成分页结构返回
     */
    @Override
    public Result queryUserListByCondition(QueryCondition queryCondition) {
        // 构造查询条件
        EntityWrapper entityWrapper = MpQueryHelper.myBatiesBuildQuery(queryCondition);
        Page page = MpQueryHelper.myBatiesBuildPage(queryCondition);

        // 具体查询 传入业务条件和分页条件
        List list = userDao.selectPage(page, entityWrapper);

        // 封装结果返回
        Integer count = userDao.selectCount(entityWrapper);
        PageBean pageBean = MpQueryHelper.myBatiesBuildPageBean(page, count, list);
        return ResultUtils.success(pageBean);
    }
}
