package com.fiberhome.filink.screen.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fiberhome.filink.bean.CheckInputString;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *     大屏管理 实体类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-28
 */
@Data
@TableName("large_screen")
public class LargeScreen {

    /**
     * 大屏ID
     */
    @TableId("large_screen_id")
    private String largeScreenId;

    /**
     * 大屏名称
     */
    @TableField("large_screen_name")
    private String largeScreenName;

    /**
     * 校验参数
     * @return true 参数错误 false正确
     */
    public boolean checkValue() {
        largeScreenName = CheckInputString.nameCheck(largeScreenName);
        return StringUtils.isEmpty(largeScreenId) || StringUtils.isEmpty(largeScreenName);
    }

}
