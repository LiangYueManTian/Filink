package com.fiberhome.filink.fdevice.service.area.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import com.fiberhome.filink.alarmCurrent_api.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.area.*;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dao.area.AreaDeptInfoDao;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDateBaseException;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDateFormatException;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDirtyDataException;
import com.fiberhome.filink.fdevice.exception.FilinkAreaException;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.utils.AreaRusultCode;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.user_api.api.DepartmentFeign;
import com.fiberhome.filink.user_api.bean.Department;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.util.*;

/**
 * <p>
 * 区域信息服务实现类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@Service
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
     * 远程调用告警服务
     */
    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;
    /**
     * level字符常量
     */
    private static final String LEVEL = "level";
    /**
     * areaId用于删除查询map
     */
    private static final String AREA_ID = "area_id";
    /**
     * deptId用于删除查询map
     */
    private static final String DEPT_ID = "dept_id";
    /**
     * FIVE用于限制最大区域级别
     */
    private static final int FIVE = 5;
    /**
     * 部门信息list
     */
    private List<Department> departmentList = null;
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
            return ResultUtils.warn(AreaRusultCode.NAME_IS_EXIST, I18nUtils.getString(AreaI18n.NAME_IS_EXIST));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18n.NAME_IS_AVAILABLE));
    }

    /**
     * 新增区域
     *
     * @param areaInfo 区域信息
     * @return 新增结果
     */
    @AddLogAnnotation(value = "add", logType = "1", functionCode = "1301101", dataGetColumnName = "areaAndPrentName", dataGetColumnId = "areaId")
    @Override
    public Result addArea(AreaInfo areaInfo) {
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaRusultCode.NAME_IS_EXIST, I18nUtils.getString(AreaI18n.NAME_IS_EXIST));
        }
        //生成并设置区域id
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        areaInfo.setAreaId(uuid);
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
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18n.INCREASE_DATA_FAILURE));
        }
        setAreaDeptInfoListAndInsert(areaInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18n.INCREASE_DATA_SUCCESS));
    }


    /**
     * 获取区域列表
     *
     * @param queryCondition 查询条件
     * @return 区域信息
     */
    @Override
    public Result queryAreaListByItem(QueryCondition queryCondition) {
        if (queryCondition.getBizCondition() == null) {
            return ResultUtils.warn(AreaRusultCode.PARAM_NULL, I18nUtils.getString(AreaI18n.PARAM_NULL));
        }
        Map map = (Map) queryCondition.getBizCondition();
        Integer level = (Integer) map.get(LEVEL);
        if (level == null) {
            level = 1;
            map.put(LEVEL, level);
        }
        List<AreaInfoTree> areaInfoTrees = areaInfoDao.queryAreaListByItem(map);
        departmentList = getDepartmentList();
        //查找并组装树
        selectChildAreaInfo(areaInfoTrees);
        return ResultUtils.success(areaInfoTrees);
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
            return ResultUtils.warn(AreaRusultCode.THIS_AREA_DOES_NOT_EXIST, I18nUtils.getString(AreaI18n.THIS_AREA_DOES_NOT_EXIST));
        }
        //设置单位名称属性实体
        areaInfo.setAccountabilityUnitName(new StringBuilder());
        departmentList = getDepartmentList();
        //设置单位名称
        setAccountabilityUnitName(areaInfo.getAccountabilityUnit(), areaInfo.getAccountabilityUnitName());
        List<String> list = areaInfoDao.selectAreaIdbyParentId(areaId);
        if (list != null && list.size() > 0) {
            areaInfo.setHasChild(true);
        } else {
            areaInfo.setHasChild(false);
        }
        return ResultUtils.success(areaInfo);
    }

    /**
     * 根据id修改区域信息
     *
     * @param areaInfo 区域信息
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "1", functionCode = "1301102", dataGetColumnName = "areaAndPrentName", dataGetColumnId = "areaId")
    @Override
    public Result updateAreaInfo(AreaInfo areaInfo) {
        //判断该区域信息是否存在
        AreaInfo areaInfo1 = areaInfoDao.selectAreaInfoById(areaInfo.getAreaId());
        if (areaInfo1 == null) {
            return ResultUtils.warn(AreaRusultCode.THIS_AREA_DOES_NOT_EXIST, I18nUtils.getString(AreaI18n.THIS_AREA_DOES_NOT_EXIST));
        }
        //是否可以修改区域详细信息
        boolean flag = queryAreaDetailsCanChange(areaInfo.getAreaId());
        //如果不能
        if (!flag) {
            //修改名称
            Boolean aBoolean = areaInfoDao.updateAreaNameById(areaInfo);
            if (aBoolean) {
                deviceInfoService.refreshDeviceAreaRedis(areaInfo.getAreaId());
                return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18n.UPDATE_SUCCESS));

            } else {
                throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18n.UPDATE_FAILED));
            }
        }
        int level = areaInfo1.getLevel();
        //计算更改的级别
        setLevel(areaInfo);
        int updateLevel = areaInfo.getLevel();
        List<String> list1 = areaInfoDao.selectAreaIdbyParentId(areaInfo.getAreaId());
        //判断是否可以改变级别
        if (list1 != null && list1.size() > 0 && level != updateLevel) {
            throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18n.UPDATE_FAILED));
        } else if (updateLevel > FIVE) {
            throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18n.UPDATE_FAILED));
        }
        AreaInfo resultAreaInfo = areaInfoDao.selectAreaInfoByName(areaInfo);
        if (resultAreaInfo != null) {
            return ResultUtils.warn(AreaRusultCode.NAME_IS_EXIST, I18nUtils.getString(AreaI18n.NAME_IS_EXIST));
        }
        // 取得日志拼接的名称
        getAllName(areaInfo);
        int result = areaInfoDao.updateAreaInfoById(areaInfo);
        //判断返回结果
        if (result != 1) {
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18n.UPDATE_FAILED));
        }
        //构建关系表删除map
        Map deleteMap = new HashMap(1);
        deleteMap.put(AREA_ID, areaInfo.getAreaId());
        //查询关系表数据
        List list = areaDeptInfoDao.selectByMap(deleteMap);
        //删除关系表
        Integer integer = areaDeptInfoDao.deleteByMap(deleteMap);
        if (integer != list.size()) {
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18n.UPDATE_FAILED));
        }
        setAreaDeptInfoListAndInsert(areaInfo);
        deviceInfoService.refreshDeviceAreaRedis(areaInfo.getAreaId());
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18n.UPDATE_SUCCESS));
    }

    /**
     * 关联设施
     *
     * @param map 关联设施信息
     * @return 操作结果
     */
    @Override
    public Boolean setAreaDevice(Map<String, List<String>> map) {
        Boolean aBoolean = deviceInfoService.setAreaDevice(map);
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
            throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18n.DELETED_AREA_EXISTS));
        }
        for (AreaInfo areaInfo : areaInfoList) {
            if ("1".equals(areaInfo.getIsDeleted())) {
                throw new FilinkAreaDirtyDataException(I18nUtils.getString(AreaI18n.DELETED_AREA_EXISTS));
            }
            getAllName(areaInfo);
        }
        //遍历该集合
        for (String areaId : areaIds) {
            //获取子类id
            List<String> childrenIds = areaInfoDao.selectAreaIdbyParentId(areaId);
            //如果子类id不为null
            if (childrenIds.size() != 0) {
                //遍历子类id
                for (String childId : childrenIds) {
                    //如果要删除集合中不包含此子类id
                    if (!areaIds.contains(childId)) {
                        return ResultUtils.warn(AreaRusultCode.HAVE_CHILD, I18nUtils.getString(AreaI18n.HAVE_CHILD));
                    }
                }
            }
            //查询该区域下的关联设施
            List<DeviceInfo> deviceInfoList = deviceInfoService.queryDeviceByAreaId(areaId);
            //如果该区域下有关联设施
            if (deviceInfoList != null && deviceInfoList.size() != 0) {
                return ResultUtils.warn(AreaRusultCode.HAVE_DEVICE, I18nUtils.getString(AreaI18n.HAVE_DEVICE));
            }
        }
        //获取删除条数
        int result = areaInfoDao.deleteAreaInfoByIds(areaIds);
        //如果删除条数与集合中不相等
        if (result != areaIds.size()) {
            throw new FilinkAreaDateBaseException(I18nUtils.getString(AreaI18n.DELETE_FAILED));
        }
        for (String areaId : areaIds) {
            Map map = new HashMap(1);
            map.put(AREA_ID, areaId);
            //删除关系表
            deleteDept(map);
        }
        addLogByAreaInfos(areaInfoList);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18n.SUCCESSFULLY_DELETED));

    }

    /**
     * 查找子节点数据 并调用造树结构方法
     *
     * @param areaInfoTreeList 父节点集合
     */
    private void selectChildAreaInfo(List<AreaInfoTree> areaInfoTreeList) {
        if (areaInfoTreeList.size() == 0) {
            return;
        }
        int level = areaInfoTreeList.get(0).getLevel();
        List<AreaInfoTree> areaInfoTreeList2 = areaInfoDao.selectAreaInfoByLevel(level + 1);
        for (AreaInfoTree areaInfoTree : areaInfoTreeList) {
            setChildAreaInfo(areaInfoTree, areaInfoTreeList2);
        }
    }

    /**
     * 递归组成树结构并添加单位名称
     *
     * @param areaInfoTree     父节点
     * @param areaInfoTreeList 所有节点集合
     * @return 拼接完的对象
     */
    private AreaInfoTree setChildAreaInfo(AreaInfoTree areaInfoTree, List<AreaInfoTree> areaInfoTreeList) {
        for (AreaInfoTree areaInfoTreeFromList : areaInfoTreeList) {
            //设置单位名称
            areaInfoTree.setAccountabilityUnitName(new StringBuilder());
            setAccountabilityUnitName(areaInfoTree.getAccountabilityUnit(), areaInfoTree.getAccountabilityUnitName());
            if (areaInfoTree.getAreaId().equals(areaInfoTreeFromList.getParentId())) {
                if (areaInfoTree.getChildren() == null) {
                    areaInfoTree.setChildren(new ArrayList<AreaInfoTree>());
                }
                areaInfoTree.getChildren().add(setChildAreaInfo(areaInfoTreeFromList, areaInfoTreeList));
            }
        }
        return areaInfoTree;
    }

    /**
     * 根据父级级别设置自己的级别
     *
     * @param areaInfo 要设置的对象
     * @return 设置结果
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
    private void setAccountabilityUnitName(Set<String> accountabilityUnit, StringBuilder accountabilityUnitName) {
        if (accountabilityUnit == null || accountabilityUnit.size() == 0) {
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
            //设置关联id
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            areaDeptInfo.setAreaDeptId(uuid);
            //设置责任单位id
            areaDeptInfo.setDeptId(accountabilityUnitId);
            //将关系实体放入集合中
            areaDeptInfoList.add(areaDeptInfo);
        }

        Integer integer = areaDeptInfoDao.addAreaDeptInfoBatch(areaDeptInfoList);
        if (areaDeptInfoList.size() == integer) {
            return;
        }
        throw new FilinkAreaDateBaseException();
    }

    /**
     * 删除关联部门
     *
     * @param deleteMap 删除map
     */
    private void deleteDept(Map deleteMap) {
        //查询关系表数据
        List list = areaDeptInfoDao.selectByMap(deleteMap);
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
        return ResultUtils.success(areaInfoDao.queryAreaListAll());
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
            Map map = new HashMap(1);
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
        List<String> list = alarmCurrentFeign.queryArea(areaIds);
        if (list == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18n.FAILED_TO_OBTAIN_ALARM_INFORMATION));
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
            addLogBean.setFunctionCode("1301103");

            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //新增操作日志
            logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * 关联设施的日志
     *
     * @param map 日志信息
     */
    private void addLogByMap(Map<String, List<String>> map) {
        List<AddOperateLogReq> logInfo = new ArrayList<AddOperateLogReq>();
        //业务数据
        //遍历业务数据
        if (map.size() == 0) {
            return;
        }
        for (Map.Entry entry : map.entrySet()) {
            String areaId = (String) entry.getKey();
            AreaInfo areaInfo = areaInfoDao.selectAreaInfoById(areaId);
            getAllName(areaInfo);
            List<String> deviceIdList = (List<String>) entry.getValue();
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
            String deviceNameStringBuilder = getDeviceNameStringBuilder(deviceIdList);
            AddOperateLogReq addOperateLogReq = new AddOperateLogReq();
            BeanUtils.copyProperties(addLogBean, addOperateLogReq);
            //对象名称
            addOperateLogReq.setOptObj(areaInfo.getAreaAndPrentName());
            //对象id
            addOperateLogReq.setOptObjId(areaId);
            addOperateLogReq.setFunctionCode("1301104");
            //获取语言
            String environment = SpringUtils.getBean(LanguageConfig.class).getEnvironment();
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
    private List<Department> getDepartmentList() {
        List<Department> departmentList = departmentFeign.queryAllDepartment();
        if (departmentList == null) {
            throw new FilinkAreaException(I18nUtils.getString(AreaI18n.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
        }
        return departmentList;
    }

    /**
     * 拼接设施名称
     *
     * @param deviceIdList 设施id集合
     * @return 拼接的名称
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
}
