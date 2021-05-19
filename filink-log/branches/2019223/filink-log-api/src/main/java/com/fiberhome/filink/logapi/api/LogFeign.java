package com.fiberhome.filink.logapi.api;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.logapi.fallback.LogFeignFallback;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.logapi.req.AddSecurityLogReq;
import com.fiberhome.filink.logapi.req.AddSystemLogReq;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author hedongwei@wistronits.com
 * Logferign接口
 * 13:41 2019/1/19
 */
@FeignClient(name = "filink-log-server",fallback = LogFeignFallback.class)
public interface LogFeign {

    /**
     * 新增操作日志
     * @author hedongwei@wistronits.com
     * @date 15:50 2019/1/22
     * @param operateLogReq 操作日志参数
     * @return 返回新增操作日志结果
     */
    @PostMapping("/log/addOperateLog")
    Result addOperateLog(@RequestBody AddOperateLogReq operateLogReq);

    /**
     * 新增安全日志
     * @author hedongwei@wistronits.com
     * @date 15:50 2019/1/22
     * @param securityLogReq 安全日志参数
     * @return 返回新增安全日志结果
     */
    @PostMapping("/log/addSecurityLog")
    Result addSecurityLog(@RequestBody AddSecurityLogReq securityLogReq);

    /**
     * 新增系统日志
     * @author hedongwei@wistronits.com
     * @date 15:50 2019/1/22
     * @param systemLogReq 系统日志参数
     * @return 返回新增系统日志结果
     */
    @PostMapping("/log/addSystemLog")
    Result addSystemLog(@RequestBody AddSystemLogReq systemLogReq);
}
