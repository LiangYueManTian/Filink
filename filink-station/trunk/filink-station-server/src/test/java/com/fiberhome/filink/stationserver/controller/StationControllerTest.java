package com.fiberhome.filink.stationserver.controller;

import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.stationserver.service.StationService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * station controller测试类
 */
@RunWith(JMockit.class)
public class StationControllerTest {

    @Tested
    private StationController stationController;

    @Injectable
    private StationService stationService;

    @Test
    public void addProtocol() {
        //软硬件为空
        ProtocolDto nullProtocolDto = new ProtocolDto();
        stationController.addProtocol(nullProtocolDto);

        //软硬件版本不为空
        ProtocolDto notNullProtocolDto = new ProtocolDto();
        notNullProtocolDto.setFileHexData("fileHexData");
        notNullProtocolDto.setHardwareVersion("NRF52840_elock");
        notNullProtocolDto.setSoftwareVersion("RP9003.004Z.bin");
        new Expectations(){
            {
                stationService.addProtocol(notNullProtocolDto);
                result = true;
            }
        };
        stationController.addProtocol(notNullProtocolDto);
    }

    @Test
    public void updateProtocol() {
        //软硬件为空
        ProtocolDto nullProtocolDto = new ProtocolDto();
        stationController.updateProtocol(nullProtocolDto);

        //软硬件版本不为空
        ProtocolDto notNullProtocolDto = new ProtocolDto();
        notNullProtocolDto.setFileHexData("fileHexData");
        notNullProtocolDto.setHardwareVersion("NRF52840_elock");
        notNullProtocolDto.setSoftwareVersion("RP9003.004Z.bin");
        new Expectations(){
            {
                stationService.updateProtocol(notNullProtocolDto);
                result = true;
            }
        };
        stationController.updateProtocol(notNullProtocolDto);
    }

    @Test
    public void deleteProtocol() {
        //软硬件为空
        ProtocolDto nullProtocolDto = new ProtocolDto();
        List<ProtocolDto> nullProtocolDtoList = new ArrayList<>();
        nullProtocolDtoList.add(nullProtocolDto);
        stationController.deleteProtocol(nullProtocolDtoList);

        //list为空
        stationController.deleteProtocol(null);

        //软硬件版本不为空
        ProtocolDto notNullProtocolDto = new ProtocolDto();
        notNullProtocolDto.setFileHexData("fileHexData");
        notNullProtocolDto.setHardwareVersion("NRF52840_elock");
        notNullProtocolDto.setSoftwareVersion("RP9003.004Z.bin");
        List<ProtocolDto> notNullProtocolDtoList = new ArrayList<>();
        notNullProtocolDtoList.add(notNullProtocolDto);
        new Expectations(){
            {
                stationService.deleteProtocol(notNullProtocolDtoList);
                result = true;
            }
        };
        stationController.deleteProtocol(notNullProtocolDtoList);
    }
}