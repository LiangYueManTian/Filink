package com.fiberhome.filink.license.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.bean.LicenseInfo;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.exception.FilinkLicenseException;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gzp
 * @since 2019-02-19
 */
public interface LicenseInfoService extends IService<LicenseInfo> {

    /**
     * 上传license
     *
     * @param licenseXml
     * @return
     * @throws Exception
     */
    Result uploadLicense(MultipartFile licenseXml) throws Exception;

    /**
     * 获取license详情
     *
     * @return
     * @throws Exception
     */
    Result getLicenseDetail() throws Exception;

    /**
     * 从Redis和数据库中获取当前License信息
     *
     * @return 当前License信息，不包含LicenseThreshold
     * @throws FilinkLicenseException
     */
    License getCurrentLicense() throws FilinkLicenseException;

    /**
     * 校验时间
     *
     * @return
     * @throws Exception
     */
    boolean validateLicenseTime() throws Exception;

    /**
     * 修改用户，在线用户，设施统一方法
     *
     * @param licenseFeignBean
     * @return
     * @throws Exception
     */
    boolean updateRedisLicenseThreshold(LicenseFeignBean licenseFeignBean) throws Exception;

    /**
     * 同步LicenseThreshold
     *
     * @param licenseFeignBean
     * @return
     */
    boolean synchronousLicenseThreshold(LicenseThresholdFeignBean licenseFeignBean);
}
