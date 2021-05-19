package com.fiberhome.filink.lockserver.util;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 记录操作日志工具类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/10
 */
@Component("operateLogUtils")
@Slf4j
public class OperateLogUtils {

    @Autowired
    private LogProcess logProcess;
    @Autowired
    private LogCastProcess logCastProcess;
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 生成日志实体
     *
     * @param deviceInfo   设施信息
     * @param functionCode 功能码
     * @param type         操作类型
     * @return 日志实体
     */
    private AddOperateLogReq getOperateLogBean(DeviceInfoDto deviceInfo, String functionCode, String type) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        //PDA操作记录日志，需要设施操作类型为pda，默认为网管
        if (LogConstants.OPT_TYPE_PDA.equals(type)) {
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
        }
        AddOperateLogReq addOperateLogReq = new AddOperateLogReq();
        addOperateLogReq.setLogId(NineteenUUIDUtils.uuid());
        BeanUtils.copyProperties(addLogBean, addOperateLogReq);
        //操作对象
        addOperateLogReq.setOptObj(deviceInfo.getDeviceName());
        //对象id
        addOperateLogReq.setOptObjId(deviceInfo.getDeviceId());
        addOperateLogReq.setFunctionCode(functionCode);
        //获取语言
        String environment = systemLanguageUtil.querySystemLanguage();
        //获取详细信息模板
        String detailTemplateInfo = "";
        String optName = "";
        try {
            XmlParseBean xmlParseBean = logCastProcess.dom4jParseXml(addOperateLogReq.getFunctionCode(), environment);
            //获得日志详细信息模板对象
            detailTemplateInfo = xmlParseBean.getDetailInfoTemplate();
            //替换日志详细信息,替换xml中的占位符
            if (null != detailTemplateInfo) {
                // 获取用户
                detailTemplateInfo = detailTemplateInfo.replace("${optUserName}", addOperateLogReq.getOptUserName());
                detailTemplateInfo = detailTemplateInfo.replace("${deviceName}", deviceInfo.getDeviceName());
            }
            optName = xmlParseBean.getOptName();
        } catch (Exception e) {
            log.error("write  control operation log failed>>>>>");
        }
        //获得日志详细信息
        addOperateLogReq.setDetailInfo(detailTemplateInfo);
        //获得日志操作名称
        addOperateLogReq.setOptName(optName);
        //需要调用失败后新增日志本地文件
        addOperateLogReq.setAddLocalFile(LogConstants.ADD_LOG_LOCAL_FILE);
        return addOperateLogReq;
    }

    /**
     * 记录主控日志
     *
     * @param deviceInfo   设施信息
     * @param functionCode 功能码
     * @param type         操作类型
     */
    public void addSimpleOperateLog(DeviceInfoDto deviceInfo, String functionCode, String type) {
        AddOperateLogReq addOperateLogReq = getOperateLogBean(deviceInfo, functionCode, type);
        //记录日志
        logProcess.addOperateLogInfoToAutoCall(addOperateLogReq, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 记录锁操作日志
     *
     * @param deviceInfo   设施
     * @param functionCode 操作码
     * @param locks        电子锁集合
     */
    public void addMediumOperateLog(List<Lock> locks, String functionCode, DeviceInfoDto deviceInfo, String type) {

        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        //PDA操作记录日志，需要设操作类型为pda，默认为网管
        if (LogConstants.OPT_TYPE_PDA.equals(type)) {
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
        }
        List<AddOperateLogReq> logList = new ArrayList<>();
        for (Lock lock : locks) {
            AddOperateLogReq addOperateLogReq = new AddOperateLogReq();
            BeanUtils.copyProperties(addLogBean, addOperateLogReq);
            addOperateLogReq.setLogId(NineteenUUIDUtils.uuid());
            //操作对象
            addOperateLogReq.setOptObj(deviceInfo.getDeviceName());
            //对象id
            addOperateLogReq.setOptObjId(deviceInfo.getDeviceId());
            addOperateLogReq.setFunctionCode(functionCode);
            //获取语言
            String environment = systemLanguageUtil.querySystemLanguage();
            //获取详细信息模板
            String detailTemplateInfo = "";
            String optName = "";
            try {
                XmlParseBean xmlParseBean = logCastProcess.dom4jParseXml(addOperateLogReq.getFunctionCode(), environment);
                //获得日志详细信息模板对象
                detailTemplateInfo = xmlParseBean.getDetailInfoTemplate();
                //替换日志详细信息,替换xml中的占位符
                if (null != detailTemplateInfo) {
                    //获取用户
                    detailTemplateInfo = detailTemplateInfo.replace("${optUserName}", addOperateLogReq.getOptUserName());
                    detailTemplateInfo = detailTemplateInfo.replace("${slotNum}", lock.getDoorNum());
                    detailTemplateInfo = detailTemplateInfo.replace("${deviceName}", deviceInfo.getDeviceName());
                }
                optName = xmlParseBean.getOptName();
            } catch (Exception e) {
                log.error("write lock operation log failed>>>>>");
            }
            //获得日志详细信息
            addOperateLogReq.setDetailInfo(detailTemplateInfo);
            //获得日志操作名称
            addOperateLogReq.setOptName(optName);
            //需要调用失败后新增日志本地文件
            addOperateLogReq.setAddLocalFile(LogConstants.ADD_LOG_LOCAL_FILE);
            logList.add(addOperateLogReq);
        }
        //批量新增操作日志信息
        logProcess.addOperateLogBatchInfoToAutoCall(logList, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * 批量记录日志
     *
     * @param functionCode   操作编码
     * @param deviceInfoList 设施信息集合
     * @param type           操作类型
     */
    public void batchAddOperationLog(List<DeviceInfoDto> deviceInfoList, String functionCode, String type) {
        List<AddOperateLogReq> addOperateLogReqList = new ArrayList<>();
        for (DeviceInfoDto deviceInfo : deviceInfoList) {
            AddOperateLogReq controlLogBean = getOperateLogBean(deviceInfo, functionCode, type);
            addOperateLogReqList.add(controlLogBean);
        }
        //批量新增操作日志信息
        logProcess.addOperateLogBatchInfoToAutoCall(addOperateLogReqList, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
