package com.fiberhome.filink.fdevice.service.device.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmCurrent_api.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.fdevice.bean.DeviceI18n;
import com.fiberhome.filink.fdevice.bean.area.AreaI18n;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.dto.HomeDeviceInfoDto;
import com.fiberhome.filink.fdevice.exception.*;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.utils.*;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPageBean;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zepenggao@wistronits.com
 * @since 2019-01-07
 */
@Slf4j
@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoDao, DeviceInfo>
		implements DeviceInfoService {

	@Autowired
	private DeviceInfoDao deviceInfoDao;

	@Autowired
	private AreaInfoService areaInfoService;

	@Autowired
	private LogProcess logProcess;

	@Autowired
	private AlarmCurrentFeign alarmCurrentFeign;


	@Value("${unitCode}")
	private String unitCode;

	/**
	 * 新增设施
	 *
	 * @param deviceInfo DeviceInfo
	 * @return Result
	 */
	@AddLogAnnotation(value = "add", logType = "1", functionCode = "1301201", dataGetColumnName = "deviceName", dataGetColumnId = "deviceId")
	@Override
	public Result addDevice(DeviceInfo deviceInfo) {
		// 校验必填参数
		if (chechDeviceParams(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_PARAM_ERROR));
		}
		deviceInfo.parameterFormat();
		//校验坐标信息是否合法
		if (!deviceInfo.checkDevicePositionBase() || !deviceInfo.checkDevicePositionGps()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}

		// 校验设施名
		if (!deviceInfo.checkDeviceName()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_NAME_ERROR));
		}

		if (!deviceInfo.checkParameterFormat()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}

		// 校验设施名是否重复
		if (checkDeviceName(null, deviceInfo.getDeviceName())) {
			throw new FilinkDeviceNameSameException();
		}
		// 获取编号 TODO 当数据量很大的时候会存在死循环或者查询过长的问题
		String deviceCode = null;
		while (!checkDeviceCode(deviceCode = serialNumber(unitCode, deviceInfo.getDeviceType()))) {
			log.warn("重复的deviceCode--------------" + deviceCode);
		}
		log.warn("最终的deviceCode--------------" + deviceCode);
		deviceInfo.setDeviceCode(deviceCode);
		log.info(deviceCode);
		// TODO 从缓存中获取当前用户信息
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String userId = request.getHeader("userId");
		deviceInfo.setCreateUser(userId);
		deviceInfo.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));

		// 网管新建都是位置设备
		deviceInfo.setDeviceStatus(DeviceStatus.Unknown.getCode());
		// 未布防
		deviceInfo.setDeployStatus(DeployStatus.NODEFENCE.getCode());
		// 存入数据库
		int result = deviceInfoDao.insertDevice(deviceInfo);
		if (result != 1) {
			return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.ADD_DEVICE_FAIL));
		}

		// 存入缓存
		try {
			HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
			RedisUtils.hSet(Constant.DEVICE_GIS_MAP, deviceInfo.getDeviceId(), homeDeviceInfoDto);
		} catch (Exception e) {
			log.error("新增时缓存错误");
		}
		return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.ADD_DEVICE_SUCCESS));
}

	/**
	 * 校验设施名称是否重复
	 *
	 * @param deviceId 设施id
	 * @param deviceName 设施名称
	 * @return boolean
	 */
	@Override
	public boolean checkDeviceName(String deviceId, String deviceName) {
		DeviceInfo deviceInfo = deviceInfoDao.selectByName(deviceName);
		// deviceId为空时，新增校验；不为空，修改校验
		if (StringUtils.isEmpty(deviceId)) {
            return !ObjectUtils.isEmpty(deviceInfo);
		} else {
			if (!ObjectUtils.isEmpty(deviceInfo)) {
                return !deviceInfo.getDeviceId().equals(deviceId);
			}
		}
		return false;
	}

	/**
	 * 修改设施信息
	 *
	 * @param deviceInfo 设施基本信息
	 * @return Result
	 */
	@AddLogAnnotation(value = "update", logType = "1", functionCode = "1301202", dataGetColumnName = "deviceName", dataGetColumnId = "deviceId")
	@Override
	public Result updateDevice(DeviceInfo deviceInfo) {

		// 校验设施id
		if (StringUtils.isEmpty(deviceInfo.getDeviceId())) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
		}

		// 校验必填参数
		if (chechDeviceParams(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_PARAM_ERROR));
		}
		deviceInfo.parameterFormat();
		if (!deviceInfo.checkDevicePositionBase() || !deviceInfo.checkDevicePositionGps()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}
		// 校验设施名
		if (!deviceInfo.checkDeviceName()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_NAME_ERROR));
		}

		if (!deviceInfo.checkParameterFormat()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}
		// 校验设施是否存在
		if (checkDeviceIsExist(deviceInfo.getDeviceId())) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
		}

		// 校验设施名是否重复
		if (checkDeviceName(deviceInfo.getDeviceId(), deviceInfo.getDeviceName())) {
			throw new FilinkDeviceNameSameException();
		}

		// TODO 有工单或告警的设施不让修改类型


		// TODO 从缓存中获取用户信息
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String userId = request.getHeader("userId");
		deviceInfo.setUpdateUser(userId);
		deviceInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
		// 更新数据库
		int result = deviceInfoDao.updateById(deviceInfo);
		if (result != 1) {
			return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.UPDATE_DEVICE_FAIL));
		}

		// 修改缓存
		try {
			HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
			RedisUtils.hSet(Constant.DEVICE_GIS_MAP, deviceInfo.getDeviceId(), homeDeviceInfoDto);
		} catch (Exception e) {
			log.error("修改时缓存错误");
		}
		return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.UPDATE_DEVICE_SUCCESS));
	}

	/**
	 * 分页查询设施列表
	 *
	 * @param queryCondition 查询封装类
	 * @return Result
     */
	@Override
	public Result listDevice(QueryCondition<DeviceInfo> queryCondition) {

		Page page = MyBatiesBuildPage(queryCondition);
		Map map = convertFilterConditionsToMap(queryCondition.getFilterConditions());
		SortCondition sortCondition = queryCondition.getSortCondition();
		if(StringUtils.isEmpty(sortCondition.getSortRule())){
			sortCondition.setSortField(null);
		}
		Integer begin = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
		queryCondition.getPageCondition().setBeginNum(begin);
		List<DeviceInfoDto> deviceInfoDtos = deviceInfoDao.selectDeviceByPage(queryCondition.getPageCondition(), map, sortCondition);
		log.info(JSONObject.toJSONString(deviceInfoDtos));
		for (DeviceInfoDto deviceInfoDto : deviceInfoDtos) {
			// 返回时间戳
			if (!ObjectUtils.isEmpty(deviceInfoDto.getCreateTime())) {
				deviceInfoDto.setCTime(deviceInfoDto.getCreateTime().getTime());
			}
			if (!ObjectUtils.isEmpty(deviceInfoDto.getUpdateTime())) {
				deviceInfoDto.setUTime(deviceInfoDto.getUpdateTime().getTime());
			}
		}
		Integer count = deviceInfoDao.seleteDeviceCount(map);
		PageBean pageBean = MyBatiesBuildPageBean(page, count, deviceInfoDtos);
		return ResultUtils.pageSuccess(pageBean);
	}

	/**
	 * 过滤条件数据转换
	 *
	 * @param filterConditions
	 * @return
	 */
	public Map convertFilterConditionsToMap(List<FilterCondition> filterConditions) {
		Map map = new HashMap(64);
		for (FilterCondition filterCondition : filterConditions) {
			String key = filterCondition.getFilterField();
			String value = (String) filterCondition.getFilterValue();
			map.put(key, value);
		}
		return map;
	}


	/**
	 * 根据区域id查询设施
	 *
	 * @param areaId 查询id
	 * @return 查询结果
	 */
	@Override
	public List<DeviceInfo> queryDeviceByAreaId(String areaId) {
		List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
		return deviceInfoList;
	}

	/**
	 * 关联设施
	 *
	 * @param map 关联设施信息
	 * @return 操作结果
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean setAreaDevice(Map<String, List<String>> map) {
		int a = 0;
		int b = 0;
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			List<String> deviceIds = entry.getValue();
			if (deviceIds.size() == 0) {
				return true;
			}
			List<String> deviceIdList = queryAlarmSource(deviceIds);
			if (deviceIdList.size() > 0) {
				String deviceName = deviceInfoDao.selectDeviceById(deviceIdList.get(0)).getDeviceName();
				if(StringUtils.isEmpty(deviceName)) {
					throw new FilinkAreaDirtyDataException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
				}
				throw new FilinkAreaDirtyDataException(
						deviceName + I18nUtils.getString(AreaI18n.HAVE_A_WORK_ORDER_OR_AN_ALARM));
			}
			List<DeviceInfo> deviceInfoList = deviceInfoDao.selectByIds(deviceIds);
			if (deviceInfoList.size() != deviceIds.size()) {
				return false;
			}
			b += entry.getValue().size();
			Integer i = deviceInfoDao.setAreaDevice(entry.getKey(), deviceIds);
			if (deviceIds.size() == i) {
				// 修改缓存
				updateRedisBatch(deviceIds);
			}
			a += i;
		}
		if (a == b) {
			return true;
		}
		throw new FilinkAreaDateBaseException();
	}

	/**
	 * 获取设施详情
	 *
	 * @param id 设施id
	 * @return Result
	 * @throws Exception 异常
	 */
	@Override
	public Result getDeviceById(String id) throws Exception {
		// 校验设施id
		if (StringUtils.isEmpty(id)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
		}
		DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
		if (ObjectUtils.isEmpty(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
		}
		// 转换为界面显示字段信息
		return ResultUtils.success(DeviceResultCode.SUCCESS,
				I18nUtils.getString(DeviceI18n.QUERY_DEVICE_SUCCESS),
				convertDeviceInfoToDeviceInfoDto(deviceInfo));
	}

	/**
	 * 转换界面所需要的设施信息
	 *
	 * @param deviceInfo 数据库基本信息
	 * @return DeviceInfoDto 界面展示信息
	 * @throws Exception 异常
	 */
	public DeviceInfoDto convertDeviceInfoToDeviceInfoDto(DeviceInfo deviceInfo) throws Exception {
		DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
		copyProperties(deviceInfoDto, deviceInfo);
		// 返回区域
		Result result = areaInfoService.queryAreaById(deviceInfo.getAreaId());
		AreaInfo areaInfo = new AreaInfo() ;
		if (result.getData() instanceof AreaInfo) {
			areaInfo = (AreaInfo) result.getData();
		}
		if (!ObjectUtils.isEmpty(areaInfo)) {
			deviceInfoDto.setAreaInfo(areaInfo);
		}

		// 返回时间戳
		if (!ObjectUtils.isEmpty(deviceInfo.getCreateTime())) {
			deviceInfoDto.setCTime(deviceInfo.getCreateTime().getTime());
		}
		if (!ObjectUtils.isEmpty(deviceInfo.getUpdateTime())) {
			deviceInfoDto.setUTime(deviceInfo.getUpdateTime().getTime());
		}

		return deviceInfoDto;
	}

	/**
	 * 校验设施是否存在
	 *
	 * @param id 设施id
	 * @return boolean
	 */
	public boolean checkDeviceIsExist(String id) {
		DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
		return ObjectUtils.isEmpty(deviceInfo);
	}

	/**
	 * 生成设施编号(单位编号+设施类型编号+7位流水号)
	 *
	 * @param unitCode 单位
	 * @param deviceType 设施类型
	 * @return 设施编号
	 */
	public String serialNumber(String unitCode, String deviceType) {
		return unitCode + deviceType + RandomUtil.getRandomOfServen();
	}

	/**
	 * 校验设施编号唯一
	 *
	 * @param deviceCode 设施编号
	 * @return boolean
	 */
	private boolean checkDeviceCode(String deviceCode) {
		List<DeviceInfo> deviceInfo = deviceInfoDao.checkDeviceCode(deviceCode);
		return deviceInfo == null || deviceInfo.size() == 0;
	}

	/**
	 * 校验设施基本参数
	 *
	 * @param deviceInfo
	 * @return
	 */
	private boolean chechDeviceParams(DeviceInfo deviceInfo) {
        return StringUtils.isBlank(deviceInfo.getDeviceName()) || StringUtils.isBlank(deviceInfo.getDeviceType())
                || StringUtils.isBlank(deviceInfo.getAreaId())
                || StringUtils.isBlank(deviceInfo.getPositionBase())
                || StringUtils.isBlank(deviceInfo.getPositionGps())
                || StringUtils.isBlank(deviceInfo.getAddress());
    }

	/**
	 * 查询用户所有设施基本信息
	 *
	 * @return 设施基本信息
	 */
	@Override
	public Result queryDeviceAreaList() {
		List<HomeDeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
		// 判断Redis缓存
		if (RedisUtils.hasKey(Constant.DEVICE_GIS_MAP)) {
			// Redis有缓存信息，从Redis获取
			Map<Object, Object> deviceInfoMap = RedisUtils.hGetMap(Constant.DEVICE_GIS_MAP);
			for (Object obj : deviceInfoMap.values()) {
				HomeDeviceInfoDto homeDeviceInfoDto = (HomeDeviceInfoDto) obj;
				deviceInfoDtoList.add(homeDeviceInfoDto);
			}
		} else {
			// Redis没有缓存,从数据库查询并放入缓存
			deviceInfoDtoList = deviceInfoDao.queryDeviceAreaList();
			if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
				deviceInfoDtoList = new ArrayList<>();
			}
			if (deviceInfoDtoList.size() > 0) {
				//如果数据库有设施数据放入缓存
				Map<String, Object> deviceInfoRedis = new HashMap<>(128);
				for (HomeDeviceInfoDto homeDeviceInfoDto : deviceInfoDtoList) {
					deviceInfoRedis.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
				}
				// 存入Redis
				RedisUtils.hSetMap(Constant.DEVICE_GIS_MAP, deviceInfoRedis);
			}
		}
		return ResultUtils.success(deviceInfoDtoList);
	}

	/**
	 * 删除设施
	 *
	 * @param ids 设施ids
	 * @return Result
	 */
	@Override
	public Result deleteDeviceByIds(String[] ids) {
		for (String id : ids) {
			if (checkDeviceIsExist(id)) {
				throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
			}
		}

		// TODO 删除与设施相关联的
		/*
		 * 如果该设施关联工单或告警,提示该设施有工单或告警信息, 删除设施会删除与该设施关联的工单，告警,电子锁,RFID,图片,设施日志
		 * build1暂时只删除设施
		 */
		List<DeviceInfo> deviceInfoList = deviceInfoDao.selectDeviceByIds(ids);
		deviceInfoDao.deleteDeviceByIds(ids);

		List list = new ArrayList();
		for (DeviceInfo deviceInfo : deviceInfoList) {
			String logType = LogConstants.LOG_TYPE_OPERATE;
			AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
			addLogBean.setDataId("deviceId");
			addLogBean.setDataName("deviceName");
			addLogBean.setOptObj(deviceInfo.getDeviceName());
			addLogBean.setFunctionCode("1301203");
			addLogBean.setOptObjId(deviceInfo.getDeviceId());
			addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
			list.add(addLogBean);
			try {
				RedisUtils.hRemove(Constant.DEVICE_GIS_MAP, deviceInfo.getDeviceId());
			} catch (Exception e) {
				log.error("删除缓存异常");
			}
		}
		logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);

		return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.DELETE_DEVICE_SUCCESS));
	}

	/**
	 * 根据序列号查询设施
	 *
	 * @param seriaNum 设施序列号
	 * @return DeviceInfoDto
	 * @throws Exception 异常
	 */
	@Override
	public DeviceInfoDto findDeviceBySeriaNumber(String seriaNum) throws Exception {
		// 校验序列号
		if (StringUtils.isEmpty(seriaNum)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
		}
		DeviceInfo deviceInfo = deviceInfoDao.selectDeviceBySeriaNumber(seriaNum);
		if (ObjectUtils.isEmpty(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
		}
		// 转换为界面显示字段信息
		return convertDeviceInfoToDeviceInfoDto(deviceInfo);
	}

	/**
	 * 设施是否可以修改
	 *
	 * @param deviceId 设施序列号
	 * @return
	 */
	@Override
	public boolean deviceCanChangeDetail(String deviceId) {
		List list = new ArrayList(Arrays.asList(deviceId));
		return hasAlarmOrOrder(list);
	}

	/**
	 * 刷新指定区域的Gis map 信息
	 *
	 * @param areaId 区域ID
	 */
	@Override
	public void refreshDeviceAreaRedis(String areaId) {
		//查询区域内所有设施ID
		List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
		for (DeviceInfo deviceInfo : deviceInfoList) {
			// 存入缓存
			try {
				HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
				RedisUtils.hSet(Constant.DEVICE_GIS_MAP, deviceInfo.getDeviceId(), homeDeviceInfoDto);
			} catch (Exception e) {
				log.error("刷新区域设施缓存时错误");
			}
		}
	}

	/**
	 * 有告警或工单
	 *
	 * @param deviceIds
	 * @return
	 */
	public boolean hasAlarmOrOrder(List<String> deviceIds) {
		List<String> list = alarmCurrentFeign.queryAlarmSource(deviceIds);
		if (list == null) {
			throw new FilinkAreaException(I18nUtils.getString(AreaI18n.FAILED_TO_OBTAIN_ALARM_INFORMATION));
		}
        return list.isEmpty();
    }

	private void updateRedisBatch(List<String> deviceIds) {
		for (String deviceId : deviceIds) {
			try {
				HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceId);
				RedisUtils.hSet(Constant.DEVICE_GIS_MAP, deviceId, homeDeviceInfoDto);
			} catch (Exception e) {
				log.error("修改时缓存错误");
			}
		}
	}

	/**
	 * 查询有当前告警的设施
	 *
	 * @param deviceIds
	 * @return
	 */
	private List<String> queryAlarmSource(List<String> deviceIds) {
		List<String> list = alarmCurrentFeign.queryAlarmSource(deviceIds);
		if (list == null) {
			throw new FilinkAreaException(I18nUtils.getString(AreaI18n.FAILED_TO_OBTAIN_ALARM_INFORMATION));
		}
		return list;
	}

}
