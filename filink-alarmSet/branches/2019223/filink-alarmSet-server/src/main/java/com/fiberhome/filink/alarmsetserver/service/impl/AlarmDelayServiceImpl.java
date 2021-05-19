package com.fiberhome.filink.alarmsetserver.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelayDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmSetI18;
import com.fiberhome.filink.alarmsetserver.dao.AlarmDelayDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmDelayService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.server_common.utils.I18nUtils;
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
public class AlarmDelayServiceImpl extends ServiceImpl<AlarmDelayDao, AlarmDelay> implements AlarmDelayService {

    @Autowired
    private AlarmDelayDao alarmDelayDao;

    /**
     * 查询历史设置信息
     *
     * @return 历史设置信息
     */
    @Override
    public Result selectAlarmDelay() {
        AlarmDelay alarmDelay = alarmDelayDao.selectDelay();
        if (alarmDelay == null) {
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.HISTORY_QUERY_FAILED));
        }
        return ResultUtils.success(alarmDelay);
    }

    /**
     * 修改入库时间
     *
     * @param alarmDelayDto 延时入库信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "1", functionCode = "1701301", dataGetColumnName = "alarmDelayName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmDelay(AlarmDelayDto alarmDelayDto) {
        Integer updateById = alarmDelayDao.updateDelay(alarmDelayDto.getAlarmDelay());
        if (updateById != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AlarmSetI18.UPDATE_TIAM_FAILED));
        }
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getString(AlarmSetI18.UPDATE_TIAM_SUCCESS));
    }
}
