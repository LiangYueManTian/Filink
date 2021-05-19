package com.fiberhome.filink.workflowbusinessserver.utils.common;

import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcInfoDateStatisticalGroup;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcInfoMonthStatisticalGroup;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcInfoWeekStatisticalGroup;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcInfoYearStatisticalGroup;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.QueryProcCountByMonthReq;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.QueryProcCountByTimeReq;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.QueryProcCountByWeekReq;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.QueryProcCountByYearReq;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal.QueryProcCountByMonthStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal.QueryProcCountByTimeStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal.QueryProcCountByWeekStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal.QueryProcCountByYearStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcDeviceTypeOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcErrorReasonOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcProcessingSchemeOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcStatusOverviewStatisticalVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/6/4 14:55
 */

public class CastStatisticalUtil {


    /**
     * 获取首页增量查询条件
     * @author hedongwei@wistronits.com
     * @date  2019/6/27 13:30
     * @param permissionDeviceTypes 设施类型权限信息
     * @param permissionAreaId 区域权限信息
     * @param permissionDeptIds 部门权限信息
     * @param timeList 时间集合
     * @param procType 工单类型
     * @return 增量查询条件
     */
    public static Criteria getHomeSearchCriteria(Set<String> permissionDeviceTypes, Set<String> permissionAreaId, Set<String> permissionDeptIds, List<Long> timeList, String procType) {
        Criteria criteria = null;
        if (ObjectUtils.isEmpty(permissionDeptIds)) {
            criteria = Criteria.where("nowDate").gte(timeList.get(0)).lte(timeList.get(1))
                    .and("procType").is(procType);
        } else {
            List<String> permissionAreaIdList = new ArrayList<>();
            permissionAreaIdList.addAll(permissionAreaId);
            List<String> permissionDeviceTypeList = new ArrayList<>();
            permissionDeviceTypeList.addAll(permissionDeviceTypes);
            //部门筛选条件
            List<String> deptList = new ArrayList<>();
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                deptList.addAll(permissionDeptIds);
            }
            criteria = Criteria.where("areaId").in(permissionAreaIdList).and("deviceType").in(permissionDeviceTypeList)
                    .and("nowDate").gte(timeList.get(0)).lte(timeList.get(1))
                    .and("procType").is(procType).and("accountabilityDept").in(deptList);
        }
        return criteria;
    }

    /**
     * 获取工单状态概览map
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 14:58
     * @param procStatusVoList 工单状态集合
     * @return 获取工单状态概览map
     */
    public static Map<String, ProcStatusOverviewStatisticalVo> getProcStatusOverviewVoMap(List<ProcStatusOverviewStatisticalVo> procStatusVoList) {
        Map<String, ProcStatusOverviewStatisticalVo> overviewStatisticalVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procStatusVoList)) {
            for (ProcStatusOverviewStatisticalVo statisticalVo : procStatusVoList) {
                if (!ObjectUtils.isEmpty(statisticalVo.getStatus())) {
                    overviewStatisticalVoMap.put(statisticalVo.getStatus(), statisticalVo);
                }
            }
        }
        return overviewStatisticalVoMap;
    }



    /**
     * 获取工单处理方案概览map
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 14:58
     * @param procSchemeVoList 工单处理方案集合
     * @return 获取工单处理方案概览map
     */
    public static Map<String, ProcProcessingSchemeOverviewStatisticalVo> getProcProcessingSchemeOverviewVoMap(List<ProcProcessingSchemeOverviewStatisticalVo> procSchemeVoList) {
        Map<String, ProcProcessingSchemeOverviewStatisticalVo> overviewStatisticalVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procSchemeVoList)) {
            for (ProcProcessingSchemeOverviewStatisticalVo statisticalVo : procSchemeVoList) {
                if (!ObjectUtils.isEmpty(statisticalVo.getProcessingScheme())) {
                    overviewStatisticalVoMap.put(statisticalVo.getProcessingScheme(), statisticalVo);
                }
            }
        }
        return overviewStatisticalVoMap;
    }


    /**
     * 获取工单故障原因概览map
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 14:58
     * @param procErrorReasonVoList 工单故障原因集合
     * @return 获取工单故障原因概览map
     */
    public static Map<String, ProcErrorReasonOverviewStatisticalVo> getProcErrorReasonOverviewVoMap(List<ProcErrorReasonOverviewStatisticalVo> procErrorReasonVoList) {
        Map<String, ProcErrorReasonOverviewStatisticalVo> overviewStatisticalVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procErrorReasonVoList)) {
            for (ProcErrorReasonOverviewStatisticalVo statisticalVo : procErrorReasonVoList) {
                if (!ObjectUtils.isEmpty(statisticalVo.getErrorReason())) {
                    overviewStatisticalVoMap.put(statisticalVo.getErrorReason(), statisticalVo);
                }
            }
        }
        return overviewStatisticalVoMap;
    }


    /**
     * 获取工单设施类型概览map
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 14:58
     * @param procDeviceTypeVoList 工单设施类型集合
     * @return 获取工单设施类型概览map
     */
    public static Map<String, ProcDeviceTypeOverviewStatisticalVo> getProcDeviceTypeOverviewVoMap(List<ProcDeviceTypeOverviewStatisticalVo> procDeviceTypeVoList) {
        Map<String, ProcDeviceTypeOverviewStatisticalVo> overviewStatisticalVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procDeviceTypeVoList)) {
            for (ProcDeviceTypeOverviewStatisticalVo statisticalVo : procDeviceTypeVoList) {
                if (!ObjectUtils.isEmpty(statisticalVo.getDeviceType())) {
                    overviewStatisticalVoMap.put(statisticalVo.getDeviceType(), statisticalVo);
                }
            }
        }
        return overviewStatisticalVoMap;
    }

    /**
     * 工单增量统计map
     * @author hedongwei@wistronits.com
     * @date  2019/6/10 11:15
     * @param nowDateList 工单增量数据
     * @return 工单增量统计map
     */
    public static Map<Long, QueryProcCountByTimeStatisticalVo> getProcCountByTimeStatisticalVoMap(List<ProcInfoDateStatisticalGroup> nowDateList) {
        Map<Long, QueryProcCountByTimeStatisticalVo> queryProcCountByTimeVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(nowDateList)) {
            QueryProcCountByTimeStatisticalVo statisticalVo = null;
            for (ProcInfoDateStatisticalGroup groupOne : nowDateList) {
                statisticalVo = new QueryProcCountByTimeStatisticalVo();
                if (!ObjectUtils.isEmpty(groupOne.getNowDate())) {
                    BeanUtils.copyProperties(groupOne, statisticalVo);
                    queryProcCountByTimeVoMap.put(statisticalVo.getNowDate(), statisticalVo);
                }
            }
        }
        return queryProcCountByTimeVoMap;
    }

    /**
     * 工单周增量统计map
     * @author hedongwei@wistronits.com
     * @date  2019/6/10 11:15
     * @param nowDateList 工单周增量数据
     * @return 工单周增量统计map
     */
    public static Map<Long, QueryProcCountByWeekStatisticalVo> getProcCountByWeekStatisticalVoMap(List<ProcInfoWeekStatisticalGroup> nowDateList) {
        Map<Long, QueryProcCountByWeekStatisticalVo> queryProcCountByWeekVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(nowDateList)) {
            QueryProcCountByWeekStatisticalVo statisticalVo = null;
            for (ProcInfoWeekStatisticalGroup groupOne : nowDateList) {
                statisticalVo = new QueryProcCountByWeekStatisticalVo();
                if (!ObjectUtils.isEmpty(groupOne.getNowDate())) {
                    BeanUtils.copyProperties(groupOne, statisticalVo);
                    queryProcCountByWeekVoMap.put(statisticalVo.getNowDate(), statisticalVo);
                }
            }
        }
        return queryProcCountByWeekVoMap;
    }

    /**
     * 工单月度增量统计map
     * @author hedongwei@wistronits.com
     * @date  2019/6/10 11:15
     * @param nowDateList 工单月度增量数据
     * @return 工单月度增量统计map
     */
    public static Map<Long, QueryProcCountByMonthStatisticalVo> getProcCountByMonthStatisticalVoMap(List<ProcInfoMonthStatisticalGroup> nowDateList) {
        Map<Long, QueryProcCountByMonthStatisticalVo> queryProcCountByTimeVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(nowDateList)) {
            QueryProcCountByMonthStatisticalVo statisticalVo = null;
            for (ProcInfoMonthStatisticalGroup groupOne : nowDateList) {
                statisticalVo = new QueryProcCountByMonthStatisticalVo();
                if (!ObjectUtils.isEmpty(groupOne.getNowDate())) {
                    BeanUtils.copyProperties(groupOne, statisticalVo);
                    queryProcCountByTimeVoMap.put(statisticalVo.getNowDate(), statisticalVo);
                }
            }
        }
        return queryProcCountByTimeVoMap;
    }

    /**
     * 工单年度增量统计map
     * @author hedongwei@wistronits.com
     * @date  2019/6/10 11:15
     * @param nowDateList 工单年度增量数据
     * @return 工单年度增量统计map
     */
    public static Map<Long, QueryProcCountByYearStatisticalVo> getProcCountByYearStatisticalVoMap(List<ProcInfoYearStatisticalGroup> nowDateList) {
        Map<Long, QueryProcCountByYearStatisticalVo> queryProcCountByYearVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(nowDateList)) {
            QueryProcCountByYearStatisticalVo statisticalVo = null;
            for (ProcInfoYearStatisticalGroup groupOne : nowDateList) {
                statisticalVo = new QueryProcCountByYearStatisticalVo();
                if (!ObjectUtils.isEmpty(groupOne.getNowDate())) {
                    BeanUtils.copyProperties(groupOne, statisticalVo);
                    queryProcCountByYearVoMap.put(statisticalVo.getNowDate(), statisticalVo);
                }
            }
        }
        return queryProcCountByYearVoMap;
    }

    /**
     * 获取时间0点参数
     * @author hedongwei@wistronits.com
     * @date  2019/6/26 14:15
     * @param time 时间参数
     * @return 获取当前0时区的时间
     */
    public static Long getTimeZeroTime(Long time) {
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(time);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取时间0时区
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 11:39
     * @param addDay 当前时间加上偏移量
     * @return 获取计算后的时间
     */
    public static Long getNowTimeAdd(int addDay) {
        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, addDay);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取时间0时区
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 11:39
     * @param addDay 当前时间加上偏移量
     * @return 获取计算后的时间
     */
    public static Long getNowDayLastTimeAdd(int addDay) {
        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, addDay);
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取当前时间偏移周
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:31
     * @param num 偏移周
     * @return 返回周的时间
     */
    public static Long getAdvanceNumberWeek(int num) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_WEEK, 2);
        date.add(Calendar.DATE, 7 * num);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取当前时间偏移周
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:31
     * @param num 偏移周
     * @return 返回周的时间
     */
    public static Long getAdvanceLastNumberWeek(int num) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_WEEK, 1);
        date.add(Calendar.DATE, 7 * num + 7);
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取当前时间偏移月
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:31
     * @param num 偏移月
     * @return 返回月的时间
     */
    public static Long getAdvanceNumberMonth(int num) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, num);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取当前时间偏移月
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:31
     * @param num 偏移月
     * @return 返回月的时间
     */
    public static Long getAdvanceLastNumberMonth(int num) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, num);
        date.set(Calendar.DATE, date.getActualMaximum(Calendar.DATE));
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取当前时间偏移年
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:31
     * @param num 偏移年
     * @return 返回年的时间
     */
    public static Long getAdvanceNumberYear(int num) {
        Calendar date = new GregorianCalendar();
        date.add(Calendar.YEAR, num);
        date.set(Calendar.MONTH, 0);
        date.set(Calendar.DATE, 1);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 获取当前时间偏移年
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:31
     * @param num 偏移年
     * @return 返回年的时间
     */
    public static Long getAdvanceLastNumberYear(int num) {
        //获取年份
        Calendar yearDate = new GregorianCalendar();
        yearDate.add(Calendar.YEAR, num);
        int year = yearDate.get(Calendar.YEAR);
        //获取最后一年数据
        Calendar lastYearDate = new GregorianCalendar();
        lastYearDate.clear();
        lastYearDate.set(Calendar.YEAR, year);
        lastYearDate.roll(Calendar.DAY_OF_YEAR, -1);
        lastYearDate.set(Calendar.HOUR_OF_DAY, 23);
        lastYearDate.set(Calendar.MINUTE, 59);
        lastYearDate.set(Calendar.SECOND, 59);
        lastYearDate.set(Calendar.MILLISECOND, 999);
        System.out.println(lastYearDate.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastYearDate.getTime()));
        return lastYearDate.getTimeInMillis();
    }

    /**
     * 获取默认统计的时间
     */
    public static List<Long> getHomeDefaultInfo() {
        int startDay = -15;
        Long startTime = CastStatisticalUtil.getNowTimeAdd(startDay);
        int endDay = -1;
        Long endTime = CastStatisticalUtil.getNowTimeAdd(endDay);
        List<Long> timeList = new ArrayList<>();
        timeList.add(startTime);
        timeList.add(endTime);
        return timeList;
    }

    /**
     * 周统计默认时间
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 20:33
     */
    public static List<Long> getHomeWeekDefaultInfo() {
        int startWeek = -15;
        Long weekTime = CastStatisticalUtil.getAdvanceNumberWeek(startWeek);
        int endWeek = -1;
        Long endTime = CastStatisticalUtil.getAdvanceNumberWeek(endWeek);
        List<Long> timeList = new ArrayList<>();
        timeList.add(weekTime);
        timeList.add(endTime);
        return timeList;
    }

    /**
     * 月统计默认统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 20:34
     */
    public static List<Long> getHomeMonthDefaultInfo() {
        int startMonth = -12;
        Long monthTime = CastStatisticalUtil.getAdvanceNumberMonth(startMonth);
        int endMonth = -1;
        Long endTime = CastStatisticalUtil.getAdvanceNumberMonth(endMonth);
        List<Long> timeList = new ArrayList<>();
        timeList.add(monthTime);
        timeList.add(endTime);
        return timeList;
    }

    /**
     * 默认年默认统计条件
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 20:37
     */
    public static List<Long> getHomeYearDefaultInfo() {
        int startYear = -12;
        Long yearTime = CastStatisticalUtil.getAdvanceNumberYear(startYear);
        int endYear = -1;
        Long endTime = CastStatisticalUtil.getAdvanceNumberYear(endYear);
        List<Long> timeList = new ArrayList<>();
        timeList.add(yearTime);
        timeList.add(endTime);
        return timeList;
    }

    /**
     * 获取首页默认工单增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 11:03
     * @param timeList 时间区间
     * @return 返回首页默认工单增量统计
     */
    public static List<QueryProcCountByTimeStatisticalVo> getHomeDefaultProcCount(List<Long> timeList) {
        List<QueryProcCountByTimeStatisticalVo> queryProcCountByTimeStatisticalVoList = CastStatisticalUtil.getProcCountVo(timeList);
        return queryProcCountByTimeStatisticalVoList;
    }

    /**
     * 获取首页默认工单周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 11:03
     * @param timeList 时间区间
     * @return 返回首页默认工单周增量统计
     */
    public static List<QueryProcCountByWeekStatisticalVo> getHomeDefaultWeekProcCount(List<Long> timeList) {
        List<QueryProcCountByWeekStatisticalVo> queryProcCountByWeekStatisticalVoList = CastStatisticalUtil.getProcCountWeekVo(timeList);
        return queryProcCountByWeekStatisticalVoList;
    }

    /**
     * 获取首页默认工单月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 11:03
     * @param timeList 时间区间
     * @return 返回首页默认工单月增量统计
     */
    public static List<QueryProcCountByMonthStatisticalVo> getHomeDefaultMonthProcCount(List<Long> timeList) {
        List<QueryProcCountByMonthStatisticalVo> queryProcCountByMonthStatisticalVoList = CastStatisticalUtil.getProcCountMonthVo(timeList);
        return queryProcCountByMonthStatisticalVoList;
    }

    /**
     * 获取首页默认工单年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 11:03
     * @param timeList 时间区间
     * @return 返回首页默认工单月增量统计
     */
    public static List<QueryProcCountByYearStatisticalVo> getHomeDefaultYearProcCount(List<Long> timeList) {
        List<QueryProcCountByYearStatisticalVo> queryProcCountByYearStatisticalVoList = CastStatisticalUtil.getProcCountYearVo(timeList);
        return queryProcCountByYearStatisticalVoList;
    }

    /**
     * 获取默认工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 16:05
     * @param req 默认数量
     * @return 获取默认工单数量
     */
    public static List<QueryProcCountByTimeStatisticalVo> getDefaultProcCountByTime(QueryProcCountByTimeReq req) {
        List<QueryProcCountByTimeStatisticalVo> queryProcCountByTimeStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(req)) {
            List<Long> timeList = req.getTimeList();
            queryProcCountByTimeStatisticalVoList = CastStatisticalUtil.getProcCountVo(timeList);
        }
        return queryProcCountByTimeStatisticalVoList;
    }

    /**
     * 获取默认周统计工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 16:05
     * @param req 默认数量
     * @return 获取默认周统计工单数量
     */
    public static List<QueryProcCountByWeekStatisticalVo> getDefaultProcCountByWeek(QueryProcCountByWeekReq req) {
        List<QueryProcCountByWeekStatisticalVo> queryProcCountByWeekStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(req)) {
            List<Long> timeList = req.getTimeList();
            queryProcCountByWeekStatisticalVoList = CastStatisticalUtil.getProcCountWeekVo(timeList);
        }
        return queryProcCountByWeekStatisticalVoList;
    }

    /**
     * 获取默认月统计工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 16:05
     * @param req 默认数量
     * @return 获取默认月统计工单数量
     */
    public static List<QueryProcCountByMonthStatisticalVo> getDefaultProcCountByMonth(QueryProcCountByMonthReq req) {
        List<QueryProcCountByMonthStatisticalVo> queryProcCountByMonthStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(req)) {
            List<Long> timeList = req.getTimeList();
            queryProcCountByMonthStatisticalVoList = CastStatisticalUtil.getProcCountMonthVo(timeList);
        }
        return queryProcCountByMonthStatisticalVoList;
    }

    /**
     * 获取默认年统计工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 16:05
     * @param req 默认数量
     * @return 获取默认年统计工单数量
     */
    public static List<QueryProcCountByYearStatisticalVo> getDefaultProcCountByYear(QueryProcCountByYearReq req) {
        List<QueryProcCountByYearStatisticalVo> queryProcCountByYearStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(req)) {
            List<Long> timeList = req.getTimeList();
            queryProcCountByYearStatisticalVoList = CastStatisticalUtil.getProcCountYearVo(timeList);
        }
        return queryProcCountByYearStatisticalVoList;
    }

    /**
     * 获取工单周统计默认数据
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 16:02
     * @param timeList 时间集合
     * @return 返回工单周统计默认数据
     */
    public static List<QueryProcCountByWeekStatisticalVo> getProcCountWeekVo(List<Long> timeList) {
        List<QueryProcCountByWeekStatisticalVo> queryProcCountByWeekStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(timeList)) {
            int timeListCount = 2;
            if (timeList.size() == timeListCount) {
                long startTime = timeList.get(0);
                long endTime = timeList.get(1);
                //获得相差的周数
                int diffWeek = DateUtilInfos.getWeekNum(startTime, endTime);
                if (diffWeek >= 0) {
                    List<Long> weekDateList = DateUtilInfos.getWeekDateList(startTime, diffWeek);
                    if (weekDateList.size() > 0) {
                        QueryProcCountByWeekStatisticalVo voOne = null;
                        for (Long weekDateInfo : weekDateList) {
                            voOne = new QueryProcCountByWeekStatisticalVo();
                            voOne.setOrderCount(0);
                            voOne.setNowDate(weekDateInfo);
                            if (!ObjectUtils.isEmpty(weekDateInfo)) {
                                voOne.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Long.valueOf(weekDateInfo)));
                            }
                            queryProcCountByWeekStatisticalVoList.add(voOne);
                        }
                    }
                }
            }
        }
        return queryProcCountByWeekStatisticalVoList;
    }

    /**
     * 默认工单月度统计的结果
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 9:06
     * @param timeList 时间区间
     * @return 默认工单月度统计的结果
     */
    public static List<QueryProcCountByMonthStatisticalVo> getProcCountMonthVo(List<Long> timeList) {
        List<QueryProcCountByMonthStatisticalVo> queryProcCountByMonthStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(timeList)) {
            int timeListCount = 2;
            if (timeList.size() == timeListCount) {
                long startTime = timeList.get(0);
                long endTime = timeList.get(1);
                //获得相差的月份数
                int diffMonth = DateUtilInfos.getMonthNum(startTime, endTime);
                if (diffMonth >= 0) {
                    List<Long> monthDateList = DateUtilInfos.getMonthDateList(startTime, diffMonth);
                    if (monthDateList.size() > 0) {
                        QueryProcCountByMonthStatisticalVo voOne = null;
                        for (Long monthDateInfo : monthDateList) {
                            voOne = new QueryProcCountByMonthStatisticalVo();
                            voOne.setOrderCount(0);
                            voOne.setNowDate(monthDateInfo);
                            if (!ObjectUtils.isEmpty(monthDateInfo)) {
                                voOne.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Long.valueOf(monthDateInfo)));
                            }
                            queryProcCountByMonthStatisticalVoList.add(voOne);
                        }
                    }
                }
            }
        }
        return queryProcCountByMonthStatisticalVoList;
    }

    /**
     * 默认工单年度统计的结果
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 9:06
     * @param timeList 时间区间
     * @return 默认工单年度统计的结果
     */
    public static List<QueryProcCountByYearStatisticalVo> getProcCountYearVo(List<Long> timeList) {
        List<QueryProcCountByYearStatisticalVo> queryProcCountByYearStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(timeList)) {
            int timeListCount = 2;
            if (timeList.size() == timeListCount) {
                long startTime = timeList.get(0);
                long endTime = timeList.get(1);
                //获得相差的年份数
                int diffYear = DateUtilInfos.getYearNum(startTime, endTime);
                if (diffYear >= 0) {
                    List<Long> yearDateList = DateUtilInfos.getYearDateList(startTime, diffYear);
                    if (yearDateList.size() > 0) {
                        QueryProcCountByYearStatisticalVo voOne = null;
                        for (Long yearDateInfo : yearDateList) {
                            voOne = new QueryProcCountByYearStatisticalVo();
                            voOne.setOrderCount(0);
                            voOne.setNowDate(yearDateInfo);
                            if (!ObjectUtils.isEmpty(yearDateInfo)) {
                                voOne.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Long.valueOf(yearDateInfo)));
                            }
                            queryProcCountByYearStatisticalVoList.add(voOne);
                        }
                    }
                }
            }
        }
        return queryProcCountByYearStatisticalVoList;
    }

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 10:53
     * @param timeList 时间数组
     * @return 返回时间信息
     */
    public static List<QueryProcCountByTimeStatisticalVo> getProcCountVo(List<Long> timeList) {
        List<QueryProcCountByTimeStatisticalVo> queryProcCountByTimeStatisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(timeList)) {
            int timeListCount = 2;
            Calendar calendar = null;
            if (timeList.size() == timeListCount) {
                long startTime = timeList.get(0);
                startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
                long endTime = timeList.get(1);
                endTime = CastStatisticalUtil.getTimeZeroTime(endTime);
                if (endTime > startTime) {
                    //每天毫秒数
                    long nd = 1000 * 24 * 60 * 60;
                    //开始时间和结束时间相差的毫秒数
                    long diffTime = endTime - startTime;
                    //开始时间和结束时间相差的天数
                    long day = diffTime/nd;
                    int dayInt = new Long(day).intValue();
                    dayInt = dayInt + 1;
                    QueryProcCountByTimeStatisticalVo vo = null;
                    long statisticalTime = 0L;
                    for (int i = 0 ; i < dayInt; i++) {
                        vo = new QueryProcCountByTimeStatisticalVo();
                        if (0L == statisticalTime) {
                            statisticalTime = startTime;
                        } else {
                            statisticalTime = statisticalTime + nd;
                        }
                        vo.setOrderCount(0);
                        vo.setNowDate(statisticalTime);
                        if (!ObjectUtils.isEmpty(statisticalTime)) {
                            vo.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Long.valueOf(statisticalTime)));
                        }
                        queryProcCountByTimeStatisticalVoList.add(vo);
                    }
                }
            }
        }
        return queryProcCountByTimeStatisticalVoList;
    }

    public static void main(String [] args) {
        CastStatisticalUtil.getNowTimeAdd(-1);
        CastStatisticalUtil.getNowDayLastTimeAdd(-1);
        CastStatisticalUtil.getAdvanceNumberWeek(-1);
        CastStatisticalUtil.getAdvanceLastNumberWeek(-1);
        CastStatisticalUtil.getAdvanceNumberMonth(-1);
        CastStatisticalUtil.getAdvanceLastNumberMonth(-1);
        CastStatisticalUtil.getAdvanceNumberYear(-1);
        CastStatisticalUtil.getAdvanceLastNumberYear(-1);
    }
}
