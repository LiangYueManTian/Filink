package com.fiberhome.filink.workflowbusinessserver.service.process;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessserver.req.process.*;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;

import java.util.List;

/**
 * <p>
 * 流程 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
public interface WorkflowService {

    /**
     * 启动流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 流程参数
     * @return 返回启动流程结果
     */
    Result startProcess(StartProcessInfoReq req);

    /**
     * 办结流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 流程参数
     * @return 返回办结流程结果
     */
    Result completeProcess(CompleteProcessInfoReq req);

    /**
     * 退单流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 退单参数
     * @return 返回退单结果
     */
    Result singleBackProcess(SingleBackInfoReq req);

    /**
     * 撤回流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 撤回参数
     * @return 返回撤回结果
     */
    Result revokeProcess(RevokeInfoReq req);

    /**
     * 转派流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 14:32
     * @param req 转派参数
     * @return 返回转派结果
     */
    Result turnProcess(TurnInfoReq req);

    /**
     * app批量下载工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 20:18
     * @param procBaseRespForAppList
     */
    void appDownloadProcBatch(List<ProcBaseRespForApp> procBaseRespForAppList);
}
