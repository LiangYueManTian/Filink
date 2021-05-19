package com.fiberhome.filink.rfid.controller.deletedevice;

import com.fiberhome.filink.rfid.service.deletedevice.DeleteDeviceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by chaofanrong on 2019/7/16.
 * DeleteDeviceControllerTest test
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteDeviceControllerTest {

    @InjectMocks
    private DeleteDeviceController deleteDeviceController;

    /**
     * deleteDeviceService
     */
    @Mock
    private DeleteDeviceService deleteDeviceService;

    @Test
    public void checkDevice() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("fdsafdasf");
        when(deleteDeviceService.checkDevice(deviceIds)).thenReturn(true);
        Assert.assertEquals(true, deleteDeviceController.checkDevice(deviceIds));
    }


}