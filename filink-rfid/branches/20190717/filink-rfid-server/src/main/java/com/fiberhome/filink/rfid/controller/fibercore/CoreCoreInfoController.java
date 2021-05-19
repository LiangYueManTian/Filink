package com.fiberhome.filink.rfid.controller.fibercore;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.service.fibercore.CoreCoreInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 熔接信息表 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
@RestController
@RequestMapping("/coreCoreInfo")
public class CoreCoreInfoController {

    @Autowired
    private CoreCoreInfoService coreCoreInfoService;

    /**
     * 获取熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     *
     * @return Result
     */
    @PostMapping("/queryCoreCoreInfo")
    public Result queryCoreCoreInfo(@RequestBody CoreCoreInfoReq coreCoreInfoReq) {
        return coreCoreInfoService.queryCoreCoreInfo(coreCoreInfoReq);
    }

    /**
     * 获取其他设施熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     *
     * @return Result
     */
    @PostMapping("/queryCoreCoreInfoNotInDevice")
    public Result queryCoreCoreInfoNotInDevice(@RequestBody CoreCoreInfoReq coreCoreInfoReq) {
        return coreCoreInfoService.queryCoreCoreInfoNotInDevice(coreCoreInfoReq);
    }

    /**
     * 保存熔纤信息
     *
     * @param insertCoreCoreInfoReqList 新增熔纤信息请求列表
     *
     * @return Result
     */
    @PostMapping("/saveCoreCoreInfo")
    public Result saveCoreCoreInfo(@RequestBody List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList){
        return coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList);
    }

    /**
     * app请求纤芯熔接信息
     *
     * @param queryCoreCoreInfoReqForApp app请求纤芯熔接信息
     *
     * @return Result
     */
    @PostMapping("/queryCoreCoreInfoForApp")
    public Result queryCoreCoreInfoForApp(@RequestBody QueryCoreCoreInfoReqForApp queryCoreCoreInfoReqForApp){
        return coreCoreInfoService.queryCoreCoreInfoForApp(queryCoreCoreInfoReqForApp);
    }

    /**
     * app处理熔纤信息
     *
     * @param batchOperationCoreCoreInfoReqForApp app熔纤信息列表
     *
     * @return Result
     */
    @PostMapping("/operationCoreCoreInfoReqForApp")
    public Result operationCoreCoreInfoReqForApp(@RequestBody BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp){
        return coreCoreInfoService.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp);
    }

}
