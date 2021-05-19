package com.fiberhome.filink.export.consumer;

import com.fiberhome.filink.export.service.TaskInfoService;
import com.fiberhome.filink.export.stream.ExportStreams;
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
     * 自动注入导出服务
     */
    @Autowired
    private TaskInfoService taskInfoService;
    /**
     * 删除过期任务消息码
     */
    private static final int DELETE_OVERDUE_TASK = 1;

    /**
     * 监听消息
     *
     * @param code 消息码
     */
    @StreamListener(ExportStreams.EXPORT_INPUT)
    public void exportConsumer(int code) {
        if (code == DELETE_OVERDUE_TASK) {
            log.info("正在执行删除过期任务");
            taskInfoService.deleteOverdueTask();
        }
    }
}
