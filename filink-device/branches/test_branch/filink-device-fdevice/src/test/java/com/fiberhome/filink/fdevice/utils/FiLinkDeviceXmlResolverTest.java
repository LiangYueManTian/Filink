package com.fiberhome.filink.fdevice.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;

/**
 * FiLinkDeviceXmlResolverTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/8
 */
@RunWith(MockitoJUnitRunner.class)
public class FiLinkDeviceXmlResolverTest {

    @InjectMocks
    private FiLinkDeviceXmlResolver fiLinkDeviceXmlResolver;


    @Test
    public void resolveDeviceXml() {
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        try {
            fiLinkDeviceXmlResolver.resolveDeviceXml(inputStream);
        } catch (Exception e) {

        }

    }

    @Test
    public void resolveConfigPatternXmlToMap() {
    }
}