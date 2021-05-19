package com.fiberhome.filink.userserver.bean;

import lombok.Data;

import java.util.List;

/**
 * 通用参数类
 * @author xuangong
 */
@Data
public class Parameters {

    /**
     * token信息
     */
    private String token;

    /**
     * 第一个参数信息
     */
    private String firstParameter;

    /**
     * 第二个参数信息
     */
    private String secondParameter;

    /**
     * 第一个数组参数
     */
    private String[] firstArrayParameter;

    /**
     * 第一个集合参数
     */
    private List<String> firstListParameter;

    /**
     * 标志位
     */
    private boolean flag;
}
