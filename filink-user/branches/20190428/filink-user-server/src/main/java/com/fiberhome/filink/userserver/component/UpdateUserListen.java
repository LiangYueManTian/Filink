package com.fiberhome.filink.userserver.component;

import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaDeptInfo;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.stream.UpdateUserListenStream;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xgong
 */
@Component
@Slf4j
public class UpdateUserListen {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AreaFeign areaFeign;

    /**
     * 监听消息
     *
     * @param code 消息码
     */
    @StreamListener(UpdateUserListenStream.UPDATE_USER_INPUT)
    public void updateUserListen(String code) {

        if (UserConst.UPDATE_USER_INFO.equals(code)) {

            List<User> users = userDao.queryAllUserDetailInfo();
            //对用户信息进行完善，还有区域，设施类型等数据要更新进去
            List<AreaDeptInfo> areaDeptInfoList = new ArrayList<>();
            if (CheckEmptyUtils.collectEmpty(users)) {
                List<String> deptList = new ArrayList<>();
                users.forEach(user -> {
                    deptList.add(user.getDeptId());
                });
                areaDeptInfoList = areaFeign.selectAreaDeptInfoByDeptIds(deptList);
            }
            //完善用户的部门信息
            for (User eachUser : users) {
                Department department = eachUser.getDepartment();
                if (department != null) {
                    String deptId = eachUser.getDeptId();
                    List<String> areaList = new ArrayList<>();
                    if (CheckEmptyUtils.collectEmpty(areaDeptInfoList)) {
                        List<AreaDeptInfo> collect = areaDeptInfoList.stream()
                                .filter(areaDeptInfo -> deptId.equals(areaDeptInfo.getDeptId()))
                                .collect(Collectors.toList());
                        if (CheckEmptyUtils.collectEmpty(collect)) {
                            collect.forEach(areaDeptInfo -> {
                                areaList.add(areaDeptInfo.getAreaId());
                            });
                        }
                    }
                    eachUser.getDepartment().setAreaIdList(areaList);
                }
            }

            //判断每个用户是否在redis缓存中
            if (CheckEmptyUtils.collectEmpty(users)) {

                users.forEach(user -> {
                    //完善用户的详细信息
                    if (RedisUtils.hasKey(user.getId() + user.getId())) {
                        RedisUtils.set(user.getId() + user.getId(), user);
                    }

                    Set<String> keys = RedisUtils.keys(UserConst.USER_PREFIX + user.getId() +
                            UserConst.REDIS_SPLIT + UserConst.REDIS_WILDCARD);
                    //如果存在数据，则对数据进行更新
                    if (CheckEmptyUtils.collectEmpty(keys)) {

                        keys.forEach(key -> {
                            //获取被更新信息用户的时间
                            long expire = RedisUtils.getExpire(key);
                            user.setDepartment(null);
                            user.setRole(null);
                            RedisUtils.set(key, user, expire);
                        });
                    }
                });
            }
        }
    }

}
