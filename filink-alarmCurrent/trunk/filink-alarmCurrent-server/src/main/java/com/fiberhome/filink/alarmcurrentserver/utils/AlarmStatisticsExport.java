package com.fiberhome.filink.alarmcurrentserver.utils;


import com.fiberhome.filink.alarmcurrentserver.bean.AlarmAreaRateStatisticsReq;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmHandleStatisticsReq;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmLevelStatisticsReq;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmNameStatisticsReq;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 导出方法
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Component
public class AlarmStatisticsExport extends AbstractExport {


    /**
     * 查询导出信息
     *
     * @param queryCondition 条件
     * @return 导出数据
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        List<Object> objectList = (List<Object>) ExportApiUtils.getObjectList();
        String pageType = queryCondition.getBizCondition().toString();
        List exportList = getPageTypeChange(objectList, pageType);
        return exportList;
    }

    protected List getPageTypeChange(List<Object> objectList, String pageType) {
        List exportList = new ArrayList();
        if (AppConstant.ALARM_TYPE_STATISTICS.equals(pageType)) {
            for (Object object : objectList) {
                JSONObject json = JSONObject.fromObject(object);
                AlarmLevelStatisticsReq req = (AlarmLevelStatisticsReq) JSONObject.toBean(json, AlarmLevelStatisticsReq.class);
                exportList.add(req);
            }
        } else if (AppConstant.ALARM_HANDLE_STATISTICS.equals(pageType)) {
            for (Object object : objectList) {
                JSONObject json = JSONObject.fromObject(object);
                AlarmHandleStatisticsReq req = (AlarmHandleStatisticsReq) JSONObject.toBean(json, AlarmHandleStatisticsReq.class);
                exportList.add(req);
            }
        } else if (AppConstant.ALARM_NAME_STATISTICS.equals(pageType)) {
            for (Object object : objectList) {
                JSONObject json = JSONObject.fromObject(object);
                AlarmNameStatisticsReq req = (AlarmNameStatisticsReq) JSONObject.toBean(json, AlarmNameStatisticsReq.class);
                exportList.add(req);
            }
        } else if (AppConstant.AREA_RATIO_STATISTICS.equals(pageType)) {
            for (Object object : objectList) {
                JSONObject json = JSONObject.fromObject(object);
                AlarmAreaRateStatisticsReq req = (AlarmAreaRateStatisticsReq) JSONObject.toBean(json, AlarmAreaRateStatisticsReq.class);
                exportList.add(req);
            }
        }
        return exportList;
    }

    /**
     * 查询count
     *
     * @param queryCondition
     * @return count信息
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        List<Object> objectList = (List<Object>) ExportApiUtils.getObjectList();
        return objectList.size();
    }


}
