package com.fiberhome.filink.workflowserver.dao;

import com.fiberhome.filink.workflowserver.bean.ActProcess;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.workflowserver.bean.ActProcessTaskConfig;

import java.util.List;

/**
 * <p>
 * 流程模板记录表 Mapper 接口
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-13
 */
public interface ActProcessDao extends BaseMapper<ActProcess> {

    /**
     * 获取流程模板信息
     * @author hedongwei@wistronits.com
     * @date 2018/11/30 10:12
     * @param actProcess 流程模板参数
     * @return List<ActProcess> 返回查询流程模板结果
     */
    List<ActProcess> getActProcess(ActProcess actProcess);

    /**
     * 修改流程模板的信息
     * @author hedongwei@wistronits.com
     * @date 2018/11/30 10:12
     * @param actProcess 修改流程模板参数
     * @return int 返回修改结果
     */
    int updateActProcess(ActProcess actProcess);


    /**
     * 查询流程配置信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/20
     * @param actProcessTaskConfig 查询条件
     * @return 返回流程配置结果
     */
    List<ActProcessTaskConfig> queryListProcessTaskConfig(ActProcessTaskConfig actProcessTaskConfig);

}
