package com.fiberhome.filink.stationserver.sender.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.util.FiLinkProtocolResolver;
import com.fiberhome.filink.stationserver.util.lockenum.CmdId;
import com.fiberhome.filink.stationserver.util.lockenum.CmdType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FiLinkUdpRequestResolver测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class FiLinkUdpRequestResolverTest {

    @InjectMocks
    private FiLinkUdpRequestResolver fiLinkUdpRequestResolver;

    /**
     * 解析请求帧测试方法
     */
    @Test
    public void resolveUdpReq() throws Exception{
        //异常场景
        try {
            //构造请求帧参数
            FiLinkReqParams fiLinkReqParams = new FiLinkReqParams();
            fiLinkReqParams.setCmdId(CmdId.SET_CONFIG);
            fiLinkReqParams.setCmdType(CmdType.REQUEST_TYPE);
            fiLinkReqParams.setDeviceId("13172750");
            fiLinkUdpRequestResolver.resolveUdpReq(fiLinkReqParams);
        }catch (Exception e){
            e.printStackTrace();
        }

        //获取协议对象
        ClassPathResource classPathResource = new ClassPathResource("/config/protocolConfig.xml");
        FiLinkProtocolBean resolve = new FiLinkProtocolResolver().resolve(classPathResource.getInputStream());
        //构造请求帧参数
        FiLinkReqParams fiLinkReqParams = new FiLinkReqParams();
        fiLinkReqParams.setProtocolBean(resolve);
        fiLinkReqParams.setCmdId(CmdId.SET_CONFIG);
        fiLinkReqParams.setCmdType(CmdType.REQUEST_TYPE);
        fiLinkReqParams.setDeviceId("13172750");
        //构造配置策略参数
        List<Map<String,Object>> paramList = new ArrayList<>();
        //"electricity","30"
        Map<String,Object> configParamMapElect = new HashMap<>();
        configParamMapElect.put("dataClass","electricity");
        configParamMapElect.put("data","30");
        paramList.add(configParamMapElect);
        //"temperature","43"
        Map<String,Object> configParamMapTemp = new HashMap<>();
        configParamMapTemp.put("dataClass","temperature");
        configParamMapTemp.put("data","43");
        paramList.add(configParamMapTemp);
        Map<String,Object> param = new HashMap<>();
        param.put("params",paramList);
        fiLinkReqParams.setParams(param);
        fiLinkUdpRequestResolver.resolveUdpReq(fiLinkReqParams);
    }

}