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
 * ???????????????
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
	 * ????????????
	 *
	 * @param deviceInfo DeviceInfo
	 * @return Result
	 */
	@AddLogAnnotation(value = "add", logType = "1", functionCode = "1301201", dataGetColumnName = "deviceName", dataGetColumnId = "deviceId")
	@Override
	public Result addDevice(DeviceInfo deviceInfo) {
		// ??????????????????
		if (chechDeviceParams(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_PARAM_ERROR));
		}
		deviceInfo.parameterFormat();
		//??????????????????????????????
		if (!deviceInfo.checkDevicePositionBase() || !deviceInfo.checkDevicePositionGps()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}

		// ???????????????
		if (!deviceInfo.checkDeviceName()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_NAME_ERROR));
		}

		if (!deviceInfo.checkParameterFormat()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}

		// ???????????????????????????
		if (checkDeviceName(null, deviceInfo.getDeviceName())) {
			throw new FilinkDeviceNameSameException();
		}
		// ???????????? TODO ????????????????????????????????????????????????????????????????????????
		String deviceCode = null;
		while (!checkDeviceCode(deviceCode = serialNumber(unitCode, deviceInfo.getDeviceType()))) {
			log.warn("?????????deviceCode--------------" + deviceCode);
		}
		log.warn("?????????deviceCode--------------" + deviceCode);
		deviceInfo.setDeviceCode(deviceCode);
		log.info(deviceCode);
		// TODO ????????????????????????????????????
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String userId = request.getHeader("userId");
		deviceInfo.setCreateUser(userId);
		deviceInfo.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));

		// ??????????????????????????????
		deviceInfo.setDeviceStatus(DeviceStatus.Unknown.getCode());
		// ?????????
		deviceInfo.setDeployStatus(DeployStatus.NODEFENCE.getCode());
		// ???????????????
		int result = deviceInfoDao.insertDevice(deviceInfo);
		if (result != 1) {
			return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.ADD_DEVICE_FAIL));
		}

		// ????????????
		try {
			HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
			RedisUtils.hSet(Constant.DEVICE_GIS_MAP, deviceInfo.getDeviceId(), homeDeviceInfoDto);
		} catch (Exception e) {
			log.error("?????????????????????");
		}
		return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.ADD_DEVICE_SUCCESS));
}

	/**
	 * ??????????????????????????????
	 *
	 * @param deviceId ??????id
	 * @param deviceName ????????????
	 * @return boolean
	 */
	@Override
	public boolean checkDeviceName(String deviceId, String deviceName) {
		DeviceInfo deviceInfo = deviceInfoDao.selectByName(deviceName);
		// deviceId???????????????????????????????????????????????????
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
	 * ??????????????????
	 *
	 * @param deviceInfo ??????????????????
	 * @return Result
	 */
	@AddLogAnnotation(value = "update", logType = "1", functionCode = "1301202", dataGetColumnName = "deviceName", dataGetColumnId = "deviceId")
	@Override
	public Result updateDevice(DeviceInfo deviceInfo) {

		// ????????????id
		if (StringUtils.isEmpty(deviceInfo.getDeviceId())) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
		}

		// ??????????????????
		if (chechDeviceParams(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_PARAM_ERROR));
		}
		deviceInfo.parameterFormat();
		if (!deviceInfo.checkDevicePositionBase() || !deviceInfo.checkDevicePositionGps()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}
		// ???????????????
		if (!deviceInfo.checkDeviceName()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_NAME_ERROR));
		}

		if (!deviceInfo.checkParameterFormat()) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
		}
		// ????????????????????????
		if (checkDeviceIsExist(deviceInfo.getDeviceId())) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
		}

		// ???????????????????????????
		if (checkDeviceName(deviceInfo.getDeviceId(), deviceInfo.getDeviceName())) {
			throw new FilinkDeviceNameSameException();
		}

		// TODO ?????????????????????????????????????????????


		// TODO ??????????????????????????????
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String userId = request.getHeader("userId");
		deviceInfo.setUpdateUser(userId);
		deviceInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
		// ???????????????
		int result = deviceInfoDao.updateById(deviceInfo);
		if (result != 1) {
			return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.UPDATE_DEVICE_FAIL));
		}

		// ????????????
		try {
			HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
			RedisUtils.hSet(Constant.DEVICE_GIS_MAP, deviceInfo.getDeviceId(), homeDeviceInfoDto);
		} catch (Exception e) {
			log.error("?????????????????????");
		}
		return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.UPDATE_DEVICE_SUCCESS));
	}

	/**
	 * ????????????????????????
	 *
	 * @param queryCondition ???????????????
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
			// ???????????????
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
	 * ????????????????????????
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
	 * ????????????id????????????
	 *
	 * @param areaId ??????id
	 * @return ????????????
	 */
	@Override
	public List<DeviceInfo> queryDeviceByAreaId(String areaId) {
		List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
		return deviceInfoList;
	}

	/**
	 * ????????????
	 *
	 * @param map ??????????????????
	 * @return ????????????
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
				// ????????????
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
	 * ??????????????????
	 *
	 * @param id ??????id
	 * @return Result
	 * @throws Exception ??????
	 */
	@Override
	public Result getDeviceById(String id) throws Exception {
		// ????????????id
		if (StringUtils.isEmpty(id)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
		}
		DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
		if (ObjectUtils.isEmpty(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
		}
		// ?????????????????????????????????
		return ResultUtils.success(DeviceResultCode.SUCCESS,
				I18nUtils.getString(DeviceI18n.QUERY_DEVICE_SUCCESS),
				convertDeviceInfoToDeviceInfoDto(deviceInfo));
	}

	/**
	 * ????????????????????????????????????
	 *
	 * @param deviceInfo ?????????????????????
	 * @return DeviceInfoDto ??????????????????
	 * @throws Exception ??????
	 */
	public DeviceInfoDto convertDeviceInfoToDeviceInfoDto(DeviceInfo deviceInfo) throws Exception {
		DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
		copyProperties(deviceInfoDto, deviceInfo);
		// ????????????
		Result result = areaInfoService.queryAreaById(deviceInfo.getAreaId());
		AreaInfo areaInfo = new AreaInfo() ;
		if (result.getData() instanceof AreaInfo) {
			areaInfo = (AreaInfo) result.getData();
		}
		if (!ObjectUtils.isEmpty(areaInfo)) {
			deviceInfoDto.setAreaInfo(areaInfo);
		}

		// ???????????????
		if (!ObjectUtils.isEmpty(deviceInfo.getCreateTime())) {
			deviceInfoDto.setCTime(deviceInfo.getCreateTime().getTime());
		}
		if (!ObjectUtils.isEmpty(deviceInfo.getUpdateTime())) {
			deviceInfoDto.setUTime(deviceInfo.getUpdateTime().getTime());
		}

		return deviceInfoDto;
	}

	/**
	 * ????????????????????????
	 *
	 * @param id ??????id
	 * @return boolean
	 */
	public boolean checkDeviceIsExist(String id) {
		DeviceInfo deviceInfo = deviceInfoDao.selectDeviceById(id);
		return ObjectUtils.isEmpty(deviceInfo);
	}

	/**
	 * ??????????????????(????????????+??????????????????+7????????????)
	 *
	 * @param unitCode ??????
	 * @param deviceType ????????????
	 * @return ????????????
	 */
	public String serialNumber(String unitCode, String deviceType) {
		return unitCode + deviceType + RandomUtil.getRandomOfServen();
	}

	/**
	 * ????????????????????????
	 *
	 * @param deviceCode ????????????
	 * @return boolean
	 */
	private boolean checkDeviceCode(String deviceCode) {
		List<DeviceInfo> deviceInfo = deviceInfoDao.checkDeviceCode(deviceCode);
		return deviceInfo == null || deviceInfo.size() == 0;
	}

	/**
	 * ????????????????????????
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
	 * ????????????????????????????????????
	 *
	 * @return ??????????????????
	 */
	@Override
	public Result queryDeviceAreaList() {
		List<HomeDeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
		// ??????Redis??????
		if (RedisUtils.hasKey(Constant.DEVICE_GIS_MAP)) {
			// Redis?????????????????????Redis??????
			Map<Object, Object> deviceInfoMap = RedisUtils.hGetMap(Constant.DEVICE_GIS_MAP);
			for (Object obj : deviceInfoMap.values()) {
				HomeDeviceInfoDto homeDeviceInfoDto = (HomeDeviceInfoDto) obj;
				deviceInfoDtoList.add(homeDeviceInfoDto);
			}
		} else {
			// Redis????????????,?????????????????????????????????
			deviceInfoDtoList = deviceInfoDao.queryDeviceAreaList();
			if (ObjectUtils.isEmpty(deviceInfoDtoList)) {
				deviceInfoDtoList = new ArrayList<>();
			}
			if (deviceInfoDtoList.size() > 0) {
				//??????????????????????????????????????????
				Map<String, Object> deviceInfoRedis = new HashMap<>(128);
				for (HomeDeviceInfoDto homeDeviceInfoDto : deviceInfoDtoList) {
					deviceInfoRedis.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
				}
				// ??????Redis
				RedisUtils.hSetMap(Constant.DEVICE_GIS_MAP, deviceInfoRedis);
			}
		}
		return ResultUtils.success(deviceInfoDtoList);
	}

	/**
	 * ????????????
	 *
	 * @param ids ??????ids
	 * @return Result
	 */
	@Override
	public Result deleteDeviceByIds(String[] ids) {
		for (String id : ids) {
			if (checkDeviceIsExist(id)) {
				throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
			}
		}

		// TODO ???????????????????????????
		/*
		 * ????????????????????????????????????,???????????????????????????????????????, ?????????????????????????????????????????????????????????,?????????,RFID,??????,????????????
		 * build1?????????????????????
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
				log.error("??????????????????");
			}
		}
		logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);

		return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.DELETE_DEVICE_SUCCESS));
	}

	/**
	 * ???????????????????????????
	 *
	 * @param seriaNum ???????????????
	 * @return DeviceInfoDto
	 * @throws Exception ??????
	 */
	@Override
	public DeviceInfoDto findDeviceBySeriaNumber(String seriaNum) throws Exception {
		// ???????????????
		if (StringUtils.isEmpty(seriaNum)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_ID_LOSE));
		}
		DeviceInfo deviceInfo = deviceInfoDao.selectDeviceBySeriaNumber(seriaNum);
		if (ObjectUtils.isEmpty(deviceInfo)) {
			throw new FilinkDeviceException(I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
		}
		// ?????????????????????????????????
		return convertDeviceInfoToDeviceInfoDto(deviceInfo);
	}

	/**
	 * ????????????????????????
	 *
	 * @param deviceId ???????????????
	 * @return
	 */
	@Override
	public boolean deviceCanChangeDetail(String deviceId) {
		List list = new ArrayList(Arrays.asList(deviceId));
		return hasAlarmOrOrder(list);
	}

	/**
	 * ?????????????????????Gis map ??????
	 *
	 * @param areaId ??????ID
	 */
	@Override
	public void refreshDeviceAreaRedis(String areaId) {
		//???????????????????????????ID
		List<DeviceInfo> deviceInfoList = deviceInfoDao.queryDeviceByAreaId(areaId);
		for (DeviceInfo deviceInfo : deviceInfoList) {
			// ????????????
			try {
				HomeDeviceInfoDto homeDeviceInfoDto = deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId());
				RedisUtils.hSet(Constant.DEVICE_GIS_MAP, deviceInfo.getDeviceId(), homeDeviceInfoDto);
			} catch (Exception e) {
				log.error("?????????????????????????????????");
			}
		}
	}

	/**
	 * ??????????????????
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
				log.error("?????????????????????");
			}
		}
	}

	/**
	 * ??????????????????????????????
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
