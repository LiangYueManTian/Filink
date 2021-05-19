package com.fiberhome.filink.license.bean;

import lombok.Data;

/**
 * license使用量类
 *
 * @author zepenggao@wistronits.com
 * @date 2019/2/21 10:43
 */
@Data
public class LicenseThresholdFeignBean {

    /**
     * 名称
     */
    public String name;

    /**
     * 数量
     */
    public int num;

}
