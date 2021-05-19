package com.fiberhome.filink.screen.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.screen.bean.LargeScreen;
import com.fiberhome.filink.screen.constant.LargeScreenConstants;
import com.fiberhome.filink.screen.constant.LargeScreenI18n;
import com.fiberhome.filink.screen.constant.LargeScreenResultCode;
import com.fiberhome.filink.screen.dao.LargeScreenDao;
import com.fiberhome.filink.screen.service.LargeScreenService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     大屏管理 服务实现类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-28
 */
@Slf4j
@Service
public class LargeScreenServiceImpl implements LargeScreenService {
    /**
     * 自动注入dao
     */
    @Autowired
    private LargeScreenDao largeScreenDao;
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * 查询所有大屏
     *
     * @return 大屏信息 List
     */
    @Override
    public Result queryLargeScreenAll() {
        List<LargeScreen> largeScreens = largeScreenDao.queryLargeScreenAll();
        //如果为null，转为空
        if (CollectionUtils.isEmpty(largeScreens)) {
            largeScreens = new ArrayList<>();
        }
        return ResultUtils.success(largeScreens);
    }

    /**
     * 查询大屏名称是否重复
     *
     * @param largeScreen 大屏信息
     * @return 结果
     */
    @Override
    public Result queryLargeScreenNameRepeat(LargeScreen largeScreen) {
        //判断大屏名称是否重复
        if  (checkLargeScreenNameRepeat(largeScreen)) {
            return ResultUtils.success();
        }
        return ResultUtils.warn(LargeScreenResultCode.LARGE_SCREEN_NAME_REPEAT,
                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_REPEAT));
    }

    /**
     * 根据大屏ID修改大屏名称
     *
     * @param largeScreen 大屏信息
     * @return 结果
     */
    @Override
    public Result updateLargeScreenNameById(LargeScreen largeScreen) {
        if  (!checkLargeScreenNameRepeat(largeScreen)) {
            //大屏名称重复
            return ResultUtils.warn(LargeScreenResultCode.LARGE_SCREEN_NAME_REPEAT,
                    I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_REPEAT));
        }
        Integer result = largeScreenDao.updateLargeScreenNameById(largeScreen);
        if (result != 1) {
            //数据库操作异常
            return ResultUtils.warn(LargeScreenResultCode.LARGE_SCREEN_NAME_UPDATE_FAIL,
                    I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_UPDATE_FAIL));
        }
        //记录日志
        addLog(largeScreen, LargeScreenConstants.LARGE_SCREEN_UPDATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        return ResultUtils.success(LargeScreenResultCode.SUCCESS,
                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_UPDATE_SUCCESS));
    }
    /**
     * 判断大屏名称是否重复
     *
     * @param largeScreen 大屏信息
     * @return true 没有重复 false 重复
     */
    private boolean checkLargeScreenNameRepeat(LargeScreen largeScreen) {
        //根据名称查询不等于大屏ID的大屏ID
        String largeScreenId = largeScreenDao.queryLargeScreenNameRepeat(largeScreen);
        //判断是否重复
        return StringUtils.isEmpty(largeScreenId);
    }

    /**
     * 新增日志
     * @param largeScreen 大屏信息
     * @param functionCode XML functionCode
     * @param logConstants 操作类型
     */
    private void addLog(LargeScreen largeScreen, String functionCode, String logConstants) {
        systemLanguageUtil.querySystemLanguage();
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(LargeScreenConstants.LARGE_SCREEN_ID);
        addLogBean.setDataName(LargeScreenConstants.LARGE_SCREEN_NAME);
        //获得操作对象名称
        addLogBean.setOptObj(largeScreen.getLargeScreenName());
        addLogBean.setFunctionCode(functionCode);
        //获得操作对象id
        addLogBean.setOptObjId(largeScreen.getLargeScreenId());
        //操作为新增
        addLogBean.setDataOptType(logConstants);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
