package com.fiberhome.filink.alarmsetserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.bean.AlarmSetI18;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmNameService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.MpQueryHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  当前告警设置服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
public class AlarmNameServiceImpl extends ServiceImpl<AlarmNameDao, AlarmName> implements AlarmNameService {

    @Autowired
    private AlarmNameDao alarmNameDao;

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 封装条件
     * @return 告警名称信息
     */
    @Override
    public Result queryAlarmNameList(QueryCondition queryCondition) {
        EntityWrapper entityWrapper = MpQueryHelper.MyBatiesBuildQuery(queryCondition);
        List<AlarmName> alarmNames = alarmNameDao.selectList(entityWrapper);
        if (alarmNames == null || alarmNames.size() == 0) {
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.ALARM_NAME_NULL));
        }
        return ResultUtils.success(alarmNames);
    }

    /**
     * 查询单个告警设置信息
     *
     * @param alarmId 告警设置ID
     * @return 告警名称信息
     */
    @Override
    public Result queryAlarmCurrentSetById(String alarmId) {
        AlarmName alarmName = alarmNameDao.selectById(alarmId);
        if (alarmName == null) {
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.SINGLE_ALARM_FAILED));
        }
        return ResultUtils.success(alarmName);
    }

    /**
     * 修改告警设置信息
     *
     *+ @param alarmName 告警信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "1", functionCode = "1701101",
            dataGetColumnName = "alarmName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmCurrentSet(AlarmName alarmName) {
        Integer updateById = alarmNameDao.updateById(alarmName);
        if (updateById != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AlarmSetI18.UPDATE_ALARM_FAILED));
        }
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getString(AlarmSetI18.UPDATE_ALARM_SUCCESS));
    }


}
