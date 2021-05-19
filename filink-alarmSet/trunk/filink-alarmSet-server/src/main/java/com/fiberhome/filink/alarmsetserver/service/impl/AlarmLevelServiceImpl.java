package com.fiberhome.filink.alarmsetserver.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.constant.AlarmSetResultCode;
import com.fiberhome.filink.alarmsetserver.dao.AlarmLevelDao;
import com.fiberhome.filink.alarmsetserver.service.AlarmLevelService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 告警级别设置服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
@Service
public class AlarmLevelServiceImpl extends ServiceImpl<AlarmLevelDao, AlarmLevel> implements AlarmLevelService {

    /**
     * 告警级别dao
     */
    @Autowired
    private AlarmLevelDao alarmLevelDao;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询告警级别信息列表
     *
     * @return 告警级别信息
     */
    @Override
    public Result queryAlarmLevelList() {
        List<AlarmLevel> alarmLevel = alarmLevelDao.selectList(null);
        RedisUtils.set(AppConstant.ALARM_REDIS_LEVEL, alarmLevel);
        return ResultUtils.success(alarmLevel);
    }

    /**
     * 修改告警级别
     *
     * @param alarmLevel 告警级别
     * @return 修改结果
     */
    @AddLogAnnotation(value = AppConstant.UPDATE, logType = "1", functionCode = AppConstant.ALARM_LOG_TEN,
            dataGetColumnName = "alarmLevelName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmLevel(AlarmLevel alarmLevel) {
        // 查询当前id以外其它的级别
        List<AlarmLevel> lists = alarmLevelDao.queryAlarmLevelColor(alarmLevel.getId());
        // 判断所选的颜色和数据库已选的颜色匹配
        AlarmLevel alarmLevelTwo = lists.stream().filter((AlarmLevel alarmLevelOne) ->
                alarmLevelOne.getAlarmLevelColor().equals(alarmLevel.getAlarmLevelColor())).findFirst().orElse(null);
        // 判断是否匹配
        if (alarmLevelTwo != null) {
            return ResultUtils.warn(AlarmSetResultCode.COLOR_USED, I18nUtils.getSystemString(AppConstant.COLOR_USED));
        }
        Integer result = alarmLevelDao.updateById(alarmLevel);
        if (result != 1) {
            return ResultUtils.warn(AlarmSetResultCode.SET_UP_FAILED, I18nUtils.getSystemString(AppConstant.SET_UP_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_LEVEL);
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.SET_UP_SUCCESS));
    }

    /**
     * 查询单个告警级别信息
     *
     * @param alarmId 告警级别ID
     * @return 告警级别信息
     */
    @Override
    public Result queryAlarmLevelById(String alarmId) {
        List<AlarmLevel> alarmLevels = new ArrayList<>();
        AlarmLevel alarmLevel = new AlarmLevel();
        if (RedisUtils.hasKey(AppConstant.ALARM_REDIS_LEVEL)) {
            alarmLevels = (List<AlarmLevel>) RedisUtils.get(AppConstant.ALARM_REDIS_LEVEL);
            // 判断缓存是否有数据
            if (ListUtil.isEmpty(alarmLevels)) {
                alarmLevel = alarmLevelDao.selectById(alarmId);
            } else {
                List<AlarmLevel> alarmLevelList = alarmLevels.stream().filter(alarmLevelOne ->
                        alarmId.equals(alarmLevelOne.getId())).collect(Collectors.toList());
                alarmLevel = alarmLevelList.get(0);
            }
        }
        if (alarmLevel == null) {
            return ResultUtils.success(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_ID_FAILED));
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
        if (ListUtil.isEmpty(alarmLevels)) {
            return ResultUtils.success(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_LEVEL_NULL));
        }
        return ResultUtils.success(alarmLevels);
    }

    /**
     * 根据告警级别编码查询告警级别设置信息
     *
     * @param alarmLevelCode 告警级别编码
     * @return 告警级别
     */
    @Override
    public AlarmLevel queryAlarmLevelSetFeign(String alarmLevelCode) {
        return alarmLevelDao.queryAlarmLevelSetFeign(alarmLevelCode);
    }
}
