package com.fiberhome.filink.exportapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



/**
 * @author qiqizhu@wistronits.com
 * create on 2019/3/13 1:21 PM
 */
@Slf4j
@Component
public class ExportFeignFallback implements ExportFeign {
    private static final String INFO ="System service feign call blow》》》》》》》》》》";
    @Override
    public Result addTask(ExportDto exportDto) {
        log.error(INFO);
        return null;
    }

    @Override
    public Boolean updateTaskFileNumById(ExportDto exportDto) {
        log.error(INFO);
        return null;
    }

    @Override
    public Result stopExport(String taskId) {
        log.error(INFO);
        return null;
    }

    @Override
    public Boolean changeTaskStatusToUnusual(String taskId) {
        log.error(INFO);
        return null;
    }

    @Override
    public Boolean selectTaskIsStopById(String taskId) {
        log.error(INFO);
        return null;
    }
}
