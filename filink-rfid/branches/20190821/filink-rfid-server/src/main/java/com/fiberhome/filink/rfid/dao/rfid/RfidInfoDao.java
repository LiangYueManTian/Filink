package com.fiberhome.filink.rfid.dao.rfid;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.rfid.bean.rfid.RfidInfo;
import com.fiberhome.filink.rfid.req.rfid.DeleteRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.QueryRfidInfoReq;
import com.fiberhome.filink.rfid.resp.rfid.RfidInfoResp;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * rfId信息表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-11
 */
public interface RfidInfoDao extends BaseMapper<RfidInfo> {

    /**
     * 查询rfid信息
     *
     * @param queryRfidInfoReq 查询条件
     * @return List<RfidInfoResp>
     */
    List<RfidInfoResp> queryRfidInfo(QueryRfidInfoReq queryRfidInfoReq);

    /**
     * 新增rfid信息
     *
     * @param insertRfidInfoReqList 新增rfid请求列表
     * @return int
     */
    int addRfidInfo(@Param("insertRfidInfoReqList") List<InsertRfidInfoReq> insertRfidInfoReqList);

    /**
     * 删除rfid信息
     *
     * @param rfidCode   智能标签code
     * @param updateUser 修改人
     * @param updateTime 修改时间
     * @return int
     */
    int deleteRfidInfo(@Param("rfidCode") String rfidCode, @Param("updateUser") String updateUser, @Param("updateTime") Date updateTime);

    /**
     * 根据rfidCode获取rfidCode（用于校验rfidCode是否已存在）
     *
     * @param rfidCode 智能标签code
     * @return List<String>
     */
    List<String> queryRfidInfoByRfidCode(@Param("rfidCode") String rfidCode);

    /**
     * 根据设施id删除智能标签信息
     *
     * @param deleteRfidInfoReq 删除智能标签请求
     * @return int
     */
    int deleteRfidInfoByDeviceId(DeleteRfidInfoReq deleteRfidInfoReq);

    /**
     * 替换标签
     *
     * @param newLabel 新标签
     * @param oldLabel 旧标签
     */
    void changeLabel(@Param("newLabel") String newLabel, @Param("oldLabel") String oldLabel);

    /**
     * 根据rfidCode 获取rfid 信息
     *
     * @param rfidCode rfidCode
     * @return
     */
    List<RfidInfoResp> queryRfidInfoByRfidCodes(List<String> rfidCode);
}
