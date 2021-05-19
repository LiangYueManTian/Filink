package com.fiberhome.filink.workflowserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.workflowserver.bean.ActProcessTaskConfig;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-20
 */
public interface ActProcessTaskConfigDao extends BaseMapper<ActProcessTaskConfig> {

    /**
     * 查询任务参数
     * @author hedongwei@wistronits.com
     * @date 2019/2/20
     * @param actProcessTaskConfig 配置参数
     * @return 返回流程任务配置结果
     */
    List<ActProcessTaskConfig> queryListProcessTaskConfig(ActProcessTaskConfig actProcessTaskConfig);
}
