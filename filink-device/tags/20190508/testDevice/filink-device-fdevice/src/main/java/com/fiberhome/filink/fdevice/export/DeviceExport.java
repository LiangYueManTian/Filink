package com.fiberhome.filink.fdevice.export;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设施列表导出类
 *
 * @Author: zl
 * @Date: 2019/4/9 21:36
 * @Description: com.fiberhome.filink.fdevice.export
 * @version: 1.0
 */
@Component
@Slf4j
public class DeviceExport extends AbstractExport {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private UserFeign userFeign;

    /**
     * 查询数据
     *
     * @param queryCondition
     * @return
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        //获取用户
        Object userObject = userFeign.queryUserInfoById(ExportApiUtils.getCurrentUserId()).getData();
        User user = DeviceInfoService.convertObjectToUser(userObject);
        Result result = deviceInfoService.listDevice(queryCondition, user, false);
        List<DeviceInfoDto> dtoList = (List<DeviceInfoDto>) result.getData();
        return dtoList;
    }

    /**
     * 查询数量
     *
     * @param queryCondition
     * @return
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        return deviceInfoService.queryDeviceCount(queryCondition, null);
    }

}
