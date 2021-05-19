package com.fiberhome.filink.rfid.bean.template;

import lombok.Data;

/**
 * 实景图坐标实体
 *
 * @author liyj
 * @date 2019/5/23
 */
@Data
public class RealPosition {
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
     * 走向 不需要存走向
     * 走向和编号规则仅仅只是计算出业务编号而已
     */
    private Integer trend;
    /**
     * A/B面 templateSideEnum
     */
    private Integer side;
    /**
     * 列 计算坐标需要用到
     */
    private Integer col;
    /**
     * 行 计算坐标需要用到
     */
    private Integer row;
    /**
     * TemplateCodeRuleEnum
     * 编号规则
     */
    private Integer codeRule;
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
     * 关系id 用于关联和模板的关系
     */
    private String relationId;
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
     * 构造函数
     * A/B面
     *
     * @param abscissa 横坐标
     * @param ordinate 纵坐标
     */
    public RealPosition(Double abscissa, Double ordinate, Integer deviceType) {
        this.abscissa = abscissa;
        this.ordinate = ordinate;
        this.deviceType = deviceType;
    }

    /**
     * 箱坐标 构造函数
     *
     * @param id          主键id
     * @param colNo       列编号
     * @param rowNo       行编号
     * @param abscissa    queen X坐标
     * @param ordinate    queen Y坐标
     * @param businessNum 业务编号
     * @param deviceType  设施类型
     * @param height      高度
     * @param width       宽度
     */
    public RealPosition(String id, Integer colNo,
                        Integer rowNo, Double abscissa,
                        Double ordinate, Integer businessNum,
                        Integer deviceType,
                        Double height, Double width,
                        Integer trend, Integer codeRule,
                        Integer side, String relationId,
                        String parentId, String deviceId,
                        Integer state, Integer realNo) {
        this.id = id;
        this.parentId = parentId;
        this.colNo = colNo;
        this.rowNo = rowNo;
        this.abscissa = abscissa;
        this.ordinate = ordinate;
        this.businessNum = businessNum;
        this.deviceType = deviceType;
        this.height = height;
        this.width = width;
        this.trend = trend;
        this.codeRule = codeRule;
        this.side = side;
        this.relationId = relationId;
        this.deviceId = deviceId;
        this.state = state;
        this.realNo = realNo;
    }

    /**
     * 无参构造方法
     */
    public RealPosition() {
    }
}
