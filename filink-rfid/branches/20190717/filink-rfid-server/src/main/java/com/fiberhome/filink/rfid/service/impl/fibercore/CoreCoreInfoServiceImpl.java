package com.fiberhome.filink.rfid.service.impl.fibercore;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.fibercore.CoreCoreInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.CoreCoreInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.resp.fibercore.CoreCoreInfoResp;
import com.fiberhome.filink.rfid.resp.fibercore.app.CoreCoreInfoRespForApp;
import com.fiberhome.filink.rfid.service.fibercore.CoreCoreInfoService;
import com.fiberhome.filink.rfid.service.impl.UpdateOpticCableSectionStatus;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import com.fiberhome.filink.rfid.utils.RfidServerPermission;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class CoreCoreInfoServiceImpl extends ServiceImpl<CoreCoreInfoDao, CoreCoreInfo> implements CoreCoreInfoService {

    @Autowired
    private CoreCoreInfoDao coreCoreInfoDao;

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
     * ??????Feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * ??????Feign
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * ????????????????????????rfidServerPermission
     */
    @Autowired
    private RfidServerPermission rfidServerPermission;

    @Autowired
    private OpticCableSectionInfoService opticCableSectionInfoService;

    /**
     * ??????updateOpticCableSectionStatusAsync??????
     */
    @Autowired
    private UpdateOpticCableSectionStatus updateOpticCableSectionStatusAsync;

    /**
     * ??????????????????
     *
     * @param coreCoreInfoReq ????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCoreCoreInfo(CoreCoreInfoReq coreCoreInfoReq) {
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfo(coreCoreInfoReq);
        return ResultUtils.success(coreCoreInfoRespList);
    }

    /**
     * ??????????????????????????????
     *
     * @param coreCoreInfoReq ????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCoreCoreInfoNotInDevice(CoreCoreInfoReq coreCoreInfoReq) {
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfoNotInDevice(coreCoreInfoReq);
        return ResultUtils.success(coreCoreInfoRespList);
    }

    /**
     * ??????????????????
     *
     * @param insertCoreCoreInfoReqList ??????????????????????????????
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveCoreCoreInfo(List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList) {
        //?????????????????????????????????
        if (ObjectUtils.isEmpty(insertCoreCoreInfoReqList)){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //?????????????????????????????????
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList){
            //????????????
            if (StringUtils.isEmpty(insertCoreCoreInfoReq.getIntermediateNodeDeviceId()) ||
                    StringUtils.isEmpty(insertCoreCoreInfoReq.getOppositeResource()) ||
                    StringUtils.isEmpty(insertCoreCoreInfoReq.getResource())
            ){
                return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
        }

        //?????????????????????????????????
        Set<String> deviceIds = new HashSet<>();
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList){
            deviceIds.add(insertCoreCoreInfoReq.getIntermediateNodeDeviceId());
        }
        if (rfidServerPermission.getPermissionsInfoFoRfidServer(deviceIds, RequestInfoUtils.getUserId())){
            return ResultUtils.warn(RfIdResultCodeConstant.NOT_HAVE_PERMISSION_FOR_RFID, I18nUtils.getSystemString(RfIdI18nConstant.NOT_HAVE_PERMISSION_FOR_RFID));
        }

        CoreCoreInfoReq coreCoreInfoReq = new CoreCoreInfoReq();
        BeanUtils.copyProperties(insertCoreCoreInfoReqList.get(0),coreCoreInfoReq);
        coreCoreInfoReq.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        coreCoreInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        coreCoreInfoDao.deleteCoreCoreInfoByResourceAndDevice(coreCoreInfoReq);
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList){
            insertCoreCoreInfoReq.setCoreCoreId(NineteenUUIDUtils.uuid());
            insertCoreCoreInfoReq.setCreateUser(RequestInfoUtils.getUserId());
            insertCoreCoreInfoReq.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        }

        //?????????????????????????????????????????????????????????????????????
        if (insertCoreCoreInfoReqList.size() == 1){
            //??????????????????????????????????????????????????????????????????
            if (!(StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getCableCoreNo()) && StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                coreCoreInfoDao.addCoreCoreInfo(insertCoreCoreInfoReqList);
                log.info("????????????????????????");
            }
        } else {
            coreCoreInfoDao.addCoreCoreInfo(insertCoreCoreInfoReqList);
            log.info("????????????????????????");
        }

        //????????????????????????
        Set<String> opticCableIds = new HashSet<>();
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList){
            opticCableIds.add(insertCoreCoreInfoReq.getResource());
            opticCableIds.add(insertCoreCoreInfoReq.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
            log.info("????????????????????????");
        }

        //???????????????????????????
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByCoreCore(insertCoreCoreInfoReqList);
        log.info("?????????????????????????????????");

        //??????????????????
        this.saveOperatorLog(insertCoreCoreInfoReqList);
        log.info("??????????????????????????????");

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_CORE_CORE_SUCCESS));
    }

    /**
     * app????????????????????????
     *
     * @param queryCoreCoreInfoReqForApp app????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCoreCoreInfoForApp(QueryCoreCoreInfoReqForApp queryCoreCoreInfoReqForApp) {
        List<CoreCoreInfoRespForApp> coreCoreInfoRespForAppList = coreCoreInfoDao.queryCoreCoreInfoForApp(queryCoreCoreInfoReqForApp);
        return ResultUtils.success(coreCoreInfoRespForAppList);
    }

    /**
     * app??????????????????
     *
     * @param batchOperationCoreCoreInfoReqForApp app????????????????????????
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result operationCoreCoreInfoReqForApp(BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp) {
        //?????????????????????????????????
        if (ObjectUtils.isEmpty(batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //????????????????????????????????????????????????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
                CoreCoreInfoForApp coreCoreInfoForApp = new CoreCoreInfoForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,coreCoreInfoForApp);
                coreCoreInfoForApp.setUpdateUser(RequestInfoUtils.getUserId());
                coreCoreInfoForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                coreCoreInfoDao.deleteCoreCoreInfoByCoreAndDeviceForApp(coreCoreInfoForApp);
            }
        }

        //???????????????????????????
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
                CoreCoreInfoForApp coreCoreInfoForApp = new CoreCoreInfoForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,coreCoreInfoForApp);
                coreCoreInfoForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                coreCoreInfoForApp.setUpdateUser(RequestInfoUtils.getUserId());
                coreCoreInfoDao.deleteCoreCoreInfoByCoreCoreInfoForApp(coreCoreInfoForApp);
                log.info("app????????????????????????");
            }
        }

        //???????????????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType()) || AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
                //????????????uuid
                operationCoreCoreInfoReqForApp.setCoreCoreId(NineteenUUIDUtils.uuid());
                operationCoreCoreInfoReqForApp.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                operationCoreCoreInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            }
            coreCoreInfoDao.saveCoreCoreInfoForApp(batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList());
            log.info("app????????????????????????");
        }

        //????????????????????????
        Set<String> opticCableIds = new HashSet<>();
        for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
            opticCableIds.add(operationCoreCoreInfoReqForApp.getResource());
            opticCableIds.add(operationCoreCoreInfoReqForApp.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
            log.info("app??????????????????");
        }

        //???????????????????????????
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByCoreCoreForApp(batchOperationCoreCoreInfoReqForApp);
        log.info("app?????????????????????????????????");

        //??????app????????????
        this.saveOperatorLogForApp(batchOperationCoreCoreInfoReqForApp);
        log.info("app??????????????????????????????");

        //??????
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_CORE_CORE_SUCCESS));
            //??????
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_CORE_CORE_SUCCESS));
            //??????
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_CORE_CORE_SUCCESS));
        } else {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
    }

    /**
     * ??????????????????
     *
     * @param insertCoreCoreInfoReqList ????????????
     * @return void
     */
    public void saveOperatorLog(List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList) {

        //????????????code
        String functionCode = LogFunctionCodeConstant.CLEAR_CORE_CORE_FUNCTION_CODE;

        //?????????????????????????????????????????????????????????????????????
        if (insertCoreCoreInfoReqList.size() == 1){
            //??????????????????????????????????????????????????????????????????
            if (!(StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getCableCoreNo()) && StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                functionCode = LogFunctionCodeConstant.ADD_CORE_CORE_FUNCTION_CODE;
            }
        } else {
            functionCode = LogFunctionCodeConstant.ADD_CORE_CORE_FUNCTION_CODE;
        }

        // ????????????????????????
        List list = new ArrayList();
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            //?????????????????????
            if (LogFunctionCodeConstant.CLEAR_CORE_CORE_FUNCTION_CODE.equals(functionCode)){
                addLogBean.setDataId("resource");
                addLogBean.setDataName("resource");
                addLogBean.setOptObj(insertCoreCoreInfoReq.getResource());
                addLogBean.setOptObjId(insertCoreCoreInfoReq.getResource());
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            } else {
                addLogBean.setDataId("coreCoreId");
                addLogBean.setDataName("cableCoreNo");
                addLogBean.setOptObj(insertCoreCoreInfoReq.getCableCoreNo());
                addLogBean.setOptObjId(insertCoreCoreInfoReq.getCoreCoreId());
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
            }
            addLogBean.setFunctionCode(functionCode);

            list.add(addLogBean);
        }
        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ??????app????????????
     *
     * @param batchOperationCoreCoreInfoReqForApp app??????????????????
     * @return void
     */
    public void saveOperatorLogForApp(BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp) {
        List list = new ArrayList();
        for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("coreCoreId");
            addLogBean.setDataName("cableCoreNo");
            addLogBean.setOptObj(operationCoreCoreInfoReqForApp.getCableCoreNo());
            addLogBean.setOptObjId(operationCoreCoreInfoReqForApp.getCoreCoreId());

            //??????
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_CORE_CORE_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_CORE_CORE_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_CORE_CORE_FUNCTION_CODE);
            }

            //?????????????????????pda????????????
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);

            list.add(addLogBean);
        }
        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
