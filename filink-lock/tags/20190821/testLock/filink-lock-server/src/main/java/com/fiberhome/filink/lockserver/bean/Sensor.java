package com.fiberhome.filink.lockserver.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 * 传感器值
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/30
 */
@Data
@Document(collection = "sensor")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Sensor {

    /**
     * 主键id
     */
    @Id
    private String sensorId;
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 主控id
     */
    private String controlId;
    /**
     * 主控名称
     */
    private String controlName;
    /**
     * 温度
     */
    private Integer temperature;
    /**
     * 湿度
     */
    private Integer humidity;
    /**
     * 电量
     */
    private Integer electricity;
    /**
     * 倾斜
     */
    private Integer lean;
    /**
     * 水浸
     */
    private Integer leach;
    /**
     * 创建时间
     */
    private Long currentTime;


}

