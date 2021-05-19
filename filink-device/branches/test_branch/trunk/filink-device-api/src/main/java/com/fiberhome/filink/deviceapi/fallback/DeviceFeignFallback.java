package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoBase;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.bean.DeviceParam;
import com.fiberhome.filink.deviceapi.bean.UpdateDeviceStatusPda;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/15 16:35
 */
@Slf4j
@Component
public class DeviceFeignFallback implements DeviceFeign {
//	@Override
//	public DeviceInfoDto findDeviceBySeriaNumber(String id) throws Exception {
//		log.warn("》》》》》》》》》》》》feign调用熔断");
//		return null;
//	}

    /**
     * 修改首页首次加载阈值（设施数量）
     *
     * @param homeDeviceLimit 首页首次加载阈值
     */
    @Override
    public void updateHomeDeviceLimit(Integer homeDeviceLimit) {
        log.warn("update home device limit feign fallback>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public DeviceInfoDto getDeviceById(String id) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    @Override
    public List<DeviceInfoDto> getDeviceByIds(String[] ids) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    @Override
    public Result deviceListByPage(QueryCondition<DeviceInfoDto> queryCondition) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    @Override
    public Integer queryCurrentDeviceCount() {
        log.warn("》》》》》》》》》》》》feign queryCurrentDeviceCount调用熔断");
        return null;
    }

    @Override
    public Result addDevice(DeviceInfoDto deviceInfoDto) throws Exception {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    @Override
    public Result updateDeviceStatus(DeviceInfoDto deviceInfoDto) throws Exception {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    @Override
    public Result updateDeviceListStatus(UpdateDeviceStatusPda updateDeviceStatusPda) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    @Override
    public List<DeviceInfoDto> queryDeviceDtoByParam(DeviceParam deviceParam) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    @Override
    public List<DeviceInfoBase> queryDeviceInfoBaseByParam(DeviceParam deviceParam) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

    /**
     * 获取默认配置值
     *
     * @param deviceId
     * @return
     */
    @Override
    public String getDefaultParams(String deviceId) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }
}
