package com.fiberhome.filink.alarmsetserver.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.bean.AlarmSetI18;
import com.fiberhome.filink.alarmsetserver.dao.AlarmLevelDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmLevelService;
import com.fiberhome.filink.alarmsetserver.utils.AlarmSetCode;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  告警级别设置服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
public class AlarmLevelServiceImpl extends ServiceImpl<AlarmLevelDao, AlarmLevel> implements AlarmLevelService {

    @Autowired
    private AlarmLevelDao alarmLevelDao;

    /**
     * 查询告警级别信息列表
     *
     * @return 告警级别信息
     */
    @Override
    public Result queryAlarmLevelList() {
        List<AlarmLevel> alarmLevel = alarmLevelDao.selectList(null);
        if(alarmLevel == null || alarmLevel.size() == 1){
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.ALARM_LEVEL_FAILED));
        }
        return ResultUtils.success(alarmLevel);
    }

    /**
     * 修改告警级别
     *
     * @param alarmLevel 告警级别
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "1", functionCode = "1701201",
            dataGetColumnName = "alarmLevelName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmLevel(AlarmLevel alarmLevel) {
        // 判断颜色是否被使用
        List<AlarmLevel> lists = alarmLevelDao.queryAlarmLevelColor(alarmLevel.getId());
        AlarmLevel alarmLevel2 = lists.stream().filter((AlarmLevel alarmLevel1) ->
                alarmLevel1.getAlarmLevelColor().equals(alarmLevel.getAlarmLevelColor())).findFirst().orElse(null);
        if (alarmLevel2 != null) {
            return ResultUtils.success(AlarmSetCode.COLOR_USED,I18nUtils.getString(AlarmSetI18.COLOR_USED));
        }
        // 修改
        Integer result = alarmLevelDao.updateById(alarmLevel);
        if(result != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AlarmSetI18.SET_UP_FAILED));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AlarmSetI18.SET_UP_SUCCESS));
    }

    /**
     * 查询单个告警级别信息
     *
     * @param alarmId 告警级别ID
     * @return 告警级别信息
     */
    @Override
    public Result queryAlarmLevelById(String alarmId) {
        AlarmLevel alarmLevel = alarmLevelDao.selectById(alarmId);
        if(alarmLevel == null ){
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.ALARM_ID_FAILED));
        }
        return ResultUtils.success(alarmLevel);
    }

    /**
     * 查询告警级别
     *
     * @return 告警级别信息
     */
    @Override
    public Result queryAlarmLevel() {
        List<AlarmLevel> alarmLevels = alarmLevelDao.selectAlarmLevel();
        if (alarmLevels == null || alarmLevels.size() == 0) {
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.ALARM_LEVEL_NULL));
        }
        return ResultUtils.success(alarmLevels);
    }
}
