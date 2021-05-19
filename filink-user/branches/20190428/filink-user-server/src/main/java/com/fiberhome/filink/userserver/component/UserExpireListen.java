package com.fiberhome.filink.userserver.component;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.stream.UpdateUserListenStream;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 查询用户是否为有效用户
 *
 * @author xgong
 */
@Component
@Slf4j
public class UserExpireListen {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityFeign securityFeign;

    private static final String USER_EXPIRE = "USER_EXPIRE";

    /**
     * 启用账号禁用策略
     */
    private static final String OPEN_STRATEY = "1";

    private static final String UNIT_DAY = "d";
    private static final String UNIT_MONTH = "m";
    private static final String UNIT_YEAR = "y";

    /**
     * 用户禁用
     */
    private static final int USER_STATUS_STOP = 0;

    /**
     * 用户启用的状态值
     */
    private static final String OPEN_USER_STATUS = "1";


    /**
     * 监听禁用用户的服务
     *
     * @param code 消息码
     */
    @StreamListener(UpdateUserListenStream.EXPIRE_USER)
    public void userExpireListen(String code) {

        log.info("监听到了kafka-userExpireListen的信息,code = " + code);
        if (USER_EXPIRE.equals(code)) {

            Result result = securityFeign.queryAccountSecurity();
            //获取安全策略信息
            if (result.getData() != null) {
                AccountSecurityStrategy strategy = JSON.parseObject(JSON.toJSONString(result.getData()), AccountSecurityStrategy.class);

                //当前时间
                long currentTime = System.currentTimeMillis();
                List<String> userIdList = new ArrayList<>();
                //如果启用账号锁定策略
                if (OPEN_STRATEY.equals(strategy.getForbidStrategy())) {
                    List<User> userList = userDao.queryAllUser();
                    userList.forEach(user -> {
                        //如果用户为启用状态
                        if (user.getUserStatus().equals(OPEN_USER_STATUS)) {
                            String countValidityTime = user.getCountValidityTime();
                            Long createTime = user.getCreateTime();
                            if (StringUtils.isNotEmpty(countValidityTime)) {
                                Calendar calendar = Calendar.getInstance();
                                //设置进当前时间
                                calendar.setTimeInMillis(createTime);
                                if (countValidityTime.contains(UNIT_DAY)) {
                                    int day = Integer.parseInt(countValidityTime.substring(0, countValidityTime.length() - 1));
                                    //时间往前推指定的天数
                                    calendar.add(Calendar.DAY_OF_YEAR, day);
                                } else if (countValidityTime.contains(UNIT_MONTH)) {
                                    int month = Integer.parseInt(countValidityTime.substring(0, countValidityTime.length() - 1));
                                    //时间往前推指定的天数
                                    calendar.add(Calendar.MONTH, month);
                                } else if (countValidityTime.contains(UNIT_YEAR)) {
                                    int year = Integer.parseInt(countValidityTime.substring(0, countValidityTime.length() - 1));
                                    //时间往前推指定的天数
                                    calendar.add(Calendar.YEAR, year);
                                }

                                //截止时间
                                long endTime = calendar.getTimeInMillis();
                                //如果有效期截止时间小于等于当前时间，则设置禁用当前账户
                                if (endTime <= currentTime) {
                                    userIdList.add(user.getId());
                                }
                            }
                        }
                    });

                    //如果用户过了有效期，则把用户设置为禁用
                    if (CheckEmptyUtils.collectEmpty(userIdList)) {
                        String[] idArray = {};
                        userDao.updateUserStatus(USER_STATUS_STOP, userIdList.toArray(idArray));

                        //记录禁用过期用户的日志
                        List<User> userLogList = userDao.queryUserByIdList(userIdList);
                        LogUtils.addTimeTaskLog(userLogList,
                                UserConst.FORBIDDEN_EXPIRED_USER_MODEL, LogConstants.LOG_TYPE_SYSTEM);
                    }

                }
            }
        }
    }

}
