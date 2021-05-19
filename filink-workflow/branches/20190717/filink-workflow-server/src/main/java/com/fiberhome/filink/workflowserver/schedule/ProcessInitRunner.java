package com.fiberhome.filink.workflowserver.schedule;

import com.fiberhome.filink.workflowserver.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author hedongwei@wistronits.com
 * description 流程初始化发布
 * date 2018/11/30 9:37
 */
@Component
@Order(value = 1)
public class ProcessInitRunner implements ApplicationRunner {

    @Autowired
    private ProcessService processService;

    /**
     * @author hedongwei@wistronits.com
     * description 启动需要运行的方法
     * date 9:42 2018/11/30
     * param [applicationArguments]
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        processService.deploy();
    }

}
