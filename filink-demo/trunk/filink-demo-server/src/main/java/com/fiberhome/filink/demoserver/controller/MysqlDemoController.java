package com.fiberhome.filink.demoserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.demoserver.bean.User;
import com.fiberhome.filink.demoserver.service.UserService;
import com.fiberhome.filink.demoserver.service.impl.UserServiceImpl;
import com.fiberhome.filink.mysql.MpQueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 示例项目控制层
 *
 * 示例类容：
 *      mysql数据的新增
 *      mysql数据的修改
 *      mysql数据的删除
 *      mysql数据的查询
 *
 * @author 姚远
 *
 */
@RestController
@RequestMapping("/demo")
public class MysqlDemoController {

    /**
     * 注入用户service
     * @see UserServiceImpl
     */
    @Autowired
    private UserService userService;

    /**
     * 示例方法---新增用户
     *
     * @return 新增结果
     */
    @PostMapping("/addUser")
    public Result addUser(@RequestBody User user) {
        if (user != null) {
            if (userService.insert(user)) {
                return ResultUtils.success();
            }
        }
        return ResultUtils.warn(ResultCode.FAIL, "新增用户失败");
    }

    /**
     * 示例方法---删除用户
     *
     * @param queryCondition 删除条件可能是user的某一个属性，也可能是时间段等属性，
     *                       所以使用queryCondition封装
     * @return 删除结果
     */
    @PostMapping("/deleteUserByContion")
    public Result deleteUserByContion(@RequestBody QueryCondition queryCondition) {
        if (queryCondition != null) {
            if (userService.delete(MpQueryHelper.myBatiesBuildQuery(queryCondition))) {
                return ResultUtils.success();
            }
        }
        return ResultUtils.warn(ResultCode.FAIL, "用户删除失败");
    }

    /**
     * 示例方法---更新用户
     * @param user  更新条件
     * @return 更新结果
     */
    @PostMapping("/updateUserbyCondition")
    public Result updateUserbyCondition(@RequestBody User user) {
        if (user != null) {
            if (userService.insertOrUpdate(user)) {
                return ResultUtils.success();
            }
        }
        return ResultUtils.warn(ResultCode.FAIL, "更新用户失败");
    }


    /**
     * 示例方法---根据条件查询mysql数据库
     *
     * @param queryCondition 查询条件
     * @return 根据条件查询出的结果
     */
    @PostMapping("/queryUserListByCondition")
    public Result queryUserListByCondition(@RequestBody QueryCondition queryCondition) {
        Result result = null;
        if ((result=checkQueryCondition(queryCondition)).getCode()==0) {
            result = userService.queryUserListByCondition(queryCondition);
        }
        return result;
    }



    /**
     *  示例方法---检查请求传入参数
     *
     * @param queryCondition 传入参数封装
     * @return 检查结果
     */
    private Result checkQueryCondition(QueryCondition queryCondition) {
        if (queryCondition == null) {
            return ResultUtils.warn(ResultCode.FAIL, "查询条件为空");
        }
        if (queryCondition.getPageCondition() == null) {
            return ResultUtils.warn(ResultCode.FAIL, "分页条件为空");
        }
        // ... 根据需要添加
        return ResultUtils.success();
    }

}
