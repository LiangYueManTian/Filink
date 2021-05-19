package com.fiberhome.filink.logserver.export.statistics;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.logserver.bean.LogTypeExportBean;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  日志类型统计列表导出类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/21
 */
@Component
public class LogTypeExport extends AbstractExport {
    @Override
    protected List queryData(QueryCondition condition) {
        List<Object> objectList = ExportApiUtils.getObjList(condition);
        List<LogTypeExportBean> logTypeExportBeanList = new ArrayList<>();
        for (Object object : objectList) {
            JSONObject json = JSONObject.fromObject(object);
            LogTypeExportBean req = (LogTypeExportBean) JSONObject.toBean(json, LogTypeExportBean.class);
            logTypeExportBeanList.add(req);
        }
        return logTypeExportBeanList;
    }

    @Override
    protected Integer queryCount(QueryCondition condition) {
        List<Object> objectList = (List<Object>) ExportApiUtils.getObjectList();
        return objectList.size();
    }
}
