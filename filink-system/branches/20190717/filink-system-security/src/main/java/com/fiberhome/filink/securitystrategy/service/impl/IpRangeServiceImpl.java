package com.fiberhome.filink.securitystrategy.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.securitystrategy.bean.IpAddress;
import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyResultCode;
import com.fiberhome.filink.securitystrategy.dao.IpRangeDao;
import com.fiberhome.filink.securitystrategy.exception.*;
import com.fiberhome.filink.securitystrategy.service.IpRangeService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  访问控制服务实现类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-02-28
 */
@Service
public class IpRangeServiceImpl extends ServiceImpl<IpRangeDao, IpRange> implements IpRangeService {
    /**
     * 自动注入DAO
     */
    @Autowired
    private IpRangeDao ipRangeDao;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * 系统语言
     */
    @Autowired
    private SystemLanguageUtil languageUtil;
    /**
     * 查询IP地址是否在访问范围内
     *
     * @param ipAddress IP地址
     * @return Result
     */
    @Override
    public Result hasIpAddress(IpAddress ipAddress) {
        Map<String, IpRange> ipRangeMap = setIpRangeRedis();
        if (ObjectUtils.isEmpty(ipRangeMap)) {
            return ResultUtils.success();
        }
        String checkIp = ipAddress.getIpAddress();
        if (checkIp.matches(SecurityStrategyConstants.IPV4_REGEX) && joinIpv4Ranges(ipRangeMap, checkIp)) {
            return ResultUtils.success();
        }
        if(checkIp.matches(SecurityStrategyConstants.IPV6_REGEX_FULL) && joinIpv6Ranges(ipRangeMap, checkIp)) {
            return ResultUtils.success();
        }
        return ResultUtils.warn(SecurityStrategyResultCode.FAIL);
    }

