package com.fiberhome.filink.parts.exception;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * PartsExceptionHandlerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/9
 */
@RunWith(MockitoJUnitRunner.class)
public class PartsExceptionHandlerTest {

    @InjectMocks
    private PartsExceptionHandler partsExceptionHandler;


    @Test
    public void handlerDeviceException() {
        Assert.assertEquals(-1, partsExceptionHandler.handlerDeviceException(new FilinkPartsException("X")).getCode());
    }
}