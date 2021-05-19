package com.fiberhome.filink.rfid.service.impl.statistics;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.export.statistics.JumpConnectionInCabinetExport;
import com.fiberhome.filink.rfid.export.statistics.JumpConnectionOutCabinetExport;
import com.fiberhome.filink.rfid.req.statistics.jumpconnection.JumpConnectionStatisticsReq;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.resp.statistics.JumpConnectionStatisticsResp;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtil;
import com.fiberhome.filink.rfid.utils.export.ExportServiceUtilDto;
import mockit.Expectations;
import org.junit.Assert;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * ONT设施跳接关系 服务类测试
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/27
 */
@RunWith(MockitoJUnitRunner.class)
public class JumpConnectionStatisticsServiceImplTest {

    /**
     * 被测试类
     */
    @InjectMocks
    private JumpConnectionStatisticsServiceImpl jumpConnectionStatisticsService;
    /**
     * Mock JumpFiberInfoDao
     */
    @Mock
    private JumpFiberInfoDao jumpFiberInfoDao;
    /**
     * Mock JumpConnectionInCabinetExport
     */
    @Mock
    private JumpConnectionInCabinetExport jumpConnectionInCabinetExport;
    /**
     * Mock JumpConnectionOutCabinetExport
     */
    @Mock
    private JumpConnectionOutCabinetExport jumpConnectionOutCabinetExport;
    /**
     * 注入模板接口
     */
    @Mock
    private TemplateService templateService;
    /**
     * list
     */
    List<Map<String, String>> list = new ArrayList<>();
    /**
     * 返回结果封装
     */
    private Result resultResp;


    @Before
    public void setUp() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("0", "12");
        list.add(map);
        ReflectionTestUtils.setField(jumpConnectionStatisticsService, "exportServerName", "filink-rfid-server");
        ReflectionTestUtils.setField(jumpConnectionStatisticsService, "maxExportDataSize", 10000);
    }

    @Test
    public void inCabinet() {

        List<JumpConnectionStatisticsResp> jumpConnectionStatisticsRespList = new ArrayList<>();
        JumpConnectionStatisticsResp req = new JumpConnectionStatisticsResp();
        req.setDeviceId("deviceId");
        req.setFrameNo("1");
        req.setDiscSide(0);
        req.setBoxSide(0);
        req.setDiscNo("1");
        req.setPortNo("no");
        req.setOppositeDeviceId("deviceId");
        req.setOppositeFrameNo("1");
        req.setOppositeDiscSide(0);
        req.setOppositeBoxSide(0);
        req.setOppositeDiscNo("1");
        req.setOppositePortNo("no");
        jumpConnectionStatisticsRespList.add(req);
        when(jumpFiberInfoDao.queryInCabinet(any())).thenReturn(jumpConnectionStatisticsRespList);
        when(templateService.queryPortIdByPortInfo(any())).thenReturn("portId");
        when(templateService.queryPortNumByPortId(any())).thenReturn("2");

        jumpConnectionStatisticsService.inCabinet(new JumpConnectionStatisticsReq());
    }

    @Test
    public void exportInCabinet() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        jumpConnectionStatisticsService.exportInCabinet(new ExportDto<>());
    }

    @Test
    public void outCabinet() {

        com.fiberhome.filink.bean.Result result = jumpConnectionStatisticsService.outCabinet(null);
        Assert.assertTrue(result.getCode() == RfIdResultCode.PARAMS_ERROR);

        List<JumpConnectionStatisticsResp> jumpConnectionStatisticsRespList = new ArrayList<>();
        JumpConnectionStatisticsResp resp = new JumpConnectionStatisticsResp();
        resp.setDeviceName("deviceName");
        resp.setDeviceId("deviceId");
        resp.setOppositeDeviceName("oppdeviceName");
        resp.setOppositeDeviceId("oppdevice");
        resp.setFrameNo("1");
        resp.setDiscNo("1");
        resp.setDiscSide(1);
        resp.setPortNo("1");
        resp.setBoxSide(1);
        jumpConnectionStatisticsRespList.add(resp);

        JumpConnectionStatisticsReq req = new JumpConnectionStatisticsReq();
        req.setDeviceId("XX");
        req.setDeviceName("xx");
        req.setOppositeDeviceId("ZZ");
        req.setOppositeDeviceName("zz");
        new Expectations() {
            {
                jumpFiberInfoDao.queryOutCabinet((JumpConnectionStatisticsReq) any);
                result = jumpConnectionStatisticsRespList;

                templateService.queryPortIdByPortInfo((PortInfoReqDto) any);
                result = "portID";
            }
        };
        this.jumpConnectionStatisticsService.outCabinet(req);
    }

    @Test
    public void exportOutCabinet() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        jumpConnectionStatisticsService.exportOutCabinet(new ExportDto<>());
    }
}