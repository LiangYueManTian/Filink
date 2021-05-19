package com.fiberhome.filink.userserver.bean;

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
    private List<Unifyauth> unifyauthList;

    /**
     * 临时授权信息
     */
    private List<Tempauth> tempauthList;

    public AuthInfo(List<Unifyauth> unifyAuthList, List<Tempauth> tempAuthList) {
        this.unifyauthList = unifyAuthList;
        this.tempauthList = tempAuthList;
    }
}
