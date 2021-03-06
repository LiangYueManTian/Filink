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
 * ?????????????????? ???????????????
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
@Service
@Slf4j
public class OpticCableSectionInfoServiceImpl extends ServiceImpl<OpticCableSectionInfoDao, OpticCableSectionInfo> implements OpticCableSectionInfoService {

    /**
     * ?????????????????? Mapper ??????
     */
    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
    /**
     * ??????????????? Mapper ??????
     */
    @Autowired
    private OpticCableInfoDao opticCableInfoDao;
    /**
     * ??????????????? Mapper ??????
     */
    @Autowired
    private TemplateDao templateDao;

    /**
     * ??????api
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * ??????api
     */
    @Autowired
    private DeviceFeign deviceFeign;
    /**
     * ??????Feign
     */
    @Autowired
    private AreaFeign areaFeign;

    /**
     * ??????Feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * ????????????SystemLanguage??????
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ??????????????????????????????
     */
    @Autowired
    @Lazy
    private OpticCableSectionExport opticCableSectionExport;

    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * ???????????????
     */
    @Value("${exportServerName}")
    private String exportServerName;

    /**
     * ??????????????? Mapper ??????
     */
    @Autowired
    private CoreCoreInfoDao coreCoreInfoDao;

    /**
     * ??????????????? Mapper ??????
     */
    @Autowired
    private PortCableCoreInfoDao portCableCoreInfoDao;

    /**
     * ?????????gis????????? Mapper ??????
     */
    @Autowired
    private OpticCableSectionRfidInfoDao opticCableSectionRfidInfoDao;


    /**
     * ?????????????????????
     *
     * @param queryCondition ???????????????
     * @return Result
     */
    @Override
    public Result selectOpticCableSection(QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition
                .getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);

        //??????????????????
        queryCondition = this.getOpticCableSectionInfoReq(queryCondition, RequestInfoUtils.getUserId());

        List<OpticCableSectionInfoResp> list = opticCableSectionInfoDao
                .selectOpticCableSection(queryCondition);

        //?????????????????????
        this.assemblyOpticCableSectionInfoResp(list);

