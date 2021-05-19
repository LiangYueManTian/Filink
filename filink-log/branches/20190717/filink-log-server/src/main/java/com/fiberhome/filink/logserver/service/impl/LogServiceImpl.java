package com.fiberhome.filink.logserver.service.impl;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logserver.bean.*;
import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.logserver.constant.LogConstants;
import com.fiberhome.filink.logserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.logserver.export.statistics.LogTypeExport;
import com.fiberhome.filink.logserver.export.statistics.OperateTypeExport;
import com.fiberhome.filink.logserver.export.statistics.SecurityLevelExport;
import com.fiberhome.filink.logserver.logexport.OperateLogExport;
import com.fiberhome.filink.logserver.logexport.SecurityLogExport;
import com.fiberhome.filink.logserver.logexport.SysLogExport;
import com.fiberhome.filink.logserver.req.AddOperateLogReq;
import com.fiberhome.filink.logserver.req.AddSecurityLogReq;
import com.fiberhome.filink.logserver.req.AddSystemLogReq;
import com.fiberhome.filink.logserver.service.FunctionDangerLevelConfigService;
import com.fiberhome.filink.logserver.service.LogService;
import com.fiberhome.filink.logserver.utils.ExportStatisticUtil;
import com.fiberhome.filink.logserver.utils.LogI18nCast;
import com.fiberhome.filink.logserver.utils.LogResultCode;
import com.fiberhome.filink.logserver.utils.PageBeanHelper;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiberhome.filink.logserver.constant.I18nConstants.*;
import static com.fiberhome.filink.logserver.constant.LogConstants.*;
import static com.fiberhome.filink.logserver.constant.LogConstants.DANGER_LEVEL_DANGER;
import static com.fiberhome.filink.logserver.constant.LogConstants.DANGER_LEVEL_GENERAL;
import static com.fiberhome.filink.logserver.constant.LogConstants.DANGER_LEVEL_PROMPT;

/**
 * @author hedongwei@wistronits.com
 * description 日志逻辑层
 * date 2019/1/14 14:44
 */
