package com.fiberhome.filink.fdevice.service.device.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.fdevice.async.DeviceAttentionAsync;
import com.fiberhome.filink.fdevice.bean.device.DeviceCollecting;
import com.fiberhome.filink.fdevice.constant.device.DeviceCollectingI18n;
import com.fiberhome.filink.fdevice.dao.device.DeviceCollectingDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dto.DeviceAttentionDto;
import com.fiberhome.filink.fdevice.dto.DeviceParam;
import com.fiberhome.filink.fdevice.exception.FilinkAttentionDataException;
import com.fiberhome.filink.fdevice.service.device.DeviceCollectingService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.fiberhome.filink.fdevice.constant.device.DeviceConstant.ADMIN;
import static com.fiberhome.filink.fdevice.constant.device.DeviceConstant.FOCUS_DEVICE;
import static com.fiberhome.filink.fdevice.constant.device.DeviceConstant.UNFOLLOW_DEVICE;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.*;
import static java.util.Collections.EMPTY_LIST;

/**
 * <p>
 * 我的关注 服务实现类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-14
 */
@Service
public class DeviceCollectingServiceImpl extends ServiceImpl<DeviceCollectingDao, DeviceCollecting> implements DeviceCollectingService {
    /**
     * 注入我的关注 持久层
     */
    @Autowired
    private DeviceCollectingDao deviceCollectingDao;
    /**
     * 注入 设施信息 持久层
     */
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    /**
     * 注入 设施信息 逻辑层
     */
    @Autowired
    private DeviceInfoService deviceInfoService;
    /**
     * 注入用户服务API
     */
    @Autowired
    private UserFeign userFeign;
    @Autowired
    /**
     * 注入webSocket推送
     */
    private DeviceAttentionAsync deviceAttentionAsync;

    /**
     * 字段device_id
     */
    private static final String DEVICE_ID = "device_id";
    /**
     * 字段user_id
     */
    private static final String USER_ID = "user_id";
    /**
     * =操作符
     */
    private static final String EQ = "eq";

