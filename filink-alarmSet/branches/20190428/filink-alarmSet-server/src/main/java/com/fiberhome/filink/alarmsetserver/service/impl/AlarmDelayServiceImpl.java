package com.fiberhome.filink.alarmsetserver.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.dao.AlarmDelayDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmDelayService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 历史告警设置服务实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
@Slf4j
public class AlarmDelayServiceImpl extends ServiceImpl<AlarmDelayDao, AlarmDelay> implements AlarmDelayService {

    /**
     * 历史告警设置dao
     */
    @Autowired
    private AlarmDelayDao alarmDelayDao;

    /**
     * 日志
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 查询历史设置信息
     *
     * @return 历史设置信息
     */
    @Override
    public Result selectAlarmDelay() {
        AlarmDelay alarmDelay = alarmDelayDao.selectDelay();
        log.info("key = " + RedisUtils.hasKey(alarmDelay.getId() + AppConstant.REDIS_DELAY));
        RedisUtils.set(alarmDelay.getId() + AppConstant.REDIS_DELAY, alarmDelay);
        return ResultUtils.success(alarmDelay);
    }

    /**
     * 定时任务查询历史告警设置时间
     *
     * @return 历史告警设置时间
     */
    @Override
    public Integer selectAlarmDelayTime() {
        AlarmDelay alarmDelay = alarmDelayDao.selectDelay();
        if (alarmDelay == null) {
            return 0;
        }
        return alarmDelay.getDelay();
    }

    /**
     * 修改入库时间
     *
     * @param alarmDelay 延时入库信息
     * @return 判断结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAlarmDelay(AlarmDelay alarmDelay) {
        Integer updateById = alarmDelayDao.updateDelay(alarmDelay);
        if (updateById != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_TIAM_FAILED));
        }
        RedisUtils.remove(alarmDelay.getId() + AppConstant.REDIS_DELAY);
        updateLog(AppConstant.ALARM_LOG_ONE, AppConstant.DELAY, alarmDelay);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.UPDATE_TIAM_SUCCESS));
    }

    /**
     * 修改日志记录
     *
     * @param alarmDelay 历史告警时间
     * @param model      模板
     * @param name       名称
     */
    private void updateLog(String model, String name, AlarmDelay alarmDelay) {
        // 获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(AppConstant.ALARM_ID);
        addLogBean.setDataName(name);
        addLogBean.setFunctionCode(model);
        // 获取操作对象
        addLogBean.setOptObjId(alarmDelay.getId());
        addLogBean.setOptObj(String.valueOf(alarmDelay.getDelayName()));
        // 操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // 新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
