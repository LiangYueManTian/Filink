package com.fiberhome.filink.rfid.service.impl.statistics;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.constant.statistics.OpticalCableLevelEnum;
import com.fiberhome.filink.rfid.constant.statistics.UsageStateEnum;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.statistics.FiberOpticsAndCoreStatisticsDao;
import com.fiberhome.filink.rfid.export.statistics.OpticalCableSectionStatisticsExport;
import com.fiberhome.filink.rfid.export.statistics.OpticalFiberStatisticsExport;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticCableInfoStatisticReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticalCableSectionStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.CoreStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.OpticCableInfoSectionStatisticsReq;
import com.fiberhome.filink.rfid.resp.statistics.CoreStatisticsResp;
import com.fiberhome.filink.rfid.resp.statistics.OpticCableInfoSectionStatisticsResp;
import com.fiberhome.filink.rfid.resp.statistics.OpticCableInfoStatisticsResp;
import com.fiberhome.filink.rfid.service.statistics.FiberOpticsAndCoreStatisticsService;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtilDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 光缆及纤芯统计 服务类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-31
 */
@Service
public class FiberOpticsAndCoreStatisticsServiceImpl implements FiberOpticsAndCoreStatisticsService {

    /**
     * 光缆及纤芯统计dao
     */
    @Autowired
    private FiberOpticsAndCoreStatisticsDao statisticsDao;
    /**
     * 光缆导出
     */
    @Autowired
    private OpticalFiberStatisticsExport opticalFiberStatisticsExport;
    /**
     * 光缆段和纤芯导出
     */
    @Autowired
    private OpticalCableSectionStatisticsExport opticalCableSectionStatisticsExport;
    /**
     * 导出服务名
     */
    @Value("${exportServerName}")
    private String exportServerName;

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;
    /**
     * 远程调用SystemLanguage服务
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * 光缆段信息表 Mapper 接口
     */
    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    /**
     * 光缆统计
     *
     * @return Result
     */
    @Override
    public Result opticalFiberStatistics() {
        List<Map<String, String>> list = statisticsDao.opticalFiberStatistics();
        /*
        将list转换成OpticCableInfoStatisticsResp
         */
        Map<String, String> maps = new HashMap<>(list.size());
        for (Map<String, String> map : list) {
            maps.put(OpticalCableLevelEnum.getLabel(map.get(AppConstant.PORT_STATE)), map.get(AppConstant.PORT_NUMBER));
        }
        OpticCableInfoStatisticsResp resp = new OpticCableInfoStatisticsResp();
        try {
            BeanUtils.populate(resp, maps);
        } catch (Exception e) {
            ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        return ResultUtils.success(resp);
    }

    /**
     * 光缆统计导出
     *
     * @param exportDto 查询条件
     * @return Result
     */
    @Override
    public Result exportOpticalFiberStatistics(ExportDto<ExportOpticCableInfoStatisticReq> exportDto) {
        ExportServiceUtilDto<OpticalFiberStatisticsExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                opticalFiberStatisticsExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_OPTICAL_FIBER_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_OPTICAL_FIBER_STATISTICS_FUNCTION_CODE);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }

    /**
     * 光缆段统计
     *
     * @param opticCableInfoSectionStatisticsReq 查询条件
     * @return Result
     */
    @Override
    public Result opticalFiberSection(OpticCableInfoSectionStatisticsReq opticCableInfoSectionStatisticsReq) {
        List<Map<String, String>> list = statisticsDao.opticalFiberSection(opticCableInfoSectionStatisticsReq);
         /*
        将list转换成OpticCableInfoSectionStatisticsResp
         */
        Map<String, String> maps = new HashMap<>(list.size());
        for (Map<String, String> map : list) {
            maps.put(UsageStateEnum.getLabel(map.get(AppConstant.PORT_STATE)), map.get(AppConstant.PORT_NUMBER));
        }
        OpticCableInfoSectionStatisticsResp resp = new OpticCableInfoSectionStatisticsResp();
        try {
            BeanUtils.populate(resp, maps);
        } catch (Exception e) {
            ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        resp.setTotalCount(resp.getUsed() + resp.getUnused());
        return ResultUtils.success(resp);
    }

    /**
     * 光缆统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @Override
    public Result exportOpticalFiberSection(ExportDto<ExportOpticalCableSectionStatisticsReq> exportDto) {
        ExportServiceUtilDto<OpticalCableSectionStatisticsExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                opticalCableSectionStatisticsExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_OPTICAL_FIBER_SECTION_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_OPTICAL_FIBER_SECTION_STATISTICS_FUNCTION_CODE);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }

    /**
     * 纤芯统计
     *
     * @param coreStatisticsReq 查询条件
     * @return Result
     */
    @Override
    public Result coreStatistics(CoreStatisticsReq coreStatisticsReq) {
        CoreStatisticsResp resp = opticCableSectionInfoDao.queryOpticCableSectionById(coreStatisticsReq);
        if (Objects.isNull(resp)) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        resp.setUnusedCount(resp.getTotalCount() - resp.getUsedCount());
        return ResultUtils.success(resp);
    }

    /**
     * 纤芯统计导出
     *
     * @param exportDto 查询条件
     * @return Result
     */
    @Override
    public Result exportCoreStatistics(ExportDto<ExportOpticalCableSectionStatisticsReq> exportDto) {
        ExportServiceUtilDto<OpticalCableSectionStatisticsExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                opticalCableSectionStatisticsExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_CORE_STATE_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_CORE_STATE_STATISTICS_FUNCTION_CODE);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }


}