        // ??????????????????
        Page page = myBatiesBuildPage(queryCondition);
        PageBean pageBean = myBatiesBuildPageBean(page,
                opticCableSectionInfoDao.opticCableSectionByIdTotal(queryCondition),
                list);
        return ResultUtils.pageSuccess(pageBean);
    }


    /**
     * ???????????????????????????
     *
     * @author hedongwei@wistronits.com
     * @date 2019/7/30 13:25
     */
    public QueryCondition<OpticCableSectionInfoReq> getOpticCableSectionInfoReq(QueryCondition<OpticCableSectionInfoReq> queryCondition, String userId) {
        //??????????????????
        if (ObjectUtils.isEmpty(userId)) {
            return queryCondition;
        }

        //??????????????????
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        Object userObj = userFeign.queryUserByIdList(userIds);
        //??????????????????
        if (ObjectUtils.isEmpty(userObj)) {
            throw new FilinkObtainUserInfoException();
        }
        List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userObj), User.class);
        User user = new User();
        //????????????map
        if (!ObjectUtils.isEmpty(userInfoList)) {
            user = userInfoList.get(0);
        }

        //admin????????????????????????
        String adminUserId = CommonConstant.ADMIN_USER_ID;
        String loginUserId = userId;
        if (!ObjectUtils.isEmpty(loginUserId)) {
            //???????????????admin???????????????????????????
            if (adminUserId.equals(loginUserId)) {
                return queryCondition;
            }
        }

        //??????????????????
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //????????????????????????
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        //??????????????????
        Department department = user.getDepartment();

        //???????????????????????????????????????????????????????????????
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
     * ??????????????????id?????????????????????
     *
     * @param opticCableSectionInfoReq ?????????????????????
     * @return Result
     */
    @Override
    public Result opticCableSectionByIdForTopology(OpticCableSectionInfoReq opticCableSectionInfoReq) {

        //??????????????????
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        queryCondition.setBizCondition(opticCableSectionInfoReq);
        opticCableSectionInfoReq = (OpticCableSectionInfoReq) this.getPermissionsInfo(queryCondition).getBizCondition();

        List<OpticCableSectionInfoResp> opticCableSectionInfoRespList = opticCableSectionInfoDao.opticCableSectionByIdForTopology(opticCableSectionInfoReq);

        //??????????????????
        Set<String> permissionDeviceTypes = opticCableSectionInfoReq.getPermissionDeviceTypes();
        for (OpticCableSectionInfoResp opticCableSectionInfoResp : opticCableSectionInfoRespList) {
            //????????????
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

        //?????????????????????????????????????????????
        for (int i = 0; i < opticCableSectionInfoRespList.size(); i++) {
            OpticCableSectionInfoResp opticCableSectionInfoResp = opticCableSectionInfoRespList.get(i);
            if (StringUtils.isEmpty(opticCableSectionInfoResp.getStartNodeDeviceType()) && StringUtils.isEmpty(opticCableSectionInfoResp.getTerminationNodeDeviceType())) {
                opticCableSectionInfoRespList.remove(opticCableSectionInfoResp);
            }
        }
        return ResultUtils.success(opticCableSectionInfoRespList);
    }

    /**
     * ????????????id???????????????
     * *
     *
     * @param deviceId ??????id
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
     * ???????????????id???????????????
     *
     * @param opticCableSectionId ?????????id
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteOpticCableSectionByOpticCableSectionId(String opticCableSectionId) {

        //???????????????
        OpticCableSectionInfo opticCableSectionInfo = new OpticCableSectionInfo();
        opticCableSectionInfo.setIsDeleted("0");
        opticCableSectionInfo.setOpticCableSectionId(opticCableSectionId);
        opticCableSectionInfo = opticCableSectionInfoDao.selectOne(opticCableSectionInfo);

        //??????????????????????????????
        if (ObjectUtils.isEmpty(opticCableSectionInfo)) {
            return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_NOT_EXIST));
        }

        //????????????
        CoreCoreInfoReq coreCoreInfoReq = new CoreCoreInfoReq();
        coreCoreInfoReq.setResource(opticCableSectionId);
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfo(coreCoreInfoReq);
        //????????????
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        portCableCoreInfoReq.setOppositeResource(opticCableSectionId);
        List<PortCableCoreInfo> portCableCoreInfoList = portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        //??????????????????????????????
        if (!ObjectUtils.isEmpty(coreCoreInfoRespList) || !ObjectUtils.isEmpty(portCableCoreInfoList)) {
            return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_HAVE_SERVER, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_HAVE_SERVER));
        }

        //???????????????
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setOpticCableSectionId(opticCableSectionId);
        opticCableSectionInfoReq.setUpdateUser(RequestInfoUtils.getUserId());
        opticCableSectionInfoReq.setUpdateTime(UtcTimeUtil.getUtcTime());
        opticCableSectionInfoDao.deleteOpticCableSectionByOpticCableSectionId(opticCableSectionInfoReq);
        log.info("???????????????????????????");

        //???????????????gis??????
        UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfIdInfoReqApp = new UploadOpticCableSectionRfidInfoReqApp();
        List<String> opticCableSectionIdList = new ArrayList<>();
        opticCableSectionIdList.add(opticCableSectionId);
        uploadOpticCableSectionRfIdInfoReqApp.setOpticCableSectionIdList(opticCableSectionIdList);
        uploadOpticCableSectionRfIdInfoReqApp.setUpdateUser(RequestInfoUtils.getUserId());
        uploadOpticCableSectionRfIdInfoReqApp.setUpdateTime(UtcTimeUtil.getUtcTime());
        opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(uploadOpticCableSectionRfIdInfoReqApp);
        log.info("???????????????gis????????????");

        //??????????????????
        this.saveOperatorLog(opticCableSectionInfo);
        log.info("?????????????????????????????????");

        return ResultUtils.success(RfIdResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.DELETE_OPTIC_CABLE_SECTION_SUCCESS));
    }

    /**
     * app???????????????????????????
     *
     * @param opticCableSectionInfoReqForApp app??????????????????
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
     * app???????????????????????????
     *
     * @param operatorOpticCableSectionInfoReqForApp app????????????????????????
     * @return Result
     */
    @Override
    public Result uploadOpticCableSectionInfoForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp) {
        //?????????????????????
        if (StringUtils.isEmpty(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        //??????????????????
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            //????????????????????????????????????
            if (operatorOpticCableSectionInfoReqForApp.getStartNode().equals(operatorOpticCableSectionInfoReqForApp.getTerminationNode())) {
                return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
            }
            /*???????????????????????????????????????????????????*/
            //????????????????????????
            Integer coreNum = opticCableInfoDao.queryOpticCableByCoreNum(operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId());
            if (Objects.isNull(coreNum)) {
                return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_PARAM_ERROR,
                        I18nUtils.getSystemString(RfIdI18nConstant.BELONG_OPTIC_CABLE_ID_ERROR));
            }
            if (coreNum < Integer.valueOf(operatorOpticCableSectionInfoReqForApp.getCoreNum())) {
                return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_CORE_OVERRUN,
                        I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_CORE_OVERRUN));
            }
            //?????????????????????????????????
            String flag = this.checkOpticCableSectionDeviceId(operatorOpticCableSectionInfoReqForApp);
            if (OpticCableSectionConstant.DEVICE_SERVER_ERROR.equals(flag)) {
                return ResultUtils.warn(RfIdResultCode.DEVICE_SERVER_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_SERVER_ERROR));
            } else if (OpticCableSectionConstant.DEVICE_NODE_NOT_EXISTS.equals(flag)) {
                return ResultUtils.warn(RfIdResultCode.DEVICE_NODE_NOT_EXISTS, I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_NODE_NOT_EXISTS));
            }
            //?????????????????????????????????
            if (this.checkOpticCableSectionNameForApp(operatorOpticCableSectionInfoReqForApp)) {
                return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_NAME_SAME));
            }
        }
        int total = 0;
        //??????
        if (AppConstant.OPERATOR_TYPE_SAVE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            operatorOpticCableSectionInfoReqForApp.setOpticCableSectionId(NineteenUUIDUtils.uuid());
            operatorOpticCableSectionInfoReqForApp.setCreateUser(RequestInfoUtils.getUserId());
            operatorOpticCableSectionInfoReqForApp.setCreateTime(UtcTimeUtil.getUtcTime());
            operatorOpticCableSectionInfoReqForApp.setStatus(OpticCableSectionConstant.STATUS_UNUSED);
            total = opticCableSectionInfoDao.addOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp);
            log.info("app???????????????????????????");
        }
        //??????
        if (AppConstant.OPERATOR_TYPE_UPDATE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            operatorOpticCableSectionInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
            operatorOpticCableSectionInfoReqForApp.setUpdateTime(UtcTimeUtil.getUtcTime());
            total = opticCableSectionInfoDao.updateOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp);
            log.info("app???????????????????????????");
        }
        //??????
        if (AppConstant.OPERATOR_TYPE_DELETE.equals(operatorOpticCableSectionInfoReqForApp.getUploadType())) {
            operatorOpticCableSectionInfoReqForApp.setUpdateUser(RequestInfoUtils.getUserId());
            operatorOpticCableSectionInfoReqForApp.setUpdateTime(UtcTimeUtil.getUtcTime());
            total = opticCableSectionInfoDao.deleteOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp);
            log.info("app???????????????????????????");
            //???????????????gis??????
            UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfIdInfoReqApp = new UploadOpticCableSectionRfidInfoReqApp();
            List<String> opticCableSectionIdList = new ArrayList<>();
            opticCableSectionIdList.add(operatorOpticCableSectionInfoReqForApp.getOpticCableSectionId());
            uploadOpticCableSectionRfIdInfoReqApp.setOpticCableSectionIdList(opticCableSectionIdList);
            uploadOpticCableSectionRfIdInfoReqApp.setUpdateUser(RequestInfoUtils.getUserId());
            uploadOpticCableSectionRfIdInfoReqApp.setUpdateTime(UtcTimeUtil.getUtcTime());
            opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(uploadOpticCableSectionRfIdInfoReqApp);
            log.info("app???????????????gis????????????");
        }
        if (total < 1) {
            return ResultUtils.warn(RfIdResultCode.SAVE_OPTIC_CABLE_SECTION_FAIL,
                    I18nUtils.getSystemString(RfIdI18nConstant.SAVE_OPTIC_CABLE_SECTION_FAIL));
        }
        this.getAddOpticCableSectionInfoLogParam(operatorOpticCableSectionInfoReqForApp);
        log.info("app?????????????????????????????????");
        return ResultUtils.success(operatorOpticCableSectionInfoReqForApp.getOpticCableSectionId());
    }


    /**
     * ????????????id????????????????????????
     *
     * @param opticCableId ??????id
     * @return Result
     */
    @Override
    public Result queryDeviceInfoListByOpticCableId(String opticCableId) {

        //??????????????????
        if (StringUtils.isEmpty(opticCableId)) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }

        //??????????????????
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setBelongOpticCableId(opticCableId);
        queryCondition.setBizCondition(opticCableSectionInfoReq);
        opticCableSectionInfoReq = (OpticCableSectionInfoReq) this.getPermissionsInfo(queryCondition).getBizCondition();

        List<OpticCableSectionInfo> opticCableSectionInfoList = opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(opticCableSectionInfoReq);

        //??????????????????
        Set<String> deviceIds = new HashSet<>();

        //??????????????????
        Set<String> permissionDeviceTypes = opticCableSectionInfoReq.getPermissionDeviceTypes();
        for (OpticCableSectionInfo opticCableSectionInfo : opticCableSectionInfoList) {
            //????????????
            if (!"1".equals(RequestInfoUtils.getUserId())) {
                if (!ObjectUtils.isEmpty(permissionDeviceTypes)) {
                    //????????????
                    if (permissionDeviceTypes.contains(opticCableSectionInfo.getStartNodeDeviceType())) {
                        deviceIds.add(opticCableSectionInfo.getStartNode());
                    }
                    //????????????
                    if (permissionDeviceTypes.contains(opticCableSectionInfo.getTerminationNodeDeviceType())) {
                        deviceIds.add(opticCableSectionInfo.getTerminationNode());
                    }
                }
            } else {
                //????????????
                deviceIds.add(opticCableSectionInfo.getStartNode());
                //????????????
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
     * ?????????????????????
     *
     * @param exportDto ???????????????????????????
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

        //????????????
        ExportLogServer.addLogByExport(exportDto, LogFunctionCodeConstant.EXPORT_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE, logProcess, systemLanguageUtil);

        return ResultUtils.success(OpticCableSectionConstant.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ?????????????????????????????????
     *
     * @param reqForApp ???????????????
     * @return ??????????????????????????????
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
            //??????
        } else if (AppConstant.OPERATOR_TYPE_UPDATE.equals(reqForApp.getUploadType())) {
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.UPDATE_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE);
            //??????
        } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(reqForApp.getUploadType())) {
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE);
        }
        //?????????????????????pda????????????
        addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * ?????????id?????????Name
     *
     * @param list ???????????????
     * @return Map<String, String>
     */
    private <T extends OpticCableSectionInfo> Map<String, String> deviceNameByID(List<T> list) {
        Set<String> deviceIdSet = getDeviceIdSetByOpticCableSectionInfo(list);
        if (deviceIdSet.size() == 0) {
            return new HashMap<>(16);
        }
        String[] deviceIds = deviceIdSet.toArray(new String[deviceIdSet.size()]);
        //??????????????????
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
     * ?????????id?????????Name
     *
     * @param list ???????????????
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
        //??????????????????
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
     * ????????????id??????????????????rfId??????id
     *
     * @param list ???????????????
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
     * @param list ???????????????list
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

    /*---------------------------------------????????????????????????start-----------------------------------------*/

    /**
     * ????????????????????????
     *
     * @param queryCondition ????????????
     * @return queryCondition    ????????????
     */
    public QueryCondition getPermissionsInfo(QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        return getPermissionsInfo(queryCondition, RequestInfoUtils.getUserId());
    }

    /**
     * ????????????????????????????????????
     *
     * @param queryCondition ????????????
     * @return queryCondition    ????????????
     */
    public QueryCondition getPermissionsInfoForExport(QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        return getPermissionsInfo(queryCondition, ExportApiUtils.getCurrentUserId());
    }

    /**
     * ????????????????????????
     *
     * @param queryCondition ????????????
     * @return queryCondition    ????????????
     */
    public QueryCondition getPermissionsInfo(QueryCondition<OpticCableSectionInfoReq> queryCondition, String userId) {
        //??????????????????
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        Object userObj = userFeign.queryUserByIdList(userIds);
        //??????????????????
        if (ObjectUtils.isEmpty(userObj)) {
            throw new FilinkObtainUserInfoException();
        }
        List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userObj), User.class);
        User user = new User();
        //????????????map
        if (!ObjectUtils.isEmpty(userInfoList)) {
            user = userInfoList.get(0);
        }

        //admin????????????????????????
        String adminUserId = "1";
        String loginUserId = userId;
        if (!ObjectUtils.isEmpty(loginUserId)) {
            //???????????????admin???????????????????????????
            if (adminUserId.equals(loginUserId)) {
                return queryCondition;
            }
        }

        //??????????????????
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //????????????????????????
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();

        //???????????????????????????????????????????????????????????????
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
    /*---------------------------------------????????????????????????end-----------------------------------------*/

    /**
     * ?????????????????????
     *
     * @param opticCableSectionInfoRespList ?????????????????????
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
     * ????????????????????????
     *
     * @param opticCableSectionId ?????????id
     */
    @Override
    public Future<Integer> coreStatisticsCount(String opticCableSectionId) {
        //??????????????????
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfoByOpticCableId(opticCableSectionId);
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        portCableCoreInfoReq.setOppositeResource(opticCableSectionId);
        //??????????????????
        List<PortCableCoreInfo> portCableCoreInfoList = portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        Set<String> coreNoSet = new HashSet<>();
        Integer usedCoreNum = 0;
        //?????????????????????????????????????????????????????????
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
     * ?????????????????????????????????
     *
     * @param operatorOpticCableSectionInfoReqForApp app?????????????????????
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
     * ?????????????????????????????????
     *
     * @param operatorOpticCableSectionInfoReqForApp app?????????????????????
     */
    @Override
    public Boolean checkOpticCableSectionNameForApp(OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp) {
        //???????????????????????????
        String opticCableSectionName = CheckInputString.nameCheck(operatorOpticCableSectionInfoReqForApp.getOpticCableSectionName());
        if (StringUtils.isEmpty(opticCableSectionName)) {
            return true;
        }
        //??????????????????????????? ???????????????????????????
        OpticCableSectionInfo opticCableSectionInfo = opticCableSectionInfoDao.
                queryOpticCableSectionByName(opticCableSectionName, operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId());
        if (ObjectUtils.isEmpty(opticCableSectionInfo)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * ???????????????????????????
     *
     * @param deviceIdList ??????????????????
     * @return ????????????
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
     * ??????????????????????????????????????????????????????
     *
     * @param deviceIdList ??????????????????
     * @return ????????????
     * @author hedongwei@wistronits.com
     * @date 2019/7/30 14:16
     */
    @Override
    public Result existIsPermissionDeviceByDeviceIdList(List<String> deviceIdList) {
        boolean returnFlag = false;
        if (!ObjectUtils.isEmpty(deviceIdList)) {
            List<DeviceInfoDto> deviceInfoDtoReturnList = this.getIsPermissionDevice(deviceIdList);
            if (!ObjectUtils.isEmpty(deviceInfoDtoReturnList)) {
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????true
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
     * ??????????????????????????????
     *
     * @param deviceIdList
     * @return ????????????????????????
     * @author hedongwei@wistronits.com
     * @date 2019/7/30 15:25
     */
    public List<DeviceInfoDto> getIsPermissionDevice(List<String> deviceIdList) {
        String[] deviceArray = new String[deviceIdList.size()];
        deviceIdList.toArray(deviceArray);

        //??????????????????
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);
        if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
            return new ArrayList<>();
        }
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        OpticCableSectionInfoReq bizCondition = new OpticCableSectionInfoReq();
        queryCondition.setBizCondition(bizCondition);
        //??????????????????
        queryCondition = this.getOpticCableSectionInfoReq(queryCondition, RequestInfoUtils.getUserId());
        if (CommonConstant.ADMIN_USER_ID.equals(RequestInfoUtils.getUserId())) {
            return deviceInfoDtoList;
        }
        //??????????????????
        Set<String> deviceTypeIdSet = queryCondition.getBizCondition().getPermissionDeviceTypes();

        //??????????????????
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
            //?????????????????????????????????????????????????????????
            if (deviceAreaIdMap.containsKey(deviceInfoDtoOne.getAreaInfo().getAreaId()) && deviceTypeMap.containsKey(deviceInfoDtoOne.getDeviceType())) {
                deviceInfoDtoReturnList.add(deviceInfoDtoOne);
            }
        }
        return deviceInfoDtoReturnList;
    }

    /**
     * ??????????????????
     *
     * @param opticCableSectionInfo ?????????
     * @return void
     */
    public void saveOperatorLog(OpticCableSectionInfo opticCableSectionInfo) {
        // ???????????????????????????
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("opticCableSectionId");
        addLogBean.setDataName("opticCableSectionName");
        addLogBean.setOptObj(opticCableSectionInfo.getOpticCableSectionName());
        addLogBean.setOptObjId(opticCableSectionInfo.getOpticCableSectionId());
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
        addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_OPTIC_CABLE_SECTION_INFO_FUNCTION_CODE);

        //????????????feign????????????log??????
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
