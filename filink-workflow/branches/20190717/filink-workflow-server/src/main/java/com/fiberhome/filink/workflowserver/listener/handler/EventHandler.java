package com.fiberhome.filink.workflowserver.listener.handler;

import org.activiti.engine.delegate.event.ActivitiEvent;

/**
 * 事件处理
 * @author hedongwei@wistronits.com
 * @date 2019/2/20 18:28
 */
public interface EventHandler {

    /**
     * 事件处理器
     * @param event
     */
    public void handle(ActivitiEvent event);
}
