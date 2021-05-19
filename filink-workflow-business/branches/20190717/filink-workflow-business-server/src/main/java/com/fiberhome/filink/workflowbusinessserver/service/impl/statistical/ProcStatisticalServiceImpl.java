package com.fiberhome.filink.workflowbusinessserver.service.impl.statistical;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.export.*;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.*;
import com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness.WorkflowBusinessI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcStatisticalConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
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
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkflowBusinessResultCode;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcDeviceTypeOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcErrorReasonOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcProcessingSchemeOverviewStatisticalVo;
import com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview.ProcStatusOverviewStatisticalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
            errorReasonInfoList = procStatisticalDao.queryListProcInspectionGroupByErrorReason(req);;
        } else {
            //销障工单
            //查询故障原因统计数据
            errorReasonInfoList = procStatisticalDao.queryListProcClearGroupByErrorReason(req);;
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
            return ResultUtils.warn(ProcBaseResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(ProcBaseI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export data exception", e);
            return ResultUtils.warn(ProcBaseResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_CREATE_EXPORT_TASK));
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
     * 新增测试数据
     *
     * @param deviceInfoDto          新增设施信息
     * @param procRelatedDepartments 工单关联部门信息
     * @param procParamBase          工单附带参数
     * @author hedongwei@wistronits.com
     * @date 2019/6/15 10:35
     */
    @Autowired
    private Test test;

    @Override
    public void addProcInspectionInfo() {
        List<ProcClearFailure> procClearFailureList = new ArrayList<>();
        int row = 100000;
        List<ProcBase> procBaseList = new ArrayList<>();
        List<ProcInspectionRecord> procInspectionRecordList = new ArrayList<>();
        ProcInspectionRecord procInspectionRecord = null;
        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_001);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_030);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_060);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_090);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_210);

        ProcBase procBase = null;
        List<ProcRelatedDevice> addDeviceInfo = new ArrayList<>();
        List<ProcRelatedDepartment> addDepartment = new ArrayList<>();
        List<ProcInspectionRecord> addInspectionRecordInfo = new ArrayList<>();
        List<ProcInspection> procInspectionList = new ArrayList<>();
        ProcInspection procInspection = null;
        ProcClearFailure procClearFailure = null;


        ProcBase procBaseParam = new ProcBase();
        procBaseParam.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);

        for (int i = 95000 ; i < row ; i++) {
            procBase = new ProcBase();
            /*procBase.setProcId(NineteenUUIDUtils.uuid());
            String procType = "";
            procType = ProcBaseConstants.PROC_CLEAR_FAILURE;*/
            procBase.setProcId(i + "");
            String procType = "";
            procType = ProcBaseConstants.PROC_INSPECTION;

            procBase.setProcType(procType);

            String statusName = "";
            if (i % 6 == 0) {
                statusName = ProcStatisticalConstants.statusList.get(0);
            } else if (i % 6 == 1) {
                statusName = ProcStatisticalConstants.statusList.get(1);
            } else if (i % 6 == 2) {
                statusName = ProcStatisticalConstants.statusList.get(2);
            } else if (i % 6 == 3) {
                statusName = ProcStatisticalConstants.statusList.get(3);
            } else if (i % 6 == 4) {
                statusName = ProcStatisticalConstants.statusList.get(4);
            } else if (i % 6 == 5) {
                statusName = ProcStatisticalConstants.statusList.get(5);
            }
            procBase.setStatus(statusName);
            procBase.setExpectedCompletedTime(new Date());
            procBase.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_1);
            String errorReason = "";
            if (i % 5 == 0) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(0);
            } else if (i % 5 == 1) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(1);
            } else if (i % 5 == 2) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(2);
            } else if (i % 5 == 3) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(3);
            } else if (i % 5 == 4) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(4);
            }
            procBase.setErrorReason(errorReason);

            String processingScheme = "";
            if (i % 3 == 0) {
                processingScheme = ProcStatisticalConstants.processingSchemeList.get(0);
            } else if (i % 3 == 1) {
                processingScheme = ProcStatisticalConstants.processingSchemeList.get(1);
            } else if (i % 3 == 2) {
                processingScheme = ProcStatisticalConstants.processingSchemeList.get(2);
            }
            procBase.setProcessingScheme(processingScheme);

            String areaName = "";
            String areaId = "";
            String deviceType = "";
            if (i % 5 == 0) {
                deviceType = deviceTypeList.get(0);
                areaName = "工单人井区域";
                areaId = "thapZ2AMzl7wqdXjei9";
            } else if (i % 5 == 1) {
                deviceType = deviceTypeList.get(1);
                areaName = "工单光交箱区域";
                areaId = "wdRHbr6VwS0i5d2Iizm";
            } else if (i % 5 == 2) {
                deviceType = deviceTypeList.get(2);
                areaName = "工单配线架区域";
                areaId = "BFguCBkT2QpcOnDef0L";
            } else if (i % 5 == 3) {
                deviceType = deviceTypeList.get(3);
                areaName = "工单接头盒区域";
                areaId = "vhybjRi2wiNl5W0PX6o";
            } else if (i % 5 == 4) {
                deviceType = deviceTypeList.get(4);
                areaName = "工单室外柜区域";
                areaId = "EkEr8CIrxHNzPkEcvDV";
            }
            procBase.setDeviceType(deviceType);

            if (i % 2 == 0) {
                procBase.setSingleBackReason("1");
            } else if (i % 2 == 1) {
                procBase.setSingleBackReason("0");
                procBase.setSingleBackUserDefinedReason("testInfo");
            }
            procBase.setTitle("工单性能测试数据" + i);
            procBase.setDeviceAreaId(areaId);
            procBase.setDeviceAreaName(areaName);
            procBaseList.add(procBase);
        }

        for (ProcBase  procBaseOne : procBaseList) {
            ProcInspection procInspectionOne = new ProcInspection();
            BeanUtils.copyProperties(procBaseOne, procInspectionOne);
            procInspectionList.add(procInspectionOne);
        }


        for (ProcBase procBaseOne : procBaseList) {
            for (int j = 0 ; j < 100 ; j ++) {
                ProcRelatedDevice procRelatedDeviceInfo = new ProcRelatedDevice();
                procRelatedDeviceInfo.setProcRelatedDeviceId(NineteenUUIDUtils.uuid());
                procRelatedDeviceInfo.setProcId(procBaseOne.getProcId());
                procRelatedDeviceInfo.setIsDeleted("0");
                Random r = new Random();
                int a = r.nextInt(300000) + 1;
                procRelatedDeviceInfo.setDeviceId(a + "");
                addDeviceInfo.add(procRelatedDeviceInfo);
            }
        }



        List<ProcInspection> procInspectionList2 = new ArrayList<>();
        for (int i = 0; i < procInspectionList.size(); i++) {
            procInspectionList2.add(procInspectionList.get(i));
            if (procInspectionList2.size() > 10000 || i+1==  procInspectionList.size()) {
                test.test3(procInspectionList2);
                procInspectionList2 = new ArrayList<>();
            }
        }

        List<ProcRelatedDevice> addDeviceInfo2 = new ArrayList<>();
        for (int i = 0; i < addDeviceInfo.size(); i++) {
            if (i + 1 <= 500000) {
                addDeviceInfo2.add(addDeviceInfo.get(i));
            }
            if (addDeviceInfo2.size() >= 10000 || i+1==addDeviceInfo.size()) {
                if (i + 1 <= 500000) {
                    test.test1(addDeviceInfo2);
                    addDeviceInfo2 = new ArrayList<>();
                }
            }
        }
    }


    public void addInsertTest(List<ProcRelatedDevice> deviceInfoDto, List<ProcRelatedDepartment> procRelatedDepartments, ProcBase procParamBase) {
        int row = 2035000;
        List<ProcBase> procBaseList = new ArrayList<>();
        List<ProcInspectionRecord> procInspectionRecordList = new ArrayList<>();
        ProcInspectionRecord procInspectionRecord = null;
        for (ProcRelatedDevice deviceOne : deviceInfoDto) {
            procInspectionRecord = new ProcInspectionRecord();
            BeanUtils.copyProperties(deviceOne, procInspectionRecord);
            procInspectionRecordList.add(procInspectionRecord);
        }

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_001);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_030);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_060);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_090);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_210);

        ProcBase procBase = null;
        List<ProcRelatedDevice> addDeviceInfo = new ArrayList<>();
        List<ProcRelatedDepartment> addDepartment = new ArrayList<>();
        List<ProcInspectionRecord> addInspectionRecordInfo = new ArrayList<>();
        List<ProcInspection> procInspectionList = new ArrayList<>();
        List<ProcClearFailure> procClearFailureList = new ArrayList<>();
        ProcInspection procInspection = null;
        ProcClearFailure procClearFailure = null;


        ProcBase procBaseParam = new ProcBase();
        procBaseParam.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);

        for (int i = 1835000 ; i < row ; i++) {
            procBase = new ProcBase();
            procBase.setProcId(NineteenUUIDUtils.uuid());
            String procType = "";
            procType = ProcBaseConstants.PROC_CLEAR_FAILURE;
            /*procBase.setProcId(i);
            String procType = "";
            procType = ProcBaseConstants.PROC_INSPECTION;*/

            procBase.setProcType(procType);

            String statusName = "";
            if (i % 6 == 0) {
                statusName = ProcStatisticalConstants.statusList.get(0);
            } else if (i % 6 == 1) {
                statusName = ProcStatisticalConstants.statusList.get(1);
            } else if (i % 6 == 2) {
                statusName = ProcStatisticalConstants.statusList.get(2);
            } else if (i % 6 == 3) {
                statusName = ProcStatisticalConstants.statusList.get(3);
            } else if (i % 6 == 4) {
                statusName = ProcStatisticalConstants.statusList.get(4);
            } else if (i % 6 == 5) {
                statusName = ProcStatisticalConstants.statusList.get(5);
            }
            procBase.setStatus(statusName);
            procBase.setExpectedCompletedTime(new Date());
            procBase.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_1);
            String errorReason = "";
            if (i % 5 == 0) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(0);
            } else if (i % 5 == 1) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(1);
            } else if (i % 5 == 2) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(2);
            } else if (i % 5 == 3) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(3);
            } else if (i % 5 == 4) {
                errorReason = ProcStatisticalConstants.errorReasonList.get(4);
            }
            procBase.setErrorReason(errorReason);

            String processingScheme = "";
            if (i % 3 == 0) {
                processingScheme = ProcStatisticalConstants.processingSchemeList.get(0);
            } else if (i % 3 == 1) {
                processingScheme = ProcStatisticalConstants.processingSchemeList.get(1);
            } else if (i % 3 == 2) {
                processingScheme = ProcStatisticalConstants.processingSchemeList.get(2);
            }
            procBase.setProcessingScheme(processingScheme);

            if (i % 2 == 0) {
                procBase.setSingleBackReason("1");
            } else if (i % 2 == 1) {
                procBase.setSingleBackReason("0");
                procBase.setSingleBackUserDefinedReason("testInfo");
            }
            procBase.setTitle("工单性能测试数据" + i);


            procBase.setRefAlarm(procParamBase.getRefAlarm());
            procBase.setRefAlarmCode(procParamBase.getRefAlarmCode());
            procBase.setRefAlarmName(procParamBase.getRefAlarmName());
            procBaseList.add(procBase);

        }

        List<ProcBase> procBaseInfoList = procBaseList;

        addDeviceInfo = new ArrayList<>();
        addDepartment = new ArrayList<>();
        addInspectionRecordInfo = new ArrayList<>();
        procInspectionList = new ArrayList<>();
        for (int i = 0; i < procBaseInfoList.size(); i++) {
            ProcBase procBaseInfo = procBaseInfoList.get(i);
            String deviceType = "";
            if (i % 5 == 0) {
                deviceType = deviceTypeList.get(0);
            } else if (i % 5 == 1) {
                deviceType = deviceTypeList.get(1);
            } else if (i % 5 == 2) {
                deviceType = deviceTypeList.get(2);
            } else if (i % 5 == 3) {
                deviceType = deviceTypeList.get(3);
            } else if (i % 5 == 4) {
                deviceType = deviceTypeList.get(4);
            }
            ProcRelatedDevice clearDevice = new ProcRelatedDevice();
            ProcRelatedDevice inspectionDevice = new ProcRelatedDevice();
            if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseInfoList.get(i).getProcType())) {
                for (ProcRelatedDevice procRelatedDeviceOne : deviceInfoDto) {
                    if (deviceType.equals(procRelatedDeviceOne.getDeviceType())) {
                        ProcRelatedDevice procRelatedDeviceInfo = new ProcRelatedDevice();
                        BeanUtils.copyProperties(procRelatedDeviceOne, procRelatedDeviceInfo);
                        procRelatedDeviceInfo.setProcRelatedDeviceId(NineteenUUIDUtils.uuid());
                        procRelatedDeviceInfo.setProcId(procBaseInfo.getProcId());
                        addDeviceInfo.add(procRelatedDeviceInfo);
                        inspectionDevice = procRelatedDeviceInfo;
                    }
                }
            } else {
                for (ProcRelatedDevice procRelatedDeviceOne : deviceInfoDto) {
                    if (deviceType.equals(procRelatedDeviceOne.getDeviceType())) {
                        ProcRelatedDevice procRelatedDeviceInfo = new ProcRelatedDevice();
                        BeanUtils.copyProperties(procRelatedDeviceOne, procRelatedDeviceInfo);
                        procRelatedDeviceInfo.setProcRelatedDeviceId(NineteenUUIDUtils.uuid());
                        procRelatedDeviceInfo.setProcId(procBaseInfo.getProcId());
                        BeanUtils.copyProperties(procRelatedDeviceOne, clearDevice);
                    }
                }
            }


            ProcRelatedDepartment departmentInfo = new ProcRelatedDepartment();
            if (i % 5 == 0) {
                departmentInfo = procRelatedDepartments.get(0);
            } else if (i % 5 == 1) {
                departmentInfo = procRelatedDepartments.get(1);
            } else if (i % 5 == 2) {
                departmentInfo = procRelatedDepartments.get(2);
            } else if (i % 5 == 3) {
                departmentInfo = procRelatedDepartments.get(3);
            } else if (i % 5 == 4) {
                departmentInfo = procRelatedDepartments.get(4);
            }

            ProcInspectionRecord recordNewInfo = null;
            if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseInfo.getProcType())) {
                for (ProcInspectionRecord inspectionRecord : procInspectionRecordList) {
                    if (deviceType.equals(inspectionRecord.getDeviceType())) {
                        recordNewInfo = new ProcInspectionRecord();
                        BeanUtils.copyProperties(inspectionRecord, recordNewInfo);
                        recordNewInfo.setProcInspectionRecordId(NineteenUUIDUtils.uuid());
                        recordNewInfo.setProcId(procBaseInfo.getProcId());
                        addInspectionRecordInfo.add(recordNewInfo);
                    }
                }

                procInspection = new ProcInspection();
                BeanUtils.copyProperties(procBaseInfo, procInspection);
                procInspection.setInspectionCompletedCount(addInspectionRecordInfo.size());
                procInspection.setProcId(procBaseInfo.getProcId());
                procInspection.setInspectionStartTime(new Date());
                procInspection.setInspectionEndTime(new Date());
                procInspection.setDeviceAreaId(inspectionDevice.getDeviceAreaId());
                procInspection.setDeviceAreaName(inspectionDevice.getDeviceAreaName());
                procInspection.setDeviceType(inspectionDevice.getDeviceType());
                ProcRelatedDepartment department = new ProcRelatedDepartment();
                BeanUtils.copyProperties(departmentInfo, department);
                //关联部门
                procInspection.setAccountabilityDept(department.getAccountabilityDept());
                procInspectionList.add(procInspection);
            } else {
                procClearFailure = new ProcClearFailure();
                BeanUtils.copyProperties(procBaseInfo, procClearFailure);
                if (ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(procClearFailure.getStatus())) {
                    procClearFailure.setIsCheckSingleBack(ProcBaseConstants.IS_CHECK_SINGLE_BACK);
                }
                //关联设施
                procClearFailure.setDeviceAreaId(clearDevice.getDeviceAreaId());
                procClearFailure.setDeviceAreaName(clearDevice.getDeviceAreaName());
                procClearFailure.setDeviceType(clearDevice.getDeviceType());
                procClearFailure.setDeviceId(clearDevice.getDeviceId());
                procClearFailure.setDeviceName(clearDevice.getDeviceName());
                procClearFailureList.add(procClearFailure);
                List<ProcRelatedDepartment> procClearRelatedDepartments = new ArrayList<>();
                if (i % 5 == 0) {
                    procClearRelatedDepartments.add(procRelatedDepartments.get(0));
                    procClearRelatedDepartments.add(procRelatedDepartments.get(1));
                } else if (i % 5 == 1) {
                    procClearRelatedDepartments.add(procRelatedDepartments.get(1));
                    procClearRelatedDepartments.add(procRelatedDepartments.get(2));
                } else if (i % 5 == 2) {
                    procClearRelatedDepartments.add(procRelatedDepartments.get(2));
                    procClearRelatedDepartments.add(procRelatedDepartments.get(3));
                } else if (i % 5 == 3) {
                    procClearRelatedDepartments.add(procRelatedDepartments.get(3));
                    procClearRelatedDepartments.add(procRelatedDepartments.get(4));
                } else if (i % 5 == 4) {
                    procClearRelatedDepartments.add(procRelatedDepartments.get(4));
                    procClearRelatedDepartments.add(procRelatedDepartments.get(0));
                }
                for (ProcRelatedDepartment department : procClearRelatedDepartments) {
                    ProcRelatedDepartment departmentOne = new ProcRelatedDepartment();
                    BeanUtils.copyProperties(department, departmentOne);
                    departmentOne.setProcRelatedDeptId(NineteenUUIDUtils.uuid());
                    departmentOne.setProcId(procBaseInfo.getProcId());
                    addDepartment.add(departmentOne);
                }
            }
        }
        //批量新增关联设施
        /*List<ProcRelatedDevice> addDeviceInfo2 = new ArrayList<>();
        for (int i = 0; i < addDeviceInfo.size(); i++) {
            if (i + 1 > 750000) {
                addDeviceInfo2.add(addDeviceInfo.get(i));
            }
            if (addDeviceInfo2.size() >= 10000 || i+1==addDeviceInfo.size()) {
                if (i + 1 > 750000) {
                    test.test1(addDeviceInfo2);
                    addDeviceInfo2 = new ArrayList<>();
                }
            }
        }*/

        //关联部门
        /*List<ProcRelatedDepartment> addDepartment2 = new ArrayList<>();
        for (int i = 0; i < addDepartment.size(); i++) {
            addDepartment2.add(addDepartment.get(i));
            if (addDepartment2.size() > 10000 || i+1== addDepartment.size()) {
                test.test2(addDepartment2);
                addDepartment2 = new ArrayList<>();
            }
        }*/

        //销障工单
        List<ProcClearFailure> procClearFailures = new ArrayList<>();
        for (int i = 0; i < procClearFailureList.size(); i++ ) {
            if (i + 1 <= 750000) {
                procClearFailures.add(procClearFailureList.get(i));
            }

            if (procClearFailures.size() >= 5000 || i+1== procClearFailureList.size()) {
                if (i + 1 <= 750000) {
                    try {
                        Thread.sleep(1000L);
                    } catch (Exception e) {

                    }
                    test.test5(procClearFailures);
                    procClearFailures = new ArrayList<>();
                }
            }
        }

        //巡检工单
        /*List<ProcInspection> procInspectionList2 = new ArrayList<>();
        for (int i = 0; i < procInspectionList.size(); i++) {
            procInspectionList2.add(procInspectionList.get(i));
            if (procInspectionList2.size() > 10000 || i+1==  procInspectionList.size()) {
                test.test3(procInspectionList2);
                procInspectionList2 = new ArrayList<>();
            }
        }*/

        //巡检任务记录
        /*List<ProcInspectionRecord> addInspectionRecordInfo2 = new ArrayList<>();
        for (int i = 0; i <addInspectionRecordInfo.size(); i++) {
            if (i + 1 > 750000) {
                addInspectionRecordInfo2.add(addInspectionRecordInfo.get(i));
            }
            if (addInspectionRecordInfo2.size() >= 10000 || i+1==  addInspectionRecordInfo.size()) {
                if (i + 1 > 750000) {
                    test.test4(addInspectionRecordInfo2);
                    addInspectionRecordInfo2 = new ArrayList<>();
                }
            }
        }*/
    }

    /**
     * 测试新增数据信息(本地新增数据)
     */
    @Override
    public void insertTest() {
        List<ProcRelatedDevice> deviceInfoDto = new ArrayList<>();
        ProcRelatedDevice deviceInfoOne = new ProcRelatedDevice();
        deviceInfoOne.setDeviceId("0At8fvtc1kWZQNkhSaA");
        deviceInfoOne.setDeviceName("工单测试设施1");
        deviceInfoOne.setDeviceType("030");
        deviceInfoOne.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoOne.setDeviceAreaName("工单测试区域(勿删除)11");
        deviceInfoDto.add(deviceInfoOne);

        ProcRelatedDevice deviceInfoTwo = new ProcRelatedDevice();
        deviceInfoTwo.setDeviceId("0At8fvtc1kWZQNkhSaB");
        deviceInfoTwo.setDeviceName("工单测试设施2");
        deviceInfoTwo.setDeviceType("001");
        deviceInfoTwo.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoTwo.setDeviceAreaName("工单测试区域(勿删除)11");
        deviceInfoDto.add(deviceInfoTwo);

        ProcRelatedDevice deviceInfoThree = new ProcRelatedDevice();
        deviceInfoThree.setDeviceId("0At8fvtc1kWZQNkhSaD");
        deviceInfoThree.setDeviceName("工单测试设施4");
        deviceInfoThree.setDeviceType("060");
        deviceInfoThree.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoThree.setDeviceAreaName("工单测试区域(勿删除)11");
        deviceInfoDto.add(deviceInfoThree);

        ProcRelatedDevice deviceInfoFour = new ProcRelatedDevice();
        deviceInfoFour.setDeviceId("0At8fvtc1kWZQNkhSaE");
        deviceInfoFour.setDeviceName("工单测试设施5");
        deviceInfoFour.setDeviceType("030");
        deviceInfoFour.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoFour.setDeviceAreaName("工单测试区域(勿删除)11");
        deviceInfoDto.add(deviceInfoFour);

        ProcRelatedDevice deviceInfoFive = new ProcRelatedDevice();
        deviceInfoFive.setDeviceId("0fdTi6epUxytPwWnPk6");
        deviceInfoFive.setDeviceName("Leyvi_xia02(工单测试勿删)");
        deviceInfoFive.setDeviceType("030");
        deviceInfoFive.setDeviceAreaId("d6TOcr9n9Cdk579Hlgr");
        deviceInfoFive.setDeviceAreaName("Leyvi_xia(勿动)");
        deviceInfoDto.add(deviceInfoFive);

        ProcRelatedDevice deviceInfoSix = new ProcRelatedDevice();
        deviceInfoSix.setDeviceId("1B7OYZV4vA3KrwRQK22");
        deviceInfoSix.setDeviceName("dduduuduu");
        deviceInfoSix.setDeviceType("001");
        deviceInfoSix.setDeviceAreaId("p4RyBu9WdGo2x4zC5sQ");
        deviceInfoSix.setDeviceAreaName("创业街67号");
        deviceInfoDto.add(deviceInfoSix);

        ProcRelatedDevice deviceInfoSeven = new ProcRelatedDevice();
        deviceInfoSeven.setDeviceId("1rvnbPGqn81mFQgVBJA");
        deviceInfoSeven.setDeviceName("Leyvi_xia020(图片测试勿删)");
        deviceInfoSeven.setDeviceType("030");
        deviceInfoSeven.setDeviceAreaId("AVBnwYfRrzHlE04jLhl");
        deviceInfoSeven.setDeviceAreaName("Leyvi_001");
        deviceInfoDto.add(deviceInfoSeven);

        ProcRelatedDevice deviceInfoEight = new ProcRelatedDevice();
        deviceInfoEight.setDeviceId("2WJKjEC7PP1eUAfyh0Q");
        deviceInfoEight.setDeviceName("001");
        deviceInfoEight.setDeviceType("设施123456");
        deviceInfoEight.setDeviceAreaId("ZTXdWeJZMQmzxZJaVFE");
        deviceInfoEight.setDeviceAreaName("zhuting区域01");
        deviceInfoDto.add(deviceInfoEight);

        ProcRelatedDevice deviceInfoNine = new ProcRelatedDevice();
        deviceInfoNine.setDeviceId("5htjTOkYz48zLnugyJ7");
        deviceInfoNine.setDeviceName("测试用户0003");
        deviceInfoNine.setDeviceType("090");
        deviceInfoNine.setDeviceAreaId("etzw2Wp0GnCKa2hP67I");
        deviceInfoNine.setDeviceAreaName("新增区域测试");
        deviceInfoDto.add(deviceInfoNine);

        ProcRelatedDevice deviceInfoTen = new ProcRelatedDevice();
        deviceInfoTen.setDeviceId("5jLMbUeKyMEeHoDHPsm");
        deviceInfoTen.setDeviceName("12313");
        deviceInfoTen.setDeviceType("001");
        deviceInfoTen.setDeviceAreaId("Z6MsT3WXhhsCJwgeiHL");
        deviceInfoTen.setDeviceAreaName("多鹤测试区域");
        deviceInfoDto.add(deviceInfoTen);


        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_001);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_030);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_060);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_090);

        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
        ProcRelatedDepartment procRelatedDepartment1 = new ProcRelatedDepartment();
        procRelatedDepartment1.setAccountabilityDept("02430396038e4ef1b918a453301dc08b");
        procRelatedDepartments.add(procRelatedDepartment1);

        ProcRelatedDepartment procRelatedDepartment2 = new ProcRelatedDepartment();
        procRelatedDepartment2.setAccountabilityDept("0b30c4bf4aaf46ef97f7a7287dbf33ca");
        procRelatedDepartments.add(procRelatedDepartment2);

        ProcRelatedDepartment procRelatedDepartment3 = new ProcRelatedDepartment();
        procRelatedDepartment3.setAccountabilityDept("0f822128220541fe8a76ffcbd8c93345");
        procRelatedDepartments.add(procRelatedDepartment3);

        ProcRelatedDepartment procRelatedDepartment4 = new ProcRelatedDepartment();
        procRelatedDepartment4.setAccountabilityDept("137dc516b2e94a9c952726c59fd2334v");
        procRelatedDepartments.add(procRelatedDepartment4);

        ProcRelatedDepartment procRelatedDepartment5 = new ProcRelatedDepartment();
        procRelatedDepartment5.setAccountabilityDept("137dc516b2e94a9c952726c59fd2337b");
        procRelatedDepartments.add(procRelatedDepartment5);
        ProcBase procBase = new ProcBase();
        procBase.setRefAlarm("T2dxtqCgpE0S389YBgY");
        procBase.setRefAlarmName("电量");
        procBase.setRefAlarmCode("012");
        this.addInsertTest(deviceInfoDto, procRelatedDepartments, procBase);
    }

    /**
     * 客户环境数据
     */
    @Override
    public void insertTestCustomData() {
        List<ProcRelatedDevice> deviceInfoDto = new ArrayList<>();
        ProcRelatedDevice deviceInfoOne = new ProcRelatedDevice();
        deviceInfoOne.setDeviceId("2hDnqiMkPeAnEDhRvYS");
        deviceInfoOne.setDeviceName("工单光交箱设施4");
        deviceInfoOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoOne.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoOne.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoOne);

        ProcRelatedDevice deviceInfoTwo = new ProcRelatedDevice();
        deviceInfoTwo.setDeviceId("3wKmehzBl9CW9kndqPN");
        deviceInfoTwo.setDeviceName("工单光交箱设施3");
        deviceInfoTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoTwo.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoTwo.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoTwo);

        ProcRelatedDevice deviceInfoThree = new ProcRelatedDevice();
        deviceInfoThree.setDeviceId("dc09hss0e2yxAgbVgFl");
        deviceInfoThree.setDeviceName("工单光交箱设施2");
        deviceInfoThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoThree.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoThree.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoThree);

        ProcRelatedDevice deviceInfoFour = new ProcRelatedDevice();
        deviceInfoFour.setDeviceId("EUteMXhcU2L0762gxTr");
        deviceInfoFour.setDeviceName("工单光交箱设施9");
        deviceInfoFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoFour.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoFour.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoFour);

        ProcRelatedDevice deviceInfoFive = new ProcRelatedDevice();
        deviceInfoFive.setDeviceId("GXfreV7zRF2rnPTOszy");
        deviceInfoFive.setDeviceName("工单光交箱设施6");
        deviceInfoFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoFive.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoFive.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoFive);

        ProcRelatedDevice deviceInfoSix = new ProcRelatedDevice();
        deviceInfoSix.setDeviceId("MOjUBCKMOSDQjMuelW8");
        deviceInfoSix.setDeviceName("工单光交箱设施7");
        deviceInfoSix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoSix.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoSix.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoSix);

        ProcRelatedDevice deviceInfoSeven = new ProcRelatedDevice();
        deviceInfoSeven.setDeviceId("NOLTls1LyF31uTQDIbF");
        deviceInfoSeven.setDeviceName("工单光交箱设施5");
        deviceInfoSeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoSeven.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoSeven.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoSeven);

        ProcRelatedDevice deviceInfoEight = new ProcRelatedDevice();
        deviceInfoEight.setDeviceId("Qf2DhlLkboxgyXFrl6F");
        deviceInfoEight.setDeviceName("工单光交箱设施1");
        deviceInfoEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoEight.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoEight.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoEight);

        ProcRelatedDevice deviceInfoNine = new ProcRelatedDevice();
        deviceInfoNine.setDeviceId("rBcyHTq9a6Owch0EpJW");
        deviceInfoNine.setDeviceName("工单光交箱设施10");
        deviceInfoNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoNine.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoNine.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoNine);

        ProcRelatedDevice deviceInfoTen = new ProcRelatedDevice();
        deviceInfoTen.setDeviceId("L7y4nYJlMejQmwL2Pe9");
        deviceInfoTen.setDeviceName("工单光交箱设施8");
        deviceInfoTen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoTen.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoTen.setDeviceAreaName("工单光交箱区域");
        deviceInfoDto.add(deviceInfoTen);

        ProcRelatedDevice deviceInfoEleven = new ProcRelatedDevice();
        deviceInfoEleven.setDeviceId("3brhT9Yru1E5AkD0prc");
        deviceInfoEleven.setDeviceName("工单人井设施7");
        deviceInfoEleven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoEleven.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoEleven.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoEleven);

        ProcRelatedDevice deviceInfoTwelve = new ProcRelatedDevice();
        deviceInfoTwelve.setDeviceId("9HphCuYC8pHYcc3ckag");
        deviceInfoTwelve.setDeviceName("工单人井设施9");
        deviceInfoTwelve.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoTwelve.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoTwelve.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoTwelve);


        ProcRelatedDevice deviceInfoThirteen = new ProcRelatedDevice();
        deviceInfoThirteen.setDeviceId("9HWHTRbXcrfBFZnwYTC");
        deviceInfoThirteen.setDeviceName("工单人井设施3");
        deviceInfoThirteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoThirteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoThirteen.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoThirteen);

        ProcRelatedDevice deviceInfoFourteen = new ProcRelatedDevice();
        deviceInfoFourteen.setDeviceId("B1Hewm1wHXRipI3YWer");
        deviceInfoFourteen.setDeviceName("工单人井设施5");
        deviceInfoFourteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoFourteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoFourteen.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoFourteen);


        ProcRelatedDevice deviceInfoFifteen = new ProcRelatedDevice();
        deviceInfoFifteen.setDeviceId("BEMEG8yPjMpD5A1AkCb");
        deviceInfoFifteen.setDeviceName("工单人井设施6");
        deviceInfoFifteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoFifteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoFifteen.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoFifteen);

        ProcRelatedDevice deviceInfoSixteen = new ProcRelatedDevice();
        deviceInfoSixteen.setDeviceId("GpSgTPYhy67Bx0zKfyT");
        deviceInfoSixteen.setDeviceName("工单人井设施1");
        deviceInfoSixteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoSixteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoSixteen.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoSixteen);

        ProcRelatedDevice deviceInfoSeventeen = new ProcRelatedDevice();
        deviceInfoSeventeen.setDeviceId("mMKvaW6rCOvohtkxj3i");
        deviceInfoSeventeen.setDeviceName("工单人井设施10");
        deviceInfoSeventeen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoSeventeen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoSeventeen.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoSeventeen);


        ProcRelatedDevice deviceInfoEighteen = new ProcRelatedDevice();
        deviceInfoEighteen.setDeviceId("nV4QITYyzTdcWbG7DK3");
        deviceInfoEighteen.setDeviceName("工单人井设施8");
        deviceInfoEighteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoEighteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoEighteen.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoEighteen);

        ProcRelatedDevice deviceInfoNineteen = new ProcRelatedDevice();
        deviceInfoNineteen.setDeviceId("oAfyu6qOEFeX9krR8Pq");
        deviceInfoNineteen.setDeviceName("工单人井设施2");
        deviceInfoNineteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoNineteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoNineteen.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoNineteen);

        ProcRelatedDevice deviceInfoTwenty = new ProcRelatedDevice();
        deviceInfoTwenty.setDeviceId("WSyuPxBcfineOFNbU8i");
        deviceInfoTwenty.setDeviceName("工单人井设施4");
        deviceInfoTwenty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoTwenty.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoTwenty.setDeviceAreaName("工单人井区域");
        deviceInfoDto.add(deviceInfoTwenty);

        ProcRelatedDevice deviceInfoTwentyOne = new ProcRelatedDevice();
        deviceInfoTwentyOne.setDeviceId("2wdfUdK1O4367n3WlZc");
        deviceInfoTwentyOne.setDeviceName("工单配线架设施1");
        deviceInfoTwentyOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyOne.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyOne.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentyOne);

        ProcRelatedDevice deviceInfoTwentyTwo = new ProcRelatedDevice();
        deviceInfoTwentyTwo.setDeviceId("79fAXWZiO8Tyv7ymDWG");
        deviceInfoTwentyTwo.setDeviceName("工单配线架设施10");
        deviceInfoTwentyTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyTwo.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyTwo.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentyTwo);

        ProcRelatedDevice deviceInfoTwentyThree = new ProcRelatedDevice();
        deviceInfoTwentyThree.setDeviceId("7xv1zktafvK6bQAuqcC");
        deviceInfoTwentyThree.setDeviceName("工单配线架设施8");
        deviceInfoTwentyThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyThree.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyThree.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentyThree);

        ProcRelatedDevice deviceInfoTwentyFour = new ProcRelatedDevice();
        deviceInfoTwentyFour.setDeviceId("8QHpqM4mWIDDleaagjI");
        deviceInfoTwentyFour.setDeviceName("工单配线架设施9");
        deviceInfoTwentyFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyFour.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyFour.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentyFour);

        ProcRelatedDevice deviceInfoTwentyFive = new ProcRelatedDevice();
        deviceInfoTwentyFive.setDeviceId("JhZgpNXa8vibm4w4ztr");
        deviceInfoTwentyFive.setDeviceName("工单配线架设施2");
        deviceInfoTwentyFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyFive.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyFive.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentyFive);

        ProcRelatedDevice deviceInfoTwentySix = new ProcRelatedDevice();
        deviceInfoTwentySix.setDeviceId("MpLQ7Yorn7D1KC8HxFG");
        deviceInfoTwentySix.setDeviceName("工单配线架设施6");
        deviceInfoTwentySix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentySix.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentySix.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentySix);

        ProcRelatedDevice deviceInfoTwentySeven = new ProcRelatedDevice();
        deviceInfoTwentySeven.setDeviceId("Qb69DVKLRl8yAEkwy4w");
        deviceInfoTwentySeven.setDeviceName("工单配线架设施4");
        deviceInfoTwentySeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentySeven.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentySeven.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentySeven);

        ProcRelatedDevice deviceInfoTwentyEight = new ProcRelatedDevice();
        deviceInfoTwentyEight.setDeviceId("Su7kvkYiywViwu4ITRl");
        deviceInfoTwentyEight.setDeviceName("工单配线架设施5");
        deviceInfoTwentyEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyEight.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyEight.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentyEight);

        ProcRelatedDevice deviceInfoTwentyNine = new ProcRelatedDevice();
        deviceInfoTwentyNine.setDeviceId("T9bKlpVlucVCKsPcj2I");
        deviceInfoTwentyNine.setDeviceName("工单配线架设施7");
        deviceInfoTwentyNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyNine.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyNine.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoTwentyNine);

        ProcRelatedDevice deviceInfoThirty = new ProcRelatedDevice();
        deviceInfoThirty.setDeviceId("xzZ3ZTwhtgTo4GYyRx6");
        deviceInfoThirty.setDeviceName("工单配线架设施3");
        deviceInfoThirty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoThirty.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoThirty.setDeviceAreaName("工单配线架区域");
        deviceInfoDto.add(deviceInfoThirty);

        ProcRelatedDevice deviceInfoThirtyOne = new ProcRelatedDevice();
        deviceInfoThirtyOne.setDeviceId("5C7e9VyJrGfmZWz1tVC");
        deviceInfoThirtyOne.setDeviceName("工单接头盒设施7");
        deviceInfoThirtyOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyOne.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyOne.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtyOne);

        ProcRelatedDevice deviceInfoThirtyTwo = new ProcRelatedDevice();
        deviceInfoThirtyTwo.setDeviceId("8VmVx11ZFcvXz3kzQl9");
        deviceInfoThirtyTwo.setDeviceName("工单接头盒设施2");
        deviceInfoThirtyTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyTwo.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyTwo.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtyTwo);

        ProcRelatedDevice deviceInfoThirtyThree = new ProcRelatedDevice();
        deviceInfoThirtyThree.setDeviceId("ftpUwt0s8JhWnn4Mbkg");
        deviceInfoThirtyThree.setDeviceName("工单接头盒设施5");
        deviceInfoThirtyThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyThree.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyThree.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtyThree);

        ProcRelatedDevice deviceInfoThirtyFour = new ProcRelatedDevice();
        deviceInfoThirtyFour.setDeviceId("GrDrW8evl3xnVvKq4Gf");
        deviceInfoThirtyFour.setDeviceName("工单接头盒设施1");
        deviceInfoThirtyFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyFour.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyFour.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtyFour);

        ProcRelatedDevice deviceInfoThirtyFive = new ProcRelatedDevice();
        deviceInfoThirtyFive.setDeviceId("OAoxH1e380Njlc3o7WB");
        deviceInfoThirtyFive.setDeviceName("工单接头盒设施4");
        deviceInfoThirtyFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyFive.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyFive.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtyFive);

        ProcRelatedDevice deviceInfoThirtySix = new ProcRelatedDevice();
        deviceInfoThirtySix.setDeviceId("rNCyvz3HQ2pEhcd0xIh");
        deviceInfoThirtySix.setDeviceName("工单接头盒设施10");
        deviceInfoThirtySix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtySix.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtySix.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtySix);

        ProcRelatedDevice deviceInfoThirtySeven = new ProcRelatedDevice();
        deviceInfoThirtySeven.setDeviceId("RznuTwgCCm8ui9qB9ZX");
        deviceInfoThirtySeven.setDeviceName("工单接头盒设施9");
        deviceInfoThirtySeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtySeven.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtySeven.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtySeven);

        ProcRelatedDevice deviceInfoThirtyEight = new ProcRelatedDevice();
        deviceInfoThirtyEight.setDeviceId("uBkvCpkoMjjMJIqVgq5");
        deviceInfoThirtyEight.setDeviceName("工单接头盒设施3");
        deviceInfoThirtyEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyEight.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyEight.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtyEight);

        ProcRelatedDevice deviceInfoThirtyNine = new ProcRelatedDevice();
        deviceInfoThirtyNine.setDeviceId("vEstWmDwdJAjwqNpaY1");
        deviceInfoThirtyNine.setDeviceName("工单接头盒设施8");
        deviceInfoThirtyNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyNine.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyNine.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoThirtyNine);

        ProcRelatedDevice deviceInfoForty = new ProcRelatedDevice();
        deviceInfoForty.setDeviceId("xOTUYzQ82o6jd0pr4s7");
        deviceInfoForty.setDeviceName("工单接头盒设施6");
        deviceInfoForty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoForty.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoForty.setDeviceAreaName("工单接头盒区域");
        deviceInfoDto.add(deviceInfoForty);


        ProcRelatedDevice deviceInfoFortyOne = new ProcRelatedDevice();
        deviceInfoFortyOne.setDeviceId("0hY4vZGGv55oTYyHuVz");
        deviceInfoFortyOne.setDeviceName("工单室外柜设施4");
        deviceInfoFortyOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyOne.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyOne.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortyOne);

        ProcRelatedDevice deviceInfoFortyTwo = new ProcRelatedDevice();
        deviceInfoFortyTwo.setDeviceId("4zv7vbwbQnIrEzzADyu");
        deviceInfoFortyTwo.setDeviceName("工单室外柜设施7");
        deviceInfoFortyTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyTwo.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyTwo.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortyTwo);

        ProcRelatedDevice deviceInfoFortyThree = new ProcRelatedDevice();
        deviceInfoFortyThree.setDeviceId("8BApbiMbz09Qs7gf6j9");
        deviceInfoFortyThree.setDeviceName("工单室外柜设施3");
        deviceInfoFortyThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyThree.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyThree.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortyThree);


        ProcRelatedDevice deviceInfoFortyFour = new ProcRelatedDevice();
        deviceInfoFortyFour.setDeviceId("bXcDRiU8sbnhXV8V5ha");
        deviceInfoFortyFour.setDeviceName("工单室外柜设施9");
        deviceInfoFortyFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyFour.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyFour.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortyFour);


        ProcRelatedDevice deviceInfoFortyFive = new ProcRelatedDevice();
        deviceInfoFortyFive.setDeviceId("C0TEGCKF09Xyqka1j37");
        deviceInfoFortyFive.setDeviceName("工单室外柜设施2");
        deviceInfoFortyFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyFive.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyFive.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortyFive);


        ProcRelatedDevice deviceInfoFortySix = new ProcRelatedDevice();
        deviceInfoFortySix.setDeviceId("e74LPZbpTt8ro3SBrPI");
        deviceInfoFortySix.setDeviceName("工单室外柜设施5");
        deviceInfoFortySix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortySix.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortySix.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortySix);

        ProcRelatedDevice deviceInfoFortySeven = new ProcRelatedDevice();
        deviceInfoFortySeven.setDeviceId("gPbncSEgunep1c52K75");
        deviceInfoFortySeven.setDeviceName("工单室外柜设施8");
        deviceInfoFortySeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortySeven.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortySeven.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortySeven);

        ProcRelatedDevice deviceInfoFortyEight = new ProcRelatedDevice();
        deviceInfoFortyEight.setDeviceId("KyxteZ5h0InVscTU1k2");
        deviceInfoFortyEight.setDeviceName("工单室外柜设施10");
        deviceInfoFortyEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyEight.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyEight.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortyEight);

        ProcRelatedDevice deviceInfoFortyNine = new ProcRelatedDevice();
        deviceInfoFortyNine.setDeviceId("qO6RTIpbxbLV3g0TDEM");
        deviceInfoFortyNine.setDeviceName("工单室外柜设施1");
        deviceInfoFortyNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyNine.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyNine.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFortyNine);

        ProcRelatedDevice deviceInfoFifty = new ProcRelatedDevice();
        deviceInfoFifty.setDeviceId("xsRXwrN3esvtaMac4Ky");
        deviceInfoFifty.setDeviceName("工单室外柜设施6");
        deviceInfoFifty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFifty.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFifty.setDeviceAreaName("工单室外柜区域");
        deviceInfoDto.add(deviceInfoFifty);


        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_001);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_030);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_060);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_090);
        deviceTypeList.add(ProcBaseConstants.DEVICE_TYPE_210);

        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
        ProcRelatedDepartment procRelatedDepartment1 = new ProcRelatedDepartment();
        procRelatedDepartment1.setAccountabilityDept("hSbiawtur3yvncyEUcw");
        procRelatedDepartments.add(procRelatedDepartment1);

        ProcRelatedDepartment procRelatedDepartment2 = new ProcRelatedDepartment();
        procRelatedDepartment2.setAccountabilityDept("4tcwsUZO7bouUzoq4Gu");
        procRelatedDepartments.add(procRelatedDepartment2);

        ProcRelatedDepartment procRelatedDepartment3 = new ProcRelatedDepartment();
        procRelatedDepartment3.setAccountabilityDept("SRWHYXeS8uTinIxyZTB");
        procRelatedDepartments.add(procRelatedDepartment3);

        ProcRelatedDepartment procRelatedDepartment4 = new ProcRelatedDepartment();
        procRelatedDepartment4.setAccountabilityDept("8ZbIQV6oMKzjaUZhJWu");
        procRelatedDepartments.add(procRelatedDepartment4);

        ProcRelatedDepartment procRelatedDepartment5 = new ProcRelatedDepartment();
        procRelatedDepartment5.setAccountabilityDept("JZwKT17uOCedCkbTYua");
        procRelatedDepartments.add(procRelatedDepartment5);
        ProcBase procBase = new ProcBase();
        procBase.setRefAlarm("fXKvIDRTdRRxPJDet9T");
        procBase.setRefAlarmName("非法开门");
        procBase.setRefAlarmCode("violenceClose");
        this.addInsertTest(deviceInfoDto, procRelatedDepartments, procBase);
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
