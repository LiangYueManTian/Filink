package com.fiberhome.filink.rfid.service.rfid;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;
import com.fiberhome.filink.rfid.req.rfid.DeleteOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.OpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.UpdateOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.app.OpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;

import java.util.List;

/**
 * <p>
 * 光缆段rfid信息表 服务类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-30
 */
public interface OpticCableSectionRfidInfoService extends IService<OpticCableSectionRfidInfo> {

    /**
     * 查询光缆段智能标签列表
     *
     * @param queryCondition 查询条件
     * @return Result
     */
    Result opticCableSectionRfidInfoById(QueryCondition<OpticCableSectionRfidInfoReq> queryCondition);

    /**
     * app查询光缆段智能标签列表
     *
     * @param opticCableSectionRfidInfoReqApp 查询条件
     * @return Result
     */
    Result queryOpticCableSectionRfidInfo(OpticCableSectionRfidInfoReqApp opticCableSectionRfidInfoReqApp);

    /**
     * app查询光缆段rfid列表
     *
     * @param uploadOpticCableSectionRfidInfoReqApp 智能标签信息
     * @return Result
     */
    Result uploadOpticCableSectionRfidInfo(UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp);

    /**
     * 根据光缆段id查询光缆段gis信息
     *
     * @param opticCableSectionId 光缆段id
     *
     * @return Result
     */
    Result queryOpticCableSectionRfidInfoByOpticCableSectionId(String opticCableSectionId);

    /**
     * 根据光缆id查询光缆段gis信息
     *
     * @param opticCableId 光缆段id
     *
     * @return Result
     */
    Result queryOpticCableSectionRfidInfoByOpticCableId(String opticCableId);

    /**
     * 根据id光缆段gis坐标微调
     *
     * @param updateOpticCableSectionRfidInfoReqList 光缆段gis列表
     *
     * @return Result
     */
    Result updateOpticCableSectionRfidInfoPositionById(List<UpdateOpticCableSectionRfidInfoReq> updateOpticCableSectionRfidInfoReqList);

    /**
     * 删除光缆段rfid信息
     *
     * @param deleteOpticCableSectionRfidInfoReq 删除光缆段rfid请求
     * @return Result
     */
    Result deleteOpticCableSectionRfidInfoById(DeleteOpticCableSectionRfidInfoReq deleteOpticCableSectionRfidInfoReq);
}
