package com.fiberhome.filink.rfid.resp.template;

import com.fiberhome.filink.rfid.bean.template.RealPosition;
import lombok.Data;

import java.util.List;

/**
 * 实景图返回实体
 *
 * @author liyj
 * @date 2019/6/2
 */
@Data
public class RealRspDto extends RealPosition {

    /**
     * 子集
     */
    List<RealRspDto> childList;

    /**
     * 构造函数
     * A/B面
     *
     * @param abscissa   横坐标
     * @param ordinate   纵坐标
     * @param deviceType
     */
    public RealRspDto(Double abscissa, Double ordinate, Integer deviceType) {
        super(abscissa, ordinate, deviceType);
    }

    /**
     * 无参构造方法
     */
    public RealRspDto(List<RealRspDto> childList) {
        this.childList = childList;
    }

    /**
     * 无参构造方法
     */
    public RealRspDto() {
    }
}
