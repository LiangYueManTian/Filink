package com.fiberhome.filink.fdevice.service.area.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.async.AreaAsync;
import com.fiberhome.filink.fdevice.bean.area.AreaDeptInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;
import com.fiberhome.filink.fdevice.bean.area.ToTopAreaInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.area.AreaFunctionCodeConstant;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.fdevice.constant.area.AreaResultCode;
import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.dao.area.AreaDeptInfoDao;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dto.AreaInfoDto;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;
import com.fiberhome.filink.fdevice.exception.*;
import com.fiberhome.filink.fdevice.export.AreaExport;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.stream.DeviceStreams;
import com.fiberhome.filink.fdevice.utils.AreaCreateTimeComparator;
import com.fiberhome.filink.fdevice.utils.AreaUtil;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.fiberhome.filink.fdevice.constant.area.AreaConstant.*;

/**
 * <p>
 * ???????????????????????????
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@RefreshScope
@Service
@Slf4j
public class AreaInfoServiceImpl extends ServiceImpl<AreaInfoDao, AreaInfo> implements AreaInfoService {
    /**
     * ??????????????????dao??????
     */
    @Autowired
    private AreaInfoDao areaInfoDao;
    /**
     * ??????????????????dao??????
     */
    @Autowired
    private AreaDeptInfoDao areaDeptInfoDao;
    /**
     * ??????????????????dao??????
     */
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    /**
     * ??????????????????service
     */
    @Autowired
    private DeviceInfoService deviceInfoService;
    /**
     * ????????????????????????
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * ????????????????????????
     */
    @Autowired
    private LogCastProcess logCastProcess;

    /**
     * ????????????????????????
     */
    @Autowired
    private DepartmentFeign departmentFeign;
    /**
     * ??????????????????????????????
     */
    @Autowired
    private UserFeign userFeign;
    /**
     * ????????????????????????
     */
    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;
    /**
     * ?????????
     */
    @Autowired
    private AreaExport areaExport;
    /**
     * ??????????????????????????????
     */
    @Autowired
    private DeviceStreams deviceStreams;
    /**
     * departmentList ???????????????????????????????????????
     */
    public static TransmittableThreadLocal<List<Department>> tlDepartmentList = new TransmittableThreadLocal<>();
    /**
     * ??????????????????
     */
    @Value(MAX_EXPORT_DATA_SIZE)
    private Integer maxExportDataSize;
    /**
     * ?????????????????????????????????
     */
    @Autowired
    private AreaAsync areaAsync;
    /**
     * ??????????????????
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;
    /**
     * ???????????????
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ???????????????????????????
     *
     * @param areaInfo ????????????
     * @return ????????????
     */
    @Override
    public Result queryAreaNameIsExist(AreaInfo areaInfo) {
        //??????????????????????????????????????????
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaResultCode.NAME_IS_EXIST, I18nUtils.getSystemString(AreaI18nConstant.NAME_IS_EXIST));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AreaI18nConstant.NAME_IS_AVAILABLE));
    }

    /**
     * ????????????
     *
     * @param areaInfo ????????????
     * @return ????????????
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.ADD_LOG_LOCAL_FILE, functionCode = AreaFunctionCodeConstant.ADD_AREA_FUNCTION_CODE, dataGetColumnName = "areaAndPrentName", dataGetColumnId = "areaId")
    @Override
    public Result addArea(AreaInfo areaInfo) {
        systemLanguageUtil.querySystemLanguage();
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaResultCode.NAME_IS_EXIST, I18nUtils.getSystemString(AreaI18nConstant.NAME_IS_EXIST));
        }
        //?????????????????????id
        areaInfo.setAreaId(NineteenUUIDUtils.uuid());
        areaInfo = setLevel(areaInfo);
        if (FIVE < areaInfo.getLevel()) {
            throw new FiLinkAreaDateFormatException();
        }
        areaInfo.setCreateTime(new Date());
        //??????????????????
        int result = areaInfoDao.addAreaInfo(areaInfo);
        //????????????????????????
        getAllName(areaInfo);
        //??????????????????
        if (result != 1) {
            throw new FiLinkAreaDateBaseException(I18nUtils.getSystemString(AreaI18nConstant.INCREASE_DATA_FAILURE));
        }
        setAreaDeptInfoListAndInsert(areaInfo);
        //?????????????????????????????????
        areaAsync.afterAddAreaSuccess(areaInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AreaI18nConstant.INCREASE_DATA_SUCCESS));
    }


    /**
     * ??????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????
     */
    @Override
    public Result queryAreaListByItem(QueryCondition<AreaInfoDto> queryCondition) {
        if (queryCondition.getBizCondition() == null) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        AreaInfoDto areaInfoDto = queryCondition.getBizCondition();
        List<AreaInfo> areaInfos = areaInfoDao.queryAreaListByItem(areaInfoDto);
        //????????????id
        List<AreaInfoTree> areaInfoTreeList = setDeptIdByAreaIds(areaInfos);
        //????????????????????????????????????
        tlDepartmentList.set(getDepartmentList());
        //?????????
        selectChildAreaInfo(areaInfoTreeList);
        tlDepartmentList.remove();
        return ResultUtils.success(areaInfoTreeList);
    }

    /**
     * ??????id??????????????????
     *
     * @param areaId ??????id
     * @return ????????????
     */
    @Override
    public Result queryAreaById(String areaId) {
        //??????????????????
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(areaId);
        //???????????????????????????
        if (areaInfo == null) {
            return ResultUtils.warn(AreaResultCode.THIS_AREA_DOES_NOT_EXIST, I18nUtils.getSystemString(AreaI18nConstant.THIS_AREA_DOES_NOT_EXIST));
        }
        List<AreaInfo> areaInfoList = new ArrayList<>();
        areaInfoList.add(areaInfo);
        //????????????id
        List<AreaInfoTree> areaInfoTreeList = setDeptIdByAreaIds(areaInfoList);
        AreaInfoTree areaInfoTree = areaInfoTreeList.get(0);
        //?????????????????????????????????????????????
        tlDepartmentList.set(getDepartmentList());
        //??????????????????
        setAccountabilityUnitName(areaInfoTree.getAccountabilityUnit(), areaInfoTree.getAccountabilityUnitName());
        List<String> list = areaInfoDao.selectAreaIdByParentId(areaId);
        if (list != null && list.size() > 0) {
            areaInfoTree.setHasChild(true);
        } else {
            areaInfoTree.setHasChild(false);
        }
        tlDepartmentList.remove();
        return ResultUtils.success(areaInfoTree);
    }

    /**
     * ??????id?????????????????? for pda
     *
     * @param areaId ??????id
     * @return ????????????
     */
    @Override
    public AreaInfo queryAreaByIdForPda(String areaId) {
        //??????????????????
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(areaId);
        //???????????????????????????
        if (areaInfo == null) {
            throw new FilinkAreaDoesNotExistException();
        }
        return areaInfo;
    }

    /**
     * ??????id??????????????????
     *
     * @param areaInfo ????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.ADD_LOG_LOCAL_FILE, functionCode = AreaFunctionCodeConstant.UPDATE_AREA_FUNCTION_CODE, dataGetColumnName = "areaAndPrentName", dataGetColumnId = "areaId")
    @Override
    public Result updateAreaInfo(AreaInfo areaInfo) {
        systemLanguageUtil.querySystemLanguage();
        String areaId = areaInfo.getAreaId();
        //?????????????????????????????????
        AreaInfo areaInfo1 = areaInfoDao.selectAreaInfoById(areaId);
        if (areaInfo1 == null) {
            throw new FilinkAreaDoesNotExistException();
        }
        //????????????????????????
        /*  2019-08-20 ???????????????????????????,???????????????????????????
         List<String> currentUserPermissionsId = getCurrentUserPermissionsId();
        */
        //?????????????????????
        /*  2019-08-20 ???????????????????????????,???????????????????????????
          if (!currentUserPermissionsId.contains(areaInfo.getAreaId())) {
            throw new FilinkAreaNoDataPermissionsException();
          }
        */
        //????????????????????????????????????
        boolean flag = queryAreaDetailsCanChange(areaId);
        int level = areaInfo1.getLevel();
        //?????????????????????
        setLevel(areaInfo);
        areaInfo.setCreateTime(areaInfo1.getCreateTime());
        //????????????
        if (!flag) {
            //????????????
            Boolean aBoolean = areaInfoDao.updateAreaNameById(areaInfo);
            if (aBoolean) {
                areaAsync.afterUpdateAreaSuccess(areaInfo);
                return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AreaI18nConstant.UPDATE_SUCCESS));
            } else {
                throw new FiLinkAreaDirtyDataException(I18nUtils.getSystemString(AreaI18nConstant.UPDATE_FAILED));
            }
        }
        int updateLevel = areaInfo.getLevel();
        List<String> list1 = areaInfoDao.selectAreaIdByParentId(areaId);
        //??????????????????????????????
        if (list1 != null && list1.size() > 0 && level != updateLevel) {
            throw new FiLinkAreaDirtyDataException(I18nUtils.getSystemString(AreaI18nConstant.UPDATE_FAILED));
        } else if (updateLevel > FIVE) {
            throw new FiLinkAreaDirtyDataException(I18nUtils.getSystemString(AreaI18nConstant.UPDATE_FAILED));
        }
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaResultCode.NAME_IS_EXIST, I18nUtils.getSystemString(AreaI18nConstant.NAME_IS_EXIST));
        }
        // ???????????????????????????
        getAllName(areaInfo);
        int result = areaInfoDao.updateAreaInfoById(areaInfo);
        //??????????????????
        if (result != 1) {
            throw new FiLinkAreaDateBaseException(I18nUtils.getSystemString(AreaI18nConstant.UPDATE_FAILED));
        }
        //?????????????????????map
        Map<String, Object> deleteMap = new HashMap<String, Object>(1);
        deleteMap.put(AREA_ID, areaInfo.getAreaId());
        //?????????????????????
        List<AreaDeptInfo> list = areaDeptInfoDao.selectByMap(deleteMap);
        //???????????????
        Integer integer = areaDeptInfoDao.deleteByMap(deleteMap);
        if (integer != list.size()) {
            throw new FiLinkAreaDateBaseException(I18nUtils.getSystemString(AreaI18nConstant.UPDATE_FAILED));
        }
        //???????????????????????????
        setAreaDeptInfoListAndInsert(areaInfo);
        areaAsync.afterUpdateAreaSuccess(areaInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AreaI18nConstant.UPDATE_SUCCESS));
    }

    /**
     * ????????????
     *
     * @param map ??????????????????
     * @return ????????????
     */
    @Override
    public Boolean setAreaDevice(Map<String, List<String>> map) {
        systemLanguageUtil.querySystemLanguage();
        Set<String> areaIds = map.keySet();
        AreaInfo areaInfo = new AreaInfo();
        //??????????????????????????????????????????map??????????????????????????????????????????????????????????????????
        for (String areaId : areaIds) {
            areaInfo = areaInfoDao.selectAreaInfoById(areaId);
            if (areaInfo == null) {
                throw new FilinkAreaDoesNotExistException();
            }
        }
        //????????????????????????
        List<String> currentUserPermissionsIds = getCurrentUserPermissionsId();
        //?????????????????????
        if (!currentUserPermissionsIds.containsAll(areaIds)) {
            throw new FilinkAreaNoDataPermissionsException();
        }
        Boolean aBoolean = deviceInfoService.setAreaDevice(map, areaInfo);
        if (aBoolean) {
            addLogByMap(map);
        }
        return aBoolean;
    }

    /**
     * ??????????????????
     *
     * @param areaIds ??????id??????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteAreaByIds(List<String> areaIds) {
        systemLanguageUtil.querySystemLanguage();
        List<AreaInfo> areaInfoList = areaInfoDao.selectBatchIds(areaIds);

        if (areaInfoList.size() != areaIds.size()) {
            if (areaIds.size() > 1) {
                throw new FiLinkAreaDirtyDataException(I18nUtils.getSystemString(AreaI18nConstant.DELETED_AREA_EXISTS));
            }
            throw new FilinkAreaDoesNotExistException();
        }
        for (AreaInfo areaInfo : areaInfoList) {
            if ("1".equals(areaInfo.getIsDeleted())) {
                throw new FiLinkAreaDirtyDataException(I18nUtils.getSystemString(AreaI18nConstant.DELETED_AREA_EXISTS));
            }
            getAllName(areaInfo);
        }
        //????????????????????????
        List<String> currentUserPermissionsIds = getCurrentUserPermissionsId();
        //?????????????????????
        if (!currentUserPermissionsIds.containsAll(areaIds)) {
            throw new FilinkAreaNoDataPermissionsException();
        }
        //???????????????
        for (String areaId : areaIds) {
            //????????????id
            List<String> childrenIds = areaInfoDao.selectAreaIdByParentId(areaId);
            //????????????id??????null
            if (childrenIds.size() != 0) {
                return ResultUtils.warn(AreaResultCode.HAVE_CHILD, I18nUtils.getSystemString(AreaI18nConstant.HAVE_CHILD));
            }
            //?????????????????????????????????
            List<DeviceInfo> deviceInfoList = deviceInfoService.queryDeviceByAreaId(areaId);
            //?????????????????????????????????
            if (deviceInfoList != null && deviceInfoList.size() != 0) {
                return ResultUtils.warn(AreaResultCode.HAVE_DEVICE, I18nUtils.getSystemString(AreaI18nConstant.HAVE_DEVICE));
            }
        }
        //??????????????????
        int result = areaInfoDao.deleteAreaInfoByIds(areaIds);
        //???????????????????????????????????????
        if (result != areaIds.size()) {
            throw new FiLinkAreaDateBaseException(I18nUtils.getSystemString(AreaI18nConstant.DELETE_FAILED));
        }
        for (String areaId : areaIds) {
            Map<String, Object> map = new HashMap<String, Object>(1);
            map.put(AREA_ID, areaId);
            //???????????????
            deleteDept(map);
            try {
                RedisUtils.hRemove(DeviceConstant.AREAINFO_FOREIGN, areaId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        addLogByAreaInfos(areaInfoList);
        areaAsync.afterDeleteAreaSuccess(areaIds);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AreaI18nConstant.SUCCESSFULLY_DELETED));

    }

    /**
     * ????????????id????????????????????????
     *
     * @param areaIds ??????id??????
     * @return ??????id??????
     */
    @Override
    public List<AreaInfoForeignDto> selectAreaInfoByIds(List<String> areaIds) {
        List<AreaInfoForeignDto> areaInfoForeignDtoList = new ArrayList<>();
        if (!RedisUtils.hasKey(DeviceConstant.AREAINFO_FOREIGN)) {
            getAreaInfoFromRedis();

        }
        for (String areaId : areaIds) {
            AreaInfoForeignDto areaInfoForeignDto = (AreaInfoForeignDto) RedisUtils.hGet(DeviceConstant.AREAINFO_FOREIGN, areaId);
            if (areaInfoForeignDto != null) {
                areaInfoForeignDtoList.add(areaInfoForeignDto);
            }
        }
        return areaInfoForeignDtoList;
    }

    /**
     * ????????????id??????????????????
     *
     * @param deptIds ??????id??????
     * @return ??????????????????
     */
    @Override
    public List<AreaInfo> selectAreaInfoByDeptIds(List<String> deptIds) {
        List<AreaInfo> areaInfoList = new ArrayList<>();
        areaInfoList.addAll(areaInfoDao.selectAreaInfoByDeptIds(deptIds));
        return areaInfoList;
    }

    /**
     * ??????????????????????????????
     *
     * @param exportDto ????????????
     * @return ??????????????????
     */
    @Override
    public Result exportArea(ExportDto exportDto) {
        systemLanguageUtil.querySystemLanguage();
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = areaExport.insertTask(exportDto, SERVERNAME, I18nUtils.getSystemString(AreaI18nConstant.AREA_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(AreaResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(AreaI18nConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(AreaResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(AreaI18nConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(AreaResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto);
        areaExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AreaI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    @Override
    public Result selectForeignAreaInfo(List<String> userIds) {
        List<AreaInfoForeignDto> areaInfoForeignDtoList = getAreaInfoFromRedis();
        if (areaInfoForeignDtoList.size() == 0) {
            return ResultUtils.success(areaInfoForeignDtoList);
        }
        List<String> currentUserPermissionsId;
        if (userIds.size() == 0) {
            currentUserPermissionsId = getCurrentUserPermissionsId();
        } else {
            currentUserPermissionsId = getPermissionsId(userIds);
        }
        //????????????
        AreaUtil.areaForeignTreePermissionsFilter(areaInfoForeignDtoList, currentUserPermissionsId);
        //?????????????????????????????????
        List<AreaInfoForeignDto> areaInfoForeignDtos = transformToAreaInfoForeignDto(areaInfoForeignDtoList);
        return ResultUtils.success(areaInfoForeignDtos);
    }

    /**
     * ??????????????????????????????
     *
     * @return ????????????
     */
    @Override
    public Result selectSimultaneousForeignAreaInfo() {
        //????????????????????????
        List<String> currentUserPermissionsId = getCurrentUserPermissionsId();
        //??????????????????????????????
        List<AreaInfoForeignDto> areaInfoFromRedis = getAreaInfoFromRedis();
        //????????????
        AreaUtil.permissionsFilter(areaInfoFromRedis, currentUserPermissionsId);
        areaInfoFromRedis.sort(new AreaCreateTimeComparator());
        return ResultUtils.success(areaInfoFromRedis);
    }

    /**
     * ????????????id??????????????????id??????
     *
     * @param deptIds ??????id??????
     * @return ??????????????????
     */
    @Override
    public List<String> selectAreaIdsByDeptIds(List<String> deptIds) {
        return areaDeptInfoDao.selectAreaIdByDeptIds(deptIds);
    }

    /**
     * ????????????id??????????????????????????????
     *
     * @param deptIds ??????id??????
     * @return ????????????
     */
    @Override
    public List<AreaDeptInfo> selectAreaDeptInfosByDeptIds(List<String> deptIds) {
        return areaDeptInfoDao.selectAreaDeptInfoByDeptIds(deptIds);
    }

    /**
     * ????????????id????????????????????????????????????
     *
     * @param areaIds ??????id??????
     * @return ??????????????????
     */
    @Override
    public Result selectAreaDeptInfoByAreaIds(List<String> areaIds) {
        List<AreaDeptInfo> areaDeptInfos = areaDeptInfoDao.selectAreaDeptInfoByAreaIds(areaIds);
        return ResultUtils.success(areaDeptInfos);
    }


    /**
     * ????????????????????????????????????
     *
     * @param areaInfoForeignDtos ?????????????????????
     */
    private List<AreaInfoForeignDto> transformToAreaInfoForeignDto(List<AreaInfoForeignDto> areaInfoForeignDtos) {
        Map<Integer, List<AreaInfoForeignDto>> collect = areaInfoForeignDtos.stream().collect(Collectors.groupingBy(AreaInfoForeignDto::getAreaLevel));
        AreaCreateTimeComparator areaCreateTimeComparator = new AreaCreateTimeComparator();
        for (int i = 1; i < FIVE; i++) {
            List<AreaInfoForeignDto> areaInfoForeignDtos1 = collect.get(i);
            if (areaInfoForeignDtos1 == null || areaInfoForeignDtos1.size() == 0) {
                continue;
            }
            Collections.sort(areaInfoForeignDtos1, areaCreateTimeComparator);
            List<AreaInfoForeignDto> areaInfoForeignDtos2 = collect.get(i + 1);
            if (areaInfoForeignDtos2 == null || areaInfoForeignDtos2.size() == 0) {
                break;
            }
            Collections.sort(areaInfoForeignDtos2, areaCreateTimeComparator);
            for (AreaInfoForeignDto areaInfoForeignDto : areaInfoForeignDtos1) {
                for (AreaInfoForeignDto areaInfoForeignDto2 : areaInfoForeignDtos2) {
                    if (areaInfoForeignDto.getAreaId().equals(areaInfoForeignDto2.getParentAreaId())) {
                        areaInfoForeignDto.getChildren().add(areaInfoForeignDto2);
                    }
                }
            }
        }
        return collect.get(1);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param areaInfo ??????????????????
     * @return ?????????
     */
    private AreaInfo setLevel(AreaInfo areaInfo) {
        String parentId = areaInfo.getParentId();
        //????????????id?????????????????????????????????
        if (parentId != null && !("").equals(parentId)) {
            //??????????????????
            AreaInfo parentAreaInfo = areaInfoDao.selectAreaInfoById(parentId);
            //?????????????????????
            if (parentAreaInfo == null) {
                throw new FiLinkAreaDirtyDataException();
            }
            //??????????????????
            Integer parentLevel = parentAreaInfo.getLevel();
            //?????????????????????????????????
            if (parentLevel == null) {
                throw new FiLinkAreaDirtyDataException();
            }
            areaInfo.setLevel(parentLevel + 1);
            //????????????id???????????????????????????1
        } else {
            areaInfo.setLevel(1);
        }
        return areaInfo;
    }

    /**
     * ???????????????????????????
     *
     * @param areaInfo ????????????
     */
    private void getAllName(AreaInfo areaInfo) {
        String parentId = areaInfo.getParentId();
        String areaName = areaInfo.getAreaName();
        StringBuilder allName = new StringBuilder(areaName);
        int level = areaInfo.getLevel();
        if (StringUtils.isEmpty(parentId)) {
            areaInfo.setAreaAndPrentName(allName.toString());
        }
        ToTopAreaInfo toTopAreaInfo = areaInfoDao.selectTopArea(areaInfo.getParentId());
        for (int i = 1; i < level; i++) {
            if (toTopAreaInfo != null) {
                allName.insert(0, toTopAreaInfo.getAreaName() + "-");
                toTopAreaInfo = toTopAreaInfo.getParentAreaInfo();
            }
        }
        areaInfo.setAreaAndPrentName(allName.toString());
    }

    /**
     * ??????????????????
     *
     * @param accountabilityUnit     ??????id??????
     * @param accountabilityUnitName ????????????et
     */
    public void setAccountabilityUnitName(Set<String> accountabilityUnit, StringBuilder accountabilityUnitName) {
        List<Department> departmentList = tlDepartmentList.get();
        if (accountabilityUnit == null || accountabilityUnit.size() == 0) {
            return;
        }
        if (departmentList == null) {
            return;
        }
        for (String id : accountabilityUnit) {
            //???dept??????????????????????????????????????????
            for (Department department : departmentList) {
                if (id.equals(department.getId())) {
                    accountabilityUnitName.append(department.getDeptName() + ",");
                }
            }
        }
        if (accountabilityUnitName.length() == 0) {
            accountabilityUnitName.append(",");
        }
        //????????????????????????
        accountabilityUnitName.deleteCharAt(accountabilityUnitName.lastIndexOf(","));
    }

    /**
     * ??????????????????
     *
     * @param areaIdList
     * @return
     */
    @Override
    public List<AreaInfo> queryAreaInfoByIds(List<String> areaIdList) {
        if (ObjectUtils.isEmpty(areaIdList)) {
            return new ArrayList<>();
        }

        //??????????????????
        List<AreaInfo> areaInfoList = areaInfoDao.selectAreaInfoByIds(areaIdList);
        //??????ID
        List<AreaDeptInfo> areaDeptInfos = areaDeptInfoDao.selectAreaDeptInfoByAreaIds(areaIdList);

        //?????????????????????????????????????????????
        tlDepartmentList.set(getDepartmentList());

        for (AreaInfo areaInfo : areaInfoList) {
            Set<String> deptIds = queryDeptIdListByAreaId(areaDeptInfos, areaInfo.getAreaId());

            StringBuilder deptName = new StringBuilder();
            //??????????????????
            setAccountabilityUnitName(deptIds, deptName);
            areaInfo.setAccountabilityUnit(deptIds);
            areaInfo.setAccountabilityUnitName(deptName);
        }
        tlDepartmentList.remove();
        return areaInfoList;
    }

    /**
     * ????????????id ???????????????????????????
     *
     * @param areaIdList ??????id
     * @return ????????????
     */
    @Override
    public List<Department> queryDeptCommonByAreaId(List<String> areaIdList) {
        if (areaIdList == null || areaIdList.size() == 0) {
            log.info("param is null by queryDeptCommonByAreaId");
            return null;
        }
        List<String> deptIds = Lists.newArrayList();
        // ????????????id ???????????????????????????
        List<AreaDeptInfo> areaDeptInfos = areaDeptInfoDao.selectAreaDeptInfoByAreaIds(areaIdList);
        if (areaIdList.size() == 1) {
            deptIds = areaDeptInfos.stream().map(AreaDeptInfo::getDeptId).collect(Collectors.toList());
        } else {
            // 1 ????????????id ????????????????????????????????????
            Map<String, List<AreaDeptInfo>> collect = areaDeptInfos.stream().collect(Collectors.groupingBy(AreaDeptInfo::getAreaId));
            if (collect.size() != areaIdList.size()) {
                // ????????????????????? ????????????null
                return null;
            }
            List<List<String>> lists = Lists.newArrayList();
            areaIdList.stream().forEach(obj -> {
                List<AreaDeptInfo> deptInfos = collect.get(obj);
                lists.add(deptInfos.stream().map(AreaDeptInfo::getDeptId).collect(Collectors.toList()));
            });
            // 2 ???????????? ????????????
            Optional<List<String>> result = lists.parallelStream()
                    .filter(areas -> areas != null && ((List) areas).size() != 0)
                    .reduce((a, b) -> {
                        a.retainAll(b);
                        return a;
                    });
            deptIds = result.orElse(new ArrayList<>());
        }
        return deptIds == null ? null : departmentFeign.queryDepartmentFeignById(deptIds);
    }


    /**
     * ???????????????????????????
     *
     * @param areaDeptInfos
     * @param areaId
     * @return
     */
    private Set<String> queryDeptIdListByAreaId(List<AreaDeptInfo> areaDeptInfos, String areaId) {
        if (areaId == null || areaDeptInfos == null) {
            return null;
        }
        Set<String> deptIdList = new HashSet<>();
        for (AreaDeptInfo areaDeptInfo : areaDeptInfos) {
            if (areaId.equals(areaDeptInfo.getAreaId())) {
                deptIdList.add(areaDeptInfo.getDeptId());
            }
        }
        return deptIdList;
    }

    /**
     * ??????????????????????????????
     *
     * @param areaInfo ????????????
     */
    private void setAreaDeptInfoListAndInsert(AreaInfo areaInfo) {
        Set<String> accountabilityUnit = areaInfo.getAccountabilityUnit();
        if (accountabilityUnit == null || accountabilityUnit.size() == 0) {
            return;
        }
        List<Department> departmentList = getDepartmentList();
        List<AreaDeptInfo> areaDeptInfoList = new ArrayList<AreaDeptInfo>();
        //?????????????????????id??????
        List<String> deptIdList = new ArrayList<>();
        for (Department department : departmentList) {
            deptIdList.add(department.getId());
        }
        for (String accountabilityUnitId : accountabilityUnit) {
            //???????????????id?????????
            if (!deptIdList.contains(accountabilityUnitId)) {
                throw new FiLinkAreaDirtyDataException();
            }
            //?????????????????????
            AreaDeptInfo areaDeptInfo = new AreaDeptInfo();
            //??????????????????id
            areaDeptInfo.setAreaId(areaInfo.getAreaId());
            //??????????????????id
            areaDeptInfo.setDeptId(accountabilityUnitId);
            //??????????????????????????????
            areaDeptInfoList.add(areaDeptInfo);
        }
        Integer integer = areaDeptInfoDao.addAreaDeptInfoBatch(areaDeptInfoList);
        if (areaDeptInfoList.size() != integer) {
            throw new FiLinkAreaDateBaseException();
        }
    }

    /**
     * ????????????????????? ???????????????????????????
     *
     * @param areaInfoTreeList ???????????????
     */
    public void selectChildAreaInfo(List<AreaInfoTree> areaInfoTreeList) {
        if (areaInfoTreeList.size() == 0) {
            return;
        }
        int level = areaInfoTreeList.get(0).getLevel();
        List<AreaInfo> areaInfoList2 = areaInfoDao.selectAreaInfoByLevel(level + 1);
        //????????????
        List<String> currentUserPermissionsId = getCurrentUserPermissionsId();
        AreaUtil.areaTreePermissionsFilter(areaInfoList2, currentUserPermissionsId);
        //????????????id
        List<AreaInfoTree> areaInfoTreeList2 = setDeptIdByAreaIds(areaInfoList2);
        for (AreaInfoTree areaInfoTree : areaInfoTreeList) {
            setChildAreaInfo(areaInfoTree, areaInfoTreeList2);
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param areaInfoTree     ?????????
     * @param areaInfoTreeList ??????????????????
     * @return
     */
    private AreaInfoTree setChildAreaInfo(AreaInfoTree areaInfoTree, List<AreaInfoTree> areaInfoTreeList) {
        //??????????????????
        setAccountabilityUnitName(areaInfoTree.getAccountabilityUnit(), areaInfoTree.getAccountabilityUnitName());
        for (AreaInfoTree areaInfoTreeFromList : areaInfoTreeList) {
            if (areaInfoTree.getChildren() == null) {
                areaInfoTree.setChildren(new ArrayList<>());
            }
            if (areaInfoTree.getAreaId().equals(areaInfoTreeFromList.getParentId())) {
                areaInfoTree.getChildren().add(setChildAreaInfo(areaInfoTreeFromList, areaInfoTreeList));
            }
        }
        return areaInfoTree;
    }

    /**
     * ??????????????????
     *
     * @param deleteMap ??????map
     */
    private void deleteDept(Map<String, Object> deleteMap) {
        //?????????????????????
        List<AreaDeptInfo> list = areaDeptInfoDao.selectByMap(deleteMap);
        //???????????????
        Integer integer = areaDeptInfoDao.deleteByMap(deleteMap);
        if (integer != list.size()) {
            throw new FiLinkAreaDateBaseException();
        }
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    @Override
    public Result queryAreaListAll() {
        //??????????????????
        List<AreaInfoForeignDto> areaInfoForeignDtoList = getAreaInfoFromRedis();
        //????????????
        if (areaInfoForeignDtoList.size() == 0) {
            return ResultUtils.success(areaInfoForeignDtoList);
        }
        //????????????
        AreaUtil.areaForeignTreePermissionsFilter(areaInfoForeignDtoList, getCurrentUserPermissionsId());
        areaInfoForeignDtoList.sort(new AreaCreateTimeComparator());
        return ResultUtils.success(areaInfoForeignDtoList);
    }

    /**
     * ????????????????????????????????????????????????
     * * @param areaIds  ????????????
     *
     * @return ????????????
     */
    @Override
    public String queryAreaListAllForPermission(List<String> areaIds) {
        //??????????????????
        List<AreaInfoForeignDto> areaInfoForeignDtoList = getAreaInfoFromRedis();
        if (!CollectionUtils.isEmpty(areaInfoForeignDtoList)) {
            areaInfoForeignDtoList.sort(new AreaCreateTimeComparator());
            if (CollectionUtils.isEmpty(areaIds)) {
                return areaInfoForeignDtoList.get(0).getAreaId();
            }
            //????????????
            for (AreaInfoForeignDto areaInfoForeignDto : areaInfoForeignDtoList) {
                String areaId = areaInfoForeignDto.getAreaId();
                if (areaIds.contains(areaId)) {
                    return areaId;
                }
            }
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param deptIds ??????id??????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteAreaDeptRelation(List<String> deptIds) {
        for (String deptId : deptIds) {
            //????????????map
            Map<String, Object> map = new HashMap<String, Object>(1);
            map.put(DEPT_ID, deptId);
            deleteDept(map);
        }
        return true;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param areaId ??????id
     * @return ????????????
     */
    @Override
    public Boolean queryAreaDetailsCanChange(String areaId) {
        ArrayList<String> areaIds = new ArrayList<>();
        areaIds.add(areaId);
        Result<List<String>> result = procBaseFeign.queryProcExitsForAreaIds(areaIds);
        if (result == null) {
            throw new FilinkAreaException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_WORK_ORDER_INFORMATION));
        }
        List<String> proAreaIds = (List<String>) result.getData();
        if (proAreaIds != null && proAreaIds.size() > 0) {
            return false;
        }
        List<String> list = alarmCurrentFeign.queryAreaForFeign(areaIds);
        if (list == null) {
            throw new FilinkAreaException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_ALARM_INFORMATION));
        }
        return list.size() == 0;
    }

    /**
     * ???????????????????????????
     *
     * @param areaInfoList ??????????????????
     */
    private void addLogByAreaInfos(List<AreaInfo> areaInfoList) {
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //????????????
        //??????????????????
        if (areaInfoList.size() == 0) {
            return;
        }
        for (AreaInfo areaInfo : areaInfoList) {
            //??????????????????
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("areaId");
            addLogBean.setDataName("areaAndPrentName");
            //??????????????????id
            addLogBean.setOptObjId(areaInfo.getAreaId());
            //???????????????
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBean.setOptObj(areaInfo.getAreaAndPrentName());
            addLogBean.setFunctionCode(AreaFunctionCodeConstant.DELETE_AREA_FUNCTION_CODE);

            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //??????????????????
            logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * ????????????????????????
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //??????????????????id
        addLogBean.setOptObjId("export");
        //???????????????
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(AreaFunctionCodeConstant.LIST_EXPORT_FUNCTION_CODE);
        //??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ?????????????????????
     *
     * @param map ????????????
     */
    private void addLogByMap(Map<String, List<String>> map) {
        List<AddOperateLogReq> logInfo = new ArrayList<AddOperateLogReq>();
        //????????????
        //??????????????????
        if (map.size() == 0) {
            return;
        }
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String areaId = entry.getKey();
            AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(areaId);
            getAllName(areaInfo);
            List<String> deviceIdList = entry.getValue();
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
            String deviceNameStringBuilder = getDeviceNameStringBuilder(deviceIdList);
            AddOperateLogReq addOperateLogReq = new AddOperateLogReq();
            BeanUtils.copyProperties(addLogBean, addOperateLogReq);
            //????????????
            addOperateLogReq.setOptObj(areaInfo.getAreaAndPrentName());
            //??????id
            addOperateLogReq.setOptObjId(areaId);
            addOperateLogReq.setFunctionCode(AreaFunctionCodeConstant.ASSOCIATED_FACILITY_FUNCTION_CODE);
            //????????????
            String environment = ((LanguageConfig) SpringUtils.getBean(LanguageConfig.class)).getEnvironment();
            //????????????????????????
            String detailTemplateInfo = "";
            String optName = "";
            try {
                XmlParseBean xmlParseBean = logCastProcess.dom4jParseXml(addOperateLogReq.getFunctionCode(), environment);
                //????????????????????????????????????
                detailTemplateInfo = xmlParseBean.getDetailInfoTemplate();
                //????????????????????????,??????xml???????????????
                if (null != detailTemplateInfo) {
                    detailTemplateInfo = detailTemplateInfo.replace("${areaAndPrentName}", areaInfo.getAreaAndPrentName());
                    detailTemplateInfo = detailTemplateInfo.replace("${deviceName}", deviceNameStringBuilder);
                    detailTemplateInfo = detailTemplateInfo.replace("${optUserName}", addOperateLogReq.getOptUserName());
                }
                optName = xmlParseBean.getOptName();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //????????????????????????
            addOperateLogReq.setDetailInfo(detailTemplateInfo);
            //????????????????????????
            addOperateLogReq.setOptName(optName);
            //?????????????????????????????????????????????
            addOperateLogReq.setAddLocalFile(LogConstants.ADD_LOG_LOCAL_FILE);
            logInfo.add(addOperateLogReq);
        }
        //??????????????????????????????
        logProcess.addOperateLogBatchInfoToAutoCall(logInfo, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ????????????????????????????????????
     *
     * @return ????????????
     */
    public List<Department> getDepartmentList() {
        List<Department> departmentList = departmentFeign.queryAllDepartment();
        if (departmentList == null) {
            throw new FilinkAreaException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
        }
        return departmentList;
    }

    /**
     * ??????????????????
     *
     * @param deviceIdList id??????
     * @return ????????????
     */
    private String getDeviceNameStringBuilder(List<String> deviceIdList) {
        StringBuilder deviceNameStringBuilder = new StringBuilder();
        if (deviceIdList.size() == 0) {
            return deviceNameStringBuilder.toString();
        }
        List<DeviceInfo> deviceInfoList = deviceInfoDao.selectByIds(deviceIdList);
        if (deviceInfoList == null || deviceInfoList.size() == 0) {
            return deviceNameStringBuilder.toString();
        }
        DeviceInfo deviceInfo1 = deviceInfoList.get(0);
        deviceNameStringBuilder.append(deviceInfo1.getDeviceName());
        for (int i = 1; i < deviceInfoList.size(); i++) {
            deviceNameStringBuilder.append("," + deviceInfoList.get(i).getDeviceName());
        }
        return deviceNameStringBuilder.toString();
    }

    /**
     * ??????????????????????????????id
     *
     * @param areaInfos ??????????????????
     */
    public List<AreaInfoTree> setDeptIdByAreaIds(List<AreaInfo> areaInfos) {
        List<AreaInfoTree> areaInfoTreeList = new ArrayList<>();
        if (areaInfos.size() == 0) {
            return areaInfoTreeList;
        }
        List<String> areaIds = new ArrayList<>();
        for (AreaInfo areaInfo : areaInfos) {
            areaIds.add(areaInfo.getAreaId());
            AreaInfoTree areaInfoTree = new AreaInfoTree();
            BeanUtils.copyProperties(areaInfo, areaInfoTree);
            areaInfoTreeList.add(areaInfoTree);
        }
        List<AreaDeptInfo> areaDeptInfos = areaDeptInfoDao.selectAreaDeptInfoByAreaIds(areaIds);
        if (areaDeptInfos == null || areaDeptInfos.size() == 0) {
            return areaInfoTreeList;
        }
        for (AreaInfoTree areaInfoTree : areaInfoTreeList) {
            Set<String> accountabilityUnits = new HashSet<>();
            for (AreaDeptInfo areaDeptInfo : areaDeptInfos) {
                if (areaInfoTree.getAreaId().equals(areaDeptInfo.getAreaId())) {
                    accountabilityUnits.add(areaDeptInfo.getDeptId());
                }
            }
            areaInfoTree.setAccountabilityUnit(accountabilityUnits);
        }
        return areaInfoTreeList;
    }

    /**
     * ??????id??????????????????????????????
     *
     * @param areaInfoForeignDto ????????????
     */
    public void updateRedisArea(AreaInfoForeignDto areaInfoForeignDto) {
        RedisUtils.hSet(DeviceConstant.AREAINFO_FOREIGN, areaInfoForeignDto.getAreaId(), areaInfoForeignDto);
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @Override
    public List<AreaInfoForeignDto> getAreaInfoFromRedis() {
        List<AreaInfoForeignDto> areaInfoForeignDtoList = new ArrayList<>();
        if (RedisUtils.hasKey(DeviceConstant.AREAINFO_FOREIGN)) {
            Map<Object, Object> objectObjectMap = RedisUtils.hGetMap(DeviceConstant.AREAINFO_FOREIGN);
            for (Object obj : objectObjectMap.values()) {
                AreaInfoForeignDto areaInfoForeignDto = (AreaInfoForeignDto) obj;
                areaInfoForeignDtoList.add(areaInfoForeignDto);
            }
        } else {
            areaInfoForeignDtoList = areaInfoDao.queryForeignAreaListAll();
            if (areaInfoForeignDtoList == null || areaInfoForeignDtoList.size() == 0) {
                return new ArrayList<>();
            }
            Map<String, Object> areaInfoRedis = new HashMap<>(128);
            for (AreaInfoForeignDto areaInfoForeignDto : areaInfoForeignDtoList) {
                areaInfoRedis.put(areaInfoForeignDto.getAreaId(), areaInfoForeignDto);
            }
            RedisUtils.hSetMap(DeviceConstant.AREAINFO_FOREIGN, areaInfoRedis);
        }
        return areaInfoForeignDtoList;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param fe ??????
     * @return ????????????
     */
    private Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        fe.printStackTrace();
        String string = I18nUtils.getSystemString(AreaI18nConstant.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(AreaResultCode.EXPORT_DATA_TOO_LARGE, msg);
    }

    /**
     * ???????????????????????????id
     *
     * @param userIds ??????id??????
     * @return
     */
    public List<String> getPermissionsId(List<String> userIds) {
        if (userIds.contains(DeviceConstant.ADMIN)) {
            return getRedisAllAreaIds();
        }
        List<String> areaIds = new ArrayList<>();
        List<Object> objects = (List<Object>) userFeign.queryUserByIdList(userIds);
        if (objects == null) {
            throw new FilinkAreaException(I18nUtils.getSystemString(AreaI18nConstant.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
        }
        if (objects.size() == 0) {
            throw new FilinkAreaNoDataPermissionsException();
        }
        for (Object o : objects) {
            User user = JSONArray.toJavaObject((JSON) JSONArray.toJSON(o), User.class);
            List<String> areaIdList = user.getDepartment().getAreaIdList();
            if (areaIdList != null && areaIdList.size() > 0) {
                areaIds.addAll(areaIdList);
            }
        }
        return areaIds;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public List<String> getCurrentUserPermissionsId() {
        List<String> userIds = new ArrayList<>();
        String userId = RequestInfoUtils.getUserId();
        userIds.add(userId);
        return getPermissionsId(userIds);
    }

    /**
     * ???????????????????????????id??????
     *
     * @return
     */
    private List<String> getRedisAllAreaIds() {
        if (!RedisUtils.hasKey(DeviceConstant.AREAINFO_FOREIGN)) {
            getAreaInfoFromRedis();
        }
        List<String> areaIds = new ArrayList<>();
        Map<Object, Object> objectObjectMap = RedisUtils.hGetMap(DeviceConstant.AREAINFO_FOREIGN);
        for (Object o : objectObjectMap.keySet()) {
            areaIds.add((String) o);
        }
        return areaIds;
    }

    /**
     * ????????????????????????
     */
    public void sendUpdateUserInfo() {
        Message msg = MessageBuilder.withPayload(DeviceConstant.UPDATE_USER_INFO).build();
        deviceStreams.updateUserInfo().send(msg);
    }

    /**
     * ??????????????????????????????
     *
     * @param areaIds ??????id??????
     */
    public void deleteRedisArea(List<String> areaIds) {
        areaIds.forEach(areaId -> {
            try {
                RedisUtils.hRemove(DeviceConstant.AREAINFO_FOREIGN, areaId);
            } catch (Exception e) {
                e.printStackTrace();

            }
        });
    }
}
