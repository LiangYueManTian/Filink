package com.fiberhome.filink.userserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.TempAuthParameter;
import com.fiberhome.filink.userserver.bean.Tempauth;

import java.util.List;

/**
 * <p>
 * 临时授权表 服务类
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
public interface TempauthService extends IService<Tempauth> {

    /**
     * 条件查询临时授权信息
     *
     * @param queryCondition 临时授权参数实体类
     * @return 临时授权列表
     */
    Result queryTempAuthByCondition(QueryCondition<TempAuthParameter> queryCondition);

    /**
     * 单个审核临时授权信息
     *
     * @param tempauth 临时授权信息
     * @return 审核结果
     */
    Result audingTempAuthById(Tempauth tempauth);

    /**
     * 批量审核临时授权信息
     *
     * @param idList   待审核临时授权id
     * @param tempAuth 临时授权信息
     * @param userIdList 推送消息的用户
     * @return 审核结果
     */
    Result batchAudingTempAuthByIds(String[] idList, Tempauth tempAuth, List<String> userIdList);

    /**
     * 删除单个临时授权信息
     *
     * @param id 临时授权id
     * @return 删除的结果
     */
    Result deleteTempAuthById(String id);

    /**
     * 批量删除零食授权信息
     *
     * @param ids 临时授权id数组
     * @return 删除的结果
     */
    Result batchDeleteTempAuth(String[] ids);

    /**
     * 添加临时授权信息
     *
     * @param tempauth 临时授权信息
     * @return 临时授权的结果
     */
    Result addTempAuth(Tempauth tempauth);

    /**
     * 根据id查询临时授权信息
     * @param id    临时授权id
     * @return  临时授权信息
     */
    Result queryTempAuthById(String id);
}
