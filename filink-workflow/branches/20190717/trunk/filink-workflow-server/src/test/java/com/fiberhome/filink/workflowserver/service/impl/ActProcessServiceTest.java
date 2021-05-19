package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.workflowserver.bean.ActProcess;
import com.fiberhome.filink.workflowserver.dao.ActProcessDao;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 流程模板记录表 服务实现测试类
 * @author hedongwei@wistronits.com
 * @date 2019/6/30 17:48
 */
@RunWith(JMockit.class)
@SpringBootTest
public class ActProcessServiceTest {

    /**
     * 流程模板记录表 服务实现类
     */
    @Tested
    private ActProcessServiceImpl actProcessServiceImpl;

    /**
     * 流程模板mapper接口
     */
    @Injectable
    private ActProcessDao actProcessDao;

    /**
     * 获得需要发布的流程模板测试方法
     * @author hedongwei@wistronits.com
     * @date 9:59 2019/2/12
     */
    @Test
    public void getActProcess() {
        ActProcess actProcess = new ActProcess();
        actProcess.setIsDeploy("0");
        actProcess.setFileName("fileName");
        actProcess.setProcType("process");
        actProcess.setProcId("1");
        actProcess.setProcAction("/action");
        actProcess.setProcName("name");
        actProcess.setProcNamespace("222");
        actProcess.toString();
        new Expectations() {
            {
                List<ActProcess> actProcess = actProcessDao.getActProcess((ActProcess) any);
                result = actProcess;
            }
        };
        actProcessServiceImpl.getActProcess(actProcess);
    }

    /**
     * 修改流程模板信息
     * @author hedongwei@wistronits.com
     * @date 9:59 2019/2/12
     */
    @Test
    public void updateActProcess() {
        ActProcess actProcess = new ActProcess();
        actProcess.setIsDeploy("0");
        actProcess.setProcType("process");
        new Expectations() {
            {
                int num = actProcessDao.updateActProcess((ActProcess)any);
                result = num;
            }
        };
        actProcessServiceImpl.updateActProcess(actProcess);
    }

}
