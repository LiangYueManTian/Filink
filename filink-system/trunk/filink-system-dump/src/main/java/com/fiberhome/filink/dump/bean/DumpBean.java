package com.fiberhome.filink.dump.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 转储策略
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/29
 */
@Data
public class DumpBean implements Serializable {

    /**
     * 是否启用转储   0为不启用 1为启用
     */
    private String enableDump;
    /**
     * 触发条件    0为数量超限   1 为按月执行
     */
    private String triggerCondition;
    /**
     * 转储数量阈值
     */
    private String dumpQuantityThreshold;
    /**
     * 转储时间间隔  按月执行时可更改（1-24月）   数量超限时为12
     */
    private String dumpInterval;
    /**
     * 转储后操作  0为从数据库中删除  1为保留 或者转储到文件
     */
    private String dumpOperation;
    /**
     * 转储位置  0为转储到本地  1为转储到fastDFS服务器
     */
    private String dumpPlace;
    /**
     * , 转储范围 0为全部字段  1为指定字段
     */
    private String dumpScope;
    /**
     * 转储字段
     */
    private String dumpField;
    /**
     * 转储条数
     */
    private String turnOutNumber;

}
