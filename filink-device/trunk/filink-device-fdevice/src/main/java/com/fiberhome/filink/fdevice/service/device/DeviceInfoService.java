package com.fiberhome.filink.fdevice.service.device;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.DeviceI18n;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceException;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zepenggao@wistronits.com
 * @since 2019-01-07
 */
public interface DeviceInfoService extends IService<DeviceInfo> {

    /**
     * 新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     * @throws Exception 异常
     */
    Result addDevice(DeviceInfo deviceInfo);

    /**
     * PDA新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     * @throws Exception 异常
     */
    Result addDeviceForPda(DeviceInfo deviceInfo);

    /**
     * 校验设施名称是否重复
     *
     * @param deviceId   设施id
     * @param deviceName 设施名称
     * @return boolean
     */
    boolean checkDeviceName(String deviceId, String deviceName);

    /**
     * 修改设施信息
     *
     * @param deviceInfo 设施基本信息
     * @return Result
     * @throws Exception 异常
     */
    Result updateDevice(DeviceInfo deviceInfo);

    /**
     * 修改PDA设施信息
     *
     * @param deviceInfo
     * @return
     */
    Result updateDeviceForPda(DeviceInfo deviceInfo);

    /**
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     * @throws Exception 异常
     */
    Result listDevice(QueryCondition<DeviceInfo> queryCondition);

    /**
     * 分页查询设施列表（用户权限）
     *
     * @param queryCondition 查询条件
     * @param user           操作用户
     * @param needsAuth      是否需要控制权限
     * @return 设施List
     */
    Result listDevice(QueryCondition<DeviceInfo> queryCondition, User user, boolean needsAuth);


    /**
     * 选择器分页查询设施列表（用户权限）
     * @param queryCondition 查询条件
     * @return 设施List
     */
    Result listDeviceForSelectionLock(QueryCondition<DeviceInfo> queryCondition);

    /**
     * 条件查询设施数量
     *
     * @param queryCondition
     * @param user
     * @return
     */
    Integer queryDeviceCount(QueryCondition<DeviceInfo> queryCondition, User user);

    /**
     * 查询当前设施数
     *
     * @return
     */
    Integer queryCurrentDeviceCount();

    /**
     * 根据区域id查询绑定的设施，不加权限
     *
     * @param areaId 查询区域id
     * @return 查询结果
     */
    List<DeviceInfo> queryDeviceByAreaId(String areaId);

    /**
     * 根据区域id查询绑定的设施Dto，不加权限
     *
     * @param areaId
     * @return
     */
    List<DeviceInfoDto> queryDeviceDtoByAreaId(String areaId);

    /**
     * 根据区域ids查询绑定的设施Dto，不加权限
     *
     * @param areaIds
     * @return
     */
    List<DeviceInfoDto> queryDeviceDtoByAreaIds(List<String> areaIds);

    /**
     * 关联设施
     *
     * @param map      关联设施信息
     * @param areaInfo 区域信息
     * @return 操作结果
     * @throws
     */
    Boolean setAreaDevice(Map<String, List<String>> map, AreaInfo areaInfo);

    /**
     * 查询设施详情
     *
     * @param id     设施id
     * @param userId 用户id
     * @return
     */
    Result getDeviceById(String id, String userId);

    /**
     * 获取设施详情
     *
     * @param deviceReq 参数请求对象
     * @return Result 查询结果
     * @throws Exception 异常
     */
    Result queryDeviceByBean(DeviceReq deviceReq) throws Exception;

    /**
     * 查询设施详情
     *
     * @param ids 设施ID数组
     * @return
     * @throws Exception
     */
    List<DeviceInfoDto> getDeviceByIds(String[] ids) throws Exception;

    /**
     * 查询设施详情（前端）
     *
     * @param ids
     * @return
     */
    Result getDeviceResultByIds(String[] ids);

    /**
     * 查询用户所有设施基本信息
     *
     * @return 设施基本信息
     */
    Result queryDeviceAreaList();

    /**
     * 首页查询用户所有设施基本信息
     *
     * @return 设施基本信息
     */
    Result queryHomeDeviceArea();

    /**
     * 首页刷新用户所有设施信息
     *
     * @return 首页设施信息
     */
    Result refreshHomeDeviceArea();

    /**
     * 首页刷新用户指定设施信息（大数据）
     *
     * @param areaIdList 首页刷新的区域
     * @return 首页设施信息
     */
    Result refreshHomeDeviceAreaHuge(List<String> areaIdList);

    /**
     * 首页根据设施Id查询设施基本信息
     *
     * @param deviceId 设施Id
     * @return 设施基本信息
     */
    Result queryHomeDeviceById(String deviceId);

    /**
     * 首页根据设施IdList查询设施基本信息
     *
     * @param deviceIds 设施IdList
     * @return 设施基本信息
     */
    Result queryHomeDeviceByIds(List<String> deviceIds);

