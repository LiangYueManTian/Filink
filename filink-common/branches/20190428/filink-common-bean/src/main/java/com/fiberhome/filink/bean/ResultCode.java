package com.fiberhome.filink.bean;

/**
 * <p>
 *      系统共用返回码定义
 *      子服务返回码定义规范如下：
 *          基础服务：   11****
 *          用户服务：   12****
 *          设施服务：   13****
 *          电子锁服务： 14****
 *          RFID服务：   15****
 *          工单服务：   16****
 *          告警服务：   17****
 *          统计服务：   18****
 *          中转服务：   19****
 *          网关服务：   20****
 *          系统服务:    21****
 *
 *          例如用户不存在可以定义为： 120000
 *          后续增加服务编号递增
 *
 * </p>
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/7 14:10
 */
public class ResultCode {

    /**
     * 请求成功
     */
    public static final Integer SUCCESS = 0;

    public static final Integer FAIL = -1;

    /**
     * 未登录
     */
    public static final Integer NOT_LOGIN = -2;

}
