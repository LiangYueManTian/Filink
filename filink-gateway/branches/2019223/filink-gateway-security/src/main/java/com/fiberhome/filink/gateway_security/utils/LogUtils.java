package com.fiberhome.filink.gateway_security.utils;

import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.user_api.bean.User;

import static com.fiberhome.filink.logapi.log.LogProcess.logProcess;

public class LogUtils {

    private static final String ID = "id";

    /**
     * 添加日志工具类
     * @param user  操作用户
     * @param model 日子模板
     * @param logType   日志类型
     */
    public static  void addLog(User user, String model, String logType) {

        //获取日志类型
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(ID);
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
