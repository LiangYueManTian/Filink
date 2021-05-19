package com.fiberhome.filink.securitystrategy.service;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.securitystrategy.bean.IpAddress;
import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-02-28
 */
public interface IpRangeService extends IService<IpRange> {

    /**
     * 查询IP地址是否在访问范围内
     * @param ipAddress IP地址
     * @return Result
     */
    Result hasIpAddress(IpAddress ipAddress);
    /**
     * 查询IP范围
     * @param queryCondition 查询封装
     * @return IP范围List
     */
    Result queryIpRanges(QueryCondition<IpRange> queryCondition);
    /**
     * 新增IP范围
     * @param ipRange IP段范围
     * @return 结果
     */
    Result addIpRange(IpRange ipRange);
    /**
     * 根据ID查询IP范围
     * @param ipRange ID
     * @return IP范围
     */
    Result queryIpRangeById(IpRange ipRange);
    /**
     * 修改IP范围
     * @param ipRange IP段范围
     * @return 结果
     */
    Result updateIpRange(IpRange ipRange);
    /**
     * 启用/禁用 IP范围
     * @param rangeIds  ID集合LIST
     * @param rangeStatus 启用/禁用
     * @return 结果
     */
    Result updateIpRangeStatus(List<String> rangeIds, String rangeStatus);
    /**
     * 全部启用/禁用
     * @param ipRange IP段范围
     * @return 结果
     */
    Result updateAllRangesStatus(IpRange ipRange);
    /**
     * 批量删除IP范围
     * @param rangeIds ID集合LIST
     * @return 结果
     */
    Result deleteRanges(List<String> rangeIds);
    /**
     * 查询Redis缓存
     * @return Redis缓存
     */
    Map<String, IpRange> setIpRangeRedis();
}
