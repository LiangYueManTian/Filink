package com.fiberhome.filink.menu.utils;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menu.exception.FilinkMenuDateFormatException;
import org.springframework.util.StringUtils;

/**
 * 检测工具类
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/2/28 15:59
 */
public class CheckUtil {
    /**
     * 检测PageCondition
     *
     * @param pageCondition 接收参数
     * @return 检测结果
     */
    public static boolean checkPageConditionNull(PageCondition pageCondition) {
        if (pageCondition.getPageNum() == null || pageCondition.getPageSize() == null) {
            return true;
        }
        return false;
    }

    /**
     * 检测参数并格式化
     *
     * @param menuTemplateAndMenuInfoTree 接收参数
     * @return 检测结果
     */
    public static boolean cheackParameterAndFormat(MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree) {
        menuTemplateAndMenuInfoTree.parameterFormat();
        if (StringUtils.isEmpty(menuTemplateAndMenuInfoTree.getTemplateName())) {
            return false;
        }
        if (!menuTemplateAndMenuInfoTree.checkParameterFormat()) {
            throw new FilinkMenuDateFormatException();
        }
        return true;
    }
}
