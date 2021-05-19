package com.fiberhome.filink.logserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logserver.bean.FilterTemplate;
import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.logserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.logserver.dao.TemplateDao;
import com.fiberhome.filink.logserver.service.TemplateService;
import com.fiberhome.filink.logserver.utils.LogResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildQuery;

/**
 * <p>
 * 查询模板逻辑层实现类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/3
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    /**
     * 注入持久层
     */
    @Autowired
    private TemplateDao templateDao;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 默认排序字段
     */
    private final static String defaultSortFeild = "create_time";
    /**
     * 默认排序规则
     */
    private final static String defaultSortRule = "desc";


    /**
     * 插入模板
     *
     * @param filterTemplate 模板对象
     * @return 插入结果
     */
    @Override
    public Result insertTemplate(FilterTemplate filterTemplate) {
        //设置id
        filterTemplate.setId(NineteenUUIDUtils.uuid());
        //设置创建时间
        filterTemplate.setCreateTime(new Timestamp(FiLinkTimeUtils.getUtcZeroTimeStamp()));
        //设置创建人
        filterTemplate.setCreateUser(RequestInfoUtils.getUserId());
        templateDao.insertOne(filterTemplate);
        //记日志
        addLog(filterTemplate, LogFunctionCodeConstant.INSERT_TEMPLATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_ADD);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.INSERT_TEMPLATE));
    }

    /**
     * 更新模板
     *
     * @param filterTemplate 模板对象
     * @return 更新结果
     */
    @Override
    public Result updateTemplate(FilterTemplate filterTemplate) {
        FilterTemplate oldFilterTemplate = templateDao.queryOne(filterTemplate.getId());
        if(oldFilterTemplate == null){
            return ResultUtils.warn(LogResultCode.FILTER_TEMPLATE_MISS, I18nUtils.getSystemString(I18nConstants.FILTER_TEMPLATE_MISS));
        }
        templateDao.updateOne(filterTemplate);
        //记日志
        addLog(filterTemplate, LogFunctionCodeConstant.UPDATE_TEMPLATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.UPDATE_TEMPLATE));
    }

    /**
     * 删除模板
     *
     * @param filterTemplate 模板对象
     * @return 删除结果
     */
    @Override
    public Result deleteTemplate(FilterTemplate filterTemplate) {
        FilterTemplate oldFilterTemplate = templateDao.queryOne(filterTemplate.getId());
        if(oldFilterTemplate == null){
            return ResultUtils.warn(LogResultCode.FILTER_TEMPLATE_MISS, I18nUtils.getSystemString(I18nConstants.FILTER_TEMPLATE_MISS));
        }
        templateDao.deleteOne(filterTemplate);
        //记日志
        addLog(filterTemplate, LogFunctionCodeConstant.DELETE_TEMPLATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_DELETE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.DELETE_TEMPLATE));
    }

    /**
     * 查询模板
     *
     * @param id 模板id
     * @return 查询结果
     */
    @Override
    public Result queryTemplate(String id) {
        //根据id查询模板
        FilterTemplate filterTemplate = templateDao.queryOne(id);
        return ResultUtils.success(filterTemplate);
    }

    /**
     * 查询模板列表
     *
     * @param queryCondition 查询条件实体
     * @return 查询结果
     */
    @Override
    public Result queryList(QueryCondition queryCondition) {
        //若排序条件为空,默认时间排序
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (sortCondition == null || StringUtils.isEmpty(sortCondition.getSortField())
                || StringUtils.isEmpty(sortCondition.getSortRule())) {
            sortCondition = new SortCondition();
            sortCondition.setSortField(defaultSortFeild);
            sortCondition.setSortRule(defaultSortRule);
            queryCondition.setSortCondition(sortCondition);
        }

        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        // 构造过滤、排序等条件
        EntityWrapper wrapper = myBatiesBuildQuery(queryCondition);
        List list = templateDao.selectPage(page, wrapper);
        Integer count = templateDao.selectCount(wrapper);
        // 构造返回结果,返回
        return ResultUtils.pageSuccess(myBatiesBuildPageBean(page, count, list));
    }

    /**
     * 记录日志
     *
     * @param filterTemplate 模板
     * @param functionCode   XML functionCode
     * @param type           操作类型
     */
    private void addLog(FilterTemplate filterTemplate, String functionCode, String type) {
        //获取语言
        systemLanguageUtil.querySystemLanguage();
        //记录日志 数据id
        String dataId = "id";
        //记录日志 数据名
        String dataName = "name";
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(dataId);
        addLogBean.setDataName(dataName);
        addLogBean.setOptObjId(filterTemplate.getId());
        addLogBean.setOptObj(filterTemplate.getName());
        addLogBean.setFunctionCode(functionCode);
        addLogBean.setDataOptType(type);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
