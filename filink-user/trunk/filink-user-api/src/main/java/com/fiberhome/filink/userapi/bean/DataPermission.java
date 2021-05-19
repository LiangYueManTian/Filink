package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.util.List;

/**
 * 数据权限参数
 * @author xgong
 */
@Data
public class DataPermission {

    /**
     * 部门id列表
     */
    private List<String> deptList;

    /**
     * 设施类型列表
     */
    private List<String> deviceTypes;

    /**
     * 设施id
     */
    private String deviceId;
}
