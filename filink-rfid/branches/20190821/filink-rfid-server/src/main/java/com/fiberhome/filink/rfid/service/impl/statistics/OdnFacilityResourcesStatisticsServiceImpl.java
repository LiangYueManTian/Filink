package com.fiberhome.filink.rfid.service.impl.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DevicePortUtilizationRateFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.constant.statistics.PortStatusEnum;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.dao.statistics.OdnFacilityResourcesStatisticsDao;
import com.fiberhome.filink.rfid.exception.FilinkPortInfoException;
import com.fiberhome.filink.rfid.export.statistics.MeltFiberPortStatisticsExport;
import com.fiberhome.filink.rfid.export.statistics.PortStatisticsExport;
import com.fiberhome.filink.rfid.export.statistics.PortTopNumberExport;
import com.fiberhome.filink.rfid.req.statistics.export.ExportMeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortTopNumberReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.DiscPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.FramePortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.JumpFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.MeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.resp.statistics.JumpConnectionStatisticsResp;
import com.fiberhome.filink.rfid.resp.statistics.JumpFiberPortStatisticsResp;
import com.fiberhome.filink.rfid.resp.statistics.MeltFiberPortStatisticsResp;
import com.fiberhome.filink.rfid.service.statistics.OdnFacilityResourcesStatisticsService;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtilDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ONT?????????????????? ?????????
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-10
 */
@Service
public class OdnFacilityResourcesStatisticsServiceImpl implements OdnFacilityResourcesStatisticsService {

    /**
     * ?????????????????????dao
     */
    @Autowired
    private OdnFacilityResourcesStatisticsDao odnFacilityResourcesStatisticsDao;
    /**
     * ????????????
     */
    @Autowired
    private JumpFiberInfoDao jumpFiberInfoDao;
    /**
     * ?????????????????????Feign
     */
    @Autowired
    private DevicePortUtilizationRateFeign devicePortUtilizationRateFeign;
    /**
     * ??????????????????
     */
    @Autowired
    private PortStatisticsExport portStatisticsExport;
    /**
     * ??????????????????
     */
    @Autowired
    private MeltFiberPortStatisticsExport meltFiberPortStatisticsExport;
    /**
     * TopN?????????????????????
     */
    @Autowired
    private PortTopNumberExport portTopNumberExport;
    /**
     * ???????????????
     */
    @Value("${exportServerName}")
    private String exportServerName;
    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;
    /**
     * ????????????SystemLanguage??????
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * ??????api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ?????????????????????
     *
     * @param jumpFiberPortStatisticsReq ????????????
     * @return Result
     */
    @Override
    public Result jumpFiberPortStatistics(JumpFiberPortStatisticsReq jumpFiberPortStatisticsReq) {
        List<Map<String, Integer>> list = odnFacilityResourcesStatisticsDao
                .jumpFiberPortStatistics(jumpFiberPortStatisticsReq);
        return ResultUtils.success(converterToJumpFiberPortStatisticsResp(list));
    }

    /**
     * ???????????????????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result exportJumpFiberPortStatistics(ExportDto<ExportPortStatisticsReq> exportDto) {
        ExportServiceUtilDto<PortStatisticsExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                portStatisticsExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_JUMP_FIBER_PORT_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_JUMP_FIBER_PORT_STATISTICS);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);

    }


    /**
     * ?????????????????????
     *
     * @param meltFiberPortStatisticsReq ????????????
     * @return Result
     */
    @Override
    public Result meltFiberPortStatistics(MeltFiberPortStatisticsReq meltFiberPortStatisticsReq) {
        Integer count = odnFacilityResourcesStatisticsDao
                .portCountStatistics(meltFiberPortStatisticsReq);
        Integer odnFacilityCount = odnFacilityResourcesStatisticsDao
                .meltFiberPortStatistics(meltFiberPortStatisticsReq);
        MeltFiberPortStatisticsResp resp = new MeltFiberPortStatisticsResp();
        resp.setUnusedPortCount(count - odnFacilityCount);
        resp.setUsedPortCount(odnFacilityCount);
        resp.setTotalCount(count);
        return ResultUtils.success(resp);
    }

