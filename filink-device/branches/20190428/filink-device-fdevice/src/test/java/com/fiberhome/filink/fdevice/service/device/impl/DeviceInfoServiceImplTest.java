package com.fiberhome.filink.fdevice.service.device.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceCollectingDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.dto.HomeDeviceInfoDto;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDateBaseException;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDirtyDataException;
import com.fiberhome.filink.fdevice.exception.FilinkDeviceException;
import com.fiberhome.filink.fdevice.exception.FilinkDeviceNameSameException;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildQuery;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author WH1707069 create on 2019/1/12
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoServiceImplTest {

	@InjectMocks
	private DeviceInfoServiceImpl deviceInfoService;

	@Mock
	private DeviceInfoDao deviceInfoDao;

	@Mock
	private DeviceCollectingDao deviceCollectingDao;

	@Mocked
	private ServletRequestAttributes servletRequestAttributes;

	@Mock
	private AreaInfoService areaInfoService;

	@Mock
	private AlarmCurrentFeign alarmCurrentFeign;

	@Mock
	private LogProcess logProcess;

	@Mock
	private HttpServletRequest request;

	@Mock
	AreaInfoDao areaInfoDao;
	private List<DeviceInfo> deviceInfos;

	public static final String DEFAULT_RESULT = "result";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceName("aaa");
		deviceInfos = new ArrayList<>();
		deviceInfos.add(deviceInfo);
	}

	@Test
	public void addDeviceTest() throws Exception {
//		new Expectations(I18nUtils.class) {
//			{
//				I18nUtils.getString(anyString);
//				result = "demo";
//			}
//		};
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hSet(anyString, anyString, anyString);
//				result = true;
//			}
//		};
//		DeviceInfo deviceInfo = new DeviceInfo();
//		try {
//			deviceInfoService.addDevice(deviceInfo);
//			Assert.fail();
//		} catch (FilinkDeviceException e) {
//			Assert.assertNotNull(e);
//			Assert.assertEquals(e.getMessage(), "demo");
//		}
//
//		deviceInfo.setDeviceName("test");
//		deviceInfo.setDeviceType("001");
//		deviceInfo.setAreaId("69fd919d0fcf11e99e5ee8b1fc5e4c2c");
//		deviceInfo.setAddress("dsadsadda");
//		deviceInfo.setPositionGps("123,123");
//		deviceInfo.setPositionBase("123,123");
//
//		when(deviceInfoDao.insertDevice(deviceInfo)).thenReturn(1);
//		AreaInfo areaInfo = new AreaInfo();
//		areaInfo.setAreaName("testarea");
//
//		when(areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId())).thenReturn(areaInfo);
//		new Expectations(RequestContextHolder.class) {
//			{
//				RequestContextHolder.getRequestAttributes();
//				result = servletRequestAttributes.getRequest();
//			}
//		};
//		when(request.getHeader("userId")).thenReturn("111");
//		Result s = deviceInfoService.addDevice(deviceInfo);
//		Assert.assertTrue(s.getCode() == ResultUtils.success().getCode());
//
//		//修改时缓存错误
//		when(deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId())).thenThrow(Exception.class);
//		s = deviceInfoService.addDevice(deviceInfo);
//		Assert.assertTrue(s.getCode() == ResultUtils.success().getCode());
//
//
//		deviceInfo.setPositionGps("123,123,123");
//		try {
//			deviceInfoService.addDevice(deviceInfo);
//			Assert.fail();
//		} catch (FilinkDeviceException e) {
//			Assert.assertTrue(e.getMessage() == "demo");
//		}
//
//		deviceInfo.setPositionGps("123,123");
//		deviceInfo.setPositionBase("123,123,123");
//		try {
//			deviceInfoService.addDevice(deviceInfo);
//			Assert.fail();
//		} catch (FilinkDeviceException e) {
//			Assert.assertTrue(e.getMessage() == "demo");
//		}
//
//		deviceInfo.setPositionBase("123.4,123.4");
//		//测试非法字符
//		deviceInfo.setDeviceName("!@#$%");
//		try {
//			deviceInfoService.addDevice(deviceInfo);
//			Assert.fail();
//		} catch (FilinkDeviceException e) {
//			Assert.assertNotNull(e);
//			Assert.assertTrue(e.getMessage() == "demo");
//		}
//
//		deviceInfo.setDeviceName("testName");
//		String randomString = RandomStringUtils.randomAlphanumeric(65);
//		deviceInfo.setAddress(randomString);
//		try {
//			deviceInfoService.addDevice(deviceInfo);
//			Assert.fail();
//		} catch (Exception e) {
//			Assert.assertTrue(e instanceof FilinkDeviceException);
//			Assert.assertTrue(e.getMessage() == "demo");
//		}
//
//		deviceInfo.setAddress("address");
//		when(deviceInfoDao.selectByName(deviceInfo.getDeviceName())).thenReturn(deviceInfo);
//		try {
//			deviceInfoService.addDevice(deviceInfo);
//			Assert.fail();
//		} catch (Exception e) {
//			Assert.assertTrue(e instanceof FilinkDeviceNameSameException);
//		}
//
//		when(deviceInfoDao.selectByName(deviceInfo.getDeviceName())).thenReturn(null);
//		when(deviceInfoDao.insertDevice(deviceInfo)).thenReturn(-1);
//		Result s1 = deviceInfoService.addDevice(deviceInfo);deviceInfoService.addDevice(deviceInfo);
//		Assert.assertTrue(s1.getCode() == ResultCode.FAIL);

	}

	@Test
	public void checkDeviceNameTest() {
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceName("device001");
		deviceInfo.setDeviceId("1");
		when(deviceInfoDao.selectByName("device001")).thenReturn(deviceInfo);
		boolean result1 = deviceInfoService.checkDeviceName("", "device001");
		boolean result2 = deviceInfoService.checkDeviceName("1", "device001");
		boolean result3 = deviceInfoService.checkDeviceName("2", "device001");
		Assert.assertEquals(result1, true);
		Assert.assertEquals(result2, false);
		Assert.assertEquals(result3, true);
	}

	@Test
	public void updateDeviceTest() throws Exception {
		new Expectations(I18nUtils.class) {
			{
				I18nUtils.getString(anyString);
				result = "demo";
			}
		};
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hSet(anyString, anyString, anyString);
//				result = true;
//			}
//		};
		DeviceInfo deviceInfo = new DeviceInfo();

		//校验设施id为空
		try {
			deviceInfoService.updateDevice(deviceInfo);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof FilinkDeviceException);
			Assert.assertEquals(e.getMessage(), "demo");
		}

		deviceInfo.setDeviceId("id001");
		// 校验必填参数
		try {
			deviceInfoService.updateDevice(deviceInfo);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof FilinkDeviceException);
			Assert.assertEquals(e.getMessage(), "demo");
		}

		deviceInfo.setDeviceName("name001");
		deviceInfo.setDeviceType("type001");
		deviceInfo.setAreaId("areaid001");
		deviceInfo.setPositionGps("123,123");
		deviceInfo.setAddress("address001");
		deviceInfo.setRemarks("remarks001");

		deviceInfo.setPositionBase("132,123,123");
		try {
			deviceInfoService.updateDevice(deviceInfo);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof FilinkDeviceException);
			Assert.assertEquals(e.getMessage(), "demo");
		}
		deviceInfo.setPositionBase("132,123");

		//测试非法字符
		deviceInfo.setDeviceName("!@#$%");
		try {
			deviceInfoService.updateDevice(deviceInfo);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof FilinkDeviceException);
			Assert.assertEquals(e.getMessage(), "demo");
		}
		deviceInfo.setDeviceName("testName");

		String randomString = RandomStringUtils.randomAlphanumeric(201);
		deviceInfo.setRemarks(randomString);
		try {
			deviceInfoService.updateDevice(deviceInfo);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof FilinkDeviceException);
			Assert.assertEquals(e.getMessage(), "demo");
		}
		deviceInfo.setRemarks("remarks");

		when(deviceInfoDao.selectDeviceById(deviceInfo.getDeviceId())).thenReturn(null);
		try {
			deviceInfoService.updateDevice(deviceInfo);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof FilinkDeviceException);
			Assert.assertEquals(e.getMessage(), "demo");
		}
		when(deviceInfoDao.selectDeviceById(deviceInfo.getDeviceId())).thenReturn(deviceInfo);

		new Expectations(RequestContextHolder.class) {
			{
				RequestContextHolder.getRequestAttributes();
				result = servletRequestAttributes.getRequest();
			}
		};
		when(deviceInfoDao.updateById(deviceInfo)).thenReturn(-1);
		Result s1 = deviceInfoService.updateDevice(deviceInfo);
		Assert.assertTrue(s1.getCode() == ResultCode.FAIL);
		when(deviceInfoDao.updateById(deviceInfo)).thenReturn(1);

		AreaInfo areaInfo = new AreaInfo();
		areaInfo.setAreaName("testarea");
		when(request.getHeader("userId")).thenReturn("111");
		when(areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId())).thenReturn(areaInfo);
		Result s = deviceInfoService.updateDevice(deviceInfo);
		Assert.assertTrue(s.getCode() == ResultUtils.success().getCode());

		//修改时缓存错误
		when(deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId())).thenThrow(Exception.class);
		s = deviceInfoService.updateDevice(deviceInfo);
		Assert.assertTrue(s.getCode() == ResultUtils.success().getCode());

	}

	/**
	 * 校验设施名重复异常
	 */
	@Test
	public void updateDeviceNameSameExceptionTest() throws FilinkDeviceNameSameException{
//		new Expectations(I18nUtils.class) {
//			{
//				I18nUtils.getString(anyString);
//				result = "demo";
//			}
//		};
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hSet(anyString, anyString, anyString);
//				result = true;
//			}
//		};
//		DeviceInfo deviceInfo = new DeviceInfo();
//		deviceInfo.setDeviceId("id001");
//		deviceInfo.setDeviceName("name001");
//		deviceInfo.setDeviceType("type001");
//		deviceInfo.setAreaId("areaid001");
//		deviceInfo.setPositionGps("123,123");
//		deviceInfo.setPositionBase("132,123");
//		deviceInfo.setAddress("address001");
//
//		DeviceInfo deviceInfo1 = new DeviceInfo();
//		deviceInfo1.setDeviceId("id002");
//		deviceInfo1.setDeviceName("name001");
//		when(deviceInfoDao.selectDeviceById(deviceInfo.getDeviceId())).thenReturn(deviceInfo);
//		when(deviceInfoDao.selectByName("name001")).thenReturn(deviceInfo1);
//		thrown.expect(FilinkDeviceNameSameException.class);
//		deviceInfoService.updateDevice(deviceInfo);
	}

	@Test
	public void listDeviceTest() throws Exception {
		QueryCondition<DeviceInfo> queryCondition = new QueryCondition<>();
		PageCondition pageCondition = new PageCondition();
		pageCondition.setPageSize(10);
		pageCondition.setPageNum(1);
		FilterCondition filterCondition = new FilterCondition();
		List<FilterCondition> filterConditions = new ArrayList<>();
		filterCondition.setFilterField("device_name");
		filterCondition.setOperator("like");
		filterCondition.setFilterValue("1");
		filterConditions.add(filterCondition);
		queryCondition.setPageCondition(pageCondition);
		queryCondition.setFilterConditions(filterConditions);
		queryCondition.setSortCondition(new SortCondition());
		EntityWrapper wrapper = myBatiesBuildQuery(queryCondition);
		Page page = myBatiesBuildPage(queryCondition);
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("1");
		deviceInfo.setDeviceName("test");
		deviceInfo.setDeviceType("001");
		deviceInfo.setAreaId("123");
		deviceInfo.setPositionBase("132,123");
		deviceInfo.setPositionGps("123,123");
		List list = new ArrayList();
		list.add(deviceInfo);
		list.add(deviceInfo);
		when(deviceInfoDao.selectPage(page, wrapper)).thenReturn(list);
		when(deviceInfoDao.selectCount(wrapper)).thenReturn(2);
		// new Expectations(I18nUtils.class) {
		// {
		// I18nUtils.getString(anyString);
		// result = "demo";
		// }
		// };
		List<DeviceInfoDto> list1 = new ArrayList<>();
		DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
		deviceInfoDto.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
		deviceInfoDto.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
		list1.add(deviceInfoDto);
//		when(deviceInfoDao.selectDeviceByPage(any(), any(), any())).thenReturn(list1);
//		Result s = deviceInfoService.listDevice(queryCondition);
//		Assert.assertTrue(s.getCode() == ResultUtils.success().getCode());
	}

	@Test
	public void getDeviceByIdTest() throws Exception {
//		new Expectations(I18nUtils.class) {
//			{
//				I18nUtils.getString(anyString);
//				result = "demo";
//			}
//		};
//		//校验设施id为空
//		try {
//			deviceInfoService.getDeviceById("","");
//			Assert.fail();
//		} catch (Exception e) {
//			Assert.assertTrue(e instanceof FilinkDeviceException);
//			Assert.assertEquals(e.getMessage(),"demo");
//		}
//
//		DeviceInfo deviceInfo = new DeviceInfo();
//		deviceInfo.setDeviceId("id1");
//		deviceInfo.setDeviceName("test");
//		deviceInfo.setAreaId("123");
//		deviceInfo.setDeviceType("001");
//		when(deviceInfoDao.selectDeviceById("123")).thenReturn(deviceInfo);
//		AreaInfo areaInfo = new AreaInfo();
//		areaInfo.setAreaName("testarea");
//		when(areaInfoDao.selectAreaInfoById("123")).thenReturn(areaInfo);
//		Result result = new Result();
//		result.setData(areaInfo);
//		result.setMsg("123");
//		when(areaInfoService.queryAreaById("123")).thenReturn(result);
//		new Expectations(RequestInfoUtils.class) {
//			{
//				RequestInfoUtils.getUserId();
//				result = "userId";
//			}
//		};
//		when(deviceCollectingDao.selectAttentionDeviceCount("id1","userId")).thenReturn(1);
//		Result s = deviceInfoService.getDeviceById("123");
//		Assert.assertTrue(s.getCode() == ResultUtils.success().getCode());

//		when(deviceInfoDao.selectDeviceById("123")).thenReturn(null);
//		try {
//			deviceInfoService.getDeviceById("123");
//			Assert.fail();
//		} catch (Exception e) {
//			Assert.assertTrue(e instanceof FilinkDeviceException);
//		}
	}

	/***
	 * queryDeviceByAreaId
	 */
	@Test
	public void queryDeviceByAreaId() {
		List<DeviceInfo> a = deviceInfoService.queryDeviceByAreaId("a");
		Assert.assertTrue(a != null);
	}

	/**
	 * queryDeviceAreaIdIsNull
	 */
	@Test
	public void queryDeviceAreaIdIsNull() {
		// List<DeviceInfo> deviceInfos = deviceInfoService.queryDeviceAreaIdIsNull();
		// Assert.assertTrue(deviceInfos != null);
	}

	/**
	 * setAreaDevice
	 */
	@Test
	public void setAreaDevice() {
//		Map<String, List<String>> map = new HashMap<>();
//		List<String> list = new ArrayList<>();
//		map.put("m1", list);
//		boolean b1 = deviceInfoService.setAreaDevice(map);
//		Assert.assertTrue(b1);

//		list.add("s1");
//		//返回List有值
//		when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(list);
//		DeviceInfo deviceInfo = new DeviceInfo();
//		deviceInfo.setDeviceId("s1");
//		when(deviceInfoDao.selectDeviceById(list.get(0))).thenReturn(deviceInfo);
//
//		new Expectations(I18nUtils.class) {
//			{
//				I18nUtils.getString(anyString);
//				result = DEFAULT_RESULT;
//			}
//		};
//		try {
//			deviceInfoService.setAreaDevice(map);
//			Assert.fail();
//		} catch (FilinkAreaDirtyDataException e) {
//			Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
//		}
//
//		deviceInfo.setDeviceName("name1");
//		try {
//			deviceInfoService.setAreaDevice(map);
//			Assert.fail();
//		} catch (FilinkAreaDirtyDataException e) {
//			Assert.assertTrue(e.getMessage().contains(deviceInfo.getDeviceName() + DEFAULT_RESULT));
//		}
//
//		//List没有值
//		when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(new ArrayList<>());
//		List<DeviceInfo> deviceInfoList = new ArrayList<>();
//		when(deviceInfoDao.selectByIds(list)).thenReturn(deviceInfoList);
//		boolean b2 = deviceInfoService.setAreaDevice(map);
//		Assert.assertTrue(!b2);

//		deviceInfoList.add(deviceInfo);
//		when(deviceInfoDao.setAreaDevice("m1", list)).thenReturn(1);
//		HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
//		when(deviceInfoDao.queryDeviceAreaById(list.get(0))).thenReturn(homeDeviceInfoDto);
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hSet(anyString, anyString, any());
//				result = true;
//			}
//		};
//		boolean b3 = deviceInfoService.setAreaDevice(map);
//		Assert.assertTrue(b3);

//		when(deviceInfoDao.setAreaDevice("m1", list)).thenReturn(0);
//		try {
//			deviceInfoService.setAreaDevice(map);
//			Assert.fail();
//		} catch (FilinkAreaDateBaseException e) {
//			Assert.assertNotNull(e);
//		}
	}

	@Test
	public void deleteDeviceByIdsTest() {
//		String[] str = { "00000de5373611e9aaf5f48e38f46893" };
//		DeviceInfo deviceInfo = new DeviceInfo();
//		deviceInfo.setDeviceName("test");
//		deviceInfo.setAreaId("123");
//		deviceInfo.setDeviceType("001");
//		when(deviceInfoDao.selectDeviceById("00000de5373611e9aaf5f48e38f46893")).thenReturn(deviceInfo);
//		List list = new ArrayList();
//		list.add("00000de5373611e9aaf5f48e38f46893");
//		when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(new ArrayList());
//		List<DeviceInfo> deviceInfoList = new ArrayList<>();
//		deviceInfoList.add(deviceInfo);
//		when(deviceInfoDao.selectDeviceByIds(str)).thenReturn(deviceInfoList);
//		when(logProcess.generateAddLogToCallParam("1")).thenReturn(new AddLogBean());
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hRemove(anyString, anyString);
//				result = null;
//			}
//		};
//		new Expectations(I18nUtils.class) {
//			{
//				I18nUtils.getString(anyString);
//				result = "demo";
//			}
//		};
//		Result result = deviceInfoService.deleteDeviceByIds(str);
//		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
//
//		//设施号不存在
//		when(deviceInfoDao.selectDeviceById(anyString())).thenReturn(null);
//		try {
//			deviceInfoService.deleteDeviceByIds(str);
//			Assert.fail();
//		} catch (Exception e) {
//			Assert.assertTrue(e instanceof FilinkDeviceException);
//		}
	}

