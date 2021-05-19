package com.fiberhome.filink.rfid.service.impl.opticcable;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.facility.BaseInfoBean;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.constant.*;
import com.fiberhome.filink.rfid.constant.opticcable.OpticCableSectionConstant;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.rfid.OpticCableSectionRfidInfoDao;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.exception.FilinkObtainUserInfoException;
import com.fiberhome.filink.rfid.exception.FilinkUserPermissionException;
import com.fiberhome.filink.rfid.export.opticcablesection.OpticCableSectionExport;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.app.OperatorOpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.opticcable.app.OpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.resp.fibercore.CoreCoreInfoResp;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;
import com.fiberhome.filink.rfid.resp.opticcable.app.OpticCableSectionInfoRespForApp;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.rfid.utils.export.ExportLogServer;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Future;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;

/**
 * <p>
 * 光缆段信息表 服务实现类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
@Service
@Slf4j
public class OpticCableSectionInfoServiceImpl extends ServiceImpl<OpticCableSectionInfoDao, OpticCableSectionInfo> implements OpticCableSectionInfoService {

    /**
     * 光缆段信息表 Mapper 接口
     */
    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
    /**
     * 光缆信息表 Mapper 接口
     */
    @Autowired
    private OpticCableInfoDao opticCableInfoDao;
    /**
     * 模版信息表 Mapper 接口
     */
    @Autowired
    private TemplateDao templateDao;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * 设备api
     */
    @Autowired
    private DeviceFeign deviceFeign;
    /**
     * 区域Feign
     */
    @Autowired
    private AreaFeign areaFeign;

    /**
     * 用户Feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 远程调用SystemLanguage服务
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 注入光缆段列表导出类
     */
    @Autowired
    @Lazy
    private OpticCableSectionExport opticCableSectionExport;

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * 导出服务名
     */
    @Value("${exportServerName}")
    private String exportServerName;

    /**
     * 熔纤信息表 Mapper 接口
     */
    @Autowired
    private CoreCoreInfoDao coreCoreInfoDao;

    /**
     * 成端信息表 Mapper 接口
     */
    @Autowired
    private PortCableCoreInfoDao portCableCoreInfoDao;

    /**
     * 光缆段gis信息表 Mapper 接口
     */
    @Autowired
    private OpticCableSectionRfidInfoDao opticCableSectionRfidInfoDao;


    /**
     * 查询光缆段列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result selectOpticCableSection(QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition
                .getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);

        //增加区域权限
        queryCondition = this.getOpticCableSectionInfoReq(queryCondition, RequestInfoUtils.getUserId());

        List<OpticCableSectionInfoResp> list = opticCableSectionInfoDao
                .selectOpticCableSection(queryCondition);

        //组装光缆段信息
        this.assemblyOpticCableSectionInfoResp(list);

        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        PageBean pageBean = myBatiesBuildPageBean(page,
                opticCableSectionInfoDao.opticCableSectionByIdTotal(queryCondition),
                list);
        return ResultUtils.pageSuccess(pageBean);
    }


    /**
     * 获取光缆段数据权限
     *
     * @author hedongwei@wistronits.com
     * @date 2019/7/30 13:25
     */
    public QueryCondition<OpticCableSectionInfoReq> getOpticCableSectionInfoReq(QueryCondition<OpticCableSectionInfoReq> queryCondition, String userId) {
        //查询用户信息
        if (ObjectUtils.isEmpty(userId)) {
            return queryCondition;
        }

        //获取权限信息
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        Object userObj = userFeign.queryUserByIdList(userIds);
        //校验是否有值
        if (ObjectUtils.isEmpty(userObj)) {
            throw new FilinkObtainUserInfoException();
        }
        List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userObj), User.class);
        User user = new User();
        //添加用户map
        if (!ObjectUtils.isEmpty(userInfoList)) {
            user = userInfoList.get(0);
        }

        //admin用户不用校验权限
        String adminUserId = CommonConstant.ADMIN_USER_ID;
        String loginUserId = userId;
        if (!ObjectUtils.isEmpty(loginUserId)) {
            //当前用户是admin时不用获取所有权限
            if (adminUserId.equals(loginUserId)) {
                return queryCondition;
            }
        }

        //用户管理区域
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //用户管理设施类型
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        //用户管理部门
        Department department = user.getDepartment();

        //当用户权限信息中有信息为空，直接返回页面空
        if (ObjectUtils.isEmpty(areaIds) || ObjectUtils.isEmpty(roleDeviceTypes)) {
            throw new FilinkUserPermissionException();
        }

        Set<String> deviceTypeSet = new HashSet<>();
        Set<String> areaIdSet = new HashSet<>();
        Set<String> departmentIdSet = new HashSet<>();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypeSet.add(roleDeviceType.getDeviceTypeId());
        }
        for (String areaId : areaIds) {
            areaIdSet.add(areaId);
        }
        departmentIdSet.add(department.getId());

        queryCondition.getBizCondition().setPermissionDeviceTypes(deviceTypeSet);
        queryCondition.getBizCondition().setPermissionAreaIds(areaIdSet);

        return queryCondition;
    }

    /**
     * 拓扑根据光缆id查询光缆段列表
     *
     * @param opticCableSectionInfoReq 查询光缆段请求
     * @return Result
     */
    @Override
    public Result opticCableSectionByIdForTopology(OpticCableSectionInfoReq opticCableSectionInfoReq) {

        //获取权限信息
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        queryCondition.setBizCondition(opticCableSectionInfoReq);
        opticCableSectionInfoReq = (OpticCableSectionInfoReq) this.getPermissionsInfo(queryCondition).getBizCondition();

        List<OpticCableSectionInfoResp> opticCableSectionInfoRespList = opticCableSectionInfoDao.opticCableSectionByIdForTopology(opticCableSectionInfoReq);

        //过滤设施类型
        Set<String> permissionDeviceTypes = opticCableSectionInfoReq.getPermissionDeviceTypes();
        for (OpticCableSectionInfoResp opticCableSectionInfoResp : opticCableSectionInfoRespList) {
            //超级用户
            if (!"1".equals(RequestInfoUtils.getUserId())) {
                if (!ObjectUtils.isEmpty(permissionDeviceTypes)) {
                    if (!permissionDeviceTypes.contains(opticCableSectionInfoResp.getStartNodeDeviceType())) {
                        opticCableSectionInfoResp.setStartNode(null);
                        opticCableSectionInfoResp.setStartNodeDeviceType(null);
                    }
                    if (!permissionDeviceTypes.contains(opticCableSectionInfoResp.getTerminationNodeDeviceType())) {
                        opticCableSectionInfoResp.setTerminationNode(null);
                        opticCableSectionInfoResp.setTerminationNodeDeviceType(null);
                    }
                }
            }
        }

        //清除起始及终止节点都为空的数据
        for (int i = 0; i < opticCableSectionInfoRespList.size(); i++) {
            OpticCableSectionInfoResp opticCableSectionInfoResp = opticCableSectionInfoRespList.get(i);
            if (StringUtils.isEmpty(opticCableSectionInfoResp.getStartNodeDeviceType()) && StringUtils.isEmpty(opticCableSectionInfoResp.getTerminationNodeDeviceType())) {
                opticCableSectionInfoRespList.remove(opticCableSectionInfoResp);
            }
        }
        return ResultUtils.success(opticCableSectionInfoRespList);
    }

    /**
     * 根据设备id查询光缆段
     * *
     *
     * @param deviceId 设备id
     * @return Result
     */
    @Override
    public Result selectOpticCableSectionByDeviceId(String deviceId) {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(deviceId);
        List<OpticCableSectionInfoResp> list = opticCableSectionInfoDao.opticCableSectionByDevice(deviceIds);
        if (Objects.isNull(list) || list.size() == 0) {
            return ResultUtils.success(list);
        }
        Map<String, String> deviceInfoMap = deviceNameByID(list);
        Map<String, String> areaInfoMap = areaNameByID(list);
        for (OpticCableSectionInfoResp resp : list) {
            resp.setStartNodeName(deviceInfoMap.get(resp.getStartNode()));
            resp.setTerminationNodeName(deviceInfoMap.get(resp.getTerminationNode()));
            resp.setAreaName(areaInfoMap.get(resp.getAreaId()));
        }
        return ResultUtils.success(list);
    }

    /**
     * 通过光缆段id删除光缆段
     *
     * @param opticCableSectionId 光缆段id
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteOpticCableSectionByOpticCableSectionId(String opticCableSectionId) {

        //获取光缆段
        OpticCableSectionInfo opticCableSectionInfo = new OpticCableSectionInfo();
        opticCableSectionInfo.setIsDeleted("0");
        opticCableSectionInfo.setOpticCableSectionId(opticCableSectionId);
        opticCableSectionInfo = opticCableSectionInfoDao.selectOne(opticCableSectionInfo);

        //校验光缆是否已被删除
        if (ObjectUtils.isEmpty(opticCableSectionInfo)) {
            return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_NOT_EXIST));
        }

        //熔纤信息
        CoreCoreInfoReq coreCoreInfoReq = new CoreCoreInfoReq();
        coreCoreInfoReq.setResource(opticCableSectionId);
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfo(coreCoreInfoReq);
        //成端信息
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        portCableCoreInfoReq.setOppositeResource(opticCableSectionId);
        List<PortCableCoreInfo> portCableCoreInfoList = portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        //有业务信息不允许删除
        if (!ObjectUtils.isEmpty(coreCoreInfoRespList) || !ObjectUtils.isEmpty(portCableCoreInfoList)) {
            return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_HAVE_SERVER, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_HAVE_SERVER));
        }

        //删除光缆段
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setOpticCableSectionId(opticCableSectionId);
        opticCableSectionInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        opticCableSectionInfoReq.setUpdateTime(UtcTimeUtil.getUtcTime());
        opticCableSectionInfoDao.deleteOpticCableSectionByOpticCableSectionId(opticCableSectionInfoReq);
        log.info("删除光缆段信息成功");

        //删除光缆段gis信息
        UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfIdInfoReqApp = new UploadOpticCableSectionRfidInfoReqApp();
        List<String> opticCableSectionIdList = new ArrayList<>();
        opticCableSectionIdList.add(opticCableSectionId);
        uploadOpticCableSectionRfIdInfoReqApp.setOpticCableSectionIdList(opticCableSectionIdList);
        uploadOpticCableSectionRfIdInfoReqApp.setUpdateUser(RequestInfoUtils.getUserId());
        uploadOpticCableSectionRfIdInfoReqApp.setUpdateTime(UtcTimeUtil.getUtcTime());
        opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(uploadOpticCableSectionRfIdInfoReqApp);
        log.info("删除光缆段gis信息成功");

        //保存操作日志
        this.saveOperatorLog(opticCableSectionInfo);
        log.info("删除光缆段操作日志成功");

        return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_OPTIC_CABLE_SECTION_SUCCESS));
    }

    /**
     * app请求光缆段基础信息
     *
     * @param opticCableSectionInfoReqForApp app光缆段请求类
     * @return Result
     */
    @Override
    public Result queryOpticCableSectionListForApp(OpticCableSectionInfoReqForApp opticCableSectionInfoReqForApp) {
        List<OpticCableSectionInfoRespForApp> list = opticCableSectionInfoDao
                .queryOpticCableSectionListForApp(opticCableSectionInfoReqForApp);
        Map<String, String> deviceInfoMap = deviceNameByID(list);
        Map<String, String> areaInfoMap = areaNameByID(list);
        Map<String, String> deviceRfIdMap = gainDeviceRfidById(list);
        for (OpticCableSectionInfoRespForApp resp : list) {
            resp.setAreaName(areaInfoMap.get(resp.getAreaId()));
            resp.setTerminationNodeName(deviceInfoMap.get(resp.getTerminationNode()));
            resp.setStartNodeName(deviceInfoMap.get(resp.getStartNode()));
            resp.setStartNodeLabel(deviceRfIdMap.get(resp.getStartNode()));
            resp.setTerminationNodeLabel(deviceRfIdMap.get(resp.getTerminationNode()));
        }
        return ResultUtils.success(list);
    }

    /**
     * app处理光缆段基础信息
     *
     * @param operatorOpticCableSectionInfoReqForApp app光缆段处理请求类
     * @return Result
     */
    @Override
    public Result uploadOpticCableSectionInfoForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp) {
        //当没有操作类型
        if (StringUtils.isEmpty(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //新增时的校验
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            //当起始节点和终止节点一样
            if (operatorOpticCableSectionInfoReqForApp.getStartNode().equals(operatorOpticCableSectionInfoReqForApp.getTerminationNode())) {
                return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
            /*校验光缆段的芯数不能大于光缆的芯数*/
            //获取光缆的纤芯数
            Integer coreNum = opticCableInfoDao.queryOpticCableByCoreNum(operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId());
            if (Objects.isNull(coreNum)) {
                return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_PARAM_ERROR,
                        I18nUtils.getSystemString(RfIdI18nConstant.BELONG_OPTIC_CABLE_ID_ERROR));
            }
            if (coreNum < Integer.valueOf(operatorOpticCableSectionInfoReqForApp.getCoreNum())) {
                return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_CORE_OVERRUN,
                        I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_CORE_OVERRUN));
            }
            //校验光缆段节点是否存在
            String flag = this.checkOpticCableSectionDeviceId(operatorOpticCableSectionInfoReqForApp);
            if (OpticCableSectionConstant.DEVICE_SERVER_ERROR.equals(flag)) {
                return ResultUtils.warn(RfIdResultCode.DEVICE_SERVER_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_SERVER_ERROR));
            } else if (OpticCableSectionConstant.DEVICE_NODE_NOT_EXISTS.equals(flag)) {
                return ResultUtils.warn(RfIdResultCode.DEVICE_NODE_NOT_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_NODE_NOT_EXISTS));
            }
            //校验光缆段名字是否存在
            if (this.checkOpticCableSectionNameForApp(operatorOpticCableSectionInfoReqForApp)) {
                return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_NAME_SAME));
            }
        }
        int total = 0;
        //新增
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            operatorOpticCableSectionInfoReqForApp.setOpticCableSectionId(NineteenUUIDUtils.uuid());
            operatorOpticCableSectionInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            operatorOpticCableSectionInfoReqForApp.setCreateTime(UtcTimeUtil.getUtcTime());
            operatorOpticCableSectionInfoReqForApp.setStatus(OpticCableSectionConstant.STATUS_UNUSED);
            total = opticCableSectionInfoDao.addOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp);
            log.info("app保存光缆段信息成功");
        }
        //修改
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            operatorOpticCableSectionInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
            operatorOpticCableSectionInfoReqForApp.setUpdateTime(UtcTimeUtil.getUtcTime());
            total = opticCableSectionInfoDao.updateOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp);
            log.info("app修改光缆段信息成功");
        }
        //删除
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            operatorOpticCableSectionInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
            operatorOpticCableSectionInfoReqForApp.setUpdateTime(UtcTimeUtil.getUtcTime());
            total = opticCableSectionInfoDao.deleteOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp);
            log.info("app删除光缆段信息成功");
            //删除光缆段gis信息
            UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfIdInfoReqApp = new UploadOpticCableSectionRfidInfoReqApp();
            List<String> opticCableSectionIdList = new ArrayList<>();
            opticCableSectionIdList.add(operatorOpticCableSectionInfoReqForApp.getOpticCableSectionId());
            uploadOpticCableSectionRfIdInfoReqApp.setOpticCableSectionIdList(opticCableSectionIdList);
            uploadOpticCableSectionRfIdInfoReqApp.setUpdateUser(RequestInfoUtils.getUserId());
            uploadOpticCableSectionRfIdInfoReqApp.setUpdateTime(UtcTimeUtil.getUtcTime());
            opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(uploadOpticCableSectionRfIdInfoReqApp);
            log.info("app删除光缆段gis信息成功");
        }
        if (total < 1) {
            return ResultUtils.warn(RfIdResultCode.SAVE_OPTIC_CABLE_SECTION_FAIL,
                    I18nUtils.getSystemString(RfIdI18nConstant.SAVE_OPTIC_CABLE_SECTION_FAIL));
        }
        this.getAddOpticCableSectionInfoLogParam(operatorOpticCableSectionInfoReqForApp);
        log.info("app保存光缆段操作日志成功");
        return ResultUtils.success(operatorOpticCableSectionInfoReqForApp.getOpticCableSectionId());
    }


    /**
     * 通过光缆id查询所有设施信息
     *
     * @param opticCableId 光缆id
     * @return Result
     */
    @Override
    public Result queryDeviceInfoListByOpticCableId(String opticCableId) {

        //必填参数校验
        if (StringUtils.isEmpty(opticCableId)) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //获取权限信息
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setBelongOpticCableId(opticCableId);
        queryCondition.setBizCondition(opticCableSectionInfoReq);
        opticCableSectionInfoReq = (OpticCableSectionInfoReq) this.getPermissionsInfo(queryCondition).getBizCondition();

        List<OpticCableSectionInfo> opticCableSectionInfoList = opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(opticCableSectionInfoReq);

        //获取设施信息
        Set<String> deviceIds = new HashSet<>();

        //过滤设施类型
        Set<String> permissionDeviceTypes = opticCableSectionInfoReq.getPermissionDeviceTypes();
        for (OpticCableSectionInfo opticCableSectionInfo : opticCableSectionInfoList) {
            //超级用户
            if (!"1".equals(RequestInfoUtils.getUserId())) {
                if (!ObjectUtils.isEmpty(permissionDeviceTypes)) {
                    //开始节点
                    if (permissionDeviceTypes.contains(opticCableSectionInfo.getStartNodeDeviceType())) {
                        deviceIds.add(opticCableSectionInfo.getStartNode());
                    }
                    //结束节点
                    if (permissionDeviceTypes.contains(opticCableSectionInfo.getTerminationNodeDeviceType())) {
                        deviceIds.add(opticCableSectionInfo.getTerminationNode());
                    }
                }
            } else {
                //开始节点
                deviceIds.add(opticCableSectionInfo.getStartNode());
                //结束节点
                deviceIds.add(opticCableSectionInfo.getTerminationNode());
            }
        }

        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        String[] deviceArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceArray);
        if (!ObjectUtils.isEmpty(deviceIds)) {
            deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);
        }

        if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
            return ResultUtils.success(new ArrayList<>());
        }

        return ResultUtils.success(deviceInfoDtoList);
    }

    /**
     * 光缆段列表导出
     *
     * @param exportDto 光缆段列表导出请求
     * @return Result
     */
    @Override
    public Result exportOpticCableSectionList(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo = null;
        try {
            exportRequestInfo = opticCableSectionExport.insertTask(exportDto, exportServerName, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_LIST));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(RfIdResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return ExportServiceUtil.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(RfIdResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(RfIdI18nConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(RfIdResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(RfIdI18nConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        opticCableSectionExport.exportData(exportRequestInfo);

        //新增日志
        ExportLogServer.addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE, logProcess, systemLanguageUtil);

        return ResultUtils.success(OpticCableSectionConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 新增光缆段操作日志参数
     *
     * @param reqForApp 光缆段参数
     * @return 新增光缆操作日志参数
     */
    private void getAddOpticCableSectionInfoLogParam(OperatorOpticCableSectionInfoReqForApp reqForApp) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(AppConstant.OPTICAL_CABLE_SECTION_ID);
        addLogBean.setDataName(AppConstant.OPTICAL_CABLE_SECTION__NAME);
        addLogBean.setOptObj(reqForApp.getOpticCableSectionName());
        addLogBean.setOptObjId(reqForApp.getOpticCableSectionId());
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(reqForApp.getUploadType())) {
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.ADD_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE);
            //修改
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(reqForApp.getUploadType())) {
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE);
            //删除
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(reqForApp.getUploadType())) {
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE);
        }
        //设置日志类型为pda操作日志
        addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * 将设备id转换成Name
     *
     * @param list 光缆段信息
     * @return Map<String, String>
     */
    private <T extends OpticCableSectionInfo> Map<String, String> deviceNameByID(List<T> list) {
        Set<String> deviceIdSet = getDeviceIdSetByOpticCableSectionInfo(list);
        if (deviceIdSet.size() == 0) {
            return new HashMap<>(16);
        }
        String[] deviceIds = deviceIdSet.toArray(new String[deviceIdSet.size()]);
        //获取设备信息
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceIds);
        if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
            return new HashMap<>(16);
        }
        Map<String, String> deviceInfoMap = new HashMap<>(deviceInfoDtoList.size());
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
            deviceInfoMap.put(deviceInfoDto.getDeviceId(), deviceInfoDto.getDeviceName());
        }
        return deviceInfoMap;
    }

    /**
     * 将地区id转换成Name
     *
     * @param list 光缆段信息
     * @return Map<String, String>
     */
    private <T extends OpticCableSectionInfo> Map<String, String> areaNameByID(List<T> list) {
        List<String> areaIdList = new ArrayList<>();
        for (OpticCableSectionInfo resp : list) {
            areaIdList.add(resp.getAreaId());
        }
        if (areaIdList.size() == 0) {
            return new HashMap<>(16);
        }
        //获取区域信息
        List<AreaInfoForeignDto> areaInfoForeignDtoList = areaFeign.selectAreaInfoByIds(areaIdList);
        if (ObjectUtils.isEmpty(areaInfoForeignDtoList)) {
            return new HashMap<>(16);
        }
        Map<String, String> areaInfoMap = new HashMap<>(areaInfoForeignDtoList.size());
        for (AreaInfoForeignDto areaInfoForeignDto : areaInfoForeignDtoList) {
            areaInfoMap.put(areaInfoForeignDto.getAreaId(), areaInfoForeignDto.getAreaName());
        }
        return areaInfoMap;
    }

    /**
     * 根据设施id数组获取设施rfId标签id
     *
     * @param list 光缆段信息
     * @return Map<String, Integer>
     */
    private <T extends OpticCableSectionInfo> Map<String, String> gainDeviceRfidById(List<T> list) {
        Set<String> deviceIdSet = getDeviceIdSetByOpticCableSectionInfo(list);
        if (deviceIdSet.size() == 0) {
            return new HashMap<>(16);
        }
        List<BaseInfoBean> realPositions = templateDao.getDeviceRfidById(new ArrayList<>(deviceIdSet));
        Map<String, String> deviceRfIdMap = new HashMap<>(realPositions.size());
        for (BaseInfoBean baseInfoBean : realPositions) {
            deviceRfIdMap.put(baseInfoBean.getDeviceId(), baseInfoBean.getBoxLabel());
        }
        return deviceRfIdMap;
    }

    /**
     * @param list 光缆段信息list
     * @param <T>  OpticCableSectionInfo
     * @return Set<String>
     */
    private <T extends OpticCableSectionInfo> Set<String> getDeviceIdSetByOpticCableSectionInfo(List<T> list) {
        Set<String> deviceIdSet = new HashSet<>();
        for (OpticCableSectionInfo resp : list) {
            deviceIdSet.add(resp.getStartNode());
            deviceIdSet.add(resp.getTerminationNode());
        }
        return deviceIdSet;
    }

    /*---------------------------------------获取权限公共方法start-----------------------------------------*/

    /**
     * 获取拥有权限信息
     *
     * @param queryCondition 查询对象
     * @return queryCondition    查询对象
     */
    public QueryCondition getPermissionsInfo(QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        return getPermissionsInfo(queryCondition, RequestInfoUtils.getUserId());
    }

    /**
     * 获取拥有权限信息（导出）
     *
     * @param queryCondition 查询对象
     * @return queryCondition    查询对象
     */
    public QueryCondition getPermissionsInfoForExport(QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        return getPermissionsInfo(queryCondition, ExportApiUtils.getCurrentUserId());
    }

    /**
     * 获取拥有权限信息
     *
     * @param queryCondition 查询对象
     * @return queryCondition    查询对象
     */
    public QueryCondition getPermissionsInfo(QueryCondition<OpticCableSectionInfoReq> queryCondition, String userId) {
        //获取权限信息
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        Object userObj = userFeign.queryUserByIdList(userIds);
        //校验是否有值
        if (ObjectUtils.isEmpty(userObj)) {
            throw new FilinkObtainUserInfoException();
        }
        List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userObj), User.class);
        User user = new User();
        //添加用户map
        if (!ObjectUtils.isEmpty(userInfoList)) {
            user = userInfoList.get(0);
        }

        //admin用户不用校验权限
        String adminUserId = "1";
        String loginUserId = userId;
        if (!ObjectUtils.isEmpty(loginUserId)) {
            //当前用户是admin时不用获取所有权限
            if (adminUserId.equals(loginUserId)) {
                return queryCondition;
            }
        }

        //用户管理区域
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //用户管理设施类型
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();

        //当用户权限信息中有信息为空，直接返回页面空
        if (ObjectUtils.isEmpty(areaIds) || ObjectUtils.isEmpty(roleDeviceTypes)) {
            throw new FilinkUserPermissionException();
        }

        Set<String> deviceTypeSet = new HashSet<>();
        Set<String> areaIdSet = new HashSet<>();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypeSet.add(roleDeviceType.getDeviceTypeId());
        }
        for (String areaId : areaIds) {
            areaIdSet.add(areaId);
        }

        queryCondition.getBizCondition().setPermissionDeviceTypes(deviceTypeSet);
        queryCondition.getBizCondition().setPermissionAreaIds(areaIdSet);

        return queryCondition;
    }
    /*---------------------------------------获取权限公共方法end-----------------------------------------*/

    /**
     * 组装光缆段信息
     *
     * @param opticCableSectionInfoRespList 光缆段信息列表
     * @return List<OpticCableSectionInfoResp>
     */
    @Override
    public List<OpticCableSectionInfoResp> assemblyOpticCableSectionInfoResp(List<OpticCableSectionInfoResp> opticCableSectionInfoRespList) {
        Map<String, String> deviceInfoMap = deviceNameByID(opticCableSectionInfoRespList);
        Map<String, String> areaInfoMap = areaNameByID(opticCableSectionInfoRespList);
        for (OpticCableSectionInfoResp resp : opticCableSectionInfoRespList) {
            resp.setAreaName(areaInfoMap.get(resp.getAreaId()));
            resp.setStartNodeName(deviceInfoMap.get(resp.getStartNode()));
            resp.setTerminationNodeName(deviceInfoMap.get(resp.getTerminationNode()));
        }
        return opticCableSectionInfoRespList;
    }

    /**
     * 计算纤芯已占用数
     *
     * @param opticCableSectionId 光缆段id
     */
    @Override
    public Future<Integer> coreStatisticsCount(String opticCableSectionId) {
        //获取熔纤信息
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfoByOpticCableId(opticCableSectionId);
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        portCableCoreInfoReq.setOppositeResource(opticCableSectionId);
        //获取成端信息
        List<PortCableCoreInfo> portCableCoreInfoList = portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        Set<String> coreNoSet = new HashSet<>();
        Integer usedCoreNum = 0;
        //计算使用两次的纤芯号数量，及纤芯占用数
        for (CoreCoreInfoResp coreCoreInfoResp : coreCoreInfoRespList) {
            if (opticCableSectionId.equals(coreCoreInfoResp.getResource())) {
                if (!coreNoSet.add(coreCoreInfoResp.getCableCoreNo())) {
                    usedCoreNum++;
                }

            }
            if (opticCableSectionId.equals(coreCoreInfoResp.getOppositeResource())) {
                if (!coreNoSet.add(coreCoreInfoResp.getOppositeCableCoreNo())) {
                    usedCoreNum++;
                }
            }
        }
        for (PortCableCoreInfo portCableCoreInfo : portCableCoreInfoList) {
            if (!coreNoSet.add(portCableCoreInfo.getOppositeCableCoreNo())) {
                usedCoreNum++;
            }
        }
        OpticCableSectionInfo cableSectionInfo = new OpticCableSectionInfo();
        cableSectionInfo.setOpticCableSectionId(opticCableSectionId);
        cableSectionInfo.setUsedCoreNum(usedCoreNum.toString());
        return new AsyncResult<>(opticCableSectionInfoDao.updateOpticCableSectionUsedCoreNum(cableSectionInfo));
    }

    /**
     * 校验光缆段节点是否存在
     *
     * @param operatorOpticCableSectionInfoReqForApp app光缆段信息请求
     */
    public String checkOpticCableSectionDeviceId(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp) {
        Set<String> deviceIds = new HashSet<>();
        deviceIds.add(operatorOpticCableSectionInfoReqForApp.getStartNode());
        deviceIds.add(operatorOpticCableSectionInfoReqForApp.getTerminationNode());
        if (ObjectUtils.isEmpty(deviceIds)) {
            return OpticCableSectionConstant.DEVICE_NODE_NOT_EXISTS;
        }
        String[] deviceIdArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceIdArray);
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceIdArray);
        if (null == deviceInfoDtoList) {
            return OpticCableSectionConstant.DEVICE_SERVER_ERROR;
        } else if (0 <= deviceInfoDtoList.size() && deviceIds.size() > deviceInfoDtoList.size()) {
            return OpticCableSectionConstant.DEVICE_NODE_NOT_EXISTS;
        } else if (deviceIds.size() == deviceInfoDtoList.size()) {
            return OpticCableSectionConstant.DEVICE_NODE_EXISTS;
        } else {
            return OpticCableSectionConstant.DEVICE_SERVER_ERROR;
        }
    }

    /**
     * 校验光缆段名称是否存在
     *
     * @param operatorOpticCableSectionInfoReqForApp app光缆段信息请求
     */
    @Override
    public Boolean checkOpticCableSectionNameForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp) {
        //光缆段名称统一校验
        String opticCableSectionName = CheckInputString.nameCheck(operatorOpticCableSectionInfoReqForApp.getOpticCableSectionName());
        if (StringUtils.isEmpty(opticCableSectionName)) {
            return true;
        }
        //光缆段名称重复校验 保证光缆段名称唯一
        OpticCableSectionInfo opticCableSectionInfo = opticCableSectionInfoDao.
                queryOpticCableSectionByName(opticCableSectionName, operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId());
        if (ObjectUtils.isEmpty(opticCableSectionInfo)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 校验设施是否有权限
     *
     * @param deviceIdList 设施编号集合
     * @return 设施信息
     * @author hedongwei@wistronits.com
     * @date 2019/7/30 14:16
     */
    @Override
    public Result checkIsPermissionDeviceByDeviceIdList(List<String> deviceIdList) {
        if (!ObjectUtils.isEmpty(deviceIdList)) {
            List<DeviceInfoDto> deviceInfoDtoReturnList = this.getIsPermissionDevice(deviceIdList);
            return ResultUtils.success(deviceInfoDtoReturnList);
        }
        return ResultUtils.success(new ArrayList<>());
    }


    /**
     * 校验用户同时拥有需要查询的设施的权限
     *
     * @param deviceIdList 设施编号集合
     * @return 设施信息
     * @author hedongwei@wistronits.com
     * @date 2019/7/30 14:16
     */
    @Override
    public Result existIsPermissionDeviceByDeviceIdList(List<String> deviceIdList) {
        boolean returnFlag = false;
        if (!ObjectUtils.isEmpty(deviceIdList)) {
            List<DeviceInfoDto> deviceInfoDtoReturnList = this.getIsPermissionDevice(deviceIdList);
            if (!ObjectUtils.isEmpty(deviceInfoDtoReturnList)) {
                //如果查询出来的设施数据个数等于需要查询的设施个数，证明用户拥有所有的设施权限，返回true
                if (deviceIdList.size() == deviceInfoDtoReturnList.size()) {
                    returnFlag = true;
                }
            }
            return ResultUtils.success(returnFlag);
        }
        returnFlag = true;
        return ResultUtils.success(returnFlag);
    }

    /**
     * 获取有权限的设施信息
     *
     * @param deviceIdList
     * @return 返回权限设施信息
     * @author hedongwei@wistronits.com
     * @date 2019/7/30 15:25
     */
    public List<DeviceInfoDto> getIsPermissionDevice(List<String> deviceIdList) {
        String[] deviceArray = new String[deviceIdList.size()];
        deviceIdList.toArray(deviceArray);

        //查询设施信息
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);
        if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
            return new ArrayList<>();
        }
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        OpticCableSectionInfoReq bizCondition = new OpticCableSectionInfoReq();
        queryCondition.setBizCondition(bizCondition);
        //获取权限信息
        queryCondition = this.getOpticCableSectionInfoReq(queryCondition, RequestInfoUtils.getUserId());
        if (CommonConstant.ADMIN_USER_ID.equals(RequestInfoUtils.getUserId())) {
            return deviceInfoDtoList;
        }
        //设施类型权限
        Set<String> deviceTypeIdSet = queryCondition.getBizCondition().getPermissionDeviceTypes();

        //设施区域权限
        Set<String> deviceAreaIdSet = queryCondition.getBizCondition().getPermissionAreaIds();

        Map<String, String> deviceTypeMap = new HashMap<>(CommonConstant.INIT_MAP_NUMBER);
        if (!ObjectUtils.isEmpty(deviceTypeIdSet)) {
            for (String deviceTypeOne : deviceTypeIdSet) {
                deviceTypeMap.put(deviceTypeOne, deviceTypeOne);
            }
        }
        Map<String, String> deviceAreaIdMap = new HashMap<>(CommonConstant.INIT_MAP_NUMBER);
        if (!ObjectUtils.isEmpty(deviceAreaIdSet)) {
            for (String areaId : deviceAreaIdSet) {
                deviceAreaIdMap.put(areaId, areaId);
            }
        }

        List<DeviceInfoDto> deviceInfoDtoReturnList = new ArrayList<>();
        for (DeviceInfoDto deviceInfoDtoOne : deviceInfoDtoList) {
            //有设施类型的权限，并且有设施区域的权限
            if (deviceAreaIdMap.containsKey(deviceInfoDtoOne.getAreaInfo().getAreaId()) && deviceTypeMap.containsKey(deviceInfoDtoOne.getDeviceType())) {
                deviceInfoDtoReturnList.add(deviceInfoDtoOne);
            }
        }
        return deviceInfoDtoReturnList;
    }

    /**
     * 保存操作日志
     *
     * @param opticCableSectionInfo 光缆段
     * @return void
     */
    public void saveOperatorLog(OpticCableSectionInfo opticCableSectionInfo) {
        // 保存光缆段操作日志
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("opticCableSectionId");
        addLogBean.setDataName("opticCableSectionName");
        addLogBean.setOptObj(opticCableSectionInfo.getOpticCableSectionName());
        addLogBean.setOptObjId(opticCableSectionInfo.getOpticCableSectionId());
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
        addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE);

        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
