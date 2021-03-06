package com.fiberhome.filink.rfid.service.impl.fibercore;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.UpdatePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.PortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.resp.fibercore.app.PortCableCoreInfoRespForApp;
import com.fiberhome.filink.rfid.service.fibercore.PortCableCoreInfoService;
import com.fiberhome.filink.rfid.service.impl.UpdateOpticCableSectionStatus;
import com.fiberhome.filink.rfid.service.impl.UpdatePortStatusAsync;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.fiberhome.filink.rfid.utils.RfidServerPermission;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
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
public class PortCableCoreInfoServiceImpl extends ServiceImpl<PortCableCoreInfoDao, PortCableCoreInfo> implements PortCableCoreInfoService {

    @Autowired
    private PortCableCoreInfoDao portCableCoreInfoDao;

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
     * ????????????????????????rfidServerPermission
     */
    @Autowired
    private RfidServerPermission rfidServerPermission;

    @Autowired
    private OpticCableSectionInfoService opticCableSectionInfoService;

    @Autowired
    private TemplateService templateService;

    /**
     * ??????updatePortStatusAsync??????
     */
    @Autowired
    private UpdatePortStatusAsync updatePortStatusAsync;

    /**
     * ??????updateOpticCableSectionStatusAsync??????
     */
    @Autowired
    private UpdateOpticCableSectionStatus updateOpticCableSectionStatusAsync;

    /**
     * ??????????????????
     *
     * @param portCableCoreInfoReq ????????????????????????
     *
     * @return Result
     */
    @Override
    public Result getPortCableCoreInfo(PortCableCoreInfoReq portCableCoreInfoReq) {
        List<PortCableCoreInfo> portCableCoreInfoList =  portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        return ResultUtils.success(portCableCoreInfoList);
    }

    /**
     * ??????????????????????????????
     *
     * @param portCableCoreInfoReq ????????????????????????
     *
     * @return Result
     */
    @Override
    public Result getPortCableCoreInfoNotInDevice(PortCableCoreInfoReq portCableCoreInfoReq) {
        List<PortCableCoreInfo> portCableCoreInfoList =  portCableCoreInfoDao.getPortCableCoreInfoNotInDevice(portCableCoreInfoReq);
        return ResultUtils.success(portCableCoreInfoList);
    }

