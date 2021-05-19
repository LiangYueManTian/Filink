package com.fiberhome.filink.dprotocol.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *     设施协议Mapper 接口
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-12
 */
public interface DeviceProtocolDao extends BaseMapper<DeviceProtocol> {
    /**
     * 新增设施协议
     *
     * @param deviceProtocol 设施协议信息
     * @return Integer
     */
    Integer addDeviceProtocol(DeviceProtocol deviceProtocol);

    /**
     * 批量删除设施协议
     * @param deviceProtocols 设施协议信息List
     * @param updateUser 更新人
     * @return Integer
     */
    Integer batchDeleteDeviceProtocolList(@Param("list") List<DeviceProtocol> deviceProtocols, @Param("updateUser") String updateUser);

    /**
     * 查询设施协议列表
     *
     * @return 查询结果
     */
    List<DeviceProtocol> queryDeviceProtocolList();

    /**
     * 根据设施协议ID查询设施协议详情
     *
     * @param protocolId 设施协议ID
     * @return 设施详情
     */
    DeviceProtocol getDeviceProtocolById(String protocolId);

    /**
     * 根据ID List查询设施协议列表
     * @param protocolIds 设施协议IDList
     * @return 查询结果
     */
    List<DeviceProtocol> getDeviceProtocolListById(List<String> protocolIds);

    /**
     * 根据名称查询设施协议
     * @param deviceProtocol 设施协议
     * @return 查询结果ID
     */
    String queryDeviceProtocolByName(DeviceProtocol deviceProtocol);

    /**
     * 根据版本查询设施协议
     *
     * @param deviceProtocol 设施协议
     * @return 查询结果Id
     */
    String queryDeviceProtocolByVersion(DeviceProtocol deviceProtocol);
    /**
     * 根据版本查询设施协议文件路径
     *
     * @param deviceProtocol 设施协议
     * @return 查询文件路径
     */
    String queryUrlByVersion(DeviceProtocol deviceProtocol);
}
