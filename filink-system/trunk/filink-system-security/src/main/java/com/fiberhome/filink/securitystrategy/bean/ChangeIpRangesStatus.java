package com.fiberhome.filink.securitystrategy.bean;


import lombok.Data;

import java.util.List;

/**
 * <p>
 *     批量启用/禁用实体
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
@Data
public class ChangeIpRangesStatus {
    /**
     * id list
     */
    private List<String> rangeIds;
    /**
     * 启用/禁用
     */
    private String rangeStatus;
}
