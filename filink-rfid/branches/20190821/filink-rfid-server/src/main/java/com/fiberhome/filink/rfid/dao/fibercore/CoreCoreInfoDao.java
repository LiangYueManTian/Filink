package com.fiberhome.filink.rfid.dao.fibercore;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.rfid.bean.fibercore.CoreCoreInfo;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.CoreCoreInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.rfid.DeleteCoreInfoReq;
import com.fiberhome.filink.rfid.resp.fibercore.CoreCoreInfoResp;
import com.fiberhome.filink.rfid.resp.fibercore.app.CoreCoreInfoRespForApp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 熔接信息表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
public interface CoreCoreInfoDao extends BaseMapper<CoreCoreInfo> {

    /**
     * 获取熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     * @return Result
     */
    List<CoreCoreInfoResp> queryCoreCoreInfo(CoreCoreInfoReq coreCoreInfoReq);

    /**
     * 获取其他设施熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     * @return List<CoreCoreInfoResp>
     */
    List<CoreCoreInfoResp> queryCoreCoreInfoNotInDevice(CoreCoreInfoReq coreCoreInfoReq);

    /**
     * 删除该光缆段和该设施原有熔纤信息
     *
     * @param coreCoreInfoReq 熔纤信息请求
     * @return int
     */
    int deleteCoreCoreInfoByResourceAndDevice(CoreCoreInfoReq coreCoreInfoReq);

    /**
     * 新增熔纤信息
     *
     * @param insertCoreCoreInfoReqList 新增熔纤信息请求列表
     * @return int
     */
    int addCoreCoreInfo(@Param("insertCoreCoreInfoReqList") List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList);

    /**
     * 根据设施id获取熔纤信息
     *
     * @param deviceId 设施id
     * @return List<CoreCoreInfoResp>
     */
    List<CoreCoreInfoResp> queryCoreCoreInfoByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 根据设施id列表获取熔纤信息
     *
     * @param deviceIds 设施id列表
     * @return List<CoreCoreInfoResp>
     */
    List<CoreCoreInfoResp> queryCoreCoreInfoByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    /**
     * 根据光缆id列表获取熔纤信息
     *
     * @param opticCableId 设施id列表
     * @return List<CoreCoreInfoResp>
     */
    List<CoreCoreInfoResp> queryCoreCoreInfoByOpticCableId(@Param("opticCableId") String opticCableId);

    /**
     * app请求纤芯熔接信息
     *
     * @param queryCoreCoreInfoReqForApp app获取熔纤信息请求
     * @return List<CoreCoreInfoRespForApp>
     */
    List<CoreCoreInfoRespForApp> queryCoreCoreInfoForApp(QueryCoreCoreInfoReqForApp queryCoreCoreInfoReqForApp);

    /**
     * app删除当前熔纤信息（正反匹配删除-待定）
     *
     * @param coreCoreInfoForApp app熔纤信息
     * @return int
     */
    int deleteCoreCoreInfoByCoreCoreInfoForApp(CoreCoreInfoForApp coreCoreInfoForApp);

    /**
     * app删除本端及对端纤芯及该中间节点设施原有熔纤信息（待定）
     *
     * @param coreCoreInfoForApp app熔纤信息
     * @return int
     */
    int deleteCoreCoreInfoByCoreAndDeviceForApp(CoreCoreInfoForApp coreCoreInfoForApp);

    /**
     * app熔纤关系信息保存
     *
     * @param operationCoreCoreInfoReqForAppList app处理熔纤信息请求
     * @return int
     */
    int saveCoreCoreInfoForApp(@Param("operationCoreCoreInfoReqForAppList") List<OperationCoreCoreInfoReqForApp> operationCoreCoreInfoReqForAppList);

    /**
     * delete 熔纤
     */
    void deleteCoreCoreINnfoForApp(List<DeleteCoreInfoReq> list);
}
