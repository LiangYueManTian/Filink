package com.fiberhome.filink.rfid.service.impl.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.export.statistics.JumpConnectionInCabinetExport;
import com.fiberhome.filink.rfid.export.statistics.JumpConnectionOutCabinetExport;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionInCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionOutCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.jumpconnection.JumpConnectionStatisticsReq;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.resp.statistics.JumpConnectionStatisticsResp;
import com.fiberhome.filink.rfid.service.statistics.JumpConnectionStatisticsService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtilDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * ONT设施跳接关系 服务类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-10
 */
@Service
public class JumpConnectionStatisticsServiceImpl implements JumpConnectionStatisticsService {


    @Autowired
    private JumpFiberInfoDao jumpFiberInfoDao;
    @Autowired
    private JumpConnectionInCabinetExport jumpConnectionInCabinetExport;
    @Autowired
    private JumpConnectionOutCabinetExport jumpConnectionOutCabinetExport;
    /**
     * 注入模板接口
     */
    @Autowired
    private TemplateService templateService;
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
     * 柜内跳接
     *
     * @param jumpConnectionStatisticsReq 条件
     * @return Result
     */
    @Override
    public Result inCabinet(JumpConnectionStatisticsReq jumpConnectionStatisticsReq) {
        List<JumpConnectionStatisticsResp> jumpConnectionStatisticsRespList = jumpFiberInfoDao
                .queryInCabinet(jumpConnectionStatisticsReq);
        queryPortNumByPortId(jumpConnectionStatisticsRespList);
        return ResultUtils.success(jumpConnectionStatisticsRespList);
    }

    /**
     * 柜内跳接导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @Override
    public Result exportInCabinet(ExportDto<ExportJumpConnectionInCabinetReq> exportDto) {
        ExportServiceUtilDto<JumpConnectionInCabinetExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                jumpConnectionInCabinetExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_JUMP_CONNECTION_IN_CABINET),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_JUMP_CONNECTION_IN_CABINET);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }


    /**
     * 柜间跳接
     *
     * @param jumpConnectionStatisticsReq 条件
     * @return Result
     */
    @Override
    public Result outCabinet(JumpConnectionStatisticsReq jumpConnectionStatisticsReq) {
        if (Objects.isNull(jumpConnectionStatisticsReq)
                || StringUtils.isEmpty(jumpConnectionStatisticsReq.getDeviceId())
                || StringUtils.isEmpty(jumpConnectionStatisticsReq.getOppositeDeviceId())) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }

        List<JumpConnectionStatisticsResp> jumpConnectionStatisticsRespList = jumpFiberInfoDao
                .queryOutCabinet(jumpConnectionStatisticsReq);

        //将名字和id绑定
        Map<String, String> deviceMap = new HashMap<>(2);
        deviceMap.put(jumpConnectionStatisticsReq.getDeviceId(), jumpConnectionStatisticsReq.getDeviceName());
        deviceMap.put(jumpConnectionStatisticsReq.getOppositeDeviceId(), jumpConnectionStatisticsReq.getOppositeDeviceName());
        for (JumpConnectionStatisticsResp resp : jumpConnectionStatisticsRespList) {
            resp.setDeviceName(deviceMap.get(resp.getDeviceId()));
            resp.setOppositeDeviceName(deviceMap.get(resp.getOppositeDeviceId()));
        }
        queryPortNumByPortId(jumpConnectionStatisticsRespList);
        return ResultUtils.success(jumpConnectionStatisticsRespList);
    }

    private  void  queryPortNumByPortId(List<JumpConnectionStatisticsResp> jumpFiberInfoRespList){
        //获取对端端口号
        for (JumpConnectionStatisticsResp jumpFiberInfoResp : jumpFiberInfoRespList){
            //获取对端端口id
            PortInfoReqDto portInfoReqDto = new PortInfoReqDto();
            portInfoReqDto.setDeviceId(jumpFiberInfoResp.getDeviceId());
            portInfoReqDto.setFrameNo(jumpFiberInfoResp.getFrameNo());
            portInfoReqDto.setDiscSide(jumpFiberInfoResp.getDiscSide());
            portInfoReqDto.setBoxSide(jumpFiberInfoResp.getBoxSide());
            portInfoReqDto.setDiscNo(jumpFiberInfoResp.getDiscNo());
            portInfoReqDto.setPortNo(jumpFiberInfoResp.getPortNo());
            String portId = templateService.queryPortIdByPortInfo(portInfoReqDto);
            jumpFiberInfoResp.setPortNo(templateService.queryPortNumByPortId(portId));
        }

        //获取对端端口号
        for (JumpConnectionStatisticsResp jumpFiberInfoResp : jumpFiberInfoRespList){
            //获取对端端口id
            PortInfoReqDto oppositePortInfoReqDto = new PortInfoReqDto();
            oppositePortInfoReqDto.setDeviceId(jumpFiberInfoResp.getOppositeDeviceId());
            oppositePortInfoReqDto.setFrameNo(jumpFiberInfoResp.getOppositeFrameNo());
            oppositePortInfoReqDto.setDiscSide(jumpFiberInfoResp.getOppositeDiscSide());
            oppositePortInfoReqDto.setBoxSide(jumpFiberInfoResp.getOppositeBoxSide());
            oppositePortInfoReqDto.setDiscNo(jumpFiberInfoResp.getOppositeDiscNo());
            oppositePortInfoReqDto.setPortNo(jumpFiberInfoResp.getOppositePortNo());
            String oppositePortId = templateService.queryPortIdByPortInfo(oppositePortInfoReqDto);
            jumpFiberInfoResp.setOppositePortNo(templateService.queryPortNumByPortId(oppositePortId));
        }
    }


    /**
     * 柜间跳接导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @Override
    public Result exportOutCabinet(ExportDto<ExportJumpConnectionOutCabinetReq> exportDto) {
        ExportServiceUtilDto<JumpConnectionOutCabinetExport> exportServiceUtilDto = new ExportServiceUtilDto<>(
                jumpConnectionOutCabinetExport,
                exportDto,
                exportServerName,
                maxExportDataSize,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_JUMP_CONNECTION_OUT_CABINET),
                systemLanguageUtil,
                logProcess,
                LogFunctionCodeConstant.EXPORT_JUMP_CONNECTION_OUT_CABINET);
        return ExportServiceUtil.exportProcessing(exportServiceUtilDto);
    }
}
