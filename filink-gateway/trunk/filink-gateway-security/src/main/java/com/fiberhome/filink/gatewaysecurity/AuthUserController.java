package com.fiberhome.filink.gatewaysecurity;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 11:09
 */
@RestController
public class AuthUserController {

    @RequestMapping(value = "/auth/user", produces = "application/json")
    public Map<String, Object> user( OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user.getUserAuthentication().getPrincipal());
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));
        return userInfo;
    }

    @GetMapping("/testAuth")
    public Result test() {
        return ResultUtils.success();
    }

    @PreAuthorize("@rbacService.hasPermission(request,authentication)")
    @GetMapping("/testUser")
    public void demo() {
        System.out.println("this is ok");
    }

}
