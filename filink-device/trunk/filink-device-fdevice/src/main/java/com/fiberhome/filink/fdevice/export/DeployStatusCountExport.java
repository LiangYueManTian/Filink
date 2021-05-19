package com.fiberhome.filink.fdevice.export;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.dto.DeployStatusDto;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/20 20:19
 * @Description: com.fiberhome.filink.fdevice.export
 * @version: 1.0
 */
@Component
public class DeployStatusCountExport extends AbstractExport {
    /**
     * 获取导出数据
     *
     * @param condition 条件
     * @return List
     */
    @Override
    protected List queryData(QueryCondition condition) {
        List<Object> objectList = ExportApiUtils.getObjList(condition);
        List<DeployStatusDto> deployStatusDtoList = new ArrayList<>();
        for (Object object : objectList) {
            JSONObject json = JSONObject.fromObject(object);
            DeployStatusDto req = (DeployStatusDto) JSONObject.toBean(json, DeployStatusDto.class);
            deployStatusDtoList.add(req);
        }
        return deployStatusDtoList;
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
