package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.workflowserver.bean.ActProcessTaskConfig;
import com.fiberhome.filink.workflowserver.dao.ActProcessTaskConfigDao;
import com.fiberhome.filink.workflowserver.service.ActProcessTaskConfigService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-20
 */
@Service
public class ActProcessTaskConfigServiceImpl extends ServiceImpl<ActProcessTaskConfigDao, ActProcessTaskConfig> implements ActProcessTaskConfigService {

    @Autowired
    private ActProcessTaskConfigDao actProcessTaskConfigDao;

    /**
     * 查询任务参数
     * @author hedongwei@wistronits.com
     * @date 2019/2/20
     * @param actProcessTaskConfig 配置参数
     * @return List<ActProcessTaskConfig> 任务配置列表
     */
    @Override
    public List<ActProcessTaskConfig> queryListProcessTaskConfig(ActProcessTaskConfig actProcessTaskConfig) {
        return actProcessTaskConfigDao.queryListProcessTaskConfig(actProcessTaskConfig);
    }
}
