package com.fiberhome.filink.fdevice.dao.device;

import com.fiberhome.filink.fdevice.bean.device.RetryDeleteInfo;

import java.util.List;

/**
 * @Author: qiqizhu@wistronits.com
 * Date:2019/6/14
 */
public interface RetryDeleteInfoDao {
    /**
     * 查找所有的需要重试的删除信息
     *
     * @return
     */
    List<RetryDeleteInfo> selectAllRetryDeleteInfo();

    /**
     * 根据id 更新
     * @param retryDeleteInfo
     */
    void updateRetryNumById(RetryDeleteInfo retryDeleteInfo);
     /**
     * 根据id 删除
     * @param deleteId id
     */
    void deleteById(String deleteId);

    /**
     * 新增
     * @param retryDeleteInfo
     */
    void insert(RetryDeleteInfo retryDeleteInfo);
}
