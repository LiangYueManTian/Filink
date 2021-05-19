package com.fiberhome.filink.workflowserver.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowserver.req.*;

/**
 * @author hedongwei@wistronits.com
 * description 流程逻辑层
 * date 2018/11/30 10:27
 */
public interface ProcessService {

    /**
     * 发布流程模板
     * @author hedongwei@wistronits.com
     * @date 10:04 2019/2/12
     */
    void deploy();

    /**
     *  启动流程
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 启动流程的参数
     * @return  Result 返回的数据
     */
    Result startProcess(StartProcessReq req);

    /**
     *  办结任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 办结任务的参数
     * @return  Result 返回的数据
     */
    Result completeTask(CompleteTaskReq req);

    /**
     *  领办任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 领办任务的参数
     * @return  Result 返回的数据
     */
    Result claim(ClaimTaskReq req);

    /**
     *  会签任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 会签任务的参数
     * @return  Result 返回的数据
     */
    Result delegateTask(DelegateTaskReq req);

    /**
     *  转办任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 转办任务的参数
     * @return  Result 返回的数据
     */
    Result turnTask(TurnTaskReq req);

    /**
     * 修改流程状态
     * @author hedongwei@wistronits.com
     * @date  2019/4/3 19:28
     * @param procBase 修改对象的参数
     */
    void updateProcBaseStatusById(ProcBase procBase);

    /**
     * 提醒可以下载该工单的用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 12:16
     * @param procId 工单编号
     * @param assign 指派人
     */
    void noticeDownloadUser(String procId, String assign);

    /**
     * 获取当前流程的流程类型
     * @author hedongwei@wistronits.com
     * @date  2019/2/25 16:19
     * @param instanceId 当前流程正在执行的节点
     * @return 返回当前流程的流程类型
     */
    String getRuntimeProcType(String instanceId);
}
