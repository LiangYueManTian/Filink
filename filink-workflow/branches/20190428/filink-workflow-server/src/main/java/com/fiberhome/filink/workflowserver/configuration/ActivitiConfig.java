package com.fiberhome.filink.workflowserver.configuration;

import com.fiberhome.filink.workflowserver.listener.ActivitiStatusListener;
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
     * 添加全局监听信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/20 18:09
     * @param processEngineConfiguration 流程配置信息
     */
    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        //配置全局监听器
       /* List<ActivitiEventListener> activitiEventListenerList = new ArrayList<ActivitiEventListener>();
        activitiEventListenerList.add(activitiAllListener);
        processEngineConfiguration.setEventListeners(activitiEventListenerList);*/
    }
}
