package com.fiberhome.filink.rfid.service.impl.statistics;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.deviceapi.api.DevicePortUtilizationRateFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.dao.statistics.OdnFacilityResourcesStatisticsDao;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.DiscPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.FramePortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.JumpFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.MeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.resp.statistics.JumpConnectionStatisticsResp;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtilDto;
import mockit.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * ONT设施资源统计 服务类测试
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/27
 */
@RunWith(MockitoJUnitRunner.class)
public class OdnFacilityResourcesStatisticsServiceImplTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private OdnFacilityResourcesStatisticsServiceImpl odnFacilityResourcesStatisticsService;
    /**
     * Mock OdnFacilityResourcesStatisticsDao
     */
    @Mock
    private OdnFacilityResourcesStatisticsDao odnFacilityResourcesStatisticsDao;
    /**
     * Mock JumpFiberInfoDao
     */
    @Mock
    private JumpFiberInfoDao jumpFiberInfoDao;
    /**
     * Mock DevicePortUtilizationRateFeign
     */
    @Mock
    private DevicePortUtilizationRateFeign devicePortUtilizationRateFeign;

    /**
     * list
     */
    List<Map<String, Integer>> list = new ArrayList<>();
    /**
     * 返回结果封装
     */
    private Result resultResp;


    @Before
    public void setUp() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("0", 12);
        list.add(map);
        ReflectionTestUtils.setField(odnFacilityResourcesStatisticsService, "exportServerName", "filink-rfid-server");
        ReflectionTestUtils.setField(odnFacilityResourcesStatisticsService, "maxExportDataSize", 10000);
    }

    @Test
    public void jumpFiberPortStatistics() {
        JumpFiberPortStatisticsReq jumpFiberPortStatisticsReq = new JumpFiberPortStatisticsReq();
        when(odnFacilityResourcesStatisticsDao.jumpFiberPortStatistics(jumpFiberPortStatisticsReq)).thenReturn(list);
        odnFacilityResourcesStatisticsService.jumpFiberPortStatistics(jumpFiberPortStatisticsReq);
    }

    @Test
    public void exportJumpFiberPortStatistics() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        odnFacilityResourcesStatisticsService.exportJumpFiberPortStatistics(new ExportDto<>());
    }

    @Test
    public void meltFiberPortStatistics() {
        MeltFiberPortStatisticsReq meltFiberPortStatisticsReq=new MeltFiberPortStatisticsReq();
        when(odnFacilityResourcesStatisticsDao.portCountStatistics(meltFiberPortStatisticsReq)).thenReturn(100);
        when(odnFacilityResourcesStatisticsDao.meltFiberPortStatistics(meltFiberPortStatisticsReq)).thenReturn(60);
        odnFacilityResourcesStatisticsService.meltFiberPortStatistics(meltFiberPortStatisticsReq);
    }

    @Test
    public void exportMeltFiberPortStatistics() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        odnFacilityResourcesStatisticsService.exportMeltFiberPortStatistics(new ExportDto<>());
    }

    @Test
    public void discPortStatistics() {
        DiscPortStatisticsReq discPortStatisticsReq = new DiscPortStatisticsReq();
        when(odnFacilityResourcesStatisticsDao.discPortStatistics(discPortStatisticsReq)).thenReturn(list);
        odnFacilityResourcesStatisticsService.discPortStatistics(discPortStatisticsReq);
    }

    @Test
    public void exportDiscPortStatistics() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        odnFacilityResourcesStatisticsService.exportDiscPortStatistics(new ExportDto<>());
    }

    @Test
    public void framePortStatistics() {
        FramePortStatisticsReq framePortStatisticsReq = new FramePortStatisticsReq();
        when(odnFacilityResourcesStatisticsDao.framePortStatistics(framePortStatisticsReq)).thenReturn(list);
        odnFacilityResourcesStatisticsService.framePortStatistics(framePortStatisticsReq);
    }

    @Test
    public void exportFramePortStatistics() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        odnFacilityResourcesStatisticsService.exportFramePortStatistics(new ExportDto<>());
    }

    @Test
    public void portTopNumber() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        odnFacilityResourcesStatisticsService.portTopNumber(new ExportDto<>());
    }

    @Test
    public void devicePortStatistics() {
        JumpFiberPortStatisticsReq jumpFiberPortStatisticsReq = new JumpFiberPortStatisticsReq();
        String deviceId = "zz";
        List<String> listDeviceId = new ArrayList<>();
        listDeviceId.add(deviceId);
        jumpFiberPortStatisticsReq.setFacilities(listDeviceId);
        when(odnFacilityResourcesStatisticsDao.jumpFiberPortStatistics(jumpFiberPortStatisticsReq)).thenReturn(list);
        odnFacilityResourcesStatisticsService.devicePortStatistics(deviceId);
    }

    @Test
    public void deviceUsePortStatistics() {

        List<Map<String, Object>> listCount = new ArrayList<>();
        Map<String, Object> map  = new HashMap<>();
        map.put("id","XX");
        map.put("number",9L);
        listCount.add(map);
        String deviceId = "XX";
        List<String> listDeviceId =new ArrayList<>();
        listDeviceId.add(deviceId);
        when(odnFacilityResourcesStatisticsDao.countPortStatisticsList(listDeviceId)).thenReturn(listCount);
        List<JumpConnectionStatisticsResp> listJumpConnectionStatisticsResp = new ArrayList<>();
        when( jumpFiberInfoDao.queryJumpFiberInfoByDeviceId(listDeviceId)).thenReturn(listJumpConnectionStatisticsResp);
        odnFacilityResourcesStatisticsService.deviceUsePortStatistics(deviceId);
    }

    @Test
    public void portStatistics() {
        List<Map<String, Object>> listCount = new ArrayList<>();
        Map<String, Object> map  = new HashMap<>();
        map.put("id","XX");
        map.put("number",9L);
        listCount.add(map);
        String deviceId = "XX";
        List<String> listDeviceId =new ArrayList<>();
        listDeviceId.add(deviceId);
        List<JumpConnectionStatisticsResp> list  = new ArrayList<>();
        JumpConnectionStatisticsResp jumpConnectionStatisticsResp = new JumpConnectionStatisticsResp();
        jumpConnectionStatisticsResp.setDeviceId(deviceId);
        jumpConnectionStatisticsResp.setInnerDevice("0");
        list.add(jumpConnectionStatisticsResp);
        when(jumpFiberInfoDao.queryJumpFiberInfoByDeviceId(listDeviceId)).thenReturn(list);
        when(odnFacilityResourcesStatisticsDao.countPortStatisticsList(listDeviceId)).thenReturn(listCount);
        List<DevicePortUtilizationRate> rate = new ArrayList<>();
        when(devicePortUtilizationRateFeign.addPortStatistics(rate)).thenReturn(1);
        odnFacilityResourcesStatisticsService.portStatistics(listDeviceId);
    }
}