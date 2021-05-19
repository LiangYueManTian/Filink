package com.fiberhome.filink.picture.req.app;

import lombok.Data;

import java.util.Set;

/**
 * <p>
 * 获取实景图请求
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-16
 */
@Data
public class LivePicReq {

    /**
     * 图片张数
     */
    private String picNum;

    /**
     * 设施ids
     */
    private Set<String> deviceIds;

    /**
     * 设施名称
     */
    private String deviceName;

}
