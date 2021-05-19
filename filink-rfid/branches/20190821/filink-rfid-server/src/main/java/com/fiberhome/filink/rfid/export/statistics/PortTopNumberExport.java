package com.fiberhome.filink.rfid.export.statistics;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortTopNumberReq;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * topN端口使用率统计
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/21
 */
@Component
public class PortTopNumberExport extends AbstractExport {
    /**
     * 获取导出数据
     *
     * @param condition 条件
     * @return List
     */
    @Override
    protected List queryData(QueryCondition condition) {
        List<Object> objectList = ExportApiUtils.getObjList(condition);
        List<ExportPortTopNumberReq> reqList = new ArrayList<>();
        for (Object object : objectList) {
            ExportPortTopNumberReq req = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)),
                    ExportPortTopNumberReq.class);
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
