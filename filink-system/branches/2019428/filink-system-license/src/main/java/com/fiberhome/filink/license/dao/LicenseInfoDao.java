package com.fiberhome.filink.license.dao;

import com.fiberhome.filink.license.bean.LicenseInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.sql.Timestamp;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gzp
 * @since 2019-02-19
 */
public interface LicenseInfoDao extends BaseMapper<LicenseInfo> {

	LicenseInfo findDefaultLicense();

	LicenseInfo findNonDefaultLicense();
}
