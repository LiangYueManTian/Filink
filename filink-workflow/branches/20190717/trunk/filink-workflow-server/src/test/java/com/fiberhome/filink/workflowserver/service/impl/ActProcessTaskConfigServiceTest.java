package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.workflowserver.bean.ActProcessTaskConfig;
import com.fiberhome.filink.workflowserver.dao.ActProcessTaskConfigDao;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 流程任务配置服务实现测试类
 * @author hedongwei@wistronits.com
 * @date 2019/6/30 17:49
 */
@RunWith(JMockit.class)
@SpringBootTest
public class ActProcessTaskConfigServiceTest {

    /**
     * 流程任务配置服务实现类
     */
    @Tested
    private ActProcessTaskConfigServiceImpl actProcessTaskConfigService;

    /**
     * 流程任务配置mapper接口
     */
    @Injectable
    private ActProcessTaskConfigDao actProcessTaskConfigDao;

    /**
     * 查询任务配置信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/20
     */
    @Test
    public void queryListProcessTaskConfig() {
        ActProcessTaskConfig actProcessTaskConfig = new ActProcessTaskConfig();
        actProcessTaskConfig.setProcTaskName("name");
        actProcessTaskConfig.setProcType("process");
        actProcessTaskConfig.setProcId("1");
        actProcessTaskConfig.setProcStatusCode("code");
        actProcessTaskConfig.setProcStatusName("statusName");
        actProcessTaskConfig.setProcTaskDesc("taskDesc");
        actProcessTaskConfig.setProcName("procName");
        actProcessTaskConfig.getProcStatusCode();
        actProcessTaskConfig.getProcId();
        actProcessTaskConfig.getProcName();
        actProcessTaskConfig.getProcStatusName();
        actProcessTaskConfig.getProcTaskDesc();
        actProcessTaskConfig.getProcType();
        actProcessTaskConfig.getProcTaskName();
        new Expectations() {
            {
                List<ActProcessTaskConfig> actProcessTaskConfigList = actProcessTaskConfigDao.queryListProcessTaskConfig(actProcessTaskConfig);
                result = actProcessTaskConfigList;
            }
        };
        actProcessTaskConfigService.queryListProcessTaskConfig(actProcessTaskConfig);
    }

}