    /**
     * 校验IPV4是否有权限
     * @param ipRangeMap ID范围
     * @param checkIp IP
     * @return 是否有权限
     */
    private boolean joinIpv4Ranges(Map<String, IpRange> ipRangeMap, String checkIp) {
        for (IpRange ipRange : ipRangeMap.values()) {
            if (ipRange.getStartIp().matches(SecurityStrategyConstants.IPV4_ZERO_REGEX)) {
                return true;
            }
            if (SecurityStrategyConstants.IPV4.equals(ipRange.getIpType()) && inIpv4Range(checkIp, ipRange)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验IPV6是否有权限
     * @param ipRangeMap ID范围
     * @param checkIp IP
     * @return 是否有权限
     */
    private boolean joinIpv6Ranges(Map<String, IpRange> ipRangeMap, String checkIp) {
        String ipv6 = buildIpv6(checkIp);
        for (IpRange ipRange : ipRangeMap.values()) {
            if (ipRange.getStartIp().matches(SecurityStrategyConstants.IPV6_ZERO_REGEX)) {
                return true;
            }
            if (SecurityStrategyConstants.IPV6.equals(ipRange.getIpType()) && inIpv6Range(ipv6, ipRange)) {
                return true;
            }
        }
        return false;
    }



    /**
     * 查询IP范围
     * @param queryCondition 查询封装
     * @return IP范围List
     */
    @Override
    public Result queryIpRanges(QueryCondition<IpRange> queryCondition) {
        //封装逻辑删除查询
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("is_deleted");
        filterCondition.setOperator("neq");
        filterCondition.setFilterValue(1);
        queryCondition.getFilterConditions().add(filterCondition);
        // 无排序时的默认排序（当前按照创建时间降序）
        if (queryCondition.getSortCondition() == null || org.springframework.util.StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortRule("desc");
            sortCondition.setSortField("create_time");
            queryCondition.setSortCondition(sortCondition);
        }
        // 构造分页条件
        Page page = MpQueryHelper.myBatiesBuildPage(queryCondition);
        // 构造过滤、排序等条件
        EntityWrapper wrapper = MpQueryHelper.myBatiesBuildQuery(queryCondition);
        List<IpRange> ipRanges = ipRangeDao.selectPage(page, wrapper);
        Integer count = ipRangeDao.selectCount(wrapper);
        //判断是否有数据
        if (ObjectUtils.isEmpty(ipRanges)) {
            //没有数据替换null
            ipRanges = new ArrayList<>();
        }
        PageBean pageBean = MpQueryHelper.myBatiesBuildPageBean(page, count, ipRanges);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 新增IP范围
     *
     *
     * @param ipRange IP段范围
     * @return 结果
     */
    @Override
    public Result addIpRange(IpRange ipRange) {
        //校验IP范围是否可以新增
        checkIpRangeDb(ipRange);
        //获取用户ID
        ipRange.setRangeId(NineteenUUIDUtils.uuid());
        ipRange.setCreateUser(RequestInfoUtils.getUserId());
        //新增IP范围
        int result = ipRangeDao.addIpRange(ipRange);
        //判断结果
        if (result != 1) {
            throw new FilinkSecurityStrategyDatabaseException();
        }
        //记录日志
        addLogByRange(ipRange, SecurityStrategyConstants.ADD_IP_RANGE_CODE, LogConstants.DATA_OPT_TYPE_ADD);
        return ResultUtils.success(SecurityStrategyResultCode.SUCCESS, I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_ADD_SUCCESS));
    }

    /**
     * 根据ID查询IP范围
     *
     * @param ipRange ID
     * @return IP范围
     */
    @Override
    public Result queryIpRangeById(IpRange ipRange) {
        //根据ID查询IP范围
        IpRange ipRangeDb = hasIpRange(ipRange.getRangeId());
        return ResultUtils.success(ipRangeDb);
    }

    /**
     * 修改IP范围
     *
     * @param ipRange IP段范围
     * @return 结果
     */
    @Override
    public Result updateIpRange(IpRange ipRange) {
        //判断是否已被删除
        hasIpRange(ipRange.getRangeId());
        //校验IP范围是否可以新增
        checkIpRangeDb(ipRange);
        //获取用户ID
        ipRange.setUpdateUser(RequestInfoUtils.getUserId());
        //修改IP范围
        int result = ipRangeDao.updateById(ipRange);
        //判断结果
        if (result != 1) {
            throw  new FilinkSecurityStrategyDatabaseException();
        }
        //存入缓存
        if (RedisUtils.hHasKey(SecurityStrategyConstants.IP_RANGE_REDIS, ipRange.getRangeId())) {
            RedisUtils.hSet(SecurityStrategyConstants.IP_RANGE_REDIS, ipRange.getRangeId(), ipRange);
        }
        //记录日志
        addLogByRange(ipRange, SecurityStrategyConstants.UPDATE_IP_RANGE_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        return ResultUtils.success(SecurityStrategyResultCode.SUCCESS, I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_UPDATE_SUCCESS));
    }

    /**
     * 启用/禁用 IP范围
     *
     * @param rangeIds ID集合LIST
     * @param rangeStatus 启用/禁用
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateIpRangeStatus(List<String> rangeIds, String rangeStatus) {
        //判断是否已被删除,根据ID查询IP段范围
        List<IpRange> ipRanges = hasIpRanges(rangeIds);
        //获取用户ID
        String updateUser = RequestInfoUtils.getUserId();
        //修改启用/禁用
        int result = ipRangeDao.updateRangesStatus(rangeIds, rangeStatus, updateUser);
        //判断结果
        if (result != rangeIds.size()) {
            throw  new FilinkSecurityStrategyDatabaseException();
        }
        //记录日志 修改缓存
        if (SecurityStrategyConstants.ONE_TYPE.equals(rangeStatus)) {
            updateRangesRedis(ipRanges);
            addLogList(ipRanges, SecurityStrategyConstants.ENABLE_IP_RANGE_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        } else {
            removeRangesRedis(rangeIds);
            addLogList(ipRanges, SecurityStrategyConstants.DISABLE_IP_RANGE_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        }
        return ResultUtils.success(SecurityStrategyResultCode.SUCCESS, I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_ENABLE_DISABLE_SUCCESS));
    }

    /**
     * 全部启用/禁用
     *
     * @param ipRange IP段范围
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAllRangesStatus(IpRange ipRange) {
        //获取用户ID
        ipRange.setUpdateUser(RequestInfoUtils.getUserId());
        //修改启用/禁用
        ipRangeDao.updateAllRangesStatus(ipRange);
        //删除缓存
        RedisUtils.remove(SecurityStrategyConstants.IP_RANGE_REDIS);
        //记录日志
        if (SecurityStrategyConstants.ONE_TYPE.equals(ipRange.getRangeStatus())) {
            //存入缓存
            setIpRangeRedis();
            addLog(SecurityStrategyConstants.RANGE_ID, languageUtil.getI18nString(SecurityStrategyI18n.IP_RANGE_ALL),  SecurityStrategyConstants.ENABLE_IP_RANGES_ALL_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        } else {
            addLog(SecurityStrategyConstants.RANGE_ID, languageUtil.getI18nString(SecurityStrategyI18n.IP_RANGE_ALL), SecurityStrategyConstants.DISABLE_IP_RANGES_AL_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        }
        return ResultUtils.success(SecurityStrategyResultCode.SUCCESS, I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_ENABLE_DISABLE_SUCCESS));
    }

    /**
     * 批量删除IP范围
     * @param rangeIds ID集合LIST
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteRanges(List<String> rangeIds) {
        //判断是否已被删除,根据ID查询IP段范围
        List<IpRange> ipRanges = hasIpRanges(rangeIds);
        //批量删除IP范围
        deleteOrRecoverRanges(rangeIds, SecurityStrategyConstants.ONE_TYPE);
        //删除缓存
        removeRangesRedis(rangeIds);
        //记录日志
        addLogList(ipRanges, SecurityStrategyConstants.DELETE_IP_RANGE_CODE, LogConstants.DATA_OPT_TYPE_DELETE);
        return ResultUtils.success(SecurityStrategyResultCode.SUCCESS, I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_DELETE_SUCCESS));
    }
    /**
     * 批量删除缓存
     * @param rangeIds ID集合LIST
     */
    private void removeRangesRedis(List<String> rangeIds) {
        //删除缓存
        if (RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS)) {
            String[] rangeIdArray = new String[rangeIds.size()];
            rangeIds.toArray(rangeIdArray);
            //存入
            RedisUtils.hRemove(SecurityStrategyConstants.IP_RANGE_REDIS, rangeIdArray);
        }
    }
    /**
     * 批量存入缓存
     * @param ipRanges 集合LIST
     */
    private void updateRangesRedis(List<IpRange> ipRanges) {
        //删除缓存
        if (RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS)) {
            Map<String, IpRange> ipRangeMap = (Map)RedisUtils.hGetMap(SecurityStrategyConstants.IP_RANGE_REDIS);
            for (IpRange ipRange : ipRanges) {
                ipRange.setRangeStatus(SecurityStrategyConstants.ONE_TYPE);
                ipRangeMap.put(ipRange.getRangeId(), ipRange);
            }
            //存入
            RedisUtils.hSetMap(SecurityStrategyConstants.IP_RANGE_REDIS, (Map)ipRangeMap);
        }
    }


    /**
     * 批量删除或回复IP范围
     * @param rangeIds rangeIds ID集合LIST
     * @param isDeleted 删除或恢复
     */
    private void deleteOrRecoverRanges(List<String> rangeIds, String isDeleted) {
        //获取用户ID
        String updateUser = RequestInfoUtils.getUserId();
        //批量删除或回复IP范围
        int result = ipRangeDao.deleteRangesByIds(rangeIds, isDeleted, updateUser);
        //判断结果
        if (result != rangeIds.size()) {
            throw new FilinkSecurityStrategyDatabaseException();
        }
    }

    /**
     * 查询Redis缓存
     * @return Redis缓存
     */
    @Override
    public Map<String, IpRange> setIpRangeRedis() {
        Map<String, IpRange> ipRangeMap = new HashMap<>(16);
        //查询Redis缓存
        if (RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS)) {
            return (Map)RedisUtils.hGetMap(SecurityStrategyConstants.IP_RANGE_REDIS);
        }
        //查询数据库
        List<IpRange> ipRanges = ipRangeDao.queryIpRangeAll();
        //数据库没有数据
        if (ObjectUtils.isEmpty(ipRanges)) {
            return ipRangeMap;
        }
        //转MAP
        for (IpRange ipRange : ipRanges) {
            ipRangeMap.put(ipRange.getRangeId(), ipRange);
        }
        //存入
        RedisUtils.hSetMap(SecurityStrategyConstants.IP_RANGE_REDIS, (Map)ipRangeMap);
        return ipRangeMap;
    }

