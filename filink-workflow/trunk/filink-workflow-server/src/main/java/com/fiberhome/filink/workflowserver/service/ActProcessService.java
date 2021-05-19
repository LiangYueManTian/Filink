package com.fiberhome.filink.workflowserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.workflowserver.bean.ActProcess;

import java.util.List;

/**
 * <p>
 * 流程模板记录表 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-13
 */
public interface ActProcessService extends IService<ActProcess> {

    /**
     * 查询流程模板信息
     * @author hedongwei@wistronits.com
     * @date 10:14 2018/11/30
     * @param actProcess 查询条件
     * @return List<ActProcess> 流程模板列表
     */
    List<ActProcess> getActProcess(ActProcess actProcess);

    /**
     * 修改流程模板信息
     * @author hedongwei@wistronits.com
     * @date 10:14 2018/11/30
     * @param actProcess 修改流程信息
     * @return int 返回修改的结果
     */
    int updateActProcess(ActProcess actProcess);

}