    /**
     * ???????????????????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result exportMeltFiberPortStatistics(ExportDto<ExportMeltFiberPortStatisticsReq> exportDto) {
        ExportServiceUtilDto<MeltFiberPortStatisticsExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                meltFiberPortStatisticsExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_MELT_FIBER_PORT_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_MELT_FIBER_PORT_STATISTICS);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }


    /**
     * ???????????????
     *
     * @param discPortStatisticsReq ????????????
     * @return Result
     */
    @Override
    public Result discPortStatistics(DiscPortStatisticsReq discPortStatisticsReq) {
        List<Map<String, Integer>> list = odnFacilityResourcesStatisticsDao.discPortStatistics(discPortStatisticsReq);
        return ResultUtils.success(converterToJumpFiberPortStatisticsResp(list));
    }

    /**
     * ?????????????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result exportDiscPortStatistics(ExportDto<ExportPortStatisticsReq> exportDto) {
        ExportServiceUtilDto<PortStatisticsExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                portStatisticsExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_DISC_PORT_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_DISC_PORT_STATISTICS);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }

    /**
     * ???????????????
     *
     * @param framePortStatisticsReq ????????????
     * @return Result
     */

    @Override
    public Result framePortStatistics(FramePortStatisticsReq framePortStatisticsReq) {
        List<Map<String, Integer>> list = odnFacilityResourcesStatisticsDao.framePortStatistics(framePortStatisticsReq);
        return ResultUtils.success(converterToJumpFiberPortStatisticsResp(list));
    }

    /**
     * ???????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result exportFramePortStatistics(ExportDto<ExportPortStatisticsReq> exportDto) {
        ExportServiceUtilDto<PortStatisticsExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                portStatisticsExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_FRAME_PORT_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_FRAME_PORT_STATISTICS);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }

    /**
     * TopN?????????????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result portTopNumber(ExportDto<ExportPortTopNumberReq> exportDto) {
        ExportServiceUtilDto<PortTopNumberExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                portTopNumberExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_TOP_NUMBER_PORT_STATISTICS),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_TOP_N_PORT_STATISTICS);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }

    /**
     * ??????????????????????????????
     *
     * @param deviceId ??????id
     * @return Result
     */
    @Override
    public Result devicePortStatistics(String deviceId) {
        JumpFiberPortStatisticsReq jumpFiberPortStatisticsReq = new JumpFiberPortStatisticsReq();
        List<String> listDeviceId = new ArrayList<>();
        listDeviceId.add(deviceId);
        jumpFiberPortStatisticsReq.setFacilities(listDeviceId);
        List<Map<String, Integer>> list = odnFacilityResourcesStatisticsDao
                .jumpFiberPortStatistics(jumpFiberPortStatisticsReq);
        return ResultUtils.success(converterToJumpFiberPortStatisticsResp(list));
    }

    /**
     * ?????????????????????????????????
     *
     * @param deviceId ??????id
     * @return Result
     */
    @Override
    public Result deviceUsePortStatistics(String deviceId) {
        List<String> list = new ArrayList<>();
        list.add(deviceId);
        Map<String, Long> mapDevicePortCount = mapDevicePortCountByList(list);
         /*
           ??????????????????????????????
         */
        Integer usedPort = 0;
        List<JumpConnectionStatisticsResp> listResp = jumpFiberInfoDao.queryJumpFiberInfoByDeviceId(list);
        for (String deviceIdStr : list) {
            usedPort = usedPortNum(deviceIdStr, listResp);
        }
        MeltFiberPortStatisticsResp resp = new MeltFiberPortStatisticsResp();
        resp.setUsedPortCount(usedPort);
        Integer count = Integer.valueOf(mapDevicePortCount.get(deviceId).toString());
        resp.setTotalCount(count);
        resp.setUnusedPortCount(count - usedPort);
        return ResultUtils.success(resp);
    }

