package com.fiberhome.filink.picture.resp.app;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 获取实景图请求
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-16
 */
@Data
public class LivePicResp {

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 图片list
     */
    private List<LivePicInfo> picList;

}
