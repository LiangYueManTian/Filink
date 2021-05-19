package com.fiberhome.filink.rfid.resp.template;

import lombok.Data;

import java.util.List;

/**
 * 实景图返回实体
 *
 * @author liyj
 * @date 2019/7/16
 */
@Data
public class RealPositionRspDto {
    /**
     * 主键id
     */
    private String id;
    /**
     * 父id
     */
    private String parentId;
    /**
     * 编号坐标x (用于后期微调处理 这里需要有调整 和App 尽量保持一致)
     */
    private Integer colNo;
    /**
     * 编号坐标y
     */
    private Integer rowNo;
    /**
     * 横坐标x
     */
    private Double abscissa;
    /**
     * 纵坐标Y
     */
    private Double ordinate;
    /**
     * 实际框号 给页面显示用 App 和前端都用这个字段
     */
    private Integer businessNum;
    /**
     * 模板编号 可能会改变
     */
    private Integer realNo;
    /**
     * A/B面 templateSideEnum
     */
    private Integer side;
    /**
     * 状态 用于实景图中 每个设施的都不相同
     */
    private Integer state;
    /**
     * 设施id(唯一和设施绑定 )
     */
    private String deviceId;
    /**
     * 设施类型 TemplateDeviceTypeEnum
     */
    private Integer deviceType;
    /**
     * 高度
     */
    private Double height;
    /**
     * 宽度
     */
    private Double width;

    /**
     * 上级箱id (用于统计)
     */
    private String boxId;
    /**
     * 上级框id(用于统计)
     */
    private String frameId;
    /**
     * 端口所属盘 编号
     */
    private Integer discNum;
    /**
     * 上级盘id(用于统计)
     */
    private String discId;
    /**
     * 横放/竖放 1/0  BoardPutStateEnum 只用于盘
     */
    private Integer putState;
    /**
     * 端口状态  PortSateEnum
     */
    private Integer portState;
    /**
     * 业务绑定状态 0-0-0(默认 成端-跳接-端口标签)
     */
    private String busBindingState;
    /**
     * 业务状态 0/1 没有业务 1 有业务绑定
     */
    private Integer busState;
    /**
     * 成端端口状态 0/1 (空闲/占用)
     */
    private Integer portCableState;
    /**
     * 子集
     */
    List<RealPositionRspDto> childList;
}
