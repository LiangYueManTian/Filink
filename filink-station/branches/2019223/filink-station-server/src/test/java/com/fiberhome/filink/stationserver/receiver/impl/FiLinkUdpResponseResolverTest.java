package com.fiberhome.filink.stationserver.receiver.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResInputParams;
import com.fiberhome.filink.stationserver.util.FiLinkProtocolResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

/**
 * FiLinkUdpResponseResolver测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class FiLinkUdpResponseResolverTest {

    @InjectMocks
    private FiLinkUdpResponseResolver fiLinkUdpResponseResolver;

    @Test
    public void resolveRes() throws Exception{
        //获取配置文件流
        ClassPathResource classPathResource = new ClassPathResource("/config/protocolConfig.xml");
        FiLinkProtocolBean resolve = new FiLinkProtocolResolver().resolve(classPathResource.getInputStream());
        FiLinkResInputParams resInputParams = new FiLinkResInputParams();
        resInputParams.setDataSource("FFEF003A42474D500000320400C900CC000100000000000000000000000000000000001A0000000007E20712101B2C010000000000000000000000000000");
        resInputParams.setProtocolBean(resolve);
        fiLinkUdpResponseResolver.resolveRes(resInputParams);

        //心跳帧
        resInputParams.setDataSource("FFEE00000000");
        fiLinkUdpResponseResolver.resolveRes(resInputParams);

        //参数上报
        resInputParams.setDataSource("FFEF00A842474D500000220800C900CC000100000000000000000000000000000000008800000000000000060000390107E207151026160200020060000000000000390207E207151026170200020016000000000000390607E207151026170200020026000000000000390907E207151026170200020022000000000000200507E207151026170000020021000000000000200607E207151026170000020000000000000000000000000000");
        fiLinkUdpResponseResolver.resolveRes(resInputParams);
    }
}