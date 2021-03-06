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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * ?????????????????????
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
     * ????????????
     */
    @Autowired
    private ProcBaseService procBaseService;

    /**
     * ??????feign
     */
    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProcBaseDao procBaseDao;

    /**
     * ????????????service
     */
    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * ????????????service
     */
    @Autowired
    private ProcInspectionService procInspectionService;

    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * ?????????????????????
     */
    @Autowired
    private ProcStatusStatisticalExport procStatusStatisticalExport;

    /**
     * ???????????????????????????
     */
    @Autowired
    private ProcDeviceTypeStatisticalExport procDeviceTypeStatisticalExport;

    /**
     * ???????????????????????????
     */
    @Autowired
    private ProcErrorReasonStatisticalExport procErrorReasonStatisticalExport;

    /**
     * ???????????????????????????
     */
    @Autowired
    private ProcProcessingSchemeStatisticalExport procProcessingSchemeStatisticalExport;

    /**
     * ????????????????????????
     */
    @Autowired
    private ProcAreaPercentStatisticalExport procAreaPercentStatisticalExport;

    /**
     * ?????????????????????????????????
     */
    @Autowired
    private ProcTopListStatisticalExport procTopListStatisticalExport;

    /**
     * ????????????service
     */
    @Autowired
    private ProcLogService procLogService;


    /**
     * ??????????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/5/28 19:14
     */
    @Override
    public Result queryListProcGroupByProcStatus(QueryCondition<QueryListProcGroupByProcStatusReq> req) {

        //????????????
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
        //????????????
        String procType = bizCondition.getProcType();
        List<ProcStatusStatistical> procStatusList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //?????????????????? ??? ?????????????????????????????????
            procStatusList = procStatisticalDao.queryListProcInspectionGroupByProcStatus(req);

        } else {
            //????????????
            //?????????????????? ??? ?????????????????????????????????
            procStatusList = procStatisticalDao.queryListProcClearGroupByProcStatus(req);
        }

        //?????????????????????????????????????????????????????????????????????
        List<ProcStatusStatisticalVo> procStatusVoList = ProcStatusStatisticalVo.castProcStatusVoForProcStatusBean(procStatusList);

        //??????????????????????????????
        List<String> areaIdList = bizCondition.getAreaIdList();
        //??????????????????
        List<String> statusList = ProcStatisticalConstants.statusList;

        //?????????????????????????????????
        Map<String, Map<String, Object>> procStatusStatisticalMap = this.getDefaultStatisticalMap(areaIdList, statusList);

        //??????????????????
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procStatusVoList)) {

            //????????????????????????Map???
            for (ProcStatusStatisticalVo procStatusOne : procStatusVoList) {
                Map<String, Object> mapOne = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                if (procStatusStatisticalMap.containsKey(procStatusOne.getAreaId())) {
                    mapOne = procStatusStatisticalMap.get(procStatusOne.getAreaId());
                }
                mapOne.put(procStatusOne.getStatus(), procStatusOne.getOrderCount());
                procStatusStatisticalMap.put(procStatusOne.getAreaId(), mapOne);
            }
        }

        //????????????????????????
        for (String areaIdOne : areaIdList) {
            retList.add(procStatusStatisticalMap.get(areaIdOne));
        }
        return ResultUtils.success(retList);
    }

    /**
     * ????????????????????????
     *
     * @param req ????????????????????????
     * @return ????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 14:03
     */
    @Override
    public Result queryListProcOverviewGroupByProcStatus(QueryCondition<QueryListProcOverviewGroupByProcStatusReq> req) {

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByProcStatusReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //?????????????????????????????????
        List<ProcStatusOverviewStatisticalVo> defaultVoList = ProcStatusOverviewStatisticalVo.getDefaultOverviewStatusVoList(bizCondition);

        //???????????????????????????
        List<ProcStatusOverviewStatisticalVo> procStatusVoList = this.getProcStatusOverviewVoList(req);

        //??????????????????????????????
        List<ProcStatusOverviewStatisticalVo> retVoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //????????????map
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
     * ????????????????????????????????????
     *
     * @param req ??????????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 15:29
     */
    @Override
    public Result queryNowDateAddOrderCount(QueryCondition<QueryNowDateAddOrderCountReq> req) {
        //??????????????????
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
        //????????????
        String procType = bizCondition.getProcType();
        List<ProcNowDateAddOrderCountOverview> addOrderList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //????????????????????????
            addOrderList = procStatisticalDao.queryNowDateInspectionAddOrderCount(req);

        } else {
            //????????????
            //????????????????????????
            addOrderList = procStatisticalDao.queryNowDateClearAddOrderCount(req);
        }
        return ResultUtils.success(addOrderList);
    }


    /**
     * ????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcStatusOverviewStatisticalVo> getProcStatusOverviewVoList(QueryCondition<QueryListProcOverviewGroupByProcStatusReq> req) {
        //????????????
        String procType = req.getBizCondition().getProcType();
        List<ProcStatusOverviewStatistical> procStatusList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //??????????????????
            procStatusList = procStatisticalDao.queryListProcInspectionOverviewGroupByProcStatus(req);
        } else {
            //????????????
            //??????????????????
            procStatusList = procStatisticalDao.queryListProcClearOverviewGroupByProcStatus(req);
        }
        //?????????????????????????????????????????????????????????????????????
        List<ProcStatusOverviewStatisticalVo> procStatusVoList = ProcStatusOverviewStatisticalVo.castProcStatusVoForProcStatusBean(procStatusList);
        return procStatusVoList;
    }


    /**
     * ????????????????????????????????????
     *
     * @param req ??????????????????????????????
     * @return ????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/5/28 19:14
     */
    @Override
    public Result queryListProcGroupByProcProcessingScheme(QueryCondition<QueryListProcGroupByProcProcessingSchemeReq> req) {

        //????????????
        QueryListProcGroupByProcProcessingSchemeReq bizCondition = (QueryListProcGroupByProcProcessingSchemeReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //????????????
        String procType = bizCondition.getProcType();
        List<ProcProcessingSchemeStatistical> procProcessingSchemeList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //?????????????????? ??? ???????????????????????????????????????
            procProcessingSchemeList = procStatisticalDao.queryListProcInspectionGroupByProcProcessingScheme(req);

        } else {
            //????????????
            //?????????????????? ??? ???????????????????????????????????????
            procProcessingSchemeList = procStatisticalDao.queryListProcClearGroupByProcProcessingScheme(req);
        }

        //????????????????????????????????????
        List<ProcProcessingSchemeStatisticalVo> retVoList = ProcProcessingSchemeStatisticalVo.castProcessingSchemeVoForProcStatusBean(procProcessingSchemeList);

        //????????????????????????????????????
        List<String> areaIdList = bizCondition.getAreaIdList();
        //????????????????????????
        List<String> processingSchemeList = ProcStatisticalConstants.processingSchemeList;

        //???????????????????????????????????????
        Map<String, Map<String, Object>> processingSchemeStatisticalMap = this.getDefaultStatisticalMap(areaIdList, processingSchemeList);

        //??????????????????
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(retVoList)) {

            //????????????????????????Map???
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

        //??????????????????????????????
        for (String areaIdOne : areaIdList) {
            retList.add(processingSchemeStatisticalMap.get(areaIdOne));
        }

        return ResultUtils.success(retList);
    }


    /**
     * ??????????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 19:11
     */
    @Override
    public Result queryListProcOverviewGroupByProcProcessingScheme(QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> req) {

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByProcessingSchemeReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //??????????????????
        List<ProcProcessingSchemeOverviewStatisticalVo> defaultVoList = ProcProcessingSchemeOverviewStatisticalVo.getDefaultOverviewProcessingSchemeVoList(bizCondition);

        //?????????????????????????????????
        List<ProcProcessingSchemeOverviewStatisticalVo> schemeOverviewVoList = this.getProcessingSchemeOverviewVoList(req);


        //????????????????????????????????????
        List<ProcProcessingSchemeOverviewStatisticalVo> retVoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //????????????map
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
     * ????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcProcessingSchemeOverviewStatisticalVo> getProcessingSchemeOverviewVoList(QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> req) {
        //????????????
        String procType = req.getBizCondition().getProcType();

        List<ProcProcessingSchemeOverviewStatistical> schemeOverviewList;

        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //??????????????????????????????
            schemeOverviewList = procStatisticalDao.queryListProcInspectionOverviewGroupByProcProcessingScheme(req);
        } else {
            //????????????
            //??????????????????????????????
            schemeOverviewList = procStatisticalDao.queryListProcClearOverviewGroupByProcProcessingScheme(req);
        }

        //?????????????????????????????????????????????????????????????????????
        List<ProcProcessingSchemeOverviewStatisticalVo> schemeOverviewVoList = ProcProcessingSchemeOverviewStatisticalVo.castProcessingSchemeVoForProcErrorReasonBean(schemeOverviewList);
        return schemeOverviewVoList;
    }


    /**
     * ????????????????????????????????????
     *
     * @param req ????????????????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 21:06
     */
    @Override
    public Result queryListProcGroupByErrorReason(QueryCondition<QueryListProcGroupByProcErrorReasonReq> req) {

        //????????????
        QueryListProcGroupByProcErrorReasonReq bizCondition = (QueryListProcGroupByProcErrorReasonReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //????????????
        String procType = bizCondition.getProcType();
        List<ProcErrorReasonStatistical> errorReasonInfoList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //??????????????????????????????
            errorReasonInfoList = procStatisticalDao.queryListProcInspectionGroupByErrorReason(req);;
        } else {
            //????????????
            //??????????????????????????????
            errorReasonInfoList = procStatisticalDao.queryListProcClearGroupByErrorReason(req);;
        }

        //????????????????????????????????????
        List<ProcErrorReasonStatisticalVo> voList = ProcErrorReasonStatisticalVo.castErrorReasonVoForProcStatusBean(errorReasonInfoList);

        //????????????????????????????????????
        List<String> areaIdList = bizCondition.getAreaIdList();
        //????????????????????????
        List<String> errorReasonList = ProcStatisticalConstants.errorReasonList;

        //???????????????????????????????????????
        Map<String, Map<String, Object>> errorReasonStatisticalMap = this.getDefaultStatisticalMap(areaIdList, errorReasonList);

        //??????????????????
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(voList)) {

            //????????????????????????Map???
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

        //??????????????????????????????
        for (String areaIdOne : areaIdList) {
            retList.add(errorReasonStatisticalMap.get(areaIdOne));
        }

        return ResultUtils.success(retList);
    }

    /**
     * ????????????????????????
     *
     * @param req ??????????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 21:48
     */
    @Override
    public Result queryListProcOverviewGroupByProcErrorReason(QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> req) {
        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByErrorReasonReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //??????????????????
        List<ProcErrorReasonOverviewStatisticalVo> defaultVoList = ProcErrorReasonOverviewStatisticalVo.getDefaultOverviewErrorReasonVoList(bizCondition);

        //?????????????????????????????????
        List<ProcErrorReasonOverviewStatisticalVo> errorReasonOverviewVoList = this.getErrorReasonOverviewVoList(req);

        //????????????????????????????????????
        List<ProcErrorReasonOverviewStatisticalVo> retVoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //????????????????????????map
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
     * ????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcErrorReasonOverviewStatisticalVo> getErrorReasonOverviewVoList(QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> req) {

        //????????????
        String procType = req.getBizCondition().getProcType();
        List<ProcErrorReasonOverviewStatistical> schemeOverviewList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //??????????????????????????????
            schemeOverviewList = procStatisticalDao.queryListProcInspectionOverviewGroupByErrorReason(req);

        } else {
            //????????????
            //??????????????????????????????
            schemeOverviewList = procStatisticalDao.queryListProcClearOverviewGroupByErrorReason(req);
        }

        //?????????????????????????????????????????????????????????????????????
        List<ProcErrorReasonOverviewStatisticalVo> schemeOverviewVoList = ProcErrorReasonOverviewStatisticalVo.castErrorReasonVoForErrorReasonBean(schemeOverviewList);
        return schemeOverviewVoList;
    }

    /**
     * ????????????????????????
     *
     * @param req ????????????????????????
     * @return ????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/5/31 15:00
     */
    @Override
    public Result queryListProcGroupByDeviceType(QueryCondition<QueryListProcGroupByProcDeviceTypeReq> req) {
        //????????????
        QueryListProcGroupByProcDeviceTypeReq bizCondition = (QueryListProcGroupByProcDeviceTypeReq) req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizCondition)) {
            if (ObjectUtils.isEmpty(bizCondition.getAreaIdList()) || ObjectUtils.isEmpty(bizCondition.getDeviceTypeList())) {
                return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
            }
        } else {
            return ResultUtils.success(new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE));
        }

        //????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //????????????
        String procType = bizCondition.getProcType();
        List<ProcDeviceTypeStatistical> procDeviceTypeStatisticalList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //????????????????????????
            procDeviceTypeStatisticalList = procStatisticalDao.queryListProcInspectionGroupByDeviceType(req);

        } else {
            //????????????
            //????????????????????????
            procDeviceTypeStatisticalList = procStatisticalDao.queryListProcClearGroupByDeviceType(req);
        }

        //??????????????????????????????
        List<ProcDeviceTypeStatisticalVo> voList = ProcDeviceTypeStatisticalVo.castDeviceTypeVoForProcStatusBean(procDeviceTypeStatisticalList);

        //????????????????????????????????????
        List<String> areaIdList = bizCondition.getAreaIdList();
        //????????????????????????
        List<String> deviceTypeList = bizCondition.getDeviceTypeList();

        //???????????????????????????????????????
        Map<String, Map<String, Object>> deviceTypeStatisticalMap = this.getDefaultStatisticalMap(areaIdList, deviceTypeList);

        //??????????????????
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(voList)) {

            //????????????????????????Map???
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

        //??????????????????????????????
        for (String areaIdOne : areaIdList) {
            retList.add(deviceTypeStatisticalMap.get(areaIdOne));
        }
        return ResultUtils.success(retList);
    }


    /**
     * ????????????????????????
     *
     * @param req ??????????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 21:48
     */
    @Override
    public Result queryListProcOverviewGroupByProcDeviceType(QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> req) {

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        QueryListProcOverviewGroupByDeviceTypeReq bizCondition = req.getBizCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        req.setBizCondition(bizCondition);

        //??????????????????
        List<ProcDeviceTypeOverviewStatisticalVo> defaultVoList = ProcDeviceTypeOverviewStatisticalVo.getDefaultOverviewDeviceTypeVoList(bizCondition);

        //?????????????????????????????????
        List<ProcDeviceTypeOverviewStatisticalVo> deviceTypeOverviewVoList = this.getDeviceTypeOverviewVoList(req);

        //????????????????????????????????????
        List<ProcDeviceTypeOverviewStatisticalVo> retVoList = new ArrayList<>();


        if (!ObjectUtils.isEmpty(defaultVoList)) {
            //????????????map
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
     * ????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 10:20
     */
    public List<ProcDeviceTypeOverviewStatisticalVo> getDeviceTypeOverviewVoList(QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> req) {
        //????????????
        String procType = req.getBizCondition().getProcType();
        List<ProcDeviceTypeOverviewStatistical> deviceTypeOverviewList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //??????????????????????????????
            deviceTypeOverviewList =  procStatisticalDao.queryListProcInspectionOverviewGroupByDeviceTypeList(req);
        } else {
            //????????????
            //??????????????????????????????
            deviceTypeOverviewList =  procStatisticalDao.queryListProcClearOverviewGroupByDeviceTypeList(req);
        }
        //?????????????????????????????????????????????????????????????????????
        List<ProcDeviceTypeOverviewStatisticalVo> deviceTypeOverviewVoList = ProcDeviceTypeOverviewStatisticalVo.castDeviceTypeVoForProcDeviceTypeBean(deviceTypeOverviewList);
        return deviceTypeOverviewVoList;
    }

    /**
     * ???????????????????????????
     *
     * @param req ?????????????????????
     * @return ?????????????????????
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

        //????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //????????????
        String procType = bizCondition.getProcType();
        List<ProcAreaPercentStatistical> areaPercentList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //???????????????????????????
            areaPercentList = procStatisticalDao.queryListProcInspectionGroupByAreaPercent(req);

        } else {
            //????????????
            //???????????????????????????
            areaPercentList = procStatisticalDao.queryListProcClearGroupByAreaPercent(req);
        }
        //???????????????????????????
        List<ProcAreaPercentStatisticalVo> percentStatisticalVoList = ProcAreaPercentStatisticalVo.castAreaPercentVoForProcStatusBean(areaPercentList);


        //????????????????????????????????????
        List<String> areaIdList = bizCondition.getAreaIdList();

        //????????????????????????????????????
        Map<String, Map<String, Object>> areaPercentStatisticalMap = this.getDefaultStatisticalMap(areaIdList);

        //??????????????????
        List<Map<String, Object>> retList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(percentStatisticalVoList)) {

            //????????????????????????Map???
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

        //???????????????????????????
        for (String areaIdOne : areaIdList) {
            retList.add(areaPercentStatisticalMap.get(areaIdOne));
        }
        return ResultUtils.success(retList);
    }

    /**
     * ????????????????????????
     *
     * @param req ??????????????????
     * @return ????????????????????????
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

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        req.setBizCondition(bizCondition);

        //????????????
        String procType = bizCondition.getProcType();
        List<ProcDepartmentStatistical> departmentStatisticalList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //????????????????????????
            departmentStatisticalList = procStatisticalDao.queryInspectionDeptListGroupByAccountabilityDept(req);

        } else {
            //????????????
            //????????????????????????
            departmentStatisticalList = procStatisticalDao.queryClearDeptListGroupByAccountabilityDept(req);
        }

        //??????
        Map<String, String> getDepartmentMap = this.getDepartmentMap(bizCondition);

        //??????????????????
        List<String> deptIdList = bizCondition.getAccountabilityDeptList();
        //????????????
        List<ProcDepartmentStatisticalVo> defaultVoList = this.getDefaultDeptStatisticalList(deptIdList, getDepartmentMap);


        //????????????????????????
        List<ProcDepartmentStatisticalVo> procDepartmentStatisticalList = ProcDepartmentStatisticalVo.castDepartmentVoForProcStatusBean(departmentStatisticalList, getDepartmentMap);
        Map<String, ProcDepartmentStatisticalVo> procDepartmentStatisticalVoMap = this.getDepartmentStatisticalVoMap(procDepartmentStatisticalList);


        //?????????????????????????????????
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
     * ????????????map
     *
     * @param req ????????????????????????
     * @return ????????????map
     * @author hedongwei@wistronits.com
     * @date 2019/6/3 16:22
     */
    public Map<String, String> getDepartmentMap(QueryDeptListGroupByAccountabilityDeptReq req) {
        Map<String, String> departmentMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(req.getAccountabilityDeptList())) {
            Set<String> deptSet = new HashSet<>();
            //???????????????????????????????????????
            for (String deptOne : req.getAccountabilityDeptList()) {
                deptSet.add(deptOne);
            }
            //????????????
            List<String> deptIdList = new ArrayList<>();
            deptIdList.addAll(deptSet);
            List<Department> departmentAllSearchList = departmentFeign.queryDepartmentFeignById(deptIdList);
            departmentMap = CastMapUtil.getDepartmentMap(departmentAllSearchList);
        }
        return departmentMap;
    }

    /**
     * ???????????????n??????????????????
     *
     * @param req ????????????
     * @return ???????????????n??????????????????
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
        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //????????????
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //????????????
        String procType = bizCondition.getProcType();
        List<ProcDeviceTopStatistical> deviceTopList;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //????????????
            //???????????????n??????????????????
            deviceTopList = procStatisticalDao.queryInspectionListDeviceCountGroupByDevice(req);

        } else {
            //????????????
            //???????????????n??????????????????
            deviceTopList = procStatisticalDao.queryClearListDeviceCountGroupByDevice(req);
        }

        //???????????????n????????????????????????
        List<ProcDeviceTopStatisticalVo> procDeviceTopVoList = ProcDeviceTopStatisticalVo.castDeviceTopVoForProcStatusBean(deviceTopList);
        return ResultUtils.success(procDeviceTopVoList);
    }

    /**
     * ??????????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????????????????
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


        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //????????????
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //??????????????????
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }

        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //????????????????????????????????????????????????
        List<QueryProcCountByTimeStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByTime(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType()).and("accountabilityDept").in(deptList);
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //??????????????????
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
     * ?????????????????????????????????
     *
     * @param req ???????????????????????????
     * @return ??????????????????????????????
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


        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //????????????
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //??????????????????
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }



        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //????????????????????????????????????????????????
        List<QueryProcCountByWeekStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByWeek(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType()).and("accountabilityDept").in(deptList);
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //??????????????????
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
     * ?????????????????????????????????
     *
     * @param req ???????????????????????????
     * @return ??????????????????????????????
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


        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //????????????
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //??????????????????
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }


        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //????????????????????????????????????????????????
        List<QueryProcCountByMonthStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByMonth(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType()).and("accountabilityDept").in(deptList);
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //??????????????????
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
     * ?????????????????????????????????
     *
     * @param req ???????????????????????????
     * @return ??????????????????????????????
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


        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        //????????????
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //??????????????????
        List<String> deptList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bizCondition.getPermissionDeptIds())) {
            deptList.addAll(bizCondition.getPermissionDeptIds());
        }


        List<Long> timeList = bizCondition.getTimeList();
        long startTime = timeList.get(0);
        startTime = CastStatisticalUtil.getTimeZeroTime(startTime);
        long endTime = timeList.get(1);
        endTime = CastStatisticalUtil.getTimeZeroTime(endTime);

        //????????????????????????????????????????????????
        List<QueryProcCountByYearStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getDefaultProcCountByYear(bizCondition);

        Criteria criteria;
        if (!ObjectUtils.isEmpty(deptList)) {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType()).and("accountabilityDept").in(deptList);
        } else {
            criteria = Criteria.where("areaId").in(bizCondition.getAreaIdList()).and("deviceType").in(bizCondition.getDeviceTypeList())
                    .and("nowDate").gte(startTime).lte(endTime)
                    .and("procType").is(bizCondition.getProcType());
        }

        //??????????????????
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
     * ??????????????????????????????
     *
     * @param req ????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByDay(QueryCondition<QueryHomeProcCountByTimeReq> req) {
        QueryHomeProcCountByTimeReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeDefaultInfo());

        //????????????????????????????????????????????????
        List<QueryProcCountByTimeStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultProcCount(bizCondition.getTimeList());

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());


        //??????????????????
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //????????????????????????
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //????????????????????????
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //????????????
        List<Long> timeList = bizCondition.getTimeList();
        //????????????
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //??????????????????
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //??????????????????
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
     * ?????????????????????????????????
     *
     * @param req ???????????????????????????
     * @return ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByWeek(QueryCondition<QueryHomeProcCountByWeekReq> req) {
        QueryHomeProcCountByWeekReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeWeekDefaultInfo());
        //????????????????????????????????????????????????
        List<QueryProcCountByWeekStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultWeekProcCount(bizCondition.getTimeList());

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //??????????????????
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //????????????????????????
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //????????????????????????
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //????????????
        List<Long> timeList = bizCondition.getTimeList();
        //????????????
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //??????????????????
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //??????????????????
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
     * ?????????????????????????????????
     *
     * @param req ???????????????????????????
     * @return ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByMonth(QueryCondition<QueryHomeProcCountByMonthReq> req) {
        QueryHomeProcCountByMonthReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeMonthDefaultInfo());

        //????????????????????????????????????????????????
        List<QueryProcCountByMonthStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultMonthProcCount(bizCondition.getTimeList());

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //??????????????????
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //????????????????????????
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //????????????????????????
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //????????????
        List<Long> timeList = bizCondition.getTimeList();
        //????????????
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //??????????????????
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //??????????????????
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
     * ?????????????????????????????????
     *
     * @param req ???????????????????????????
     * @return ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/6 14:07
     */
    @Override
    public Result queryHomeProcAddListCountGroupByYear(QueryCondition<QueryHomeProcCountByYearReq> req) {
        QueryHomeProcCountByYearReq bizCondition = req.getBizCondition();
        bizCondition.setTimeList(CastStatisticalUtil.getHomeYearDefaultInfo());

        //????????????????????????????????????????????????
        List<QueryProcCountByYearStatisticalVo> defaultQueryProcCount = CastStatisticalUtil.getHomeDefaultYearProcCount(bizCondition.getTimeList());

        //??????????????????
        ProcBaseReq procBaseReq = this.getPermissionCondition();
        bizCondition.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        bizCondition.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        bizCondition.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());

        //??????????????????
        Set<String> permissionDeviceTypes = bizCondition.getPermissionDeviceTypes();
        //????????????????????????
        Set<String> permissionAreaIds = bizCondition.getPermissionAreaIds();
        //????????????????????????
        Set<String> permissionDeptIds = bizCondition.getPermissionDeptIds();
        //????????????
        List<Long> timeList = bizCondition.getTimeList();
        //????????????
        String procType = bizCondition.getProcType();

        if (ObjectUtils.isEmpty(permissionDeviceTypes) || ObjectUtils.isEmpty(permissionAreaIds)) {
            if (!ObjectUtils.isEmpty(permissionDeptIds)) {
                return ResultUtils.success(defaultQueryProcCount);
            }
        }
        //??????????????????
        Criteria criteria = CastStatisticalUtil.getHomeSearchCriteria(permissionDeviceTypes, permissionAreaIds, permissionDeptIds, timeList, procType);

        //??????????????????
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
     * ?????????????????????????????????
     *
     * @param results               ??????????????????????????????
     * @param defaultQueryProcCount ????????????????????????
     * @return ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByTimeStatisticalVo> getReturnQueryProcCount(AggregationResults<ProcInfoDateStatisticalGroup> results, List<QueryProcCountByTimeStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //???????????????????????????????????????
            List<ProcInfoDateStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByTimeStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByTimeStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByTimeStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByTimeStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //????????????
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }


    /**
     * ???????????????????????????????????????
     *
     * @param results               ????????????????????????????????????
     * @param defaultQueryProcCount ??????????????????????????????
     * @return ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByWeekStatisticalVo> getReturnQueryWeekProcCount(AggregationResults<ProcInfoWeekStatisticalGroup> results, List<QueryProcCountByWeekStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //???????????????????????????????????????
            List<ProcInfoWeekStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByWeekStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByWeekStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByWeekStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByWeekStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //????????????
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param results               ????????????????????????????????????
     * @param defaultQueryProcCount ??????????????????????????????
     * @return ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByMonthStatisticalVo> getReturnQueryMonthProcCount(AggregationResults<ProcInfoMonthStatisticalGroup> results, List<QueryProcCountByMonthStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //???????????????????????????????????????
            List<ProcInfoMonthStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByMonthStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByMonthStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByMonthStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByMonthStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //????????????
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }


    /**
     * ???????????????????????????????????????
     *
     * @param results               ????????????????????????????????????
     * @param defaultQueryProcCount ??????????????????????????????
     * @return ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/12 11:13
     */
    public List<QueryProcCountByYearStatisticalVo> getReturnQueryYearProcCount(AggregationResults<ProcInfoYearStatisticalGroup> results, List<QueryProcCountByYearStatisticalVo> defaultQueryProcCount) {
        if (!ObjectUtils.isEmpty(defaultQueryProcCount)) {
            //???????????????????????????????????????
            List<ProcInfoYearStatisticalGroup> nowDateList = results.getMappedResults();
            Map<Long, QueryProcCountByYearStatisticalVo> statisticalVoMap = CastStatisticalUtil.getProcCountByYearStatisticalVoMap(nowDateList);
            if (!ObjectUtils.isEmpty(nowDateList)) {
                for (QueryProcCountByYearStatisticalVo procInfoOne : defaultQueryProcCount) {
                    if (statisticalVoMap.containsKey(procInfoOne.getNowDate())) {
                        QueryProcCountByYearStatisticalVo statisticalVoOne = statisticalVoMap.get(procInfoOne.getNowDate());
                        //????????????
                        procInfoOne.setOrderCount(statisticalVoOne.getOrderCount());
                    }
                }
            }
        }
        return defaultQueryProcCount;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @return ????????????
     * @author hedongwei@wistronits.com
     */
    @Override
    public void setProcCountSumToProcAddCountTable() {
        int beforeDay = -1;
        Long beginDate = CastStatisticalUtil.getNowTimeAdd(beforeDay);
        Long beginTime = beginDate / 1000;

        //?????????????????????????????????
        Long endDate = CastStatisticalUtil.getNowDayLastTimeAdd(beforeDay);
        Long endTime = endDate / 1000;

        QueryProcCountSumReq req = new QueryProcCountSumReq();
        List<Long> timeList = new ArrayList<>();
        timeList.add(beginTime);
        timeList.add(endTime);
        req.setTimeList(timeList);
        //????????????????????????
        List<ProcInfoDateStatistical> inspectionDateInfoList = procStatisticalDao.queryProcInspectionListByNowDate(req);
        //????????????????????????
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

        //??????????????????????????????
        int deleteDay = -15;
        Long deleteDate = CastStatisticalUtil.getNowTimeAdd(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoDateStatistical.class);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:19
     */
    @Override
    public void setProcWeekCountSumToProcAddCountTable() {
        int beforeWeek = -1;
        Long beginDate = CastStatisticalUtil.getAdvanceNumberWeek(beforeWeek);
        Long beginTime = beginDate / 1000;
        //?????????????????????????????????
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

        //??????????????????????????????
        int deleteDay = -15;
        Long deleteDate = CastStatisticalUtil.getAdvanceLastNumberWeek(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoWeekStatistical.class);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:19
     */
    @Override
    public void setProcMonthCountSumToProcAddCountTable() {
        int beforeMonth = -1;
        Long beginDate = CastStatisticalUtil.getAdvanceNumberMonth(beforeMonth);
        Long beginTime = beginDate / 1000;
        //?????????????????????????????????
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

        //??????????????????????????????
        int deleteDay = -12;
        Long deleteDate = CastStatisticalUtil.getAdvanceLastNumberMonth(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoMonthStatistical.class);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:19
     */
    @Override
    public void setProcYearCountSumToProcAddCountTable() {
        int beforeYear = -1;
        Long beginDate = CastStatisticalUtil.getAdvanceNumberYear(beforeYear);
        Long beginTime = beginDate / 1000;
        //?????????????????????????????????
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

        //??????????????????????????????
        int deleteDay = -12;
        Long deleteDate = CastStatisticalUtil.getAdvanceLastNumberYear(deleteDay);
        Query query = Query.query(Criteria.where("nowDate").lt(deleteDate));
        mongoTemplate.remove(query, ProcInfoYearStatistical.class);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ??????????????????????????????
     */
    @Override
    public Result procClearStatusStatisticalExport(ExportDto<ProcStatusStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procStatusStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_STATUS_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_STATUS_EXPORT_CODE);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ????????????????????????????????????
     */
    @Override
    public Result procClearDeviceTypeStatisticalExport(ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procDeviceTypeStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_DEVICE_TYPE_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_DEVICE_TYPE_EXPORT_CODE);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ????????????????????????????????????
     */
    @Override
    public Result procClearProcessingSchemeStatisticalExport(ExportDto<ProcProcessingSchemeStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procProcessingSchemeStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_PROCESSING_SCHEME_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_PROCESSING_SCHEME_EXPORT_CODE);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ????????????????????????????????????
     */
    @Override
    public Result procClearErrorReasonStatisticalExport(ExportDto<ProcErrorReasonStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procErrorReasonStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_ERROR_REASON_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_ERROR_REASON_EXPORT_CODE);
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ?????????????????????????????????
     */
    @Override
    public Result procClearAreaPercentStatisticalExport(ExportDto<ProcAreaPercentStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procAreaPercentStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_AREA_PERCENT_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_AREA_PERCENT_EXPORT_CODE);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ??????????????????????????????
     */
    @Override
    public Result procInspectionStatusStatisticalExport(ExportDto<ProcStatusStatisticalExportBean> exportDto) {
        return this.exportInfo(procStatusStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_STATUS_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_STATUS_EXPORT_CODE);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ????????????????????????????????????
     */
    @Override
    public Result procInspectionDeviceTypeStatisticalExport(ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procDeviceTypeStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_DEVICE_TYPE_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_DEVICE_TYPE_EXPORT_CODE);
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto ????????????
     * @return ?????????????????????????????????
     */
    @Override
    public Result procInspectionAreaPercentStatisticalExport(ExportDto<ProcAreaPercentStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procAreaPercentStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_AREA_PERCENT_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_AREA_PERCENT_EXPORT_CODE);
    }

    /**
     * ??????????????????top??????
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto ????????????
     * @return ??????????????????
     */
    @Override
    public Result procClearTopListStatisticalExport(ExportDto<ProcTopListStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procTopListStatisticalExport, exportDto,
                ProcBaseI18n.CLEAR_FAILURE_COUNT_EXPORT_STATISTICAL, LogFunctionCodeConstant.CLEAR_FAILURE_COUNT_EXPORT_CODE);
    }

    /**
     * ??????????????????top??????
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto ????????????
     * @return ??????????????????
     */
    @Override
    public Result procInspectionTopListStatisticalExport(ExportDto<ProcTopListStatisticalExportBean> exportDto) {
        //????????????
        return this.exportInfo(procTopListStatisticalExport, exportDto,
                ProcBaseI18n.INSPECTION_COUNT_EXPORT_STATISTICAL, LogFunctionCodeConstant.INSPECTION_COUNT_EXPORT_CODE);
    }

    /**
     * ????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 18:51
     * @param abstractExport ???????????????
     * @param exportDto ??????dto
     * @param tableName ????????????
     * @param functionCode ?????????
     * @return ????????????
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

        //????????????
        procLogService.addLogByExport(exportDto, functionCode);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 15:48
     * @param beginTime ????????????
     * @param endTime ????????????
     * @return ??????????????????
     *
     */
    public List<ProcInfoDateStatistical> getSearchTimeDateCountNum(Long beginTime, Long endTime) {
        QueryProcCountSumReq req = new QueryProcCountSumReq();
        List<Long> timeList = new ArrayList<>();
        timeList.add(beginTime);
        timeList.add(endTime);
        req.setTimeList(timeList);
        //????????????????????????
        List<ProcInfoDateStatistical> inspectionDateInfoList = procStatisticalDao.queryProcInspectionListByNowDate(req);
        //????????????????????????
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
     * ??????????????????
     *
     * @param deviceInfoDto          ??????????????????
     * @param procRelatedDepartments ????????????????????????
     * @param procParamBase          ??????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/6/15 10:35
     */
    @Autowired
    private Test test;

    public void addInsertTest(List<ProcRelatedDevice> deviceInfoDto, List<ProcRelatedDepartment> procRelatedDepartments, ProcBase procParamBase) {
        int row = 300000;
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
        List<ProcBase> procBaseInfoList = procBaseDao.selectAllProcBase(new ProcBase());

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
                //????????????
                procInspection.setAccountabilityDept(department.getAccountabilityDept());
                procInspectionList.add(procInspection);
            } else {
                procClearFailure = new ProcClearFailure();
                BeanUtils.copyProperties(procBaseInfo, procClearFailure);
                if (ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(procClearFailure.getStatus())) {
                    procClearFailure.setIsCheckSingleBack(ProcBaseConstants.IS_CHECK_SINGLE_BACK);
                }
                //????????????
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
        //????????????????????????
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

        //????????????
        List<ProcRelatedDepartment> addDepartment2 = new ArrayList<>();
        for (int i = 0; i < addDepartment.size(); i++) {
            addDepartment2.add(addDepartment.get(i));
            if (addDepartment2.size() > 10000 || i+1== addDepartment.size()) {
                test.test2(addDepartment2);
                addDepartment2 = new ArrayList<>();
            }
        }

        //????????????
        /*List<ProcClearFailure> procClearFailures = new ArrayList<>();
        for (int i = 0; i < procClearFailureList.size(); i++ ) {
            procClearFailures.add(procClearFailureList.get(i));
            if (procClearFailures.size() > 10000 || i+1== procClearFailureList.size()) {
                test.test5(procClearFailures);
                procClearFailures = new ArrayList<>();
            }
        }*/

        //????????????
        /*List<ProcInspection> procInspectionList2 = new ArrayList<>();
        for (int i = 0; i < procInspectionList.size(); i++) {
            procInspectionList2.add(procInspectionList.get(i));
            if (procInspectionList2.size() > 10000 || i+1==  procInspectionList.size()) {
                test.test3(procInspectionList2);
                procInspectionList2 = new ArrayList<>();
            }
        }*/

        //??????????????????
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
     * ????????????????????????(??????????????????)
     */
    @Override
    public void insertTest() {
        List<ProcRelatedDevice> deviceInfoDto = new ArrayList<>();
        ProcRelatedDevice deviceInfoOne = new ProcRelatedDevice();
        deviceInfoOne.setDeviceId("0At8fvtc1kWZQNkhSaA");
        deviceInfoOne.setDeviceName("??????????????????1");
        deviceInfoOne.setDeviceType("030");
        deviceInfoOne.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoOne.setDeviceAreaName("??????????????????(?????????)11");
        deviceInfoDto.add(deviceInfoOne);

        ProcRelatedDevice deviceInfoTwo = new ProcRelatedDevice();
        deviceInfoTwo.setDeviceId("0At8fvtc1kWZQNkhSaB");
        deviceInfoTwo.setDeviceName("??????????????????2");
        deviceInfoTwo.setDeviceType("001");
        deviceInfoTwo.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoTwo.setDeviceAreaName("??????????????????(?????????)11");
        deviceInfoDto.add(deviceInfoTwo);

        ProcRelatedDevice deviceInfoThree = new ProcRelatedDevice();
        deviceInfoThree.setDeviceId("0At8fvtc1kWZQNkhSaD");
        deviceInfoThree.setDeviceName("??????????????????4");
        deviceInfoThree.setDeviceType("060");
        deviceInfoThree.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoThree.setDeviceAreaName("??????????????????(?????????)11");
        deviceInfoDto.add(deviceInfoThree);

        ProcRelatedDevice deviceInfoFour = new ProcRelatedDevice();
        deviceInfoFour.setDeviceId("0At8fvtc1kWZQNkhSaE");
        deviceInfoFour.setDeviceName("??????????????????5");
        deviceInfoFour.setDeviceType("030");
        deviceInfoFour.setDeviceAreaId("SVnAVIUNIJpF58L0SE1");
        deviceInfoFour.setDeviceAreaName("??????????????????(?????????)11");
        deviceInfoDto.add(deviceInfoFour);

        ProcRelatedDevice deviceInfoFive = new ProcRelatedDevice();
        deviceInfoFive.setDeviceId("0fdTi6epUxytPwWnPk6");
        deviceInfoFive.setDeviceName("Leyvi_xia02(??????????????????)");
        deviceInfoFive.setDeviceType("030");
        deviceInfoFive.setDeviceAreaId("d6TOcr9n9Cdk579Hlgr");
        deviceInfoFive.setDeviceAreaName("Leyvi_xia(??????)");
        deviceInfoDto.add(deviceInfoFive);

        ProcRelatedDevice deviceInfoSix = new ProcRelatedDevice();
        deviceInfoSix.setDeviceId("1B7OYZV4vA3KrwRQK22");
        deviceInfoSix.setDeviceName("dduduuduu");
        deviceInfoSix.setDeviceType("001");
        deviceInfoSix.setDeviceAreaId("p4RyBu9WdGo2x4zC5sQ");
        deviceInfoSix.setDeviceAreaName("?????????67???");
        deviceInfoDto.add(deviceInfoSix);

        ProcRelatedDevice deviceInfoSeven = new ProcRelatedDevice();
        deviceInfoSeven.setDeviceId("1rvnbPGqn81mFQgVBJA");
        deviceInfoSeven.setDeviceName("Leyvi_xia020(??????????????????)");
        deviceInfoSeven.setDeviceType("030");
        deviceInfoSeven.setDeviceAreaId("AVBnwYfRrzHlE04jLhl");
        deviceInfoSeven.setDeviceAreaName("Leyvi_001");
        deviceInfoDto.add(deviceInfoSeven);

        ProcRelatedDevice deviceInfoEight = new ProcRelatedDevice();
        deviceInfoEight.setDeviceId("2WJKjEC7PP1eUAfyh0Q");
        deviceInfoEight.setDeviceName("001");
        deviceInfoEight.setDeviceType("??????123456");
        deviceInfoEight.setDeviceAreaId("ZTXdWeJZMQmzxZJaVFE");
        deviceInfoEight.setDeviceAreaName("zhuting??????01");
        deviceInfoDto.add(deviceInfoEight);

        ProcRelatedDevice deviceInfoNine = new ProcRelatedDevice();
        deviceInfoNine.setDeviceId("5htjTOkYz48zLnugyJ7");
        deviceInfoNine.setDeviceName("????????????0003");
        deviceInfoNine.setDeviceType("090");
        deviceInfoNine.setDeviceAreaId("etzw2Wp0GnCKa2hP67I");
        deviceInfoNine.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoNine);

        ProcRelatedDevice deviceInfoTen = new ProcRelatedDevice();
        deviceInfoTen.setDeviceId("5jLMbUeKyMEeHoDHPsm");
        deviceInfoTen.setDeviceName("12313");
        deviceInfoTen.setDeviceType("001");
        deviceInfoTen.setDeviceAreaId("Z6MsT3WXhhsCJwgeiHL");
        deviceInfoTen.setDeviceAreaName("??????????????????");
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
        procBase.setRefAlarmName("??????");
        procBase.setRefAlarmCode("012");
        this.addInsertTest(deviceInfoDto, procRelatedDepartments, procBase);
    }

    /**
     * ??????????????????
     */
    @Override
    public void insertTestCustomData() {
        List<ProcRelatedDevice> deviceInfoDto = new ArrayList<>();
        ProcRelatedDevice deviceInfoOne = new ProcRelatedDevice();
        deviceInfoOne.setDeviceId("2hDnqiMkPeAnEDhRvYS");
        deviceInfoOne.setDeviceName("?????????????????????4");
        deviceInfoOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoOne.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoOne.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoOne);

        ProcRelatedDevice deviceInfoTwo = new ProcRelatedDevice();
        deviceInfoTwo.setDeviceId("3wKmehzBl9CW9kndqPN");
        deviceInfoTwo.setDeviceName("?????????????????????3");
        deviceInfoTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoTwo.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoTwo.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwo);

        ProcRelatedDevice deviceInfoThree = new ProcRelatedDevice();
        deviceInfoThree.setDeviceId("dc09hss0e2yxAgbVgFl");
        deviceInfoThree.setDeviceName("?????????????????????2");
        deviceInfoThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoThree.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoThree.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThree);

        ProcRelatedDevice deviceInfoFour = new ProcRelatedDevice();
        deviceInfoFour.setDeviceId("EUteMXhcU2L0762gxTr");
        deviceInfoFour.setDeviceName("?????????????????????9");
        deviceInfoFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoFour.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoFour.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFour);

        ProcRelatedDevice deviceInfoFive = new ProcRelatedDevice();
        deviceInfoFive.setDeviceId("GXfreV7zRF2rnPTOszy");
        deviceInfoFive.setDeviceName("?????????????????????6");
        deviceInfoFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoFive.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoFive.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFive);

        ProcRelatedDevice deviceInfoSix = new ProcRelatedDevice();
        deviceInfoSix.setDeviceId("MOjUBCKMOSDQjMuelW8");
        deviceInfoSix.setDeviceName("?????????????????????7");
        deviceInfoSix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoSix.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoSix.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoSix);

        ProcRelatedDevice deviceInfoSeven = new ProcRelatedDevice();
        deviceInfoSeven.setDeviceId("NOLTls1LyF31uTQDIbF");
        deviceInfoSeven.setDeviceName("?????????????????????5");
        deviceInfoSeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoSeven.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoSeven.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoSeven);

        ProcRelatedDevice deviceInfoEight = new ProcRelatedDevice();
        deviceInfoEight.setDeviceId("Qf2DhlLkboxgyXFrl6F");
        deviceInfoEight.setDeviceName("?????????????????????1");
        deviceInfoEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoEight.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoEight.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoEight);

        ProcRelatedDevice deviceInfoNine = new ProcRelatedDevice();
        deviceInfoNine.setDeviceId("rBcyHTq9a6Owch0EpJW");
        deviceInfoNine.setDeviceName("?????????????????????10");
        deviceInfoNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoNine.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoNine.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoNine);

        ProcRelatedDevice deviceInfoTen = new ProcRelatedDevice();
        deviceInfoTen.setDeviceId("L7y4nYJlMejQmwL2Pe9");
        deviceInfoTen.setDeviceName("?????????????????????8");
        deviceInfoTen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
        deviceInfoTen.setDeviceAreaId("wdRHbr6VwS0i5d2Iizm");
        deviceInfoTen.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTen);

        ProcRelatedDevice deviceInfoEleven = new ProcRelatedDevice();
        deviceInfoEleven.setDeviceId("3brhT9Yru1E5AkD0prc");
        deviceInfoEleven.setDeviceName("??????????????????7");
        deviceInfoEleven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoEleven.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoEleven.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoEleven);

        ProcRelatedDevice deviceInfoTwelve = new ProcRelatedDevice();
        deviceInfoTwelve.setDeviceId("9HphCuYC8pHYcc3ckag");
        deviceInfoTwelve.setDeviceName("??????????????????9");
        deviceInfoTwelve.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoTwelve.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoTwelve.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoTwelve);


        ProcRelatedDevice deviceInfoThirteen = new ProcRelatedDevice();
        deviceInfoThirteen.setDeviceId("9HWHTRbXcrfBFZnwYTC");
        deviceInfoThirteen.setDeviceName("??????????????????3");
        deviceInfoThirteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoThirteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoThirteen.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoThirteen);

        ProcRelatedDevice deviceInfoFourteen = new ProcRelatedDevice();
        deviceInfoFourteen.setDeviceId("B1Hewm1wHXRipI3YWer");
        deviceInfoFourteen.setDeviceName("??????????????????5");
        deviceInfoFourteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoFourteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoFourteen.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoFourteen);


        ProcRelatedDevice deviceInfoFifteen = new ProcRelatedDevice();
        deviceInfoFifteen.setDeviceId("BEMEG8yPjMpD5A1AkCb");
        deviceInfoFifteen.setDeviceName("??????????????????6");
        deviceInfoFifteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoFifteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoFifteen.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoFifteen);

        ProcRelatedDevice deviceInfoSixteen = new ProcRelatedDevice();
        deviceInfoSixteen.setDeviceId("GpSgTPYhy67Bx0zKfyT");
        deviceInfoSixteen.setDeviceName("??????????????????1");
        deviceInfoSixteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoSixteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoSixteen.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoSixteen);

        ProcRelatedDevice deviceInfoSeventeen = new ProcRelatedDevice();
        deviceInfoSeventeen.setDeviceId("mMKvaW6rCOvohtkxj3i");
        deviceInfoSeventeen.setDeviceName("??????????????????10");
        deviceInfoSeventeen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoSeventeen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoSeventeen.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoSeventeen);


        ProcRelatedDevice deviceInfoEighteen = new ProcRelatedDevice();
        deviceInfoEighteen.setDeviceId("nV4QITYyzTdcWbG7DK3");
        deviceInfoEighteen.setDeviceName("??????????????????8");
        deviceInfoEighteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoEighteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoEighteen.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoEighteen);

        ProcRelatedDevice deviceInfoNineteen = new ProcRelatedDevice();
        deviceInfoNineteen.setDeviceId("oAfyu6qOEFeX9krR8Pq");
        deviceInfoNineteen.setDeviceName("??????????????????2");
        deviceInfoNineteen.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoNineteen.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoNineteen.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoNineteen);

        ProcRelatedDevice deviceInfoTwenty = new ProcRelatedDevice();
        deviceInfoTwenty.setDeviceId("WSyuPxBcfineOFNbU8i");
        deviceInfoTwenty.setDeviceName("??????????????????4");
        deviceInfoTwenty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_030);
        deviceInfoTwenty.setDeviceAreaId("thapZ2AMzl7wqdXjei9");
        deviceInfoTwenty.setDeviceAreaName("??????????????????");
        deviceInfoDto.add(deviceInfoTwenty);

        ProcRelatedDevice deviceInfoTwentyOne = new ProcRelatedDevice();
        deviceInfoTwentyOne.setDeviceId("2wdfUdK1O4367n3WlZc");
        deviceInfoTwentyOne.setDeviceName("?????????????????????1");
        deviceInfoTwentyOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyOne.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyOne.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentyOne);

        ProcRelatedDevice deviceInfoTwentyTwo = new ProcRelatedDevice();
        deviceInfoTwentyTwo.setDeviceId("79fAXWZiO8Tyv7ymDWG");
        deviceInfoTwentyTwo.setDeviceName("?????????????????????10");
        deviceInfoTwentyTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyTwo.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyTwo.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentyTwo);

        ProcRelatedDevice deviceInfoTwentyThree = new ProcRelatedDevice();
        deviceInfoTwentyThree.setDeviceId("7xv1zktafvK6bQAuqcC");
        deviceInfoTwentyThree.setDeviceName("?????????????????????8");
        deviceInfoTwentyThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyThree.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyThree.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentyThree);

        ProcRelatedDevice deviceInfoTwentyFour = new ProcRelatedDevice();
        deviceInfoTwentyFour.setDeviceId("8QHpqM4mWIDDleaagjI");
        deviceInfoTwentyFour.setDeviceName("?????????????????????9");
        deviceInfoTwentyFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyFour.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyFour.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentyFour);

        ProcRelatedDevice deviceInfoTwentyFive = new ProcRelatedDevice();
        deviceInfoTwentyFive.setDeviceId("JhZgpNXa8vibm4w4ztr");
        deviceInfoTwentyFive.setDeviceName("?????????????????????2");
        deviceInfoTwentyFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyFive.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyFive.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentyFive);

        ProcRelatedDevice deviceInfoTwentySix = new ProcRelatedDevice();
        deviceInfoTwentySix.setDeviceId("MpLQ7Yorn7D1KC8HxFG");
        deviceInfoTwentySix.setDeviceName("?????????????????????6");
        deviceInfoTwentySix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentySix.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentySix.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentySix);

        ProcRelatedDevice deviceInfoTwentySeven = new ProcRelatedDevice();
        deviceInfoTwentySeven.setDeviceId("Qb69DVKLRl8yAEkwy4w");
        deviceInfoTwentySeven.setDeviceName("?????????????????????4");
        deviceInfoTwentySeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentySeven.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentySeven.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentySeven);

        ProcRelatedDevice deviceInfoTwentyEight = new ProcRelatedDevice();
        deviceInfoTwentyEight.setDeviceId("Su7kvkYiywViwu4ITRl");
        deviceInfoTwentyEight.setDeviceName("?????????????????????5");
        deviceInfoTwentyEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyEight.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyEight.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentyEight);

        ProcRelatedDevice deviceInfoTwentyNine = new ProcRelatedDevice();
        deviceInfoTwentyNine.setDeviceId("T9bKlpVlucVCKsPcj2I");
        deviceInfoTwentyNine.setDeviceName("?????????????????????7");
        deviceInfoTwentyNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoTwentyNine.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoTwentyNine.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoTwentyNine);

        ProcRelatedDevice deviceInfoThirty = new ProcRelatedDevice();
        deviceInfoThirty.setDeviceId("xzZ3ZTwhtgTo4GYyRx6");
        deviceInfoThirty.setDeviceName("?????????????????????3");
        deviceInfoThirty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_060);
        deviceInfoThirty.setDeviceAreaId("BFguCBkT2QpcOnDef0L");
        deviceInfoThirty.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirty);

        ProcRelatedDevice deviceInfoThirtyOne = new ProcRelatedDevice();
        deviceInfoThirtyOne.setDeviceId("5C7e9VyJrGfmZWz1tVC");
        deviceInfoThirtyOne.setDeviceName("?????????????????????7");
        deviceInfoThirtyOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyOne.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyOne.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtyOne);

        ProcRelatedDevice deviceInfoThirtyTwo = new ProcRelatedDevice();
        deviceInfoThirtyTwo.setDeviceId("8VmVx11ZFcvXz3kzQl9");
        deviceInfoThirtyTwo.setDeviceName("?????????????????????2");
        deviceInfoThirtyTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyTwo.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyTwo.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtyTwo);

        ProcRelatedDevice deviceInfoThirtyThree = new ProcRelatedDevice();
        deviceInfoThirtyThree.setDeviceId("ftpUwt0s8JhWnn4Mbkg");
        deviceInfoThirtyThree.setDeviceName("?????????????????????5");
        deviceInfoThirtyThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyThree.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyThree.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtyThree);

        ProcRelatedDevice deviceInfoThirtyFour = new ProcRelatedDevice();
        deviceInfoThirtyFour.setDeviceId("GrDrW8evl3xnVvKq4Gf");
        deviceInfoThirtyFour.setDeviceName("?????????????????????1");
        deviceInfoThirtyFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyFour.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyFour.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtyFour);

        ProcRelatedDevice deviceInfoThirtyFive = new ProcRelatedDevice();
        deviceInfoThirtyFive.setDeviceId("OAoxH1e380Njlc3o7WB");
        deviceInfoThirtyFive.setDeviceName("?????????????????????4");
        deviceInfoThirtyFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyFive.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyFive.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtyFive);

        ProcRelatedDevice deviceInfoThirtySix = new ProcRelatedDevice();
        deviceInfoThirtySix.setDeviceId("rNCyvz3HQ2pEhcd0xIh");
        deviceInfoThirtySix.setDeviceName("?????????????????????10");
        deviceInfoThirtySix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtySix.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtySix.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtySix);

        ProcRelatedDevice deviceInfoThirtySeven = new ProcRelatedDevice();
        deviceInfoThirtySeven.setDeviceId("RznuTwgCCm8ui9qB9ZX");
        deviceInfoThirtySeven.setDeviceName("?????????????????????9");
        deviceInfoThirtySeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtySeven.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtySeven.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtySeven);

        ProcRelatedDevice deviceInfoThirtyEight = new ProcRelatedDevice();
        deviceInfoThirtyEight.setDeviceId("uBkvCpkoMjjMJIqVgq5");
        deviceInfoThirtyEight.setDeviceName("?????????????????????3");
        deviceInfoThirtyEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyEight.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyEight.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtyEight);

        ProcRelatedDevice deviceInfoThirtyNine = new ProcRelatedDevice();
        deviceInfoThirtyNine.setDeviceId("vEstWmDwdJAjwqNpaY1");
        deviceInfoThirtyNine.setDeviceName("?????????????????????8");
        deviceInfoThirtyNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoThirtyNine.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoThirtyNine.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoThirtyNine);

        ProcRelatedDevice deviceInfoForty = new ProcRelatedDevice();
        deviceInfoForty.setDeviceId("xOTUYzQ82o6jd0pr4s7");
        deviceInfoForty.setDeviceName("?????????????????????6");
        deviceInfoForty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_090);
        deviceInfoForty.setDeviceAreaId("vhybjRi2wiNl5W0PX6o");
        deviceInfoForty.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoForty);


        ProcRelatedDevice deviceInfoFortyOne = new ProcRelatedDevice();
        deviceInfoFortyOne.setDeviceId("0hY4vZGGv55oTYyHuVz");
        deviceInfoFortyOne.setDeviceName("?????????????????????4");
        deviceInfoFortyOne.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyOne.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyOne.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortyOne);

        ProcRelatedDevice deviceInfoFortyTwo = new ProcRelatedDevice();
        deviceInfoFortyTwo.setDeviceId("4zv7vbwbQnIrEzzADyu");
        deviceInfoFortyTwo.setDeviceName("?????????????????????7");
        deviceInfoFortyTwo.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyTwo.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyTwo.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortyTwo);

        ProcRelatedDevice deviceInfoFortyThree = new ProcRelatedDevice();
        deviceInfoFortyThree.setDeviceId("8BApbiMbz09Qs7gf6j9");
        deviceInfoFortyThree.setDeviceName("?????????????????????3");
        deviceInfoFortyThree.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyThree.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyThree.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortyThree);


        ProcRelatedDevice deviceInfoFortyFour = new ProcRelatedDevice();
        deviceInfoFortyFour.setDeviceId("bXcDRiU8sbnhXV8V5ha");
        deviceInfoFortyFour.setDeviceName("?????????????????????9");
        deviceInfoFortyFour.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyFour.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyFour.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortyFour);


        ProcRelatedDevice deviceInfoFortyFive = new ProcRelatedDevice();
        deviceInfoFortyFive.setDeviceId("C0TEGCKF09Xyqka1j37");
        deviceInfoFortyFive.setDeviceName("?????????????????????2");
        deviceInfoFortyFive.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyFive.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyFive.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortyFive);


        ProcRelatedDevice deviceInfoFortySix = new ProcRelatedDevice();
        deviceInfoFortySix.setDeviceId("e74LPZbpTt8ro3SBrPI");
        deviceInfoFortySix.setDeviceName("?????????????????????5");
        deviceInfoFortySix.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortySix.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortySix.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortySix);

        ProcRelatedDevice deviceInfoFortySeven = new ProcRelatedDevice();
        deviceInfoFortySeven.setDeviceId("gPbncSEgunep1c52K75");
        deviceInfoFortySeven.setDeviceName("?????????????????????8");
        deviceInfoFortySeven.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortySeven.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortySeven.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortySeven);

        ProcRelatedDevice deviceInfoFortyEight = new ProcRelatedDevice();
        deviceInfoFortyEight.setDeviceId("KyxteZ5h0InVscTU1k2");
        deviceInfoFortyEight.setDeviceName("?????????????????????10");
        deviceInfoFortyEight.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyEight.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyEight.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortyEight);

        ProcRelatedDevice deviceInfoFortyNine = new ProcRelatedDevice();
        deviceInfoFortyNine.setDeviceId("qO6RTIpbxbLV3g0TDEM");
        deviceInfoFortyNine.setDeviceName("?????????????????????1");
        deviceInfoFortyNine.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFortyNine.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFortyNine.setDeviceAreaName("?????????????????????");
        deviceInfoDto.add(deviceInfoFortyNine);

        ProcRelatedDevice deviceInfoFifty = new ProcRelatedDevice();
        deviceInfoFifty.setDeviceId("xsRXwrN3esvtaMac4Ky");
        deviceInfoFifty.setDeviceName("?????????????????????6");
        deviceInfoFifty.setDeviceType(ProcBaseConstants.DEVICE_TYPE_210);
        deviceInfoFifty.setDeviceAreaId("EkEr8CIrxHNzPkEcvDV");
        deviceInfoFifty.setDeviceAreaName("?????????????????????");
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
        procBase.setRefAlarmName("????????????");
        procBase.setRefAlarmCode("violenceClose");
        this.addInsertTest(deviceInfoDto, procRelatedDepartments, procBase);
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     * @author hedongwei@wistronits.com
     * @date 2019/5/30 14:29
     */
    public ProcBaseReq getPermissionCondition() {
        QueryCondition<ProcBaseReq> procBaseReqCondition = new QueryCondition<ProcBaseReq>();
        procBaseReqCondition.setBizCondition(new ProcBaseReq());
        //?????????????????? ??? ??????????????????????????????
        ProcBaseReq procBaseReq = (ProcBaseReq) procBaseService.getPermissionsInfo(procBaseReqCondition).getBizCondition();
        return procBaseReq;
    }

    /**
     * ?????????????????????map
     *
     * @param areaIdList ????????????
     * @param dataList   ????????????
     * @return ?????????????????????map
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
     * ?????????????????????map
     *
     * @param areaIdList ????????????
     * @return ?????????????????????map
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
     * ????????????map
     *
     * @param voList ??????map
     * @return ??????map
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
     * ?????????????????????????????????
     *
     * @param deptList ????????????
     * @param deptMap  ??????map
     * @return ?????????????????????????????????
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
                //?????????
                statisticalVo.setDepartmentCount(0);
                voList.add(statisticalVo);
            }
        }
        return voList;
    }
}
