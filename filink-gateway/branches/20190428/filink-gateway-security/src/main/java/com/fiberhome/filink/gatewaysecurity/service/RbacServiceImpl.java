package com.fiberhome.filink.gatewaysecurity.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Permission;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限控制 返回false则请求失败
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:58
 */
@Slf4j
@Component("rbacService")
public class RbacServiceImpl implements RbacService {

    @Autowired
    private UserFeign userFeign;

    /**
     * 接口url的分隔符
     */
    private static final String INTERFACE_SPLIT = ",";

    /**
     * 选择器的标识
     */
    private static final String SELECT_TIP = "ForPageSelection";

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();

        boolean permission = false;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            String userId = RequestInfoUtils.getUserId();
            Result result = userFeign.queryUserInfoById(userId);
            Object data = result.getData();
            User user = JSONArray.toJavaObject((JSON) JSONArray.toJSON(data), User.class);
            Set<String> userPermissionList = getUserPermissionList(user);

            //如果是选择器就直接放行
            if(request.getRequestURI().contains(SELECT_TIP)){
                return true;
            }

            for (String url : userPermissionList) {
                // 传进来的url是否匹配
                if (pathMatcher.match(url, request.getRequestURI())) {
                    permission = true;
                    break;
                }
            }
        }
        return permission;
    }

    /**
     * 获取用户的权限能够访问的接口信息
     * @return  接口url列表
     */
    public Set<String> getUserPermissionList(User user){

        Set<String> urlSet = new HashSet<>();
        List<Permission> permissionList = user.getRole().getPermissionList();
        permissionList.forEach(permission -> {
            String interfaceUrl = permission.getInterfaceUrl();
            if(StringUtils.isNotEmpty(interfaceUrl)){
                urlSet.addAll(Arrays.asList(interfaceUrl.split(INTERFACE_SPLIT)));
            }
        });
        return urlSet;
    }

}
