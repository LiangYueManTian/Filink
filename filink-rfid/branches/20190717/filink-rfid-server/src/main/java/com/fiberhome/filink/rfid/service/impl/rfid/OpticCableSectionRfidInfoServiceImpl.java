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
 * 光缆段rfId信息表 服务实现类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-30
 */
@Service
@Slf4j
public class OpticCableSectionRfidInfoServiceImpl extends ServiceImpl<OpticCableSectionRfidInfoDao, OpticCableSectionRfidInfo> implements OpticCableSectionRfidInfoService {

    /**
     * 光缆段gis信息表 Mapper 接口
     */
    @Autowired
    private OpticCableSectionRfidInfoDao opticCableSectionRfidInfoDao;

    /**
     * 光缆信息表 Mapper 接口
     */
    @Autowired
    private OpticCableInfoDao opticCableInfoDao;

    /**
     * 光缆段信息表 Mapper 接口
     */
    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 设施api
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 远程调用SystemLanguage服务
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询光缆段智能标签列表
     *
     * @param queryCondition 查询条件
     * @return Result
     */
    @Override
    public Result opticCableSectionRfidInfoById(QueryCondition<OpticCableSectionRfidInfoReq> queryCondition) {
        List<OpticCableSectionRfidInfoResp> list = opticCableSectionRfidInfoDao
                .opticCableSectionById(queryCondition);
        return ResultUtils.success(list);
    }

    /**
     * app查询光缆段智能标签列表
     *
     * @param queryCondition 查询条件
     * @return Result
     */
    @Override
    public Result queryOpticCableSectionRfidInfo(OpticCableSectionRfidInfoReqApp queryCondition) {
        List<OpticCableSectionRfidInfoRespApp> list = opticCableSectionRfidInfoDao
                .queryOpticCableSectionRfidInfo(queryCondition);
        return ResultUtils.success(list);
    }

    /**
     * 上传光缆段GIS标签信息
     *
     * @param uploadOpticCableSectionRfidInfoReqApp GIS标签信息
     * @return Result
     */
    @Override
    public Result uploadOpticCableSectionRfidInfo(UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp) {

        //校验rfIdCode是否已存在
        for (OpticCableSectionRfidInfo opticCableSectionRfidInfo : uploadOpticCableSectionRfidInfoReqApp.getSegmentGISList()){
            List<String> rfIdCodeList = opticCableSectionRfidInfoDao.checkRfidCodeListIsExist(opticCableSectionRfidInfo.getRfidCode());
            if (0 < rfIdCodeList.size()){
                return ResultUtils.warn(RfIdResultCodeConstant.RFID_CODE_IS_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.RFID_CODE_IS_EXISTS));
            }
        }

