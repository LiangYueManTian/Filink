package com.fiberhome.filink.dump.controller;

import com.fiberhome.filink.dump.service.TaskInfoService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 任务信息控制层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/29 16:52
 */
@RunWith(JMockit.class)
public class TaskInfoControllerTest {

    @Tested
    private TaskInfoController taskInfoController;

    @Injectable
    private TaskInfoService taskInfoService;

    /**
     * 转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:55
     */
    @Test
    public void dumpData() {
        Integer dumpType = 1;
        taskInfoController.dumpData(dumpType);
    }

    /**
     * 根据id查询转储的数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:56
     */
    @Test
    public void queryDumpInfoById() {
        String id = "1";
        taskInfoController.queryDumpInfoById(id);
    }

    /**
     * 查询转储信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:59
     */
    @Test
    public void queryDumpInfo() {
        String dumpType = "1";
        taskInfoController.queryDumpInfo(dumpType);
    }
}
