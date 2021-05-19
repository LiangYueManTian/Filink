package com.fiberhome.filink.log_server.service.impl;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.log_server.bean.*;
import com.fiberhome.filink.log_server.req.AddOperateLogReq;
import com.fiberhome.filink.log_server.req.AddSecurityLogReq;
import com.fiberhome.filink.log_server.req.AddSystemLogReq;
import com.fiberhome.filink.log_server.service.FunctionDangerLevelConfigService;
import com.fiberhome.filink.log_server.service.LogService;
import com.fiberhome.filink.log_server.utils.*;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import net.minidev.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * description 日志逻辑层
 * date 2019/1/14 14:44
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FunctionDangerLevelConfigService functionDangerLevelConfigService;

    /**
     * @author hedongwei@wistronits.com
     * description 新增操作日志信息
     * date 14:46 2019/1/14
     * param [log]
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOperateLog(AddOperateLogReq operateLogReq) {
        if (null != operateLogReq) {
            OperateLog operateLog = new OperateLog();
            //获取日志编号
            if (StringUtils.isEmpty(operateLogReq.getLogId())) {
                String logId = UUIDUtil.getInstance().UUID32();
                operateLogReq.setLogId(logId);
            }
            //获得危险级别信息
            String dangerLevelName = this.getDangerLevelForFunctionCode(operateLogReq.getFunctionCode());
            operateLogReq.setDangerLevel(dangerLevelName);
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
                String logId = UUIDUtil.getInstance().UUID32();
                securityLogReq.setLogId(logId);
            }
            //获得危险级别信息
            String dangerLevelName = this.getDangerLevelForFunctionCode(securityLogReq.getFunctionCode());
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
                String logId = UUIDUtil.getInstance().UUID32();
                systemLogReq.setLogId(logId);
            }
            //获得危险级别信息
            String dangerLevelName = this.getDangerLevelForFunctionCode(systemLogReq.getFunctionCode());
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
    private String getDangerLevel(String functionCode) {
        String dangerLevel = "";
        //获取日志编码信息
        if (!StringUtils.isEmpty(functionCode)) {
            FunctionDangerLevelConfig functionDangerLevelConfig = functionDangerLevelConfigService.getDangerLevelConfigByFunctionCode(functionCode);
            if (null != functionDangerLevelConfig) {
                dangerLevel = functionDangerLevelConfig.getDangerLevel();
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
     * @author hedongwei@wistronits.com
     * @date  2019/2/28 15:37
     * @param queryCondition 校验pageCondition参数信息
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
     * @author hedongwei@wistronits.com
     * description 转换成mongoQuery对象
     * date 14:08 2019/1/16
     * param [queryCondition, query]
     */
    private GetMongoQueryData castToMongoQuery(QueryCondition queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }

        // 无排序时的默认排序（当前按照创建时间降序）
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())){
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
            result = ResultUtils.warn(pageConditionCode ,  I18nUtils.getString(pageConditionMessage));
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
    private String getDangerLevelForFunctionCode(String functionCode) {
        String dangerLevelName = "";
        if (!StringUtils.isEmpty(functionCode)) {
            //获取危险级别
            String dangerLevel = this.getDangerLevel(functionCode);
            //获取危险名称
            dangerLevelName = LogI18nCast.getDangerLevel(dangerLevel);
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

}
