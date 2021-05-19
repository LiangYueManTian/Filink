package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * @author xgong
 */
@Data
public class ResultDto {

    private boolean data;

    private String msg;

    public ResultDto(boolean data, String msg) {
        this.data = data;
        this.msg = msg;
    }
}
