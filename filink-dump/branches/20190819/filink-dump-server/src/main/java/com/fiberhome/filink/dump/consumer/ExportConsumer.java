package com.fiberhome.filink.dump.consumer;

import com.fiberhome.filink.dump.constant.DumpConstant;
import com.fiberhome.filink.dump.constant.ExportApiConstant;
import com.fiberhome.filink.dump.service.TaskInfoService;
import com.fiberhome.filink.dump.stream.ExportStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 导出消息消费
 * 用于过期任务自动删除
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 18:28
 */
@Slf4j
@Component
public class ExportConsumer {

    /**
     * 定时任务开始转储
     */
    private static final String DELETE_OVERDUE_TASK = "DUMP_DATA";

    @Autowired
    private TaskInfoService taskInfoService;

    /**
     * 监听消息
     *
     * @param code 消息码
     */
    @StreamListener(ExportStreams.EXPORT_INPUT)
    public void exportConsumer(String code) {
        log.info("export data code = {}" , code);
        if (DELETE_OVERDUE_TASK.equals(code)) {
            taskInfoService.dumpData(ExportApiConstant.ALL_LOG_TYPE, DumpConstant.DUMP_TRIGGER_JOB);
        }
    }
}