@RefreshScope
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FunctionDangerLevelConfigService functionDangerLevelConfigService;
    /**
     * 注入操作日志导出类
     */
    @Autowired
    private OperateLogExport operateLogExport;
    /**
     * 注入安全日志导出类
     */
    @Autowired
    private SecurityLogExport securityLogExport;
    /**
     * 注入系统日志导出类
     */
    @Autowired
    private SysLogExport sysLogExport;
    /**
     * 服务名
     */
    private static String SERVER_NAME = "filink-log-server";
    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 系统语言
     */
    @Autowired
    private SystemLanguageUtil systemLanguage;

    /**
     * 日志类型统计导出类
     */
    @Autowired
    private LogTypeExport logTypeExport;

    /**
     * 操作类型统计导出类
     */
    @Autowired
    private OperateTypeExport operateTypeExport;

    /**
     * 安全级别统计导出类
     */
    @Autowired
    private SecurityLevelExport securityLevelExport;

    /**
     * @author hedongwei@wistronits.com
     * description 新增操作日志信息
     * date 14:46 2019/1/14
     * param [log]
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOperateLog(AddOperateLogReq operateLogReq) {
        if (null != operateLogReq) {
            OperateLog operateLog = new OperateLog();
            //获取日志编号
            if (StringUtils.isEmpty(operateLogReq.getLogId())) {
                String logId = NineteenUUIDUtils.uuid();
                operateLogReq.setLogId(logId);
            }

            if (!StringUtils.isEmpty(operateLogReq.getFunctionCode())) {
                //获得危险级别信息
                Integer dangerLevelName = this.getDangerLevelForFunctionCode(operateLogReq.getFunctionCode());
                operateLogReq.setDangerLevel(dangerLevelName);
            }
            //获得操作名称
            String optResultName = LogConstants.OPT_RESULT_SUCCESS;
            operateLogReq.setOptResult(optResultName);
            //获得操作类型
            String optTypeName = operateLogReq.getOptType();
            operateLogReq.setOptType(optTypeName);
            //新增操作日志信息
            BeanUtils.copyProperties(operateLogReq, operateLog);
            mongoTemplate.save(operateLog);
        }
        return true;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 新增安全日志信息
     * date 14:46 2019/1/14
     * param [log]
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSecurityLog(AddSecurityLogReq securityLogReq) {
        if (null != securityLogReq) {
            SecurityLog securityLog = new SecurityLog();
            //获取日志编号
            if (StringUtils.isEmpty(securityLogReq.getLogId())) {
                String logId = NineteenUUIDUtils.uuid();
                securityLogReq.setLogId(logId);
            }
            //获得危险级别信息
            Integer dangerLevelName = this.getDangerLevelForFunctionCode(securityLogReq.getFunctionCode());
            securityLogReq.setDangerLevel(dangerLevelName);
            //获得操作名称
            String optResultName = LogConstants.OPT_RESULT_SUCCESS;
            securityLogReq.setOptResult(optResultName);
            //获得操作类型
            String optTypeName = securityLogReq.getOptType();
            securityLogReq.setOptType(optTypeName);
            BeanUtils.copyProperties(securityLogReq, securityLog);
            //新增安全日志信息
            mongoTemplate.save(securityLog);
        }
    }


    /**
     * @author hedongwei@wistronits.com
     * description 新增系统日志信息
     * date 14:46 2019/1/14
     * param [log]
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSystemLog(AddSystemLogReq systemLogReq) {
        if (null != systemLogReq) {
            //获取日志编号(UUID)
            SystemLog systemLog = new SystemLog();
            if (StringUtils.isEmpty(systemLogReq.getLogId())) {
                String logId = NineteenUUIDUtils.uuid();
                systemLogReq.setLogId(logId);
            }

            //设置日志的执行者为系统
            systemLogReq.setOptUserCode("system");
            systemLogReq.setOptUserName("system");
            systemLogReq.setOptUserRole("system");
            systemLogReq.setOptUserRoleName("system");

            //获得危险级别信息
            Integer dangerLevelName = this.getDangerLevelForFunctionCode(systemLogReq.getFunctionCode());
            systemLogReq.setDangerLevel(dangerLevelName);
            //获得操作名称
            String optResultName = LogConstants.OPT_RESULT_SUCCESS;
            systemLogReq.setOptResult(optResultName);
            //获得操作类型
            String optTypeName = systemLogReq.getOptType();
            systemLogReq.setOptType(optTypeName);
            //新增系统日志信息
            BeanUtils.copyProperties(systemLogReq, systemLog);
            mongoTemplate.save(systemLog);
        }
    }

    /**
     * 自动新增操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/17 13:32
     */
    @Override
    public void autoInsertOperateLog() {
        List<OperateLog> operateLogList = new ArrayList<>();
        OperateLog operateLog = new OperateLog();
        operateLog.setOptUserCode("system");
        operateLog.setOptUserName("system");
        operateLog.setOptUserRole("system");
        operateLog.setOptUserRoleName("system");
        operateLog.setOptTerminal("127.0.0.1");
        operateLog.setOptTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        operateLog.setOptType(LogConstants.OPT_RESULT_SUCCESS);
        operateLog.setDetailInfo("测试信息");
        operateLog.setRemark("111");
        operateLog.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        operateLog.setCreateUser("system");

        for (int i = 0 ; i < 100000; i++) {
            OperateLog operateLogOne = new OperateLog();
            BeanUtils.copyProperties(operateLog, operateLogOne);
            operateLogOne.setLogId(NineteenUUIDUtils.uuid());
            operateLogOne.setOptObj("新增测试操作日志" + i);
            operateLogOne.setOptObjId(i + "");
            operateLogOne.setOptName("性能测试数据" + i);
            if (i % 2 == 0) {
                operateLogOne.setOptType(LogConstants.OPT_TYPE_PDA);
            } else if (i % 2 == 1) {
                operateLogOne.setOptType(LogConstants.OPT_TYPE_WEB);
            }

            if (i % 3 == 0) {
                operateLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_PROMPT);
            } else if (i % 3 == 1) {
                operateLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_GENERAL);
            } else if (i % 3 == 2) {
                operateLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_DANGER);
            }
            operateLogList.add(operateLogOne);
        }

        List<OperateLog> operateLogInfoList = new ArrayList<>();
        for (int i = 0; i < operateLogList.size(); i ++) {
            operateLogInfoList.add(operateLogList.get(i));
            if (operateLogInfoList.size() == 10000 || i == operateLogList.size() - 1) {
                mongoTemplate.insertAll(operateLogInfoList);
                operateLogInfoList = new ArrayList<>();
            }
        }
    }

    /**
     * 自动新增系统日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/17 13:32
     */
    @Override
    public void autoInsertSystemLog() {
        List<SystemLog> systemList = new ArrayList<>();
        SystemLog systemLog = new SystemLog();
        systemLog.setOptUserCode("system");
        systemLog.setOptUserName("system");
        systemLog.setOptUserRole("system");
        systemLog.setOptUserRoleName("system");
        systemLog.setOptTerminal("127.0.0.1");
        systemLog.setOptTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        systemLog.setOptType(LogConstants.OPT_RESULT_SUCCESS);
        systemLog.setDetailInfo("测试信息");
        systemLog.setRemark("111");
        systemLog.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        systemLog.setCreateUser("system");

        for (int i = 0 ; i < 100000; i++) {
            SystemLog systemLogOne = new SystemLog();
            BeanUtils.copyProperties(systemLog, systemLogOne);
            systemLogOne.setLogId(NineteenUUIDUtils.uuid());
            systemLogOne.setOptObj("新增测试系统日志" + i);
            systemLogOne.setOptObjId(i + "");
            systemLogOne.setOptName("性能测试数据" + i);
            if (i % 2 == 0) {
                systemLogOne.setOptType(LogConstants.OPT_TYPE_PDA);
            } else if (i % 2 == 1) {
                systemLogOne.setOptType(LogConstants.OPT_TYPE_WEB);
            }

            if (i % 3 == 0) {
                systemLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_PROMPT);
            } else if (i % 3 == 1) {
                systemLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_GENERAL);
            } else if (i % 3 == 2) {
                systemLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_DANGER);
            }
            systemList.add(systemLogOne);
        }

        List<SystemLog> systemLogInfoList = new ArrayList<>();
        for (int i = 0; i < systemList.size(); i ++) {
            systemLogInfoList.add(systemList.get(i));
            if (systemLogInfoList.size() == 10000 || i == systemList.size() - 1) {
                mongoTemplate.insertAll(systemLogInfoList);
                systemLogInfoList = new ArrayList<>();
            }
        }
    }

    /**
     * 自动新增安全日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/17 13:32
     */
    @Override
    public void autoInsertSecurityLog() {
        List<SecurityLog> securityLogList = new ArrayList<>();
        SecurityLog securityLog = new SecurityLog();
        securityLog.setOptUserCode("system");
        securityLog.setOptUserName("system");
        securityLog.setOptUserRole("system");
        securityLog.setOptUserRoleName("system");
        securityLog.setOptTerminal("127.0.0.1");
        securityLog.setOptTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        securityLog.setOptType(LogConstants.OPT_RESULT_SUCCESS);
        securityLog.setDetailInfo("测试信息");
        securityLog.setRemark("111");
        securityLog.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        securityLog.setCreateUser("system");

        for (int i = 0 ; i < 100000; i++) {
            SecurityLog securityLogOne = new SecurityLog();
            BeanUtils.copyProperties(securityLog, securityLogOne);
            securityLogOne.setLogId(NineteenUUIDUtils.uuid());
            securityLogOne.setOptObj("新增测试安全日志" + i);
            securityLogOne.setOptObjId(i + "");
            securityLogOne.setOptName("性能测试数据" + i);
            if (i % 2 == 0) {
                securityLogOne.setOptType(LogConstants.OPT_TYPE_PDA);
            } else if (i % 2 == 1) {
                securityLogOne.setOptType(LogConstants.OPT_TYPE_WEB);
            }

            if (i % 3 == 0) {
                securityLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_PROMPT);
            } else if (i % 3 == 1) {
                securityLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_GENERAL);
            } else if (i % 3 == 2) {
                securityLogOne.setDangerLevel(LogConstants.DANGER_LEVEL_DANGER);
            }
            securityLogList.add(securityLogOne);
        }

        List<SecurityLog> securityLogInfoList = new ArrayList<>();
        for (int i = 0; i < securityLogList.size(); i ++) {
            securityLogInfoList.add(securityLogList.get(i));
            if (securityLogInfoList.size() == 10000 || i == securityLogList.size() - 1) {
                mongoTemplate.insertAll(securityLogInfoList);
                securityLogInfoList = new ArrayList<>();
            }
        }
    }

    /**
     * @author hedongwei@wistronits.com
     * description 获得危险级别信息
     * date 16:16 2019/1/22
     * param [functionCode]
     */
    public Integer getDangerLevel(String functionCode) {
        Integer dangerLevel = 0;
        //获取日志编码信息
        if (!StringUtils.isEmpty(functionCode)) {
            FunctionDangerLevelConfig functionDangerLevelConfig = functionDangerLevelConfigService.getDangerLevelConfigByFunctionCode(functionCode);
            if (null != functionDangerLevelConfig) {
                dangerLevel = Integer.parseInt(functionDangerLevelConfig.getDangerLevel());
            }
        }
        return dangerLevel;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询操作日志信息
     * date 10:42 2019/1/15
     * param [queryCondition]
     */
    @Override
    public Result queryListOperateLogByPage(QueryCondition queryCondition) {
        Query query = null;

        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //分页条件不能为空
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //将查询条件创建成query对象
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();

        //获取操作日志个数
        long count = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);

        //查询操作日志列表信息
        List<OperateLog> logList = mongoTemplate.find(query, OperateLog.class);

        //返回分页对象信息，组装分页对象
        PageBean pageBean = PageBeanHelper.generatePageBean(logList, queryCondition, count);

        //返回显示数据
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询安全日志信息
     * date 10:43 2019/1/15
     * param [queryCondition]
     */
    @Override
    public Result queryListSecurityLogByPage(QueryCondition queryCondition) {
        Query query = null;
        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }


        //分页条件不能为空
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }


        //将查询条件创建成query对象
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();

        //获取操作日志个数
        long count = mongoTemplate.count(query, LogConstants.SECURITY_LOG_TABLE_NAME);

        //查询操作日志列表信息
        List<SecurityLog> logList = mongoTemplate.find(query, SecurityLog.class);

        //返回分页对象信息，组装分页对象
        PageBean pageBean = PageBeanHelper.generatePageBean(logList, queryCondition, count);

        //返回显示数据
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询系统日志信息
     * date 10:43 2019/1/15
     * param [queryCondition]
     */
    @Override
    public Result queryListSystemLogByPage(QueryCondition queryCondition) {
        Query query = null;

        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //分页条件不能为空
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //转换成query查询对象
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();

        //获取操作日志个数
        long count = mongoTemplate.count(query, LogConstants.SYSTEM_LOG_TABLE_NAME);

        //查询操作日志列表信息
        List<SystemLog> logList = mongoTemplate.find(query, SystemLog.class);

        //返回分页对象信息，组装分页对象
        PageBean pageBean = PageBeanHelper.generatePageBean(logList, queryCondition, count);

        //返回显示数据
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 按日志类型统计日志
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    public Result countByLogType(QueryCondition queryCondition) {
        Map<String, Long> resultMap = new HashMap<>(64);
        Query query = new Query();

        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        MongoQueryHelper.buildQuery(query, queryCondition);
        //获取系统日志个数
        long systemCount = mongoTemplate.count(query, LogConstants.SYSTEM_LOG_TABLE_NAME);
        //获取安全日志个数
        long securityCount = mongoTemplate.count(query, LogConstants.SECURITY_LOG_TABLE_NAME);
        //获取操作日志个数
        long operateCount = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);

        resultMap.put(LogConstants.OPERATE_LOG_TABLE_NAME, operateCount);
        resultMap.put(LogConstants.SECURITY_LOG_TABLE_NAME, securityCount);
        resultMap.put(LogConstants.SYSTEM_LOG_TABLE_NAME, systemCount);
        return ResultUtils.success(resultMap);
    }

    /**
     * 按操作类型统计日志
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    public Result countByOperateType(QueryCondition queryCondition) {
        Map<String, Long> resultMap = new HashMap<>(64);
        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        Long pdaCount = countPdaOperate(queryCondition);
        Long webCount = countWebOperate(queryCondition);


        resultMap.put(LogConstants.OPT_TYPE_PDA, pdaCount);
        resultMap.put(LogConstants.OPT_TYPE_WEB, webCount);
        return ResultUtils.success(resultMap);
    }

    /**
     * 按安全级别统计日志
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    public Result countBySecurityType(QueryCondition queryCondition) {
        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return ResultUtils.success(countDangerLeverLog(queryCondition));
    }

    /**
     * 校验pageCondition参数信息
     *
     * @param queryCondition 校验pageCondition参数信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/28 15:37
     */
    public boolean checkPageCondition(QueryCondition queryCondition) {
        boolean checkResult = true;
        //分页条件不能为空
        if (null == queryCondition.getPageCondition()) {
            checkResult = false;
        } else {
            //pageNumber不能为空
            if (StringUtils.isEmpty(queryCondition.getPageCondition().getPageNum())) {
                checkResult = false;
            }
            //pageSize不能为空
            if (StringUtils.isEmpty(queryCondition.getPageCondition().getPageSize())) {
                checkResult = false;
            }
        }
        return checkResult;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 根据日志编号查询操作日志详情
     * date 15:48 2019/1/16
     * param [logId]
     */
    @Override
    public Result getOperateLogByLogId(String logId) {
        Query query = new Query();

        //将logId作为查询条件
        Criteria criteria = Criteria.where("logId").is(logId);
        query.addCriteria(criteria);

        //查询操作日志信息
        OperateLog log = mongoTemplate.findOne(query, OperateLog.class);
        if (null == log) {
            JSONObject jsonObject = new JSONObject();
            return ResultUtils.success(jsonObject);
        }
        return ResultUtils.success(log);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 根据日志编号查询安全日志详情
     * date 15:48 2019/1/16
     * param [logId]
     */
    @Override
    public Result getSecurityLogByLogId(String logId) {
        Query query = new Query();

        //将logId作为查询条件
        Criteria criteria = Criteria.where("logId").is(logId);
        query.addCriteria(criteria);

        //查询安全日志信息
        SecurityLog log = mongoTemplate.findOne(query, SecurityLog.class);
        if (null == log) {
            JSONObject jsonObject = new JSONObject();
            return ResultUtils.success(jsonObject);
        }
        return ResultUtils.success(log);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 根据日志编号查询系统日志详情
     * date 15:49 2019/1/16
     * param [logId]
     */
    @Override
    public Result getSystemLogByLogId(String logId) {
        Query query = new Query();

        //将logId作为查询条件
        Criteria criteria = Criteria.where("logId").is(logId);
        query.addCriteria(criteria);

        //查询系统日志信息
        SystemLog log = mongoTemplate.findOne(query, SystemLog.class);
        if (null == log) {
            JSONObject jsonObject = new JSONObject();
            return ResultUtils.success(jsonObject);
        }
        return ResultUtils.success(log);
    }

    /**
     * 创建导出操作日志任务
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportOperateLog(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo = new ExportRequestInfo();
        systemLanguage.querySystemLanguage();
        try {
            exportRequestInfo = operateLogExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(I18nConstants.OPERATE_LOG_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            log.error("export operate log no data error", fe);
            return ResultUtils.warn(LogResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(I18nConstants.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export operate log num too big error", fe);
            return ResultUtils.warn(LogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(I18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export operate log error", e);
            return ResultUtils.warn(LogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(I18nConstants.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_OPERATE_FUNCTION_CODE);
        operateLogExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    @Override
    public Result exportSysLog(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo = new ExportRequestInfo();
        systemLanguage.querySystemLanguage();
        try {
            exportRequestInfo = sysLogExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(I18nConstants.SYSTEM_LOG_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            log.error("export system log no data error", fe);
            return ResultUtils.warn(LogResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(I18nConstants.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export system log num too big error", fe);
            return ResultUtils.warn(LogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(I18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export system log error", e);
            return ResultUtils.warn(LogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(I18nConstants.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_SYSLOG_FUNCTION_CODE);
        sysLogExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    @Override
    public Result exportSecurityLog(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo;
        systemLanguage.querySystemLanguage();
        try {
            exportRequestInfo = securityLogExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(I18nConstants.SECURITY_LOG_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            log.error("export security log no data error", fe);
            return ResultUtils.warn(LogResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(I18nConstants.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export security log num too big error", fe);
            return ResultUtils.warn(LogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(I18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export security log error", e);
            return ResultUtils.warn(LogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(I18nConstants.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_SECURITY_LOG_FUNCTION_CODE);
        securityLogExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }


    /**
     * @author hedongwei@wistronits.com
     * description 转换成mongoQuery对象
     * date 14:08 2019/1/16
     * param [queryCondition, query]
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }

        // 无排序时的默认排序（当前按照创建时间降序）
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("optTime");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }


        //判断pageCondition对象是否为空
        if (null != queryCondition.getPageCondition()) {
            //添加分页条件到MonogoQuery条件
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //获取返回分页条件的编码值
            Integer pageConditionCode = LogResultCode.PAGE_CONDITION_NULL;
            //获取分页条件的提示信息
            String pageConditionMessage = I18nConstants.PAGE_CONDITION_NULL;
            //返回pageCondition对象不能为空
            result = ResultUtils.warn(pageConditionCode, I18nUtils.getSystemString(pageConditionMessage));
            queryResult.setQuery(query);
            queryResult.setResult(result);
            return queryResult;
        }
        query = MongoQueryHelper.buildQuery(query, queryCondition);
        queryResult.setQuery(query);
        queryResult.setResult(result);
        return queryResult;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 获取操作结果
     * date 10:56 2019/1/26
     * aram []
     */
    public String getOptResult() {
        String optResult = LogConstants.OPT_RESULT_SUCCESS;
        //获得操作结果
        String optResultName = LogI18nCast.getOptResult(optResult);
        return optResultName;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 获取危险级别
     * date 10:59 2019/1/26
     * param [functionCode]
     */
    public Integer getDangerLevelForFunctionCode(String functionCode) {
        Integer dangerLevelName = 0;
        if (!StringUtils.isEmpty(functionCode)) {
            //获取危险级别
            dangerLevelName = this.getDangerLevel(functionCode);
        }
        return dangerLevelName;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 获得操作类型
     * date 11:03 2019/1/26
     * param [optType]
     */
    public String getOptType(String optType) {
        String optTypeName = "";
        if (!StringUtils.isEmpty(optType)) {
            optTypeName = LogI18nCast.getOptType(optType);
        }
        return optTypeName;
    }

    /**
     * 导出数据超过最大限制返回信息
     *
     * @param fe 异常
     * @return 返回结果
     */
    public Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        log.error("export data too large error" , fe);
        String string = I18nUtils.getSystemString(I18nConstants.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(LogResultCode.EXPORT_DATA_TOO_LARGE, msg);
    }

    /**
     * 列表导出记录日志
     *
     * @param exportDto
     */
    public void addLogByExport(ExportDto exportDto, String functionCode) {
        systemLanguage.querySystemLanguage();
        String logType = com.fiberhome.filink.logapi.constant.LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(functionCode);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, com.fiberhome.filink.logapi.constant.LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 计算web操作日志总数
     *
     * @param queryCondition
     * @return
     */
    public Long countWebOperate(QueryCondition queryCondition) {
        Query query = setFilterCondition(queryCondition, OPT_TYPE, EQ_STRING, OPT_TYPE_WEB);
        //获取web操作日志个数
        long webOperateCount = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);
        return webOperateCount;
    }


    /**
     * 计算pda操作日志总数
     *
     * @param queryCondition
     * @return
     */
    public Long countPdaOperate(QueryCondition queryCondition) {
        Query query = setFilterCondition(queryCondition, OPT_TYPE, EQ_STRING, OPT_TYPE_PDA);
        //获取pda操作日志个数
        long pdaOperateCount = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);
        return pdaOperateCount;
    }


    /**
     * 生成查询条件
     *
     * @param queryCondition 查询条件
     * @param filterField    过滤字段
     * @param operateor      比较符
     * @param filterValue    过滤值
     * @return
     */
    public Query setFilterCondition(QueryCondition queryCondition, String filterField, String operateor, Object filterValue) {
        Query query = new Query();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setOperator(operateor);
        filterCondition.setFilterValue(filterValue);
        filterCondition.setFilterField(filterField);
        //前台请求的过滤条件
        List filterConditions = new ArrayList(queryCondition.getFilterConditions());
        queryCondition.getFilterConditions().add(filterCondition);
        MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        MongoQueryHelper.buildQuery(query, queryCondition);
        //生成查询统计的条件后，恢复成前台传过来的过滤条件
        queryCondition.setFilterConditions(filterConditions);
        return query;
    }


    /**
     * 计算安全级别日志总数
     *
     * @param queryCondition
     * @return 结果
     */
    public Map<Integer, Long> countDangerLeverLog(QueryCondition queryCondition) {
        Map<Integer, Long> map = new HashMap<>(64);
        //将危险级别各条件加到query对象
        Query query1 = setFilterCondition(queryCondition, DANGER_LEVEL, EQ_STRING, DANGER_LEVEL_PROMPT);
        Query query2 = setFilterCondition(queryCondition, DANGER_LEVEL, EQ_STRING, DANGER_LEVEL_GENERAL);
        Query query3 = setFilterCondition(queryCondition, DANGER_LEVEL, EQ_STRING, DANGER_LEVEL_DANGER);
        //获取操作日志中各危险级别的数量
        long count1ByQuery1 = mongoTemplate.count(query1, LogConstants.OPERATE_LOG_TABLE_NAME);
        long count1ByQuery2 = mongoTemplate.count(query2, LogConstants.OPERATE_LOG_TABLE_NAME);
        long count1ByQuery3 = mongoTemplate.count(query3, LogConstants.OPERATE_LOG_TABLE_NAME);
        //获取安全日志中各危险级别的数量
        long count2ByQuery1 = mongoTemplate.count(query1, LogConstants.SECURITY_LOG_TABLE_NAME);
        long count2ByQuery2 = mongoTemplate.count(query2, LogConstants.SECURITY_LOG_TABLE_NAME);
        long count2ByQuery3 = mongoTemplate.count(query3, LogConstants.SECURITY_LOG_TABLE_NAME);
        //获取系统日志中各危险级别的数量
        long count3ByQuery1 = mongoTemplate.count(query1, LogConstants.SYSTEM_LOG_TABLE_NAME);
        long count3ByQuery2 = mongoTemplate.count(query2, LogConstants.SYSTEM_LOG_TABLE_NAME);
        long count3ByQuery3 = mongoTemplate.count(query3, LogConstants.SYSTEM_LOG_TABLE_NAME);

        //提示，一般，危险级别总数
        long count1 = count1ByQuery1 + count2ByQuery1 + count3ByQuery1;
        long count2 = count1ByQuery2 + count2ByQuery2 + count3ByQuery2;
        long count3 = count1ByQuery3 + count2ByQuery3 + count3ByQuery3;

        map.put(DANGER_LEVEL_PROMPT, count1);
        map.put(DANGER_LEVEL_GENERAL, count2);
        map.put(DANGER_LEVEL_DANGER, count3);
        return map;
    }

    /**
     * 日志类型统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    @Override
    public Result exportLogType(ExportDto<LogTypeExportBean> exportDto) {
        //导出结果
        Result result = ExportStatisticUtil.exportProcessing(logTypeExport, exportDto,
                SERVER_NAME, I18nUtils.getSystemString(LOG_TYPE_STATISTIC_LIST_NAME));
        //记录日志
        if(result != null && result.getCode()==ResultCode.SUCCESS) {
            addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_LOG_TYPE_FUNCTION_CODE);
        }
        return result;
    }

    /**
     * 操作类型统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    @Override
    public Result exportOperateType(ExportDto<OperateTypeExportBean> exportDto) {
        //导出结果
        Result result = ExportStatisticUtil.exportProcessing(operateTypeExport, exportDto,
                SERVER_NAME, I18nUtils.getSystemString(OPERATE_TYPE_STATISTIC_LIST_NAME));
        //记录日志
        if(result != null && result.getCode()==ResultCode.SUCCESS) {
            addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_OPERATE_TYPE_FUNCTION_CODE);
        }
        return result;
    }

    /**
     * 安全级别统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    @Override
    public Result exportSecurityLevel(ExportDto<SecurityLevelExportBean> exportDto) {
        //导出结果
        Result result = ExportStatisticUtil.exportProcessing(securityLevelExport, exportDto,
                SERVER_NAME, I18nUtils.getSystemString(SECURITY_LEVEL_STATISTIC_LIST_NAME));
        //记录日志
        if(result != null && result.getCode()==ResultCode.SUCCESS) {
            addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_SECURITY_LEVEL_FUNCTION_CODE);
        }
        return result;
    }
}
