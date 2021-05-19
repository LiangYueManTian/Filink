package com.fiberhome.filink.userserver.utils;

import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.userserver.bean.User;

import java.util.ArrayList;
import java.util.List;

import static com.fiberhome.filink.logapi.log.LogProcess.logProcess;

/**
 * @author xgong
 */
public class LogUtils {

    /**
     * id字段信息
     */
    private static final String ID = "id";

    /**
     * userName字段信息
     */
    private static final String USER_NAME = "userName";

    /**
     * 添加日志工具类
     *
     * @param user    操作用户
     * @param model   日子模板
     * @param logType 日志类型
     */
    public static void addLog(User user, String model, String logType) {

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


    /**
     * 添加定时任务日志工具类
     *
     * @param userList 被处理的用户
     * @param model    日子模板
     * @param logType  日志类型
     */
    public static void addTimeTaskLog(List<User> userList, String model, String logType) {
        if (!CheckEmptyUtils.collectEmpty(userList)) {
            return;
        }

        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        for (User user : userList) {
            //获取日志类型
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(ID);
            addLogBean.setDataName(USER_NAME);
            //获得操作对象名称
            addLogBean.setFunctionCode(model);
            //获得操作对象id
            addLogBean.setOptObjId(user.getId());
            addLogBean.setOptObj(user.getUserName());

            //操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);

            //新增操作日志
            addLogBeanList.add(addLogBean);
        }

        if (CheckEmptyUtils.collectEmpty(addLogBeanList)) {
            //新增操作日志
            logProcess.addSystemLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }
}
