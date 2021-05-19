package com.fiberhome.filink.logserver.req;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.logserver.bean.OperateLog;
import com.fiberhome.filink.logserver.constant.LogConstants;
import com.fiberhome.filink.logserver.utils.LogParamValidate;
import com.fiberhome.filink.userapi.bean.User;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author hedongwei@wistronits.com
 * description 新增操作日志参数
 * date 2019/1/21 12:36
 */
@Data
public class AddOperateLogReq extends OperateLog {
    /**
     * 是否添加本地文件  0 添加  1 不添加
     */
    private String addLocalFile;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 危险级别编码
     */
    private String functionCode;


    /**
     * 校验app操作日志新增参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 12:08
     * @param operateLogReq app操作日志新增参数
     * @return 返回操作日志新增结果
     */
    public static Result checkAppOperateLogReq(AddOperateLogReq operateLogReq) {
        String optName = operateLogReq.getOptName();
        Result optNameResult = LogParamValidate.checkOptName(optName);
        if (null != optNameResult) {
            return optNameResult;
        }

        String userCode = operateLogReq.getOptUserCode();
        Result userCodeResult = LogParamValidate.checkOptUserCode(userCode);
        if (null != userCodeResult) {
            return userCodeResult;
        }

        Long optTime = operateLogReq.getOptTime();
        Result optTimeResult = LogParamValidate.checkOptTime(optTime);
        if (null != optTimeResult) {
            return optTimeResult;
        }

        String optTerminal = operateLogReq.getOptTerminal();
        Result optTerminalResult = LogParamValidate.checkOptTerminal(optTerminal);
        if (null != optTerminalResult) {
            return optTerminalResult;
        }

        String optObj = operateLogReq.getOptObj();
        Result optObjResult = LogParamValidate.checkOptObj(optObj);
        if (null != optObjResult) {
            return optObjResult;
        }

        String optResult = operateLogReq.getOptResult();
        Result optResultInfo = LogParamValidate.checkOptResult(optResult);
        if (null != optResultInfo) {
            return optResultInfo;
        }

        String detailInfo = operateLogReq.getDetailInfo();
        Result detailInfoResult = LogParamValidate.checkDetailInfo(detailInfo);
        if (null != detailInfoResult) {
            return detailInfoResult;
        }

        Integer dangerLevel = operateLogReq.getDangerLevel();
        Result dangerLevelResult = LogParamValidate.checkDangerLevel(dangerLevel);
        if (null != dangerLevelResult) {
            return dangerLevelResult;
        }

        return null;
    }

    /**
     * 生成app操作日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 9:32
     * @param operateLogReq 新增app操作日志参数
     * @return 生成app操作日志参数
     */
    public static AddOperateLogReq generateAppOperateLogReq(AddOperateLogReq operateLogReq, User user) {
        String uuid = NineteenUUIDUtils.uuid();
        operateLogReq.setLogId(uuid);
        operateLogReq.setOptResult(LogConstants.OPT_RESULT_SUCCESS);
        //pda操作
        operateLogReq.setOptType(LogConstants.OPT_TYPE_PDA);
        //创建时间
        operateLogReq.setCreateTime(operateLogReq.getOptTime());
        if (!StringUtils.isEmpty(operateLogReq.getOptUserCode())) {
            //用户编号
            String userId = operateLogReq.getOptUserCode();
            //创建用户
            operateLogReq.setCreateUser(userId);
            //操作用户不能为空

            //用户名称
            operateLogReq.setOptUserName(user.getUserName());
            //角色编号
            operateLogReq.setOptUserRole(user.getRoleId());
            //角色名称
            operateLogReq.setOptUserRoleName(user.getRole().getRoleName());
        }
        return operateLogReq;
    }
}
