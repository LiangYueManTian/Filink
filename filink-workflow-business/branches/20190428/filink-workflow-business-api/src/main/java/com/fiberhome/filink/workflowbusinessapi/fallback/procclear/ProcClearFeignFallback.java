package com.fiberhome.filink.workflowbusinessapi.fallback.procclear;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowbusinessapi.api.procclear.ProcClearFeign;
import com.fiberhome.filink.workflowbusinessapi.req.procclear.InsertClearFailureReq;
import com.fiberhome.filink.workflowbusinessapi.utils.procclear.ProcClearResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author chaofanrong@wistronits.com
 * ferign 熔断
 * 15:50 2019/4/16
 */
@Slf4j
@Component
public class ProcClearFeignFallback implements ProcClearFeign {

    private static final String INFO = "Proc clear feign call fallback";

    /**
     * 新增销障工单
     * @author chaofanrong@wistronits.com
     * @date 15:50 2019/4/16
     * @param insertClearFailureReq 新增销障工单
     * @return 新增销障工单结果
     */
    @Override
    public Result addClearFailureProc(InsertClearFailureReq insertClearFailureReq) {
        String addClearProcErrorMsg = "Add clear failure proc error";
        log.info(INFO);
        return ResultUtils.warn(ProcClearResultCode.ADD_CLEAR_PROC_ERROR, addClearProcErrorMsg);
    }
}
