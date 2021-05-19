package com.fiberhome.filink.rfid.bean.template;

import lombok.Data;

/**
 * 坐标模板公共类
 *
 * @author liyj
 * @date 2019/7/9
 */
@Data
public class RealPositionCommon {
    /**
     * 箱-宽度
     */
    private Double boxWidth;
    /**
     * 箱-高度
     */
    private Double boxHeight;
    /**
     * 框-宽度
     */
    private Double frameWidth;
    /**
     * 框-高度
     */
    private Double frameHeight;
    /**
     * 盘-宽度
     */
    private Double discWidth;
    /**
     * 盘-高度
     */
    private Double discHeight;
    /**
     * 端口-宽度
     */
    private Double portWidth;
    /**
     * 端口-高度
     */
    private Double portHeight;

}
