package com.fiberhome.filink.onenetserver.bean.device;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *   oneNet平台HTTP公用实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-06
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class HttpEntity extends BaseCommonEntity {
    /**
     * 输出 url
     *
     * @return url
     */
    @Override
    public String toUrl() {
        return null;
    }
}
