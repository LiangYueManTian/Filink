package com.fiberhome.filink.rfid.controller;

/**
 * AddDataService
 * todo: 测试代码 造千万脏数据
 * @author congcongsun2@wistronits.com
 * @since 2019/7/10
 */
public interface AddDataService {
    /**
     * 添加成端
     */
    void addData();

    /**
     * 添加跳纤
     */
    void addDataByJump();

    /**
     * 添加熔纤
     */
    void  addDataByCore();
}
