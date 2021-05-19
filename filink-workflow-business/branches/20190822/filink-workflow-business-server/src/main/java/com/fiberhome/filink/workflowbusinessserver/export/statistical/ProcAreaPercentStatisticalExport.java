package com.fiberhome.filink.workflowbusinessserver.export.statistical;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.export.ProcAreaPercentStatisticalExportBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单区域比导出
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 16:37
 */
@Component
public class ProcAreaPercentStatisticalExport extends AbstractExport {

    /**
     * 查询数据信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:37
     * @param queryCondition 查询对象
     * @return 查询数据信息
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        List<Object> objectList = ExportApiUtils.getObjList(queryCondition);
        List<ProcAreaPercentStatisticalExportBean> exportList = new ArrayList<>();
        for (Object object : objectList) {
            ProcAreaPercentStatisticalExportBean exportBean = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)), ProcAreaPercentStatisticalExportBean.class);
            exportList.add(exportBean);
        }
        return exportList;
    }

    /**
     * 查询对象的个数
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:38
     * @param queryCondition 查询对象
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        List<Object> objectList = (List<Object>) ExportApiUtils.getObjectList();
        return objectList.size();
    }
}
