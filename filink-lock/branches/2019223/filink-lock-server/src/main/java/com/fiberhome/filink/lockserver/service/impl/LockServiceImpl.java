package com.fiberhome.filink.lockserver.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;
import com.fiberhome.filink.lockserver.dao.LockDao;
import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import com.fiberhome.filink.lockserver.service.LockService;
import com.fiberhome.filink.lockserver.util.LockI18n;
import com.fiberhome.filink.lockserver.util.LockResultCode;
import com.fiberhome.filink.lockserver.util.constant.CmdId;
import com.fiberhome.filink.lockserver.util.constant.FunctionCode;
import com.fiberhome.filink.lockserver.util.constant.LockOperator;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.stationapi.bean.FiLinkReqParamsDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 电子锁实现类
 *
 * @author CongcaiYu
 */
@Log4j
@Service
public class LockServiceImpl implements LockService {

    @Autowired
    private LockDao lockDao;

    @Autowired
    private FiLinkStationFeign fiLinkStationFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private LogCastProcess logCastProcess;

    @Autowired
    private DeviceFeign deviceFeign;


    /**
     * 保存电子锁信息
     *
     * @param lock 电子锁信息
     */
    @Override
    public Result saveLockInfo(Lock lock) {
        lockDao.saveLockInfo(lock);
        return ResultUtils.success();
    }

    /**
     * 根据设施id查询电子锁信息
     *
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    @Override
    public List<Lock> queryLockByDeviceId(String deviceId) {
        return lockDao.queryLockByDeviceId(deviceId);
    }

    /**
     * 更新电子锁状态
     *
     * @param lock 电子锁信息
     */
    @Override
    public void updateLockStatus(Lock lock) {
        lockDao.updateLockStatus(lock);
    }

    /**
     * 批量更新电子锁状态
     *
     * @param locks 电子锁集合
     * @return 操作结果
     */
    @Override
    public Result batchUpdateLockStatus(List<Lock> locks) {
        for (Lock lock : locks) {
            updateLockStatus(lock);
        }
        //记录操作日志
        addUpdateLockStateLog(locks);
        return ResultUtils.success();
    }

    /**
     * 记录更新电子锁操作日志
     *
     * @param locks 电子锁集合
     */
    private void addUpdateLockStateLog(List<Lock> locks) {
        //查询设施信息
        String deviceId = locks.get(0).getDeviceId();
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(deviceId);
        //查询电子锁信息
        List<Lock> lockList = queryLockByDeviceId(deviceId);
        //记录操作日志
        addOpenLockLog(lockList, FunctionCode.UPDATE_LOCK_STATE,deviceInfo);
    }

    /**
     * 开锁
     *
     * @param openLockBean 开锁参数
     * @return 操作结果
     */
    @Override
    public Result openLock(OpenLockBean openLockBean) {
        String deviceId = openLockBean.getDeviceId();
        List<String> slotNumList = openLockBean.getSlotNumList();
        //校验锁具编号
        List<Lock> locks = new ArrayList<>();
        checkSlotNum(slotNumList,locks,deviceId);
        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(deviceId);
        //构造指令开锁参数
        FiLinkReqParamsDto reqParamsDto = new FiLinkReqParamsDto();
        reqParamsDto.setCmdId(CmdId.OPEN_LOCK);
        reqParamsDto.setSerialNum(deviceInfo.getSerialNum());
        List<Map<String, Object>> lockMaps = new ArrayList<>();
        for (String slotNum : slotNumList) {
            Map<String, Object> lockMap = new HashMap<>(64);
            lockMap.put("slotNum", slotNum);
            lockMap.put("operate", LockOperator.LOCK_ON);
            lockMaps.add(lockMap);
        }
        Map<String, Object> params = new HashMap<>(64);
        params.put("params", lockMaps);
        reqParamsDto.setParams(params);
        //发送指令
        fiLinkStationFeign.sendUdpReq(reqParamsDto);
        //记录日志
        addOpenLockLog(locks, FunctionCode.OPEN_LOCK_CODE,deviceInfo);
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getString(LockI18n.OPEN_LOCK_SUCCESS));
    }


    /**
     * 校验锁具编号
     * @param slotNumList 锁具编号集合
     * @param locks 电子锁集合
     * @param deviceId 设施id
     */
    private void checkSlotNum(List<String> slotNumList, List<Lock> locks, String deviceId) {
        for (String slotNum : slotNumList) {
            Lock lock = lockDao.queryLockByDeviceIdAndSlotNum(deviceId, slotNum);
            if(lock == null){
                throw new FiLinkLockException("slotNum: "+slotNum+" is not exist>>>>>");
            }
            locks.add(lock);
        }
    }

    /**
     * 根据设施序列id查询设施信息
     * @param deviceId 设施id
     */
    private DeviceInfoDto findDeviceByDeviceId(String deviceId) {
        DeviceInfoDto deviceInfoDto;
        try {
            deviceInfoDto = deviceFeign.getDeviceById(deviceId);
        }
        catch (Exception e){
            throw new FiLinkLockException("device feign execute failed>>>>>");
        }
        if(deviceInfoDto == null){
            throw new FiLinkLockException("device info is null>>>>>");
        }
        return deviceInfoDto;
    }


    /**
     * 记录开锁操作日志
     *
     * @param deviceInfo 设施
     * @param functionCode 操作码
     * @param locks 电子锁集合
     */
    private void addOpenLockLog(List<Lock> locks, String functionCode, DeviceInfoDto deviceInfo) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        List<AddOperateLogReq> logList = new ArrayList<>();
        for (Lock lock : locks) {
            AddOperateLogReq addOperateLogReq = new AddOperateLogReq();
            BeanUtils.copyProperties(addLogBean, addOperateLogReq);
            //操作对象
            addOperateLogReq.setOptObj(deviceInfo.getDeviceName());
            //对象id
            addOperateLogReq.setOptObjId(deviceInfo.getDeviceId());
            addOperateLogReq.setFunctionCode(functionCode);
            //获取语言
            String environment = ((LanguageConfig) SpringUtils.getBean(LanguageConfig.class)).getEnvironment();
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
                    detailTemplateInfo = detailTemplateInfo.replace("${slotNum}", lock.getLockNum());
                    detailTemplateInfo = detailTemplateInfo.replace("${deviceName}", deviceInfo.getDeviceName());
                }
                optName = xmlParseBean.getOptName();
            } catch (Exception e) {
                throw new FiLinkLockException("write lock operation log failed>>>>>");
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
}
