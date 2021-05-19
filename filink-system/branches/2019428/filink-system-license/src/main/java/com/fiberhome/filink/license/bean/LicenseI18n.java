package com.fiberhome.filink.license.bean;

/**
 * License 国际化实体类
 * @Author: zl
 * @Date: 2019/3/29 10:40
 * @Description: com.fiberhome.filink.license.bean
 * @version: 1.0
 */
public class LicenseI18n {
    /**
     * 文件不合法
     */
    public static final String ILLEGAL_LICENSE_FILE = "illegalLicenseFile";

    /**
     * 保存Redis出错
     */
    public static final String ERROR_SAVING_LICENSE_TO_REDIS = "errorSavingLicenseToRedis";

    /**
     * License上传成功
     */
    public static final String LICENSE_SUCCESSFUL_UPLOADED = "licenseSuccessfulUploaded";

    /**
     * redis和FastDFS都没有license
     */
    public static final String LICENSE_NOT_FOUND = "licenseNotFound";

    /**
     * 文件服务器中获取文件出错
     */
    public static final String ERROR_GETTING_LICENSE_FILE = "errorGettingLicenseFile";

    /**
     * 解析文件出错
     */
    public static final String ERROR_ANALYZING_LICENSE_FILE = "errorAnalyzingLicenseFile";

    /**
     *操作类型参数错误
     */
    public static final String LICENSE_OPERATION_TYPE_ERROR = "licenseOperationTypeError";

    /**
     * Redis中未获取到值
     */
    public static final String MISSING_VALUE_FROM_REDIS = "MissingValueFromRedis";

    /**
     * Redis中获取数值类型错误
     */
    public static final String ERROR_PARAM_FORMAT_IN_REDIS = "ErrorParamFormatInRedis";

    /**
     * 获取Redis锁失败
     */
    public static final String ERROR_GETTING_REDIS_LOCK = "ErrorGettingRedisLock";

    /**
     * License文件超长
     */
    public static final String LICENSE_FILE_TOO_LARGE = "licenseFileTooLarge";
}
