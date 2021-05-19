package com.fiberhome.filink.workflowbusinessserver.service.impl.statistical;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.export.*;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcDeviceTypeOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcErrorReasonOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcProcessingSchemeOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcStatusOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcStatusOverviewConstants;
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
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工单统计测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/30 9:09
 */
@RunWith(JMockit.class)
public class ProcStatisticalServiceTest {

    @Tested
    private ProcStatisticalServiceImpl procStatisticalService;

    @Injectable
    private ProcStatisticalDao procStatisticalDao;

    /**
     * 工单信息
     */
    @Injectable
    private ProcBaseService procBaseService;

    /**
     * 部门feign
     */
    @Injectable
    private DepartmentFeign departmentFeign;

    @Injectable
    private MongoTemplate mongoTemplate;

    @Injectable
    private ProcBaseDao procBaseDao;

    /**
     * 巡检记录service
     */
    @Injectable
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * 巡检工单service
     */
    @Injectable
    private ProcInspectionService procInspectionService;

    /**
     * 最大导出条数
     */
    @Injectable
    private Integer maxExportDataSize;

    /**
     * 工单状态导出类
     */
    @Injectable
    private ProcStatusStatisticalExport procStatusStatisticalExport;

    /**
     * 工单设施类型导出类
     */
    @Injectable
    private ProcDeviceTypeStatisticalExport procDeviceTypeStatisticalExport;

    /**
     * 工单异常原因导出类
     */
    @Injectable
    private ProcErrorReasonStatisticalExport procErrorReasonStatisticalExport;

    /**
     * 工单处理方案导出类
     */
    @Injectable
    private ProcProcessingSchemeStatisticalExport procProcessingSchemeStatisticalExport;

    /**
     * 工单区域比导出类
     */
    @Injectable
    private ProcAreaPercentStatisticalExport procAreaPercentStatisticalExport;

    /**
     * 工单设施统计数量导出类
     */
    @Injectable
    private ProcTopListStatisticalExport procTopListStatisticalExport;

    /**
     * 工单类型service
     */
    @Injectable
    private ProcLogService procLogService;

    /**
     * 用户feign
     */
    @Injectable
    private UserFeign userFeign;

    /**
     * 新增测试数据
     */
    @Injectable
    private AddProcData addProcData;


    /**
     * 调用统计类
     * @author hedongwei@wistronits.com
     * @date  2019/8/2 14:49
     */
    @Test
    public void callStatisticalClass() {
        ProcAreaPercentStatisticalExportBean areaExportBean = new ProcAreaPercentStatisticalExportBean();
        areaExportBean.getAreaName();
        areaExportBean.getAreaProcCount();
        areaExportBean.getAreaProcPercent();

        ProcDeviceTypeStatisticalExportBean deviceTypeBean = new ProcDeviceTypeStatisticalExportBean();
        deviceTypeBean.getAreaName();
        deviceTypeBean.getDistributionFrameCount();
        deviceTypeBean.getJunctionBoxCount();
        deviceTypeBean.getOpticalBoxCount();
        deviceTypeBean.getOutdoorCabinetCount();
        deviceTypeBean.getWellCount();

        ProcErrorReasonStatisticalExportBean errorReasonBean = new ProcErrorReasonStatisticalExportBean();
        errorReasonBean.getAreaName();
        errorReasonBean.getClearFailureCount();
        errorReasonBean.getFailureCount();
        errorReasonBean.getOtherCount();
        errorReasonBean.getRodeWorkCount();
        errorReasonBean.getStealWearCount();

        ProcProcessingSchemeStatisticalExportBean processingSchemeBean = new ProcProcessingSchemeStatisticalExportBean();
        processingSchemeBean.getAreaName();
        processingSchemeBean.getLiveCount();
        processingSchemeBean.getOtherCount();
        processingSchemeBean.getRepairCount();

        ProcStatusStatisticalExportBean statusBean = new ProcStatusStatisticalExportBean();
        statusBean.getAreaName();
        statusBean.getAssignedCount();
        statusBean.getCompletedCount();
        statusBean.getPendingCount();
        statusBean.getProcessingCount();
        statusBean.getSingleBackCount();
        statusBean.getTurnProcessingCount();

        ProcTopListStatisticalExportBean topBean = new ProcTopListStatisticalExportBean();
        topBean.getAccountabilityUnitName();
        topBean.getAddress();
        topBean.getAreaName();
        topBean.getDeviceName();
        topBean.getRanking();
        topBean.getStatus();
    }