    /**
     * 修改首页首次加载阈值（设施数量）
     *
     * @param homeDeviceLimit 首页首次加载阈值
     */
    void updateHomeDeviceLimit(Integer homeDeviceLimit);

    /**
     * 查询所有设施信息
     * 数据结构 《区域ID，《设施类型，《设施ID， 设施信息》》》
     *
     * @return 所有设施信息
     */
    Map<String, Map<String, HomeDeviceInfoDto>> queryDeviceAreaAll();

    /**
     * 删除设施
     *
     * @param ids 设施ids
     * @return Result
     */
    Result deleteDeviceByIds(String[] ids);

    /**
     * 设施是否可以修改
     *
     * @param deviceId 设施序列号
     * @return
     */
    boolean deviceCanChangeDetail(String deviceId);

    /**
     * 刷新指定区域的Gis map 信息
     *
     * @param areaId 区域ID
     */
    void refreshDeviceAreaRedis(String areaId);

    /**
     * 类型转换
     *
     * @param filterConditions
     * @return
     */
    Map convertFilterConditionsToMap(List<FilterCondition> filterConditions);

    /**
     * 导出设施列表
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportDeviceList(ExportDto exportDto);

    /**
     * 请求附近设施列表
     *
     * @param deviceReqPda 请求参数
     * @return 返回结果
     */
    Result queryNearbyDeviceListForPda(DeviceReqPda deviceReqPda);

    /**
     * 更改指定设施的设施状态，部署状态
     *
     * @param deviceInfoDto
     * @return
     * @throws Exception
     */
    Result updateDeviceStatus(DeviceInfoDto deviceInfoDto) throws Exception;

    /**
     * 更改指定设施的部署状态
     *
     * @param updateDeviceStatusPda 更新信息参数
     * @return
     * @throws Exception
     */
    Result updateDeviceListStatus(UpdateDeviceStatusPda updateDeviceStatusPda);

    /**
     * 生成删除查询条件
     *
     * @return
     */
    static FilterCondition generateDeleteFilterCondition() {
        return generateFilterCondition("is_deleted", "neq", 1);
    }

    /**
     * 生成过滤条件
     *
     * @param filterField 过滤字段名
     * @param operator    过滤操作符 eq, like, in, gt, lt, ge etc.
     * @param filterValue 过滤值
     * @return
     */
    static FilterCondition generateFilterCondition(String filterField, String operator, Object filterValue) {
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField(filterField);
        filterCondition.setOperator(operator);
        filterCondition.setFilterValue(filterValue);
        return filterCondition;
    }

    /**
     * 转换为用户对象
     *
     * @param userObj
     * @return
     */
    static User convertObjectToUser(Object userObj) {
        //校验是否有值
        if (userObj == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.USER_SERVER_ERROR));
        }
        User user = JSON.parseObject(JSON.toJSONString(userObj), User.class);
        //校验用户信息
        if (user == null) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.USER_AUTH_INFO_ERROR));
        }
        if (user.getDepartment() == null) {
            user.setDepartment(new Department());
        }
        if (user.getDepartment().getAreaIdList() == null) {
            user.getDepartment().setAreaIdList(new ArrayList<>());
        }
        if (user.getRole() == null) {
            user.setRole(new Role());
        }
        if (user.getRole().getRoleDevicetypeList() == null) {
            user.getRole().setRoleDevicetypeList(new ArrayList<>());
        }
        return user;
    }

    /**
     * 根据区域id和设施类型查询设施
     *
     * @param deviceParam
     * @return
     */
    Result queryDeviceDtoForPageSelection(DeviceParam deviceParam);

    /**
     * 获取配置默认值
     *
     * @param deviceId 设施id
     * @return 配置默认值
     */
    Map<String, String> getDefaultParamsByDeviceType(String deviceId);

    /**
     * 查询用户的区域和设施类型权限
     *
     * @param user
     * @return
     */
    DeviceParam getUserAuth(User user);

    /**
     * 根据区域ID集合查询设施类型集合
     *
     * @param areaIds 区域ID集合
     * @return 设施类型集合
     */
    List<String> queryDeviceTypesByAreaIds(List<String> areaIds);

    /**
     * 根据区域id和设施类型查询设施信息
     *
     * @param deviceParam
     * @return
     */
    List<DeviceInfoBase> queryDeviceInfoBaseByParam(DeviceParam deviceParam);

    /**
     * 删除重试
     */
    void retryDelete();

    /**
     * 根据设施id查看设施名称
     *
     * @param deviceId
     * @return
     */
    String queryDeviceNameById(String deviceId);

    /**
     * 根据设施id  查看设施类型
     *
     * @param deviceId deviceId
     * @return
     */
    String queryDeviceTypeById(String deviceId);

    /**
     * 根据id查询设施是否存在
     *
     * @param id id
     * @return 查询结果
     */
    Result queryDeviceIsExistById(String id);
}
