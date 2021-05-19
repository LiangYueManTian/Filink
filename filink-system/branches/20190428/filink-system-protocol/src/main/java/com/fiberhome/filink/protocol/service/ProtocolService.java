package com.fiberhome.filink.protocol.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.protocol.dto.ProtocolField;
import com.fiberhome.filink.protocol.dto.ProtocolParams;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 通信协议 服务类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-02-20
 */
public interface ProtocolService {

    /**
     * 更新协议内容(Json数据)
     *
     * @param protocolParams 协议参数实体
     * @param file           协议证书
     * @return 更新结果
     */
    Result updateProtocol(ProtocolParams protocolParams, MultipartFile file);


    /**
     * 查询协议内容
     *
     * @param type 协议类型
     * @return 协议内容
     */
    ProtocolField queryProtocol(String type);
}
