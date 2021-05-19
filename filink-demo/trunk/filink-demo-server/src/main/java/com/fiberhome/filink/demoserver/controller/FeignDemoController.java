package com.fiberhome.filink.demoserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例demo---远程调用
 *
 * @author 姚远
 */
@RestController
@RequestMapping("/rpc")
public class FeignDemoController {

    /**
     * 注入第三方服务
     *
     * 电子锁服务提供的api
     */
    @Autowired
    private ControlFeign controlFeign;

    /**
     * 远程调用电子锁服务的ping接口查看服务是否启动
     *
     * @return 提示信息
     */
    @GetMapping
    public Result rpcDemo() {
        String str = "";
        if (controlFeign.ping()) {
            str = "LOCK SERVICE IS OK!";
        }else {
            str ="LOCK SERVICE IS ERROR";
        }
        return ResultUtils.success(str);
    }
}
