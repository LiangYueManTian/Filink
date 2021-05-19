package com.fiberhome.filink.rfid.dao.rfid;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;
import com.fiberhome.filink.rfid.req.rfid.OpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.UpdateOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.app.OpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.resp.rfid.OpticCableSectionRfidInfoResp;
import com.fiberhome.filink.rfid.resp.rfid.app.OpticCableSectionRfidInfoRespApp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * <p>
 * 光缆段rfid信息表 Mapper 接口
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-30
 */
public interface OpticCableSectionRfidInfoDao extends BaseMapper<OpticCableSectionRfidInfo> {

    /**
     * 查询光缆段rfid列表
     *
     * @param queryCondition 查询条件
     * @return List<OpticCableSectionRfIdInfo>
     */
    List<OpticCableSectionRfidInfoResp> opticCableSectionById(QueryCondition<OpticCableSectionRfidInfoReq> queryCondition);

    /**
     * 查询光缆段rfid列表
     *
     * @param queryCondition 查询条件
     * @return List<OpticCableSectionRfIdInfo>
     */
    List<OpticCableSectionRfidInfoRespApp> queryOpticCableSectionRfidInfo(OpticCableSectionRfidInfoReqApp queryCondition);

    /**
     * 新增光缆段rfid列表
     *
     * @param uploadOpticCableSectionRfidInfoReqApp 查询条件
     * @return int
     */
    int addOpticCableSectionRfidInfo(UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp);

    /**
     * 修改光缆段rfid列表
     *
     * @param uploadOpticCableSectionRfidInfoReqApp 查询条件
     * @return int
     */
    int updateOpticCableSectionRfidInfo(UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp);

    /**
     * 删除光缆段rfId列表
     *
     * @param uploadOpticCableSectionRfidInfoReqApp 上传条件
     * @return int
     */
    int deleteOpticCableSectionRfidInfo(UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp);

    /**
     * 根据光缆段id查询光缆段gis列表
     *
     * @param opticCableSectionIds 光缆段ids
     *
     * @return List<OpticCableSectionRfidInfoResp>
     */
    List<OpticCableSectionRfidInfoResp> queryOpticCableSectionRfidInfoByOpticCableSectionId(@Param("opticCableSectionIds") Set<String> opticCableSectionIds);

    /**
     * 根据id光缆段gis坐标微调
     *
     * @param updateOpticCableSectionRfidInfoReq 光缆段gis列表
     *
     * @return int
     */
    int updateOpticCableSectionRfidInfoPositionById(UpdateOpticCableSectionRfidInfoReq updateOpticCableSectionRfidInfoReq);

    /**
     * 校验光缆段rfidCode是否存在
     *
     * @param rfidCode 光缆段gis
     *
     * @return int
     */
    List<String> checkRfidCodeListIsExist(@Param("rfidCode") String rfidCode);

    /**
     *  删除gis信息
     *
     * @param opticCableSectionRfidInfoList 光缆段gis列表
     *
     * @return int
     */
    int deleteOpticCableSectionRfidById(@Param("opticCableSectionRfidInfoList") List<OpticCableSectionRfidInfo> opticCableSectionRfidInfoList);
}
