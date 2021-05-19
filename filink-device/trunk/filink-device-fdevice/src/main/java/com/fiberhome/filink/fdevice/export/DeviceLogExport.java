package com.fiberhome.filink.fdevice.export;

import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: zl
 * @Date: 2019/4/10 18:18
 * @Description: com.fiberhome.filink.fdevice.export
 * @version: 1.0
 */
@Component
public class DeviceLogExport extends AbstractExport {

    @Autowired
    private DeviceLogService deviceLogService;

    @Autowired
    private UserFeign userFeign;

    @Override
    protected List queryData(QueryCondition queryCondition) {
        //获取用户
        Object userObject = userFeign.queryUserInfoById(ExportApiUtils.getCurrentUserId()).getData();
        User user = DeviceInfoService.convertObjectToUser(userObject);
        PageBean pageBean = deviceLogService.deviceLogListByPage(queryCondition, user, false);
        List<DeviceLog> deviceLogList = (List<DeviceLog>) pageBean.getData();
        return deviceLogList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        return deviceLogService.deviceLogCount(queryCondition, null);
    }
}
