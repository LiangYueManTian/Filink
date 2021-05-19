package com.fiberhome.filink.dump.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpParam;
import com.fiberhome.filink.dump.constant.DumpConstants;
import com.fiberhome.filink.dump.constant.DumpI18n;
import com.fiberhome.filink.dump.constant.DumpLogCode;
import com.fiberhome.filink.dump.constant.DumpResultCode;
import com.fiberhome.filink.dump.service.DumpService;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.bean.SysParam;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import com.fiberhome.filink.systemcommons.dao.SysParamDao;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum.ALARM_LOG_DUMP;
import static com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum.DEVICE_LOG_DUMP;
import static com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum.SYSTEM_LOG_DUMP;

/**
 * <p>
 * 转储策略逻辑层实现
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/29
 */
@Service
@Slf4j
public class DumpServiceImpl implements DumpService {
    /**
     * 注入ProtocolDao对象
     */
    @Autowired
    private SysParamDao sysParamDao;

    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;

    @Autowired
    private SystemLanguageUtil  systemLanguageUtil;

    /**
     * 更新转存策略
     *
     * @param dumpParam 策略参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDump(DumpParam dumpParam) {
        //获得转储策略id、类型、内容
        String dumpId = dumpParam.getParamId();
        String dumpType = dumpParam.getParamType();
        DumpBean dumpBean = dumpParam.getDumpBean();

        //构建更新时要用的实体
        SysParam dump = new SysParam();
        dump.setParamId(dumpId);
        dump.setPresentValue(JSON.toJSONString(dumpBean));
        dump.setUpdateUser(RequestInfoUtils.getUserId());

        //查询策略是否存在
        SysParam sysParam = sysParamDao.queryParamById(dumpId);
        if (sysParam == null) {
            //要更新的策略不存在
            return ResultUtils.warn(DumpResultCode.DUMP_EMPTY, I18nUtils.getSystemString(DumpI18n.DUMP_EMPTY));
        }
        sysParamDao.updateParamById(dump);


        //更新缓存
        String key = ParamTypeRedisEnum.getKeyByType(dumpType);
        sysParam.setPresentValue(dump.getPresentValue());
        RedisUtils.set(key, sysParam);
        //记日志
        addLogByType(dumpId, dumpType);


        return generateResulByType(dumpType);
    }


    /**
     * 新增日志
     *
     * @param paramId      参数ID
     * @param dumpPolicy   转储策略
     * @param functionCode XML functionCode
     */
    private void addLog(String paramId, String dumpPolicy, String functionCode) {
        systemLanguageUtil.querySystemLanguage();
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(DumpConstants.PARAM_ID);
        addLogBean.setDataName(DumpConstants.PARAM_TYPE);
        //获得操作对象名称
        addLogBean.setOptObj(dumpPolicy);
        addLogBean.setFunctionCode(functionCode);
        //获得操作对象id
        addLogBean.setOptObjId(paramId);
        //操作为更新
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 调用新增日志方法
     *
     * @param paramId 参数ID
     * @param type    协议类型
     */
    private void addLogByType(String paramId, String type) {
        if (SYSTEM_LOG_DUMP.getType().equals(type)) {
            addLog(paramId, I18nUtils.getSystemString(DumpI18n.SYSTEM_LOG_DUMP), DumpLogCode.UPDATE_SYSTEM_LOG_DUMP_FUNCTION_CODE);
        } else if (DEVICE_LOG_DUMP.getType().equals(type)) {
            addLog(paramId, I18nUtils.getSystemString(DumpI18n.DEVICE_LOG_DUMP), DumpLogCode.UPDATE_DEVICE_LOG_DUMP_FUNCTION_CODE);
        } else if (ALARM_LOG_DUMP.getType().equals(type)) {
            addLog(paramId, I18nUtils.getSystemString(DumpI18n.ALARM_LOG_DUMP), DumpLogCode.UPDATE_ALARM_LOG_DUMP_FUNCTION_CODE);
        }
    }

    /**
     * 根据协议类型响应
     *
     * @param type 协议类型
     * @return 响应结果
     */
    private Result generateResulByType(String type) {
        Result result = new Result();
        if (SYSTEM_LOG_DUMP.getType().equals(type)) {
            result = ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(DumpI18n.UPDATE_SYSTEM_LOG_DUMP_SUCCESS));
        } else if (DEVICE_LOG_DUMP.getType().equals(type)) {
            result = ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(DumpI18n.UPDATE_DEVICE_LOG_DUMP_SUCCESS));
        } else if (ALARM_LOG_DUMP.getType().equals(type)) {
            result = ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(DumpI18n.UPDATE_ALARM_LOG_DUMP_SUCCESS));
        }
        return result;
    }

    /**
     * 查询转储策略
     *
     * @param type 类型
     * @return 转储策略
     */
    @Override
    public DumpBean queryDump(String type) {
        sysParamDao.queryParamByType(type);
        SysParam sysParam = sysParamDao.queryParamByType(type);
        String presentValue = sysParam.getPresentValue();
        DumpBean dumpBean = new DumpBean();
        Map<String,String> map = JSONObject.parseObject(presentValue, Map.class);
        try{
            BeanUtils.populate(dumpBean, map);
        }catch (Exception e){
            log.error("dumpMap转成dumpBean过程出错：{}", e.getMessage());
        }
        return dumpBean;
    }
}
