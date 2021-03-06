package com.fiberhome.filink.rfid.service.impl.rfid;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.rfid.OpticCableSectionRfidInfoDao;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.rfid.DeleteOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.OpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.UpdateOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.app.OpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.resp.rfid.OpticCableSectionRfidInfoResp;
import com.fiberhome.filink.rfid.resp.rfid.app.OpticCableSectionRfidInfoRespApp;
import com.fiberhome.filink.rfid.service.rfid.OpticCableSectionRfidInfoService;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * ?????????rfId????????? ???????????????
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-30
 */
@Service
@Slf4j
public class OpticCableSectionRfidInfoServiceImpl extends ServiceImpl<OpticCableSectionRfidInfoDao, OpticCableSectionRfidInfo> implements OpticCableSectionRfidInfoService {

    /**
     * ?????????gis????????? Mapper ??????
     */
    @Autowired
    private OpticCableSectionRfidInfoDao opticCableSectionRfidInfoDao;

    /**
     * ??????????????? Mapper ??????
     */
    @Autowired
    private OpticCableInfoDao opticCableInfoDao;

    /**
     * ?????????????????? Mapper ??????
     */
    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    /**
     * ??????api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ??????api
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * ????????????SystemLanguage??????
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ?????????????????????????????????
     *
     * @param queryCondition ????????????
     * @return Result
     */
    @Override
    public Result opticCableSectionRfidInfoById(QueryCondition<OpticCableSectionRfidInfoReq> queryCondition) {
        List<OpticCableSectionRfidInfoResp> list = opticCableSectionRfidInfoDao
                .opticCableSectionById(queryCondition);
        return ResultUtils.success(list);
    }

    /**
     * app?????????????????????????????????
     *
     * @param queryCondition ????????????
     * @return Result
     */
    @Override
    public Result queryOpticCableSectionRfidInfo(OpticCableSectionRfidInfoReqApp queryCondition) {
        List<OpticCableSectionRfidInfoRespApp> list = opticCableSectionRfidInfoDao
                .queryOpticCableSectionRfidInfo(queryCondition);
        return ResultUtils.success(list);
    }

    /**
     * ???????????????GIS????????????
     *
     * @param uploadOpticCableSectionRfidInfoReqApp GIS????????????
     * @return Result
     */
    @Override
    public Result uploadOpticCableSectionRfidInfo(UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp) {

        //??????rfIdCode???????????????
        for (OpticCableSectionRfidInfo opticCableSectionRfidInfo : uploadOpticCableSectionRfidInfoReqApp.getSegmentGISList()){
            List<String> rfIdCodeList = opticCableSectionRfidInfoDao.checkRfidCodeListIsExist(opticCableSectionRfidInfo.getRfidCode());
            if (0 < rfIdCodeList.size()){
                return ResultUtils.warn(RfIdResultCodeConstant.RFID_CODE_IS_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.RFID_CODE_IS_EXISTS));
            }
        }

