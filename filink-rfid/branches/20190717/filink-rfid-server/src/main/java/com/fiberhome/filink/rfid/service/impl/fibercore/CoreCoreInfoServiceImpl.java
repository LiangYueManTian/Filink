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
 * 熔接信息表 服务实现类
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
     * 远程调用日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 远程调用SystemLanguage服务
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 用户Feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 设施Feign
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 智能标签业务权限rfidServerPermission
     */
    @Autowired
    private RfidServerPermission rfidServerPermission;

    @Autowired
    private OpticCableSectionInfoService opticCableSectionInfoService;

    /**
     * 注入updateOpticCableSectionStatusAsync接口
     */
    @Autowired
    private UpdateOpticCableSectionStatus updateOpticCableSectionStatusAsync;

    /**
     * 获取熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     *
     * @return Result
     */
    @Override
    public Result queryCoreCoreInfo(CoreCoreInfoReq coreCoreInfoReq) {
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfo(coreCoreInfoReq);
        return ResultUtils.success(coreCoreInfoRespList);
    }

    /**
     * 获取其他设施熔纤信息
     *
     * @param coreCoreInfoReq 获取熔纤信息请求
     *
     * @return Result
     */
    @Override
    public Result queryCoreCoreInfoNotInDevice(CoreCoreInfoReq coreCoreInfoReq) {
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfoNotInDevice(coreCoreInfoReq);
        return ResultUtils.success(coreCoreInfoRespList);
    }

    /**
     * 保存熔纤信息
     *
     * @param insertCoreCoreInfoReqList 新增熔纤信息请求列表
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveCoreCoreInfo(List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList) {
        //数据为空，返回参数错误
        if (ObjectUtils.isEmpty(insertCoreCoreInfoReqList)){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //数据为空，返回参数错误
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList){
            //数据校验
            if (StringUtils.isEmpty(insertCoreCoreInfoReq.getIntermediateNodeDeviceId()) ||
                    StringUtils.isEmpty(insertCoreCoreInfoReq.getOppositeResource()) ||
                    StringUtils.isEmpty(insertCoreCoreInfoReq.getResource())
            ){
                return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
        }

        //校验是否有智能标签业务
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

        //当页面熔纤数据为空时，按规则只删除数据，不新增
        if (insertCoreCoreInfoReqList.size() == 1){
            //当数据只有一条，纤芯号为空时，为清空数据操作
            if (!(StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getCableCoreNo()) && StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                coreCoreInfoDao.addCoreCoreInfo(insertCoreCoreInfoReqList);
                log.info("保存熔纤信息成功");
            }
        } else {
            coreCoreInfoDao.addCoreCoreInfo(insertCoreCoreInfoReqList);
            log.info("保存熔纤信息成功");
        }

        //更新纤芯统计数据
        Set<String> opticCableIds = new HashSet<>();
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList){
            opticCableIds.add(insertCoreCoreInfoReq.getResource());
            opticCableIds.add(insertCoreCoreInfoReq.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
            log.info("更新纤芯统计成功");
        }

        //更新光缆段使用状态
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByCoreCore(insertCoreCoreInfoReqList);
        log.info("更新光缆段使用状态成功");

        //保存操作日志
        this.saveOperatorLog(insertCoreCoreInfoReqList);
        log.info("保存熔纤操作日志成功");

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_CORE_CORE_SUCCESS));
    }

    /**
     * app请求纤芯熔接信息
     *
     * @param queryCoreCoreInfoReqForApp app请求纤芯熔接信息
     *
     * @return Result
     */
    @Override
    public Result queryCoreCoreInfoForApp(QueryCoreCoreInfoReqForApp queryCoreCoreInfoReqForApp) {
        List<CoreCoreInfoRespForApp> coreCoreInfoRespForAppList = coreCoreInfoDao.queryCoreCoreInfoForApp(queryCoreCoreInfoReqForApp);
        return ResultUtils.success(coreCoreInfoRespForAppList);
    }

    /**
     * app处理熔纤信息
     *
     * @param batchOperationCoreCoreInfoReqForApp app熔纤信息请求列表
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result operationCoreCoreInfoReqForApp(BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp) {
        //数据为空，返回参数错误
        if (ObjectUtils.isEmpty(batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //修改时需要先删除（未启用，待定）
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
                CoreCoreInfoForApp coreCoreInfoForApp = new CoreCoreInfoForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,coreCoreInfoForApp);
                coreCoreInfoForApp.setUpdateUser(RequestInfoUtils.getUserId());
                coreCoreInfoForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                coreCoreInfoDao.deleteCoreCoreInfoByCoreAndDeviceForApp(coreCoreInfoForApp);
            }
        }

        //删除时删除当前关系
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
                CoreCoreInfoForApp coreCoreInfoForApp = new CoreCoreInfoForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,coreCoreInfoForApp);
                coreCoreInfoForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                coreCoreInfoForApp.setUpdateUser(RequestInfoUtils.getUserId());
                coreCoreInfoDao.deleteCoreCoreInfoByCoreCoreInfoForApp(coreCoreInfoForApp);
                log.info("app删除熔纤信息成功");
            }
        }

        //修改或新增
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType()) || AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
                //统一设置uuid
                operationCoreCoreInfoReqForApp.setCoreCoreId(NineteenUUIDUtils.uuid());
                operationCoreCoreInfoReqForApp.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                operationCoreCoreInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            }
            coreCoreInfoDao.saveCoreCoreInfoForApp(batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList());
            log.info("app保存熔纤信息成功");
        }

        //更新纤芯统计数据
        Set<String> opticCableIds = new HashSet<>();
        for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()){
            opticCableIds.add(operationCoreCoreInfoReqForApp.getResource());
            opticCableIds.add(operationCoreCoreInfoReqForApp.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
            log.info("app纤芯统计成功");
        }

        //更新光缆段使用状态
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByCoreCoreForApp(batchOperationCoreCoreInfoReqForApp);
        log.info("app更新光缆段使用状态成功");

        //保存app操作日志
        this.saveOperatorLogForApp(batchOperationCoreCoreInfoReqForApp);
        log.info("app保存熔纤操作日志成功");

        //新增
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_CORE_CORE_SUCCESS));
            //修改
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_CORE_CORE_SUCCESS));
            //删除
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_CORE_CORE_SUCCESS));
        } else {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
    }

    /**
     * 保存操作日志
     *
     * @param insertCoreCoreInfoReqList 熔纤列表
     * @return void
     */
    public void saveOperatorLog(List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList) {

        //清空熔纤code
        String functionCode = LogFunctionCodeConstant.CLEAR_CORE_CORE_FUNCTION_CODE;

        //当页面熔纤数据为空时，按规则只删除数据，不新增
        if (insertCoreCoreInfoReqList.size() == 1){
            //当数据只有一条，纤芯号为空时，为清空数据操作
            if (!(StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getCableCoreNo()) && StringUtils.isEmpty(insertCoreCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                functionCode = LogFunctionCodeConstant.ADD_CORE_CORE_FUNCTION_CODE;
            }
        } else {
            functionCode = LogFunctionCodeConstant.ADD_CORE_CORE_FUNCTION_CODE;
        }

        // 保存熔纤操作日志
        List list = new ArrayList();
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            //如果是清空操作
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
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 保存app操作日志
     *
     * @param batchOperationCoreCoreInfoReqForApp app熔纤信息列表
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

            //新增
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_CORE_CORE_FUNCTION_CODE);
                //修改
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_CORE_CORE_FUNCTION_CODE);
                //删除
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationCoreCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_CORE_CORE_FUNCTION_CODE);
            }

            //设置日志类型为pda操作日志
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);

            list.add(addLogBean);
        }
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
