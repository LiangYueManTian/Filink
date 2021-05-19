package com.fiberhome.filink.securitystrategy.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.securitystrategy.bean.ChangeIpRangesStatus;
import com.fiberhome.filink.securitystrategy.bean.IpAddress;
import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyResultCode;
import com.fiberhome.filink.securitystrategy.service.IpRangeService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  访问控制前端控制器
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-02-28
 */
@RestController
@RequestMapping("/ipRange")
public class IpRangeController {
    /**
     * 自动注入访问控制服务类
     */
    @Autowired
    private IpRangeService ipRangeService;

    /**
     * 查询IP地址是否在访问范围内
     * @param ipAddress IP地址
     * @return Result
     */
    @PostMapping("/hasIpAddress")
    public Result hasIpAddress(@RequestBody IpAddress ipAddress) {
        if (ObjectUtils.isEmpty(ipAddress) || !ipAddress.check()) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.hasIpAddress(ipAddress);
    }

    /**
     * 查询IP范围
     * @param queryCondition 查询封装
     * @return IP范围List
     */
    @PostMapping("/queryRanges")
    public Result queryIpRange(@RequestBody QueryCondition<IpRange> queryCondition) {
        if (ObjectUtils.isEmpty(queryCondition) || queryCondition.getFilterConditions() == null
                || queryCondition.getPageCondition() == null || queryCondition.getPageCondition().getPageNum() == null
                || queryCondition.getPageCondition().getPageSize() == null) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.queryIpRanges(queryCondition);
    }
    /**
     * 新增IP范围
     * @param ipRange IP段范围
     * @return 结果
     */
    @PostMapping("/addIpRange")
    public Result addIpRange(@RequestBody IpRange ipRange) {
        //参数校验
        if (ObjectUtils.isEmpty(ipRange) || ipRange.checkIpRange()) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.addIpRange(ipRange);
    }
    /**
     * 根据ID查询IP范围
     * @param ipRange ID
     * @return IP范围
     */
    @PostMapping("/queryIpRangeById")
    public Result queryIpRangeById(@RequestBody IpRange ipRange) {
        //参数校验
        if (ObjectUtils.isEmpty(ipRange) || ipRange.checkId()) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.queryIpRangeById(ipRange);
    }
    /**
     * 修改IP范围
     * @param ipRange IP段范围
     * @return 结果
     */
    @PutMapping("/updateIpRange")
    public Result updateIpRange(@RequestBody IpRange ipRange) {
        //参数校验
        if (ObjectUtils.isEmpty(ipRange) || ipRange.checkIpRange() || ipRange.checkId()) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.updateIpRange(ipRange);
    }
    /**
     * 启用/禁用 IP范围
     * @param changeIpRangesStatus IP段范围
     * @return 结果
     */
    @PutMapping("/updateRangeStatus")
    public Result updateIpRangeStatus(@RequestBody ChangeIpRangesStatus changeIpRangesStatus) {
        //参数校验
        if (ObjectUtils.isEmpty(changeIpRangesStatus.getRangeIds()) || StringUtils.isEmpty(changeIpRangesStatus.getRangeStatus()) || !changeIpRangesStatus.getRangeStatus().matches(SecurityStrategyConstants.ONE_ZERO_REGEX)) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.updateIpRangeStatus(changeIpRangesStatus.getRangeIds(), changeIpRangesStatus.getRangeStatus());
    }
    /**
     * 全部启用/禁用
     * @param ipRange IP段范围
     * @return 结果
     */
    @PutMapping("/updateAllRangesStatus")
    public Result updateAllRangesStatus(@RequestBody IpRange ipRange) {
        //参数校验
        if (ObjectUtils.isEmpty(ipRange) || ipRange.checkRangeStatus()) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.updateAllRangesStatus(ipRange);
    }
    /**
     * 批量删除IP范围
     * @param rangeIds ID集合LIST
     * @return 结果
     */
    @PostMapping("/deleteRanges")
    public Result deleteRanges(@RequestBody List<String> rangeIds) {
        //参数校验
        if (ObjectUtils.isEmpty(rangeIds)) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return ipRangeService.deleteRanges(rangeIds);
    }

}
