package com.fiberhome.filink.rfid.service.impl.fibercore;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.constant.fibercore.JumpCoreConstant;
import com.fiberhome.filink.rfid.constant.fibercore.RfIdConstant;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.exception.FilinkJumpInfoException;
import com.fiberhome.filink.rfid.export.jumpfiber.JumpFiberExport;
import com.fiberhome.filink.rfid.req.fibercore.DeleteJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationJumpFiberInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.JumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.fibercore.app.JumpFiberInfoRespForApp;
import com.fiberhome.filink.rfid.service.fibercore.JumpFiberInfoService;
import com.fiberhome.filink.rfid.service.impl.UpdatePortStatusAsync;
import com.fiberhome.filink.rfid.service.rfid.RfidInfoService;
import com.fiberhome.filink.rfid.service.statistics.OdnFacilityResourcesStatisticsService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.rfid.utils.export.ExportLogServer;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
@Service
@Slf4j
public class JumpFiberInfoServiceImpl extends ServiceImpl<JumpFiberInfoDao, JumpFiberInfo> implements JumpFiberInfoService {

    @Autowired
    private JumpFiberInfoDao jumpFiberInfoDao;

    @Autowired
    private RfidInfoService rfidInfoService;

    /**
     * ????????????????????????
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ????????????SystemLanguage??????
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * odn????????????
     */
    @Autowired
    private OdnFacilityResourcesStatisticsService odnFacilityResourcesStatisticsService;

    /**
     * ????????????????????????
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * ?????????????????????????????????
     */
    @Autowired
    private JumpFiberExport jumpFiberExport;

    /**
     * ??????????????????
     */
    @Autowired
    private TemplateService templateService;

    /**
     * ??????rfIdCode??????
     */
    @Autowired
    private RfidInfoService rfIdInfoService;

    /**
     * ??????updatePortStatusAsync??????
     */
    @Autowired
    private UpdatePortStatusAsync updatePortStatusAsync;

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
    @Value("${jumpFiberMaxNum}")
    private Integer jumpFiberMaxNum;


