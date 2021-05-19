package com.fiberhome.filink.rfid.service.fibercore;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.req.fibercore.UpdatePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryPortCableCoreInfoReqForApp;

import java.util.List;

/**
 * <p>
 * 成端信息表 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
public interface PortCableCoreInfoService extends IService<PortCableCoreInfo> {

    /**
     * 获取成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return Result
     */
    Result getPortCableCoreInfo(PortCableCoreInfoReq portCableCoreInfoReq);

    /**
     * 获取成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return Result
     */
    Result getPortCableCoreInfoNotInDevice(PortCableCoreInfoReq portCableCoreInfoReq);

    /**
     * 保存成端信息
     *
     * @param insertPortCableCoreInfoReqList 新增成端信息请求列表
     * @param updatePortCableCoreInfoReqList 更相信端口信息请求列表
     *
     * @return Result
     */
    Result savePortCableCoreInfo(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList,List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList);

    /**
     * app请求纤芯成端信息
     *
     * @param queryPortCableCoreInfoReqForApp app请求纤芯成端信息
     *
     * @return Result
     */
    Result queryPortCableCoreInfoForApp(QueryPortCableCoreInfoReqForApp queryPortCableCoreInfoReqForApp);

    /**
     * app处理纤芯成端信息
     *
     * @param batchOperationPortCableCoreInfoReqForApp app成端信息请求
     *
     * @return Result
     */
    Result operationPortCableCoreInfoForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp);

}