    /**
     * ??????????????????
     *
     * @param insertPortCableCoreInfoReqList ??????????????????????????????
     * @param updatePortCableCoreInfoReqList ?????????????????????????????????
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result savePortCableCoreInfo(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList,List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList) {
        //?????????????????????????????????
        if (ObjectUtils.isEmpty(insertPortCableCoreInfoReqList)){
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //?????????????????????????????????
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            //????????????
            if (StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceDeviceId()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceBoxSide()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceFrameNo()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceDiscSide()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getOppositeResource())
            ){
                return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
        }
        //?????????????????????????????????
        Set<String> deviceIds = new HashSet<>();
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            deviceIds.add(insertPortCableCoreInfoReq.getResourceDeviceId());
        }
        if (rfidServerPermission.getPermissionsInfoFoRfidServer(deviceIds, RequestInfoUtils.getUserId())){
            return ResultUtils.warn(RfIdResultCode.NOT_HAVE_PERMISSION_FOR_RFID, I18nUtils.getSystemString(RfIdI18nConstant.NOT_HAVE_PERMISSION_FOR_RFID));
        }
        //??????????????????
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        BeanUtils.copyProperties(insertPortCableCoreInfoReqList.get(0),portCableCoreInfoReq);
        portCableCoreInfoReq.setUpdateTime(UtcTimeUtil.getUtcTime());
        portCableCoreInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        portCableCoreInfoDao.deletePortCoreInfoByResourceAndDevice(portCableCoreInfoReq);
        //????????????uuid
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            insertPortCableCoreInfoReq.setPortCoreId(NineteenUUIDUtils.uuid());
            insertPortCableCoreInfoReq.setUpdateTime(UtcTimeUtil.getUtcTime());
            insertPortCableCoreInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        }
        //?????????????????????????????????????????????????????????????????????
        if (insertPortCableCoreInfoReqList.size() == 1) {
            //????????????????????????????????????????????????????????????????????????????????????????????????
            if (!(StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getResourceDiscNo()) && StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                portCableCoreInfoDao.savePortCableCoreInfo(insertPortCableCoreInfoReqList);
                log.info("????????????????????????");
            }
        } else {
            portCableCoreInfoDao.savePortCableCoreInfo(insertPortCableCoreInfoReqList);
            log.info("????????????????????????");
        }
        //????????????????????????
        Set<String> opticCableIds = new HashSet<>();
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            opticCableIds.add(insertPortCableCoreInfoReq.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
            log.info("????????????????????????");
        }
        //????????????????????????
        updatePortStatusAsync.updatePortBindingState(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);
        log.info("??????????????????????????????");
        //??????????????????????????????
        updatePortStatusAsync.batchUpdatePortCableState(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);
        log.info("??????????????????????????????");
        //???????????????????????????
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByPortCableCore(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);
        log.info("?????????????????????????????????");
        //??????????????????
        this.saveOperatorLog(insertPortCableCoreInfoReqList);
        log.info("??????????????????????????????");
        return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_PORT_CORE_SUCCESS));
    }

    /**
     * app????????????????????????
     *
     * @param queryPortCableCoreInfoReqForApp app????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryPortCableCoreInfoForApp(QueryPortCableCoreInfoReqForApp queryPortCableCoreInfoReqForApp) {
        List<PortCableCoreInfoRespForApp> portCableCoreInfoRespForAppList = portCableCoreInfoDao.queryPortCableCoreInfoForApp(queryPortCableCoreInfoReqForApp);
        return ResultUtils.success(portCableCoreInfoRespForAppList);
    }

    /**
     * app????????????????????????
     *
     * @param batchOperationPortCableCoreInfoReqForApp app????????????????????????
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result operationPortCableCoreInfoForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp) {

        //?????????????????????????????????
        if (ObjectUtils.isEmpty(batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList())){
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //????????????????????????????????????????????????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            for (OperationPortCableCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
                PortCableCoreInfoReqForApp portCableCoreInfoReqForApp = new PortCableCoreInfoReqForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,portCableCoreInfoReqForApp);
                portCableCoreInfoReqForApp.setUpdateTime(UtcTimeUtil.getUtcTime());
                portCableCoreInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
                portCableCoreInfoDao.deletePortCoreInfoByPortForApp(portCableCoreInfoReqForApp);
            }
        }

        //???????????????????????????????????????
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            for (OperationPortCableCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
                PortCableCoreInfoReqForApp portCableCoreInfoReqForApp = new PortCableCoreInfoReqForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,portCableCoreInfoReqForApp);
                portCableCoreInfoReqForApp.setUpdateTime(UtcTimeUtil.getUtcTime());
                portCableCoreInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
                portCableCoreInfoDao.deletePortCoreInfoByPortCoreInfoForApp(portCableCoreInfoReqForApp);
            }

            //app????????????????????????
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationPortCableCoreInfoReqForApp);
            //app????????????????????????
            updatePortStatusAsync.batchUpdatePortCableStateForApp(batchOperationPortCableCoreInfoReqForApp);
        }

        //???????????????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType()) || AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
                operationPortCableCoreInfoReqForApp.setPortCoreId(NineteenUUIDUtils.uuid());
                operationPortCableCoreInfoReqForApp.setCreateTime(UtcTimeUtil.getUtcTime());
                operationPortCableCoreInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            }

            portCableCoreInfoDao.savePortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList());
            //app????????????????????????
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationPortCableCoreInfoReqForApp);
            //app????????????????????????
            updatePortStatusAsync.batchUpdatePortCableStateForApp(batchOperationPortCableCoreInfoReqForApp);
        }

        //app???????????????????????????
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByPortCableCoreForApp(batchOperationPortCableCoreInfoReqForApp);

        //????????????????????????
        Set<String> opticCableIds = new HashSet<>();
        for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
            opticCableIds.add(operationPortCableCoreInfoReqForApp.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
        }

        //??????app????????????
        this.saveOperatorLogForApp(batchOperationPortCableCoreInfoReqForApp);
        //??????
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_PORT_CORE_SUCCESS));
            //??????
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_PORT_CORE_SUCCESS));
            //??????
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_PORT_CORE_SUCCESS));
        } else {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
    }

    /**
     * ??????????????????
     *
     * @param insertPortCableCoreInfoReqList ????????????
     * @return void
     */
    public void saveOperatorLog(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList) {
        //????????????code
        String functionCode = LogFunctionCodeConstant.CLEAR_PORT_CORE_FUNCTION_CODE;

        //?????????????????????????????????????????????????????????????????????
        if (insertPortCableCoreInfoReqList.size() == 1) {
            //????????????????????????????????????????????????????????????????????????????????????????????????
            if (!(StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getResourceDiscNo()) && StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                functionCode = LogFunctionCodeConstant.ADD_PORT_CORE_FUNCTION_CODE;
            }
        } else {
            functionCode = LogFunctionCodeConstant.ADD_PORT_CORE_FUNCTION_CODE;
        }

        // ????????????????????????
        List list = new ArrayList();
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            //?????????????????????
            if (LogFunctionCodeConstant.CLEAR_PORT_CORE_FUNCTION_CODE.equals(functionCode)){
                addLogBean.setDataId("resourceDeviceId");
                addLogBean.setDataName("resourceFrameNo");
                addLogBean.setOptObj(insertPortCableCoreInfoReq.getResourceFrameNo());
                addLogBean.setOptObjId(insertPortCableCoreInfoReq.getResourceDeviceId());
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            } else {
                addLogBean.setDataId("portCoreId");
                addLogBean.setDataName("portNo");
                addLogBean.setOptObj(insertPortCableCoreInfoReq.getPortNo());
                addLogBean.setOptObjId(insertPortCableCoreInfoReq.getPortCoreId());
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
            }
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
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
     * @param batchOperationPortCableCoreInfoReqForApp app????????????
     * @return void
     */
    public void saveOperatorLogForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp) {
        // ????????????????????????
        List list = new ArrayList();
        for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("portCoreId");
            addLogBean.setDataName("portNo");
            addLogBean.setOptObjId(operationPortCableCoreInfoReqForApp.getPortCoreId());
            addLogBean.setOptObj(operationPortCableCoreInfoReqForApp.getPortNo());

            //??????
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_PORT_CORE_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_PORT_CORE_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_PORT_CORE_FUNCTION_CODE);
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
