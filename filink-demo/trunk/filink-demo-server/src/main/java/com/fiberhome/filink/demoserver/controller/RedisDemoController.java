package com.fiberhome.filink.demoserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.demoserver.bean.User;
import com.fiberhome.filink.redis.RedisUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例项目----Redis
 *
 * 示例内容：
 *      redis数据的新增
 *      redis数据的删除
 *      redis数据的查询
 *      因为改就是直接覆盖，所有没有示例方法
 *
 * @author 姚远
 */
@RestController
@RequestMapping("/redis")
public class RedisDemoController {


    /**
     * 示例方法---新增user到redis中，以用户id为key，user对象为value
     *
     * @param user
     * @return
     */
    @PostMapping("/addUserToRedis")
    public Result addUserToRedis(@RequestBody User user) {
        if (user != null && !StringUtils.isEmpty(user.getId())) {
            RedisUtils.set(user.getId(), user);
            return ResultUtils.success();
        }
        return ResultUtils.warn(ResultCode.FAIL, "新增用户到redis失败");
    }

    /**
     * 示例方法---获取上个方法存入redis中的数据，key是user对象的id
     * @param user
     * @return
     */
    @PostMapping("/queryUserById")
    public Result queryUserById(@RequestBody User user) {
        Object o = RedisUtils.get(user.getId());
        return ResultUtils.success(o);
    }

    /**
     * 示例方法---移除redis中的某个数据,key是user对象的id
     *
     * @param user
     * @return
     */
    @PostMapping("/removeUserById")
    public Result removeUserById(@RequestBody User user) {
        RedisUtils.remove(user.getId());
        return ResultUtils.success();
    }
}
