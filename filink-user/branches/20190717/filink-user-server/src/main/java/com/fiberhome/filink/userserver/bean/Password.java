package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * @author xgong
 */
@Data
public class Password {

    private String token;

    private String userId;

    private String newPWD;

    private String oldPWD;

    private String confirmPWD;
}
