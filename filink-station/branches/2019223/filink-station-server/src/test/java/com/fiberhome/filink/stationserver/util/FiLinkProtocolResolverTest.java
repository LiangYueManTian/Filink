package com.fiberhome.filink.stationserver.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.*;

/**
 * FiLinkProtocolResolver测试类
 *
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class FiLinkProtocolResolverTest {

    @InjectMocks
    private FiLinkProtocolResolver fiLinkProtocolResolver;

    @Test
    public void resolve() throws Exception{
        ClassPathResource classPathResource = new ClassPathResource("/config/protocolConfig.xml");
        fiLinkProtocolResolver.resolve(classPathResource.getInputStream());
    }
}