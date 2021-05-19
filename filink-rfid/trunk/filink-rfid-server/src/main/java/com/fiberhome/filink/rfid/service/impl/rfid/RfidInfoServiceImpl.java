package com.fiberhome.filink.rfid.service.impl.rfid;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.rfid.RfidInfo;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.dao.rfid.RfidInfoDao;
import com.fiberhome.filink.rfid.exception.FilinkDeviceIdException;
import com.fiberhome.filink.rfid.req.rfid.DeleteRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.QueryRfidInfoReq;
import com.fiberhome.filink.rfid.resp.rfid.RfidInfoResp;
import com.fiberhome.filink.rfid.service.rfid.RfidInfoService;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * rfId信息表 服务实现类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-11
 */
@Service
@Slf4j
public class RfidInfoServiceImpl extends ServiceImpl<RfidInfoDao, RfidInfo> implements RfidInfoService {

    @Autowired
    private RfidInfoDao rfidInfoDao;

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
     * 查询rfid信息
     *
     * @param queryRfidInfoReq 查询条件
     * @return Result
     */
    @Override
    public Result queryRfidInfo(QueryRfidInfoReq queryRfidInfoReq) {
        List<RfidInfoResp> rfIdInfoRespList = rfidInfoDao.queryRfidInfo(queryRfidInfoReq);
        return ResultUtils.success(rfIdInfoRespList);
    }

    /**
     * 保存rfId信息
     *
     * @param insertRfidInfoReqList rfid信息列表
     * @return Result
     */
    @Override
    public Result addRfidInfo(List<InsertRfidInfoReq> insertRfidInfoReqList) {

        //设施id不能为空
        for (InsertRfidInfoReq insertRfidInfoReq : insertRfidInfoReqList){
            if (StringUtils.isEmpty(insertRfidInfoReq.getDeviceId())){
                throw new FilinkDeviceIdException();
            }
        }

        //统一设置主键id
        for (InsertRfidInfoReq insertRfidInfoReq : insertRfidInfoReqList){
            insertRfidInfoReq.setCreateUser(RequestInfoUtils.getUserId());
            insertRfidInfoReq.setCreateTime(UtcTimeUtil.getUtcTime());
            insertRfidInfoReq.setRfidId(NineteenUUIDUtils.uuid());
        }
        rfidInfoDao.addRfidInfo(insertRfidInfoReqList);
        log.info("app保存智能标签信息成功");
        //保存操作日志
        this.saveAddOperatorLog(insertRfidInfoReqList);
        log.info("app保存智能标签操作日志成功");
        return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.SAVE_RFID_SUCCESS));
    }

    /**
     * 删除rfid信息
     *
     * @param rfidCodeList rfidCode列表
     *
     * @return Result
     */
    @Override
    public Result deleteRfidInfo(Set<String> rfidCodeList) {
        //此处由于数据量太大，只能建索引，限制批量上传数量，循环访问数据库，已减少查询总体时间
        for (String rfidCode : rfidCodeList){
            rfidInfoDao.deleteRfidInfo(rfidCode,RequestInfoUtils.getUserId(),UtcTimeUtil.getUtcTime());
        }
        log.info("删除智能标签信息成功");
        //保存操作日志
        this.saveDeleteOperatorLog(rfidCodeList);
        log.info("删除智能标签操作日志成功");
        return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_RFID_SUCCESS));
    }

    /**
     * 校验rfidCode是否存在
     *
     * @param rfidCodeList rfidCode列表
     * @return Boolean
     */
    @Override
    public Boolean checkRfidCodeListIsExist(Set<String> rfidCodeList) {
        //此处由于数据量太大，只能建索引，限制批量上传数量，循环访问数据库，已减少查询总体时间
        if (!ObjectUtils.isEmpty(rfidCodeList)){
            for (String rfidCode : rfidCodeList){
                QueryRfidInfoReq queryRfidInfoReq = new QueryRfidInfoReq();
                queryRfidInfoReq.setRfidCode(rfidCode);
                List<String> rfidCodeRespList = rfidInfoDao.queryRfidInfoByRfidCode(rfidCode);
                if (!ObjectUtils.isEmpty(rfidCodeRespList)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void changeLabel(String newLabel, String oldLabel) {
        rfidInfoDao.changeLabel(newLabel, oldLabel);
    }


    /**
     * 通过设施id删除rfid信息
     *
     * @param deviceId 设施id
     * @return Result
     */
    @Override
    public int deleteRfidInfoByDeviceId(String deviceId) {
        DeleteRfidInfoReq deleteRfidInfoReq = new DeleteRfidInfoReq();
        deleteRfidInfoReq.setDeviceId(deviceId);
        deleteRfidInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        deleteRfidInfoReq.setUpdateTime(UtcTimeUtil.getUtcTime());
        return rfidInfoDao.deleteRfidInfoByDeviceId(deleteRfidInfoReq);
    }

    /**
     * 保存新增操作日志
     *
     * @param insertRfidInfoReqList 智能标签列表
     * @return void
     */
    public void saveAddOperatorLog(List<InsertRfidInfoReq> insertRfidInfoReqList) {
        // 保存rfId操作日志
        List list = new ArrayList();
        for (InsertRfidInfoReq insertRfidInfoReq : insertRfidInfoReqList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("rfidId");
            addLogBean.setDataName("rfidCode");
            addLogBean.setOptObj(insertRfidInfoReq.getRfidCode());
            addLogBean.setOptObjId(insertRfidInfoReq.getRfidId());
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_RFID_INFO_FUNCTION_CODE);

            list.add(addLogBean);
        }
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 保存删除操作日志
     *
     * @param rfidCodeList 智能标签code列表
     * @return void
     */
    public void saveDeleteOperatorLog(Set<String> rfidCodeList) {
        // 保存rfId操作日志
        List list = new ArrayList();
        for (String rfidCode : rfidCodeList) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("rfidCode");
            addLogBean.setDataName("rfidCode");
            addLogBean.setOptObj(rfidCode);
            addLogBean.setOptObjId(rfidCode);
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_RFID_INFO_FUNCTION_CODE);

            list.add(addLogBean);
        }
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
