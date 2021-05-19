package com.fiberhome.filink.demoserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.demoserver.bean.User;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 示例项目----Mongo
 *
 * 示例内容：
 *      mongo数据新增
 *      mongo数据删除
 *      mongo数据修改
 *      mongo数据查询
 *
 * @author 姚远
 */
@RestController
@RequestMapping("/mongo")
public class MongoDemoController {

    /**
     * mongodb实现类
     */
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 新增数据到mongodb
     *
     * @param user 传入数据
     * @return 返回插入结束
     */
    @PostMapping("/addUserToMongo")
    public Result addUserToMongo(@RequestBody User user) {
        mongoTemplate.insert(user);
        return ResultUtils.success();
    }

    /**
     * 删除MongoDB中的数据
     * @param user 要删除的参数
     * @return 删除结束
     */
    @PostMapping("/deleteUserByUserInfo")
    public Result deleteUserByUserInfo(@RequestBody User user) {
        mongoTemplate.remove(user);
        return ResultUtils.success();
    }

    /**
     * 修改MongoDB中的数据
     *
     * @param user 要删除的参数
     * @return 删除结束
     */
    @PostMapping("/updateUserByUserInfo")
    public Result updateUserByUserInfo(@RequestBody User user) {
        Update update = new Update().set("userNickname", user.getUserNickname());
        mongoTemplate.updateFirst(null,update, User.class);
        return ResultUtils.success();
    }

    /**
     * 根据条件查询
     *
     * @param queryCondition 查询条件封装
     * @return 查询结果
     */
    @PostMapping("/queryUserByCondition")
    public Result queryUserByCondition(@RequestBody QueryCondition queryCondition) {
        Query query = MongoQueryHelper.buildQuery(new Query(), queryCondition);
        List<User> users = mongoTemplate.find(query, User.class);
        return ResultUtils.success(users);
    }
}
