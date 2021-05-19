package com.fiberhome.filink.dump.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 转储策略 参数类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/29
 */
@Data
public class DumpParam implements Serializable {
    /**
     * 转储策略id
     */
    private String paramId;

    /**
     * 转储策略
     */
    private String paramType;

    /**
     * 转储策略
     */
    private DumpBean dumpBean;
}
