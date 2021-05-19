package com.fiberhome.filink.rfid.service.impl.fibercore;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
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
 * 成端信息表 服务实现类
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
     * 智能标签业务权限rfidServerPermission
     */
    @Autowired
    private RfidServerPermission rfidServerPermission;

    @Autowired
    private OpticCableSectionInfoService opticCableSectionInfoService;

    @Autowired
    private TemplateService templateService;

    /**
     * 注入updatePortStatusAsync接口
     */
    @Autowired
    private UpdatePortStatusAsync updatePortStatusAsync;

    /**
     * 注入updateOpticCableSectionStatusAsync接口
     */
    @Autowired
    private UpdateOpticCableSectionStatus updateOpticCableSectionStatusAsync;

    /**
     * 获取成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return Result
     */
    @Override
    public Result getPortCableCoreInfo(PortCableCoreInfoReq portCableCoreInfoReq) {
        List<PortCableCoreInfo> portCableCoreInfoList =  portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        return ResultUtils.success(portCableCoreInfoList);
    }

    /**
     * 获取其他设施成端信息
     *
     * @param portCableCoreInfoReq 获取成端信息请求
     *
     * @return Result
     */
    @Override
    public Result getPortCableCoreInfoNotInDevice(PortCableCoreInfoReq portCableCoreInfoReq) {
        List<PortCableCoreInfo> portCableCoreInfoList =  portCableCoreInfoDao.getPortCableCoreInfoNotInDevice(portCableCoreInfoReq);
        return ResultUtils.success(portCableCoreInfoList);
    }

    /**
     * 保存成端信息
     *
     * @param insertPortCableCoreInfoReqList 新增成端信息请求列表
     * @param updatePortCableCoreInfoReqList 更相信端口信息请求列表
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result savePortCableCoreInfo(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList,List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList) {
        //数据为空，返回参数错误
        if (ObjectUtils.isEmpty(insertPortCableCoreInfoReqList)){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //数据为空，返回参数错误
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            //数据校验
            if (StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceDeviceId()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceBoxSide()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceFrameNo()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getResourceDiscSide()) ||
                    StringUtils.isEmpty(insertPortCableCoreInfoReq.getOppositeResource())
            ){
                return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
        }
        //校验是否有智能标签业务
        Set<String> deviceIds = new HashSet<>();
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            deviceIds.add(insertPortCableCoreInfoReq.getResourceDeviceId());
        }
        if (rfidServerPermission.getPermissionsInfoFoRfidServer(deviceIds, RequestInfoUtils.getUserId())){
            return ResultUtils.warn(RfIdResultCodeConstant.NOT_HAVE_PERMISSION_FOR_RFID, I18nUtils.getSystemString(RfIdI18nConstant.NOT_HAVE_PERMISSION_FOR_RFID));
        }
        //清空现有数据
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        BeanUtils.copyProperties(insertPortCableCoreInfoReqList.get(0),portCableCoreInfoReq);
        portCableCoreInfoReq.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        portCableCoreInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        portCableCoreInfoDao.deletePortCoreInfoByResourceAndDevice(portCableCoreInfoReq);
        //统一设置uuid
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            insertPortCableCoreInfoReq.setPortCoreId(NineteenUUIDUtils.uuid());
            insertPortCableCoreInfoReq.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            insertPortCableCoreInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        }
        //当页面成端数据为空时，按规则只删除数据，不新增
        if (insertPortCableCoreInfoReqList.size() == 1) {
            //当数据只有一条，且盘号为空，纤芯号为空时，为清空数据操作，不新增
            if (!(StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getResourceDiscNo()) && StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                portCableCoreInfoDao.savePortCableCoreInfo(insertPortCableCoreInfoReqList);
                log.info("保存成端信息成功");
            }
        } else {
            portCableCoreInfoDao.savePortCableCoreInfo(insertPortCableCoreInfoReqList);
            log.info("保存成端信息成功");
        }
        //更新纤芯统计数据
        Set<String> opticCableIds = new HashSet<>();
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList){
            opticCableIds.add(insertPortCableCoreInfoReq.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
            log.info("更新纤芯统计成功");
        }
        //更新端口业务状态
        updatePortStatusAsync.updatePortBindingState(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);
        log.info("更新端口业务状态成功");
        //批量更新端口成端状态
        updatePortStatusAsync.batchUpdatePortCableState(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);
        log.info("更新端口成端状态成功");
        //更新光缆段使用状态
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByPortCableCore(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);
        log.info("更新光缆段使用状态成功");
        //保存操作日志
        this.saveOperatorLog(insertPortCableCoreInfoReqList);
        log.info("更新成端操作日志成功");
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_PORT_CORE_SUCCESS));
    }

    /**
     * app请求纤芯成端信息
     *
     * @param queryPortCableCoreInfoReqForApp app请求纤芯成端信息
     *
     * @return Result
     */
    @Override
    public Result queryPortCableCoreInfoForApp(QueryPortCableCoreInfoReqForApp queryPortCableCoreInfoReqForApp) {
        List<PortCableCoreInfoRespForApp> portCableCoreInfoRespForAppList = portCableCoreInfoDao.queryPortCableCoreInfoForApp(queryPortCableCoreInfoReqForApp);
        return ResultUtils.success(portCableCoreInfoRespForAppList);
    }

    /**
     * app处理纤芯成端信息
     *
     * @param batchOperationPortCableCoreInfoReqForApp app成端信息请求列表
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result operationPortCableCoreInfoForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp) {

        //数据为空，返回参数错误
        if (ObjectUtils.isEmpty(batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //修改时需要先删除（未启用，待定）
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            for (OperationPortCableCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
                PortCableCoreInfoReqForApp portCableCoreInfoReqForApp = new PortCableCoreInfoReqForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,portCableCoreInfoReqForApp);
                portCableCoreInfoReqForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                portCableCoreInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
                portCableCoreInfoDao.deletePortCoreInfoByPortForApp(portCableCoreInfoReqForApp);
            }
        }

        //删除时直接删除当前成端信息
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            for (OperationPortCableCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
                PortCableCoreInfoReqForApp portCableCoreInfoReqForApp = new PortCableCoreInfoReqForApp();
                BeanUtils.copyProperties(operationCoreCoreInfoReqForApp,portCableCoreInfoReqForApp);
                portCableCoreInfoReqForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                portCableCoreInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
                portCableCoreInfoDao.deletePortCoreInfoByPortCoreInfoForApp(portCableCoreInfoReqForApp);
            }

            //app更新端口业务状态
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationPortCableCoreInfoReqForApp);
            //app更新端口成端状态
            updatePortStatusAsync.batchUpdatePortCableStateForApp(batchOperationPortCableCoreInfoReqForApp);
        }

        //修改或新增
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType()) || AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
                operationPortCableCoreInfoReqForApp.setPortCoreId(NineteenUUIDUtils.uuid());
                operationPortCableCoreInfoReqForApp.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                operationPortCableCoreInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            }

            portCableCoreInfoDao.savePortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList());
            //app更新端口业务状态
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationPortCableCoreInfoReqForApp);
            //app更新端口成端状态
            updatePortStatusAsync.batchUpdatePortCableStateForApp(batchOperationPortCableCoreInfoReqForApp);
        }

        //app更新光缆段使用状态
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByPortCableCoreForApp(batchOperationPortCableCoreInfoReqForApp);

        //更新纤芯统计数据
        Set<String> opticCableIds = new HashSet<>();
        for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()){
            opticCableIds.add(operationPortCableCoreInfoReqForApp.getOppositeResource());
        }
        for (String opticCableId : opticCableIds){
            opticCableSectionInfoService.coreStatisticsCount(opticCableId);
        }

        //保存app操作日志
        this.saveOperatorLogForApp(batchOperationPortCableCoreInfoReqForApp);
        //新增
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_PORT_CORE_SUCCESS));
            //修改
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_PORT_CORE_SUCCESS));
            //删除
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_PORT_CORE_SUCCESS));
        } else {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
    }

    /**
     * 保存操作日志
     *
     * @param insertPortCableCoreInfoReqList 成端列表
     * @return void
     */
    public void saveOperatorLog(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList) {
        //清空成端code
        String functionCode = LogFunctionCodeConstant.CLEAR_PORT_CORE_FUNCTION_CODE;

        //当页面成端数据为空时，按规则只删除数据，不新增
        if (insertPortCableCoreInfoReqList.size() == 1) {
            //当数据只有一条，且盘号为空，纤芯号为空时，为清空数据操作，不新增
            if (!(StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getResourceDiscNo()) && StringUtils.isEmpty(insertPortCableCoreInfoReqList.get(0).getOppositeCableCoreNo()))){
                functionCode = LogFunctionCodeConstant.ADD_PORT_CORE_FUNCTION_CODE;
            }
        } else {
            functionCode = LogFunctionCodeConstant.ADD_PORT_CORE_FUNCTION_CODE;
        }

        // 保存成端操作日志
        List list = new ArrayList();
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            //如果是清空操作
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
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 保存app操作日志
     *
     * @param batchOperationPortCableCoreInfoReqForApp app成端信息
     * @return void
     */
    public void saveOperatorLogForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp) {
        // 保存成端操作日志
        List list = new ArrayList();
        for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("portCoreId");
            addLogBean.setDataName("portNo");
            addLogBean.setOptObjId(operationPortCableCoreInfoReqForApp.getPortCoreId());
            addLogBean.setOptObj(operationPortCableCoreInfoReqForApp.getPortNo());

            //新增
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_PORT_CORE_FUNCTION_CODE);
                //修改
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_PORT_CORE_FUNCTION_CODE);
                //删除
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_PORT_CORE_FUNCTION_CODE);
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
