package com.fiberhome.filink.logapi.job;

import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.FileConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author hedongwei@wistronits.com
 * @Description 新增本地文件任务
 * @date 2019/1/29 16:39
 */

@Component
public class AddLocalFileJob {

    @Autowired
    private LogProcess logProcess;

    /**
     * @author hedongwei@wistronits.com
     * description 执行本地文件新增日志
     * date 16:42 2019/1/29
     * param []
     */
    @Scheduled(cron="0 0 0 1/1 * ?")
    public void executeLocalFileAddLog() {
        //获得本地日志文件信息
        String path = FileConstants.fileConstants.logPath;
        //同步本地文件
        logProcess.uploadLocalLogInfoToLogService(path);
    }
}
