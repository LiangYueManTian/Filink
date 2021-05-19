package com.fiberhome.filink.fdevice.bean.area;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 区域树形结构
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@Data
public class AreaInfoTree extends AreaInfo {
    /**
     * 子类
     */
    private List<AreaInfoTree> children;
}
