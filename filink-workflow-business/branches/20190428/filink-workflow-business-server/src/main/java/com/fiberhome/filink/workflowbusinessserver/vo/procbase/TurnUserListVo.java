package com.fiberhome.filink.workflowbusinessserver.vo.procbase;

import lombok.Data;

/**
 * 转派人员集合
 * @author hedongwei@wistronits.com
 * @date 2019/4/15 17:28
 */
@Data
public class TurnUserListVo {

    /**
     * 用户编号
     */
    private String id;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户名称
     */
    private String userName;
}