    /**
     * 校验IP范围是否可以新增修改
     * @param ipRange IP范围
     */
    private void checkIpRangeDb(IpRange ipRange) {
        if (SecurityStrategyConstants.IPV4.equals(ipRange.getIpType())) {
            //校验ip和掩码是否符合规范
            checkInputIpv4Range(ipRange);
            //校验全零段
            if (checkRangeZero(ipRange, SecurityStrategyConstants.IPV4_ZERO_REGEX)) {
                //校验ip和掩码是否可以组成网络段
                checkIpv4Range(ipRange);
            }
            //查询出所有ipv4范围
            List<IpRange> ipRanges = ipRangeDao.queryRangesByType(ipRange);
            //校验输入IP范围是否与已有范围重复
            checkIpv4RangeOverlap(ipRange, ipRanges);
            //转换IP格式
            changeIpv4RangeFormat(ipRange);
        } else if (SecurityStrategyConstants.IPV6.equals(ipRange.getIpType())) {
            //校验ip和掩码是否符合规范
            checkInputIpv6Range(ipRange);
            //转换IP格式
            changeIpv6RangeFormat(ipRange);
            //校验全零段
            if (checkRangeZero(ipRange, SecurityStrategyConstants.IPV6_ZERO_REGEX)) {
                //校验ip和掩码是否可以组成网络段
                checkIpv6Range(ipRange);
            }
            //查询出所有ipv6范围
            List<IpRange> ipRanges = ipRangeDao.queryRangesByType(ipRange);
            //校验输入IP范围是否与已有范围重复
            checkIpv6RangeOverlap(ipRange, ipRanges);
        }
    }

