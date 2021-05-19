package com.fiberhome.filink.rfid.constant;

/**
 * 公共常量
 *
 * @author chaofanrong
 * @date 2019/6/25
 */
public class CommonConstant {

    /*----------------设施类型----------------*/
    /** 光交箱*/
    public static final String DEVICE_TYPE_001 = "001";
    /** 人井*/
    public static final String DEVICE_TYPE_030 = "030";
    /** 配线架*/
    public static final String DEVICE_TYPE_060 = "060";
    /** 接头盒*/
    public static final String DEVICE_TYPE_090 = "090";
    /** 分纤箱*/
    public static final String DEVICE_TYPE_150 = "150";
    /*----------------设施类型----------------*/

    /**----------------------------端口状态----------------------------*/
    /**端口状态(预占用)*/
    public static final String PORT_STATUS_PRE_OCCUPY = "0";
    /**端口状态(占用)*/
    public static final String PORT_STATUS_OCCUPY = "1";
    /**端口状态(空闲)*/
    public static final String PORT_STATUS_FREE = "2";
    /**端口状态(异常)*/
    public static final String PORT_STATUS_EXCEPTION = "3";
    /**端口状态(虚占)*/
    public static final String PORT_STATUS_VIRTUAL_OCCUPY = "4";
    /**----------------------------端口状态----------------------------*/

    /**----------------------------适配器类型----------------------------*/
    /**适配器类型(FC)*/
    public static final Integer ADAPTER_TYPE_FC = 0;
    /**适配器类型(SC)*/
    public static final Integer ADAPTER_TYPE_SC = 1;
    /**----------------------------适配器类型----------------------------*/

    /*----------------------------标签类型----------------------------*/
    /**标签类型(rfid)*/
    public static final String RFID_TYPE_RFID = "0";
    /**标签类型(二维码)*/
    public static final String RFID_TYPE_QR_CODE = "1";
    /*----------------------------标签类型----------------------------*/

    /*----------------------------标签状态----------------------------*/
    /**标签状态(正常)*/
    public static final String RFID_STATUS_NORMAL = "0";
    /**标签状态(异常)*/
    public static final String RFID_STATUS_ABNORMAL = "1";
    /*----------------------------标签类型----------------------------*/

    /*----------------------------导出参数----------------------------*/
    /**导出*/
    public static final String EXPORT = "export";
    /**列名*/
    public static final String LIST_NAME = "listName";
    /*----------------------------标签类型----------------------------*/

}
