package com.fiberhome.filink.workflowbusinessserver.utils.common;

/**
 * 巡检公共校验类
 * @author hedongwei@wistronits.com
 * @date 2019/4/1 16:24
 */

public class InspectionCommonValidate {

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:53
     * @param isOpen 是否开启
     * @return 校验结果
     */
    public static boolean checkIsOpen(String isOpen) {
        //启用的内容的字段信息
        Integer isOpenMinLength = 0;
        Integer isOpenMaxLength = 1;
        return InspectionCommonValidate.checkObject(isOpen, isOpenMinLength, isOpenMaxLength);
    }

    /**
     * 校验选择全部
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:59
     * @param isSelectAll 是否选中全集
     * @return 校验结果
     */
    public static boolean checkIsSelectAll(String isSelectAll) {
        //是否选中全集
        Integer isSelectAllMinLength = 0;
        Integer isSelectAllMaxLength = 1;
        return InspectionCommonValidate.checkObject(isSelectAll, isSelectAllMinLength, isSelectAllMaxLength);
    }

    /**
     * 返回校验数据结果
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 9:53
     * @param data 数据
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 返回校验结果
     */
    public static boolean checkObject(String data , Integer minLength, Integer maxLength) {
        if (!ValidateUtils.validateDataInfoIsEmpty(data)) {
            return false;
        }

        if (!ValidateUtils.validateDataLength(data, minLength, maxLength)) {
            return false;
        }
        return true;
    }


}