    /**
     * 判断IP段范围是否已被删除
     * @param rangeId Id
     * @return IP段范围
     */
    private IpRange hasIpRange(String rangeId) {
        //根据ID查询IP段范围
        IpRange ipRange = ipRangeDao.queryIpRangeById(rangeId);
        //判断IP段范围是否已被删除
        if (ObjectUtils.isEmpty(ipRange) || ipRange.checkId()) {
            throw new FilinkIpRangeNotExistException();
        }
        return ipRange;
    }

    /**
     * 判断IP段范围是否已被删除
     * @param rangeIds Id list
     * @return IP段范围 list
     */
    private List<IpRange> hasIpRanges(List<String> rangeIds) {
        //判断是否已被删除,根据ID查询IP段范围
        List<IpRange> ipRanges = ipRangeDao.queryRangesById(rangeIds);
        //判断是否有不存在的
        if (ipRanges == null || ipRanges.size() != rangeIds.size()) {
            throw new FilinkIpRangeNotExistException();
        }
        return ipRanges;
    }

    /**
     * 转换IP范围IPV6格式,转为小写不足补0
     * @param ipRange IP范围
     */
    private void changeIpv6RangeFormat(IpRange ipRange) {
        //转换起始IP
        ipRange.setStartIp(changeIpv6Format(ipRange.getStartIp()));
        //转换结束IP
        ipRange.setEndIp(changeIpv6Format(ipRange.getEndIp()));
    }

    /**
     * 转换IP范围IPV4格式,去除前部分0
     * @param ipRange IP范围
     */
    private void changeIpv4RangeFormat(IpRange ipRange) {
        //转换起始IP
        ipRange.setStartIp(changeIpv4Format(ipRange.getStartIp()));
        //转换结束IP
        ipRange.setEndIp(changeIpv4Format(ipRange.getEndIp()));
        //转换子网掩码
        ipRange.setMask(changeIpv4Format(ipRange.getMask()));
    }

    /**
     * 转换IPV6格式,转为小写,不足补0
     * @param ipv6 IPV6
     * @return IPV6
     */
    private String changeIpv6Format(String ipv6) {
        //全部转为小写
        ipv6 = ipv6.toLowerCase();
        //按“:”分割
        String[] ipArray = ipv6.split(SecurityStrategyConstants.IP_COLON);
        return changeIpv6Array(ipArray, ipArray.length);
    }

    /**
     * 转换IPV6格式,转为小写,不足补0
     * @param ipArray IPV6数组
     * @param changeLength 改变长度
     * @return IPV6部分
     */
    private String changeIpv6Array(String[] ipArray, int changeLength) {
        StringBuilder ipBuilder = new StringBuilder();
        //将每段补全4位，不足前面补0，并用“:”连接成新IPV6
        for (int i = 0; i < changeLength; i++) {
            for (int j = 0; j < SecurityStrategyConstants.IPV6_NUM - ipArray[i].length(); j++) {
                ipBuilder.append(0);
            }
            ipBuilder.append(ipArray[i]);
            //除最后一位，添加“:”连接
            if (i < ipArray.length - 1) {
                ipBuilder.append(SecurityStrategyConstants.IP_COLON);
            }
        }
        return ipBuilder.toString();
    }

