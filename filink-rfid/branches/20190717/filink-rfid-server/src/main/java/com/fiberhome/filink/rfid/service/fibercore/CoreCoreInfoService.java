package com.fiberhome.filink.rfid.service.fibercore;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.bean.fibercore.CoreCoreInfo;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryCoreCoreInfoReqForApp;

import java.util.List;

/**
 * <p>
 * 熔接信息表 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
public interface CoreCoreInfoService extends IService<CoreCoreInfo> {

    /**
     * 获取熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     *
     * @return Result
     */
    Result queryCoreCoreInfo(CoreCoreInfoReq coreCoreInfoReq);

    /**
     * 获取其他设施熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     *
     * @return Result
     */
    Result queryCoreCoreInfoNotInDevice(CoreCoreInfoReq coreCoreInfoReq);

    /**
     * 保存熔纤信息
     *
     * @param insertCoreCoreInfoReqList 新增熔纤信息请求列表
     *
     * @return Result
     */
    Result saveCoreCoreInfo(List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList);

    /**
     * app请求纤芯熔接信息
     *
     * @param queryCoreCoreInfoReqForApp app请求纤芯熔接信息
     *
     * @return Result
     */
    Result queryCoreCoreInfoForApp(QueryCoreCoreInfoReqForApp queryCoreCoreInfoReqForApp);

    /**
     * app处理熔纤信息
     *
     * @param batchOperationCoreCoreInfoReqForApp app熔纤信息列表
     *
     * @return Result
     */
    Result operationCoreCoreInfoReqForApp(BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp);

}
