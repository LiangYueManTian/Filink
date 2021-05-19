package com.fiberhome.filink.rfid.dao.fibercore;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.JumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.statistics.jumpconnection.JumpConnectionStatisticsReq;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.fibercore.app.JumpFiberInfoRespForApp;
import com.fiberhome.filink.rfid.resp.statistics.JumpConnectionStatisticsResp;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 跳纤信息表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
public interface JumpFiberInfoDao extends BaseMapper<JumpFiberInfo> {

    /**
     * 获取跳接信息
     *
     * @param queryJumpFiberInfoReq 跳接信息请求类
     * @return List<JumpFiberInfoResp>
     */
    List<JumpFiberInfoResp> queryJumpFiberInfoByPortInfo(QueryJumpFiberInfoReq queryJumpFiberInfoReq);

    /**
     * 删除跳接信息请求
     *
     * @param jumpFiberIdList 删除跳接信息列表
     * @param updateTime      更新时间
     * @param updateUser      更新人
     * @return int
     */
    int deleteJumpFiberInfoById(@Param("jumpFiberIdList") Set<String> jumpFiberIdList, @Param("updateTime") Date updateTime, @Param("updateUser") String updateUser);

    /**
     * app根据端口信息获取跳接信息
     *
     * @param queryJumpFiberInfoReqForApp app跳接信息请求类
     * @return List<JumpFiberInfoRespForApp>
     */
    List<JumpFiberInfoRespForApp> queryJumpFiberInfoByPortInfoForApp(QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp);

    /**
     * app删除当前跳接信息（正反匹配删除）
     *
     * @param jumpFiberInfoReqForApp app熔纤信息
     * @return int
     */
    int deleteJumpFiberInfoByJumpFiberInfoForApp(JumpFiberInfoReqForApp jumpFiberInfoReqForApp);

    /**
     * 删除该端口原有跳接信息
     *
     * @param jumpFiberInfoReqForApp 跳接信息请求
     * @return int
     */
    int deleteJumpFiberInfoByPortInfoFroApp(JumpFiberInfoReqForApp jumpFiberInfoReqForApp);

    /**
     * 删除分路器端口原有跳接信息
     *
     * @param jumpFiberInfoReqForApp 跳接信息请求
     * @return int
     */
    int deleteJumpFiberInfoByPortInfoToBranchingUnitFroApp(JumpFiberInfoReqForApp jumpFiberInfoReqForApp);

    /**
     * 新增跳接信息
     *
     * @param operationJumpFiberInfoReqForAppList app新增跳接信息列表
     * @return int
     */
    int addJumpFiberInfo(@Param("operationJumpFiberInfoReqForAppList") List<OperationJumpFiberInfoReqForApp> operationJumpFiberInfoReqForAppList);

    /**
     * 获取柜内跳接
     *
     * @param jumpConnectionStatisticsReq 跳接信息请求类
     * @return List<JumpConnectionStatisticsResp>
     */
    List<JumpConnectionStatisticsResp> queryInCabinet(JumpConnectionStatisticsReq jumpConnectionStatisticsReq);

    /**
     * 获取柜间跳接
     *
     * @param jumpConnectionStatisticsReq 跳接信息请求类
     * @return List<JumpConnectionStatisticsResp>
     */
    List<JumpConnectionStatisticsResp> queryOutCabinet(JumpConnectionStatisticsReq jumpConnectionStatisticsReq);

    /**
     * 根据设施id查询跳接关系
     *
     * @param list 设施ID
     * @return List<JumpConnectionStatisticsResp>
     */
    List<JumpConnectionStatisticsResp> queryJumpFiberInfoByDeviceId(@Param("list") List list);

    /**
     * 修改本端跳接智能标签id
     *
     * @param operationJumpFiberInfoReqForApp 跳接请求
     * @return int
     */
    int updateThisRfIdCodeForApp(OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp);

    /**
     * 修改对端跳接智能标签id
     *
     * @param operationJumpFiberInfoReqForApp 跳接请求
     * @return int
     */
    int updateOppositeRfIdCodeForApp(OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp);

    /**
     * 根据智能标签id获取跳接信息
     *
     * @param rfidCode 智能标签id
     * @return List<JumpFiberInfo>
     */
    List<JumpFiberInfo> getJumpFiberInfoByRfidCode(String rfidCode);

    /**
     * app获取跳接rfid_code信息（正反匹配删除）
     *
     * @param queryJumpFiberInfoReq 查询跳接
     * @return List<JumpFiberInfo>
     */
    List<JumpFiberInfo> getJumpFiberRfidCodeByJumpFiberInfoForApp(QueryJumpFiberInfoReq queryJumpFiberInfoReq);

    /**
     * 修改适配器类型
     *
     * @param adapterType 适配器类型
     * @param rfIdCode    rfIdCode
     */
    void updateJumpAdapterType(@Param("adapterType") Integer adapterType, @Param("rfIdCode") String rfIdCode);
}
