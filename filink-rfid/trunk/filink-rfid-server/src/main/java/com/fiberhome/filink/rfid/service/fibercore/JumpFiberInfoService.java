package com.fiberhome.filink.rfid.service.fibercore;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import com.fiberhome.filink.rfid.req.fibercore.DeleteJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationJumpFiberInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;

import java.util.List;

/**
 * <p>
 * 跳纤信息表 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
public interface JumpFiberInfoService extends IService<JumpFiberInfo> {

    /**
     * 获取跳接信息
     *
     * @param queryJumpFiberInfoReq 跳接信息请求类
     *
     * @return Result
     */
    Result queryJumpFiberInfoByPortInfo(QueryJumpFiberInfoReq queryJumpFiberInfoReq);

    /**
     * 跳接信息删除
     *
     * @param deleteJumpFiberInfoReq 删除跳接信息请求类
     *
     * @return Result
     */
    Result deleteJumpFiberInfoById(DeleteJumpFiberInfoReq deleteJumpFiberInfoReq);

    /**
     * 跳接列表导出
     *
     * @param exportDto 跳接列表导出请求
     *
     * @return Result
     */
    Result exportJumpFiberList(ExportDto exportDto);

    /**
     * app根据端口信息获取跳接信息
     *
     * @param queryJumpFiberInfoReqForApp app跳接信息请求类
     *
     * @return Result
     */
    Result queryJumpFiberInfoByPortInfoForApp(QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp);

    /**
     * app处理跳接信息
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息列表
     *
     * @return Result
     */
    Result operationJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp);

    /**
     * 组装跳接信息
     *
     * @param jumpFiberInfoRespList 跳接信息列表
     * @param queryJumpFiberInfoReq 跳接信息请求
     *
     * @return jumpFiberInfoRespList 跳接信息列表
     */
    List<JumpFiberInfoResp> assemblyJumpFiberInfoResp(List<JumpFiberInfoResp> jumpFiberInfoRespList, QueryJumpFiberInfoReq queryJumpFiberInfoReq);
}
