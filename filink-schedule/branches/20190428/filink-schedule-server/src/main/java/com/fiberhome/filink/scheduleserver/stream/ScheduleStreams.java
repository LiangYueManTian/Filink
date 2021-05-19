package com.fiberhome.filink.scheduleserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 输出输入通道定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:04
 */
public interface ScheduleStreams {

    String EXPORT_OUTPUT = "export_output";

    String WORKFLOW_BUSINESS_OUTPUT = "workflow_business_output";

    String PROC_INSPECTION_OUTPUT = "proc_inspection_output";

    String CMD_RESEND_OUTPUT = "cmd_resend_output";

    String OCEAN_CONNECT_CMD_RESEND_OUTPUT = "ocean_connect_cmd_resend_output";

    String ONE_NET_CMD_RESEND_OUTPUT = "one_net_cmd_resend_output";

    /**
     * 账号禁用
     */
    String USER_FORBIDDEN_OUTPUT = "user_forbidden_output";

    /**
     * 解锁用户
     */
    String UNLOCK_USER = "unlock_user";

    /**
     * 账号的有效期
     */
    String USER_EXPIRE = "user_expire";
    /**
     * 刷新首页设施Redis缓存信息
     */
    String HOME_DEVICE_OUTPUT = "home_device_output";
    /**
     * 定时刷新设备升级文件
     */
    String UPGRADE_FILE_OUTPUT = "upgrade_file_output";
    /**
     * 定时刷新设备升级文件
     * @return 返回结果
     */
    @Output(UPGRADE_FILE_OUTPUT)
    MessageChannel refreshUpgradeFile();
    /**
     * oneNet命令重发输出
     * @return 返回结果
     */
    @Output(HOME_DEVICE_OUTPUT)
    MessageChannel homeDeviceOutput();
    /**
     * oneNet命令重发输出
     * @return 返回结果
     */
    @Output(ONE_NET_CMD_RESEND_OUTPUT)
    MessageChannel oneNetCmdResendOutput();

    /**
     * oceanConnect命令重发输出
     * @return 返回结果
     */
    @Output(OCEAN_CONNECT_CMD_RESEND_OUTPUT)
    MessageChannel oceanConnectCmdResendOutput();

    /**
     * udp命令重发输出
     * @return 返回结果
     */
    @Output(CMD_RESEND_OUTPUT)
    MessageChannel cmdResendOutput();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(EXPORT_OUTPUT)
    MessageChannel output();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(WORKFLOW_BUSINESS_OUTPUT)
    MessageChannel workflowBusinessOutput();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(PROC_INSPECTION_OUTPUT)
    MessageChannel procInspectionOutput();


    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(USER_FORBIDDEN_OUTPUT)
    MessageChannel userForbiddenOutPut();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(UNLOCK_USER)
    MessageChannel unlockUser();

    /**
     * 用户的有效期
     * @return 返回结果
     */
    @Output(USER_EXPIRE)
    MessageChannel userExpire();
}
