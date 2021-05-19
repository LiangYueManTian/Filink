package com.fiberhome.filink.rfid.dao.fibercore;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.PortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.resp.fibercore.app.PortCableCoreInfoRespForApp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 成端信息表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
public interface PortCableCoreInfoDao extends BaseMapper<PortCableCoreInfo> {

    /**
     * 获取成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return List<PortCableCoreInfo>
     */
    List<PortCableCoreInfo> getPortCableCoreInfo(PortCableCoreInfoReq portCableCoreInfoReq);

    /**
     * 获取其他设施成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return List<PortCableCoreInfo>
     */
    List<PortCableCoreInfo> getPortCableCoreInfoNotInDevice(PortCableCoreInfoReq portCableCoreInfoReq);

    /**
     * 删除该光缆段和该设施原有成端信息
     *
     * @param portCableCoreInfoReq 成端信息请求
     *
     * @return int
     */
    int deletePortCoreInfoByResourceAndDevice(PortCableCoreInfoReq portCableCoreInfoReq);

    /**
     * 保存成端信息
     *
     * @param insertPortCableCoreInfoReqList 新增成端信息请求列表
     *
     * @return int
     */
    int savePortCableCoreInfo(@Param("insertPortCableCoreInfoReqList") List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList);

    /**
     * app请求纤芯成端信息
     *
     * @param queryPortCableCoreInfoReqForApp app请求纤芯成端信息
     *
     * @return Result
     */
    List<PortCableCoreInfoRespForApp> queryPortCableCoreInfoForApp(QueryPortCableCoreInfoReqForApp queryPortCableCoreInfoReqForApp);

    /**
     * app删除当前成端信息（待定）
     *
     * @param portCableCoreInfoReqForApp 成端信息请求
     *
     * @return int
     */
    int deletePortCoreInfoByPortCoreInfoForApp(PortCableCoreInfoReqForApp portCableCoreInfoReqForApp);

    /**
     * app删除该设施、框、盘、端口原有成端信息（待定）
     *
     * @param portCableCoreInfoReqForApp 成端信息请求
     *
     * @return int
     */
    int deletePortCoreInfoByPortForApp(PortCableCoreInfoReqForApp portCableCoreInfoReqForApp);

    /**
     * app保存成端信息
     *
     * @param operationPortCableCoreInfoReqForAppList app新增成端信息请求列表
     *
     * @return int
     */
    int savePortCableCoreInfoForApp(@Param("operationPortCableCoreInfoReqForAppList") List<OperationPortCableCoreInfoReqForApp> operationPortCableCoreInfoReqForAppList);
}
