package com.fiberhome.filink.rfid.service.opticcable;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.app.OperatorOpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.opticcable.app.OpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;

import java.util.List;
import java.util.concurrent.Future;

/**
 * <p>
 * 光缆段信息表 服务类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public interface OpticCableSectionInfoService extends IService<OpticCableSectionInfo> {

    /**
     * 查询光缆段列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result selectOpticCableSection(QueryCondition<OpticCableSectionInfoReq> queryCondition);

    /**
     * 组装光缆段信息
     *
     * @param opticCableSectionInfoRespList 光缆段信息列表
     * @return List<OpticCableSectionInfoResp>
     */
    List<OpticCableSectionInfoResp> assemblyOpticCableSectionInfoResp(List<OpticCableSectionInfoResp> opticCableSectionInfoRespList);

    /**
     * 查询光缆段列表
     *
     * @param opticCableSectionInfoReq 光缆段请求
     * @return Result
     */
    Result opticCableSectionByIdForTopology(OpticCableSectionInfoReq opticCableSectionInfoReq);

    /**
     * 根据设备id查询光缆段列表
     *
     * @param deviceId 设备id
     * @return Result
     */
    Result selectOpticCableSectionByDeviceId(String deviceId);

    /**
     * 通过光缆id查询所有设施信息
     *
     * @param opticCableId 光缆id
     * @return Result
     */
    Result queryDeviceInfoListByOpticCableId(String opticCableId);

    /**
     * 通过光缆段id删除光缆段
     *
     * @param opticCableSectionId 光缆段id
     * @return Result
     */
    Result deleteOpticCableSectionByOpticCableSectionId(String opticCableSectionId);

    /**
     * app请求光缆段基础信息
     *
     * @param opticCableSectionInfoReqForApp app光缆段请求类
     * @return Result
     */
    Result queryOpticCableSectionListForApp(OpticCableSectionInfoReqForApp opticCableSectionInfoReqForApp);

    /**
     * app光缆段基础信息上传
     *
     * @param operatorOpticCableSectionInfoReqForApp app光缆段互利请求类
     * @return Result
     */
    Result uploadOpticCableSectionInfoForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp);

    /**
     * 光缆段列表导出
     *
     * @param exportDto 光缆段列表导出请求
     * @return Result
     */
    Result exportOpticCableSectionList(ExportDto exportDto);

    /**
     * 计算光缆段已使用的纤芯数
     *
     * @param opticCableSectionId 光缆段id
     * @return int
     */

    Future<Integer> coreStatisticsCount(String opticCableSectionId);

    /**
     * 校验光缆段名字
     *
     * @param operatorOpticCableSectionInfoReqForApp 光缆段请求
     *
     * @return Boolean
     */
    Boolean checkOpticCableSectionNameForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp);

    /**
     * 校验设施是否有权限
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 14:16
     * @param deviceIdList 设施编号集合
     * @return 设施信息
     */
    Result checkIsPermissionDeviceByDeviceIdList(List<String> deviceIdList);


    /**
     * 校验用户同时拥有需要查询的设施的权限
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 14:16
     * @param deviceIdList 设施编号集合
     * @return 设施信息
     */
    Result existIsPermissionDeviceByDeviceIdList(List<String> deviceIdList);

}
