package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.alarmcurrentserver.bean.DeviceNameExport;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 告警top导出
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Component
public class AlarmTopExport extends AbstractExport {

    @Override
    protected List queryData(QueryCondition queryCondition) {
        List<Object> objectList = ExportApiUtils.getObjList(queryCondition);
        List<DeviceNameExport> deviceNameExportList = new ArrayList<>();
        for (Object object : objectList) {
            JSONObject json = JSONObject.fromObject(object);
            DeviceNameExport deviceTop = new DeviceNameExport();
            deviceTop.jsonToBean(json);
            deviceNameExportList.add(deviceTop);
        }
        return deviceNameExportList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        List<Object> objectList = (List<Object>) ExportApiUtils.getObjectList();
        return objectList.size();
    }
}
