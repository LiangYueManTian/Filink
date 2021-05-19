package com.fiberhome.filink.rfid.controller.deletedevice;


import com.fiberhome.filink.rfid.service.deletedevice.DeleteDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 删除设施 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-17
 */
@RestController
@RequestMapping("/deleteDevice")
public class DeleteDeviceController {

    @Autowired
    private DeleteDeviceService deleteDeviceService;

    /**
     * 校验设施能否删除
     *
     * @param deviceIds 设施id列表
     * @return Result
     */
    @PostMapping("/checkDevice")
    public Boolean checkDevice(@RequestBody List<String> deviceIds) {
        return deleteDeviceService.checkDevice(deviceIds);
    }


    /**
     * 校验设施能否删除
     *
     * @param deviceIds 设施id列表
     * @return Result
     */
    @PostMapping("/checkDevices")
    public Map<String, Boolean> checkDevices(@RequestBody List<String> deviceIds) {
        return deleteDeviceService.checkDevices(deviceIds);
    }
}
