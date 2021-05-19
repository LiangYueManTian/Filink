package com.fiberhome.filink.fdevice.export;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.dto.DeviceTypeDto;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 设施数量统计导出
 * @Author: zhaoliang
 * @Date: 2019/6/20 16:13
 * @Description: com.fiberhome.filink.fdevice.export
 * @version: 1.0
 */
@Component
public class DeviceCountExport extends AbstractExport {
    /**
     * 获取导出数据
     *
     * @param condition 条件
     * @return List
     */
    @Override
    protected List queryData(QueryCondition condition) {
        List<Object> objectList = ExportApiUtils.getObjList(condition);
        List<DeviceTypeDto> deviceTypeDtoList = new ArrayList<>();
        for (Object object : objectList) {
            JSONObject json = JSONObject.fromObject(object);
            DeviceTypeDto req = (DeviceTypeDto) JSONObject.toBean(json, DeviceTypeDto.class);
            deviceTypeDtoList.add(req);
        }
        return deviceTypeDtoList;
    }

    /**
     * 获取导出数据行数
     *
     * @param condition 条件
     * @return List
     */
    @Override
    protected Integer queryCount(QueryCondition condition) {
        List<Object> objectList = (List<Object>) ExportApiUtils.getObjectList();
        return objectList.size();
    }

}
