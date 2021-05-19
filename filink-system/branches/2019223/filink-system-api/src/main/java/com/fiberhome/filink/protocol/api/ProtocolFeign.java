package com.fiberhome.filink.protocol.api;


import com.fiberhome.filink.protocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.protocol.fallback.ProtocolFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 设施协议远程调用
 * @author chaofang@wistronits.com
 * @since 2019-02-14
 */
@FeignClient(name = "filink-system-server", fallback = ProtocolFeignFallback.class)
public interface ProtocolFeign {
    /**
     * 查询缓存设施协议文件信息
     * @param protocolVersionBean 设施协议文件信息
     * @return 设施协议文件信息
     */
    @PostMapping("/deviceProtocol/queryProtocolXmlBean")
    FiLinkProtocolBean queryProtocolXmlBean(@RequestBody ProtocolVersionBean protocolVersionBean);
}
