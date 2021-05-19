package com.fiberhome.filink.rfid.controller.fibercore;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.SavePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.service.fibercore.PortCableCoreInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 成端信息表 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
@RestController
@RequestMapping("/portCableCoreInfo")
public class PortCableCoreInfoController {

    @Autowired
    private PortCableCoreInfoService portCableCoreInfoService;

    /**
     * 获取当前设施成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return Result
     */
    @PostMapping("/getPortCableCoreInfo")
    public Result getPortCableCoreInfo(@RequestBody PortCableCoreInfoReq portCableCoreInfoReq){
        return portCableCoreInfoService.getPortCableCoreInfo(portCableCoreInfoReq);
    }

    /**
     * 获取其他设施成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return Result
     */
    @PostMapping("/getPortCableCoreInfoNotInDevice")
    public Result getPortCableCoreInfoNotInDevice(@RequestBody PortCableCoreInfoReq portCableCoreInfoReq){
        return portCableCoreInfoService.getPortCableCoreInfoNotInDevice(portCableCoreInfoReq);
    }

    /**
     * 保存成端信息
     *
     * @param savePortCableCoreInfoReq 保存成端信息请求
     *
     * @return Result
     */
    @PostMapping("/savePortCableCoreInfo")
    public Result savePortCableCoreInfo(@RequestBody SavePortCableCoreInfoReq savePortCableCoreInfoReq){
        return portCableCoreInfoService.savePortCableCoreInfo(savePortCableCoreInfoReq.getInsertPortCableCoreInfoReqList(),savePortCableCoreInfoReq.getUpdatePortCableCoreInfoReqList());
    }

    /**
     * app请求纤芯成端信息
     *
     * @param queryPortCableCoreInfoReqForApp app请求纤芯成端信息
     *
     * @return Result
     */
    @PostMapping("/queryPortCableCoreInfoForApp")
    public Result queryPortCableCoreInfoForApp(@RequestBody QueryPortCableCoreInfoReqForApp queryPortCableCoreInfoReqForApp){
        return portCableCoreInfoService.queryPortCableCoreInfoForApp(queryPortCableCoreInfoReqForApp);
    }

    /**
     * app处理纤芯成端信息
     *
     * @param batchOperationPortCableCoreInfoReqForApp app处理成端信息请求
     *
     * @return Result
     */
    @PostMapping("/operationPortCableCoreInfoForApp")
    public Result operationPortCableCoreInfoForApp(@RequestBody BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp){
        return portCableCoreInfoService.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp);
    }
}