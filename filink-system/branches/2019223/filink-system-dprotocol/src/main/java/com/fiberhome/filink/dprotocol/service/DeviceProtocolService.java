package com.fiberhome.filink.dprotocol.service;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.dprotocol.bean.xmlBean.FiLinkProtocolBean;
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
     * 新增设施协议
     *
     * @param protocolName 设施协议名称
     * @param file 设施协议文件
     * @return 结果
     */
    Result addDeviceProtocol(String protocolName, MultipartFile file);

    /**
     * 查询上传设施协议文件限制
     * @return 结果
     */
    Result queryFileLimit();

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
     *
     * @return 查询结果
     */
    Result queryDeviceProtocolList();

    /**
     * 查询缓存设施协议文件信息
     * @param protocolVersionBean 设施协议文件信息缓存key
     * @return 设施协议文件信息
     */
    FiLinkProtocolBean queryProtocolXmlBean(ProtocolVersionBean protocolVersionBean);
}
