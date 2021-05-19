package com.fiberhome.filink.fdevice.service.statistics.impl;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;
import com.fiberhome.filink.fdevice.dao.statistics.PortStatisticsDao;
import com.fiberhome.filink.fdevice.service.statistics.PortStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 设施端口使用率信息表 Service
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-14
 */
@Service
public class PortStatisticsServiceImpl implements PortStatisticsService {

    @Autowired
    private PortStatisticsDao portStatisticsDao;

    /**
     * 新增
     *
     * @param listDevicePortUtilizationRate 设施端口使用率信息
     * @return Result
     */
    @Override
    public Integer addPortStatistics(List<DevicePortUtilizationRate> listDevicePortUtilizationRate) {
        portStatisticsDao.deletePortStatistics(listDevicePortUtilizationRate);
        for (DevicePortUtilizationRate devicePortUtilizationRate : listDevicePortUtilizationRate) {
            devicePortUtilizationRate.setId(NineteenUUIDUtils.uuid());
        }
        return portStatisticsDao.addPortStatistics(listDevicePortUtilizationRate);
    }

    /**
     * 根据设备id查询端口使用率
     *
     * @param deviceId 设施id
     * @return Result
     */
    @Override
    public Result queryPortStatistics(String deviceId) {
        return ResultUtils.success(portStatisticsDao.queryPortStatisticsByDeviceId(deviceId));
    }

    /**
     * 查询TopN
     *
     * @param topNoPortStatisticsReq 查询条件
     * @return Result
     */
    @Override
    public Result queryPortTopNo(TopNoPortStatisticsReq topNoPortStatisticsReq) {
        return ResultUtils.success(portStatisticsDao.queryPortStatistics(topNoPortStatisticsReq));
    }
}
