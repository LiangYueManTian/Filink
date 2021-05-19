package com.fiberhome.filink.rfid.dao.opticcable;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.app.OperatorOpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.opticcable.app.OpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.statistics.opticable.CoreStatisticsReq;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;
import com.fiberhome.filink.rfid.resp.opticcable.app.OpticCableSectionInfoRespForApp;
import com.fiberhome.filink.rfid.resp.statistics.CoreStatisticsResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 光缆段信息表 Mapper 接口
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public interface OpticCableSectionInfoDao extends BaseMapper<OpticCableSectionInfo> {

    /**
     * 分页查询光缆段列表
     *
     * @param queryCondition 查询封装类
     * @return List<OpticCableSectionInfoResp>
     */
    List<OpticCableSectionInfoResp> selectOpticCableSection(QueryCondition<OpticCableSectionInfoReq> queryCondition);

    /**
     * 根据设备id列表获取光缆列表
     *
     * @param deviceIds 设备id列表
     * @return List<OpticCableSectionInfoResp>
     */
    List<OpticCableSectionInfoResp> opticCableSectionByDevice(@Param("deviceIds") List<String> deviceIds);

    /**
     * 光缆段总数
     *
     * @param queryCondition 查询封装类
     * @return Integer
     */
    Integer opticCableSectionByIdTotal(QueryCondition<OpticCableSectionInfoReq> queryCondition);

    /**
     * 拓扑根据光缆id查询光缆段列表
     *
     * @param opticCableSectionInfoReq 查询光缆段请求
     * @return List<OpticCableSectionInfoResp>
     */
    List<OpticCableSectionInfoResp> opticCableSectionByIdForTopology(OpticCableSectionInfoReq opticCableSectionInfoReq);

    /**
     * 通过光缆段id删除光缆段
     *
     * @param opticCableSectionInfoReq 光缆段请求
     * @return Integer
     */
    Integer deleteOpticCableSectionByOpticCableSectionId(OpticCableSectionInfoReq opticCableSectionInfoReq);

    /**
     * app请求光缆段基础信息
     *
     * @param opticCableSectionInfoReqForApp 查询方法
     * @return List<OpticCableSectionInfoRespForApp>
     */
    List<OpticCableSectionInfoRespForApp> queryOpticCableSectionListForApp(OpticCableSectionInfoReqForApp opticCableSectionInfoReqForApp);

    /**
     * app光缆段基础信息上传
     *
     * @param operatorOpticCableSectionInfoReqForApp 处理光缆段基础信息
     * @return int
     */
    int addOpticCableSectionInfoForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp);

    /**
     * app光缆段基础信息修改
     *
     * @param operatorOpticCableSectionInfoReqForApp 处理光缆段基础信息
     * @return int
     */
    int updateOpticCableSectionInfoForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp);

    /**
     * app光缆段基础信息删除
     *
     * @param operatorOpticCableSectionInfoReqForApp 处理光缆段基础信息
     * @return int
     */
    int deleteOpticCableSectionInfoForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp);

    /**
     * 通过光缆id查询所有光缆段信息
     *
     * @param opticCableSectionInfoReq 光缆段请求
     * @return List<OpticCableSectionInfo>
     */
    List<OpticCableSectionInfo> queryOpticCableSectionInfoByOpticCableId(OpticCableSectionInfoReq opticCableSectionInfoReq);

    /**
     * 根据光缆段id更新光缆段中已占用的纤芯数
     *
     * @param opticCableSectionInfo 光缆段信息
     * @return int
     */
    int updateOpticCableSectionUsedCoreNum(OpticCableSectionInfo opticCableSectionInfo);

    /**
     * 根据光缆段查询光缆段
     *
     * @param coreStatisticsReq 光缆段id List
     * @return OpticCableInfoSectionStatisticsResp
     */
    CoreStatisticsResp queryOpticCableSectionById(CoreStatisticsReq coreStatisticsReq);

    /**
     * 校验光缆段名字
     *
     * @param opticCableSectionName 光缆段名字
     * @param belongOpticCableId 所属光缆id
     *
     * @return OpticCableSectionInfo 光缆段信息
     */
    OpticCableSectionInfo queryOpticCableSectionByName(@Param("opticCableSectionName") String opticCableSectionName,@Param("belongOpticCableId") String belongOpticCableId);

    /**
     * 更新光缆段使用状态
     *
     * @param portCableSectionIdList 光缆段id列表
     * @param status 光缆段使用状态
     *
     * @return int
     */
    int updateOpticCableSectionStatusByIds(@Param("opticCableSectionIdList") Set<String> portCableSectionIdList,@Param("status") String status);
}
