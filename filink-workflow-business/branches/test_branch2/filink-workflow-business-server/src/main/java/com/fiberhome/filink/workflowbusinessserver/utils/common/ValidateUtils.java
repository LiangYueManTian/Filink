package com.fiberhome.filink.workflowbusinessserver.utils.common;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 验证工具类
 * @author hedongwei@wistronits.com
 * @date 2019/2/26 14:46
 */

public class ValidateUtils {

    /**
     * 验证数据长度
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 14:51
     * @param validateData 校验数据
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return boolean 校验结果
     */
    public static boolean validateDataLengthResult(String validateData, Integer minLength, Integer maxLength) {
        if (null != validateData && null != minLength && null != maxLength) {
            int dataLength =  validateData.length();
            if (dataLength >= minLength && dataLength <= maxLength) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 校验参数是否为空
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 15:38
     * @param validateData 校验的数据
     * @return Result 返回提示信息
     */
    public static boolean validateDataInfoIsEmpty(Object validateData) {
        if (ObjectUtils.isEmpty(validateData)) {
            return false;
        }
        return true;
    }

    /**
     * 校验参数长度是否满足需求
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 15:38
     * @param validateData 校验的数据
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return Result 返回提示信息
     */
    public static boolean validateDataLength(String validateData, Integer minLength,
                                            Integer maxLength) {
        if (!ValidateUtils.validateDataLengthResult(validateData, minLength, maxLength)) {
            return false;
        }
        return true;
    }


    /**
     * 校验pageCondition参数信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/28 15:37
     * @param queryCondition 校验pageCondition参数信息
     */
    public static boolean checkPageCondition(QueryCondition queryCondition) {
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
     * 查询条件参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 17:00
     * @param queryCondition 查询条件信息
     */
    public static Result checkQueryConditionParam(QueryCondition queryCondition) {

        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //筛选对象不能为空
        if (null == queryCondition.getFilterConditions()) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //分页条件不能为空
        boolean resultPageCondition = ValidateUtils.checkPageCondition(queryCondition);
        if (!resultPageCondition) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }
        return null;
    }


    /**
     * 查询条件过滤
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 14:55
     * @param queryCondition 查询条件过滤
     * @return 返回查询条件过滤之后的查询条件
     */
    public static QueryCondition filterQueryCondition(QueryCondition queryCondition){
        String sortField = "createTime";
        String sortRule = "desc";
        queryCondition = ValidateUtils.getQueryCondition(queryCondition, sortField, sortRule);
        return queryCondition;
    }


    /**
     * 查询条件过滤
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 14:55
     * @param queryCondition 查询条件过滤
     * @return 返回查询条件过滤之后的查询条件
     */
    public static QueryCondition filterQueryCondition(QueryCondition queryCondition, String sortField, String sortRule){
        // 无排序时的默认排序（当前按照创建时间降序）
        queryCondition = ValidateUtils.getQueryCondition(queryCondition, sortField, sortRule);
        return queryCondition;
    }


    /**
     * 获取查询条件
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 15:30
     * @param queryCondition 查询条件
     * @param sortField 排序字段
     * @param sortRule 排序规则
     * @return 获取查询条件
     */
    public static QueryCondition getQueryCondition(QueryCondition queryCondition, String sortField, String sortRule) {
        // 无排序时的默认排序（当前按照创建时间降序）
        if (null == queryCondition.getSortCondition()){
            queryCondition = ValidateUtils.setDefaultCondition(queryCondition, sortField, sortRule);
        } else {
            if (StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
                queryCondition = ValidateUtils.setDefaultCondition(queryCondition, sortField, sortRule);
            }
        }
        return queryCondition;
    }

    /**
     * 设置默认的排序条件
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 15:30
     * @param queryCondition 查询条件
     * @param sortField 排序字段
     * @param sortRule 排序规则
     * @return 获取默认的排序条件
     */
    public static QueryCondition setDefaultCondition(QueryCondition queryCondition, String sortField, String sortRule) {
        SortCondition sortCondition = new SortCondition();
        sortCondition.setSortField(sortField);
        sortCondition.setSortRule(sortRule);
        queryCondition.setSortCondition(sortCondition);
        return queryCondition;
    }

}
