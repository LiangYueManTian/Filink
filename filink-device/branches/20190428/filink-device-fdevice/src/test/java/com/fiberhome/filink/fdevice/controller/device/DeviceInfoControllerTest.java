package com.fiberhome.filink.fdevice.controller.device;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.service.device.impl.DeviceInfoServiceImpl;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author WH1707069 create on 2019/1/12
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoControllerTest {

	@InjectMocks
	private DeviceInfoController deviceInfoController;

	@Mock
	private DeviceInfoServiceImpl deviceInfoService;

	@Before
	public void setUp() {

	}

	@Test
	public void addDeviceTest() {
		DeviceInfo deviceInfo = new DeviceInfo();
		when(deviceInfoService.addDevice(deviceInfo)).thenReturn(ResultUtils.success());
		Result result = deviceInfoController.addDevice(deviceInfo);
		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
	}

	@Test
	public void checkDeviceNameTest() {
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("1");
		deviceInfo.setDeviceName("2");
		DeviceInfo deviceInfo1 = null;
		new Expectations(I18nUtils.class) {
			{
				I18nUtils.getString(anyString);
				result = "demo";
			}
		};
		when(deviceInfoService.checkDeviceName("1", "2")).thenReturn(true);
		when(deviceInfoService.checkDeviceName("2", "2")).thenReturn(false);
		Result result = deviceInfoController.checkDeviceName(deviceInfo);
		Result result1 = deviceInfoController.checkDeviceName(deviceInfo1);
		deviceInfo.setDeviceId("2");
		Result result2 = deviceInfoController.checkDeviceName(deviceInfo);
		Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_NAME_SAME);
		Assert.assertTrue(result1.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);
		Assert.assertTrue(result2.getCode() == ResultUtils.success().getCode());
	}

	@Test
	public void updateDeviceTest() {
		DeviceInfo deviceInfo = new DeviceInfo();
		when(deviceInfoService.updateDevice(deviceInfo)).thenReturn(new Result());
		Result result = deviceInfoController.updateDevice(deviceInfo);
		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
	}

	@Test
	public void listDeviceTest() throws Exception {
		when(deviceInfoService.listDevice(any())).thenReturn(new Result());
		Result result = deviceInfoController.listDevice(new QueryCondition<>());
		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
	}

	@Test
	public void getDeviceByIdTest() throws Exception {
//		when(deviceInfoService.getDeviceById(any())).thenReturn(new Result());
//		Result result = deviceInfoController.getDeviceById("1");
//		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
	}

	@Test
	public void feignGetDeviceById() throws Exception {
//		Result r = new Result();
//		when(deviceInfoService.getDeviceById(any())).thenReturn(r);
//		DeviceInfoDto deviceInfoDto = deviceInfoController.feignGetDeviceById("1");
//		Assert.assertEquals(deviceInfoDto, r.getData());
	}

	@Test
	public void queryDeviceAreaList() {
		when(deviceInfoService.queryDeviceAreaList()).thenReturn(new Result());
		Result result = deviceInfoController.queryDeviceAreaList();
		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
	}

	@Test
	public void deleteDeviceByIdsTest() {
		String[] str = {"!"};
		when(deviceInfoService.deleteDeviceByIds(any())).thenReturn(new Result());
		Result result = deviceInfoController.deleteDeviceByIds(str);
		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
	}

//	@Test
//	public void findDeviceBySeriaNumberTest() throws Exception {
//		DeviceInfoDto deviceInfo = new DeviceInfoDto();
//		deviceInfo.setDeviceId("123");
//		when(deviceInfoController.findDeviceBySeriaNumber("1")).thenReturn(deviceInfo);
//		DeviceInfoDto result = deviceInfoController.findDeviceBySeriaNumber("1");
//		Assert.assertTrue(result.getDeviceId().equals(deviceInfo.getDeviceId()));
//
//	}

	@Test
	public void deviceCanChangeDetailTest() {
		new Expectations(I18nUtils.class) {
			{
				I18nUtils.getString(anyString);
				result = "demo";
			}
		};
		when(deviceInfoService.deviceCanChangeDetail("1")).thenReturn(true);
		when(deviceInfoService.deviceCanChangeDetail("2")).thenReturn(false);
		Result result = deviceInfoController.deviceCanChangeDetail("1");
		Result result1 = deviceInfoController.deviceCanChangeDetail("2");
		Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
		Assert.assertTrue(result1.getCode() == DeviceResultCode.DEVICE_CANNOT_UPDATE);
	}

}