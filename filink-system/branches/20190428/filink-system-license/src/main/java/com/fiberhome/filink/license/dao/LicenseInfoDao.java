package com.fiberhome.filink.license.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.license.bean.LicenseInfo;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author gzp
 * @since 2019-02-19
 */
public interface LicenseInfoDao extends BaseMapper<LicenseInfo> {

    /**
     * 查询默认License信息
     *
     * @return
     */
    LicenseInfo findDefaultLicense();

    /**
     * 查询非默认License信息
     *
     * @return
     */
    LicenseInfo findNonDefaultLicense();
}
