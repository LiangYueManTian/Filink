package com.fiberhome.filink.about.bean;


import lombok.Data;

/**
 * <p>
 *  关于对象
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/6
 */
@Data
public class About {
    /**
     * 主键
     */
    private String aboutId;
    /**
     * licence是否授权 0 否 1 是
     */
    private String licenseAuthorize;
}
