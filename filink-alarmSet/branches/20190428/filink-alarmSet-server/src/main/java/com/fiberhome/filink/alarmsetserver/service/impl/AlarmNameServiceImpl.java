package com.fiberhome.filink.alarmsetserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmNameService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.MpQueryHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPageBean;


/**
 * <p>
 * 当前告警设置服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
@Service
public class AlarmNameServiceImpl extends ServiceImpl<AlarmNameDao, AlarmName> implements AlarmNameService {

    /**
     * 告警名称dao
     */
    @Autowired
    private AlarmNameDao alarmNameDao;

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 条件
     * @return 告警名称信息
     */
    @Override
    public Result queryAlarmNameList(QueryCondition queryCondition) {
        EntityWrapper entityWrapper = MpQueryHelper.myBatiesBuildQuery(queryCondition);
        List<AlarmName> alarmNames = alarmNameDao.selectList(entityWrapper);
        log.info("key = " + RedisUtils.hasKey(AppConstant.REDIS_NAME));
        RedisUtils.set(AppConstant.REDIS_NAME, alarmNames);
        return ResultUtils.success(alarmNames);
    }

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 条件
     * @return 告警名称信息
     */
    @Override
    public Result queryAlarmNamePage(QueryCondition queryCondition) {
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("isOrder");
        filterCondition.setOperator("eq");
        filterCondition.setFilterValue("1");
        filterConditions.add(filterCondition);
        queryCondition.setFilterConditions(filterConditions);
        // 分页条件
        Page page = MpQueryHelper.myBatiesBuildPage(queryCondition);
        // 过滤条件
        EntityWrapper entityWrapper = MpQueryHelper.myBatiesBuildQuery(queryCondition);
        // 查询列表信息
        List<AlarmName> alarmNames = alarmNameDao.selectPage(page, entityWrapper);
        // 总记录数
        Integer count = alarmNameDao.selectCount(entityWrapper);
        PageBean pageBean = myBatiesBuildPageBean(page, count, alarmNames);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 查询单个告警设置信息
     *
     * @param array 告警设置ID
     * @return 告警名称信息
     */
    @Override
    public Result queryAlarmCurrentSetById(String[] array) {
        List<AlarmName> alarmNames = new ArrayList<>();
        if (RedisUtils.hasKey(AppConstant.REDIS_NAME)) {
            alarmNames = (List<AlarmName>) RedisUtils.get(AppConstant.REDIS_NAME);
            if (ListUtil.isEmpty(alarmNames)) {
                alarmNames = alarmNameDao.selectByIds(array);
            } else {
                List<AlarmName> alarmNameList = alarmNames.stream().filter(alarmName ->
                        array[0].equals(alarmName.getId())).collect(Collectors.toList());
                alarmNames = alarmNameList;
            }
        }
        if (ListUtil.isEmpty(alarmNames)) {
            return ResultUtils.success(ResultCode.FAIL, I18nUtils.getString(AppConstant.SINGLE_ALARM_FAILED));
        }
        return ResultUtils.success(alarmNames);
    }

    /**
     * 根据告警编码查询告警名称信息
     *
     * @param alarmCode 告警编码
     * @return 告警名称信息
     */
    @Override
    public AlarmName queryCurrentAlarmSetFeign(String alarmCode) {
        AlarmName alarmName = alarmNameDao.selectAlarmByCode(alarmCode);
        return alarmName;
    }

    /**
     * 根据告警名称查询当前告警设置信息
     *
     * @param alarmName 告警名称
     * @return 告警设置信息
     */
    @Override
    public AlarmName queryCurrentAlarmSetByNameFeign(String alarmName) {
        AlarmName alarmNameInfo = alarmNameDao.selectAlarmByName(alarmName);
        return alarmNameInfo;
    }

    /**
     * 修改告警设置信息
     *
     * @param alarmName 告警信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.UPDATE, logType = "1", functionCode = AppConstant.ALARM_LOG_ELEVEN,
            dataGetColumnName = "alarmName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmCurrentSet(AlarmName alarmName) {
        Integer updateById = alarmNameDao.updateById(alarmName);
        if (updateById != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_NAME);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.UPDATE_ALARM_SUCCESS));
    }
}
