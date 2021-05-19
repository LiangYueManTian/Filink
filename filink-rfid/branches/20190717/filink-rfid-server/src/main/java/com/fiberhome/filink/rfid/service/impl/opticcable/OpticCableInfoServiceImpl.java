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
 * 光缆信息表 服务实现类
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
     * 远程调用SystemLanguage服务
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 注入光缆列表导出类
     */
    @Autowired
    private OpticCableExport opticCableExport;
    /**
     * 远程调用日志服务
     */
    @Autowired
    private LogProcess logProcess;
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
     * 最大纤芯数
     */
    @Value("${cableCoreMaxNum}")
    private Integer cableCoreMaxNum;

    /**
     * 分页查询光缆列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result opticCableListByPage(QueryCondition<OpticCableInfoReq> queryCondition) {

        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition
                .getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);
        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        PageBean pageBean = myBatiesBuildPageBean(page, opticCableInfoDao.opticCableListTotal(queryCondition), opticCableInfoDao.opticCableListByPage(queryCondition));
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 新增光缆
     *
     * @param insertOpticCableInfoReq 新增光缆请求
     * @return Result
     */
    @Override
    public Result addOpticCable(InsertOpticCableInfoReq insertOpticCableInfoReq) {
        //参数为空校验
        if (ObjectUtils.isEmpty(insertOpticCableInfoReq)) {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //最大纤芯数
        if (cableCoreMaxNum < Integer.parseInt(insertOpticCableInfoReq.getCoreNum())) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_EXCEED_THE_CORE_MAXIMUM, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_EXCEED_THE_CORE_MAXIMUM));
        }
        //名称校验
        if (this.checkOpticCableName(insertOpticCableInfoReq.getOpticCableId(), insertOpticCableInfoReq.getOpticCableName())) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_NAME_SAME));
        }

        //统一设置uuid
        insertOpticCableInfoReq.setOpticCableId(NineteenUUIDUtils.uuid());
        //获取登录用户
        insertOpticCableInfoReq.setCreateUser(RequestInfoUtils.getUserId());
        //保存utc标准时间
        insertOpticCableInfoReq.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());

        //保存光缆
        opticCableInfoDao.addOpticCable(insertOpticCableInfoReq);
        log.info("保存光缆信息成功");

        //新增日志信息
        String addOptType = LogConstants.DATA_OPT_TYPE_ADD;
        String functionCode = LogFunctionCodeConstant.ADD_OPTIC_CABLE_INFO_FUNCTION_CODE;
        AddLogBean addLogBean = getAddOpticCableInfoLogParam(insertOpticCableInfoReq, functionCode, addOptType);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("保存操作日志成功");
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_OPTIC_CABLE_SUCCESS));
    }

    /**
     * 根据id获取光缆信息
     *
     * @param id 光缆id
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
     * 修改光缆
     *
     * @param updateOpticCableInfoReq 修改光缆请求
     * @return Result
     */
    @Override
    public Result updateOpticCableById(UpdateOpticCableInfoReq updateOpticCableInfoReq) {
        OpticCableInfoDetail opticCableInfoDetail = opticCableInfoDao.queryOpticCableById(updateOpticCableInfoReq.getOpticCableId());
        if (ObjectUtils.isEmpty(opticCableInfoDetail)) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_IS_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_IS_NOT_EXIST));
        }

        //名称校验
        if (this.checkOpticCableName(updateOpticCableInfoReq.getOpticCableId(), updateOpticCableInfoReq.getOpticCableName())) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_NAME_SAME));
        }

        //获取登录用户
        updateOpticCableInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        //保存utc标准时间
        updateOpticCableInfoReq.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        //修改光缆信息
        opticCableInfoDao.updateOpticCableById(updateOpticCableInfoReq);
        log.info("修改光缆信息成功");

        //新增日志信息
        String addOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        String functionCode = LogFunctionCodeConstant.UPDATE_OPTIC_CABLE_INFO_FUNCTION_CODE;
        AddLogBean addLogBean = getAddOpticCableInfoLogParam(updateOpticCableInfoReq, functionCode, addOptType);
        //新增操作日志
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("保存操作日志成功");

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.UPDATE_OPTIC_CABLE_SUCCESS));
    }

    /**
     * 删除光缆
     *
     * @param id 删除光缆id
     * @return Result
     */
    @Override
    public Result deleteOpticCableById(String id) {
        OpticCableInfoDetail opticCableInfoDetail = opticCableInfoDao.queryOpticCableById(id);
        //校验光缆是否已被删除
        if (ObjectUtils.isEmpty(opticCableInfoDetail)){
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_IS_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_IS_NOT_EXIST));
        }

        //有光缆段信息不能删除光缆
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setBelongOpticCableId(id);
        List<OpticCableSectionInfo> opticCableSectionInfoList = opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(opticCableSectionInfoReq);
        if (!ObjectUtils.isEmpty(opticCableSectionInfoList)) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_CONTAINS_OPTIC_CABLE_SECTION, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_CONTAINS_OPTIC_CABLE_SECTION));
        }

        //删除记录
        opticCableInfoDao.updateOpticCableIsDeletedById(id, OpticCableConstant.IS_DELETED, RequestInfoUtils.getUserId(), FiLinkTimeUtils.getUtcZeroTimeStamp());
        log.info("删除光缆信息成功");

        //新增日志信息
        String addOptType = LogConstants.DATA_OPT_TYPE_DELETE;
        String functionCode = LogFunctionCodeConstant.DELETE_OPTIC_CABLE_INFO_FUNCTION_CODE;
        AddLogBean addLogBean = getAddOpticCableInfoLogParam(opticCableInfoDetail, functionCode, addOptType);
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_OPTIC_CABLE_SUCCESS));
    }

    /**
     * 校验光缆名字
     *
     * @param id 光缆id
     * @return Result
     */
    @Override
    public Boolean checkOpticCableName(String id, String name) {
        //光缆名称统一校验
        name = CheckInputString.nameCheck(name);
        if (StringUtils.isEmpty(name)) {
            return true;
        }
        //光缆名称重复校验
        OpticCableInfoDetail opticCableInfoDetail = opticCableInfoDao.queryOpticCableByName(name);
        if (!ObjectUtils.isEmpty(opticCableInfoDetail) && !opticCableInfoDetail.getOpticCableId().equals(id)) {
            return true;
        }
        return false;
    }

    /**
     * app请求所有光缆的信息
     *
     * @return Result
     */
    @Override
    public Result getOpticCableListForApp() {
        List<OpticCableInfoResp> opticCableInfoRespList = opticCableInfoDao.getOpticCableListForApp();
        return ResultUtils.success(opticCableInfoRespList);
    }

    /**
     * 光缆列表导出
     *
     * @param exportDto 光缆列表导出请求
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

        //新增日志
        ExportLogServer.addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_OPTIC_CABLE_INFO_FUNCTION_CODE, logProcess, systemLanguageUtil);

        return ResultUtils.success(RfIdResultCodeConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 新增光缆操作日志参数
     *
     * @param opticCableInfo 光缆参数
     * @param functionCode   功能项
     * @param dataOptType    数据操作
     * @return 新增光缆操作日志参数
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