    /**
     * ?????????????????????
     *
     * @param listDeviceId ??????id
     * @return DevicePortUtilizationRateResp
     */
    @Override
    public Integer portStatistics(List<String> listDeviceId) {
        Map<String, Long> mapDevicePortCount = mapDevicePortCountByList(listDeviceId);
        /*
           ??????????????????????????????
         */
        List<JumpConnectionStatisticsResp> list = jumpFiberInfoDao.queryJumpFiberInfoByDeviceId(listDeviceId);
        List<DevicePortUtilizationRate> listRate = new ArrayList<>();
        for (String deviceId : listDeviceId) {
            Integer usedPort = usedPortNum(deviceId, list);
            DevicePortUtilizationRate rate = new DevicePortUtilizationRate();
            /*
                ?????????????????????
             */
            rate.setDeviceId(deviceId);
            Long count = mapDevicePortCount.get(deviceId);
            if (count != 0) {
                DecimalFormat df = new DecimalFormat(".##");
                Double m = Double.valueOf(df.format((usedPort * 100) / Double.valueOf(count)));
                rate.setUtilizationRate(m);
                listRate.add(rate);
            }
        }
        return devicePortUtilizationRateFeign.addPortStatistics(listRate);
    }

    /**
     * ???????????????????????????
     *
     * @param listDeviceId ??????id??????
     * @return Map
     */
    private Map<String, Long> mapDevicePortCountByList(List<String> listDeviceId) {
             /*
            ????????????????????????????????????????????????MAap
         */
        List<Map<String, Object>> listCount = odnFacilityResourcesStatisticsDao.countPortStatisticsList(listDeviceId);
        if (ObjectUtils.isEmpty(listCount)) {
            throw new FilinkPortInfoException();
        }
        Map<String, Long> mapDevicePortCount = new HashMap<>(listCount.size());
        for (Map<String, Object> map : listCount) {
            mapDevicePortCount.put((String) map.get(AppConstant.DEVICE_ID), (Long) map.get(AppConstant.PORT_NUMBER));
        }
        return mapDevicePortCount;
    }

    /**
     * ????????????????????????
     *
     * @param deviceId ??????id
     * @param list     ????????????
     * @return ???????????????
     */
    private Integer usedPortNum(String deviceId, List<JumpConnectionStatisticsResp> list) {
        Integer usedPort = 0;
        for (JumpConnectionStatisticsResp resp : list) {
            if (deviceId.equals(resp.getDeviceId()) || deviceId.equals(resp.getOppositeDeviceId())) {
                    /*
                       ?????????????????????????????????1?????????????????????2
                     */
                if ("0".equals(resp.getInnerDevice())) {
                    usedPort += 2;
                } else {
                    usedPort++;
                }
            }
        }
        return usedPort;
    }

    /**
     * ???List?????????JumpFiberPortStatisticsResp
     *
     * @param list list
     * @return JumpFiberPortStatisticsResp
     */
    private JumpFiberPortStatisticsResp converterToJumpFiberPortStatisticsResp(List<Map<String, Integer>> list) {
        JumpFiberPortStatisticsResp resp = new JumpFiberPortStatisticsResp();
         /*
        ???list?????????OpticCableInfoSectionStatisticsResp
         */
        Map<String, Integer> maps = new HashMap<>(list.size());
        for (Map<String, Integer> map : list) {
            maps.put(PortStatusEnum.getLabel(map.get(AppConstant.PORT_STATE)), map.get(AppConstant.PORT_NUMBER));
        }
        try {
            BeanUtils.populate(resp, maps);
        } catch (Exception e) {
            ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }
        return resp;
    }
}
