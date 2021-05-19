package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  图片告警信息接收实体类，
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmPictureMsg implements Serializable {

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 告警设施ID
     */
    private String deviceId;

    /**
     * 门编号
     */
    private String doorNumber;

    /**
     * 图片后缀
     */
    private String suffix;

    /**
     * 告警图片信息
     */
    private String pictureInfo;

}