    /**
     * 添加设施关注
     *
     * @param deviceCollecting
     * @return 返回结果
     */
    @Override
    public Result addDeviceCollecting(DeviceCollecting deviceCollecting) {
        //查看设施是否存在
        if (StringUtils.isEmpty(deviceInfoDao.selectDeviceById(deviceCollecting.getDeviceId()))) {
            throw new FilinkAttentionDataException();
        }
        //查看用户是否存在
        if (!userFeign.queryUserById(deviceCollecting.getUserId())) {
            throw new FilinkAttentionDataException();
        }
        //查看是否已关注该设施
        if (null == deviceCollectingDao.selectOne(deviceCollecting)) {
            //没有关注，添加关注
            deviceCollectingDao.insert(deviceCollecting);
        }

        //设施信息，推送给前台
        DeviceAttentionDto deviceAttentionDto = selectOneAttentionByDeviceId(deviceCollecting);
        deviceAttentionDto.setUserId(deviceCollecting.getUserId());
        //webSocket推送
        deviceAttentionAsync.sendAttentionInfo(FOCUS_DEVICE, deviceAttentionDto);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceCollectingI18n.ADD_COLLECTING_SUCCESS));


    }

    /**
     * 删除设施关注
     *
     * @param deviceCollecting
     * @return 返回结果
     */
    @Override
    public Result delDeviceCollecting(DeviceCollecting deviceCollecting) {
        EntityWrapper<DeviceCollecting> wrapper = new EntityWrapper<>();
        //根据实体构造删除条件
        if (!StringUtils.isEmpty(deviceCollecting.getDeviceId())) {
            wrapper.eq(DEVICE_ID, deviceCollecting.getDeviceId());
        }
        if (!StringUtils.isEmpty(deviceCollecting.getUserId())) {
            wrapper.eq(USER_ID, deviceCollecting.getUserId());
        }
        //设施信息
        DeviceAttentionDto deviceAttentionDto = selectOneAttentionByDeviceId(deviceCollecting);

        if (deviceAttentionDto != null) {
            //已关注，取消关注
            deviceCollectingDao.delete(wrapper);
        } else {
            //已取消关注，将这个设施id推送给前台
            deviceAttentionDto = new DeviceAttentionDto();
            deviceAttentionDto.setDeviceId(deviceCollecting.getDeviceId());
        }

        deviceAttentionDto.setUserId(deviceCollecting.getUserId());
        //webSocket推送
        deviceAttentionAsync.sendAttentionInfo(UNFOLLOW_DEVICE, deviceAttentionDto);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceCollectingI18n.DEL_COLLECTING_SUCCESS));


    }


    /**
     * 获取我的关注列表
     *
     * @param queryCondition
     * @return 关注列表
     */
    @Override
    public Result queryDeviceCollectingList(QueryCondition<DeviceCollecting> queryCondition) {
        //增加过滤条件：当前用户
        String userId = RequestInfoUtils.getUserId();
        if (!userId.equals(ADMIN)) {
            FilterCondition filterCondition = new FilterCondition();
            filterCondition.setFilterField(USER_ID);
            filterCondition.setOperator(EQ);
            filterCondition.setFilterValue(userId);
            queryCondition.getFilterConditions().add(filterCondition);
            DeviceParam deviceParam = deviceInfoService.getUserAuth(getCurrentUser());
            //数据权限
            if (ObjectUtils.isEmpty(deviceParam) || ObjectUtils.isEmpty(deviceParam.getAreaIds()) || ObjectUtils.isEmpty(deviceParam.getDeviceTypes())) {
                return ResultUtils.success(EMPTY_LIST);
            } else {
                FilterCondition areaCondition = new FilterCondition();
                areaCondition.setFilterField("area_id");
                areaCondition.setOperator("in");
                areaCondition.setFilterValue(deviceParam.getAreaIds());
                queryCondition.getFilterConditions().add(areaCondition);
                FilterCondition deviceTypeCondition = new FilterCondition();
                areaCondition.setFilterField("device_type");
                deviceTypeCondition.setOperator("in");
                deviceTypeCondition.setFilterValue(deviceParam.getDeviceTypes());
                queryCondition.getFilterConditions().add(deviceTypeCondition);
            }
        }


        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        // 构造过滤、排序等条件
        EntityWrapper wrapper = myBatiesBuildQuery(queryCondition);
        //获得关注数据
        List<DeviceAttentionDto> list = deviceCollectingDao.selectAttentionPage(page, wrapper);
        // 查询关注数据总数
        Integer count = deviceCollectingDao.selectAttentionCount(wrapper);
        // 构造返回结果,返回
        return ResultUtils.pageSuccess(myBatiesBuildPageBean(page, count, list));

    }

    /**
     * 按设施类型统计我的关注数据
     *
     * @return 统计数据
     */
    @Override
    public Result queryDeviceCollectingCountByType() {
        //先获取当前用户id，然后统计该用户关注设施的类型数据
        List<DeviceAttentionDto> deviceAttentionDtos = deviceCollectingDao.attentionCount(RequestInfoUtils.getUserId());
        return ResultUtils.success(deviceAttentionDtos);
    }


    /**
     * 获取我的关注列表 无分页
     *
     * @return 关注列表
     */
    @Override
    public Result queryAttentionList() {
        //当前用户
        String userId = RequestInfoUtils.getUserId();
        DeviceParam deviceParam = new DeviceParam();
        // admin权限跳过
        if (!userId.equals(ADMIN)) {
            User currentUser = getCurrentUser();
            //当前用户的数据权限
            deviceParam = deviceInfoService.getUserAuth(currentUser);
        }
        List<DeviceAttentionDto> deviceAttentionDtos = deviceCollectingDao.selectAttentionList(userId, deviceParam);
        return ResultUtils.success(deviceAttentionDtos);
    }


    /**
     * 根据设施id查询我的关注的一条设施信息
     *
     * @param deviceCollecting
     * @return 设施信息
     */
    private DeviceAttentionDto selectOneAttentionByDeviceId(DeviceCollecting deviceCollecting) {
        return deviceCollectingDao.selectOneAttentionByDeviceId(deviceCollecting);
    }

    /**
     * 查询当前用户信息
     *
     * @return
     */
    private User getCurrentUser() {
        //查询当前用户对象
        Object userObj = userFeign.queryCurrentUser(RequestInfoUtils.getUserId(), RequestInfoUtils.getToken());
        //转换为User
        return DeviceInfoService.convertObjectToUser(userObj);
    }
}
