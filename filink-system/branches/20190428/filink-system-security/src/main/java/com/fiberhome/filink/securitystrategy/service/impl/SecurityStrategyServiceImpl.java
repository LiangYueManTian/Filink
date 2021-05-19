package com.fiberhome.filink.securitystrategy.service.impl;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.securitystrategy.bean.AccountParam;
import com.fiberhome.filink.securitystrategy.bean.AccountSecurityStrategy;
import com.fiberhome.filink.securitystrategy.bean.PasswordParam;
import com.fiberhome.filink.securitystrategy.bean.PasswordSecurityStrategy;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.exception.FilinkSecurityStrategyDataException;
import com.fiberhome.filink.securitystrategy.exception.FilinkSecurityStrategyDatabaseException;
import com.fiberhome.filink.securitystrategy.service.SecurityStrategyService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.bean.SysParam;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import com.fiberhome.filink.systemcommons.dao.SysParamDao;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-20
 */
@Service
public class SecurityStrategyServiceImpl implements SecurityStrategyService {

    /**
     * 自动注入系统服务统一参数dao
     */
    @Autowired
    private SysParamDao sysParamDao;
    /**
     * 自动注入系统服务统一参数Service
     */
    @Autowired
    private SysParamService sysParamService;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 查询账号安全策略
     * @return 账号安全策略
     */
    @Override
    public Result queryAccountSecurity() {
        //获取Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.ACCOUNT.getType());
        AccountSecurityStrategy accountSecurityStrategy = JSON.parseObject(sysParam.getPresentValue(), AccountSecurityStrategy.class);
        return ResultUtils.success(accountSecurityStrategy);
    }

    /**
     * 查询密码安全策略
     *
     * @return 密码安全策略
     */
    @Override
    public Result queryPasswordSecurity() {
        //获取Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.PASSWORD.getType());
        PasswordSecurityStrategy passwordSecurityStrategy = JSON.parseObject(sysParam.getPresentValue(), PasswordSecurityStrategy.class);
        return ResultUtils.success(passwordSecurityStrategy);
    }

    /**
     * 更新密码安全策略
     *
     * @param passwordParam 密码安全策略
     * @return 更新条数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePasswordStrategy(PasswordParam passwordParam) {
        //转成JSON
        String passwordStrategy = JSON.toJSONString(passwordParam.getPasswordSecurityStrategy());
        //修改数据库
        SysParam sysParam = updateSysParam(passwordParam.getParamId(), passwordStrategy);
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.PASSWORD.getKey(), sysParam);
        //记录日志
        addLog(sysParam.getParamId(), I18nUtils.getString(SecurityStrategyI18n.PASSWORD_SECURITY_STRATEGY), SecurityStrategyConstants.PASSWORD_FUNCTION_CODE);
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getString(SecurityStrategyI18n.PASSWORD_STRATEGY_UPDATE_SUCCESS));
    }

    /**
     * 更新账号安全策略
     *
     * @param accountParam 账号安全策略
     * @return 更新条数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAccountStrategy(AccountParam accountParam) {
        //转成JSON
        String accountStrategy = JSON.toJSONString(accountParam.getAccountSecurityStrategy());
        //更新数据库
        SysParam sysParam = updateSysParam(accountParam.getParamId(), accountStrategy);
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.ACCOUNT.getKey(), sysParam);
        //记录日志
        addLog(sysParam.getParamId(), I18nUtils.getString(SecurityStrategyI18n.ACCOUNT_SECURITY_STRATEGY), SecurityStrategyConstants.ACCOUNT_FUNCTION_CODE);
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getString(SecurityStrategyI18n.ACCOUNT_STRATEGY_UPDATE_SUCCESS));
    }

    /**
     * 更新数据库
     * @param paramId 参数ID
     * @param presentValue  当前参数值
     * @return 系统参数（当前和默认）
     */
    private SysParam updateSysParam(String paramId, String presentValue) {
        //查询数据库数据
        SysParam sysParam = sysParamDao.queryParamById(paramId);
        //校验是否有数据
        if (ObjectUtils.isEmpty(sysParam) || sysParam.checkValue()) {
            throw new FilinkSecurityStrategyDataException();
        }
        //封装数据
        sysParam.setPresentValue(presentValue);
        //获取用户ID
        sysParam.setUpdateUser(RequestInfoUtils.getUserId());
        //更新数据库
        Integer result = sysParamDao.updateParamById(sysParam);
        //判断结果
        if (result != 1) {
            throw new FilinkSecurityStrategyDatabaseException();
        }
        return sysParam;
    }

    /**
     * 新增日志
     * @param paramId 参数ID
     * @param securityStrategyName 策略名称
     * @param functionCode XML functionCode
     */
    private void addLog(String paramId, String securityStrategyName, String functionCode) {
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(SecurityStrategyConstants.PARAM_ID);
        addLogBean.setDataName(SecurityStrategyConstants.SECURITY_STRATEGY_NAME);
        //获得操作对象名称
        addLogBean.setOptObj(securityStrategyName);
        addLogBean.setFunctionCode(functionCode);
        //获得操作对象id
        addLogBean.setOptObjId(paramId);
        //操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
