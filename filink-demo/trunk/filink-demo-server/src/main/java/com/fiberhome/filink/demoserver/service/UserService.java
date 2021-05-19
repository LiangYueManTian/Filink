package com.fiberhome.filink.demoserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.demoserver.bean.User;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author 姚远
 * @since 2019-09-18
 */
public interface UserService extends IService<User> {

    /**
     * 示例方法---根据条件查询mysql数据库
     *
     * @return 查询结果
     */
    Result queryUserListByCondition(QueryCondition queryCondition);
}