        int total = 0;
        //??????
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(uploadOpticCableSectionRfidInfoReqApp.getUploadType())) {
            for (OpticCableSectionRfidInfo rfIdInfo : uploadOpticCableSectionRfidInfoReqApp.getSegmentGISList()) {
                //????????????id
                if (StringUtils.isEmpty(rfIdInfo.getRfidCode())){
                    rfIdInfo.setRfidCode(UUIDUtil.getInstance().UUID32());
                }
                rfIdInfo.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                rfIdInfo.setCreateUser(RequestInfoUtils.getUserId());
                rfIdInfo.setOpticStatusId(NineteenUUIDUtils.uuid());
            }
            total = opticCableSectionRfidInfoDao.addOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp);
            log.info("app???????????????gis????????????");
        }
        //??????
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(uploadOpticCableSectionRfidInfoReqApp.getUploadType())) {
            uploadOpticCableSectionRfidInfoReqApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            uploadOpticCableSectionRfidInfoReqApp.setUpdateUser(NineteenUUIDUtils.uuid());
            total = opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp);
            log.info("app???????????????gis????????????");
        }
        //??????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(uploadOpticCableSectionRfidInfoReqApp.getUploadType())) {
            for (OpticCableSectionRfidInfo rfIdInfo : uploadOpticCableSectionRfidInfoReqApp.getSegmentGISList()) {
                rfIdInfo.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                rfIdInfo.setUpdateUser(RequestInfoUtils.getUserId());
            }
            total = opticCableSectionRfidInfoDao.updateOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp);
            log.info("app???????????????gis????????????");
        }
        if (total < 1) {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //??????????????????
        this.saveOperatorLogForApp(uploadOpticCableSectionRfidInfoReqApp);
        log.info("app?????????gis????????????????????????");
        return ResultUtils.success();
    }

    /**
     * ???????????????id???????????????gis??????
     *
     * @param opticCableSectionId ?????????id
     *
     * @return Result
     */
    @Override
    public Result queryOpticCableSectionRfidInfoByOpticCableSectionId(String opticCableSectionId) {
        return getOpticCableSectionRfidInfo(null,opticCableSectionId);
    }

    /**
     * ????????????id???????????????gis??????
     *
     * @param opticCableId ?????????id
     *
     * @return Result
     */
    @Override
    public Result queryOpticCableSectionRfidInfoByOpticCableId(String opticCableId) {
        return getOpticCableSectionRfidInfo(opticCableId,null);
    }

    /**
     * ??????id?????????gis????????????
     *
     * @param updateOpticCableSectionRfidInfoReqList ?????????gis??????
     *
     * @return Result
     */
    @Override
    public Result updateOpticCableSectionRfidInfoPositionById(List<UpdateOpticCableSectionRfidInfoReq> updateOpticCableSectionRfidInfoReqList) {
        for (UpdateOpticCableSectionRfidInfoReq updateOpticCableSectionRfidInfoReq : updateOpticCableSectionRfidInfoReqList){
            opticCableSectionRfidInfoDao.updateOpticCableSectionRfidInfoPositionById(updateOpticCableSectionRfidInfoReq);
        }
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS,I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_OPTIC_CABLE_SECTION_RFID_SUCCESS));
    }

    /**
     * app???????????????GIS??????????????????
     *
     * @param reqForApp ?????????rfId??????
     */
    public void saveOperatorLogForApp(UploadOpticCableSectionRfidInfoReqApp reqForApp) {
        List<AddLogBean> list = new ArrayList<>();
        for (OpticCableSectionRfidInfo opticCableSectionRfidInfo : reqForApp.getSegmentGISList()) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.OPTICAL_CABLE_SECTION_RFID_ID);
            addLogBean.setDataName(AppConstant.OPTICAL_CABLE_SECTION__RFID_NAME);
            addLogBean.setOptObj(opticCableSectionRfidInfo.getOpticStatusId());
            addLogBean.setOptObjId(opticCableSectionRfidInfo.getRfidCode());
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(reqForApp.getUploadType())) {
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_OPTIC_CABLE_SECTION_RFID_INFO_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(reqForApp.getUploadType())) {
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_OPTIC_CABLE_SECTION_RFID_INFO_FUNCTION_CODE);
                //??????
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(reqForApp.getUploadType())) {
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_OPTIC_CABLE_SECTION_RFID_INFO_FUNCTION_CODE);
            }
            //?????????????????????pda????????????
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
            list.add(addLogBean);
        }

        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ???????????????rfid??????
     *
     * @param deleteOpticCableSectionRfidInfoReq ???????????????rfid??????
     * @return Result
     */
    @Override
    public Result deleteOpticCableSectionRfidInfoById(DeleteOpticCableSectionRfidInfoReq deleteOpticCableSectionRfidInfoReq){
        //????????????
        if (ObjectUtils.isEmpty(deleteOpticCableSectionRfidInfoReq.getOpticStatusIdList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        List<OpticCableSectionRfidInfo> opticCableSectionRfidInfoList = opticCableSectionRfidInfoDao.selectBatchIds(deleteOpticCableSectionRfidInfoReq.getOpticStatusIdList());
        //????????????
        if (ObjectUtils.isEmpty(opticCableSectionRfidInfoList) || opticCableSectionRfidInfoList.size() < deleteOpticCableSectionRfidInfoReq.getOpticStatusIdList().size()){
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_SECTION_RFID_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_RFID_NOT_EXIST));
        }

        //??????????????????
        for (OpticCableSectionRfidInfo opticCableSectionRfidInfo : opticCableSectionRfidInfoList){
            opticCableSectionRfidInfo.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            opticCableSectionRfidInfo.setUpdateUser(RequestInfoUtils.getUserId());
        }
        //??????????????????
        opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidById(opticCableSectionRfidInfoList);
        log.info("?????????gis??????????????????");

        //??????????????????
        this.saveOperatorLogToDelete(opticCableSectionRfidInfoList);
        log.info("???????????????gis??????????????????????????????");
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS,I18nUtils.getSystemString(RfIdI18nConstant.DELETE_OPTIC_CABLE_SECTION_RFID_SUCCESS));
    }

    /*-------------------------------------??????????????????????????????start-------------------------------------*/
    /**
     * ??????????????????????????????
     *
     * @param opticCableSectionRfidInfoList ?????????gis????????????
     * @return void
     */
    public void saveOperatorLogToDelete(List<OpticCableSectionRfidInfo> opticCableSectionRfidInfoList) {
        List list = new ArrayList();
        for (OpticCableSectionRfidInfo opticCableSectionRfidInfo : opticCableSectionRfidInfoList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.OPTICAL_CABLE_SECTION_RFID_ID);
            addLogBean.setDataName(AppConstant.OPTICAL_CABLE_SECTION__RFID_NAME);
            addLogBean.setOptObj(opticCableSectionRfidInfo.getOpticStatusId());
            addLogBean.setOptObjId(opticCableSectionRfidInfo.getRfidCode());
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_OPTIC_CABLE_SECTION_RFID_INFO_FUNCTION_CODE);
            list.add(addLogBean);
        }
        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /*-------------------------------------??????????????????????????????end-------------------------------------*/

    /*-------------------------------------??????????????????????????????start-------------------------------------*/
    /**
     * ?????????????????????????????????????????????
     *
     * @param opticCableId ??????id
     * @param opticCableSectionId ?????????id
     *
     * @return Result
     */
    public Result getOpticCableSectionRfidInfo(String opticCableId,String opticCableSectionId){
        //????????????????????????id?????????????????????
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        if (!StringUtils.isEmpty(opticCableSectionId)){
            opticCableSectionInfoReq.setOpticCableSectionId(opticCableSectionId);
        }
        if (!StringUtils.isEmpty(opticCableId)){
            opticCableSectionInfoReq.setBelongOpticCableId(opticCableId);
        }
        List<OpticCableSectionInfo> opticCableSectionInfoList = opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(opticCableSectionInfoReq);
        Set<String> opticCableSectionIds = new HashSet<>();
        Set<String> deviceIds = new HashSet<>();
        for (OpticCableSectionInfo opticCableSectionInfo : opticCableSectionInfoList){
            opticCableSectionIds.add(opticCableSectionInfo.getOpticCableSectionId());
            deviceIds.add(opticCableSectionInfo.getStartNode());
            deviceIds.add(opticCableSectionInfo.getTerminationNode());
        }
        if (ObjectUtils.isEmpty(opticCableSectionIds) || ObjectUtils.isEmpty(deviceIds)){
            return ResultUtils.success(new ArrayList<>());
        }
        String[] deviceIdArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceIdArray);
        //????????????????????????????????????????????????????????????
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceIdArray);
        if (null == deviceInfoDtoList){
            return ResultUtils.warn(RfIdResultCodeConstant.DEVICE_SERVER_ERROR,I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_SERVER_ERROR));
        } else if (0 == deviceInfoDtoList.size()){
            return ResultUtils.warn(RfIdResultCodeConstant.DEVICE_NODE_NOT_EXISTS,I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_NODE_NOT_EXISTS));
        }
        //???????????????id??????gis??????
        List<OpticCableSectionRfidInfoResp> opticCableSectionRfIdInfoRespList = opticCableSectionRfidInfoDao.queryOpticCableSectionRfidInfoByOpticCableSectionId(opticCableSectionIds);
        //????????????????????????
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList){
            OpticCableSectionRfidInfoResp opticCableSectionRfidInfoResp = new OpticCableSectionRfidInfoResp();
            opticCableSectionRfidInfoResp.setPosition(deviceInfoDto.getPositionBase());
            opticCableSectionRfidInfoResp.setDeviceId(deviceInfoDto.getDeviceId());
            opticCableSectionRfidInfoResp.setDeviceType(deviceInfoDto.getDeviceType());
            opticCableSectionRfIdInfoRespList.add(opticCableSectionRfidInfoResp);
        }
        return ResultUtils.success(opticCableSectionRfIdInfoRespList);
    }

}
