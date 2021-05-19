package com.fiberhome.filink.workflowbusinessserver.service.impl.statistical;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.export.*;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.*;
import com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness.WorkflowBusinessI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.*;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.dao.statistical.ProcStatisticalDao;
import com.fiberhome.filink.workflowbusinessserver.export.statistical.*;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.*;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.service.statistical.ProcStatisticalService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastMapUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastStatisticalUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcDeviceTypeOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcErrorReasonOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcProcessingSchemeOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcStatusOverviewStatisticalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 工单统计实现类
 *
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 9:52
 */
@Service
@Slf4j
public class ProcStatisticalServiceImpl implements ProcStatisticalService {

    @Autowired
    private ProcStatisticalDao procStatisticalDao;

    /**
     * 工单信息
     */
    @Autowired
    private ProcBaseService procBaseService;

    /**
     * 部门feign
     */
    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProcBaseDao procBaseDao;

    /**
     * 巡检记录service
     */
    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * 巡检工单service
     */
    @Autowired
    private ProcInspectionService procInspectionService;

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * 工单状态导出类
     */
    @Autowired
    private ProcStatusStatisticalExport procStatusStatisticalExport;

    /**
     * 工单设施类型导出类
     */
    @Autowired
    private ProcDeviceTypeStatisticalExport procDeviceTypeStatisticalExport;

    /**
     * 工单异常原因导出类
     */
    @Autowired
    private ProcErrorReasonStatisticalExport procErrorReasonStatisticalExport;

    /**
     * 工单处理方案导出类
     */
    @Autowired
    private ProcProcessingSchemeStatisticalExport procProcessingSchemeStatisticalExport;

    /**
     * 工单区域比导出类
     */
    @Autowired
    private ProcAreaPercentStatisticalExport procAreaPercentStatisticalExport;

    /**
     * 工单设施统计数量导出类
     */
    @Autowired
    private ProcTopListStatisticalExport procTopListStatisticalExport;

    /**
     * 工单类型service
     */
    @Autowired
    private ProcLogService procLogService;


