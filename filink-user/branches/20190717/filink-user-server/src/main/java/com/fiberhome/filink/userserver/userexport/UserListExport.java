package com.fiberhome.filink.userserver.userexport;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 导出用户列表
 *
 * @author xuangong
 */

@Component
public class UserListExport extends AbstractExport {

    @Autowired
    private UserDao userDao;

    @Override
    protected List queryData(QueryCondition queryCondition) {

        //获取参数信息和分页信息
        UserParameter userParameter = (UserParameter) queryCondition.getBizCondition();
        if (userParameter == null) {
            userParameter = new UserParameter();
        }

        userParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        userParameter.setPage(queryCondition.getPageCondition().getPageNum());
        userParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        List<User> userList = userDao.queryUserByField(userParameter);

        return userList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {

        //获取参数信息和分页信息
        UserParameter userParameter = (UserParameter) queryCondition.getBizCondition();
        if (userParameter == null) {
            userParameter = new UserParameter();
        }

        userParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        userParameter.setPage(queryCondition.getPageCondition().getPageNum());
        userParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        Long userNumber = userDao.queryUserNum(userParameter);
        return userNumber.intValue();
    }
}
