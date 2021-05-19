package com.fiberhome.filink.rfid.utils;

import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.rfid.exception.FilinkObtainUserInfoException;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 智能标签业务权限参数
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019/6/19
 */
@Data
@Component
public class RfidServerPermission {

    /**
     * 用户Feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 设施Feign
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 光交箱类型编号
     */
    public static final String DEVICE_TYPE_001 = "001";

    /**
     * 光交箱智能标签编号
     */
    public static final String DEVICE_TYPE_001_RFID = "001-2";

    /**
     * 人井类型编号
     */
    public static final String DEVICE_TYPE_030 = "030";

    /**
     * 人井智能标签编号
     */
    public static final String DEVICE_TYPE_030_RFID = "030-2";

    /**
     * 配线架类型编号
     */
    public static final String DEVICE_TYPE_060 = "060";

    /**
     * 配线架智能标签编号
     */
    public static final String DEVICE_TYPE_060_RFID = "060-2";

    /**
     * 接头盒类型编号
     */
    public static final String DEVICE_TYPE_090 = "090";

    /**
     * 接头盒智能标签编号
     */
    public static final String DEVICE_TYPE_090_RFID = "090-2";

    /**
     * 分纤箱类型编号
     */
    public static final String DEVICE_TYPE_150 = "150";

    /**
     * 分纤箱智能标签编号
     */
    public static final String DEVICE_TYPE_150_RFID = "150-2";

    /**
     * 检验是否具有数据权限
     *
     * @param deviceType     设施类型
     * @param deviceTypeList 设施类型集合
     */
    public static Boolean checkRfidPermission(String deviceType, List<String> deviceTypeList) {
        if (!deviceTypeList.contains(deviceType)) {
            //没有访问该设施类型访问权限
            return true;
        }
        if (deviceType.equals(DEVICE_TYPE_001)) {
            if (!deviceTypeList.contains(DEVICE_TYPE_001_RFID)) {
                //没有访问光交箱下智能标签的数据权限
                return true;
            }
        } else if (deviceType.equals(DEVICE_TYPE_030)) {
            if (!deviceTypeList.contains(DEVICE_TYPE_030_RFID)) {
                //没有访问人井下智能标签的数据权限
                return true;
            }
        }else if (deviceType.equals(DEVICE_TYPE_060)) {
            if (!deviceTypeList.contains(DEVICE_TYPE_060_RFID)) {
                //没有访问配线架下智能标签的数据权限
                return true;
            }
        } else if (deviceType.equals(DEVICE_TYPE_090)) {
            if (!deviceTypeList.contains(DEVICE_TYPE_090_RFID)) {
                //没有访问接头盒下智能标签的数据权限
                return true;
            }
        } else if (deviceType.equals(DEVICE_TYPE_150)) {
            if (!deviceTypeList.contains(DEVICE_TYPE_150_RFID)) {
                //没有访问分纤箱下智能标签的数据权限
                return true;
            }
        }
        return false;
    }

    /*---------------------------------------获取智能标签业务权限公共方法start-----------------------------------------*/
    /**
     * 获取拥有智能标签业务权限信息
     *
     * @return deviceType 设施类型
     */
    public Boolean getPermissionsInfoFoRfidServer(Set<String> deviceIds,String userId){
        boolean flag = true;
        String[] deviceIdList = new String[deviceIds.size()];
        deviceIds.toArray(deviceIdList);
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceIdList);

        //获取权限信息
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        Object userObj = userFeign.queryUserByIdList(userIds);
        //校验是否有值
        if (ObjectUtils.isEmpty(userObj)) {
            throw new FilinkObtainUserInfoException();
        }
        List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userObj), User.class);
        User user = new User();
        //添加用户map
        if (!ObjectUtils.isEmpty(userInfoList)) {
            user = userInfoList.get(0);
        }
        //admin用户不用校验权限
        String adminUserId = "1";
        String loginUserId = userId;
        if (!ObjectUtils.isEmpty(loginUserId)) {
            //当前用户是admin时不用获取所有权限
            if (adminUserId.equals(loginUserId)) {
                return false;
            }
        }
        //用户管理设施类型
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        //当用户权限信息中有信息为空，认为没有业务权限
        if (ObjectUtils.isEmpty(roleDeviceTypes)) {
            return true;
        }
        List<String> deviceTypes = new ArrayList<>();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes){
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }

        //校验是否有智能标签业务
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList){
            if (RfidServerPermission.checkRfidPermission(deviceInfoDto.getDeviceType(),deviceTypes)){
                flag = true;
                break;
            } else {
                flag = false;
            }
        }
        return flag;
    }
    /*---------------------------------------获取智能标签业务权限公共方法end-----------------------------------------*/

}
