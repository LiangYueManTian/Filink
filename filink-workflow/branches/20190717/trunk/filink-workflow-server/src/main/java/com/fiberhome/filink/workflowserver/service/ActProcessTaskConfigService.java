package com.fiberhome.filink.workflowserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.workflowserver.bean.ActProcessTaskConfig;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-20
 */
public interface ActProcessTaskConfigService extends IService<ActProcessTaskConfig> {

    /**
     * 查询任务参数
     * @author hedongwei@wistronits.com
     * @date 2019/2/20
     * @param actProcessTaskConfig 配置参数
     * @return List<ActProcessTaskConfig> 任务配置列表
     */
    List<ActProcessTaskConfig> queryListProcessTaskConfig(ActProcessTaskConfig actProcessTaskConfig);
}