        int total = 0;
        //新增
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(uploadOpticCableSectionRfidInfoReqApp.getUploadType())) {
            for (OpticCableSectionRfidInfo rfIdInfo : uploadOpticCableSectionRfidInfoReqApp.getSegmentGISList()) {
                //虚拟标签id
                if (StringUtils.isEmpty(rfIdInfo.getRfidCode())){
                    rfIdInfo.setRfidCode(UUIDUtil.getInstance().UUID32());
                }
                rfIdInfo.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                rfIdInfo.setCreateUser(RequestInfoUtils.getUserId());
                rfIdInfo.setOpticStatusId(NineteenUUIDUtils.uuid());
            }
            total = opticCableSectionRfidInfoDao.addOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp);
            log.info("app保存光缆段gis信息成功");
        }
        //删除
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(uploadOpticCableSectionRfidInfoReqApp.getUploadType())) {
            uploadOpticCableSectionRfidInfoReqApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            uploadOpticCableSectionRfidInfoReqApp.setUpdateUser(NineteenUUIDUtils.uuid());
            total = opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp);
            log.info("app删除光缆段gis信息成功");
        }
        //更新
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(uploadOpticCableSectionRfidInfoReqApp.getUploadType())) {
            for (OpticCableSectionRfidInfo rfIdInfo : uploadOpticCableSectionRfidInfoReqApp.getSegmentGISList()) {
                rfIdInfo.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                rfIdInfo.setUpdateUser(RequestInfoUtils.getUserId());
            }
            total = opticCableSectionRfidInfoDao.updateOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp);
            log.info("app修改光缆段gis信息成功");
        }
        if (total < 1) {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //保存操作日志
        this.saveOperatorLogForApp(uploadOpticCableSectionRfidInfoReqApp);
        log.info("app光缆段gis操作日志保存成功");
        return ResultUtils.success();
    }

    /**
     * 根据光缆段id查询光缆段gis列表
     *
     * @param opticCableSectionId 光缆段id
     *
     * @return Result
     */
    @Override
    public Result queryOpticCableSectionRfidInfoByOpticCableSectionId(String opticCableSectionId) {
        return getOpticCableSectionRfidInfo(null,opticCableSectionId);
    }

    /**
     * 根据光缆id查询光缆段gis信息
     *
     * @param opticCableId 光缆段id
     *
     * @return Result
     */
    @Override
    public Result queryOpticCableSectionRfidInfoByOpticCableId(String opticCableId) {
        return getOpticCableSectionRfidInfo(opticCableId,null);
    }

    /**
     * 根据id光缆段gis坐标微调
     *
     * @param updateOpticCableSectionRfidInfoReqList 光缆段gis列表
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
     * app新增光缆段GIS标签操作日志
     *
     * @param reqForApp 光缆段rfId参数
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
                //修改
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(reqForApp.getUploadType())) {
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_OPTIC_CABLE_SECTION_RFID_INFO_FUNCTION_CODE);
                //删除
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(reqForApp.getUploadType())) {
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_OPTIC_CABLE_SECTION_RFID_INFO_FUNCTION_CODE);
            }
            //设置日志类型为pda操作日志
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
            list.add(addLogBean);
        }

        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 删除光缆段rfid信息
     *
     * @param deleteOpticCableSectionRfidInfoReq 删除光缆段rfid请求
     * @return Result
     */
    @Override
    public Result deleteOpticCableSectionRfidInfoById(DeleteOpticCableSectionRfidInfoReq deleteOpticCableSectionRfidInfoReq){
        //参数校验
        if (ObjectUtils.isEmpty(deleteOpticCableSectionRfidInfoReq.getOpticStatusIdList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        List<OpticCableSectionRfidInfo> opticCableSectionRfidInfoList = opticCableSectionRfidInfoDao.selectBatchIds(deleteOpticCableSectionRfidInfoReq.getOpticStatusIdList());
        //数据校验
        if (ObjectUtils.isEmpty(opticCableSectionRfidInfoList) || opticCableSectionRfidInfoList.size() < deleteOpticCableSectionRfidInfoReq.getOpticStatusIdList().size()){
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_SECTION_RFID_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_RFID_NOT_EXIST));
        }

        //设置更新信息
        for (OpticCableSectionRfidInfo opticCableSectionRfidInfo : opticCableSectionRfidInfoList){
            opticCableSectionRfidInfo.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            opticCableSectionRfidInfo.setUpdateUser(RequestInfoUtils.getUserId());
        }
        //批量删除数据
        opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidById(opticCableSectionRfidInfoList);
        log.info("光缆段gis信息删除成功");

        //保存操作日志
        this.saveOperatorLogToDelete(opticCableSectionRfidInfoList);
        log.info("删除光缆段gis信息操作日志保存成功");
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS,I18nUtils.getSystemString(RfIdI18nConstant.DELETE_OPTIC_CABLE_SECTION_RFID_SUCCESS));
    }

    /*-------------------------------------保存删除操作日志方法start-------------------------------------*/
    /**
     * 保存删除操作日志方法
     *
     * @param opticCableSectionRfidInfoList 光缆段gis信息列表
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
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /*-------------------------------------保存删除操作日志方法end-------------------------------------*/

    /*-------------------------------------保存删除操作日志方法start-------------------------------------*/
    /**
     * 获取光缆段智能标签信息公共方法
     *
     * @param opticCableId 光缆id
     * @param opticCableSectionId 光缆段id
     *
     * @return Result
     */
    public Result getOpticCableSectionRfidInfo(String opticCableId,String opticCableSectionId){
        //根据光缆或光缆段id获取光缆段信息
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
        //根据光缆段起始节点及终止节点获取设施信息
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceIdArray);
        if (null == deviceInfoDtoList){
            return ResultUtils.warn(RfIdResultCodeConstant.DEVICE_SERVER_ERROR,I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_SERVER_ERROR));
        } else if (0 == deviceInfoDtoList.size()){
            return ResultUtils.warn(RfIdResultCodeConstant.DEVICE_NODE_NOT_EXISTS,I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_NODE_NOT_EXISTS));
        }
        //根据光缆段id获取gis信息
        List<OpticCableSectionRfidInfoResp> opticCableSectionRfIdInfoRespList = opticCableSectionRfidInfoDao.queryOpticCableSectionRfidInfoByOpticCableSectionId(opticCableSectionIds);
        //获取设施节点坐标
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