    /**
     * 转换IPV4格式,去除前部分0
     * @param ipv4 IPV4
     * @return IPV4
     */
    private String changeIpv4Format(String ipv4) {
        //按“.”分割
        String[] ipArray = ipv4.split("\\.");
        StringBuilder ipBuilder = new StringBuilder();
        //每段转换成int，去除前面是0情况，用“.”连接成新IPV4
        for (int i = 0; i < ipArray.length; i++) {
            ipBuilder.append(Integer.parseInt(ipArray[i]));
            //除最后一位，添加“.”连接
            if (i < ipArray.length - 1) {
                ipBuilder.append(".");
            }
        }
        return ipBuilder.toString();
    }

    /**
     * 校验IPV6范围是否与已有范围重复
     * @param ipRange IP范围
     * @param ipRanges 已有范围List
     */
    private void checkIpv6RangeOverlap(IpRange ipRange, List<IpRange> ipRanges) {
        if (CollectionUtils.isEmpty(ipRanges)) {
            return;
        }
        for (IpRange ipDb : ipRanges) {
            //判断两个IPV6范围是否重叠
            checkIpv6RangeOverlap(ipRange, ipDb);
        }
    }

    /**
     * 判断两个IPV6范围是否重叠
     * @param ipRange 输入IPV6范围
     * @param ipRangeDb 数据库IPV6范围
     */
    private void checkIpv6RangeOverlap(IpRange ipRange, IpRange ipRangeDb) {
        //判断输入IP是否在已有范围内
        if (inIpv6Range(ipRange.getStartIp(), ipRangeDb) || inIpv6Range(ipRange.getEndIp(), ipRangeDb)) {
            throw new FilinkIpRangeOverlapException(getMsg(SecurityStrategyI18n.IP_RANGE_OVERLAP_ERROR, ipRangeDb));
        }
        //判断输入IP是否包含已有范围
        if (inIpv6Range(ipRangeDb.getStartIp(), ipRange)) {
            throw new FilinkIpRangeContainException(getMsg(SecurityStrategyI18n.IP_RANGE_CONTAIN_ERROR, ipRangeDb));
        }
    }

    /**
     * 校验IPV4范围是否与已有范围重复
     * @param ipRange IP范围
     * @param ipRanges 已有范围List
     */
    private void checkIpv4RangeOverlap(IpRange ipRange, List<IpRange> ipRanges) {
        //数据库IP范围为空返回
        if (CollectionUtils.isEmpty(ipRanges)) {
            return;
        }
        for (IpRange ipDb : ipRanges) {
            //判断两个IPV4范围是否重叠
            checkIpv4RangeOverlap(ipRange, ipDb);
        }
    }

    /**
     * 判断两个IPV4范围是否重叠
     * @param ipRange 输入IPV4范围
     * @param ipRangeDb 数据库IPV4范围
     */
    private void checkIpv4RangeOverlap(IpRange ipRange, IpRange ipRangeDb) {
        //判断输入IP是否在已有范围内
        if (inIpv4Range(ipRange.getStartIp(), ipRangeDb) || inIpv4Range(ipRange.getEndIp(), ipRangeDb)) {
            throw new FilinkIpRangeOverlapException(getMsg(SecurityStrategyI18n.IP_RANGE_OVERLAP_ERROR, ipRangeDb));
        }
        //判断输入IP是否包含已有范围
        if (inIpv4Range(ipRangeDb.getStartIp(), ipRange)) {
            throw new FilinkIpRangeContainException(getMsg(SecurityStrategyI18n.IP_RANGE_CONTAIN_ERROR, ipRangeDb));
        }
    }

    /**
     * 获取国际化信息，并替换数据字段
     * @param i18nStr I18n国际化信息表示
     * @param ipRange IP范围数据
     * @return 前端需要信息
     */
    private String getMsg(String i18nStr, IpRange ipRange) {
        //获取国际化信息
        String msg = I18nUtils.getSystemString(i18nStr);
        //替换起始IP
        msg = msg.replace(SecurityStrategyConstants.START_IP, ipRange.getStartIp());
        //替换结束IP
        msg = msg.replace(SecurityStrategyConstants.END_IP, ipRange.getEndIp());
        return msg;
    }