    /**
     * 查询工单状态统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 9:45
     */
    @Test
    public void queryListProcGroupByProcStatus() {
        QueryCondition<QueryListProcGroupByProcStatusReq> reqInfo = new QueryCondition<>();

        try {
            procStatisticalService.queryListProcGroupByProcStatus(reqInfo);
        } catch (Exception e) {

        }

        QueryListProcGroupByProcStatusReq  req = new QueryListProcGroupByProcStatusReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryListProcGroupByProcStatus(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);


        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionGroupByProcStatus((QueryCondition) any);
                List<ProcStatusStatistical> procStatusList = new ArrayList<>();
                ProcStatusStatistical procStatusStatistical = new ProcStatusStatistical();
                procStatusStatistical.setStatus("processing");
                procStatusStatistical.setAreaId("1");
                procStatusStatistical.setAreaName("1");
                procStatusStatistical.setOrderCount(1);
                procStatusList.add(procStatusStatistical);
                result = procStatusList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByProcStatus(reqInfo);
        } catch (Exception e) {

        }

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcClearGroupByProcStatus((QueryCondition) any);
                List<ProcStatusStatistical> procStatusList = new ArrayList<>();
                ProcStatusStatistical procStatusStatistical = new ProcStatusStatistical();
                procStatusStatistical.setStatus("processing");
                procStatusStatistical.setAreaId("1");
                procStatusStatistical.setAreaName("1");
                procStatusStatistical.setOrderCount(1);
                procStatusList.add(procStatusStatistical);
                result = procStatusList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByProcStatus(reqInfo);
        } catch (Exception e) {

        }
    }

    /**
     * 查询工单状态概览
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 10:45
     */
    @Test
    public void queryListProcOverviewGroupByProcStatus() {
        QueryCondition<QueryListProcOverviewGroupByProcStatusReq> reqQueryCondition = new QueryCondition<>();
        QueryListProcOverviewGroupByProcStatusReq req = new QueryListProcOverviewGroupByProcStatusReq();
        req.setStatisticalType(ProcStatusOverviewConstants.STATISTICAL_TYPE_1);
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqQueryCondition.setBizCondition(req);
        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionOverviewGroupByProcStatus((QueryCondition<QueryListProcOverviewGroupByProcStatusReq>) any);
                List<ProcStatusOverviewStatistical> procStatusList = new ArrayList<>();
                ProcStatusOverviewStatistical procStatusOverviewStatistical = new ProcStatusOverviewStatistical();
                procStatusOverviewStatistical.setStatus("processing");
                procStatusOverviewStatistical.setOrderCount(1);
                procStatusList.add(procStatusOverviewStatistical);
                procStatusOverviewStatistical = new ProcStatusOverviewStatistical();
                procStatusOverviewStatistical.setStatus("singleBack");
                procStatusOverviewStatistical.setOrderCount(1);
                procStatusList.add(procStatusOverviewStatistical);
                result = procStatusList;
            }
        };

        procStatisticalService.queryListProcOverviewGroupByProcStatus(reqQueryCondition);


        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqQueryCondition.setBizCondition(req);
        new Expectations() {
            {
                procStatisticalDao.queryListProcClearOverviewGroupByProcStatus((QueryCondition<QueryListProcOverviewGroupByProcStatusReq>) any);
                List<ProcStatusOverviewStatistical> procStatusList = new ArrayList<>();
                ProcStatusOverviewStatistical procStatusOverviewStatistical = new ProcStatusOverviewStatistical();
                procStatusOverviewStatistical.setStatus("processing");
                procStatusOverviewStatistical.setOrderCount(1);
                procStatusList.add(procStatusOverviewStatistical);
                procStatusOverviewStatistical = new ProcStatusOverviewStatistical();
                procStatusOverviewStatistical.setStatus("singleBack");
                procStatusOverviewStatistical.setOrderCount(1);
                procStatusList.add(procStatusOverviewStatistical);
                result = procStatusList;
            }
        };

        procStatisticalService.queryListProcOverviewGroupByProcStatus(reqQueryCondition);
    }


    /**
     * 查询当前日期新增工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 11:12
     */
    @Test
    public void queryNowDateAddOrderCount() {
        QueryCondition<QueryNowDateAddOrderCountReq> reqInfo = new QueryCondition<>();
        QueryNowDateAddOrderCountReq req = new QueryNowDateAddOrderCountReq();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);
        new Expectations() {
            {
                procStatisticalDao.queryNowDateClearAddOrderCount((QueryCondition<QueryNowDateAddOrderCountReq>) any);
                result = 1;
            }
        };
        procStatisticalService.queryNowDateAddOrderCount(reqInfo);

        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        new Expectations() {
            {
                procStatisticalDao.queryNowDateInspectionAddOrderCount((QueryCondition<QueryNowDateAddOrderCountReq>) any);
                result = 1;
            }
        };
        procStatisticalService.queryNowDateAddOrderCount(reqInfo);

    }

    /**
     * 查询工单处理方案统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 11:14
     */
    @Test
    public void queryListProcGroupByProcProcessingScheme() {
        QueryCondition<QueryListProcGroupByProcProcessingSchemeReq> reqInfo = new QueryCondition<>();


        try {
            procStatisticalService.queryListProcGroupByProcProcessingScheme(reqInfo);
        } catch (Exception e) {

        }

        QueryListProcGroupByProcProcessingSchemeReq req = new QueryListProcGroupByProcProcessingSchemeReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryListProcGroupByProcProcessingScheme(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);


        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionGroupByProcProcessingScheme((QueryCondition) any);
                List<ProcProcessingSchemeStatistical> procProcessingSchemeStatisticalList = new ArrayList<>();
                ProcProcessingSchemeStatistical procProcessingSchemeStatistical = new ProcProcessingSchemeStatistical();
                procProcessingSchemeStatistical.setProcessingScheme("1");
                procProcessingSchemeStatistical.setAreaId("1");
                procProcessingSchemeStatistical.setAreaName("1");
                procProcessingSchemeStatistical.setOrderCount(1);
                procProcessingSchemeStatisticalList.add(procProcessingSchemeStatistical);
                result = procProcessingSchemeStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByProcProcessingScheme(reqInfo);
        } catch (Exception e) {

        }

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcClearGroupByProcProcessingScheme((QueryCondition) any);
                List<ProcProcessingSchemeStatistical> procProcessingSchemeStatisticalList = new ArrayList<>();
                ProcProcessingSchemeStatistical procProcessingSchemeStatistical = new ProcProcessingSchemeStatistical();
                procProcessingSchemeStatistical.setProcessingScheme("1");
                procProcessingSchemeStatistical.setAreaId("1");
                procProcessingSchemeStatistical.setAreaName("1");
                procProcessingSchemeStatistical.setOrderCount(1);
                procProcessingSchemeStatisticalList.add(procProcessingSchemeStatistical);
                result = procProcessingSchemeStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByProcProcessingScheme(reqInfo);
        } catch (Exception e) {

        }
    }

    /**
     * 查询处理方案概览统计
     * @author hedongwei@wistronits.com
     * @date 2019/6/4 19:11
     */
    @Test
    public void queryListProcOverviewGroupByProcProcessingScheme() {
        QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> queryCondition = new QueryCondition<>();
        QueryListProcOverviewGroupByProcessingSchemeReq req = new QueryListProcOverviewGroupByProcessingSchemeReq();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        queryCondition.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcClearOverviewGroupByProcProcessingScheme((QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq>) any);
                List<ProcProcessingSchemeOverviewStatistical> schemeOverviewList = new ArrayList<>();
                ProcProcessingSchemeOverviewStatistical schemeOverviewStatistical = new ProcProcessingSchemeOverviewStatistical();
                schemeOverviewStatistical.setProcessingScheme("1");
                schemeOverviewStatistical.setOrderCount(1);
                schemeOverviewList.add(schemeOverviewStatistical);
                result = schemeOverviewList;
            }
        };



        procStatisticalService.queryListProcOverviewGroupByProcProcessingScheme(queryCondition);
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);

        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionOverviewGroupByProcProcessingScheme((QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq>) any);
                List<ProcProcessingSchemeOverviewStatistical> schemeOverviewList = new ArrayList<>();
                ProcProcessingSchemeOverviewStatistical schemeOverviewStatistical = new ProcProcessingSchemeOverviewStatistical();
                schemeOverviewStatistical.setProcessingScheme("1");
                schemeOverviewStatistical.setOrderCount(1);
                schemeOverviewList.add(schemeOverviewStatistical);
                result = schemeOverviewList;
            }
        };

        procStatisticalService.queryListProcOverviewGroupByProcProcessingScheme(queryCondition);
    }

    /**
     * 查询工单故障原因统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 9:34
     */
    @Test
    public void queryListProcGroupByErrorReason() {
        QueryCondition<QueryListProcGroupByProcErrorReasonReq> reqInfo = new QueryCondition<>();

        try {
            procStatisticalService.queryListProcGroupByErrorReason(reqInfo);
        } catch (Exception e) {

        }

        QueryListProcGroupByProcErrorReasonReq req = new QueryListProcGroupByProcErrorReasonReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryListProcGroupByErrorReason(reqInfo);
        } catch (Exception e) {

        }

        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);


        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionGroupByErrorReason((QueryCondition<QueryListProcGroupByProcErrorReasonReq>) any);
                List<ProcErrorReasonStatistical> procErrorReasonStatisticalList = new ArrayList<>();
                ProcErrorReasonStatistical procErrorReasonStatistical = new ProcErrorReasonStatistical();
                procErrorReasonStatistical.setErrorReason("1");
                procErrorReasonStatistical.setErrorReasonName("errorName");
                procErrorReasonStatistical.setAreaId("1");
                procErrorReasonStatistical.setAreaName("1");
                procErrorReasonStatistical.setOrderCount(1);
                procErrorReasonStatisticalList.add(procErrorReasonStatistical);
                result = procErrorReasonStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByErrorReason(reqInfo);
        } catch (Exception e) {

        }

        req = reqInfo.getBizCondition();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcClearGroupByErrorReason((QueryCondition<QueryListProcGroupByProcErrorReasonReq>) any);
                List<ProcErrorReasonStatistical> procErrorReasonStatisticalList = new ArrayList<>();
                ProcErrorReasonStatistical procErrorReasonStatistical = new ProcErrorReasonStatistical();
                procErrorReasonStatistical.setErrorReason("1");
                procErrorReasonStatistical.setErrorReasonName("errorName");
                procErrorReasonStatistical.setAreaId("1");
                procErrorReasonStatistical.setAreaName("1");
                procErrorReasonStatistical.setOrderCount(1);
                procErrorReasonStatisticalList.add(procErrorReasonStatistical);
                result = procErrorReasonStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByErrorReason(reqInfo);
        } catch (Exception e) {

        }

    }

    /**
     * 故障原因概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 10:07
     */
    @Test
    public void queryListProcOverviewGroupByProcErrorReason() {
        QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> queryCondition = new QueryCondition<>();
        QueryListProcOverviewGroupByErrorReasonReq req = new QueryListProcOverviewGroupByErrorReasonReq();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        queryCondition.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcClearOverviewGroupByErrorReason((QueryCondition<QueryListProcOverviewGroupByErrorReasonReq>) any);
                List<ProcErrorReasonOverviewStatistical> errorReasonOverviewList = new ArrayList<>();
                ProcErrorReasonOverviewStatistical procErrorReasonOverviewStatistical = new ProcErrorReasonOverviewStatistical();
                procErrorReasonOverviewStatistical.setErrorReason("1");
                procErrorReasonOverviewStatistical.setOrderCount(1);
                errorReasonOverviewList.add(procErrorReasonOverviewStatistical);
                result = errorReasonOverviewList;
            }
        };



        procStatisticalService.queryListProcOverviewGroupByProcErrorReason(queryCondition);
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);

        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionOverviewGroupByErrorReason((QueryCondition<QueryListProcOverviewGroupByErrorReasonReq>) any);
                List<ProcErrorReasonOverviewStatistical> errorReasonOverviewList = new ArrayList<>();
                ProcErrorReasonOverviewStatistical procErrorReasonOverviewStatistical = new ProcErrorReasonOverviewStatistical();
                procErrorReasonOverviewStatistical.setErrorReason("1");
                procErrorReasonOverviewStatistical.setOrderCount(1);
                errorReasonOverviewList.add(procErrorReasonOverviewStatistical);
                result = errorReasonOverviewList;
            }
        };

        procStatisticalService.queryListProcOverviewGroupByProcErrorReason(queryCondition);
    }

    /**
     * 查询设施类型统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 10:17
     */
    @Test
    public void queryListProcGroupByDeviceType() {
        QueryCondition<QueryListProcGroupByProcDeviceTypeReq> reqInfo = new QueryCondition<>();
        try {
            procStatisticalService.queryListProcGroupByDeviceType(reqInfo);
        } catch (Exception e) {
        }

        QueryListProcGroupByProcDeviceTypeReq req = new QueryListProcGroupByProcDeviceTypeReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryListProcGroupByDeviceType(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionGroupByDeviceType((QueryCondition<QueryListProcGroupByProcDeviceTypeReq>) any);
                List<ProcDeviceTypeStatistical> ProcDeviceTypeStatisticalList = new ArrayList<>();
                ProcDeviceTypeStatistical procDeviceTypeStatistical = new ProcDeviceTypeStatistical();
                procDeviceTypeStatistical.setDeviceType("060");
                procDeviceTypeStatistical.setAreaId("1");
                procDeviceTypeStatistical.setAreaName("1");
                procDeviceTypeStatistical.setOrderCount(1);
                ProcDeviceTypeStatisticalList.add(procDeviceTypeStatistical);
                result = ProcDeviceTypeStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByDeviceType(reqInfo);
        } catch (Exception e) {

        }

        req = reqInfo.getBizCondition();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);
        new Expectations() {
            {
                procStatisticalDao.queryListProcClearGroupByDeviceType((QueryCondition<QueryListProcGroupByProcDeviceTypeReq>) any);
                List<ProcDeviceTypeStatistical> ProcDeviceTypeStatisticalList = new ArrayList<>();
                ProcDeviceTypeStatistical procDeviceTypeStatistical = new ProcDeviceTypeStatistical();
                procDeviceTypeStatistical.setDeviceType("060");
                procDeviceTypeStatistical.setAreaId("1");
                procDeviceTypeStatistical.setAreaName("1");
                procDeviceTypeStatistical.setOrderCount(1);
                ProcDeviceTypeStatisticalList.add(procDeviceTypeStatistical);
                result = ProcDeviceTypeStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListProcGroupByDeviceType(reqInfo);
        } catch (Exception e) {

        }
    }


    /**
     * 设施类型概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 10:38
     */
    @Test
    public void queryListProcOverviewGroupByProcDeviceType() {
        QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> reqInfo = new QueryCondition<>();
        QueryListProcOverviewGroupByDeviceTypeReq req = new QueryListProcOverviewGroupByDeviceTypeReq();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcClearOverviewGroupByDeviceTypeList((QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq>) any);
                List<ProcDeviceTypeOverviewStatistical> deviceTypeOverviewList = new ArrayList<>();
                ProcDeviceTypeOverviewStatistical deviceTypeOverviewStatistical = new ProcDeviceTypeOverviewStatistical();
                deviceTypeOverviewStatistical.setDeviceType("060");
                deviceTypeOverviewStatistical.setOrderCount(1);
                deviceTypeOverviewList.add(deviceTypeOverviewStatistical);
                result = deviceTypeOverviewList;
            }
        };
        procStatisticalService.queryListProcOverviewGroupByProcDeviceType(reqInfo);


        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionOverviewGroupByDeviceTypeList((QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq>) any);
                List<ProcDeviceTypeOverviewStatistical> deviceTypeOverviewList = new ArrayList<>();
                ProcDeviceTypeOverviewStatistical deviceTypeOverviewStatistical = new ProcDeviceTypeOverviewStatistical();
                deviceTypeOverviewStatistical.setDeviceType("060");
                deviceTypeOverviewStatistical.setOrderCount(1);
                deviceTypeOverviewList.add(deviceTypeOverviewStatistical);
                result = deviceTypeOverviewList;
            }
        };
        procStatisticalService.queryListProcOverviewGroupByProcDeviceType(reqInfo);
    }


    /**
     * 查询工单区域比信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 10:55
     */
    @Test
    public void queryListProcGroupByAreaPercent() {
        QueryCondition<QueryListProcGroupByProcAreaPercentReq> reqInfo = new QueryCondition<>();

        try {
            procStatisticalService.queryListProcGroupByAreaPercent(reqInfo);
        } catch (Exception e) {

        }

        QueryListProcGroupByProcAreaPercentReq req = new QueryListProcGroupByProcAreaPercentReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryListProcGroupByAreaPercent(reqInfo);
        } catch (Exception e) {

        }

        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryListProcInspectionGroupByAreaPercent((QueryCondition<QueryListProcGroupByProcAreaPercentReq>) any);
                List<ProcAreaPercentStatistical> procAreaPercentStatisticalList = new ArrayList<>();
                ProcAreaPercentStatistical procAreaPercentStatistical = new ProcAreaPercentStatistical();
                procAreaPercentStatistical.setAreaId("1");
                procAreaPercentStatistical.setAreaName("areaName");
                procAreaPercentStatistical.setOrderCount(1);
                procAreaPercentStatisticalList.add(procAreaPercentStatistical);
                result = procAreaPercentStatisticalList;
            }
        };

        procStatisticalService.queryListProcGroupByAreaPercent(reqInfo);


        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);
        new Expectations() {
            {
                procStatisticalDao.queryListProcClearGroupByAreaPercent((QueryCondition<QueryListProcGroupByProcAreaPercentReq>) any);
                List<ProcAreaPercentStatistical> procAreaPercentStatisticalList = new ArrayList<>();
                ProcAreaPercentStatistical procAreaPercentStatistical = new ProcAreaPercentStatistical();
                procAreaPercentStatistical.setAreaId("1");
                procAreaPercentStatistical.setAreaName("areaName");
                procAreaPercentStatistical.setOrderCount(1);
                procAreaPercentStatisticalList.add(procAreaPercentStatistical);
                result = procAreaPercentStatisticalList;
            }
        };

        procStatisticalService.queryListProcGroupByAreaPercent(reqInfo);
    }



    /**
     * 查询部门分组集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 11:04
     */
    @Test
    public void queryDeptListGroupByAccountabilityDept() {
        QueryCondition<QueryDeptListGroupByAccountabilityDeptReq> reqInfo = new QueryCondition<>();

        try {
            procStatisticalService.queryDeptListGroupByAccountabilityDept(reqInfo);
        } catch (Exception e) {

        }

        QueryDeptListGroupByAccountabilityDeptReq req = new QueryDeptListGroupByAccountabilityDeptReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryDeptListGroupByAccountabilityDept(reqInfo);
        } catch (Exception e) {

        }



        List<String> accountabilityDeptList = new ArrayList<>();
        accountabilityDeptList.add("1");
        req.setAccountabilityDeptList(accountabilityDeptList);
        reqInfo.setBizCondition(req);
        procStatisticalService.queryDeptListGroupByAccountabilityDept(reqInfo);

        new Expectations() {
            {
                procStatisticalDao.queryInspectionDeptListGroupByAccountabilityDept((QueryCondition<QueryDeptListGroupByAccountabilityDeptReq>) any);
                List<ProcDepartmentStatistical> procDepartmentStatisticalList = new ArrayList<>();
                ProcDepartmentStatistical procDepartmentStatistical = new ProcDepartmentStatistical();
                procDepartmentStatistical.setDepartmentId("1");
                procDepartmentStatistical.setDepartmentCount(1);
                procDepartmentStatisticalList.add(procDepartmentStatistical);
                result = procDepartmentStatisticalList;
            }
        };

        procStatisticalService.queryDeptListGroupByAccountabilityDept(reqInfo);

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);

        new Expectations() {
            {
                procStatisticalDao.queryClearDeptListGroupByAccountabilityDept((QueryCondition<QueryDeptListGroupByAccountabilityDeptReq>) any);
                List<ProcDepartmentStatistical> procDepartmentStatisticalList = new ArrayList<>();
                ProcDepartmentStatistical procDepartmentStatistical = new ProcDepartmentStatistical();
                procDepartmentStatistical.setDepartmentId("1");
                procDepartmentStatistical.setDepartmentCount(1);
                procDepartmentStatisticalList.add(procDepartmentStatistical);
                result = procDepartmentStatisticalList;
            }
        };

        procStatisticalService.queryDeptListGroupByAccountabilityDept(reqInfo);

    }

    /**
     * 查询工单前n位的设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 13:15
     */
    @Test
    public void queryListDeviceCountGroupByDevice() {
        QueryCondition<QueryTopListProcGroupByProcDeviceReq> reqInfo = new QueryCondition<>();
        try {
            procStatisticalService.queryListDeviceCountGroupByDevice(reqInfo);
        } catch (Exception e) {

        }

        QueryTopListProcGroupByProcDeviceReq req = new QueryTopListProcGroupByProcDeviceReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryListDeviceCountGroupByDevice(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);


        new Expectations() {
            {
                procStatisticalDao.queryInspectionListDeviceCountGroupByDevice((QueryCondition<QueryTopListProcGroupByProcDeviceReq>) any);
                List<ProcDeviceTopStatistical> procDeviceTopStatisticalList = new ArrayList<>();
                ProcDeviceTopStatistical procDeviceTopStatistical = new ProcDeviceTopStatistical();
                procDeviceTopStatistical.setAreaId("1");
                procDeviceTopStatistical.setAreaName("areaName");
                procDeviceTopStatistical.setDeviceId("1");
                procDeviceTopStatistical.setDeviceName("deviceName");
                procDeviceTopStatistical.setDeviceCount(1);
                procDeviceTopStatisticalList.add(procDeviceTopStatistical);
                result = procDeviceTopStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListDeviceCountGroupByDevice(reqInfo);
        } catch (Exception e) {

        }

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        reqInfo.setBizCondition(req);
        new Expectations() {
            {
                procStatisticalDao.queryClearListDeviceCountGroupByDevice((QueryCondition<QueryTopListProcGroupByProcDeviceReq>) any);
                List<ProcDeviceTopStatistical> procDeviceTopStatisticalList = new ArrayList<>();
                ProcDeviceTopStatistical procDeviceTopStatistical = new ProcDeviceTopStatistical();
                procDeviceTopStatistical.setAreaId("1");
                procDeviceTopStatistical.setAreaName("areaName");
                procDeviceTopStatistical.setDeviceId("1");
                procDeviceTopStatistical.setDeviceName("deviceName");
                procDeviceTopStatistical.setDeviceCount(1);
                procDeviceTopStatisticalList.add(procDeviceTopStatistical);
                result = procDeviceTopStatisticalList;
            }
        };

        try {
            procStatisticalService.queryListDeviceCountGroupByDevice(reqInfo);
        } catch (Exception e) {

        }
    }



    /**
     * 查询工单新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 13:30
     */
    @Test
    public void queryProcAddListCountGroupByDay() {
        QueryCondition<QueryProcCountByTimeReq> reqInfo = new QueryCondition<>();
        try {
            procStatisticalService.queryProcAddListCountGroupByDay(reqInfo);
        } catch (Exception e) {

        }


        QueryProcCountByTimeReq req = new QueryProcCountByTimeReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryProcAddListCountGroupByDay(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);

        List<Long> timeLongList = new ArrayList<>();
        timeLongList.add(1L);
        timeLongList.add(2L);
        req.setTimeList(timeLongList);
        reqInfo.setBizCondition(req);

        this.getPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByDay(reqInfo);

        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByDay(reqInfo);
    }



    /**
     * 查询工单新增周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:02
     */
    @Test
    public void queryProcAddListCountGroupByWeek() {
        QueryCondition<QueryProcCountByWeekReq> reqInfo = new QueryCondition<>();
        try {
            procStatisticalService.queryProcAddListCountGroupByWeek(reqInfo);
        } catch (Exception e) {

        }

        QueryProcCountByWeekReq req = new QueryProcCountByWeekReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryProcAddListCountGroupByWeek(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);

        List<Long> timeLongList = new ArrayList<>();
        timeLongList.add(1L);
        timeLongList.add(2L);
        req.setTimeList(timeLongList);
        reqInfo.setBizCondition(req);
        this.getPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByWeek(reqInfo);

        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByWeek(reqInfo);
    }


    /**
     * 查询工单新增月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:08
     */
    @Test
    public void queryProcAddListCountGroupByMonth() {
        QueryCondition<QueryProcCountByMonthReq> reqInfo = new QueryCondition<>();
        try {
            procStatisticalService.queryProcAddListCountGroupByMonth(reqInfo);
        } catch (Exception e) {

        }

        QueryProcCountByMonthReq req = new QueryProcCountByMonthReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryProcAddListCountGroupByMonth(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);

        List<Long> timeLongList = new ArrayList<>();
        timeLongList.add(1L);
        timeLongList.add(2L);
        req.setTimeList(timeLongList);
        reqInfo.setBizCondition(req);
        this.getPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByMonth(reqInfo);

        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByMonth(reqInfo);
    }


    /**
     * 查询工单新增年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:08
     */
    @Test
    public void queryProcAddListCountGroupByYear() {
        QueryCondition<QueryProcCountByYearReq> reqInfo = new QueryCondition<>();
        try {
            procStatisticalService.queryProcAddListCountGroupByYear(reqInfo);
        } catch (Exception e) {

        }

        QueryProcCountByYearReq req = new QueryProcCountByYearReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        reqInfo.setBizCondition(req);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        reqInfo.setPageCondition(pageCondition);

        try {
            procStatisticalService.queryProcAddListCountGroupByYear(reqInfo);
        } catch (Exception e) {

        }


        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        req.setAreaIdList(areaIdList);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("1");
        req.setDeviceTypeList(deviceTypeList);
        reqInfo.setBizCondition(req);

        List<Long> timeLongList = new ArrayList<>();
        timeLongList.add(1L);
        timeLongList.add(2L);
        req.setTimeList(timeLongList);
        reqInfo.setBizCondition(req);
        this.getPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByYear(reqInfo);

        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryProcAddListCountGroupByYear(reqInfo);
    }


    /**
     * 查询工单新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:13
     */
    @Test
    public void queryHomeProcAddListCountGroupByDay() {
        QueryCondition<QueryHomeProcCountByTimeReq> reqInfo = new QueryCondition<>();
        reqInfo.setBizCondition(new QueryHomeProcCountByTimeReq());
        this.getPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByDay(reqInfo);


        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByDay(reqInfo);
    }

    /**
     * 查询工单新增周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:18
     */
    @Test
    public void queryHomeProcAddListCountGroupByWeek() {
        QueryCondition<QueryHomeProcCountByWeekReq> reqInfo = new QueryCondition<>();
        reqInfo.setBizCondition(new QueryHomeProcCountByWeekReq());
        this.getPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByWeek(reqInfo);

        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByWeek(reqInfo);
    }

    /**
     * 查询工单新增月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:18
     */
    @Test
    public void queryHomeProcAddListCountGroupByMonth() {
        QueryCondition<QueryHomeProcCountByMonthReq> reqInfo = new QueryCondition<>();
        reqInfo.setBizCondition(new QueryHomeProcCountByMonthReq());
        this.getPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByMonth(reqInfo);

        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByMonth(reqInfo);
    }

    /**
     * 查询工单新增年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:18
     */
    @Test
    public void queryHomeProcAddListCountGroupByYear() {
        QueryCondition<QueryHomeProcCountByYearReq> reqInfo = new QueryCondition<>();
        reqInfo.setBizCondition(new QueryHomeProcCountByYearReq());
        this.getPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByYear(reqInfo);

        this.getAddDeptPermissionsInfo();
        procStatisticalService.queryHomeProcAddListCountGroupByYear(reqInfo);
    }


    /**
     * 设置每一天统计到工单数量统计表中
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:26
     */
    @Test
    public void setProcCountSumToProcAddCountTable() {
        this.commonStatisticalInsertData();
        procStatisticalService.setProcCountSumToProcAddCountTable();
    }


    /**
     * 统计每周工单增量到周工单增量表中
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:35
     */
    @Test
    public void setProcWeekCountSumToProcAddCountTable() {
        this.commonStatisticalInsertData();
        procStatisticalService.setProcWeekCountSumToProcAddCountTable();
    }

    /**
     * 统计每月工单增量到周工单增量表中
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:35
     */
    @Test
    public void setProcMonthCountSumToProcAddCountTable() {
        this.commonStatisticalInsertData();
        procStatisticalService.setProcMonthCountSumToProcAddCountTable();
    }

    /**
     * 统计每年工单增量到周工单增量表中
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:35
     */
    @Test
    public void setProcYearCountSumToProcAddCountTable() {
        this.commonStatisticalInsertData();
        procStatisticalService.setProcYearCountSumToProcAddCountTable();
    }

    
    /**
     * 销障工单状态统计导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:40
     */
    @Test
    public void procClearStatusStatisticalExport() {
        ExportDto<ProcStatusStatisticalExportBean> exportDto = new ExportDto<>();
        new Expectations() {
            {
                procStatusStatisticalExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };

        try  {
            procStatisticalService.procClearStatusStatisticalExport(exportDto);
        } catch (Exception e) {

        }



        new Expectations() {
            {
                procStatusStatisticalExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportDataTooLargeException("1");
            }
        };


        try  {
            procStatisticalService.procClearStatusStatisticalExport(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procStatusStatisticalExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };

        try  {
            procStatisticalService.procClearStatusStatisticalExport(exportDto);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                procStatusStatisticalExport.insertTask(exportDto, anyString, anyString);
                result = new Exception();
            }
        };

        try  {
            procInspectionService.exportListInspectionProcess(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procStatusStatisticalExport.insertTask(exportDto, anyString, anyString);
                result = null;
            }
        };

        new Expectations() {
            {
                procStatusStatisticalExport.exportData((ExportRequestInfo) any);
            }
        };


        new Expectations() {
            {
                procLogService.addLogByExport((ExportDto) any, anyString);
            }
        };

        try  {
            procStatisticalService.procClearStatusStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * 销障工单处理方案导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:50
     */
    @Test
    public void procClearProcessingSchemeStatisticalExport() {
        ExportDto<ProcProcessingSchemeStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procClearProcessingSchemeStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * 销障工单故障原因导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procClearErrorReasonStatisticalExport() {
        ExportDto<ProcErrorReasonStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procClearErrorReasonStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * 销障工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procClearAreaPercentStatisticalExport() {
        ExportDto<ProcAreaPercentStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procClearAreaPercentStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * 巡检工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procInspectionStatusStatisticalExport() {
        ExportDto<ProcStatusStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procInspectionStatusStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * 销障工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procClearDeviceTypeStatisticalExport() {
        ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procClearDeviceTypeStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * 巡检工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procInspectionDeviceTypeStatisticalExport() {
        ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procInspectionDeviceTypeStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * 巡检工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procInspectionAreaPercentStatisticalExport() {
        ExportDto<ProcAreaPercentStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procInspectionAreaPercentStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * 销障工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procClearTopListStatisticalExport() {
        ExportDto<ProcTopListStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procClearTopListStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * 巡检工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:53
     */
    @Test
    public void procInspectionTopListStatisticalExport() {
        ExportDto<ProcTopListStatisticalExportBean> exportDto = new ExportDto<>();
        try  {
            procStatisticalService.procInspectionTopListStatisticalExport(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * 公共统计新增数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 14:37
     */
    public void commonStatisticalInsertData() {
        new Expectations() {
            {
                procStatisticalDao.queryProcInspectionListByNowDate((QueryProcCountSumReq) any);
                List<ProcInfoDateStatistical> inspectionDateInfoList = new ArrayList<>();
                ProcInfoDateStatistical procInfoDateStatistical = new ProcInfoDateStatistical();
                procInfoDateStatistical.setAreaId("1");
                inspectionDateInfoList.add(procInfoDateStatistical);
                result = inspectionDateInfoList;
            }
        };

        new Expectations() {
            {
                procStatisticalDao.queryProcCLearListByNowDate((QueryProcCountSumReq) any);
                List<ProcInfoDateStatistical> inspectionDateInfoList = new ArrayList<>();
                ProcInfoDateStatistical procInfoDateStatistical = new ProcInfoDateStatistical();
                procInfoDateStatistical.setAreaId("1");
                inspectionDateInfoList.add(procInfoDateStatistical);
                result = inspectionDateInfoList;
            }
        };
    }

    /**
     * 获取拥有权限信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:53
     */
    public void getPermissionsInfo() {
        new MockUp<ProcStatisticalServiceImpl>(){
            @Mock
            public ProcBaseReq getPermissionCondition() {
                ProcBaseReq procBaseReq = new ProcBaseReq();
                Set<String> deviceTypeSet = new HashSet<>();
                deviceTypeSet.add("1");
                procBaseReq.setPermissionDeviceTypes(deviceTypeSet);
                Set<String> areaIdSet = new HashSet<>();
                areaIdSet.add("1");
                procBaseReq.setPermissionAreaIds(areaIdSet);
                return procBaseReq;
            }
        };
    }


    /**
     * 获取拥有权限信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:53
     */
    public void getAddDeptPermissionsInfo() {
        new MockUp<ProcStatisticalServiceImpl>(){
            @Mock
            public ProcBaseReq getPermissionCondition() {
                ProcBaseReq procBaseReq = new ProcBaseReq();
                Set<String> deviceTypeSet = new HashSet<>();
                deviceTypeSet.add("1");
                procBaseReq.setPermissionDeviceTypes(deviceTypeSet);
                Set<String> areaIdSet = new HashSet<>();
                areaIdSet.add("1");
                Set<String> deptIdSet = new HashSet<>();
                deptIdSet.add("1");
                procBaseReq.setPermissionDeptIds(deptIdSet);
                procBaseReq.setPermissionAreaIds(areaIdSet);
                return procBaseReq;
            }
        };
    }
}