    /**
     * 查询工单状态统计信息
     *
     * @param req 工单状态统计参数
     * @return 工单统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/5/28 19:14
     */
    @Override
    public Result queryListProcGroupByProcStatus(QueryCondition<QueryListProcGroupByProcStatusReq> req) {

        //业务条件
        QueryListProcGroupByProcStatusReq bizCondition = (QueryListProcGroupByProcStatusReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcStatusStatistical> procStatusList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //根据设施类型 和 区域查询工单的状态信息
            procStatusList = procStatisticalDao.queryListProcInspectionGroupByProcStatus(req);

        } else {
            //销障工单
            //根据设施类型 和 区域查询工单的状态信息
            procStatusList = procStatisticalDao.queryListProcClearGroupByProcStatus(req);
        }

        //将查询到的工单状态数据转换成工单状态返回类信息
        List<ProcStatusStatisticalVo> procStatusVoList = ProcStatusStatisticalVo.castProcStatusVoForProcStatusBean(procStatusList);

        //获得状态区域编号集合
        List<String> areaIdList = bizCondition.getAreaIdList();
        //获得状态集合
        List<String> statusList = ProcStatisticalConstants.statusList;

        //获得初始化工单状态数据
        Map<String, Map<String, Object>> procStatusStatisticalMap = this.getDefaultStatisticalMap(areaIdList, statusList);

        //返回数据格式
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procStatusVoList)) {

            //将存入的值放入到Map中
            for (ProcStatusStatisticalVo procStatusOne : procStatusVoList) {
                Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                if (procStatusStatisticalMap.containsKey(procStatusOne.getAreaId())) {
                    mapOne = procStatusStatisticalMap.get(procStatusOne.getAreaId());
                }
                mapOne.put(procStatusOne.getStatus(), procStatusOne.getOrderCount());
                procStatusStatisticalMap.put(procStatusOne.getAreaId(), mapOne);
            }
        }

        //工单状态数据信息
        for (String areaIdOne : areaIdList) {
            retList.add(procStatusStatisticalMap.get(areaIdOne));
        }
        return ResultUtils.success(retList);
    }

    /**
     * 查询工单状态概览
     *
     * @param req 工单状态概览参数
     * @return 工单状态概览数据
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 14:03
     */
    @Override
    public Result queryListProcOverviewGroupByProcStatus(QueryCondition<QueryListProcOverviewGroupByProcStatusReq> req) {

        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByProcStatusReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //默认初始化工单状态数据
        List<ProcStatusOverviewStatisticalVo> defaultVoList = ProcStatusOverviewStatisticalVo.getDefaultOverviewStatusVoList(bizCondition);

        //工单状态概览返回类
        List<ProcStatusOverviewStatisticalVo> procStatusVoList = this.getProcStatusOverviewVoList(req);

        //工单状态概览返回数据
        List<ProcStatusOverviewStatisticalVo> retVoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //工单概览map
            Map<String, ProcStatusOverviewStatisticalVo> overviewStatisticalVoMap = CastStatisticalUtil.getProcStatusOverviewVoMap(procStatusVoList);

            int allNumber = 0;
            List<ProcStatusOverviewStatisticalVo> procStatusVoAddList = new ArrayList<>();
            for (ProcStatusOverviewStatisticalVo statisticalVo : defaultVoList) {
                if (!ObjectUtils.isEmpty(overviewStatisticalVoMap)) {
                    if (overviewStatisticalVoMap.containsKey(statisticalVo.getStatus())) {
                        ProcStatusOverviewStatisticalVo voOne = overviewStatisticalVoMap.get(statisticalVo.getStatus());
                        statisticalVo.setStatusName(voOne.getStatusName());
                        statisticalVo.setOrderCount(voOne.getOrderCount());
                        allNumber = allNumber + voOne.getOrderCount();
                    }
                }
                procStatusVoAddList.add(statisticalVo);
            }


            if (!ObjectUtils.isEmpty(procStatusVoAddList)) {
                for (ProcStatusOverviewStatisticalVo statisticalVo : procStatusVoAddList) {
                    statisticalVo = ProcStatusOverviewStatisticalVo.setOrderPercentToVo(statisticalVo, allNumber);
                    retVoList.add(statisticalVo);
                }
            }

        }
        return ResultUtils.success(retVoList);
    }

    /**
     * 查询当前日期新增工单数量
     *
     * @param req 当前日期新增工单数量
     * @return 当前日期新增工单数量
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 15:29
     */
    @Override
    public Result queryNowDateAddOrderCount(QueryCondition<QueryNowDateAddOrderCountReq> req) {
        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryNowDateAddOrderCountReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        int nowDay = 0;
        if (ObjectUtils.isEmpty(bizCondition.getNowDateTime())) {
            bizCondition.setNowDateTime(CastStatisticalUtil.getNowTimeAdd(nowDay) / 1000);
        }
        req.setBizCondition(bizCondition);
        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcNowDateAddOrderCountOverview> addOrderList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询今日新增信息
            addOrderList = procStatisticalDao.queryNowDateInspectionAddOrderCount(req);

        } else {
            //销障工单
            //查询今日新增信息
            addOrderList = procStatisticalDao.queryNowDateClearAddOrderCount(req);
        }
        return ResultUtils.success(addOrderList);
    }


    /**
     * 工单状态概览集合
     *
     * @param req 工单状态概览参数
     * @return 获取工单状态概览集合
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcStatusOverviewStatisticalVo> getProcStatusOverviewVoList(QueryCondition<QueryListProcOverviewGroupByProcStatusReq> req) {
        //工单类型
        String procType = req.getBizCondition().getProcType();
        List<ProcStatusOverviewStatistical> procStatusList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询工单信息
            procStatusList = procStatisticalDao.queryListProcInspectionOverviewGroupByProcStatus(req);
        } else {
            //销障工单
            //查询工单信息
            procStatusList = procStatisticalDao.queryListProcClearOverviewGroupByProcStatus(req);
        }
        //将查询到的工单状态数据转换成工单状态返回类信息
        List<ProcStatusOverviewStatisticalVo> procStatusVoList = ProcStatusOverviewStatisticalVo.castProcStatusVoForProcStatusBean(procStatusList);
        return procStatusVoList;
    }


    /**
     * 查询工单处理方案统计信息
     *
     * @param req 工单处理方案统计参数
     * @return 工单处理方案结果
     * @author hedongwei@wistronits.com
     * @date 2019/5/28 19:14
     */
    @Override
    public Result queryListProcGroupByProcProcessingScheme(QueryCondition<QueryListProcGroupByProcProcessingSchemeReq> req) {

        //业务条件
        QueryListProcGroupByProcProcessingSchemeReq bizCondition = (QueryListProcGroupByProcProcessingSchemeReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //部门权限
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcProcessingSchemeStatistical> procProcessingSchemeList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //根据设施类型 和 区域查询工单的处理方案信息
            procProcessingSchemeList = procStatisticalDao.queryListProcInspectionGroupByProcProcessingScheme(req);

        } else {
            //销障工单
            //根据设施类型 和 区域查询工单的处理方案信息
            procProcessingSchemeList = procStatisticalDao.queryListProcClearGroupByProcProcessingScheme(req);
        }

        //将查询到的工单信息的转换
        List<ProcProcessingSchemeStatisticalVo> retVoList = ProcProcessingSchemeStatisticalVo.castProcessingSchemeVoForProcStatusBean(procProcessingSchemeList);

        //获得处理方案区域编号集合
        List<String> areaIdList = bizCondition.getAreaIdList();
        //获得处理方案集合
        List<String> processingSchemeList = ProcStatisticalConstants.processingSchemeList;

        //获得初始化工单处理方案数据
        Map<String, Map<String, Object>> processingSchemeStatisticalMap = this.getDefaultStatisticalMap(areaIdList, processingSchemeList);

        //返回数据格式
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(retVoList)) {

            //将存入的值放入到Map中
            for (ProcProcessingSchemeStatisticalVo schemeVo : retVoList) {
                Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                if (processingSchemeStatisticalMap.containsKey(schemeVo.getAreaId())) {
                    mapOne = processingSchemeStatisticalMap.get(schemeVo.getAreaId());
                }
                if (!ObjectUtils.isEmpty(schemeVo.getProcessingScheme())) {
                    mapOne.put(schemeVo.getProcessingScheme(), schemeVo.getOrderCount());
                }
                processingSchemeStatisticalMap.put(schemeVo.getAreaId(), mapOne);
            }
        }

        //工单处理方案数据信息
        for (String areaIdOne : areaIdList) {
            retList.add(processingSchemeStatisticalMap.get(areaIdOne));
        }

        return ResultUtils.success(retList);
    }


    /**
     * 查询处理方案概览统计
     *
     * @param req 处理方案概览参数
     * @return 查询处理方案
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 19:11
     */
    @Override
    public Result queryListProcOverviewGroupByProcProcessingScheme(QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> req) {

        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByProcessingSchemeReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //获取处理方案
        List<ProcProcessingSchemeOverviewStatisticalVo> defaultVoList = ProcProcessingSchemeOverviewStatisticalVo.getDefaultOverviewProcessingSchemeVoList(bizCondition);

        //工单处理方案概览返回类
        List<ProcProcessingSchemeOverviewStatisticalVo> schemeOverviewVoList = this.getProcessingSchemeOverviewVoList(req);


        //工单处理方案概览返回数据
        List<ProcProcessingSchemeOverviewStatisticalVo> retVoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //工单概览map
            Map<String, ProcProcessingSchemeOverviewStatisticalVo> schemeOverviewMap = CastStatisticalUtil.getProcProcessingSchemeOverviewVoMap(schemeOverviewVoList);


            for (ProcProcessingSchemeOverviewStatisticalVo statisticalVo : defaultVoList) {
                if (!ObjectUtils.isEmpty(schemeOverviewMap)) {
                    if (schemeOverviewMap.containsKey(statisticalVo.getProcessingScheme())) {
                        ProcProcessingSchemeOverviewStatisticalVo voOne = schemeOverviewMap.get(statisticalVo.getProcessingScheme());
                        statisticalVo.setProcessingSchemeName(voOne.getProcessingSchemeName());
                        statisticalVo.setOrderCount(voOne.getOrderCount());
                    }
                }
                retVoList.add(statisticalVo);
            }
        }
        return ResultUtils.success(retVoList);
    }

    /**
     * 处理方案概览集合
     *
     * @param req 处理方案概览参数
     * @return 获取处理方案概览集合
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcProcessingSchemeOverviewStatisticalVo> getProcessingSchemeOverviewVoList(QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> req) {
        //工单类型
        String procType = req.getBizCondition().getProcType();

        List<ProcProcessingSchemeOverviewStatistical> schemeOverviewList;

        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询处理方案统计信息
            schemeOverviewList = procStatisticalDao.queryListProcInspectionOverviewGroupByProcProcessingScheme(req);
        } else {
            //销障工单
            //查询处理方案统计信息
            schemeOverviewList = procStatisticalDao.queryListProcClearOverviewGroupByProcProcessingScheme(req);
        }

        //将查询到的处理方案数据转换成处理方案返回类信息
        List<ProcProcessingSchemeOverviewStatisticalVo> schemeOverviewVoList = ProcProcessingSchemeOverviewStatisticalVo.castProcessingSchemeVoForProcErrorReasonBean(schemeOverviewList);
        return schemeOverviewVoList;
    }


    /**
     * 查询工单故障原因统计信息
     *
     * @param req 查询工单故障原因统计参数
     * @return 工单故障原因统计信息
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 21:06
     */
    @Override
    public Result queryListProcGroupByErrorReason(QueryCondition<QueryListProcGroupByProcErrorReasonReq> req) {

        //业务条件
        QueryListProcGroupByProcErrorReasonReq bizCondition = (QueryListProcGroupByProcErrorReasonReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //部门权限
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcErrorReasonStatistical> errorReasonInfoList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询故障原因统计数据
            errorReasonInfoList = procStatisticalDao.queryListProcInspectionGroupByErrorReason(req);
        } else {
            //销障工单
            //查询故障原因统计数据
            errorReasonInfoList = procStatisticalDao.queryListProcClearGroupByErrorReason(req);
        }

        //将查询到的工单信息的转换
        List<ProcErrorReasonStatisticalVo> voList = ProcErrorReasonStatisticalVo.castErrorReasonVoForProcStatusBean(errorReasonInfoList);

        //获得故障原因区域编号集合
        List<String> areaIdList = bizCondition.getAreaIdList();
        //获得故障原因集合
        List<String> errorReasonList = ProcStatisticalConstants.errorReasonList;

        //获得初始化工单故障原因数据
        Map<String, Map<String, Object>> errorReasonStatisticalMap = this.getDefaultStatisticalMap(areaIdList, errorReasonList);

        //返回数据格式
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(voList)) {

            //将存入的值放入到Map中
            for (ProcErrorReasonStatisticalVo errorReasonVo : voList) {
                Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                if (errorReasonStatisticalMap.containsKey(errorReasonVo.getAreaId())) {
                    mapOne = errorReasonStatisticalMap.get(errorReasonVo.getAreaId());
                }
                if (!ObjectUtils.isEmpty(errorReasonVo.getErrorReason())) {
                    mapOne.put(errorReasonVo.getErrorReason(), errorReasonVo.getOrderCount());
                }
                errorReasonStatisticalMap.put(errorReasonVo.getAreaId(), mapOne);
            }
        }

        //工单处理方案数据信息
        for (String areaIdOne : areaIdList) {
            retList.add(errorReasonStatisticalMap.get(areaIdOne));
        }

        return ResultUtils.success(retList);
    }

    /**
     * 故障原因概览统计
     *
     * @param req 故障原因概览统计参数
     * @return 故障原因概览统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 21:48
     */
    @Override
    public Result queryListProcOverviewGroupByProcErrorReason(QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> req) {
        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByErrorReasonReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //获取故障原因
        List<ProcErrorReasonOverviewStatisticalVo> defaultVoList = ProcErrorReasonOverviewStatisticalVo.getDefaultOverviewErrorReasonVoList(bizCondition);

        //工单故障原因概览返回类
        List<ProcErrorReasonOverviewStatisticalVo> errorReasonOverviewVoList = this.getErrorReasonOverviewVoList(req);

        //工单故障原因概览返回数据
        List<ProcErrorReasonOverviewStatisticalVo> retVoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //工单故障原因概览map
            Map<String, ProcErrorReasonOverviewStatisticalVo> errorReasonOverviewMap = CastStatisticalUtil.getProcErrorReasonOverviewVoMap(errorReasonOverviewVoList);


            for (ProcErrorReasonOverviewStatisticalVo statisticalVo : defaultVoList) {
                if (!ObjectUtils.isEmpty(errorReasonOverviewMap)) {
                    if (errorReasonOverviewMap.containsKey(statisticalVo.getErrorReason())) {
                        ProcErrorReasonOverviewStatisticalVo voOne = errorReasonOverviewMap.get(statisticalVo.getErrorReason());
                        statisticalVo.setErrorReasonName(voOne.getErrorReasonName());
                        statisticalVo.setOrderCount(voOne.getOrderCount());
                    }
                }
                retVoList.add(statisticalVo);
            }
        }
        return ResultUtils.success(retVoList);
    }

    /**
     * 故障原因概览集合
     *
     * @param req 故障原因概览参数
     * @return 获取故障原因概览集合
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcErrorReasonOverviewStatisticalVo> getErrorReasonOverviewVoList(QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> req) {

        //工单类型
        String procType = req.getBizCondition().getProcType();
        List<ProcErrorReasonOverviewStatistical> schemeOverviewList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询故障原因统计信息
            schemeOverviewList = procStatisticalDao.queryListProcInspectionOverviewGroupByErrorReason(req);

        } else {
            //销障工单
            //查询故障原因统计信息
            schemeOverviewList = procStatisticalDao.queryListProcClearOverviewGroupByErrorReason(req);
        }

        //将查询到的故障原因数据转换成故障原因返回类信息
        List<ProcErrorReasonOverviewStatisticalVo> schemeOverviewVoList = ProcErrorReasonOverviewStatisticalVo.castErrorReasonVoForErrorReasonBean(schemeOverviewList);
        return schemeOverviewVoList;
    }

    /**
     * 查询设施类型统计
     *
     * @param req 设施类型统计参数
     * @return 设施类型统计数据
     * @author hedongwei@wistronits.com
     * @date 2019/5/31 15:00
     */
    @Override
    public Result queryListProcGroupByDeviceType(QueryCondition<QueryListProcGroupByProcDeviceTypeReq> req) {
        //业务条件
        QueryListProcGroupByProcDeviceTypeReq bizCondition = (QueryListProcGroupByProcDeviceTypeReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //部门权限
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcDeviceTypeStatistical> procDeviceTypeStatisticalList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询设施类型统计
            procDeviceTypeStatisticalList = procStatisticalDao.queryListProcInspectionGroupByDeviceType(req);

        } else {
            //销障工单
            //查询设施类型统计
            procDeviceTypeStatisticalList = procStatisticalDao.queryListProcClearGroupByDeviceType(req);
        }

        //获得设施类型统计信息
        List<ProcDeviceTypeStatisticalVo> voList = ProcDeviceTypeStatisticalVo.castDeviceTypeVoForProcStatusBean(procDeviceTypeStatisticalList);

        //获得设施类型区域编号集合
        List<String> areaIdList = bizCondition.getAreaIdList();
        //获得设施类型集合
        List<String> deviceTypeList = bizCondition.getDeviceTypeList();

        //获得初始化工单故障原因数据
        Map<String, Map<String, Object>> deviceTypeStatisticalMap = this.getDefaultStatisticalMap(areaIdList, deviceTypeList);

        //返回数据格式
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(voList)) {

            //将存入的值放入到Map中
            for (ProcDeviceTypeStatisticalVo deviceTypeVo : voList) {
                Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                if (deviceTypeStatisticalMap.containsKey(deviceTypeVo.getAreaId())) {
                    mapOne = deviceTypeStatisticalMap.get(deviceTypeVo.getAreaId());
                }
                if (!ObjectUtils.isEmpty(deviceTypeVo.getDeviceType())) {
                    mapOne.put(deviceTypeVo.getDeviceType(), deviceTypeVo.getOrderCount());
                }
                deviceTypeStatisticalMap.put(deviceTypeVo.getAreaId(), mapOne);
            }
        }

        //工单设施类型数据信息
        for (String areaIdOne : areaIdList) {
            retList.add(deviceTypeStatisticalMap.get(areaIdOne));
        }
        return ResultUtils.success(retList);
    }


    /**
     * 设施类型概览统计
     *
     * @param req 设施类型概览统计参数
     * @return 设施类型概览统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 21:48
     */
    @Override
    public Result queryListProcOverviewGroupByProcDeviceType(QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> req) {

        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByDeviceTypeReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //获取设施类型
        List<ProcDeviceTypeOverviewStatisticalVo> defaultVoList = ProcDeviceTypeOverviewStatisticalVo.getDefaultOverviewDeviceTypeVoList(bizCondition);

        //工单设施类型概览返回类
        List<ProcDeviceTypeOverviewStatisticalVo> deviceTypeOverviewVoList = this.getDeviceTypeOverviewVoList(req);

        //工单设施类型概览返回数据
        List<ProcDeviceTypeOverviewStatisticalVo> retVoList = new ArrayList<>();


        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //工单概览map
            Map<String, ProcDeviceTypeOverviewStatisticalVo> deviceTypeOverviewMap = CastStatisticalUtil.getProcDeviceTypeOverviewVoMap(deviceTypeOverviewVoList);


            for (ProcDeviceTypeOverviewStatisticalVo statisticalVo : defaultVoList) {
                if (!ObjectUtils.isEmpty(deviceTypeOverviewMap)) {
                    if (deviceTypeOverviewMap.containsKey(statisticalVo.getDeviceType())) {
                        ProcDeviceTypeOverviewStatisticalVo voOne = deviceTypeOverviewMap.get(statisticalVo.getDeviceType());
                        statisticalVo.setDeviceTypeName(voOne.getDeviceTypeName());
                        statisticalVo.setOrderCount(voOne.getOrderCount());
                    }
                }
                retVoList.add(statisticalVo);
            }
        }
        return ResultUtils.success(retVoList);
    }


    /**
     * 设施类型概览集合
     *
     * @param req 设施类型概览参数
     * @return 获取设施类型概览集合
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcDeviceTypeOverviewStatisticalVo> getDeviceTypeOverviewVoList(QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> req) {
        //工单类型
        String procType = req.getBizCondition().getProcType();
        List<ProcDeviceTypeOverviewStatistical> deviceTypeOverviewList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询设施类型统计信息
            deviceTypeOverviewList =  procStatisticalDao.queryListProcInspectionOverviewGroupByDeviceTypeList(req);
        } else {
            //销障工单
            //查询设施类型统计信息
            deviceTypeOverviewList =  procStatisticalDao.queryListProcClearOverviewGroupByDeviceTypeList(req);
        }
        //将查询到的设施类型数据转换成设施类型返回类信息
        List<ProcDeviceTypeOverviewStatisticalVo> deviceTypeOverviewVoList = ProcDeviceTypeOverviewStatisticalVo.castDeviceTypeVoForProcDeviceTypeBean(deviceTypeOverviewList);
        return deviceTypeOverviewVoList;
    }

    /**
     * 查询工单区域比信息
     *
     * @param req 工单区域比参数
     * @return 工单区域比统计
     * @author hedongwei@wistronits.com
     * @date 2019/5/31 14:36
     */
    @Override
    public Result queryListProcGroupByAreaPercent(QueryCondition<QueryListProcGroupByProcAreaPercentReq> req) {
        QueryListProcGroupByProcAreaPercentReq bizCondition = (QueryListProcGroupByProcAreaPercentReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //部门权限
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcAreaPercentStatistical> areaPercentList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询工单区域比统计
            areaPercentList = procStatisticalDao.queryListProcInspectionGroupByAreaPercent(req);

        } else {
            //销障工单
            //查询工单区域比统计
            areaPercentList = procStatisticalDao.queryListProcClearGroupByAreaPercent(req);
        }
        //转换工单区域比统计
        List<ProcAreaPercentStatisticalVo> percentStatisticalVoList = ProcAreaPercentStatisticalVo.castAreaPercentVoForProcStatusBean(areaPercentList);


        //获得设施类型区域编号集合
        List<String> areaIdList = bizCondition.getAreaIdList();

        //获得初始化工单区域比数据
        Map<String, Map<String, Object>> areaPercentStatisticalMap = this.getDefaultStatisticalMap(areaIdList);

        //返回数据格式
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(percentStatisticalVoList)) {

            //将存入的值放入到Map中
            for (ProcAreaPercentStatisticalVo percentVo : percentStatisticalVoList) {
                Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                if (areaPercentStatisticalMap.containsKey(percentVo.getAreaId())) {
                    mapOne = areaPercentStatisticalMap.get(percentVo.getAreaId());
                }
                if (!ObjectUtils.isEmpty(percentVo.getAreaId())) {
                    mapOne.put(percentVo.getAreaId(), percentVo.getOrderCount());
                }
                areaPercentStatisticalMap.put(percentVo.getAreaId(), mapOne);
            }
        }

        //工单区域比数据信息
        for (String areaIdOne : areaIdList) {
            retList.add(areaPercentStatisticalMap.get(areaIdOne));
        }
        return ResultUtils.success(retList);
    }

    /**
     * 查询部门分组集合
     *
     * @param req 查询部门参数
     * @return 查询部门分组集合
     * @author hedongwei@wistronits.com
     * @date 2019/6/3 15:12
     */
    @Override
    public Result queryDeptListGroupByAccountabilityDept(QueryCondition<QueryDeptListGroupByAccountabilityDeptReq> req) {
        QueryDeptListGroupByAccountabilityDeptReq bizCondition = (QueryDeptListGroupByAccountabilityDeptReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAccountabilityDeptList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        req.setBizCondition(bizCondition);

        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcDepartmentStatistical> departmentStatisticalList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询工单部门信息
            departmentStatisticalList = procStatisticalDao.queryInspectionDeptListGroupByAccountabilityDept(req);

        } else {
            //销障工单
            //查询工单部门信息
            departmentStatisticalList = procStatisticalDao.queryClearDeptListGroupByAccountabilityDept(req);
        }

        //查询
        Map<String, String> getDepartmentMap = this.getDepartmentMap(bizCondition);

        //部门编号集合
        List<String> deptIdList = bizCondition.getAccountabilityDeptList();
        //默认数据
        List<ProcDepartmentStatisticalVo> defaultVoList = this.getDefaultDeptStatisticalList(deptIdList, getDepartmentMap);


        //查询部门编号信息
        List<ProcDepartmentStatisticalVo> procDepartmentStatisticalList = ProcDepartmentStatisticalVo.castDepartmentVoForProcStatusBean(departmentStatisticalList, getDepartmentMap);
        Map<String, ProcDepartmentStatisticalVo> procDepartmentStatisticalVoMap = this.getDepartmentStatisticalVoMap(procDepartmentStatisticalList);


        //将部门数量添加到数据中
        if (!ObjectUtils.isEmpty(defaultVoList) && !ObjectUtils.isEmpty(procDepartmentStatisticalVoMap)) {
            for (ProcDepartmentStatisticalVo defaultVoOne : defaultVoList) {
                if (procDepartmentStatisticalVoMap.containsKey(defaultVoOne.getDepartmentId())) {
                    String departmentId = defaultVoOne.getDepartmentId();
                    ProcDepartmentStatisticalVo departmentVo = procDepartmentStatisticalVoMap.get(departmentId);
                    defaultVoOne.setDepartmentCount(departmentVo.getDepartmentCount());
                }
            }
        }

        return ResultUtils.success(defaultVoList);
    }

    /**
     * 获取部门map
     *
     * @param req 查询关联部门参数
     * @return 获取部门map
     * @author hedongwei@wistronits.com
     * @date 2019/6/3 16:22
     */
    public Map<String, String> getDepartmentMap(QueryDeptListGroupByAccountabilityDeptReq req) {
        Map<String, String> departmentMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(req.getAccountabilityDeptList())) {
            Set<String> deptSet = new HashSet<>();
            //遍历部门信息，查询部门信息
            for (String deptOne : req.getAccountabilityDeptList()) {
                deptSet.add(deptOne);
            }
            //部门集合
            List<String> deptIdList = new ArrayList<>();
            deptIdList.addAll(deptSet);
            List<Department> departmentAllSearchList = departmentFeign.queryDepartmentFeignById(deptIdList);
            departmentMap = CastMapUtil.getDepartmentMap(departmentAllSearchList);
        }
        return departmentMap;
    }

    /**
     * 查询工单前n位的设施数量
     *
     * @param req 查询条件
     * @return 查询工单前n位的设施数量
     * @author hedongwei@wistronits.com
     * @date 2019/6/3 10:10
     */
    @Override
    public Result queryListDeviceCountGroupByDevice(QueryCondition<QueryTopListProcGroupByProcDeviceReq> req) {
        QueryTopListProcGroupByProcDeviceReq bizCondition = (QueryTopListProcGroupByProcDeviceReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }
        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //部门权限
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //工单类型
        String procType = bizCondition.getProcType();
        List<ProcDeviceTopStatistical> deviceTopList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //查询工单前n位的设施数量
            deviceTopList = procStatisticalDao.queryInspectionListDeviceCountGroupByDevice(req);

        } else {
            //销障工单
            //查询工单前n位的设施数量
            deviceTopList = procStatisticalDao.queryClearListDeviceCountGroupByDevice(req);
        }

        //工单设施前n位的设施数量集合
        List<ProcDeviceTopStatisticalVo> procDeviceTopVoList = ProcDeviceTopStatisticalVo.castDeviceTopVoForProcStatusBean(deviceTopList);
        return ResultUtils.success(procDeviceTopVoList);
    }

    /**
     * 查询工单新增增量统计
     *
     * @param req 工单新增增量统计
     * @return 工单新增增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryProcAddListCountGroupByDay(QueryCondition<QueryProcCountByTimeReq> req) {

        QueryProcCountByTimeReq bizCondition = (QueryProcCountByTimeReq) req.getBizCondition();

        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())
                    || ObjectUtils.isEmpty(bizCondition.getProcType()) || ObjectUtils.isEmpty(bizCondition.getTimeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }


        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //部门权限
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //部门筛选条件
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }

        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByTimeStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByTime(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoDateStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_date_statistical", ProcInfoDateStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }

    /**
     * 查询工单新增周增量统计
     *
     * @param req 工单新增周增量统计
     * @return 工单新增增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryProcAddListCountGroupByWeek(QueryCondition<QueryProcCountByWeekReq> req) {
        QueryProcCountByWeekReq bizCondition = (QueryProcCountByWeekReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())
                    || ObjectUtils.isEmpty(bizCondition.getProcType()) || ObjectUtils.isEmpty(bizCondition.getTimeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }


        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //部门权限
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //部门筛选条件
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }



        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByWeekStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByWeek(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoWeekStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_week_statistical", ProcInfoWeekStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryWeekProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }


    /**
     * 查询工单新增月增量统计
     *
     * @param req 工单新增月增量统计
     * @return 工单新增增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryProcAddListCountGroupByMonth(QueryCondition<QueryProcCountByMonthReq> req) {
        QueryProcCountByMonthReq bizCondition = (QueryProcCountByMonthReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())
                    || ObjectUtils.isEmpty(bizCondition.getProcType()) || ObjectUtils.isEmpty(bizCondition.getTimeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }


        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //部门权限
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //部门筛选条件
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }


        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByMonthStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByMonth(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoMonthStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_month_statistical", ProcInfoMonthStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryMonthProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }


    /**
     * 查询工单新增年增量统计
     *
     * @param req 工单新增年增量统计
     * @return 工单新增增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryProcAddListCountGroupByYear(QueryCondition<QueryProcCountByYearReq> req) {
        QueryProcCountByYearReq bizCondition = (QueryProcCountByYearReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())
                    || ObjectUtils.isEmpty(bizCondition.getProcType()) || ObjectUtils.isEmpty(bizCondition.getTimeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }


        //权限信息筛选
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //部门权限
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //部门筛选条件
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }


        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByYearStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByYear(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoYearStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_year_statistical", ProcInfoYearStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryYearProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }

    /**
     * 查询工单新增增量统计
     *
     * @param req 工单新增增量统计
     * @return 工单新增增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByDay(QueryCondition<QueryHomeProcCountByTimeReq> req) {
        QueryHomeProcCountByTimeReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeDefaultInfo());

        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByTimeStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultProcCount(bizCondition.getTimeList());

        //获取权限信息
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());


        //权限设施类型
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //权限区域编号集合
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //权限部门编号集合
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //时间条件
        List<Long> timeList = bizCondition.getTimeList();
        //工单类型
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //获取查询条件
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoDateStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_date_statistical", ProcInfoDateStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }

    /**
     * 查询工单新增周增量统计
     *
     * @param req 工单新增周增量统计
     * @return 工单新增周增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByWeek(QueryCondition<QueryHomeProcCountByWeekReq> req) {
        QueryHomeProcCountByWeekReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeWeekDefaultInfo());
        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByWeekStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultWeekProcCount(bizCondition.getTimeList());

        //获取权限信息
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //权限设施类型
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //权限区域编号集合
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //权限部门编号集合
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //时间条件
        List<Long> timeList = bizCondition.getTimeList();
        //工单类型
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //获取查询条件
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoWeekStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_week_statistical", ProcInfoWeekStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryWeekProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }

    /**
     * 查询工单新增月增量统计
     *
     * @param req 工单新增月增量统计
     * @return 工单新增月增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByMonth(QueryCondition<QueryHomeProcCountByMonthReq> req) {
        QueryHomeProcCountByMonthReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeMonthDefaultInfo());

        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByMonthStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultMonthProcCount(bizCondition.getTimeList());

        //获取权限信息
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //权限设施类型
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //权限区域编号集合
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //权限部门编号集合
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //时间条件
        List<Long> timeList = bizCondition.getTimeList();
        //工单类型
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //获取查询条件
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoMonthStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_month_statistical", ProcInfoMonthStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryMonthProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }

    /**
     * 查询工单新增年增量统计
     *
     * @param req 工单新增年增量统计
     * @return 工单新增年增量统计结果
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByYear(QueryCondition<QueryHomeProcCountByYearReq> req) {
        QueryHomeProcCountByYearReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeYearDefaultInfo());

        //获取工单新增增量统计返回默认参数
        List<QueryProcCountByYearStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultYearProcCount(bizCondition.getTimeList());

        //获取权限信息
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //权限设施类型
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //权限区域编号集合
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //权限部门编号集合
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //时间条件
        List<Long> timeList = bizCondition.getTimeList();
        //工单类型
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //获取查询条件
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //分组查询根据
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("nowDate").sum("orderCount").as("orderCount"),
                Aggregation.project().and("_id").as("nowDate").and("orderCount").as("orderCount").andExclude("_id")
        );
        AggregationResults<ProcInfoYearStatisticalGroup> results =
                this.mongoTemplate.aggregate(agg, "proc_info_year_statistical", ProcInfoYearStatisticalGroup.class);
        defaultQueryProcCount = this.getReturnQueryYearProcCount(results, defaultQueryProcCount);
        return ResultUtils.success(defaultQueryProcCount);
    }

    /**
     * 查询工单增量统计返回值
     *
     * @param results               查询工单增量表返回值
     * @param defaultQueryProcCount 默认工单增量数据
     * @return 查询工单增量统计返回值
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByTimeStatisticalVo> getReturnQueryProcCount(AggregationResults<ProcInfoDateStatisticalGroup> results, List<QueryProcCountByTimeStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //查询需要统计的增量统计数据
            List<ProcInfoDateStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByTimeStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByTimeStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByTimeStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByTimeStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //工单增量
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }


    /**
     * 查询工单增量周度统计返回值
     *
     * @param results               查询工单周增量度表返回值
     * @param defaultQueryProcCount 默认工单周增量度数据
     * @return 查询工单周增量返回值
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByWeekStatisticalVo> getReturnQueryWeekProcCount(AggregationResults<ProcInfoWeekStatisticalGroup> results, List<QueryProcCountByWeekStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //查询需要统计的增量统计数据
            List<ProcInfoWeekStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByWeekStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByWeekStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByWeekStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByWeekStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //工单增量
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }

    /**
     * 查询工单增量月度统计返回值
     *
     * @param results               查询工单增量月度表返回值
     * @param defaultQueryProcCount 默认工单增量月度数据
     * @return 查询工单增量月度返回值
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByMonthStatisticalVo> getReturnQueryMonthProcCount(AggregationResults<ProcInfoMonthStatisticalGroup> results, List<QueryProcCountByMonthStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //查询需要统计的增量统计数据
            List<ProcInfoMonthStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByMonthStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByMonthStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByMonthStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByMonthStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //工单增量
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }


    /**
     * 查询工单增量年度统计返回值
     *
     * @param results               查询工单增量年度表返回值
     * @param defaultQueryProcCount 默认工单增量年度数据
     * @return 查询工单增量年度返回值
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByYearStatisticalVo> getReturnQueryYearProcCount(AggregationResults<ProcInfoYearStatisticalGroup> results, List<QueryProcCountByYearStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //查询需要统计的增量统计数据
            List<ProcInfoYearStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByYearStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByYearStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByYearStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByYearStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //工单增量
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }

    /**
     * 设置每一天统计到工单数量统计表中
     *
     * @return 设施数量
     * @author hedongwei@wistronits.com
     */
    @Override
    public void setProcCountSumToProcAddCountTable() {
        int beforeDay = -1;
        Long beginDate = CastStatisticalUtil.getNowTimeAdd(beforeDay);
        Long beginTime = beginDate / 1000;

        //获取前一天结束的时间戳
        Long endDate = CastStatisticalUtil.getNowDayLastTimeAdd(beforeDay);
        Long endTime = endDate / 1000;

        QueryProcCountSumReq req = new QueryProcCountSumReq();
        List<Long> timeList = new ArrayList<>();
        timeList.add(beginTime);
        timeList.add(endTime);
        req.setTimeList(timeList);
        //巡检工单增量统计
        List<ProcInfoDateStatistical> inspectionDateInfoList = procStatisticalDao.queryProcInspectionListByNowDate(req);
        //销障工单增量统计
        List<ProcInfoDateStatistical> clearDateInfoList = procStatisticalDao.queryProcCLearListByNowDate(req);

        List<ProcInfoDateStatistical> nowDateInfoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(inspectionDateInfoList)) {
            nowDateInfoList.addAll(inspectionDateInfoList);
        }

        if (!ObjectUtils.isEmpty(clearDateInfoList)) {
            nowDateInfoList.addAll(clearDateInfoList);
        }

        if (!ObjectUtils.isEmpty(nowDateInfoList)) {
            for (ProcInfoDateStatistical nowDateInfoOne : nowDateInfoList) {
                nowDateInfoOne.setNowDate(beginDate);
            }
            mongoTemplate.insertAll(nowDateInfoList);
        }

        //删除十五天之前的数据
        int deleteDay = -15;
        Long deleteDate = CastStatisticalUtil.getNowTimeAdd(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoDateStatistical.class);
    }

    /**
     * 统计每周工单增量到周工单增量表中
     *
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:19
     */
    @Override
    public void setProcWeekCountSumToProcAddCountTable() {
        int beforeWeek = -1;
        Long beginDate = CastStatisticalUtil.getAdvanceNumberWeek(beforeWeek);
        Long beginTime = beginDate / 1000;
        //获取前一周结束的时间戳
        Long endDate = CastStatisticalUtil.getAdvanceLastNumberWeek(beforeWeek);
        Long endTime = endDate / 1000;

        List<ProcInfoDateStatistical> nowDateInfoList = this.getSearchTimeDateCountNum(beginTime, endTime);
        List<ProcInfoWeekStatistical> weekInfoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(nowDateInfoList)) {
            ProcInfoWeekStatistical procInfoWeekStatistical = null;
            for (ProcInfoDateStatistical nowDateInfoOne : nowDateInfoList) {
                nowDateInfoOne.setNowDate(beginDate);
                procInfoWeekStatistical = new ProcInfoWeekStatistical();
                BeanUtils.copyProperties(nowDateInfoOne, procInfoWeekStatistical);
                weekInfoList.add(procInfoWeekStatistical);
            }
            mongoTemplate.insertAll(weekInfoList);
        }

        //删除十五周之前的数据
        int deleteDay = -15;
        Long deleteDate = CastStatisticalUtil.getAdvanceLastNumberWeek(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoWeekStatistical.class);
    }

    /**
     * 统计每月工单增量到月工单增量表中
     *
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:19
     */
    @Override
    public void setProcMonthCountSumToProcAddCountTable() {
        int beforeMonth = -1;
        Long beginDate = CastStatisticalUtil.getAdvanceNumberMonth(beforeMonth);
        Long beginTime = beginDate / 1000;
        //获取前一月结束的时间戳
        Long endDate = CastStatisticalUtil.getAdvanceLastNumberMonth(beforeMonth);
        Long endTime = endDate / 1000;

        List<ProcInfoDateStatistical> nowDateInfoList = this.getSearchTimeDateCountNum(beginTime, endTime);
        List<ProcInfoMonthStatistical> monthInfoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(nowDateInfoList)) {
            ProcInfoMonthStatistical procInfoMonthStatistical = null;
            for (ProcInfoDateStatistical nowDateInfoOne : nowDateInfoList) {
                nowDateInfoOne.setNowDate(beginDate);
                procInfoMonthStatistical = new ProcInfoMonthStatistical();
                BeanUtils.copyProperties(nowDateInfoOne, procInfoMonthStatistical);
                monthInfoList.add(procInfoMonthStatistical);
            }
            mongoTemplate.insertAll(monthInfoList);
        }

        //删除十二月之前的数据
        int deleteDay = -12;
        Long deleteDate = CastStatisticalUtil.getAdvanceLastNumberMonth(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoMonthStatistical.class);
    }

    /**
     * 统计每年工单增量到年工单增量表中
     *
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:19
     */
    @Override
    public void setProcYearCountSumToProcAddCountTable() {
        int beforeYear = -1;
        Long beginDate = CastStatisticalUtil.getAdvanceNumberYear(beforeYear);
        Long beginTime = beginDate / 1000;
        //获取前一年结束的时间戳
        Long endDate = CastStatisticalUtil.getAdvanceLastNumberYear(beforeYear);
        Long endTime = endDate / 1000;

        List<ProcInfoDateStatistical> nowDateInfoList = this.getSearchTimeDateCountNum(beginTime, endTime);
        List<ProcInfoYearStatistical> yearInfoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(nowDateInfoList)) {
            ProcInfoYearStatistical procInfoYearStatistical = null;
            for (ProcInfoDateStatistical nowDateInfoOne : nowDateInfoList) {
                nowDateInfoOne.setNowDate(beginDate);
                procInfoYearStatistical = new ProcInfoYearStatistical();
                BeanUtils.copyProperties(nowDateInfoOne, procInfoYearStatistical);
                yearInfoList.add(procInfoYearStatistical);
            }
            mongoTemplate.insertAll(yearInfoList);
        }

        //删除十二年之前的数据
        int deleteDay = -12;
        Long deleteDate = CastStatisticalUtil.getAdvanceLastNumberYear(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoYearStatistical.class);
    }

    /**
     * 销障工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单状态导出结果
     */
    @Override
    public Result procClearStatusStatisticalExport(ExportDto<ProcStatusStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procStatusStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_STATUS_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_STATUS_EXPORT_CODE);
    }

    /**
     * 销障工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单设施类型导出结果
     */
    @Override
    public Result procClearDeviceTypeStatisticalExport(ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procDeviceTypeStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_DEVICE_TYPE_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_DEVICE_TYPE_EXPORT_CODE);
    }

    /**
     * 销障工单处理方案导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单处理方案导出结果
     */
    @Override
    public Result procClearProcessingSchemeStatisticalExport(ExportDto<ProcProcessingSchemeStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procProcessingSchemeStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_PROCESSING_SCHEME_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_PROCESSING_SCHEME_EXPORT_CODE);
    }

    /**
     * 销障工单故障原因导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单故障原因导出结果
     */
    @Override
    public Result procClearErrorReasonStatisticalExport(ExportDto<ProcErrorReasonStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procErrorReasonStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_ERROR_REASON_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_ERROR_REASON_EXPORT_CODE);
    }

    /**
     * 销障工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单区域比导出结果
     */
    @Override
    public Result procClearAreaPercentStatisticalExport(ExportDto<ProcAreaPercentStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procAreaPercentStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_AREA_PERCENT_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_AREA_PERCENT_EXPORT_CODE);
    }

    /**
     * 巡检工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 巡检工单状态导出结果
     */
    @Override
    public Result procInspectionStatusStatisticalExport(ExportDto<ProcStatusStatisticalExportBean> exportDto) {
        return this.exportInfo(procStatusStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_STATUS_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_STATUS_EXPORT_CODE);
    }

    /**
     * 巡检工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 巡检工单设施类型导出结果
     */
    @Override
    public Result procInspectionDeviceTypeStatisticalExport(ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procDeviceTypeStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_DEVICE_TYPE_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_DEVICE_TYPE_EXPORT_CODE);
    }

    /**
     * 巡检工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 巡检工单区域比导出结果
     */
    @Override
    public Result procInspectionAreaPercentStatisticalExport(ExportDto<ProcAreaPercentStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procAreaPercentStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_AREA_PERCENT_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_AREA_PERCENT_EXPORT_CODE);
    }

    /**
     * 销障工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto 导出参数
     * @return 返回导出的值
     */
    @Override
    public Result procClearTopListStatisticalExport(ExportDto<ProcTopListStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procTopListStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_COUNT_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_COUNT_EXPORT_CODE);
    }

    /**
     * 巡检工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto 导出参数
     * @return 返回导出的值
     */
    @Override
    public Result procInspectionTopListStatisticalExport(ExportDto<ProcTopListStatisticalExportBean> exportDto) {
        //导出数据
        return this.exportInfo(procTopListStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_COUNT_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_COUNT_EXPORT_CODE);
    }

    /**
     * 导出信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 18:51
     * @param abstractExport 抽象导出类
     * @param exportDto 导出dto
     * @param tableName 列表名称
     * @param functionCode 功能项
     * @return 导出信息
     */
    public <T extends AbstractExport> Result exportInfo(T abstractExport , ExportDto exportDto, String tableName, String functionCode) {
        ExportRequestInfo exportRequestInfo = null;
        try {
            exportRequestInfo = abstractExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getSystemString(tableName));
        } catch (FilinkExportNoDataException fe) {
            log.error("export proc no data exception", fe);
            return ResultUtils.warn(WorkflowBusinessResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(WorkflowBusinessI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return WorkFlowBusinessMsg.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export task num to big exception", fe);
            return ResultUtils.warn(WorkflowBusinessResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(ProcBaseI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export data exception", e);
            return ResultUtils.warn(WorkflowBusinessResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        abstractExport.exportData(exportRequestInfo);

        //新增日志
        procLogService.addLogByExport(exportDto, functionCode);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 获取查询数据信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 15:48
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 返回查询数据
     *
     */
    public List<ProcInfoDateStatistical> getSearchTimeDateCountNum(Long beginTime, Long endTime) {
        QueryProcCountSumReq req = new QueryProcCountSumReq();
        List<Long> timeList = new ArrayList<>();
        timeList.add(beginTime);
        timeList.add(endTime);
        req.setTimeList(timeList);
        //巡检工单增量统计
        List<ProcInfoDateStatistical> inspectionDateInfoList = procStatisticalDao.queryProcInspectionListByNowDate(req);
        //销障工单增量统计
        List<ProcInfoDateStatistical> clearDateInfoList = procStatisticalDao.queryProcCLearListByNowDate(req);

        List<ProcInfoDateStatistical> nowDateInfoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(inspectionDateInfoList)) {
            nowDateInfoList.addAll(inspectionDateInfoList);
        }

        if (!ObjectUtils.isEmpty(clearDateInfoList)) {
            nowDateInfoList.addAll(clearDateInfoList);
        }
        return nowDateInfoList;
    }

    /**
     * 获取权限条件信息
     *
     * @return 权限信息
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 14:29
     */
    public ProcBaseReq getPermissionCondition() {
        QueryCondition<ProcBaseReq> procBaseReqCondition = new QueryCondition<ProcBaseReq>();
        procBaseReqCondition.setBizCondition(new ProcBaseReq());
        //根据设施类型 和 区域查询工单概览信息
        ProcBaseReq procBaseReq = (ProcBaseReq) procBaseService.getPermissionsInfo(procBaseReqCondition).getBizCondition();
        return procBaseReq;
    }

    /**
     * 获取默认的统计map
     *
     * @param areaIdList 区域编号
     * @param dataList   统计的列
     * @return 获取默认的统计map
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 20:28
     */
    public Map<String, Map<String, Object>> getDefaultStatisticalMap(List<String> areaIdList, List<String> dataList) {
        Map<String, Map<String, Object>> statisticalMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        for (String areaIdOne : areaIdList) {
            Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            mapOne.put("areaId", areaIdOne);
            for (String data : dataList) {
                mapOne.put(data, 0);
            }
            statisticalMap.put(areaIdOne, mapOne);
        }
        return statisticalMap;
    }

    /**
     * 获取默认的统计map
     *
     * @param areaIdList 区域编号
     * @return 获取默认的统计map
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 20:28
     */
    public Map<String, Map<String, Object>> getDefaultStatisticalMap(List<String> areaIdList) {
        Map<String, Map<String, Object>> statisticalMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        for (String areaIdOne : areaIdList) {
            Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            mapOne.put("areaId", areaIdOne);
            mapOne.put(areaIdOne, 0);
            statisticalMap.put(areaIdOne, mapOne);
        }
        return statisticalMap;
    }

    /**
     * 获得部门map
     *
     * @param voList 部门map
     * @return 部门map
     * @author hedongwei@wistronits.com
     * @date 2019/6/3 16:45
     */
    public Map<String, ProcDepartmentStatisticalVo> getDepartmentStatisticalVoMap(List<ProcDepartmentStatisticalVo> voList) {
        Map<String, ProcDepartmentStatisticalVo> voMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(voList)) {
            for (ProcDepartmentStatisticalVo voOne : voList) {
                voMap.put(voOne.getDepartmentId(), voOne);
            }
        }
        return voMap;
    }

    /**
     * 获得默认的部门统计集合
     *
     * @param deptList 部门集合
     * @param deptMap  部门map
     * @return 获得默认的部门统计集合
     * @author hedongwei@wistronits.com
     * @date 2019/6/3 16:56
     */
    public List<ProcDepartmentStatisticalVo> getDefaultDeptStatisticalList(List<String> deptList, Map<String, String> deptMap) {
        List<ProcDepartmentStatisticalVo> voList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(deptList)) {
            ProcDepartmentStatisticalVo statisticalVo = null;
            for (String deptId : deptList) {
                statisticalVo = new ProcDepartmentStatisticalVo();
                statisticalVo.setDepartmentId(deptId);
                if (!ObjectUtils.isEmpty(deptMap)) {
                    if (deptMap.containsKey(deptId)) {
                        statisticalVo.setDepartmentName(deptMap.get(deptId));
                    }
                }
                //初始值
                statisticalVo.setDepartmentCount(0);
                voList.add(statisticalVo);
            }
        }
        return voList;
    }
}
