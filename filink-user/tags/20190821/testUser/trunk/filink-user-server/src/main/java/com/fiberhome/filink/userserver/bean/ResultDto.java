package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * @author xgong
 */
@Data
public class ResultDto {
    /**
     * data
     */
    private boolean data;
    /**
     * 消息/提示语
     */
    private String msg;

    public ResultDto(boolean data, String msg) {
        this.data = data;
        this.msg = msg;
    }
}
