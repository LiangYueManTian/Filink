package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 箱架信息实体
 */
@Data
public class BoxInfoBean extends BaseInfoBean {

    // 制造商
    private String producer;

    // 配线架专有属性 //
    // 设施形态(光纤配线架、中间配线架、总配线架)
    private Integer deviceForm;
    // 机架行号
    //2019-07-09 烽火张智经理确认  由int修改为string
    private String lineNum;
    // 机架列号
    private String columnNum;
    // 配线架专有属性 end//

    // 光交箱、分纤箱、接头盒专有属性 //
    // 安装方式(光交箱：落地、架空；分纤箱与接头盒：落地、壁挂、抱杆)
    private Integer installationMode;
    // 光交箱、分纤箱、接头盒专有属性 end//

    // 接头盒专有属性 //
    // 最大纤芯数
    private Integer maxFiberNum;
    // 密封方式(热缩、机械)
    private Integer sealMode;
    // 敷设方式(人井、管道、架空、直埋、壁挂、托架、手孔、人孔)
    private Integer layMode;
    // 规格说明(两进两出、三进三出、四进四出)
    private Integer standard;
    // 接续信息(直通、分歧)
    private Integer follow;
    // 接头盒专有属性 end//

    /**
     * 设备类型 帽式 卧式
     */
    private Integer apparatusType;

    /**
     * 设施类型
     */
    private String deviceType;
}
