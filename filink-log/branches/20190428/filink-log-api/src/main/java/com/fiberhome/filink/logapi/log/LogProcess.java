package com.fiberhome.filink.logapi.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.clientcommon.utils.FileInfoUtils;
import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.logapi.api.LogFeign;
import com.fiberhome.filink.logapi.bean.AddLogAspectBean;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.LoginUserBean;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.logapi.req.AddSecurityLogReq;
import com.fiberhome.filink.logapi.req.AddSystemLogReq;
import com.fiberhome.filink.logapi.constant.I18nConstants;
import com.fiberhome.filink.logapi.utils.IpAddressUtil;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.constant.RequestHeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * description 日志处理类
 * date 11:36 2019/1/22
 */
@Component
@Slf4j
@RefreshScope
public class LogProcess {

    @Autowired
    private LogFeign logFeign;

    private String language;

    @Autowired
    private LogCastProcess logCastProcess;

    public static LogProcess logProcess;

    /**
     * @Author hedongwei@wistronits.com
     * @Description 初始化数据
     * @Date 10:15 2019/1/22
     * @Param []
     */
    @PostConstruct
    public void init() {
        logProcess = this;
        logProcess.language = this.logCastProcess.getLanguage();
    }



    @Value("${readLogFilePath}")
    private String logFilePath;

    private final Logger logger = LoggerFactory.getLogger(LogProcess.class);

