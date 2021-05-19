package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.util.List;

/**
 * 授权信息实体
 * @author xgong
 */
@Data
public class AuthInfo {

    /**
     * 统一授权信息
     */
    private List<UnifyAuth> unifyAuthList;

    /**
     * 临时授权信息
     */
    private List<TempAuth> tempAuthList;

    public AuthInfo(List<UnifyAuth> unifyAuthList, List<TempAuth> tempAuthList) {
        this.unifyAuthList = unifyAuthList;
        this.tempAuthList = tempAuthList;
    }
}
