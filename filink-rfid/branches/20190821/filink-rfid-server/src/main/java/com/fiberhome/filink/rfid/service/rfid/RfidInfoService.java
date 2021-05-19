package com.fiberhome.filink.rfid.service.rfid;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.bean.rfid.RfidInfo;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.QueryRfidInfoReq;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * rfId信息表 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-11
 */
public interface RfidInfoService extends IService<RfidInfo> {

    /**
     * 查询rfid信息
     *
     * @param queryRfidInfoReq 查询条件
     * @return Result
     */
    Result queryRfidInfo(QueryRfidInfoReq queryRfidInfoReq);

    /**
     * 保存rfId信息
     *
     * @param insertRfidInfoReqList rfid信息列表
     * @return Result
     */
    Result addRfidInfo(List<InsertRfidInfoReq> insertRfidInfoReqList);

    /**
     * 删除rfid信息
     *
     * @param rfidCodeList rfidCode列表
     *
     * @return Result
     */
    Result deleteRfidInfo(Set<String> rfidCodeList);

    /**
     * 校验rfIdCode是否存在
     *
     * @param rfidCodeList rfidCode列表
     * @return Boolean
     */
    Boolean checkRfidCodeListIsExist(Set<String> rfidCodeList);

    /**
     * 替换标签
     * @param newLabel 新标签
     * @param oldLabel 旧标签
     */
    void changeLabel(String newLabel, String oldLabel);

    /**
     * 通过设施id删除rfid信息
     *
     * @param deviceId 设施id
     * @return int
     */
    int deleteRfidInfoByDeviceId(String deviceId);

}
