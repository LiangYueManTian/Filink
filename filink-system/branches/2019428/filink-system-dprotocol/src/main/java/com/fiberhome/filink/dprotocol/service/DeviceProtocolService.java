package com.fiberhome.filink.dprotocol.service;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author chaofang103@wistronits.com
 * @since 2019-01-12
 */
public interface DeviceProtocolService extends IService<DeviceProtocol> {

    /**
     * 查询上传设施协议文件限制
     * @return 结果
     */
    Result queryFileLimit();
    /**
     * 新增设施协议
     *
     * @param protocolName 设施协议名称
     * @param file 设施协议文件
     * @return 结果
     */
    Result addDeviceProtocol(String protocolName, MultipartFile file);


    /**
     * 修改设施协议
     *
     * @param deviceProtocol 设施协议信息
     * @param file 设施协议文件
     * @return 结果
     */
    Result updateDeviceProtocol(DeviceProtocol deviceProtocol, MultipartFile file);

    /**
     * 修改设施协议名称
     *
     * @param deviceProtocol 设施协议信息
     * @return 结果
     */
    Result updateDeviceProtocol(DeviceProtocol deviceProtocol);

    /**
     * 删除设施协议
     *
     * @param protocolIds 设施协议ID List
     * @return 结果
     */
    Result deleteDeviceProtocol(List<String> protocolIds);

    /**
     * 查询设施协议列表
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    Result queryDeviceProtocolList(QueryCondition<DeviceProtocol> queryCondition);

    /**
     * 根据软硬件版本获取文件
     * @param deviceProtocol 设施协议
     * @return 文件16进制字符串
     */
    String queryProtocol(DeviceProtocol deviceProtocol);
}
