package com.fiberhome.filink.workflowserver.configuration;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.workflowserver.listener.ActivitiStatusListener;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 流程配置信息
 * @author hedongwei@wistronits.com
 * @date 2019/2/20 17:45
 */
@Component
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

    @Autowired
    private ActivitiStatusListener activitiAllListener;

    /**
     * 初始化流程引擎对象
     * @author hedongwei@wistronits.com
     * @date  2019/2/20 18:09
     * @param processEngineConfiguration 流程配置信息
     */
    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        //配置流程引擎对象的生成id策略为生成uuid
        processEngineConfiguration.setIdGenerator(new IdGenerator() {
            @Override
            public String getNextId() {
                return NineteenUUIDUtils.uuid();
            }
        });
    }
}
