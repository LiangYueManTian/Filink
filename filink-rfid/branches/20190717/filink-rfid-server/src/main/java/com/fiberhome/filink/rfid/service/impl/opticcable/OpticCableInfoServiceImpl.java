package com.fiberhome.filink.rfid.service.impl.opticcable;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableInfo;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.constant.opticcable.OpticCableConstant;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.export.opticcable.OpticCableExport;
import com.fiberhome.filink.rfid.req.opticcable.InsertOpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.UpdateOpticCableInfoReq;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoDetail;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoResp;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableInfoService;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.rfid.utils.export.ExportLogServer;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
@Service
@Slf4j
public class OpticCableInfoServiceImpl extends ServiceImpl<OpticCableInfoDao, OpticCableInfo> implements OpticCableInfoService {

    @Autowired
    private OpticCableInfoDao opticCableInfoDao;

    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    /**
     * ????????????SystemLanguage??????
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ???????????????????????????
     */
    @Autowired
    private OpticCableExport opticCableExport;
    /**
     * ????????????????????????
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * ???????????????
     */
    @Value("${exportServerName}")
    private String exportServerName;

    /**
     * ???????????????
     */
    @Value("${cableCoreMaxNum}")
    private Integer cableCoreMaxNum;

    /**
     * ????????????????????????
     *
     * @param queryCondition ???????????????
     * @return Result
     */
    @Override
    public Result opticCableListByPage(QueryCondition<OpticCableInfoReq> queryCondition) {

        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition
                .getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);
        // ??????????????????
        Page page = myBatiesBuildPage(queryCondition);
        PageBean pageBean = myBatiesBuildPageBean(page, opticCableInfoDao.opticCableListTotal(queryCondition), opticCableInfoDao.opticCableListByPage(queryCondition));
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * ????????????
     *
     * @param insertOpticCableInfoReq ??????????????????
     * @return Result
     */
    @Override
    public Result addOpticCable(InsertOpticCableInfoReq insertOpticCableInfoReq) {
        //??????????????????
        if (ObjectUtils.isEmpty(insertOpticCableInfoReq)) {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //???????????????
        if (cableCoreMaxNum < Integer.parseInt(insertOpticCableInfoReq.getCoreNum())) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_EXCEED_THE_CORE_MAXIMUM, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_EXCEED_THE_CORE_MAXIMUM));
        }
        //????????????
        if (this.checkOpticCableName(insertOpticCableInfoReq.getOpticCableId(), insertOpticCableInfoReq.getOpticCableName())) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_NAME_SAME));
        }

        //????????????uuid
        insertOpticCableInfoReq.setOpticCableId(NineteenUUIDUtils.uuid());
        //??????????????????
        insertOpticCableInfoReq.setCreateUser(RequestInfoUtils.getUserId());
        //??????utc????????????
        insertOpticCableInfoReq.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());

        //????????????
        opticCableInfoDao.addOpticCable(insertOpticCableInfoReq);
        log.info("????????????????????????");

        //??????????????????
        String addOptType = LogConstants.DATA_OPT_TYPE_ADD;
        String functionCode = LogFunctionCodeConstant.ADD_OPTIC_CABLE_INFO_FUNCTION_CODE;
        AddLogBean addLogBean = getAddOpticCableInfoLogParam(insertOpticCableInfoReq, functionCode, addOptType);
        //??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("????????????????????????");
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_OPTIC_CABLE_SUCCESS));
    }

    /**
     * ??????id??????????????????
     *
     * @param id ??????id
     * @return Result
     */
    @Override
    public Result queryOpticCableById(String id) {
        OpticCableInfoDetail opticCableInfoDetail = opticCableInfoDao.queryOpticCableById(id);
        if (ObjectUtils.isEmpty(opticCableInfoDetail)) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_IS_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_IS_NOT_EXIST));
        }
        return ResultUtils.success(opticCableInfoDetail);
    }

    /**
     * ????????????
     *
     * @param updateOpticCableInfoReq ??????????????????
     * @return Result
     */
    @Override
    public Result updateOpticCableById(UpdateOpticCableInfoReq updateOpticCableInfoReq) {
        OpticCableInfoDetail opticCableInfoDetail = opticCableInfoDao.queryOpticCableById(updateOpticCableInfoReq.getOpticCableId());
        if (ObjectUtils.isEmpty(opticCableInfoDetail)) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_IS_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_IS_NOT_EXIST));
        }

        //????????????
        if (this.checkOpticCableName(updateOpticCableInfoReq.getOpticCableId(), updateOpticCableInfoReq.getOpticCableName())) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_NAME_SAME));
        }

        //??????????????????
        updateOpticCableInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        //??????utc????????????
        updateOpticCableInfoReq.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        //??????????????????
        opticCableInfoDao.updateOpticCableById(updateOpticCableInfoReq);
        log.info("????????????????????????");

        //??????????????????
        String addOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        String functionCode = LogFunctionCodeConstant.UPDATE_OPTIC_CABLE_INFO_FUNCTION_CODE;
        AddLogBean addLogBean = getAddOpticCableInfoLogParam(updateOpticCableInfoReq, functionCode, addOptType);
        //??????????????????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("????????????????????????");

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_OPTIC_CABLE_SUCCESS));
    }

    /**
     * ????????????
     *
     * @param id ????????????id
     * @return Result
     */
    @Override
    public Result deleteOpticCableById(String id) {
        OpticCableInfoDetail opticCableInfoDetail = opticCableInfoDao.queryOpticCableById(id);
        //??????????????????????????????
        if (ObjectUtils.isEmpty(opticCableInfoDetail)){
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_IS_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_IS_NOT_EXIST));
        }

        //????????????????????????????????????
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setBelongOpticCableId(id);
        List<OpticCableSectionInfo> opticCableSectionInfoList = opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(opticCableSectionInfoReq);
        if (!ObjectUtils.isEmpty(opticCableSectionInfoList)) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_CONTAINS_OPTIC_CABLE_SECTION, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_CONTAINS_OPTIC_CABLE_SECTION));
        }

        //????????????
        opticCableInfoDao.updateOpticCableIsDeletedById(id, OpticCableConstant.IS_DELETED, RequestInfoUtils.getUserId(), FiLinkTimeUtils.getUtcZeroTimeStamp());
        log.info("????????????????????????");

        //??????????????????
        String addOptType = LogConstants.DATA_OPT_TYPE_DELETE;
        String functionCode = LogFunctionCodeConstant.DELETE_OPTIC_CABLE_INFO_FUNCTION_CODE;
        AddLogBean addLogBean = getAddOpticCableInfoLogParam(opticCableInfoDetail, functionCode, addOptType);
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_OPTIC_CABLE_SUCCESS));
    }

    /**
     * ??????????????????
     *
     * @param id ??????id
     * @return Result
     */
    @Override
    public Boolean checkOpticCableName(String id, String name) {
        //????????????????????????
        name = CheckInputString.nameCheck(name);
        if (StringUtils.isEmpty(name)) {
            return true;
        }
        //????????????????????????
        OpticCableInfoDetail opticCableInfoDetail = opticCableInfoDao.queryOpticCableByName(name);
        if (!ObjectUtils.isEmpty(opticCableInfoDetail) && !opticCableInfoDetail.getOpticCableId().equals(id)) {
            return true;
        }
        return false;
    }

    /**
     * app???????????????????????????
     *
     * @return Result
     */
    @Override
    public Result getOpticCableListForApp() {
        List<OpticCableInfoResp> opticCableInfoRespList = opticCableInfoDao.getOpticCableListForApp();
        return ResultUtils.success(opticCableInfoRespList);
    }

    /**
     * ??????????????????
     *
     * @param exportDto ????????????????????????
     * @return Result
     */
    @Override
    public Result exportOpticCableList(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo = null;
        try {
            exportRequestInfo = opticCableExport.insertTask(exportDto, exportServerName, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_LIST));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(RfIdResultCodeConstant.EXPORT_NO_DATA, I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return ExportServiceUtil.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(RfIdResultCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(RfIdI18nConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(RfIdResultCodeConstant.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(RfIdI18nConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        opticCableExport.exportData(exportRequestInfo);

        //????????????
        ExportLogServer.addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_OPTIC_CABLE_INFO_FUNCTION_CODE, logProcess, systemLanguageUtil);

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ??????????????????????????????
     *
     * @param opticCableInfo ????????????
     * @param functionCode   ?????????
     * @param dataOptType    ????????????
     * @return ??????????????????????????????
     */
    private AddLogBean getAddOpticCableInfoLogParam(OpticCableInfo opticCableInfo, String functionCode, String dataOptType) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(AppConstant.OPTICAL_CABLE_ID);
        addLogBean.setDataName(AppConstant.OPTICAL_CABLE_NAME);
        addLogBean.setOptObj(opticCableInfo.getOpticCableName());
        addLogBean.setOptObjId(opticCableInfo.getOpticCableId());
        addLogBean.setFunctionCode(functionCode);
        addLogBean.setDataOptType(dataOptType);
        return addLogBean;
    }

}
