package com.fiberhome.filink.fdevice.bean.area;

import lombok.Data;

/**
 * <p>
 * 包含父级的区域实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@Data
public class ToTopAreaInfo extends AreaInfo {
    /**
     * 父级信息
     */
    private ToTopAreaInfo parentAreaInfo;
}