    /**
     * 校验能否组成一个IPV6范围段
     * @param ipRange IPV6范围段
     */
    private void checkIpv6Range(IpRange ipRange) {
        //判断结束IP是否大于等于结束IP
        if (ipRange.getStartIp().compareTo(ipRange.getEndIp()) > 0) {
            throw new FilinkIpRangeNotRangeException();
        }
        //获取掩码
        int ipv6Mask = Integer.parseInt(ipRange.getMask());
        if (ipv6Mask > 0) {
            //起始IP转换成二进制字符串，截取前ipv6Mask位
            String startIpv6Value = getIpv6Value(ipRange.getStartIp()).substring(0, ipv6Mask);
            //结束IP转换成二进制字符串，截取前ipv6Mask位
            String endIpv6Value = getIpv6Value(ipRange.getEndIp()).substring(0, ipv6Mask);
            //比较截取部分是否相同，相同则是同一网络段，可以组成
            if (!startIpv6Value.equals(endIpv6Value)) {
                throw new FilinkIpRangeNotRangeException();
            }
        }
    }

    /**
     * 校验能否组成一个IPV4范围段
     * @param ipRange IPV4范围段
     */
    private void checkIpv4Range(IpRange ipRange) {
        //判断结束IP是否大于等于结束IP
        if (compareIpv4(ipRange.getStartIp(), ipRange.getEndIp()) < 0) {
            throw new FilinkIpRangeNotRangeException();
        }
        //起始IP转换成二进制int
        int startIpValue = getIpv4Value(ipRange.getStartIp());
        //结束IP转换成二进制int
        int endIpValue = getIpv4Value(ipRange.getEndIp());
        //子网掩码转换成二进制int
        int maskValue = getIpv4Value(ipRange.getMask());
        //判断是否为同一网络段
        if ((startIpValue & maskValue) != (endIpValue & maskValue)) {
            throw new FilinkIpRangeNotRangeException();
        }
    }
    /**
     * IPV6 16进制字符串转换二进制字符串
     * @param ipv6 ipv6
     * @return 二进制字符串
     */
    private String getIpv6Value(String ipv6) {
        //按“:”分割
        String[] ipArray = ipv6.split(SecurityStrategyConstants.IP_COLON);
        StringBuilder ipv6Builder = new StringBuilder();
        //逐段转换
        for (String anIpArray : ipArray) {
            //每一段逐个将16进制字符，转为4位二进制字符串，不足前面补0
            for (int j = 0; j < anIpArray.length(); j++) {
                //取每一个字符
                String indexStr = anIpArray.substring(j, j + 1);
                //将16进制字符，转为二进制字符串
                String binaryStr = Integer.toBinaryString(Integer.parseInt(indexStr, 16));
                //二进制字符串转为4位，不足前面补0
                binaryStr = String.format("%04d", Integer.parseInt(binaryStr));
                ipv6Builder.append(binaryStr);
            }
        }
        return ipv6Builder.toString();
    }


    /**
     * 获取IPV4转换二进制数值
     * @param ipv4 ipv4
     * @return 二进制数值
     */
    private int getIpv4Value(String ipv4) {
        //按“.”分割
        String[] ipArray = ipv4.split("\\.");
        byte[] ipByte = new byte[ipArray.length];
        //每一段转为二进制byte
        for (int index = 0; index < ipArray.length; index++) {
            ipByte[index] = (byte)(Integer.parseInt(ipArray[index]) & 0xff);
        }
        //转换二进制数值
        int ipv4Value = ipByte[3] & 0xFF;
        ipv4Value |= ((ipByte[2] << 8) & 0xFF00);
        ipv4Value |= ((ipByte[1] << 16) & 0xFF0000);
        ipv4Value |= ((ipByte[0] << 24) & 0xFF000000);
        return ipv4Value;
    }

