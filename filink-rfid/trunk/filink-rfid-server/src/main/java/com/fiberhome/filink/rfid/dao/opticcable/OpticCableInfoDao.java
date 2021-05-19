package com.fiberhome.filink.rfid.dao.opticcable;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.rfid.req.opticcable.InsertOpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.UpdateOpticCableInfoReq;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoDetail;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoResp;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 光缆信息表 Mapper 接口
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public interface OpticCableInfoDao extends BaseMapper<OpticCableInfo> {

    /**
     * 分页查询光缆列表
     *
     * @param queryCondition 查询封装类
     * @return List<OpticCableInfoResp>
     */
    List<OpticCableInfoResp> opticCableListByPage(QueryCondition<OpticCableInfoReq> queryCondition);

    /**
     * 光缆总数
     * @param queryCondition 查询封装类
     * @return Integer光缆总数
     */
    Integer opticCableListTotal(QueryCondition<OpticCableInfoReq> queryCondition);

    /**
     * 新增光缆
     *
     * @param insertOpticCableInfoReq 新增光缆请求
     * @return int
     */
    int addOpticCable(InsertOpticCableInfoReq insertOpticCableInfoReq);

    /**
     * 根据id获取光缆信息
     *
     * @param opticCableId 光缆id
     * @return Result
     */
    OpticCableInfoDetail queryOpticCableById(@Param("opticCableId") String opticCableId);

    /**
     * 修改光缆
     *
     * @param updateOpticCableInfoReq 修改光缆请求
     * @return int
     */
    int updateOpticCableById(UpdateOpticCableInfoReq updateOpticCableInfoReq);

    /**
     * 修改光缆isDeleted字段
     *
     * @param opticCableId 光缆id
     * @param isDeleted    逻辑删除字段
     * @param updateUser   删除用户
     * @param updateTime   删除时间
     *
     * @return int
     */
    int updateOpticCableIsDeletedById(@Param("opticCableId") String opticCableId, @Param("isDeleted") String isDeleted, @Param("updateUser") String updateUser,@Param("updateTime") Date updateTime);

    /**
     * 校验光缆名字
     *
     * @param opticCableName 光缆名字
     * @return OpticCableInfoDetail 光缆信息
     */
    OpticCableInfoDetail queryOpticCableByName(@Param("opticCableName") String opticCableName);

    /**
     * 查询光缆纤芯数
     * @param opticCableId 光缆id
     * @return Integer
     */
    Integer queryOpticCableByCoreNum(String opticCableId);
    /**
     * app请求所有光缆的信息
     *
     * @return List<OpticCableInfoResp>
     */
    List<OpticCableInfoResp> getOpticCableListForApp();

}
