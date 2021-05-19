package com.fiberhome.filink.gatewaysecurity.async;

import com.fiberhome.filink.gatewaysecurity.constant.LoginConstant;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 登陆异步类
 *
 * @Author:qiqizhu@wistronits.com Date:2019/7/19
 */
@Component
@Slf4j
public class LoginAsync {
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    @Autowired
    private LogProcess logProcess;

    /**
     * 记录日志
     *
     * @param user    用户信息
     * @param model   模板
     * @param logType 日志类型
     */
    @Async
    public void loginLog(com.fiberhome.filink.userapi.bean.User user, String model, String logType) {
        log.info("User {} login：Record the login log",user.getUserName());
        systemLanguageUtil.querySystemLanguage();
        //获取日志类型
        //String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(LoginConstant.LOG_ID_TIP);
        //获得操作对象名称
        addLogBean.setFunctionCode(model);
        //获得操作对象id
        addLogBean.setOptObjId(user.getId());

        addLogBean.setOptObj(user.getUserName());
        addLogBean.setOptUserCode(user.getUserCode());
        addLogBean.setOptUserName(user.getUserName());
        addLogBean.setCreateUser(user.getUserName());
        addLogBean.setOptUserRole(user.getRoleId());
        addLogBean.setOptUserRoleName(user.getRole().getRoleName());
        //操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);

        //新增操作日志
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