    /**
     * 判断IP范围是否是全零段
     * @param ipRange IP范围
     * @param regex 全零正则
     * @return true是 false不是
     */
    private boolean checkRangeZero(IpRange ipRange, String regex) {
        int count = 0;
        //起始IP是否是全零段
        if (ipRange.getStartIp().matches(regex)) {
            count ++;
        }
        //结束IP是否是全零段
        if (ipRange.getEndIp().matches(regex)) {
            count ++;
        }
        //判断IP范围是否是全零段
        if (count == 1) {
            throw new FilinkIpRangeNotZeroException();
        }
        return count == 0;
    }

    /**
     * 校验输入IPV6是否都是IPV4格式
     * @param ipRange IP范围
     */
    private void checkInputIpv6Range(IpRange ipRange) {
        if (ipRange.getStartIp().matches(SecurityStrategyConstants.IPV6_REGEX) && ipRange.getEndIp().matches(SecurityStrategyConstants.IPV6_REGEX)
                && ipRange.getMask().matches(SecurityStrategyConstants.IPV6_MASK_REGEX)) {
            return;
        }
        throw new FilinkIpRangeFormatException();
    }

    /**
     * 校验输入IPV4是否都是IPV4格式
     * @param ipRange IP范围
     */
    private void checkInputIpv4Range(IpRange ipRange) {
        if (ipRange.getStartIp().matches(SecurityStrategyConstants.IPV4_REGEX) && ipRange.getEndIp().matches(SecurityStrategyConstants.IPV4_REGEX)
                && ipRange.getMask().matches(SecurityStrategyConstants.IPV4_REGEX) ) {
            return;
        }
        throw new FilinkIpRangeFormatException();
    }

    /**
     * 比较两个IPV4大小
     * @param startIp 开始IP
     * @param endIp 结束IP
     * @return 1结束IP大于开始IP -1结束IP小于开始IP 0相等
     */
    private int compareIpv4(String startIp, String endIp) {
        //开始IP按“.”分割
        String[] startIpArray = startIp.split("\\.");
        //结束IP按“.”分割
        String[] endIpArray = endIp.split("\\.");
        //每一段转换为Int比较大小
        for (int i = 0; i < startIpArray.length; i ++) {
            //结束IP大于开始IP
            if (Integer.parseInt(endIpArray[i]) > Integer.parseInt(startIpArray[i])) {
                return 1;
            }
            //结束IP小于开始IP
            if (Integer.parseInt(endIpArray[i]) < Integer.parseInt(startIpArray[i])) {
                return -1;
            }
        }
        //相等
        return 0;
    }

    /**
     * 判断IPV6是否在IP范围内
     * @param ipv6 IP
     * @param ipRange IP范围
     * @return true在范围内 false不在范围内
     */
    private boolean inIpv6Range(String ipv6, IpRange ipRange) {
        return ipRange.getStartIp().compareTo(ipv6) <= 0 && ipv6.compareTo(ipRange.getEndIp()) <= 0;
    }