//	@Test
//	public void findDeviceBySeriaNumberTest() throws Exception {
//
//		DeviceInfo deviceInfo = new DeviceInfo();
//		deviceInfo.setDeviceId("id1");
//		deviceInfo.setDeviceName("test");
//		deviceInfo.setDeviceType("001");
//		deviceInfo.setAreaId("123");
//		deviceInfo.setPositionBase("132,123");
//		deviceInfo.setPositionGps("123,123");
//		deviceInfo.setAddress("dsadsadda");
//		when(deviceInfoDao.selectDeviceBySeriaNumber("1")).thenReturn(deviceInfo);
//		AreaInfo areaInfo = new AreaInfo();
//		areaInfo.setAreaName("testarea");
//		when(areaInfoDao.selectAreaInfoById("123")).thenReturn(areaInfo);
//		Result result = new Result();
//		result.setData(areaInfo);
//		result.setMsg("123");
//		when(areaInfoService.queryAreaById(any())).thenReturn(result);
//		new Expectations(RequestInfoUtils.class) {
//			{
//				RequestInfoUtils.getUserId();
//				result = "userId";
//			}
//		};
//		when(deviceCollectingDao.selectAttentionDeviceCount("id1","userId")).thenReturn(1);
//		DeviceInfoDto deviceInfoDto = deviceInfoService.findDeviceBySeriaNumber("1");
//		Assert.assertEquals(deviceInfoDto.getDeviceType(), "001");
//	}

	@Test
	public void deviceCanChangeDetailTest() {
		List list = new ArrayList();
		when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(new ArrayList());
		list.add("1");
		when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(list);
		Boolean b = deviceInfoService.deviceCanChangeDetail("1");
		Assert.assertEquals(b.toString(), "false");
	}

	/**
	 * queryDeviceAreaListTest
	 */
	@Test
	public void queryDeviceAreaListTest() {
//		Map<Object, Object> deviceInfoMap = new HashMap<>();
//		List<HomeDeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
//		HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
//		homeDeviceInfoDto.setDeviceId("11111");
//		deviceInfoMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
//		deviceInfoDtoList.add(homeDeviceInfoDto);
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hasKey(anyString);
//				result = true;
//
//				RedisUtils.hGetMap(anyString);
//				result = deviceInfoMap;
//			}
//		};
//		Result result = deviceInfoService.queryDeviceAreaList();
//		Assert.assertEquals(1, ((List) result.getData()).size());
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hasKey(anyString);
//				result = false;
//			}
//		};
//		when(deviceInfoDao.queryDeviceAreaList()).thenReturn(null);
//		result = deviceInfoService.queryDeviceAreaList();
//		Assert.assertEquals(0, ((List) result.getData()).size());
//		when(deviceInfoDao.queryDeviceAreaList()).thenReturn(deviceInfoDtoList);
//		new Expectations(RedisUtils.class) {
//			{
//				RedisUtils.hSetMap(anyString, (Map<String,Object>)any);
//				result = true;
//			}
//		};
//		result = deviceInfoService.queryDeviceAreaList();
//		Assert.assertEquals(1, ((List) result.getData()).size());
	}

}