package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.workflowserver.bean.ActProcess;
import com.fiberhome.filink.workflowserver.dao.ActProcessDao;
import com.fiberhome.filink.workflowserver.service.ActProcessService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 流程模板记录表 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-13
 */
@Service
public class ActProcessServiceImpl extends ServiceImpl<ActProcessDao, ActProcess> implements ActProcessService {

    @Autowired
    private ActProcessDao actProcessDao;

    /**
     * 获得需要发布的流程模板
     * @author hedongwei@wistronits.com
     * @date 9:59 2019/2/12
     * @param actProcess 查询条件
     * @return List<ActProcess> 返回流程模板信息
     */
    @Override
    public List<ActProcess> getActProcess(ActProcess actProcess) {
        return actProcessDao.getActProcess(actProcess);
    }

    /**
     * 修改流程模板信息
     * @author hedongwei@wistronits.com
     * @date 9:59 2019/2/12
     * @param actProcess 修改参数
     * @return int 返回操作结果
     */
    @Override
    public int updateActProcess(ActProcess actProcess) {
        return actProcessDao.updateActProcess(actProcess);
    }
}