    /**
     * 判断IPV4是否在IP范围内
     * @param ipv4 IP
     * @param ipRange IP范围
     * @return true在范围内 false不在范围内
     */
    private boolean inIpv4Range(String ipv4, IpRange ipRange) {
        return compareIpv4(ipRange.getStartIp(), ipv4) >= 0 && compareIpv4(ipv4, ipRange.getEndIp()) >= 0;
    }
    /**
     * 批量新增日志
     * @param ipRanges IP范围信息List
     * @param functionCode XML functionCode
     * @param logConstants 操作类型
     */
    private void addLogList(List<IpRange> ipRanges, String functionCode, String logConstants) {
        //设置语言环境
        languageUtil.querySystemLanguage();
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        for (IpRange ipRange : ipRanges){
            String ipRangeName = ipRange.getStartIp() + "~" + ipRange.getEndIp();
            AddLogBean addLogBean = addLogBean(ipRange.getRangeId(), ipRangeName,functionCode, logConstants);
            addLogBeanList.add(addLogBean);
        }
        //新增操作日志
        logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 新增或修改IP范围新增日志
     * @param ipRange IP范围
     * @param functionCode XML functionCode
     * @param logConstants 操作类型
     */
    private void addLogByRange(IpRange ipRange, String functionCode, String logConstants) {
        String ipRangeName = ipRange.getStartIp() + "~" + ipRange.getEndIp();
        addLog(ipRange.getRangeId(), ipRangeName, functionCode, logConstants);
    }
    /**
     * 新增日志
     * @param rangeId 参数ID
     * @param ipRangeName 新增日志记录内容
     * @param functionCode XML functionCode
     * @param logConstants 操作类型
     */
    private void addLog(String rangeId, String ipRangeName, String functionCode, String logConstants) {
        //设置语言环境
        languageUtil.querySystemLanguage();
        //获取bean
        AddLogBean addLogBean = addLogBean(rangeId, ipRangeName, functionCode, logConstants);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 新增日志bean
     * @param rangeId 参数ID
     * @param ipRangeName 新增日志记录内容
     * @param functionCode XML functionCode
     * @param logConstants 操作类型
     * @return 新增日志bean
     */
    private AddLogBean addLogBean(String rangeId, String ipRangeName, String functionCode, String logConstants) {
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(SecurityStrategyConstants.RANGE_ID);
        addLogBean.setDataName(SecurityStrategyConstants.IP_RANGE_NAME);
        //获得操作对象名称
        addLogBean.setOptObj(ipRangeName);
        addLogBean.setFunctionCode(functionCode);
        //获得操作对象id
        addLogBean.setOptObjId(rangeId);
        //操作为新增
        addLogBean.setDataOptType(logConstants);
        return addLogBean;
    }

    /**
     * 构建ipv6,将其他ipv6格式转化为8段32位标准ipv6格式
     * @param ipAddress ipv6
     * @return ipv6
     */
    private String buildIpv6(String ipAddress) {
        int dotFlag = ipAddress.indexOf(".");
        int dColonFlag = ipAddress.indexOf(SecurityStrategyConstants.IP_DOUBLE_COLON);
        int forwardSlashFlag = ipAddress.indexOf("%");
        if (forwardSlashFlag != -1) {
            ipAddress = ipAddress.substring(0, forwardSlashFlag);
        }
        String[] ipArray = ipAddress.split(SecurityStrategyConstants.IP_COLON);
        if (dotFlag != -1 ) {
            if (dColonFlag != -1) {
                ipAddress = replaceDoubleColon(ipAddress, SecurityStrategyConstants.IPV6_PART - ipArray.length);
                ipArray = ipAddress.split(SecurityStrategyConstants.IP_COLON);
            }
            String[] ipv4Array = ipArray[SecurityStrategyConstants.IPV6_IPV4_PORT].split("\\.");
            StringBuilder ipv4Builder = new StringBuilder();
            for (int i = 0; i < SecurityStrategyConstants.IPV6_NUM; i++) {
                String ipv4Str = Integer.toHexString(Integer.parseInt(ipv4Array[i]));
                if (ipv4Str.length() < SecurityStrategyConstants.IPV4_TO_IPV6_NUM) {
                    ipv4Builder.append(0);
                }
                ipv4Builder.append(ipv4Str);
                if (i == 1) {
                    ipv4Builder.append(SecurityStrategyConstants.IP_COLON);
                }
            }
            ipAddress = changeIpv6Array(ipArray, ipArray.length - 1) + SecurityStrategyConstants.IP_COLON + ipv4Builder.toString();
        } else {
            if (dColonFlag != -1) {
                ipAddress = replaceDoubleColon(ipAddress, SecurityStrategyConstants.IPV6_PART - ipArray.length + 1);
                ipArray = ipAddress.split(SecurityStrategyConstants.IP_COLON);
            }
            ipAddress = changeIpv6Array(ipArray, ipArray.length);
        }
        return ipAddress;
    }

    /**
     *将ipv6缩写格式“::”转化为完整格式
     * @param ipAddress ipv6
     * @param abbrLength 长度
     * @return ipv6
     */
    private String replaceDoubleColon(String ipAddress, int abbrLength) {
        StringBuilder abbr = new StringBuilder();
        for (int i = 0; i < abbrLength; i++) {
            abbr.append(SecurityStrategyConstants.IP_COLON);
            abbr.append(0);
        }
        abbr.append(SecurityStrategyConstants.IP_COLON);
        if (ipAddress.startsWith(SecurityStrategyConstants.IP_DOUBLE_COLON)) {
            abbr.insert(0, 0);
        } else if (ipAddress.endsWith(SecurityStrategyConstants.IP_DOUBLE_COLON)) {
            abbr.append(0);
        }
        return ipAddress.replace(SecurityStrategyConstants.IP_DOUBLE_COLON, abbr.toString());
    }

}