package com.fiberhome.filink.fdevice.exception;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/7/9
 */
@RunWith(JMockit.class)
public class DeviceExceptionHandlerTest {
    @Tested
    private DeviceExceptionHandler deviceExceptionHandler;
    @Mocked
    private I18nUtils i18nUtils;
    @Test
    public void handleFiLinkDeviceLogException() {
        deviceExceptionHandler.handleFiLinkDeviceLogException(new FiLinkDeviceLogException("test"));
    }

    @Test
    public void handleFiLinkDeviceConfigException() {
        deviceExceptionHandler.handleFiLinkDeviceConfigException(new FiLinkDeviceConfigException("test"));
    }

    @Test
    public void handlerDeviceException() {
        deviceExceptionHandler.handlerDeviceException(new FilinkDeviceNameSameException());
        deviceExceptionHandler.handlerDeviceException(new FiLinkDeviceException("test"));
    }

    @Test
    public void handlerAreaDirtyDataException() {
        deviceExceptionHandler.handlerAreaDirtyDataException(new FiLinkAreaDirtyDataException());
        deviceExceptionHandler.handlerAreaDirtyDataException(new FiLinkAreaDirtyDataException("test"));
    }

    @Test
    public void handlerAreaDateBaseException() {
        deviceExceptionHandler.handlerAreaDateBaseException(new FiLinkAreaDateBaseException());
        deviceExceptionHandler.handlerAreaDateBaseException(new FiLinkAreaDateBaseException("test"));
    }

    @Test
    public void handlerAreaNoDataPermissionsException() {
        deviceExceptionHandler.handlerAreaNoDataPermissionsException(new FilinkAreaNoDataPermissionsException());
    }


    @Test
    public void handlerAreaException() {
        deviceExceptionHandler.handlerAreaException(new FilinkAreaException("test"));
    }

    @Test
    public void handlerAreaDateFormatException() {
        deviceExceptionHandler.handlerAreaDateFormatException(new FiLinkAreaDateFormatException("test"));
    }

    @Test
    public void handlerAreaDoesNotExistException() {
        deviceExceptionHandler.handlerAreaDoesNotExistException(new FilinkAreaDoesNotExistException("test"));
    }

    @Test
    public void handlerDeviceMapParamException() {
        deviceExceptionHandler.handlerDeviceMapParamException(new FilinkDeviceMapParamException("test"));
    }

    @Test
    public void handlerDeviceMapMessageException() {
        deviceExceptionHandler.handlerDeviceMapMessageException(new FilinkDeviceMapMessageException("test"));
    }

    @Test
    public void handlerDeviceMapUpdateException() {
        deviceExceptionHandler.handlerDeviceMapUpdateException(new FilinkDeviceMapUpdateException("test"));
    }

    @Test
    public void handlerFilinkSystemErrorException() {
        deviceExceptionHandler.handlerFilinkSystemErrorException(new FilinkAttentionRepeatException());
        deviceExceptionHandler.handlerFilinkSystemErrorException(new FilinkAttentionDataException());
    }

    @Test
    public void  handlerFilinkRequestParamException() {
        deviceExceptionHandler.handlerFilinkRequestParamException(new FilinkAttentionRequestParamException());
    }
}