    /**
     * ??????????????????
     *
     * @param queryJumpFiberInfoReq ?????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryJumpFiberInfoByPortInfo(QueryJumpFiberInfoReq queryJumpFiberInfoReq) {
        List<JumpFiberInfoResp> jumpFiberInfoRespListResult = new ArrayList<>();
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);

        //??????????????????
        jumpFiberInfoRespListResult = assemblyJumpFiberInfoResp(jumpFiberInfoRespList,queryJumpFiberInfoReq);

        return ResultUtils.success(jumpFiberInfoRespListResult);
    }

    /**
     * ????????????????????????
     *
     * @param deleteJumpFiberInfoReq ????????????????????????
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteJumpFiberInfoById(DeleteJumpFiberInfoReq deleteJumpFiberInfoReq) {
        //????????????
        if (ObjectUtils.isEmpty(deleteJumpFiberInfoReq.getJumpFiberIdList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        BeanUtils.copyProperties(deleteJumpFiberInfoReq,queryJumpFiberInfoReq);
        queryJumpFiberInfoReq.setJumpFiberIdList(deleteJumpFiberInfoReq.getJumpFiberIdList());
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);

        //????????????
        if (ObjectUtils.isEmpty(jumpFiberInfoRespList) || jumpFiberInfoRespList.size() < deleteJumpFiberInfoReq.getJumpFiberIdList().size()){
            return ResultUtils.warn(RfIdResultCodeConstant.JUMP_CORE_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.JUMP_CORE_NOT_EXIST));
        }

        //??????????????????????????????
        for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoRespList){
            Set<String> rfIdCodeList = new HashSet<>();
            rfIdCodeList.add(jumpFiberInfo.getRfidCode());
            rfIdCodeList.add(jumpFiberInfo.getOppositeRfidCode());
            rfidInfoService.deleteRfidInfo(rfIdCodeList);
        }

        //??????????????????
        jumpFiberInfoDao.deleteJumpFiberInfoById(deleteJumpFiberInfoReq.getJumpFiberIdList(), FiLinkTimeUtils.getUtcZeroTimeStamp(), RequestInfoUtils.getUserId());

        //????????????????????????
        this.portStatistics(jumpFiberInfoRespList);

        //??????????????????????????????
        updatePortStatusAsync.updatePortBindingState(jumpFiberInfoRespList);
        //????????????????????????
        this.saveOperatorLogToDelete(jumpFiberInfoRespList);

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_JUMP_CORE_SUCCESS));
    }

    /**
     * ??????????????????
     *
     * @param exportDto ????????????????????????
     *
     * @return Result
     */
    @Override
    public Result exportJumpFiberList(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = jumpFiberExport.insertTask(exportDto, exportServerName, I18nUtils.getSystemString(RfIdI18nConstant.JUMP_FIBER_LIST));
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
        jumpFiberExport.exportData(exportRequestInfo);

        //????????????
        ExportLogServer.addLogByExport(exportDto,LogFunctionCodeConstant.EXPORT_JUMP_FIBER_INFO_FUNCTION_CODE,logProcess,systemLanguageUtil);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * app????????????????????????????????????
     *
     * @param queryJumpFiberInfoReqForApp app?????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryJumpFiberInfoByPortInfoForApp(QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp) {
        List<JumpFiberInfoRespForApp> jumpFiberInfoRespForAppList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfoForApp(queryJumpFiberInfoReqForApp);

        //?????????????????????
        jumpFiberInfoRespForAppList = this.assemblyJumpFiberInfoThisAndOppositeForApp(jumpFiberInfoRespForAppList,queryJumpFiberInfoReqForApp);

        return ResultUtils.success(jumpFiberInfoRespForAppList);
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app??????????????????
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result operationJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp) {
        //?????????????????????????????????
        if (ObjectUtils.isEmpty(batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //?????????????????????
        if (jumpFiberMaxNum < batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList().size()){
            return ResultUtils.warn(RfIdResultCodeConstant.EXCEED_JUMP_CORE_MAX_NUM, I18nUtils.getSystemString(RfIdI18nConstant.EXCEED_JUMP_CORE_MAX_NUM));
        }
        //????????????????????????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //??????????????????
            if (this.checkParamsToUpdateForApp(batchOperationJumpFiberInfoForApp)){
                return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
            this.updateJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
        }
        //???????????????????????????
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            if (this.checkParamsToDeleteForApp(batchOperationJumpFiberInfoForApp)){
                return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
            this.deleteJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
            log.info("app????????????????????????");
        }
        //???????????????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType()) || AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //????????????????????????
            String flag = this.checkParamsToSaveForApp(batchOperationJumpFiberInfoForApp);
            if (JumpCoreConstant.RESULT_TYPE_QUERY_REALPOSITION_DEVICE_ID_ERROR.equals(flag)){
                return ResultUtils.warn(RfIdResultCodeConstant.QUERY_REALPOSITION_DEVICE_ID_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.QUERY_REALPOSITION_DEVICE_ID_ERROR));
            }else if (JumpCoreConstant.RESULT_TYPE_PORT_HAVE_JUMP_CORE.equals(flag)){
                return ResultUtils.warn(RfIdResultCodeConstant.PORT_HAVE_JUMP_CORE, I18nUtils.getSystemString(RfIdI18nConstant.PORT_HAVE_JUMP_CORE));
            }else if (JumpCoreConstant.RESULT_TYPE_RFID_CODE_IS_EXISTS.equals(flag)){
                return ResultUtils.warn(RfIdResultCodeConstant.RFID_CODE_IS_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.RFID_CODE_IS_EXISTS));
            }
            //??????????????????
            this.saveJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
            log.info("app????????????????????????");
        }
        //??????????????????id
        if (AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //????????????????????????code????????????
            Set<String> rfIdCodeList = new HashSet<>();
            for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
                rfIdCodeList.add(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
            }
            if (rfIdInfoService.checkRfidCodeListIsExist(rfIdCodeList)){
                return ResultUtils.warn(RfIdResultCodeConstant.RFID_CODE_IS_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.RFID_CODE_IS_EXISTS));
            }
            //??????????????????id??????
            this.updateRfIdCodeForApp(batchOperationJumpFiberInfoForApp);
            log.info("app??????????????????id??????");
        }
        //??????????????????id????????????????????????
        if (!AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //????????????????????????
            this.portStatisticsForApp(batchOperationJumpFiberInfoForApp);
            log.info("app??????????????????????????????");
            //app??????????????????????????????
            this.updatePortBindingStateForApp(batchOperationJumpFiberInfoForApp);
            log.info("app??????????????????????????????");
        }
        //??????app????????????
        this.saveOperatorLogForApp(batchOperationJumpFiberInfoForApp);
        log.info("app????????????????????????");
        //??????????????????
        return this.setResult(batchOperationJumpFiberInfoForApp);
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return Boolean
     */
    public Boolean checkParamsToUpdateForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            if (
                    StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDeviceId()) ||
                    StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeBoxSide()) ||
                    StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeFrameNo()) ||
                    StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDiscSide()) ||
                    StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDiscNo()) ||
                    StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeRfidCode())
            ){
                return true;
            }
        }
        return false;
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return String
     */
    public String checkParamsToSaveForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        //????????????id????????????
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            if (StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getDeviceId()) || StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDeviceId())){
                return JumpCoreConstant.RESULT_TYPE_QUERY_REALPOSITION_DEVICE_ID_ERROR;
            }
        }
        //??????????????????????????????????????????
        if (this.checkJumpFiberUsed(batchOperationJumpFiberInfoForApp)){
            return JumpCoreConstant.RESULT_TYPE_PORT_HAVE_JUMP_CORE;
        }
        //??????????????????id
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            if (ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getRfidCode())){
                operationJumpFiberInfoReqForApp.setRfidCode(UUIDUtil.getInstance().UUID32());
            }
            if (ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeRfidCode())){
                operationJumpFiberInfoReqForApp.setOppositeRfidCode(UUIDUtil.getInstance().UUID32());
            }
        }
        //????????????????????????code????????????
        Set<String> rfidCodeList = new HashSet<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            rfidCodeList.add(operationJumpFiberInfoReqForApp.getRfidCode());
            rfidCodeList.add(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
        }
        if (rfIdInfoService.checkRfidCodeListIsExist(rfidCodeList)){
            return JumpCoreConstant.RESULT_TYPE_RFID_CODE_IS_EXISTS;
        }
        //??????
        return JumpCoreConstant.RESULT_TYPE_NORMAL;
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return Boolean
     */
    public Boolean checkParamsToDeleteForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        //????????????
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            if (ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getDeviceId()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getBoxSide()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getFrameNo()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getDiscSide()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getDiscNo()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getPortNo()) ||

                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDeviceId()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeBoxSide()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeFrameNo()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDiscSide()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDiscNo()) ||
                    ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositePortNo())
            ){
                return true;
            }
        }
        return false;
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return Result
     */
    public Result setResult(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //??????
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_JUMP_CORE_SUCCESS));
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //??????
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_JUMP_CORE_SUCCESS));
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //??????
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_JUMP_CORE_SUCCESS));
        } else if (AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //??????????????????id
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_RFID_CODE_SUCCESS));
        } else {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
    }

    /**
     * app????????????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     * @return void
     */
    public void updatePortBindingStateForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        //???????????????????????????
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationJumpFiberInfoForApp);
        }
        //???????????????????????????
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationJumpFiberInfoForApp);
        }
        //???????????????????????????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            updatePortStatusAsync.updatePortBindingStateToUpdateForApp(batchOperationJumpFiberInfoForApp);
        }
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return void
     */
    public void updateJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            JumpFiberInfoReqForApp jumpFiberInfoReqForApp = new JumpFiberInfoReqForApp();
            BeanUtils.copyProperties(operationJumpFiberInfoReqForApp,jumpFiberInfoReqForApp);
            jumpFiberInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
            jumpFiberInfoReqForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            //??????????????????
            if (JumpCoreConstant.BRANCHING_UNIT_1.equals(jumpFiberInfoReqForApp.getBranchingUnit())){
                jumpFiberInfoDao.deleteJumpFiberInfoByPortInfoToBranchingUnitFroApp(jumpFiberInfoReqForApp);
            } else {
                jumpFiberInfoDao.deleteJumpFiberInfoByPortInfoFroApp(jumpFiberInfoReqForApp);
            }
            Set<String> rfIdCodeList = new HashSet<>();
            rfIdCodeList.add(jumpFiberInfoReqForApp.getRfidCode());
            rfIdCodeList.add(jumpFiberInfoReqForApp.getOppositeRfidCode());
            rfidInfoService.deleteRfidInfo(rfIdCodeList);
        }
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return void
     */
    public void deleteJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){

            //????????????????????????
            BeanUtils.copyProperties(operationJumpFiberInfoReqForApp,queryJumpFiberInfoReq);
            List<JumpFiberInfo> jumpFiberInfoList = jumpFiberInfoDao.getJumpFiberRfidCodeByJumpFiberInfoForApp(queryJumpFiberInfoReq);

            JumpFiberInfoReqForApp jumpFiberInfoReqForApp = new JumpFiberInfoReqForApp();
            BeanUtils.copyProperties(operationJumpFiberInfoReqForApp,jumpFiberInfoReqForApp);
            jumpFiberInfoReqForApp.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            jumpFiberInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            jumpFiberInfoDao.deleteJumpFiberInfoByJumpFiberInfoForApp(jumpFiberInfoReqForApp);

            //??????????????????????????????
            for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoList){
                Set<String> rfIdCodeList = new HashSet<>();
                rfIdCodeList.add(jumpFiberInfo.getRfidCode());
                rfIdCodeList.add(jumpFiberInfo.getOppositeRfidCode());
                rfidInfoService.deleteRfidInfo(rfIdCodeList);
            }
        }
    }

    /**
     * app??????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return void
     */
    public void saveJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        List<InsertRfidInfoReq> insertRfIdInfoReqList = new ArrayList<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            //????????????uuid
            operationJumpFiberInfoReqForApp.setJumpFiberId(NineteenUUIDUtils.uuid());
            operationJumpFiberInfoReqForApp.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            operationJumpFiberInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());

            //????????????????????????
            //????????????????????????
            InsertRfidInfoReq insertRfidInfoReq = new InsertRfidInfoReq();
            insertRfidInfoReq.setRfidCode(operationJumpFiberInfoReqForApp.getRfidCode());
            insertRfidInfoReq.setMarkType(operationJumpFiberInfoReqForApp.getMarkType());
            insertRfidInfoReq.setRfidStatus(operationJumpFiberInfoReqForApp.getRfidStatus());
            insertRfidInfoReq.setDeviceId(operationJumpFiberInfoReqForApp.getDeviceId());
            //????????????
            insertRfidInfoReq.setRfidType(RfIdConstant.RFID_TYPE_JUMP_FIBER);
            insertRfIdInfoReqList.add(insertRfidInfoReq);
            //????????????????????????
            InsertRfidInfoReq oppositeInsertRfIdInfoReq = new InsertRfidInfoReq();
            oppositeInsertRfIdInfoReq.setRfidCode(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
            oppositeInsertRfIdInfoReq.setMarkType(operationJumpFiberInfoReqForApp.getOppositeMarkType());
            oppositeInsertRfIdInfoReq.setRfidStatus(operationJumpFiberInfoReqForApp.getOppositeRfidStatus());
            oppositeInsertRfIdInfoReq.setDeviceId(operationJumpFiberInfoReqForApp.getOppositeDeviceId());
            //????????????
            oppositeInsertRfIdInfoReq.setRfidType(RfIdConstant.RFID_TYPE_JUMP_FIBER);
            insertRfIdInfoReqList.add(oppositeInsertRfIdInfoReq);
            //?????????????????????
            if (operationJumpFiberInfoReqForApp.getDeviceId().equals(operationJumpFiberInfoReqForApp.getOppositeDeviceId())){
                operationJumpFiberInfoReqForApp.setInnerDevice(JumpCoreConstant.IS_INNER_DEVICE);
            } else {
                operationJumpFiberInfoReqForApp.setInnerDevice(JumpCoreConstant.NOT_INNER_DEVICE);
            }
        }
        jumpFiberInfoDao.addJumpFiberInfo(batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList());
        //??????????????????????????????
        rfidInfoService.addRfidInfo(insertRfIdInfoReqList);
    }

    /**
     * app????????????????????????id
     *
     * @param batchOperationJumpFiberInfoForApp app????????????
     *
     * @return void
     */
    public void updateRfIdCodeForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        Set<String> rfIdCodeListHis = new HashSet<>();
        List<InsertRfidInfoReq> insertRfIdInfoReqList = new ArrayList<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            //????????????id???????????????rfid
            String deviceId;
            List<JumpFiberInfo> jumpFiberInfoList = jumpFiberInfoDao.getJumpFiberInfoByRfidCode(operationJumpFiberInfoReqForApp.getRfidCode());
            if (ObjectUtils.isEmpty(jumpFiberInfoList)){
                throw new FilinkJumpInfoException();
            }
            if (operationJumpFiberInfoReqForApp.getRfidCode().equals(jumpFiberInfoList.get(0).getRfidCode())){
                deviceId = jumpFiberInfoList.get(0).getDeviceId();
            } else {
                deviceId = jumpFiberInfoList.get(0).getOppositeDeviceId();
            }

            //????????????????????????id
            jumpFiberInfoDao.updateThisRfIdCodeForApp(operationJumpFiberInfoReqForApp);
            //???????????????????????????id
            jumpFiberInfoDao.updateOppositeRfIdCodeForApp(operationJumpFiberInfoReqForApp);
            //?????????
            rfIdCodeListHis.add(operationJumpFiberInfoReqForApp.getRfidCode());
            //?????????
            InsertRfidInfoReq insertRfidInfoReq = new InsertRfidInfoReq();
            //????????????
            insertRfidInfoReq.setRfidType(RfIdConstant.RFID_TYPE_JUMP_FIBER);
            insertRfidInfoReq.setRfidCode(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
            insertRfidInfoReq.setMarkType(operationJumpFiberInfoReqForApp.getOppositeMarkType());
            insertRfidInfoReq.setRfidStatus(operationJumpFiberInfoReqForApp.getOppositeRfidStatus());
            insertRfidInfoReq.setDeviceId(deviceId);
            insertRfIdInfoReqList.add(insertRfidInfoReq);
        }
        //??????????????????????????????
        rfidInfoService.deleteRfidInfo(rfIdCodeListHis);
        //??????????????????????????????
        rfidInfoService.addRfidInfo(insertRfIdInfoReqList);
    }

    /*-------------------------------------??????app????????????????????????start-------------------------------------*/
    /**
     * ??????app????????????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app??????????????????
     * @return void
     */
    public void saveOperatorLogForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp) {
        List list = new ArrayList();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            if (AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataId("rfidCode");
                addLogBean.setDataName("rfidCode");
                addLogBean.setOptObj(operationJumpFiberInfoReqForApp.getRfidCode());
                addLogBean.setOptObjId(operationJumpFiberInfoReqForApp.getRfidCode());
            } else {
                addLogBean.setDataId("jumpFiberId");
                addLogBean.setDataName("portNo");
                addLogBean.setOptObj(operationJumpFiberInfoReqForApp.getPortNo());
                addLogBean.setOptObjId(operationJumpFiberInfoReqForApp.getJumpFiberId());
            }

            //??????
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_JUMP_CORE_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_JUMP_CORE_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_JUMP_CORE_FUNCTION_CODE);
            } else if (AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.OPERATOR_TYPE_UPDATE_RFID_CODE);
            }

            //?????????????????????pda????????????
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);

            list.add(addLogBean);
        }
        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /*-------------------------------------??????app????????????????????????end-------------------------------------*/

    /*-------------------------------------??????????????????????????????start-------------------------------------*/
    /**
     * ??????????????????????????????
     *
     * @param jumpFiberInfoList ??????????????????
     * @return void
     */
    public void saveOperatorLogToDelete(List<JumpFiberInfoResp> jumpFiberInfoList) {
        List list = new ArrayList();
        for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("rfidCode");
            addLogBean.setDataName("rfidCode");
            addLogBean.setOptObj(jumpFiberInfo.getRfidCode());
            addLogBean.setOptObjId(jumpFiberInfo.getRfidCode());
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_JUMP_CORE_FUNCTION_CODE);

            list.add(addLogBean);
        }
        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /*-------------------------------------??????????????????????????????end-------------------------------------*/

    /*-------------------------------------????????????????????????????????????start-------------------------------------*/
    /**
     * app????????????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app??????????????????
     * @return Boolean
     */
    public void portStatisticsForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp) {
        Set<String> deviceIds = new HashSet<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()) {
            deviceIds.add(operationJumpFiberInfoReqForApp.getDeviceId());
            deviceIds.add(operationJumpFiberInfoReqForApp.getOppositeDeviceId());
        }
        List deviceIdList = new ArrayList(deviceIds);
        odnFacilityResourcesStatisticsService.portStatistics(deviceIdList);
    }

    /**
     * ????????????????????????
     *
     * @param jumpFiberInfoList ??????????????????
     * @return Boolean
     */
    public void portStatistics(List<JumpFiberInfoResp> jumpFiberInfoList) {
        Set<String> deviceIds = new HashSet<>();
        for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoList) {
            deviceIds.add(jumpFiberInfo.getDeviceId());
            deviceIds.add(jumpFiberInfo.getOppositeDeviceId());
        }
        List deviceIdList = new ArrayList(deviceIds);
        odnFacilityResourcesStatisticsService.portStatistics(deviceIdList);
    }
    /*-------------------------------------????????????????????????????????????end-------------------------------------*/

    /*-------------------------------------?????????????????????start-------------------------------------*/
    /**
     * app?????????????????????
     *
     * @param jumpFiberInfoRespForAppList app??????????????????
     *
     * @return List<JumpFiberInfoRespForApp> ??????????????????
     */
    public List<JumpFiberInfoRespForApp> assemblyJumpFiberInfoThisAndOppositeForApp(List<JumpFiberInfoRespForApp> jumpFiberInfoRespForAppList,QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp) {
        //???????????????????????????
        List<JumpFiberInfoRespForApp> jumpFiberInfoRespListTemp = new ArrayList<>();
        for (JumpFiberInfoRespForApp jumpFiberInfoRespForApp : jumpFiberInfoRespForAppList){
            JumpFiberInfoRespForApp jumpFiberInfoRespForAppTemp = new JumpFiberInfoRespForApp();

            //????????????????????????????????????
            if (!StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getRfidCode())){
                if (!queryJumpFiberInfoReqForApp.getRfidCode().equals(jumpFiberInfoRespForApp.getRfidCode())){
                    //?????????????????????
                    jumpFiberInfoRespForAppTemp = exchangeJumpFiberInfoThisAndOppositeForApp(jumpFiberInfoRespForApp,jumpFiberInfoRespForAppTemp);

                    jumpFiberInfoRespListTemp.add(jumpFiberInfoRespForAppTemp);
                } else {
                    jumpFiberInfoRespListTemp.add(jumpFiberInfoRespForApp);
                }
                //???????????????????????????????????????
            } else if (!StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getDeviceId())
                    && !StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getBoxSide())
                    && !StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getFrameNo())
                    && !StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getDiscSide())
                    && !StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getDiscNo())
                    && !StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getPortNo())
            ){
                if (!(queryJumpFiberInfoReqForApp.getDeviceId() + "-"
                        + queryJumpFiberInfoReqForApp.getBoxSide() + "-"
                        + queryJumpFiberInfoReqForApp.getFrameNo() + "-"
                        + queryJumpFiberInfoReqForApp.getDiscSide() + "-"
                        + queryJumpFiberInfoReqForApp.getDiscNo() +"-"
                        + queryJumpFiberInfoReqForApp.getPortNo()).equals(
                        (jumpFiberInfoRespForApp.getDeviceId() + "-"
                                + jumpFiberInfoRespForApp.getBoxSide() + "-"
                                + jumpFiberInfoRespForApp.getFrameNo() + "-"
                                + jumpFiberInfoRespForApp.getDiscSide() + "-"
                                + jumpFiberInfoRespForApp.getDiscNo() +"-"
                                + jumpFiberInfoRespForApp.getPortNo()))){
                    //?????????????????????
                    jumpFiberInfoRespForAppTemp = exchangeJumpFiberInfoThisAndOppositeForApp(jumpFiberInfoRespForApp,jumpFiberInfoRespForAppTemp);

                    jumpFiberInfoRespListTemp.add(jumpFiberInfoRespForAppTemp);
                } else {
                    jumpFiberInfoRespListTemp.add(jumpFiberInfoRespForApp);
                }
            } else {
                jumpFiberInfoRespListTemp.add(jumpFiberInfoRespForApp);
            }
        }
        return jumpFiberInfoRespListTemp;
    }

    /**
     * app???????????????????????????
     *
     * @param jumpFiberInfoRespForApp app????????????
     *
     * @return JumpFiberInfoRespForApp app????????????
     */
    public JumpFiberInfoRespForApp exchangeJumpFiberInfoThisAndOppositeForApp(JumpFiberInfoRespForApp jumpFiberInfoRespForApp,JumpFiberInfoRespForApp jumpFiberInfoRespForAppTemp){
        //?????????????????????
        BeanUtils.copyProperties(jumpFiberInfoRespForApp,jumpFiberInfoRespForAppTemp);

        jumpFiberInfoRespForAppTemp.setRfidCode(jumpFiberInfoRespForApp.getOppositeRfidCode());
        jumpFiberInfoRespForAppTemp.setDeviceId(jumpFiberInfoRespForApp.getOppositeDeviceId());
        jumpFiberInfoRespForAppTemp.setBoxSide(jumpFiberInfoRespForApp.getOppositeBoxSide());
        jumpFiberInfoRespForAppTemp.setFrameNo(jumpFiberInfoRespForApp.getOppositeFrameNo());
        jumpFiberInfoRespForAppTemp.setDiscSide(jumpFiberInfoRespForApp.getOppositeDiscSide());
        jumpFiberInfoRespForAppTemp.setDiscNo(jumpFiberInfoRespForApp.getOppositeDiscNo());
        jumpFiberInfoRespForAppTemp.setPortNo(jumpFiberInfoRespForApp.getOppositePortNo());
        jumpFiberInfoRespForAppTemp.setRemark(jumpFiberInfoRespForApp.getOppositeRemark());

        jumpFiberInfoRespForAppTemp.setOppositeRfidCode(jumpFiberInfoRespForApp.getRfidCode());
        jumpFiberInfoRespForAppTemp.setOppositeDeviceId(jumpFiberInfoRespForApp.getDeviceId());
        jumpFiberInfoRespForAppTemp.setOppositeBoxSide(jumpFiberInfoRespForApp.getBoxSide());
        jumpFiberInfoRespForAppTemp.setOppositeFrameNo(jumpFiberInfoRespForApp.getFrameNo());
        jumpFiberInfoRespForAppTemp.setOppositeDiscSide(jumpFiberInfoRespForApp.getDiscSide());
        jumpFiberInfoRespForAppTemp.setOppositeDiscNo(jumpFiberInfoRespForApp.getDiscNo());
        jumpFiberInfoRespForAppTemp.setOppositePortNo(jumpFiberInfoRespForApp.getPortNo());
        jumpFiberInfoRespForAppTemp.setOppositeRemark(jumpFiberInfoRespForApp.getRemark());

        return jumpFiberInfoRespForAppTemp;
    }
    /*-------------------------------------?????????????????????end-------------------------------------*/

    /*-------------------------------------??????????????????????????????start-------------------------------------*/
    /**
     * ??????????????????
     *
     * @param jumpFiberInfoRespList ??????????????????
     * @param queryJumpFiberInfoReq ??????????????????
     *
     * @return jumpFiberInfoRespList ??????????????????
     */
    @Override
    @SuppressWarnings("all")
    public List<JumpFiberInfoResp> assemblyJumpFiberInfoResp(List<JumpFiberInfoResp> jumpFiberInfoRespList,QueryJumpFiberInfoReq queryJumpFiberInfoReq){
        List<JumpFiberInfoResp> jumpFiberInfoRespListResult = new ArrayList<>();
        //????????????????????????
        Set<String> deviceIds = new HashSet<>();
        for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespList){
            deviceIds.add(jumpFiberInfoResp.getDeviceId());
            deviceIds.add(jumpFiberInfoResp.getOppositeDeviceId());
        }
        String[] deviceArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceArray);
        Map<String, DeviceInfoDto> deviceInfoMap = new HashMap<>(64);
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);

        //???????????????????????????
        jumpFiberInfoRespListResult = JumpFiberInfoResp.assemblyJumpFiberInfoThisAndOpposite(jumpFiberInfoRespList,queryJumpFiberInfoReq);

        //??????????????????
        if (!ObjectUtils.isEmpty(deviceInfoDtoList)){
            log.info("????????????????????????????????????");
            for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList){
                deviceInfoMap.put(deviceInfoDto.getDeviceId(),deviceInfoDto);
            }

            for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespListResult){
                jumpFiberInfoResp.setDeviceName(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceName());
                jumpFiberInfoResp.setDeviceType(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceType());
                jumpFiberInfoResp.setDeviceTypeName(JumpFiberInfoResp.getDeviceTypeName(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceType()));

                jumpFiberInfoResp.setOppositeDeviceName(deviceInfoMap.get(jumpFiberInfoResp.getOppositeDeviceId()).getDeviceName());
                jumpFiberInfoResp.setOppositeDeviceType(deviceInfoMap.get(jumpFiberInfoResp.getOppositeDeviceId()).getDeviceType());
                jumpFiberInfoResp.setOppositeDeviceTypeName(JumpFiberInfoResp.getDeviceTypeName(deviceInfoMap.get(jumpFiberInfoResp.getOppositeDeviceId()).getDeviceType()));
            }
        }

        //??????????????????????????????
        List<JumpFiberInfoResp> jumpFiberInfoRespListTemp = templateService.batchQueryPortInfo(jumpFiberInfoRespListResult);
        if (!ObjectUtils.isEmpty(jumpFiberInfoRespListTemp)){
            jumpFiberInfoRespListResult = jumpFiberInfoRespListTemp;
            log.info("????????????????????????????????????");
        }

        //?????????????????????
        for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespListResult){
            //??????????????????id
            PortInfoReqDto oppositePortInfoReqDto = new PortInfoReqDto();
            oppositePortInfoReqDto.setDeviceId(jumpFiberInfoResp.getOppositeDeviceId());
            oppositePortInfoReqDto.setBoxSide(jumpFiberInfoResp.getOppositeBoxSide());
            oppositePortInfoReqDto.setFrameNo(jumpFiberInfoResp.getOppositeFrameNo());
            oppositePortInfoReqDto.setDiscSide(jumpFiberInfoResp.getOppositeDiscSide());
            oppositePortInfoReqDto.setDiscNo(jumpFiberInfoResp.getOppositeDiscNo());
            oppositePortInfoReqDto.setPortNo(jumpFiberInfoResp.getOppositePortNo());
            String oppositePortId = templateService.queryPortIdByPortInfo(oppositePortInfoReqDto);
            jumpFiberInfoResp.setOppositePortNo(templateService.queryPortNumByPortId(oppositePortId));
        }
        log.info("????????????????????????????????????");
        return jumpFiberInfoRespListResult;
    }
    /*-------------------------------------??????????????????????????????end-------------------------------------*/

    /**
     * ?????????????????????????????????
     *
     * @param batchOperationJumpFiberInfoForApp app??????????????????
     *
     * @return Boolean
     */
    public Boolean checkJumpFiberUsed(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        List<String> deviceIds = new ArrayList<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            if (!deviceIds.contains(operationJumpFiberInfoReqForApp.getDeviceId())){
                deviceIds.add(operationJumpFiberInfoReqForApp.getDeviceId());
            }
            if (!deviceIds.contains(operationJumpFiberInfoReqForApp.getOppositeDeviceId())){
                deviceIds.add(operationJumpFiberInfoReqForApp.getOppositeDeviceId());
            }
        }
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq.setDeviceIds(deviceIds);
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);
        Map<String,JumpFiberInfoResp> jumpFiberInfoRespMap = new HashMap<>(64);
        for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespList){
            //??????
            jumpFiberInfoRespMap.put(jumpFiberInfoResp.getDeviceId() + "-"
                                    + jumpFiberInfoResp.getBoxSide() + "-"
                                    + jumpFiberInfoResp.getFrameNo() + "-"
                                    + jumpFiberInfoResp.getDiscSide() + "-"
                                    + jumpFiberInfoResp.getDiscNo() + "-"
                                    + jumpFiberInfoResp.getPortNo(),
                           jumpFiberInfoResp);

            //??????
            jumpFiberInfoRespMap.put(jumpFiberInfoResp.getOppositeDeviceId() + "-"
                                    + jumpFiberInfoResp.getOppositeBoxSide() + "-"
                                    + jumpFiberInfoResp.getOppositeFrameNo() + "-"
                                    + jumpFiberInfoResp.getOppositeDiscSide() + "-"
                                    + jumpFiberInfoResp.getOppositeDiscNo() + "-"
                                    + jumpFiberInfoResp.getOppositePortNo(),
                            jumpFiberInfoResp);
        }

        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            //???????????????
            if (!ObjectUtils.isEmpty(
                                        jumpFiberInfoRespMap.get(operationJumpFiberInfoReqForApp.getDeviceId() + "-"
                                                                + operationJumpFiberInfoReqForApp.getBoxSide() + "-"
                                                                + operationJumpFiberInfoReqForApp.getFrameNo() + "-"
                                                                + operationJumpFiberInfoReqForApp.getDiscSide() + "-"
                                                                + operationJumpFiberInfoReqForApp.getDiscNo() + "-"
                                                                + operationJumpFiberInfoReqForApp.getPortNo())
                                    )
            ){
                return true;
            }

            //?????????????????????????????????????????????????????????????????????
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                //???????????????
                if (!ObjectUtils.isEmpty(
                        jumpFiberInfoRespMap.get(operationJumpFiberInfoReqForApp.getOppositeDeviceId() + "-"
                                + operationJumpFiberInfoReqForApp.getOppositeBoxSide() + "-"
                                + operationJumpFiberInfoReqForApp.getOppositeFrameNo() + "-"
                                + operationJumpFiberInfoReqForApp.getOppositeDiscSide() + "-"
                                + operationJumpFiberInfoReqForApp.getOppositeDiscNo() + "-"
                                + operationJumpFiberInfoReqForApp.getOppositePortNo())
                )
                ){
                    return true;
                }
            }
        }
        return false;
    }

}
