package com.fiberhome.filink.workflowbusinessserver.utils.common;

import com.fiberhome.filink.workflowapi.constant.ProcessConstants;
import com.fiberhome.filink.workflowbusinessserver.req.process.ProcessBaseInfoReq;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * @date 2019/3/16 11:01
 */
@Slf4j
public class GenerateProcessParam {


    /**
     * 流程操作map
     */
    private static HashMap<String, String> operateMap = new HashMap<String, String>();

    static {
        initProcessOperateMap();
    }

    /**
     * 初始化流程操作map
     */
    private static void initProcessOperateMap() {
        operateMap.put(ProcessConstants.PROC_OPERATION_START, "getStartProcessParam");
        operateMap.put(ProcessConstants.PROC_OPERATION_COMPLETE, "getCompleteParam");
        operateMap.put(ProcessConstants.PROC_OPERATION_SINGLE_BACK, "getSingleBackParam");
        operateMap.put(ProcessConstants.PROC_OPERATION_REVOKE, "getRevokeParam");
        operateMap.put(ProcessConstants.PROC_OPERATION_ASSIGN, "getAssignParam");
        operateMap.put(ProcessConstants.PROC_OPERATION_TURN, "getTurnParam");
    }


    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 12:05
     * @param procBase
     * @param operate 操作
     */
    public static Map<String, Object> generateProcessParam(ProcessBaseInfoReq procBase, String operate) {
        if (StringUtils.isEmpty(procBase.getStatus())) {
            return GenerateProcessParam.getStartProcessParam(procBase);
        } else {
            //根据不同的操作执行不同的方法
            GenerateProcessParam generateProcessParam = new GenerateProcessParam();
            //获得类对象
            Class clazz = generateProcessParam.getClass();
            try {
                //获得数据的方法
                Method listMethod = clazz.getDeclaredMethod(operateMap.get(operate), ProcessBaseInfoReq.class);
                //调用方法,获得数据
                Map<String, Object> resultHashMap = (Map<String, Object>) listMethod.invoke(generateProcessParam, procBase);
                return resultHashMap;
            } catch (Exception e) {
                log.warn("get proc param error", e);
            }
        }
        //获取参数失败
        return null;
    }

    /**
     * 启动流程参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 11:24
     * @param procBase 流程参数
     */
    public static Map<String, Object> getStartProcessParam(ProcessBaseInfoReq procBase) {
        Map<String, Object> retMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        //参数
        retMap.put(ProcessConstants.PROC_POINT_DEPARTMENT, procBase.getDepartmentIds());
        return retMap;
    }

    /**
     * 提交流程参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 11:24
     * @param procBase 流程参数
     */
    public static Map<String, Object> getCompleteParam(ProcessBaseInfoReq procBase) {
        Map<String, Object> retMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        //流程状态
        String status = procBase.getStatus();
        //参数
        if (ProcBaseConstants.PROC_STATUS_PENDING.equals(status)) {
            //待处理状态
            retMap.put(ProcessConstants.PROC_POINT_DOWNLOAD_USER, procBase.getAssign());
            //操作参数
            retMap.put(ProcessConstants.PROC_VAR_OPERATION, ProcessConstants.PROC_OPERATION_COMPLETE);
        } else if (ProcBaseConstants.PROC_STATUS_PROCESSING.equals(status)) {
            //处理中状态
            retMap.put(ProcessConstants.PROC_VAR_OPERATION, ProcessConstants.PROC_OPERATION_COMPLETE);
        }
        return retMap;
    }


    /**
     * 退单参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 11:24
     * @param procBase 流程参数
     * @return 返回参数
     */
    public static Map<String, Object> getSingleBackParam(ProcessBaseInfoReq procBase) {
        Map<String, Object> retMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        //操作为退单
        retMap.put(ProcessConstants.PROC_VAR_OPERATION, ProcessConstants.PROC_OPERATION_SINGLE_BACK);
        return retMap;
    }

    /**
     * 撤回流程参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 11:24
     * @param procBase 流程参数
     * @return 返回参数
     */
    public static Map<String, Object> getRevokeParam(ProcessBaseInfoReq procBase) {
        Map<String, Object> retMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        retMap.put(ProcessConstants.PROC_VAR_OPERATION, ProcessConstants.PROC_OPERATION_REVOKE);
        retMap.put(ProcessConstants.PROC_POINT_ASSIGNED, procBase.getProcId());
        return retMap;
    }

    /**
     * 指派流程参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 11:24
     * @param procBase 流程参数
     * @return 返回参数
     */
    public static Map<String, Object> getAssignParam(ProcessBaseInfoReq procBase) {
        return new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
    }

    /**
     * 转办流程参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 11:24
     * @param procBase 流程参数
     * @return 返回参数
     */
    public static Map<String, Object> getTurnParam(ProcessBaseInfoReq procBase) {
        Map<String, Object> retMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        //操作为转办
        retMap.put(ProcessConstants.PROC_VAR_OPERATION, ProcessConstants.PROC_OPERATION_TURN);
        //转办任务
        retMap.put(ProcessConstants.PROC_POINT_TURN_USER, procBase.getAssign());
        return retMap;
    }

}
