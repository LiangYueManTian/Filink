package com.fiberhome.filink.fdevice.dto;

import com.fiberhome.filink.fdevice.bean.device.DeviceMapConfig;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *首页地图和设施类型配置信息Dto
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-19
 */
@Data
public class DeviceMapConfigDto {

    private String deviceIconSize;

    private List<DeviceMapConfig> deviceConfig;
}
