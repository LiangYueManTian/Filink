package com.fiberhome.filink.rfid.export.statistics;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortStatisticsReq;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 端口状态导出
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */
@Component
public class PortStatisticsExport extends AbstractExport {
    /**
     * 获取导出数据
     *
     * @param condition 条件
     * @return List
     */
    @Override
    protected List queryData(QueryCondition condition) {
        List<Object> objectList = ExportApiUtils.getObjList(condition);
        List<ExportPortStatisticsReq> reqList = new ArrayList<>();
        for (Object object : objectList) {
            ExportPortStatisticsReq req = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)), ExportPortStatisticsReq.class);
            reqList.add(req);
        }
        return reqList;
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
