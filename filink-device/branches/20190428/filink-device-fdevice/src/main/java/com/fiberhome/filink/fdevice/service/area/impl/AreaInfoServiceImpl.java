package com.fiberhome.filink.fdevice.service.area.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
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
import com.fiberhome.filink.fdevice.constant.area.AreaResultCodeConstant;
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
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.fiberhome.filink.fdevice.constant.area.AreaConstant.*;

/**
 * <p>
 * 区域信息服务实现类
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
     * 自动注入区域dao对象
     */
    @Autowired
    private AreaInfoDao areaInfoDao;
    /**
     * 自动注入部门dao对象
     */
    @Autowired
    private AreaDeptInfoDao areaDeptInfoDao;
    /**
     * 自动注入设施dao对象
     */
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    /**
     * 自动注入设施service
     */
    @Autowired
    private DeviceInfoService deviceInfoService;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogCastProcess logCastProcess;

    /**
     * 远程调用部门服务
     */
    @Autowired
    private DepartmentFeign departmentFeign;
    /**
     * 远程调用权限服务实体
     */
    @Autowired
    private UserFeign userFeign;
    /**
     * 远程调用告警服务
     */
    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;
    /**
     * 导出类
     */
    @Autowired
    private AreaExport areaExport;
    /**
     * 注入导出发送消息实体
     */
    @Autowired
    private DeviceStreams deviceStreams;
    /**
     * departmentList 用于存储远程调用的单位信息
     */
    public static InheritableThreadLocal<List<Department>> tlDepartmentList = new InheritableThreadLocal<>();
    /**
     * 最大导出条数
     */
    @Value(MAX_EXPORT_DATA_SIZE)
    private Integer maxExportDataSize;
    /**
     * 自动注入区域异步线程类
     */
    @Autowired
    private AreaAsync areaAsync;
    /**
     * 远程调用工单
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;

    /**
     * 查询区域名是否存在
     *
     * @param areaInfo 查询名称
     * @return 查询结果
     */
    @Override
    public Result queryAreaNameIsExist(AreaInfo areaInfo) {
        //查询数据库得到的区域信息对象
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaResultCodeConstant.NAME_IS_EXIST, I18nUtils.getString(AreaI18nConstant.NAME_IS_EXIST));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18nConstant.NAME_IS_AVAILABLE));
    }


    /**
     * 新增区域
     *
     * @param areaInfo 区域信息
     * @return 新增结果
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.ADD_LOG_LOCAL_FILE, functionCode = AreaFunctionCodeConstant.ADD_AREA_FUNCTION_CODE, dataGetColumnName = "areaAndPrentName", dataGetColumnId = "areaId")
    @Override
    public Result addArea(AreaInfo areaInfo) {
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaResultCodeConstant.NAME_IS_EXIST, I18nUtils.getString(AreaI18nConstant.NAME_IS_EXIST));
        }
        //生成并设置区域id
        areaInfo.setAreaId(NineteenUUIDUtils.uuid());
        areaInfo = setLevel(areaInfo);
        if (FIVE < areaInfo.getLevel()) {
            throw new FilinkAreaDateFormatException();
        }
        //增加返回结果
        int result = areaInfoDao.addAreaInfo(areaInfo);
        //设置日志区域名称
        getAllName(areaInfo);
        //判断返回结果
        if (result != 1) {
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18nConstant.INCREASE_DATA_FAILURE));
        }
        setAreaDeptInfoListAndInsert(areaInfo);
        //新增区域完成的异步方法
        areaAsync.afterAddAreaSuccess(areaInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18nConstant.INCREASE_DATA_SUCCESS));
    }


    /**
     * 获取区域列表
     *
     * @param queryCondition 查询条件
     * @return 区域信息
     */
    @Override
    public Result queryAreaListByItem(QueryCondition<AreaInfoDto> queryCondition) {
        if (queryCondition.getBizCondition() == null) {
            return ResultUtils.warn(AreaResultCodeConstant.PARAM_NULL, I18nUtils.getString(AreaI18nConstant.PARAM_NULL));
        }
        AreaInfoDto areaInfoDto = queryCondition.getBizCondition();
        List<AreaInfo> areaInfos = areaInfoDao.queryAreaListByItem(areaInfoDto);
        //权限过滤
        List<String> currentUserPermissionsId = getCurrentUserPermissionsId();
        AreaUtil.areaTreePermissionsFilter(areaInfos, currentUserPermissionsId);
        //设置单位id
        List<AreaInfoTree> areaInfoTreeList = setDeptIdByAreaIds(areaInfos);
        //获取单位信息并保存到属性
        tlDepartmentList.set(getDepartmentList());
        //拼接树
        selectChildAreaInfo(areaInfoTreeList);
        tlDepartmentList.remove();
        return ResultUtils.success(areaInfoTreeList);
    }

    /**
     * 根据id查找区域信息
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    @Override
    public Result queryAreaById(String areaId) {
        //查找出的实体
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(areaId);
        //判断该信息是否存在
        if (areaInfo == null) {
            return ResultUtils.warn(AreaResultCodeConstant.THIS_AREA_DOES_NOT_EXIST, I18nUtils.getString(AreaI18nConstant.THIS_AREA_DOES_NOT_EXIST));
        }
        List<AreaInfo> areaInfoList = new ArrayList<>();
        areaInfoList.add(areaInfo);
        //获取单位id
        List<AreaInfoTree> areaInfoTreeList = setDeptIdByAreaIds(areaInfoList);
        AreaInfoTree areaInfoTree = areaInfoTreeList.get(0);
        //获取单位信息数据并保存到属性中
        tlDepartmentList.set(getDepartmentList());
        //设置单位名称
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
     * 根据id查找区域信息 for pda
     *
     * @param areaId 区域id
     * @return 区域信息
     */
    @Override
    public AreaInfo queryAreaByIdForPda(String areaId) {
        //查找出的实体
        AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(areaId);
        //判断该信息是否存在
        if (areaInfo == null) {
            throw new FilinkAreaDoesNotExistException();
        }
        return areaInfo;
    }

    /**
     * 根据id修改区域信息
     *
     * @param areaInfo 区域信息
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.ADD_LOG_LOCAL_FILE, functionCode = AreaFunctionCodeConstant.UPDATE_AREA_FUNCTION_CODE, dataGetColumnName = "areaAndPrentName", dataGetColumnId = "areaId")
    @Override
    public Result updateAreaInfo(AreaInfo areaInfo) {
        String areaId = areaInfo.getAreaId();
        //判断该区域信息是否存在
        AreaInfo areaInfo1 = areaInfoDao.selectAreaInfoById(areaId);
        if (areaInfo1 == null) {
            throw new FilinkAreaDoesNotExistException();
        }
        //获取当前用户权限
        List<String> currentUserPermissionsId = getCurrentUserPermissionsId();
        //判断是否有权限
        if (!currentUserPermissionsId.contains(areaInfo.getAreaId())) {
            throw new FilinkAreaNoDataPermissionsException();
        }
        //是否可以修改区域详细信息
        boolean flag = queryAreaDetailsCanChange(areaId);
        int level = areaInfo1.getLevel();
        //计算更改的级别
        setLevel(areaInfo);
        //如果不能
        if (!flag) {
            //修改名称
            Boolean aBoolean = areaInfoDao.updateAreaNameById(areaInfo);
            if (aBoolean) {
                areaAsync.afterUpdateAreaSuccess(areaInfo);
                return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18nConstant.UPDATE_SUCCESS));
            } else {
                throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18nConstant.UPDATE_FAILED));
            }
        }
        int updateLevel = areaInfo.getLevel();
        List<String> list1 = areaInfoDao.selectAreaIdByParentId(areaId);
        //判断是否可以改变级别
        if (list1 != null && list1.size() > 0 && level != updateLevel) {
            throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18nConstant.UPDATE_FAILED));
        } else if (updateLevel > FIVE) {
            throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18nConstant.UPDATE_FAILED));
        }
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaResultCodeConstant.NAME_IS_EXIST, I18nUtils.getString(AreaI18nConstant.NAME_IS_EXIST));
        }
        // 取得日志拼接的名称
        getAllName(areaInfo);
        int result = areaInfoDao.updateAreaInfoById(areaInfo);
        //判断返回结果
        if (result != 1) {
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18nConstant.UPDATE_FAILED));
        }
        //构建关系表删除map
        Map<String, Object> deleteMap = new HashMap<String, Object>(1);
        deleteMap.put(AREA_ID, areaInfo.getAreaId());
        //查询关系表数据
        List<AreaDeptInfo> list = areaDeptInfoDao.selectByMap(deleteMap);
        //删除关系表
        Integer integer = areaDeptInfoDao.deleteByMap(deleteMap);
        if (integer != list.size()) {
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18nConstant.UPDATE_FAILED));
        }
        //设置单位信息并添加
        setAreaDeptInfoListAndInsert(areaInfo);
        areaAsync.afterUpdateAreaSuccess(areaInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18nConstant.UPDATE_SUCCESS));
    }

    /**
     * 关联设施
     *
     * @param map 关联设施信息
     * @return 操作结果
     */
    @Override
    public Boolean setAreaDevice(Map<String, List<String>> map) {
        Set<String> areaIds = map.keySet();
        AreaInfo areaInfo = new AreaInfo();
        //校验是该区域是否存在，因为该map中只有一个数据，此处使用遍历，可支持多个数据
        for (String areaId : areaIds) {
            areaInfo = areaInfoDao.selectAreaInfoById(areaId);
            if (areaInfo == null) {
                throw new FilinkAreaDoesNotExistException();
            }
        }
        //获取当前用户权限
        List<String> currentUserPermissionsIds = getCurrentUserPermissionsId();
        //判断是否有权限
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
     * 删除区域信息
     *
     * @param areaIds 区域id集合
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteAreaByIds(List<String> areaIds) {
        List<AreaInfo> areaInfoList = areaInfoDao.selectBatchIds(areaIds);

        if (areaInfoList.size() != areaIds.size()) {
            if (areaIds.size() > 1) {
                throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18nConstant.DELETED_AREA_EXISTS));
            }
            throw new FilinkAreaDoesNotExistException();
        }
        for (AreaInfo areaInfo : areaInfoList) {
            if ("1".equals(areaInfo.getIsDeleted())) {
                throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18nConstant.DELETED_AREA_EXISTS));
            }
            getAllName(areaInfo);
        }
        //获取当前用户权限
        List<String> currentUserPermissionsIds = getCurrentUserPermissionsId();
        //判断是否有权限
        if (!currentUserPermissionsIds.containsAll(areaIds)) {
            throw new FilinkAreaNoDataPermissionsException();
        }
        //遍历该集合
        for (String areaId : areaIds) {
            //获取子类id
            List<String> childrenIds = areaInfoDao.selectAreaIdByParentId(areaId);
            //如果子类id不为null
            if (childrenIds.size() != 0) {
                //遍历子类id
                for (String childId : childrenIds) {
                    //如果要删除集合中不包含此子类id
                    if (!areaIds.contains(childId)) {
                        return ResultUtils.warn(AreaResultCodeConstant.HAVE_CHILD, I18nUtils.getString(AreaI18nConstant.HAVE_CHILD));
                    }
                }
            }
            //查询该区域下的关联设施
            List<DeviceInfo> deviceInfoList = deviceInfoService.queryDeviceByAreaId(areaId);
            //如果该区域下有关联设施
            if (deviceInfoList != null && deviceInfoList.size() != 0) {
                return ResultUtils.warn(AreaResultCodeConstant.HAVE_DEVICE, I18nUtils.getString(AreaI18nConstant.HAVE_DEVICE));
            }
        }
        //获取删除条数
        int result = areaInfoDao.deleteAreaInfoByIds(areaIds);
        //如果删除条数与集合中不相等
        if (result != areaIds.size()) {
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18nConstant.DELETE_FAILED));
        }
        for (String areaId : areaIds) {
            Map<String, Object> map = new HashMap<String, Object>(1);
            map.put(AREA_ID, areaId);
            //删除关系表
            deleteDept(map);
            try {
                RedisUtils.hRemove(DeviceConstant.AREAINFO_FOREIGN, areaId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        addLogByAreaInfos(areaInfoList);
        areaAsync.afterDeleteAreaSuccess(areaIds);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18nConstant.SUCCESSFULLY_DELETED));

    }

    /**
     * 根据区域id集合查找区域信息
     *
     * @param areaIds 区域id集合
     * @return 区域id集合
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
     * 根据部门id获取区域信息
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @Override
    public List<AreaInfo> selectAreaInfoByDeptIds(List<String> deptIds) {
        List<AreaInfo> areaInfoList = new ArrayList<>();
        areaInfoList.addAll(areaInfoDao.selectAreaInfoByDeptIds(deptIds));
        return areaInfoList;
    }

    /**
     * 创建导出区域列表任务
     *
     * @param exportDto 传入信息
     * @return 创建任务结果
     */
    @Override
    public Result exportArea(ExportDto exportDto) {
        Export export;
        try {
            export = areaExport.insertTask(exportDto, SERVERNAME, I18nUtils.getString(AreaI18nConstant.AREA_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(AreaResultCodeConstant.EXPORT_NO_DATA, I18nUtils.getString(AreaI18nConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(AreaResultCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getString(AreaI18nConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(AreaResultCodeConstant.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getString(AreaI18nConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto);
        areaExport.exportData(export);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 查询选择区域树结构
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
        //权限过滤
        AreaUtil.areaForeignTreePermissionsFilter(areaInfoForeignDtoList, currentUserPermissionsId);
        //转换为外部需要的树结构
        List<AreaInfoForeignDto> areaInfoForeignDtos = transformToAreaInfoForeignDto(areaInfoForeignDtoList);
        return ResultUtils.success(areaInfoForeignDtos);
    }

    /**
     * 查询对外区域平行结构
     *
     * @return 查询结果
     */
    @Override
    public Result selectSimultaneousForeignAreaInfo() {
        //获取当前用户权限
        List<String> currentUserPermissionsId = getCurrentUserPermissionsId();
        //从缓存中拿到所有数据
        List<AreaInfoForeignDto> areaInfoFromRedis = getAreaInfoFromRedis();
        //权限过滤
        AreaUtil.permissionsFilter(areaInfoFromRedis, currentUserPermissionsId);
        return ResultUtils.success(areaInfoFromRedis);
    }

    /**
     * 根据部门id集合获取区域id集合
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @Override
    public List<String> selectAreaIdsByDeptIds(List<String> deptIds) {
        return areaDeptInfoDao.selectAreaIdByDeptIds(deptIds);
    }

    /**
     * 根据部门id获取区域部门关系信息
     *
     * @param deptIds 部门id集合
     * @return 查询结果
     */
    @Override
    public List<AreaDeptInfo> selectAreaDeptInfosByDeptIds(List<String> deptIds) {
        return areaDeptInfoDao.selectAreaDeptInfoByDeptIds(deptIds);
    }

    /**
     * 根据区域id集合获取区域部门关系集合
     *
     * @param areaIds 区域id集合
     * @return 返回查询结果
     */
    @Override
    public Result selectAreaDeptInfoByAreaIds(List<String> areaIds) {
        List<AreaDeptInfo> areaDeptInfos = areaDeptInfoDao.selectAreaDeptInfoByAreaIds(areaIds);
        return ResultUtils.success(areaDeptInfos);
    }


    /**
     * 转换成外部需要的树状结构
     *
     * @param areaInfoForeignDtos 需要转换的对象
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
     * 根据父级级别设置自己的级别
     *
     * @param areaInfo 要设置的对象
     * @return 设置结
     */
    private AreaInfo setLevel(AreaInfo areaInfo) {
        String parentId = areaInfo.getParentId();
        //如果父级id存在将区域级别自增一级
        if (parentId != null && !("").equals(parentId)) {
            //获取父级数据
            AreaInfo parentAreaInfo = areaInfoDao.selectAreaInfoById(parentId);
            //如果父级不存在
            if (parentAreaInfo == null) {
                throw new FilinkAreaDirtyDataException();
            }
            //获取父级级别
            Integer parentLevel = parentAreaInfo.getLevel();
            //如果父级级别信息不存在
            if (parentLevel == null) {
                throw new FilinkAreaDirtyDataException();
            }
            areaInfo.setLevel(parentLevel + 1);
            //如果父级id不存在，设置级别为1
        } else {
            areaInfo.setLevel(1);
        }
        return areaInfo;
    }

    /**
     * 拼字段，子类到父类
     *
     * @param areaInfo 子类信息
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
     * 设置单位名称
     *
     * @param accountabilityUnit     单位id集合
     * @param accountabilityUnitName 单位名称et
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
            //与dept信息进行匹配，并拼接部门名称
            for (Department department : departmentList) {
                if (id.equals(department.getId())) {
                    accountabilityUnitName.append(department.getDeptName() + ",");
                }
            }
        }
        if (accountabilityUnitName.length() == 0) {
            accountabilityUnitName.append(",");
        }
        //删除最后一个逗号
        accountabilityUnitName.deleteCharAt(accountabilityUnitName.lastIndexOf(","));
    }

    /**
     * 设置关联表信息并添加
     *
     * @param areaInfo 区域信息
     */
    private void setAreaDeptInfoListAndInsert(AreaInfo areaInfo) {
        Set<String> accountabilityUnit = areaInfo.getAccountabilityUnit();
        if (accountabilityUnit == null || accountabilityUnit.size() == 0) {
            return;
        }
        List<Department> departmentList = getDepartmentList();
        List<AreaDeptInfo> areaDeptInfoList = new ArrayList<AreaDeptInfo>();
        //创建并取得部门id集合
        List<String> deptIdList = new ArrayList<>();
        for (Department department : departmentList) {
            deptIdList.add(department.getId());
        }
        for (String accountabilityUnitId : accountabilityUnit) {
            //如果该部门id不存在
            if (!deptIdList.contains(accountabilityUnitId)) {
                throw new FilinkAreaDirtyDataException();
            }
            //创建关系表实体
            AreaDeptInfo areaDeptInfo = new AreaDeptInfo();
            //设置关联区域id
            areaDeptInfo.setAreaId(areaInfo.getAreaId());
            //设置责任单位id
            areaDeptInfo.setDeptId(accountabilityUnitId);
            //将关系实体放入集合中
            areaDeptInfoList.add(areaDeptInfo);
        }
        Integer integer = areaDeptInfoDao.addAreaDeptInfoBatch(areaDeptInfoList);
        if (areaDeptInfoList.size() != integer) {
            throw new FilinkAreaDateBaseException();
        }
    }

    /**
     * 查找子节点数据 并调用造树结构方法
     *
     * @param areaInfoTreeList 父节点集合
     */
    public void selectChildAreaInfo(List<AreaInfoTree> areaInfoTreeList) {
        if (areaInfoTreeList.size() == 0) {
            return;
        }
        int level = areaInfoTreeList.get(0).getLevel();
        List<AreaInfo> areaInfoList2 = areaInfoDao.selectAreaInfoByLevel(level + 1);
        //权限过滤
        List<String> currentUserPermissionsId = getCurrentUserPermissionsId();
        AreaUtil.areaTreePermissionsFilter(areaInfoList2, currentUserPermissionsId);
        //设置单位id
        List<AreaInfoTree> areaInfoTreeList2 = setDeptIdByAreaIds(areaInfoList2);
        for (AreaInfoTree areaInfoTree : areaInfoTreeList) {
            setChildAreaInfo(areaInfoTree, areaInfoTreeList2);
        }
    }

    /**
     * 递归组成树结构并添加单位名称
     *
     * @param areaInfoTree     父节点
     * @param areaInfoTreeList 所有节点集合
     * @return
     */
    private AreaInfoTree setChildAreaInfo(AreaInfoTree areaInfoTree, List<AreaInfoTree> areaInfoTreeList) {
        //设置单位名称
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
     * 删除关联部门
     *
     * @param deleteMap 删除map
     */
    private void deleteDept(Map<String, Object> deleteMap) {
        //查询关系表数据
        List<AreaDeptInfo> list = areaDeptInfoDao.selectByMap(deleteMap);
        //删除关系表
        Integer integer = areaDeptInfoDao.deleteByMap(deleteMap);
        if (integer != list.size()) {
            throw new FilinkAreaDateBaseException();
        }
    }

    /**
     * 首页获取区域列表
     *
     * @return 区域信息
     */
    @Override
    public Result queryAreaListAll() {
        //获取区域信息
        List<AreaInfoForeignDto> areaInfoForeignDtoList = getAreaInfoFromRedis();
        //区域为空
        if (areaInfoForeignDtoList.size() == 0) {
            return ResultUtils.success(areaInfoForeignDtoList);
        }
        areaInfoForeignDtoList.sort(Comparator.comparing(AreaInfoForeignDto::getAreaId));
        //添加权限
        AreaUtil.areaForeignTreePermissionsFilter(areaInfoForeignDtoList, getCurrentUserPermissionsId());
        return ResultUtils.success(areaInfoForeignDtoList);
    }

    /**
     * 删除区域部门关系
     *
     * @param deptIds 部门id集合
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteAreaDeptRelation(List<String> deptIds) {
        for (String deptId : deptIds) {
            //构建删除map
            Map<String, Object> map = new HashMap<String, Object>(1);
            map.put(DEPT_ID, deptId);
            deleteDept(map);
        }
        return true;
    }

    /**
     * 查询区域细节是否可以进行修改
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    @Override
    public Boolean queryAreaDetailsCanChange(String areaId) {
        ArrayList<String> areaIds = new ArrayList<>();
        areaIds.add(areaId);
        Result<List<String>> result = procBaseFeign.queryProcExitsForAreaIds(areaIds);
        if (result == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_WORK_ORDER_INFORMATION));
        }
        List<String> proAreaIds = (List<String>) result.getData();
        if (proAreaIds != null && proAreaIds.size() > 0) {
            return false;
        }
        List<String> list = alarmCurrentFeign.queryAreaForFeign(areaIds);
        if (list == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_ALARM_INFORMATION));
        }
        return list.size() == 0;
    }

    /**
     * 删除区域信息的日志
     *
     * @param areaInfoList 区域信息集合
     */
    private void addLogByAreaInfos(List<AreaInfo> areaInfoList) {
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //业务数据
        //遍历业务数据
        if (areaInfoList.size() == 0) {
            return;
        }
        for (AreaInfo areaInfo : areaInfoList) {
            //获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("areaId");
            addLogBean.setDataName("areaAndPrentName");
            //获得操作对象id
            addLogBean.setOptObjId(areaInfo.getAreaId());
            //操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBean.setOptObj(areaInfo.getAreaAndPrentName());
            addLogBean.setFunctionCode(AreaFunctionCodeConstant.DELETE_AREA_FUNCTION_CODE);

            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //新增操作日志
            logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * 列表导出记录日志
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(AreaFunctionCodeConstant.LIST_EXPORT_FUNCTION_CODE);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 关联设施的日志
     *
     * @param map 日志数据
     */
    private void addLogByMap(Map<String, List<String>> map) {
        List<AddOperateLogReq> logInfo = new ArrayList<AddOperateLogReq>();
        //业务数据
        //遍历业务数据
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
            //对象名称
            addOperateLogReq.setOptObj(areaInfo.getAreaAndPrentName());
            //对象id
            addOperateLogReq.setOptObjId(areaId);
            addOperateLogReq.setFunctionCode(AreaFunctionCodeConstant.ASSOCIATED_FACILITY_FUNCTION_CODE);
            //获取语言
            String environment = ((LanguageConfig) SpringUtils.getBean(LanguageConfig.class)).getEnvironment();
            //获取详细信息模板
            String detailTemplateInfo = "";
            String optName = "";
            try {
                XmlParseBean xmlParseBean = logCastProcess.dom4jParseXml(addOperateLogReq.getFunctionCode(), environment);
                //获得日志详细信息模板对象
                detailTemplateInfo = xmlParseBean.getDetailInfoTemplate();
                //替换日志详细信息,替换xml中的占位符
                if (null != detailTemplateInfo) {
                    detailTemplateInfo = detailTemplateInfo.replace("${areaAndPrentName}", areaInfo.getAreaAndPrentName());
                    detailTemplateInfo = detailTemplateInfo.replace("${deviceName}", deviceNameStringBuilder);
                    detailTemplateInfo = detailTemplateInfo.replace("${optUserName}", addOperateLogReq.getOptUserName());
                }
                optName = xmlParseBean.getOptName();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获得日志详细信息
            addOperateLogReq.setDetailInfo(detailTemplateInfo);
            //获得日志操作名称
            addOperateLogReq.setOptName(optName);
            //需要调用失败后新增日志本地文件
            addOperateLogReq.setAddLocalFile(LogConstants.ADD_LOG_LOCAL_FILE);
            logInfo.add(addOperateLogReq);
        }
        //批量新增操作日志信息
        logProcess.addOperateLogBatchInfoToAutoCall(logInfo, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 调用部门服务获取部门信息
     *
     * @return 查询结果
     */
    public List<Department> getDepartmentList() {
        List<Department> departmentList = departmentFeign.queryAllDepartment();
        if (departmentList == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
        }
        return departmentList;
    }

    /**
     * 拼接设施名称
     *
     * @param deviceIdList id集合
     * @return 拼接结果
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
     * 根据区域信息设置单位id
     *
     * @param areaInfos 区域信息集合
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
     * 根据id修改缓存中的区域信息
     *
     * @param areaInfoForeignDto 区域信息
     */
    public void updateRedisArea(AreaInfoForeignDto areaInfoForeignDto) {
        RedisUtils.hSet(DeviceConstant.AREAINFO_FOREIGN, areaInfoForeignDto.getAreaId(), areaInfoForeignDto);
    }

    /**
     * 获取缓存中所有的区域信息
     *
     * @return
     */
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
     * 导出数据超过最大限制返回信息
     *
     * @param fe 异常
     * @return 返回结果
     */
    private Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        fe.printStackTrace();
        String string = I18nUtils.getString(AreaI18nConstant.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(AreaResultCodeConstant.EXPORT_DATA_TOO_LARGE, msg);
    }

    /**
     * 获取拥有权限的区域id
     *
     * @param userIds 用户id集合
     * @return
     */
    public List<String> getPermissionsId(List<String> userIds) {
        if (userIds.contains(DeviceConstant.ADMIN)) {
            return getRedisAllAreaIds();
        }
        List<String> areaIds = new ArrayList<>();
        List<Object> objects = (List<Object>) userFeign.queryUserByIdList(userIds);
        if (objects == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18nConstant.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
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
     * 当前用户权限
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
     * 获取缓存中所有区域id集合
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
     * 发送更新用户消息
     */
    public void sendUpdateUserInfo() {
        Message msg = MessageBuilder.withPayload(DeviceConstant.UPDATE_USER_INFO).build();
        deviceStreams.updateUserInfo().send(msg);
    }

    /**
     * 删除缓存中的区域信息
     *
     * @param areaIds 区域id集合
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
