package com.fiberhome.filink.logserver.service.impl;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logserver.bean.*;
import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.logserver.constant.LogConstants;
import com.fiberhome.filink.logserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.logserver.logexport.OperateLogExport;
import com.fiberhome.filink.logserver.logexport.SecurityLogExport;
import com.fiberhome.filink.logserver.logexport.SysLogExport;
import com.fiberhome.filink.logserver.req.AddOperateLogReq;
import com.fiberhome.filink.logserver.req.AddSecurityLogReq;
import com.fiberhome.filink.logserver.req.AddSystemLogReq;
import com.fiberhome.filink.logserver.service.FunctionDangerLevelConfigService;
import com.fiberhome.filink.logserver.service.LogService;
import com.fiberhome.filink.logserver.utils.LogI18nCast;
import com.fiberhome.filink.logserver.utils.LogResultCode;
import com.fiberhome.filink.logserver.utils.PageBeanHelper;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
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
import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * description 日志逻辑层
 * date 2019/1/14 14:44
 */
@RefreshScope
@Service
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
            String optResultName = this.getOptResult();
            operateLogReq.setOptResult(optResultName);
            //获得操作类型
            String optTypeName = this.getOptType(operateLogReq.getOptType());
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
            String optResultName = this.getOptResult();
            securityLogReq.setOptResult(optResultName);
            //获得操作类型
            String optTypeName = this.getOptType(securityLogReq.getOptType());
            securityLogReq.setOptType(optTypeName);
            BeanUtils.copyProperties(securityLogReq, securityLog);
            //新增操作日志信息
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
            String optResultName = this.getOptResult();
            systemLogReq.setOptResult(optResultName);
            //获得操作类型
            String optTypeName = this.getOptType(systemLogReq.getOptType());
            systemLogReq.setOptType(optTypeName);
            //新增系统日志信息
            BeanUtils.copyProperties(systemLogReq, systemLog);
            mongoTemplate.save(systemLog);
        }
    }

    /**
     * @author hedongwei@wistronits.com
     * description 获得危险级别信息
     * date 16:16 2019/1/22
     * param [functionCode]
     */
    private Integer getDangerLevel(String functionCode) {
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
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getString(I18nConstants.PARAM_NULL));
        }

        //分页条件不能为空
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getString(I18nConstants.PARAM_NULL));
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
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getString(I18nConstants.PARAM_NULL));
        }


        //分页条件不能为空
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getString(I18nConstants.PARAM_NULL));
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
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getString(I18nConstants.PARAM_NULL));
        }

        //分页条件不能为空
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getString(I18nConstants.PARAM_NULL));
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
     * 校验pageCondition参数信息
     *
     * @param queryCondition 校验pageCondition参数信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/28 15:37
     */
    private boolean checkPageCondition(QueryCondition queryCondition) {
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
        Export export;
        try {
            export = operateLogExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getString(I18nConstants.OPERATE_LOG_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogResultCode.EXPORT_NO_DATA, I18nUtils.getString(I18nConstants.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getString(I18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(LogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getString(I18nConstants.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto,LogFunctionCodeConstant.EXPORT_OPERATE_FUNCTION_CODE);
        operateLogExport.exportData(export);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(I18nConstants.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    @Override
    public Result exportSysLog(ExportDto exportDto) {
        Export export;
        try {
            export = sysLogExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getString(I18nConstants.SYSTEM_LOG_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogResultCode.EXPORT_NO_DATA, I18nUtils.getString(I18nConstants.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getString(I18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(LogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getString(I18nConstants.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto,LogFunctionCodeConstant.EXPORT_SYSLOG_FUNCTION_CODE);
        sysLogExport.exportData(export);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(I18nConstants.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    @Override
    public Result exportSecurityLog(ExportDto exportDto) {
        Export export;
        try {
            export = securityLogExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getString(I18nConstants.SECURITY_LOG_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogResultCode.EXPORT_NO_DATA, I18nUtils.getString(I18nConstants.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getString(I18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(LogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getString(I18nConstants.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto,LogFunctionCodeConstant.EXPORT_SECURITY_LOG_FUNCTION_CODE);
        securityLogExport.exportData(export);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(I18nConstants.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
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
            result = ResultUtils.warn(pageConditionCode, I18nUtils.getString(pageConditionMessage));
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
    private String getOptResult() {
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
    private Integer getDangerLevelForFunctionCode(String functionCode) {
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
    private String getOptType(String optType) {
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
    private Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        fe.printStackTrace();
        String string = I18nUtils.getString(I18nConstants.EXPORT_DATA_TOO_LARGE);
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
    private void addLogByExport(ExportDto exportDto,String functionCode) {
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
}
