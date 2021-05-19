package com.fiberhome.filink.alarmcurrentserver.bean;

import java.util.List;
import lombok.Data;

/**
 * <p>
 * 用户下的区域和设施类型
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class UserPaream {

    /**
     * 区域id
     */
    private List<String> areaIds;

    /**
     * 设施类型
     */
    private List<String> deviceTypeIds;

}
