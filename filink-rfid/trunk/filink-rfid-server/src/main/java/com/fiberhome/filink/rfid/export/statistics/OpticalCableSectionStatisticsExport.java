package com.fiberhome.filink.rfid.export.statistics;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticalCableSectionStatisticsReq;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 光缆段和纤芯导出
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */
@Component
public class OpticalCableSectionStatisticsExport extends AbstractExport {
    @Override
    protected List queryData(QueryCondition condition) {
        List<Object> objectList = ExportApiUtils.getObjList(condition);
        List<ExportOpticalCableSectionStatisticsReq> exportOpticCableInfoStatisticReqList = new ArrayList<>();
        for (Object object : objectList) {

            ExportOpticalCableSectionStatisticsReq req = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)),
                    ExportOpticalCableSectionStatisticsReq.class);
            exportOpticCableInfoStatisticReqList.add(req);
        }
        return exportOpticCableInfoStatisticReqList;
    }

    @Override
    protected Integer queryCount(QueryCondition condition) {
        List<Object> objectList = (List<Object>) ExportApiUtils.getObjectList();
        return objectList.size();
    }
}
