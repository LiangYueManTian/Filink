package com.fiberhome.filink.export_api.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.export_api.api.ExportFeign;
import com.fiberhome.filink.export_api.bean.ExportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



/**
 * @author qiqizhu@wistronits.com
 * create on 2019/3/13 1:21 PM
 */
@Slf4j
@Component
public class ExportFeignFallback implements ExportFeign {
    @Override
    public Result addTask(ExportDto exportDto) {
        log.info("系统服务任务feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Boolean updateTaskFileNumById(ExportDto exportDto) {
        log.info("系统服务任务feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Result stopExport(String taskId) {
        log.info("系统服务任务feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Boolean changeTaskStatusToUnusual(String taskId) {
        log.info("系统服务任务feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Boolean selectTaskIsStopById(String taskId) {
        log.info("系统服务任务feign调用熔断》》》》》》》》》》");
        return null;
    }
}
