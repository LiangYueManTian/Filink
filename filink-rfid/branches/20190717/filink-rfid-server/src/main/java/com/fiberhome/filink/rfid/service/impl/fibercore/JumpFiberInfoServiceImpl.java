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
 * 跳纤信息表 服务实现类
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
     * odn端口统计
     */
    @Autowired
    private OdnFacilityResourcesStatisticsService odnFacilityResourcesStatisticsService;

    /**
     * 远程调用设施服务
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 注入跳接信息列表导出类
     */
    @Autowired
    private JumpFiberExport jumpFiberExport;

    /**
     * 注入模板接口
     */
    @Autowired
    private TemplateService templateService;

    /**
     * 注入rfIdCode接口
     */
    @Autowired
    private RfidInfoService rfIdInfoService;

    /**
     * 注入updatePortStatusAsync接口
     */
    @Autowired
    private UpdatePortStatusAsync updatePortStatusAsync;

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * 导出服务名
     */
    @Value("${exportServerName}")
    private String exportServerName;

    /**
     * 最大跳接数
     */
    @Value("${jumpFiberMaxNum}")
    private Integer jumpFiberMaxNum;


    /**
     * 获取跳接信息
     *
     * @param queryJumpFiberInfoReq 跳接信息请求类
     *
     * @return Result
     */
    @Override
    public Result queryJumpFiberInfoByPortInfo(QueryJumpFiberInfoReq queryJumpFiberInfoReq) {
        List<JumpFiberInfoResp> jumpFiberInfoRespListResult = new ArrayList<>();
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);

        //组装跳接信息
        jumpFiberInfoRespListResult = assemblyJumpFiberInfoResp(jumpFiberInfoRespList,queryJumpFiberInfoReq);

        return ResultUtils.success(jumpFiberInfoRespListResult);
    }

    /**
     * 删除跳接信息请求
     *
     * @param deleteJumpFiberInfoReq 删除跳接信息请求
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteJumpFiberInfoById(DeleteJumpFiberInfoReq deleteJumpFiberInfoReq) {
        //参数校验
        if (ObjectUtils.isEmpty(deleteJumpFiberInfoReq.getJumpFiberIdList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        BeanUtils.copyProperties(deleteJumpFiberInfoReq,queryJumpFiberInfoReq);
        queryJumpFiberInfoReq.setJumpFiberIdList(deleteJumpFiberInfoReq.getJumpFiberIdList());
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);

        //数据校验
        if (ObjectUtils.isEmpty(jumpFiberInfoRespList) || jumpFiberInfoRespList.size() < deleteJumpFiberInfoReq.getJumpFiberIdList().size()){
            return ResultUtils.warn(RfIdResultCodeConstant.JUMP_CORE_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.JUMP_CORE_NOT_EXIST));
        }

        //删除关联智能标签信息
        for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoRespList){
            Set<String> rfIdCodeList = new HashSet<>();
            rfIdCodeList.add(jumpFiberInfo.getRfidCode());
            rfIdCodeList.add(jumpFiberInfo.getOppositeRfidCode());
            rfidInfoService.deleteRfidInfo(rfIdCodeList);
        }

        //删除跳接信息
        jumpFiberInfoDao.deleteJumpFiberInfoById(deleteJumpFiberInfoReq.getJumpFiberIdList(), FiLinkTimeUtils.getUtcZeroTimeStamp(), RequestInfoUtils.getUserId());

        //调用跳接统计接口
        this.portStatistics(jumpFiberInfoRespList);

        //跳接更新端口使用状态
        updatePortStatusAsync.updatePortBindingState(jumpFiberInfoRespList);
        //保存删除操作日志
        this.saveOperatorLogToDelete(jumpFiberInfoRespList);

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_JUMP_CORE_SUCCESS));
    }

    /**
     * 跳接列表导出
     *
     * @param exportDto 跳接列表导出请求
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

        //新增日志
        ExportLogServer.addLogByExport(exportDto,LogFunctionCodeConstant.EXPORT_JUMP_FIBER_INFO_FUNCTION_CODE,logProcess,systemLanguageUtil);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * app根据端口信息获取跳接信息
     *
     * @param queryJumpFiberInfoReqForApp app跳接信息请求类
     *
     * @return Result
     */
    @Override
    public Result queryJumpFiberInfoByPortInfoForApp(QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp) {
        List<JumpFiberInfoRespForApp> jumpFiberInfoRespForAppList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfoForApp(queryJumpFiberInfoReqForApp);

        //组装本端及对端
        jumpFiberInfoRespForAppList = this.assemblyJumpFiberInfoThisAndOppositeForApp(jumpFiberInfoRespForAppList,queryJumpFiberInfoReqForApp);

        return ResultUtils.success(jumpFiberInfoRespForAppList);
    }

    /**
     * app处理跳接信息
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息列表
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result operationJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp) {
        //数据为空，返回参数错误
        if (ObjectUtils.isEmpty(batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList())){
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //最大数据量限制
        if (jumpFiberMaxNum < batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList().size()){
            return ResultUtils.warn(RfIdResultCodeConstant.EXCEED_JUMP_CORE_MAX_NUM, I18nUtils.getSystemString(RfIdI18nConstant.EXCEED_JUMP_CORE_MAX_NUM));
        }
        //修改时需要先删除
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //必填参数校验
            if (this.checkParamsToUpdateForApp(batchOperationJumpFiberInfoForApp)){
                return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
            this.updateJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
        }
        //删除时删除当前关系
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            if (this.checkParamsToDeleteForApp(batchOperationJumpFiberInfoForApp)){
                return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
            this.deleteJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
            log.info("app删除跳接信息成功");
        }
        //修改或新增
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType()) || AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //保存跳接数据校验
            String flag = this.checkParamsToSaveForApp(batchOperationJumpFiberInfoForApp);
            if (JumpCoreConstant.RESULT_TYPE_QUERY_REALPOSITION_DEVICE_ID_ERROR.equals(flag)){
                return ResultUtils.warn(RfIdResultCodeConstant.QUERY_REALPOSITION_DEVICE_ID_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.QUERY_REALPOSITION_DEVICE_ID_ERROR));
            }else if (JumpCoreConstant.RESULT_TYPE_PORT_HAVE_JUMP_CORE.equals(flag)){
                return ResultUtils.warn(RfIdResultCodeConstant.PORT_HAVE_JUMP_CORE, I18nUtils.getSystemString(RfIdI18nConstant.PORT_HAVE_JUMP_CORE));
            }else if (JumpCoreConstant.RESULT_TYPE_RFID_CODE_IS_EXISTS.equals(flag)){
                return ResultUtils.warn(RfIdResultCodeConstant.RFID_CODE_IS_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.RFID_CODE_IS_EXISTS));
            }
            //保存跳接信息
            this.saveJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
            log.info("app保存跳接信息成功");
        }
        //修改智能标签id
        if (AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //校验当前智能标签code是否存在
            Set<String> rfIdCodeList = new HashSet<>();
            for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
                rfIdCodeList.add(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
            }
            if (rfIdInfoService.checkRfidCodeListIsExist(rfIdCodeList)){
                return ResultUtils.warn(RfIdResultCodeConstant.RFID_CODE_IS_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.RFID_CODE_IS_EXISTS));
            }
            //更换智能标签id信息
            this.updateRfIdCodeForApp(batchOperationJumpFiberInfoForApp);
            log.info("app更换智能标签id成功");
        }
        //修改智能标签id时不统计跳接信息
        if (!AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //调用跳接统计接口
            this.portStatisticsForApp(batchOperationJumpFiberInfoForApp);
            log.info("app更新端口统计信息成功");
            //app跳接更新端口使用状态
            this.updatePortBindingStateForApp(batchOperationJumpFiberInfoForApp);
            log.info("app更新端口使用状态成功");
        }
        //保存app操作日志
        this.saveOperatorLogForApp(batchOperationJumpFiberInfoForApp);
        log.info("app保存操作日志成功");
        //设置值返回值
        return this.setResult(batchOperationJumpFiberInfoForApp);
    }

    /**
     * app校验修改参数
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
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
     * app校验保存参数
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     *
     * @return String
     */
    public String checkParamsToSaveForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        //校验设施id是否存在
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            if (StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getDeviceId()) || StringUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeDeviceId())){
                return JumpCoreConstant.RESULT_TYPE_QUERY_REALPOSITION_DEVICE_ID_ERROR;
            }
        }
        //校验当前跳接数据是否已被跳接
        if (this.checkJumpFiberUsed(batchOperationJumpFiberInfoForApp)){
            return JumpCoreConstant.RESULT_TYPE_PORT_HAVE_JUMP_CORE;
        }
        //设置虚拟标签id
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            if (ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getRfidCode())){
                operationJumpFiberInfoReqForApp.setRfidCode(UUIDUtil.getInstance().UUID32());
            }
            if (ObjectUtils.isEmpty(operationJumpFiberInfoReqForApp.getOppositeRfidCode())){
                operationJumpFiberInfoReqForApp.setOppositeRfidCode(UUIDUtil.getInstance().UUID32());
            }
        }
        //校验当前智能标签code是否存在
        Set<String> rfidCodeList = new HashSet<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            rfidCodeList.add(operationJumpFiberInfoReqForApp.getRfidCode());
            rfidCodeList.add(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
        }
        if (rfIdInfoService.checkRfidCodeListIsExist(rfidCodeList)){
            return JumpCoreConstant.RESULT_TYPE_RFID_CODE_IS_EXISTS;
        }
        //正常
        return JumpCoreConstant.RESULT_TYPE_NORMAL;
    }

    /**
     * app校验删除参数
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     *
     * @return Boolean
     */
    public Boolean checkParamsToDeleteForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        //参数错误
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
     * app修改跳接信息
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     *
     * @return Result
     */
    public Result setResult(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //新增
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_JUMP_CORE_SUCCESS));
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //修改
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_JUMP_CORE_SUCCESS));
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //删除
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_JUMP_CORE_SUCCESS));
        } else if (AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            //修改智能标签id
            return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_RFID_CODE_SUCCESS));
        } else {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
    }

    /**
     * app更新端口使用状态
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     * @return void
     */
    public void updatePortBindingStateForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        //新增时更新端口状态
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationJumpFiberInfoForApp);
        }
        //删除时更新端口状态
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            updatePortStatusAsync.updatePortBindingStateForApp(batchOperationJumpFiberInfoForApp);
        }
        //修改时更新端口状态
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
            updatePortStatusAsync.updatePortBindingStateToUpdateForApp(batchOperationJumpFiberInfoForApp);
        }
    }

    /**
     * app修改跳接信息
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     *
     * @return void
     */
    public void updateJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            JumpFiberInfoReqForApp jumpFiberInfoReqForApp = new JumpFiberInfoReqForApp();
            BeanUtils.copyProperties(operationJumpFiberInfoReqForApp,jumpFiberInfoReqForApp);
            jumpFiberInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
            jumpFiberInfoReqForApp.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            //如果是分路器
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
     * app删除跳接信息
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     *
     * @return void
     */
    public void deleteJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){

            //获取删除跳接信息
            BeanUtils.copyProperties(operationJumpFiberInfoReqForApp,queryJumpFiberInfoReq);
            List<JumpFiberInfo> jumpFiberInfoList = jumpFiberInfoDao.getJumpFiberRfidCodeByJumpFiberInfoForApp(queryJumpFiberInfoReq);

            JumpFiberInfoReqForApp jumpFiberInfoReqForApp = new JumpFiberInfoReqForApp();
            BeanUtils.copyProperties(operationJumpFiberInfoReqForApp,jumpFiberInfoReqForApp);
            jumpFiberInfoReqForApp.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            jumpFiberInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            jumpFiberInfoDao.deleteJumpFiberInfoByJumpFiberInfoForApp(jumpFiberInfoReqForApp);

            //删除关联智能标签信息
            for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoList){
                Set<String> rfIdCodeList = new HashSet<>();
                rfIdCodeList.add(jumpFiberInfo.getRfidCode());
                rfIdCodeList.add(jumpFiberInfo.getOppositeRfidCode());
                rfidInfoService.deleteRfidInfo(rfIdCodeList);
            }
        }
    }

    /**
     * app保存跳接信息
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     *
     * @return void
     */
    public void saveJumpFiberInfoReqForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        List<InsertRfidInfoReq> insertRfIdInfoReqList = new ArrayList<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            //统一设置uuid
            operationJumpFiberInfoReqForApp.setJumpFiberId(NineteenUUIDUtils.uuid());
            operationJumpFiberInfoReqForApp.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
            operationJumpFiberInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());

            //新增智能标签信息
            //本端智能标签信息
            InsertRfidInfoReq insertRfidInfoReq = new InsertRfidInfoReq();
            insertRfidInfoReq.setRfidCode(operationJumpFiberInfoReqForApp.getRfidCode());
            insertRfidInfoReq.setMarkType(operationJumpFiberInfoReqForApp.getMarkType());
            insertRfidInfoReq.setRfidStatus(operationJumpFiberInfoReqForApp.getRfidStatus());
            insertRfidInfoReq.setDeviceId(operationJumpFiberInfoReqForApp.getDeviceId());
            //跳接类型
            insertRfidInfoReq.setRfidType(RfIdConstant.RFID_TYPE_JUMP_FIBER);
            insertRfIdInfoReqList.add(insertRfidInfoReq);
            //对端智能标签信息
            InsertRfidInfoReq oppositeInsertRfIdInfoReq = new InsertRfidInfoReq();
            oppositeInsertRfIdInfoReq.setRfidCode(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
            oppositeInsertRfIdInfoReq.setMarkType(operationJumpFiberInfoReqForApp.getOppositeMarkType());
            oppositeInsertRfIdInfoReq.setRfidStatus(operationJumpFiberInfoReqForApp.getOppositeRfidStatus());
            oppositeInsertRfIdInfoReq.setDeviceId(operationJumpFiberInfoReqForApp.getOppositeDeviceId());
            //跳接类型
            oppositeInsertRfIdInfoReq.setRfidType(RfIdConstant.RFID_TYPE_JUMP_FIBER);
            insertRfIdInfoReqList.add(oppositeInsertRfIdInfoReq);
            //柜内跳或柜间跳
            if (operationJumpFiberInfoReqForApp.getDeviceId().equals(operationJumpFiberInfoReqForApp.getOppositeDeviceId())){
                operationJumpFiberInfoReqForApp.setInnerDevice(JumpCoreConstant.IS_INNER_DEVICE);
            } else {
                operationJumpFiberInfoReqForApp.setInnerDevice(JumpCoreConstant.NOT_INNER_DEVICE);
            }
        }
        jumpFiberInfoDao.addJumpFiberInfo(batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList());
        //新增关联智能标签信息
        rfidInfoService.addRfidInfo(insertRfIdInfoReqList);
    }

    /**
     * app修改跳接智能标签id
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     *
     * @return void
     */
    public void updateRfIdCodeForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp){
        Set<String> rfIdCodeListHis = new HashSet<>();
        List<InsertRfidInfoReq> insertRfIdInfoReqList = new ArrayList<>();
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            //获取设施id，用于更换rfid
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

            //修改本端智能标签id
            jumpFiberInfoDao.updateThisRfIdCodeForApp(operationJumpFiberInfoReqForApp);
            //修改对端端智能标签id
            jumpFiberInfoDao.updateOppositeRfIdCodeForApp(operationJumpFiberInfoReqForApp);
            //被修改
            rfIdCodeListHis.add(operationJumpFiberInfoReqForApp.getRfidCode());
            //修改为
            InsertRfidInfoReq insertRfidInfoReq = new InsertRfidInfoReq();
            //跳接类型
            insertRfidInfoReq.setRfidType(RfIdConstant.RFID_TYPE_JUMP_FIBER);
            insertRfidInfoReq.setRfidCode(operationJumpFiberInfoReqForApp.getOppositeRfidCode());
            insertRfidInfoReq.setMarkType(operationJumpFiberInfoReqForApp.getOppositeMarkType());
            insertRfidInfoReq.setRfidStatus(operationJumpFiberInfoReqForApp.getOppositeRfidStatus());
            insertRfidInfoReq.setDeviceId(deviceId);
            insertRfIdInfoReqList.add(insertRfidInfoReq);
        }
        //删除关联智能标签信息
        rfidInfoService.deleteRfidInfo(rfIdCodeListHis);
        //新增关联智能标签信息
        rfidInfoService.addRfidInfo(insertRfIdInfoReqList);
    }

    /*-------------------------------------保存app操作日志公共方法start-------------------------------------*/
    /**
     * 保存app操作日志公共方法
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息列表
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

            //新增
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_JUMP_CORE_FUNCTION_CODE);
                //修改
            } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_JUMP_CORE_FUNCTION_CODE);
                //删除
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_JUMP_CORE_FUNCTION_CODE);
            } else if (AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBean.setFunctionCode(LogFunctionCodeConstant.OPERATOR_TYPE_UPDATE_RFID_CODE);
            }

            //设置日志类型为pda操作日志
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);

            list.add(addLogBean);
        }
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /*-------------------------------------保存app操作日志公共方法end-------------------------------------*/

    /*-------------------------------------保存删除操作日志方法start-------------------------------------*/
    /**
     * 保存删除操作日志方法
     *
     * @param jumpFiberInfoList 跳接信息列表
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
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /*-------------------------------------保存删除操作日志方法end-------------------------------------*/

    /*-------------------------------------更新跳接统计接口公共方法start-------------------------------------*/
    /**
     * app更新跳接统计接口
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息列表
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
     * 更新跳接统计接口
     *
     * @param jumpFiberInfoList 跳接信息列表
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
    /*-------------------------------------更新跳接统计接口公共方法end-------------------------------------*/

    /*-------------------------------------组装本端及对端start-------------------------------------*/
    /**
     * app组装本端及对端
     *
     * @param jumpFiberInfoRespForAppList app跳接信息列表
     *
     * @return List<JumpFiberInfoRespForApp> 跳接信息列表
     */
    public List<JumpFiberInfoRespForApp> assemblyJumpFiberInfoThisAndOppositeForApp(List<JumpFiberInfoRespForApp> jumpFiberInfoRespForAppList,QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp) {
        //组装本端及对端信息
        List<JumpFiberInfoRespForApp> jumpFiberInfoRespListTemp = new ArrayList<>();
        for (JumpFiberInfoRespForApp jumpFiberInfoRespForApp : jumpFiberInfoRespForAppList){
            JumpFiberInfoRespForApp jumpFiberInfoRespForAppTemp = new JumpFiberInfoRespForApp();

            //当有标签信息时，精确比对
            if (!StringUtils.isEmpty(queryJumpFiberInfoReqForApp.getRfidCode())){
                if (!queryJumpFiberInfoReqForApp.getRfidCode().equals(jumpFiberInfoRespForApp.getRfidCode())){
                    //交换本端及对端
                    jumpFiberInfoRespForAppTemp = exchangeJumpFiberInfoThisAndOppositeForApp(jumpFiberInfoRespForApp,jumpFiberInfoRespForAppTemp);

                    jumpFiberInfoRespListTemp.add(jumpFiberInfoRespForAppTemp);
                } else {
                    jumpFiberInfoRespListTemp.add(jumpFiberInfoRespForApp);
                }
                //否则精确查找时，多条件比对
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
                    //交换本端及对端
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
     * app本端及对端数据互换
     *
     * @param jumpFiberInfoRespForApp app跳接信息
     *
     * @return JumpFiberInfoRespForApp app跳接信息
     */
    public JumpFiberInfoRespForApp exchangeJumpFiberInfoThisAndOppositeForApp(JumpFiberInfoRespForApp jumpFiberInfoRespForApp,JumpFiberInfoRespForApp jumpFiberInfoRespForAppTemp){
        //本端和对端互换
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
    /*-------------------------------------组装本端及对端end-------------------------------------*/

    /*-------------------------------------组装跳接信息公共方法start-------------------------------------*/
    /**
     * 组装跳接信息
     *
     * @param jumpFiberInfoRespList 跳接信息列表
     * @param queryJumpFiberInfoReq 跳接信息请求
     *
     * @return jumpFiberInfoRespList 跳接信息列表
     */
    @Override
    @SuppressWarnings("all")
    public List<JumpFiberInfoResp> assemblyJumpFiberInfoResp(List<JumpFiberInfoResp> jumpFiberInfoRespList,QueryJumpFiberInfoReq queryJumpFiberInfoReq){
        List<JumpFiberInfoResp> jumpFiberInfoRespListResult = new ArrayList<>();
        //远程获取设施信息
        Set<String> deviceIds = new HashSet<>();
        for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespList){
            deviceIds.add(jumpFiberInfoResp.getDeviceId());
            deviceIds.add(jumpFiberInfoResp.getOppositeDeviceId());
        }
        String[] deviceArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceArray);
        Map<String, DeviceInfoDto> deviceInfoMap = new HashMap<>(64);
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);

        //组装本端及对端数据
        jumpFiberInfoRespListResult = JumpFiberInfoResp.assemblyJumpFiberInfoThisAndOpposite(jumpFiberInfoRespList,queryJumpFiberInfoReq);

        //获取设施信息
        if (!ObjectUtils.isEmpty(deviceInfoDtoList)){
            log.info("对端端口获取设施信息成功");
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

        //获取对端模板标签信息
        List<JumpFiberInfoResp> jumpFiberInfoRespListTemp = templateService.batchQueryPortInfo(jumpFiberInfoRespListResult);
        if (!ObjectUtils.isEmpty(jumpFiberInfoRespListTemp)){
            jumpFiberInfoRespListResult = jumpFiberInfoRespListTemp;
            log.info("对端端口获取模板信息成功");
        }

        //获取对端端口号
        for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespListResult){
            //获取对端端口id
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
        log.info("对端端口获取端口编号成功");
        return jumpFiberInfoRespListResult;
    }
    /*-------------------------------------组装跳接信息公共方法end-------------------------------------*/

    /**
     * 校验跳接信息是否已使用
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息请求
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
            //本端
            jumpFiberInfoRespMap.put(jumpFiberInfoResp.getDeviceId() + "-"
                                    + jumpFiberInfoResp.getBoxSide() + "-"
                                    + jumpFiberInfoResp.getFrameNo() + "-"
                                    + jumpFiberInfoResp.getDiscSide() + "-"
                                    + jumpFiberInfoResp.getDiscNo() + "-"
                                    + jumpFiberInfoResp.getPortNo(),
                           jumpFiberInfoResp);

            //对端
            jumpFiberInfoRespMap.put(jumpFiberInfoResp.getOppositeDeviceId() + "-"
                                    + jumpFiberInfoResp.getOppositeBoxSide() + "-"
                                    + jumpFiberInfoResp.getOppositeFrameNo() + "-"
                                    + jumpFiberInfoResp.getOppositeDiscSide() + "-"
                                    + jumpFiberInfoResp.getOppositeDiscNo() + "-"
                                    + jumpFiberInfoResp.getOppositePortNo(),
                            jumpFiberInfoResp);
        }

        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()){
            //本端已使用
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

            //只有新增时需要校验对端信息，修改时不会修改对端
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())){
                //对端已使用
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
