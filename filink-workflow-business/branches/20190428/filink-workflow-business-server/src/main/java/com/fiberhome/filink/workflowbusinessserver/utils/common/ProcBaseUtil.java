package com.fiberhome.filink.workflowbusinessserver.utils.common;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 工单工具类
 * @author hedongwei@wistronits.com
 * @date 2019/4/24 12:13
 */

public class ProcBaseUtil {

    /**
     * 根据操作判断查询是删除还是回复,查询条件取反
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 12:16
     * @param isDeletedParam 删除参数
     * @return 获取是否删除的查询条件
     */
    public static String getIsDeletedSearchParam(String isDeletedParam) {
        String notDeleted = WorkFlowBusinessConstants.IS_NOT_DELETED;
        String isDeleted = WorkFlowBusinessConstants.IS_DELETED;
        if (notDeleted.equals(isDeletedParam)) {
           return isDeleted;
        } else {
           return notDeleted;
        }
    }

    /**
     * 获取登录用户id
     *
     * @return String
     */
    public static String getUserId(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (!ObjectUtils.isEmpty(request)) {
                return request.getHeader("userId");
            }
        }
        return "";
    }


    /**
     * 设置设施类型名称
     *
     * @param deviceType 设施类型
     *
     * @return String 设施类型名字
     */
    public static String getDeviceTypeName(String deviceType){
        //设施类型名字
        if (ProcBaseConstants.DEVICE_TYPE_001.equals(deviceType)){
            //光交箱
            return I18nUtils.getString(ProcBaseI18n.DEVICE_TYPE_001);
        } else if (ProcBaseConstants.DEVICE_TYPE_030.equals(deviceType)){
            //人井
            return I18nUtils.getString(ProcBaseI18n.DEVICE_TYPE_030);
        } else if (ProcBaseConstants.DEVICE_TYPE_060.equals(deviceType)){
            //配线架
            return I18nUtils.getString(ProcBaseI18n.DEVICE_TYPE_060);
        } else if (ProcBaseConstants.DEVICE_TYPE_090.equals(deviceType)){
            //接头盒
            return I18nUtils.getString(ProcBaseI18n.DEVICE_TYPE_090);
        } else if (ProcBaseConstants.DEVICE_TYPE_150.equals(deviceType)){
            //分纤箱
            return I18nUtils.getString(ProcBaseI18n.DEVICE_TYPE_150);
        } else {
            return deviceType;
        }
    }



    /**
     * 设置设施名称，类型，区域
     *
     * @param processInfo 工单汇总类
     * @return 是否设置成功
     */
    public static ProcessInfo setDeviceInfoList(ProcessInfo processInfo) {
        StringBuilder deviceId = new StringBuilder();
        StringBuilder deviceName = new StringBuilder();
        StringBuilder deviceType = new StringBuilder();
        StringBuilder deviceTypeName = new StringBuilder();
        StringBuilder deviceAreaId = new StringBuilder();
        StringBuilder deviceAreaName = new StringBuilder();
        if (processInfo.getProcRelatedDevices() == null || processInfo.getProcRelatedDevices().size() == 0) {
            return processInfo;
        }
        for (ProcRelatedDevice procRelatedDevice : processInfo.getProcRelatedDevices()) {
            //拼接设施信息
            deviceId = ProcBaseUtil.concatDeviceInfo(deviceId,procRelatedDevice,ProcBaseConstants.DEVICE_INFO_DEVICE_ID);
            deviceName = ProcBaseUtil.concatDeviceInfo(deviceName,procRelatedDevice,ProcBaseConstants.DEVICE_INFO_DEVICE_NAME);
            deviceType = ProcBaseUtil.concatDeviceInfo(deviceType,procRelatedDevice,ProcBaseConstants.DEVICE_INFO_DEVICE_TYPE);
            deviceAreaId = ProcBaseUtil.concatDeviceInfo(deviceAreaId,procRelatedDevice,ProcBaseConstants.DEVICE_INFO_DEVICE_AREA_ID);
            deviceAreaName = ProcBaseUtil.concatDeviceInfo(deviceAreaName,procRelatedDevice,ProcBaseConstants.DEVICE_INFO_DEVICE_AREA_NAME);

            //拼接设施类型名字信息
            if (StringUtils.isEmpty(deviceTypeName.toString())){
                //判断设施类型是否为空
                if (StringUtils.isNotEmpty(procRelatedDevice.getDeviceType())) {
                    //拼接设施类型名字
                    deviceTypeName.append(ProcBaseUtil.getDeviceTypeName(procRelatedDevice.getDeviceType()));
                }
            } else {
                //判断设施类型名字是否为空
                if (StringUtils.isNotEmpty(procRelatedDevice.getDeviceType())) {
                    //是否存在相同的设施类型名字
                    int deviceTypeNameIndex = deviceTypeName.indexOf(ProcBaseUtil.getDeviceTypeName(procRelatedDevice.getDeviceType()));
                    if (WorkFlowBusinessConstants.NOT_SEARCH_DATA == deviceTypeNameIndex) {
                        deviceTypeName.append("," + ProcBaseUtil.getDeviceTypeName(procRelatedDevice.getDeviceType()));
                    }
                }
            }
        }

        //获取页面返回的设施信息
        processInfo.getProcBaseResp().setDeviceId(deviceId);
        processInfo.getProcBaseResp().setDeviceName(deviceName);
        processInfo.getProcBaseResp().setDeviceType(deviceType);
        processInfo.getProcBaseResp().setDeviceTypeName(deviceTypeName);
        processInfo.getProcBaseResp().setDeviceAreaId(deviceAreaId);
        processInfo.getProcBaseResp().setDeviceAreaName(deviceAreaName);
        return processInfo;
    }

    /**
     * 拼接设施信息
     *
     * @param deviceInfoBuilder 待拼接设施信息
     * @param procRelatedDevice 工单关联设施信息
     * @param property 拼接属性名
     *
     * @return String 设施类型名字
     */
    public static StringBuilder concatDeviceInfo(StringBuilder deviceInfoBuilder,ProcRelatedDevice procRelatedDevice,String property) {
        String deviceTemp = null;
        try {
            deviceTemp = (String) PropertyUtils.getProperty(procRelatedDevice,property);
        } catch (Exception e) {
            e.printStackTrace();
            return deviceInfoBuilder;
        }
        //拼接设施信息
        if (StringUtils.isEmpty(deviceInfoBuilder.toString())){
            //判断设施信息是否为空
            if (StringUtils.isNotEmpty(deviceTemp)) {
                //拼接设施信息
                deviceInfoBuilder.append(deviceTemp);
            }
        } else {
            //判断设施区域名字是否为空
            if (StringUtils.isNotEmpty(deviceTemp)) {
                //是否存在相同的设施区域名字
                int deviceIndex = deviceInfoBuilder.indexOf(deviceTemp);
                if (WorkFlowBusinessConstants.NOT_SEARCH_DATA == deviceIndex) {
                    deviceInfoBuilder.append("," + deviceTemp);
                }
            }
        }
        return deviceInfoBuilder;
    }
}
