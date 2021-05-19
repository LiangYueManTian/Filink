package com.fiberhome.filink.workflowbusinessapi.api.procclear;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessapi.fallback.procclear.ProcClearFeignFallback;
import com.fiberhome.filink.workflowbusinessapi.req.procclear.InsertClearFailureReq;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author chaofanrong@wistronits.com
 * ProcClearFeign接口
 * 15:50 2019/4/16
 */
@FeignClient(name = "filink-workflow-business-server" , path = "/procClearFailure",fallback = ProcClearFeignFallback.class)
public interface ProcClearFeign {

    /**
     * 新增销障工单
     * @author chaofanrong@wistronits.com
     * @date 15:50 2019/4/16
     * @param insertClearFailureReq 新增销障工单
     * @return 新增销障工单结果
     */
    @PostMapping("/addClearFailureProc")
    Result addClearFailureProc(@RequestBody InsertClearFailureReq insertClearFailureReq);
}
