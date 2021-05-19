package com.fiberhome.filink.rfid.service.deletedevice;


import java.util.List;
import java.util.Map;

/**
 * <p>
 * 删除设施 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-17
 */
public interface DeleteDeviceService {

    /**
     * 校验设施能否删除
     *
     * @param deviceIds 设施id列表
     * @return Result
     */
    Boolean checkDevice(List<String> deviceIds);

    /**
     * 校验设施能否删除
     *
     * @param deviceIds 设施id列表
     * @return
     */
    Map<String, Boolean> checkDevices(List<String> deviceIds);

}
