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
 * description ???????????????
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
     * ???????????????????????????
     */
    @Autowired
    private OperateLogExport operateLogExport;
    /**
     * ???????????????????????????
     */
    @Autowired
    private SecurityLogExport securityLogExport;
    /**
     * ???????????????????????????
     */
    @Autowired
    private SysLogExport sysLogExport;
    /**
     * ?????????
     */
    private static String SERVER_NAME = "filink-log-server";
    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;
    /**
     * ????????????????????????
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ????????????
     */
    @Autowired
    private SystemLanguageUtil systemLanguage;

    /**
     * ???????????????????????????
     */
    @Autowired
    private LogTypeExport logTypeExport;

    /**
     * ???????????????????????????
     */
    @Autowired
    private OperateTypeExport operateTypeExport;

    /**
     * ???????????????????????????
     */
    @Autowired
    private SecurityLevelExport securityLevelExport;

    /**
     * @author hedongwei@wistronits.com
     * description ????????????????????????
     * date 14:46 2019/1/14
     * param [log]
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOperateLog(AddOperateLogReq operateLogReq) {
        if (null != operateLogReq) {
            OperateLog operateLog = new OperateLog();
            //??????????????????
            if (StringUtils.isEmpty(operateLogReq.getLogId())) {
                String logId = NineteenUUIDUtils.uuid();
                operateLogReq.setLogId(logId);
            }

            if (!StringUtils.isEmpty(operateLogReq.getFunctionCode())) {
                //????????????????????????
                Integer dangerLevelName = this.getDangerLevelForFunctionCode(operateLogReq.getFunctionCode());
                operateLogReq.setDangerLevel(dangerLevelName);
            }
            //??????????????????
            String optResultName = LogConstants.OPT_RESULT_SUCCESS;
            operateLogReq.setOptResult(optResultName);
            //??????????????????
            String optTypeName = operateLogReq.getOptType();
            operateLogReq.setOptType(optTypeName);
            //????????????????????????
            BeanUtils.copyProperties(operateLogReq, operateLog);
            mongoTemplate.save(operateLog);
        }
        return true;
    }

    /**
     * @author hedongwei@wistronits.com
     * description ????????????????????????
     * date 14:46 2019/1/14
     * param [log]
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSecurityLog(AddSecurityLogReq securityLogReq) {
        if (null != securityLogReq) {
            SecurityLog securityLog = new SecurityLog();
            //??????????????????
            if (StringUtils.isEmpty(securityLogReq.getLogId())) {
                String logId = NineteenUUIDUtils.uuid();
                securityLogReq.setLogId(logId);
            }
            //????????????????????????
            Integer dangerLevelName = this.getDangerLevelForFunctionCode(securityLogReq.getFunctionCode());
            securityLogReq.setDangerLevel(dangerLevelName);
            //??????????????????
            String optResultName = LogConstants.OPT_RESULT_SUCCESS;
            securityLogReq.setOptResult(optResultName);
            //??????????????????
            String optTypeName = securityLogReq.getOptType();
            securityLogReq.setOptType(optTypeName);
            BeanUtils.copyProperties(securityLogReq, securityLog);
            //????????????????????????
            mongoTemplate.save(securityLog);
        }
    }


    /**
     * @author hedongwei@wistronits.com
     * description ????????????????????????
     * date 14:46 2019/1/14
     * param [log]
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSystemLog(AddSystemLogReq systemLogReq) {
        if (null != systemLogReq) {
            //??????????????????(UUID)
            SystemLog systemLog = new SystemLog();
            if (StringUtils.isEmpty(systemLogReq.getLogId())) {
                String logId = NineteenUUIDUtils.uuid();
                systemLogReq.setLogId(logId);
            }

            //?????????????????????????????????
            systemLogReq.setOptUserCode("system");
            systemLogReq.setOptUserName("system");
            systemLogReq.setOptUserRole("system");
            systemLogReq.setOptUserRoleName("system");

            //????????????????????????
            Integer dangerLevelName = this.getDangerLevelForFunctionCode(systemLogReq.getFunctionCode());
            systemLogReq.setDangerLevel(dangerLevelName);
            //??????????????????
            String optResultName = LogConstants.OPT_RESULT_SUCCESS;
            systemLogReq.setOptResult(optResultName);
            //??????????????????
            String optTypeName = systemLogReq.getOptType();
            systemLogReq.setOptType(optTypeName);
            //????????????????????????
            BeanUtils.copyProperties(systemLogReq, systemLog);
            mongoTemplate.save(systemLog);
        }
    }

    /**
     * ????????????????????????
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
        operateLog.setDetailInfo("????????????");
        operateLog.setRemark("111");
        operateLog.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        operateLog.setCreateUser("system");

        for (int i = 0 ; i < 100000; i++) {
            OperateLog operateLogOne = new OperateLog();
            BeanUtils.copyProperties(operateLog, operateLogOne);
            operateLogOne.setLogId(NineteenUUIDUtils.uuid());
            operateLogOne.setOptObj("????????????????????????" + i);
            operateLogOne.setOptObjId(i + "");
            operateLogOne.setOptName("??????????????????" + i);
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
     * ????????????????????????
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
        systemLog.setDetailInfo("????????????");
        systemLog.setRemark("111");
        systemLog.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        systemLog.setCreateUser("system");

        for (int i = 0 ; i < 100000; i++) {
            SystemLog systemLogOne = new SystemLog();
            BeanUtils.copyProperties(systemLog, systemLogOne);
            systemLogOne.setLogId(NineteenUUIDUtils.uuid());
            systemLogOne.setOptObj("????????????????????????" + i);
            systemLogOne.setOptObjId(i + "");
            systemLogOne.setOptName("??????????????????" + i);
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
     * ????????????????????????
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
        securityLog.setDetailInfo("????????????");
        securityLog.setRemark("111");
        securityLog.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        securityLog.setCreateUser("system");

        for (int i = 0 ; i < 100000; i++) {
            SecurityLog securityLogOne = new SecurityLog();
            BeanUtils.copyProperties(securityLog, securityLogOne);
            securityLogOne.setLogId(NineteenUUIDUtils.uuid());
            securityLogOne.setOptObj("????????????????????????" + i);
            securityLogOne.setOptObjId(i + "");
            securityLogOne.setOptName("??????????????????" + i);
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
     * description ????????????????????????
     * date 16:16 2019/1/22
     * param [functionCode]
     */
    public Integer getDangerLevel(String functionCode) {
        Integer dangerLevel = 0;
        //????????????????????????
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
     * description ????????????????????????
     * date 10:42 2019/1/15
     * param [queryCondition]
     */
    @Override
    public Result queryListOperateLogByPage(QueryCondition queryCondition) {
        Query query = null;

        //????????????????????????
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //????????????????????????
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //????????????????????????query??????
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();

        //????????????????????????
        long count = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);

        //??????????????????????????????
        List<OperateLog> logList = mongoTemplate.find(query, OperateLog.class);

        //?????????????????????????????????????????????
        PageBean pageBean = PageBeanHelper.generatePageBean(logList, queryCondition, count);

        //??????????????????
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * @author hedongwei@wistronits.com
     * description ????????????????????????
     * date 10:43 2019/1/15
     * param [queryCondition]
     */
    @Override
    public Result queryListSecurityLogByPage(QueryCondition queryCondition) {
        Query query = null;
        //????????????????????????
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }


        //????????????????????????
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }


        //????????????????????????query??????
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();

        //????????????????????????
        long count = mongoTemplate.count(query, LogConstants.SECURITY_LOG_TABLE_NAME);

        //??????????????????????????????
        List<SecurityLog> logList = mongoTemplate.find(query, SecurityLog.class);

        //?????????????????????????????????????????????
        PageBean pageBean = PageBeanHelper.generatePageBean(logList, queryCondition, count);

        //??????????????????
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * @author hedongwei@wistronits.com
     * description ????????????????????????
     * date 10:43 2019/1/15
     * param [queryCondition]
     */
    @Override
    public Result queryListSystemLogByPage(QueryCondition queryCondition) {
        Query query = null;

        //????????????????????????
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //????????????????????????
        boolean resultPageCondition = this.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        //?????????query????????????
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();

        //????????????????????????
        long count = mongoTemplate.count(query, LogConstants.SYSTEM_LOG_TABLE_NAME);

        //??????????????????????????????
        List<SystemLog> logList = mongoTemplate.find(query, SystemLog.class);

        //?????????????????????????????????????????????
        PageBean pageBean = PageBeanHelper.generatePageBean(logList, queryCondition, count);

        //??????????????????
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * ???????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????
     */
    @Override
    public Result countByLogType(QueryCondition queryCondition) {
        Map<String, Long> resultMap = new HashMap<>(64);
        Query query = new Query();

        //????????????????????????
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }

        MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        MongoQueryHelper.buildQuery(query, queryCondition);
        //????????????????????????
        long systemCount = mongoTemplate.count(query, LogConstants.SYSTEM_LOG_TABLE_NAME);
        //????????????????????????
        long securityCount = mongoTemplate.count(query, LogConstants.SECURITY_LOG_TABLE_NAME);
        //????????????????????????
        long operateCount = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);

        resultMap.put(LogConstants.OPERATE_LOG_TABLE_NAME, operateCount);
        resultMap.put(LogConstants.SECURITY_LOG_TABLE_NAME, securityCount);
        resultMap.put(LogConstants.SYSTEM_LOG_TABLE_NAME, systemCount);
        return ResultUtils.success(resultMap);
    }

    /**
     * ???????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????
     */
    @Override
    public Result countByOperateType(QueryCondition queryCondition) {
        Map<String, Long> resultMap = new HashMap<>(64);
        //????????????????????????
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
     * ???????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????
     */
    @Override
    public Result countBySecurityType(QueryCondition queryCondition) {
        //????????????????????????
        if (null == queryCondition.getFilterConditions()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return ResultUtils.success(countDangerLeverLog(queryCondition));
    }

    /**
     * ??????pageCondition????????????
     *
     * @param queryCondition ??????pageCondition????????????
     * @author hedongwei@wistronits.com
     * @date 2019/2/28 15:37
     */
    public boolean checkPageCondition(QueryCondition queryCondition) {
        boolean checkResult = true;
        //????????????????????????
        if (null == queryCondition.getPageCondition()) {
            checkResult = false;
        } else {
            //pageNumber????????????
            if (StringUtils.isEmpty(queryCondition.getPageCondition().getPageNum())) {
                checkResult = false;
            }
            //pageSize????????????
            if (StringUtils.isEmpty(queryCondition.getPageCondition().getPageSize())) {
                checkResult = false;
            }
        }
        return checkResult;
    }

    /**
     * @author hedongwei@wistronits.com
     * description ??????????????????????????????????????????
     * date 15:48 2019/1/16
     * param [logId]
     */
    @Override
    public Result getOperateLogByLogId(String logId) {
        Query query = new Query();

        //???logId??????????????????
        Criteria criteria = Criteria.where("logId").is(logId);
        query.addCriteria(criteria);

        //????????????????????????
        OperateLog log = mongoTemplate.findOne(query, OperateLog.class);
        if (null == log) {
            JSONObject jsonObject = new JSONObject();
            return ResultUtils.success(jsonObject);
        }
        return ResultUtils.success(log);
    }

    /**
     * @author hedongwei@wistronits.com
     * description ??????????????????????????????????????????
     * date 15:48 2019/1/16
     * param [logId]
     */
    @Override
    public Result getSecurityLogByLogId(String logId) {
        Query query = new Query();

        //???logId??????????????????
        Criteria criteria = Criteria.where("logId").is(logId);
        query.addCriteria(criteria);

        //????????????????????????
        SecurityLog log = mongoTemplate.findOne(query, SecurityLog.class);
        if (null == log) {
            JSONObject jsonObject = new JSONObject();
            return ResultUtils.success(jsonObject);
        }
        return ResultUtils.success(log);
    }

    /**
     * @author hedongwei@wistronits.com
     * description ??????????????????????????????????????????
     * date 15:49 2019/1/16
     * param [logId]
     */
    @Override
    public Result getSystemLogByLogId(String logId) {
        Query query = new Query();

        //???logId??????????????????
        Criteria criteria = Criteria.where("logId").is(logId);
        query.addCriteria(criteria);

        //????????????????????????
        SystemLog log = mongoTemplate.findOne(query, SystemLog.class);
        if (null == log) {
            JSONObject jsonObject = new JSONObject();
            return ResultUtils.success(jsonObject);
        }
        return ResultUtils.success(log);
    }

    /**
     * ??????????????????????????????
     *
     * @param exportDto ??????????????????
     * @return ??????????????????
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
     * description ?????????mongoQuery??????
     * date 14:08 2019/1/16
     * param [queryCondition, query]
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //??????filterCondition??????????????????
        if (null != queryCondition.getFilterConditions()) {
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }

        // ???????????????????????????????????????????????????????????????
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("optTime");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }


        //??????pageCondition??????????????????
        if (null != queryCondition.getPageCondition()) {
            //?????????????????????MonogoQuery??????
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //????????????????????????????????????
            Integer pageConditionCode = LogResultCode.PAGE_CONDITION_NULL;
            //?????????????????????????????????
            String pageConditionMessage = I18nConstants.PAGE_CONDITION_NULL;
            //??????pageCondition??????????????????
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
     * description ??????????????????
     * date 10:56 2019/1/26
     * aram []
     */
    public String getOptResult() {
        String optResult = LogConstants.OPT_RESULT_SUCCESS;
        //??????????????????
        String optResultName = LogI18nCast.getOptResult(optResult);
        return optResultName;
    }

    /**
     * @author hedongwei@wistronits.com
     * description ??????????????????
     * date 10:59 2019/1/26
     * param [functionCode]
     */
    public Integer getDangerLevelForFunctionCode(String functionCode) {
        Integer dangerLevelName = 0;
        if (!StringUtils.isEmpty(functionCode)) {
            //??????????????????
            dangerLevelName = this.getDangerLevel(functionCode);
        }
        return dangerLevelName;
    }

    /**
     * @author hedongwei@wistronits.com
     * description ??????????????????
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
     * ??????????????????????????????????????????
     *
     * @param fe ??????
     * @return ????????????
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
     * ????????????????????????
     *
     * @param exportDto
     */
    public void addLogByExport(ExportDto exportDto, String functionCode) {
        systemLanguage.querySystemLanguage();
        String logType = com.fiberhome.filink.logapi.constant.LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //??????????????????id
        addLogBean.setOptObjId("export");
        //???????????????
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(functionCode);
        //??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, com.fiberhome.filink.logapi.constant.LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ??????web??????????????????
     *
     * @param queryCondition
     * @return
     */
    public Long countWebOperate(QueryCondition queryCondition) {
        Query query = setFilterCondition(queryCondition, OPT_TYPE, EQ_STRING, OPT_TYPE_WEB);
        //??????web??????????????????
        long webOperateCount = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);
        return webOperateCount;
    }


    /**
     * ??????pda??????????????????
     *
     * @param queryCondition
     * @return
     */
    public Long countPdaOperate(QueryCondition queryCondition) {
        Query query = setFilterCondition(queryCondition, OPT_TYPE, EQ_STRING, OPT_TYPE_PDA);
        //??????pda??????????????????
        long pdaOperateCount = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);
        return pdaOperateCount;
    }


    /**
     * ??????????????????
     *
     * @param queryCondition ????????????
     * @param filterField    ????????????
     * @param operateor      ?????????
     * @param filterValue    ?????????
     * @return
     */
    public Query setFilterCondition(QueryCondition queryCondition, String filterField, String operateor, Object filterValue) {
        Query query = new Query();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setOperator(operateor);
        filterCondition.setFilterValue(filterValue);
        filterCondition.setFilterField(filterField);
        //???????????????????????????
        List filterConditions = new ArrayList(queryCondition.getFilterConditions());
        queryCondition.getFilterConditions().add(filterCondition);
        MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        MongoQueryHelper.buildQuery(query, queryCondition);
        //????????????????????????????????????????????????????????????????????????
        queryCondition.setFilterConditions(filterConditions);
        return query;
    }


    /**
     * ??????????????????????????????
     *
     * @param queryCondition
     * @return ??????
     */
    public Map<Integer, Long> countDangerLeverLog(QueryCondition queryCondition) {
        Map<Integer, Long> map = new HashMap<>(64);
        //??????????????????????????????query??????
        Query query1 = setFilterCondition(queryCondition, DANGER_LEVEL, EQ_STRING, DANGER_LEVEL_PROMPT);
        Query query2 = setFilterCondition(queryCondition, DANGER_LEVEL, EQ_STRING, DANGER_LEVEL_GENERAL);
        Query query3 = setFilterCondition(queryCondition, DANGER_LEVEL, EQ_STRING, DANGER_LEVEL_DANGER);
        //?????????????????????????????????????????????
        long count1ByQuery1 = mongoTemplate.count(query1, LogConstants.OPERATE_LOG_TABLE_NAME);
        long count1ByQuery2 = mongoTemplate.count(query2, LogConstants.OPERATE_LOG_TABLE_NAME);
        long count1ByQuery3 = mongoTemplate.count(query3, LogConstants.OPERATE_LOG_TABLE_NAME);
        //?????????????????????????????????????????????
        long count2ByQuery1 = mongoTemplate.count(query1, LogConstants.SECURITY_LOG_TABLE_NAME);
        long count2ByQuery2 = mongoTemplate.count(query2, LogConstants.SECURITY_LOG_TABLE_NAME);
        long count2ByQuery3 = mongoTemplate.count(query3, LogConstants.SECURITY_LOG_TABLE_NAME);
        //?????????????????????????????????????????????
        long count3ByQuery1 = mongoTemplate.count(query1, LogConstants.SYSTEM_LOG_TABLE_NAME);
        long count3ByQuery2 = mongoTemplate.count(query2, LogConstants.SYSTEM_LOG_TABLE_NAME);
        long count3ByQuery3 = mongoTemplate.count(query3, LogConstants.SYSTEM_LOG_TABLE_NAME);

        //????????????????????????????????????
        long count1 = count1ByQuery1 + count2ByQuery1 + count3ByQuery1;
        long count2 = count1ByQuery2 + count2ByQuery2 + count3ByQuery2;
        long count3 = count1ByQuery3 + count2ByQuery3 + count3ByQuery3;

        map.put(DANGER_LEVEL_PROMPT, count1);
        map.put(DANGER_LEVEL_GENERAL, count2);
        map.put(DANGER_LEVEL_DANGER, count3);
        return map;
    }

    /**
     * ????????????????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result exportLogType(ExportDto<LogTypeExportBean> exportDto) {
        //????????????
        Result result = ExportStatisticUtil.exportProcessing(logTypeExport, exportDto,
                SERVER_NAME, I18nUtils.getSystemString(LOG_TYPE_STATISTIC_LIST_NAME));
        //????????????
        if(result != null && result.getCode()==ResultCode.SUCCESS) {
            addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_LOG_TYPE_FUNCTION_CODE);
        }
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result exportOperateType(ExportDto<OperateTypeExportBean> exportDto) {
        //????????????
        Result result = ExportStatisticUtil.exportProcessing(operateTypeExport, exportDto,
                SERVER_NAME, I18nUtils.getSystemString(OPERATE_TYPE_STATISTIC_LIST_NAME));
        //????????????
        if(result != null && result.getCode()==ResultCode.SUCCESS) {
            addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_OPERATE_TYPE_FUNCTION_CODE);
        }
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param exportDto ????????????
     * @return Result
     */
    @Override
    public Result exportSecurityLevel(ExportDto<SecurityLevelExportBean> exportDto) {
        //????????????
        Result result = ExportStatisticUtil.exportProcessing(securityLevelExport, exportDto,
                SERVER_NAME, I18nUtils.getSystemString(SECURITY_LEVEL_STATISTIC_LIST_NAME));
        //????????????
        if(result != null && result.getCode()==ResultCode.SUCCESS) {
            addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_SECURITY_LEVEL_FUNCTION_CODE);
        }
        return result;
    }
}
