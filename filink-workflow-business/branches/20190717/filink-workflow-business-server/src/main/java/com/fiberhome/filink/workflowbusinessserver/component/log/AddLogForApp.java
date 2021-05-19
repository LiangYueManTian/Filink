package com.fiberhome.filink.workflowbusinessserver.component.log;

import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.constant.LogConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * app记录日志信息
 * @author hedongwei@wistronits.com
 * @date 2019/4/16 12:26
 */
@Component
public class AddLogForApp {

    @Autowired
    private LogProcess logProcess;

    /**
     * 异步批量新增app操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 12:30
     * @param addLogBeanList 新增日志集合
     */
    @Async
    public void addOperateLogBatchForApp(List<AddLogBean> addLogBeanList) {
        if (!ObjectUtils.isEmpty(addLogBeanList)) {
            //批量新增操作日志
            logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }
}
