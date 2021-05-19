package com.fiberhome.filink.rfid.controller.fibercore;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.req.fibercore.DeleteJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationJumpFiberInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.service.fibercore.JumpFiberInfoService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 跳纤信息表 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
@RestController
@RequestMapping("/jumpFiberInfo")
public class JumpFiberInfoController {

    @Autowired
    private JumpFiberInfoService jumpFiberInfoService;

    /**
     * 获取跳接信息
     *
     * @param queryJumpFiberInfoReq 跳接信息请求类
     * @return Result
     */
    @PostMapping("/queryJumpFiberInfoByPortInfo")
    public Result queryJumpFiberInfoByPortInfo(@RequestBody QueryJumpFiberInfoReq queryJumpFiberInfoReq) {
        return jumpFiberInfoService.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);
    }

    /**
     * 跳接信息删除
     *
     * @param deleteJumpFiberInfoReq 删除跳接信息请求类
     * @return Result
     */
    @PostMapping("/deleteJumpFiberInfoById")
    public Result deleteJumpFiberInfoById(@RequestBody DeleteJumpFiberInfoReq deleteJumpFiberInfoReq) {
        return jumpFiberInfoService.deleteJumpFiberInfoById(deleteJumpFiberInfoReq);
    }

    /**
     * 跳接信息导出
     *
     * @param exportDto 光缆段列表导出请求
     * @return Result
     */
    @PostMapping("/exportJumpFiberList")
    public Result exportJumpFiberList(@RequestBody ExportDto<QueryJumpFiberInfoReq> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        return jumpFiberInfoService.exportJumpFiberList(exportDto);
    }

    /**
     * app根据端口信息获取跳接信息
     *
     * @param queryJumpFiberInfoReqForApp app跳接信息请求类
     * @return Result
     */
    @PostMapping("/queryJumpFiberInfoByPortInfoForApp")
    public Result queryJumpFiberInfoByPortInfoForApp(@RequestBody QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp) {
        return jumpFiberInfoService.queryJumpFiberInfoByPortInfoForApp(queryJumpFiberInfoReqForApp);
    }

    /**
     * app处理跳接信息
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息请求类
     * @return Result
     */
    @PostMapping("/operationJumpFiberInfoReqForApp")
    public Result operationJumpFiberInfoReqForApp(@RequestBody BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp) {
        return jumpFiberInfoService.operationJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
    }

}