    /**
     * @author hedongwei@wistronits.com
     * description 新增操作日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addOperateLogInfo(JSONObject logInfo, String addLocalFile) {
        Result result = null;
        AddOperateLogReq operateLogReq = JSONObject.toJavaObject(logInfo, AddOperateLogReq.class);
        //操作日志信息
        operateLogReq.setAddLocalFile(addLocalFile);
        result = logFeign.addOperateLog(operateLogReq);
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 调用批量新增操作日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addOperateLogBatchInfoToCall(List<AddLogBean> logInfo, String addLocalFile) {
        Result result = null;
        if (0 < logInfo.size()) {
            for (AddLogBean logOne : logInfo) {
                try {
                    result = this.addOperateLogInfoToCallProcess(logOne, addLocalFile);
                } catch (Exception e) {
                    logger.error("手动新增操作日志异常", e);
                }
            }
        }
        return result;
    }
    /**
     * @author hedongwei@wistronits.com
     * description 调用新增操作日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addOperateLogInfoToCall(AddLogBean logInfo, String addLocalFile) {
        Result result = null;
        if (null != logInfo) {
            try {
                this.addOperateLogInfoToCallProcess(logInfo, addLocalFile);
            } catch (Exception e) {
                logger.error("手动新增操作日志异常", e);
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 直接调用批量新增操作日志信息
     * date 16:10 2019/1/21
     * param [logInfo, addLocalFile]
     */
    public Result addOperateLogBatchInfoToAutoCall(List<AddOperateLogReq> logInfo, String addLocalFile) {
        Result result = null;
        if (0 < logInfo.size()) {
            for (AddOperateLogReq logOne : logInfo) {
                try {
                    //新增操作日志
                    result = logFeign.addOperateLog(logOne);
                } catch (Exception e) {
                    logger.error("手动新增操作日志异常,日志id为：" + logOne.getLogId(), e);
                }
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 直接调用新增操作日志信息
     * date 16:10 2019/1/21
     * param [logInfo, addLocalFile]
     */
    public Result addOperateLogInfoToAutoCall(AddOperateLogReq logInfo, String addLocalFile) {
        Result result = null;
        if (null != logInfo) {
            try {
                //新增操作日志
                result = logFeign.addOperateLog(logInfo);
            } catch (Exception e) {
                logger.error("手动新增操作日志异常,日志id为：" + logInfo.getLogId(), e);
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 新增操作日志调用过程
     * date 10:23 2019/1/29
     * param [logInfo, addLocalFile]
     */
    public Result addOperateLogInfoToCallProcess(AddLogBean logInfo, String addLocalFile) throws Exception{
        Result result = null;
        //操作日志信息
        AddOperateLogReq addOperateLogOne = logCastProcess.castInfoToAddOperateLog(logInfo);
        addOperateLogOne.setAddLocalFile(addLocalFile);
        result = logFeign.addOperateLog(addOperateLogOne);
        return result;
    }



    /**
     * @author hedongwei@wistronits.com
     * description 新增安全日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSecurityLogInfo(JSONObject logInfo, String addLocalFile) {
        Result result = null;
        AddSecurityLogReq addSecurityLogReq = JSONObject.toJavaObject(logInfo, AddSecurityLogReq.class);
        //安全日志信息
        addSecurityLogReq.setAddLocalFile(addLocalFile);
        result = logFeign.addSecurityLog(addSecurityLogReq);
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 调用批量新增安全日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSecurityLogBatchInfoToCall(List<AddLogBean> logInfo, String addLocalFile) {
        Result result = null;
        if (0 < logInfo.size()) {
            for (AddLogBean logOne : logInfo) {
                try {
                    result = this.addSecurityLogInfoToCallProcess(logOne, addLocalFile);
                } catch (Exception e) {
                    logger.error("手动新增安全日志异常,日志id为：" + logOne.getLogId(), e);
                }
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 调用批量新增安全日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSecurityLogBatchInfoToAutoCall(List<AddSecurityLogReq> logInfo, String addLocalFile) {
        Result result = null;
        if (0 < logInfo.size()) {
            for (AddSecurityLogReq logOne : logInfo) {
                try {
                    //新增日志信息
                    result = logFeign.addSecurityLog(logOne);
                } catch (Exception e) {
                    logger.error("手动新增安全日志异常,日志id为：" + logOne.getLogId(), e);
                }
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 直接调用新增安全日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSecurityLogInfoToAutoCall(AddSecurityLogReq logInfo, String addLocalFile) {
        Result result = null;
        if (null != logInfo) {
            try {
                //新增日志信息
                result = logFeign.addSecurityLog(logInfo);
            } catch (Exception e) {
                logger.error("手动新增安全日志异常,日志id为：" + logInfo.getLogId(), e);
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 调用新增安全日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSecurityLogInfoToCall(AddLogBean logInfo, String addLocalFile) {
        Result result = null;
        if (null != logInfo) {
            try {
                this.addSecurityLogInfoToCallProcess(logInfo, addLocalFile);
            } catch (Exception e) {
                logger.error("手动新增安全日志异常,日志id为" + logInfo.getLogId(), e);
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 新增操作日志调用过程
     * date 10:23 2019/1/29
     * param [logInfo, addLocalFile]
     */
    public Result addSecurityLogInfoToCallProcess(AddLogBean logInfo, String addLocalFile) throws Exception{
        Result result = null;
        //操作日志信息
        AddSecurityLogReq addSecurityLogOne = logCastProcess.castInfoToAddSecurityLog(logInfo);
        addSecurityLogOne.setAddLocalFile(addLocalFile);
        result = logFeign.addSecurityLog(addSecurityLogOne);
        return result;
    }


    /**
     * @author hedongwei@wistronits.com
     * description 新增系统日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSystemLogInfo(JSONObject logInfo, String addLocalFile) {
        Result result = null;
        AddSystemLogReq systemLogReq = JSONObject.toJavaObject(logInfo, AddSystemLogReq.class);
        //操作日志信息
        systemLogReq.setAddLocalFile(addLocalFile);
        result = logFeign.addSystemLog(systemLogReq);
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 调用批量新增系统日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSystemLogBatchInfoToCall(List<AddLogBean> logInfo, String addLocalFile) {
        Result result = null;
        if (0 < logInfo.size()) {
            for (AddLogBean logOne : logInfo) {
                try {
                    result = this.addSystemLogInfoToCallProcess(logOne, addLocalFile);
                } catch (Exception e) {
                    logger.error("手动新增系统日志异常,日志id为：" + logOne.getLogId(), e);
                }
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 直接调用批量新增系统日志信息
     * date 16:10 2019/1/21
     * param [logInfo, addLocalFile]
     */
    public Result addSystemLogBatchInfoToAutoCall(List<AddSystemLogReq> logInfo, String addLocalFile) {
        Result result = null;
        if (0 < logInfo.size()) {
            for (AddSystemLogReq logOne : logInfo) {
                try {
                    //新增系统日志
                    result = logFeign.addSystemLog(logOne);
                } catch (Exception e) {
                    logger.error("手动新增系统日志异常,日志id为：" + logOne.getLogId(), e);
                }
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 直接调用批量新增系统日志信息
     * date 16:10 2019/1/21
     * param [logInfo, addLocalFile]
     */
    public Result addSystemLogInfoToAutoCall(AddSystemLogReq logInfo, String addLocalFile) {
        Result result = null;
        if (null != logInfo) {
            try {
                //新增系统日志
                result = logFeign.addSystemLog(logInfo);
            } catch (Exception e) {
                logger.error("手动新增系统日志异常,日志id为：" + logInfo.getLogId(), e);
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 调用新增系统日志信息
     * date 16:10 2019/1/21
     * param [logInfo]
     */
    public Result addSystemLogInfoToCall(AddLogBean logInfo, String addLocalFile) {
        Result result = null;
        if (null != logInfo) {
            try {
                this.addSystemLogInfoToCallProcess(logInfo, addLocalFile);
            } catch (Exception e) {
                logger.error("手动新增系统日志异常,日志id为" + logInfo.getLogId(), e);
            }
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 新增系统日志调用过程
     * date 10:23 2019/1/29
     * param [logInfo, addLocalFile]
     */
    public Result addSystemLogInfoToCallProcess(AddLogBean logInfo, String addLocalFile) throws Exception{
        Result result = null;
        //操作日志信息
        AddSystemLogReq addSystemLogOne = logCastProcess.castInfoToAddSystemLog(logInfo);
        addSystemLogOne.setAddLocalFile(addLocalFile);
        result = logFeign.addSystemLog(addSystemLogOne);
        return result;
    }


    /**
     * @author hedongwei@wistronits.com
     * description 根据logType生成表名
     * date 13:50 2019/1/25
     * param [logType]
     */
    public String generateTableNameForLogType(String logType) {
        //表名
        String tableName = "";
        if (LogConstants.LOG_TYPE_OPERATE.equals(logType)) {
            //调用不同的方法
            tableName = LogConstants.OPERATE_LOG_TABLE_NAME;
        } else if (LogConstants.LOG_TYPE_SECURITY.equals(logType)) {
            //调用不同的方法
            tableName = LogConstants.SECURITY_LOG_TABLE_NAME;
        } else if (LogConstants.LOG_TYPE_SYSTEM.equals(logType)) {
            //调用不同的方法
            tableName = LogConstants.SYSTEM_LOG_TABLE_NAME;
        }
        return tableName;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 根据日志类型新增日志
     * date 19:56 2019/1/24
     * param [addLogParamInfo, tableName]
     */
    public Result addLogInfoForLogType(JSONObject addLogParamInfo, String tableName) {
        Result result = null;
        if (LogConstants.OPERATE_LOG_TABLE_NAME.equals(tableName)) {
            //添加操作日志
            //需要在日志失败后新增本地日志文件
            result = this.addOperateLogInfo(addLogParamInfo, LogConstants.ADD_LOG_LOCAL_FILE);
        } else if (LogConstants.SECURITY_LOG_TABLE_NAME.equals(tableName)) {
            //添加安全日志
            //需要在日志失败后新增本地日志文件
            result = this.addSecurityLogInfo(addLogParamInfo, LogConstants.ADD_LOG_LOCAL_FILE);
        } else if (LogConstants.SYSTEM_LOG_TABLE_NAME.equals(tableName)) {
            //添加系统日志
            //需要在日志失败后新增本地日志文件
            result = this.addSystemLogInfo(addLogParamInfo, LogConstants.ADD_LOG_LOCAL_FILE);
        }
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 调用新增日志失败
     * date 17:04 2019/1/28
     * param [result, logId, addLocalFile, systemLogReq]
     */
    public void callAddLogError(Result result, String logId, String addLocalFile, Object systemLogReq) {
        if (null != result) {
            if (0 != result.getCode() && LogConstants.ADD_LOG_LOCAL_FILE.equals(addLocalFile)) {
                //获得文件类型
                String fileType = LogConstants.LOG_LOCAL_FILE_TYPE;
                //新增文件信息
                try {
                    //新增本地文件
                    this.addLocalLogFile(logId, addLocalFile, systemLogReq, fileType);
                } catch (Exception e) {
                    logger.error("日志文件新增本地文件失败", e);
                }
            }
        }
    }


    /**
     * @author hedongwei@wistronits.com
     * description 同步本地日志文件到日志服务
     * date 9:55 2019/1/23
     * param [path]
     **/
    public void uploadLocalLogInfoToLogService(String path) {
        //获取文件夹下的所有文件名
        List<String> list = FileInfoUtils.getFiles(path);
        if (null != list) {
            if (0 < list.size()) {
                //遍历文件名集合
                for (String listOne: list) {
                    try {
                        //解析文件信息
                        JSONObject logInfo = FileInfoUtils.readTxt(listOne, path);
                        Result result = null;
                        //获得文件名称
                        String fileName = listOne;
                        String tableName = logInfo.getString(LogConstants.LOG_INFO_TABLE_NAME_ATTR);
                        if (LogConstants.OPERATE_LOG_TABLE_NAME.equals(tableName)) {
                            //新增操作日志
                            result = this.addOperateLogInfo(logInfo, LogConstants.NOT_ADD_LOG_LOCAL_FILE);
                        } else if (LogConstants.SECURITY_LOG_TABLE_NAME.equals(tableName)) {
                            //新增安全日志
                            result = this.addSecurityLogInfo(logInfo, LogConstants.NOT_ADD_LOG_LOCAL_FILE);
                        } else if (LogConstants.SYSTEM_LOG_TABLE_NAME.equals(tableName)) {
                            //新增系统日志
                            result = this.addSystemLogInfo(logInfo, LogConstants.NOT_ADD_LOG_LOCAL_FILE);
                        }
                        if (0 == result.getCode()) {
                            //成功后删除文件
                            FileInfoUtils.deleteFile(fileName, path);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
    }


    /**
     * @author hedongwei@wistronits.com
     * 日志新增失败需要新增本地日志文件
     * 14:04 2019/1/21
     * param [logId, addLocalFile, data]
     */
    public void addLocalLogFile(String logId, String addLocalFile, Object data, String type) throws Exception{
        if (null != logId && LogConstants.ADD_LOG_LOCAL_FILE.equals(addLocalFile)) {
            //文件名称
            String fileName = logId + "." + type;
            //将日志实体转换成jsonString
            String jsonString = JSON.toJSONString(data, SerializerFeature.WRITE_MAP_NULL_FEATURES);
            FileInfoUtils.generateFileInfo(fileName, jsonString, logFilePath);
        }
    }

    /**
     * @author hedongwei@wistronits.com
     * description 生成新增日志拦截类
     * date 9:41 2019/1/28
     * param []
     */
    public static AddLogAspectBean generateAddLogAspectBean() {
        AddLogAspectBean addLogAspectBean = new AddLogAspectBean();
        //获得uuid
        addLogAspectBean.setLogId(NineteenUUIDUtils.uuid());

        //获取登录用户信息
        LoginUserBean loginUserBean = LogProcess.getLoginUserBeanInfo();

        //用户角色编号
        addLogAspectBean.setOptUserRole(loginUserBean.getRoleId());
        //操作用户编号
        addLogAspectBean.setOptUserCode(loginUserBean.getUserId());
        //创建用户编号
        addLogAspectBean.setCreateUser(loginUserBean.getUserId());
        //角色名称
        addLogAspectBean.setOptUserRoleName(loginUserBean.getRoleName());
        //用户名称
        addLogAspectBean.setOptUserName(loginUserBean.getUserName());

        //操作类型
        //操作类型为web
        addLogAspectBean.setOptType(LogConstants.OPT_TYPE_WEB);

        //获取登录的ip
        //用户登录ip
        String ipAdrress = LogProcess.getIpAddress();
        addLogAspectBean.setOptTerminal(ipAdrress);

        //新增操作时间
        Long nowTime = System.currentTimeMillis();
        //操作时间
        addLogAspectBean.setOptTime(nowTime);
        //创建时间
        addLogAspectBean.setCreateTime(nowTime);

        return addLogAspectBean;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 生成新增日志参数
     * date 9:41 2019/1/28
     * param [logType]
     */
    public AddLogBean generateAddLogToCallParam(String logType) {
        AddLogBean addLogBean = new AddLogBean();
        AddLogAspectBean addLogAspectBean = LogProcess.generateAddLogAspectBean();
        BeanUtils.copyProperties(addLogAspectBean, addLogBean);

        //获得表名
        String tableName = this.generateTableNameForLogType(logType);

        //新增表名
        addLogBean.setTableName(tableName);

        return addLogBean;
    }

    /**
     * 获取ip地址
     * @author hedongwei@wistronits.com
     * @date 2019/2/12
     */
    public static String getIpAddress() {
        String ipAddress = "";
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            ipAddress = IpAddressUtil.getIpAddress(request);
        } else {
            //此处获取本地ip
            try {
                InetAddress address = InetAddress.getLocalHost();
                return address.getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ipAddress;
    }

    /**
     * 获取请求头属性值
     * @author hedongwei@wistronits.com
     * @date 2019/2/12
     * @param paramName 获取的名称
     * @return paramValue 获取的属性值
     */
    public static String getHeadParam(String paramName) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (!ObjectUtils.isEmpty(request)) {
                String paramValue = request.getHeader(paramName);
                if (null == paramValue) {
                    return "";
                } else {
                    //参数名为userName和roleName时需要解码
                    if (RequestHeaderConstants.PARAM_USER_NAME.equals(paramName)
                            || RequestHeaderConstants.PARAM_ROLE_NAME.equals(paramName)) {
                        if (!StringUtils.isEmpty(paramValue)) {
                            paramValue = LogProcess.encodeAsString(paramValue);
                        }
                    }
                    return paramValue;
                }
            }
        }
        return "";
    }

    public static void main(String [] args){
        LogProcess.getHeadParam("tokenId");
    }

    /**
     * 解码头部参数
     * @author hedongwei@wistronits.com
     * @date  2019/2/21 11:44
     * @param paramValue 编码参数
     */
    private static String encodeAsString(String paramValue) {
        try {
            Base64 base64 = new Base64();
            //解码
            paramValue = new String(base64.decode(paramValue),  LogConstants.DECODE_BASE64_CHARSET_UTF8);
        } catch (Exception e) {
            log.error("get log request head cast error", e);
            return paramValue;
        }
        return paramValue;
    }
    /**
     * 获取登录用户信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/12
     * @return LoginUserBean 返回登录用户信息
     */
    public static LoginUserBean getLoginUserBeanInfo() {
        //角色名称
        String roleName = "";
        //用户名称
        String userName = "";
        //用户编号
        String userId = "";
        //用户角色编号
        String roleId = "";
        //获取token
        String token = LogProcess.getHeadParam(RequestHeaderConstants.PARAM_TOKEN);
        if (StringUtils.isEmpty(token)) {
            String language = LogProcess.logProcess.language;
            if (I18nConstants.LANGUAGE_CN.equals(language)) {
                //角色名称
                roleName = LogConstants.DEFAULT_ROLE_NAME_CN;
                //用户名称
                userName = LogConstants.DEFAULT_USER_NAME_CN;
            } else if (I18nConstants.LANGUAGE_US.equals(language)) {
                //角色名称
                roleName = LogConstants.DEFAULT_ROLE_NAME_US;
                //用户名称
                userName = LogConstants.DEFAULT_USER_NAME_US;
            }
            //用户角色编号
            roleId = LogConstants.DEFAULT_ROLE_ID;
            //用户编号
            userId = LogConstants.DEFAULT_USER_ID;

        } else {
            //用户角色编号
            roleId = LogProcess.getHeadParam(RequestHeaderConstants.PARAM_ROLE_ID);
            //操作用户角色名称
            roleName = LogProcess.getHeadParam(RequestHeaderConstants.PARAM_ROLE_NAME);
            //获取用户编号
            userId = LogProcess.getHeadParam(RequestHeaderConstants.PARAM_USER_ID);
            //用户名称
            userName = LogProcess.getHeadParam(RequestHeaderConstants.PARAM_USER_NAME);
        }
        LoginUserBean retLoginUser = new LoginUserBean();
        retLoginUser.setRoleId(roleId);
        retLoginUser.setRoleName(roleName);
        retLoginUser.setUserId(userId);
        retLoginUser.setUserName(userName);
        return retLoginUser;
    }
}
