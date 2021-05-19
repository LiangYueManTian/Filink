package com.fiberhome.filink.rfid.service.impl.statistics;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.statistics.FiberOpticsAndCoreStatisticsDao;
import com.fiberhome.filink.rfid.req.statistics.opticable.CoreStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.OpticCableInfoSectionStatisticsReq;
import com.fiberhome.filink.rfid.resp.statistics.CoreStatisticsResp;
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
 * 光缆及纤芯统计 服务类测试
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/27
 */
@RunWith(MockitoJUnitRunner.class)
public class FiberOpticsAndCoreStatisticsServiceImplTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private FiberOpticsAndCoreStatisticsServiceImpl fiberOpticsAndCoreStatisticsService;
    /**
     * Mock FiberOpticsAndCoreStatisticsDao
     */
    @Mock
    private FiberOpticsAndCoreStatisticsDao fiberOpticsAndCoreStatisticsDao;
    /**
     * Mock OpticCableSectionInfoDao
     */
    @Mock
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
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
        map.put("state", "0");
        map.put("number", "9");
        list.add(map);
        ReflectionTestUtils.setField(fiberOpticsAndCoreStatisticsService, "exportServerName", "filink-rfid-server");
        ReflectionTestUtils.setField(fiberOpticsAndCoreStatisticsService, "maxExportDataSize", 10000);
    }

    @Test
    public void opticalFiberStatistics() {
        when(fiberOpticsAndCoreStatisticsDao.opticalFiberStatistics()).thenReturn(list);
        fiberOpticsAndCoreStatisticsService.opticalFiberStatistics();
    }

    @Test
    public void exportOpticalFiberStatistics() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        fiberOpticsAndCoreStatisticsService.exportOpticalFiberStatistics(new ExportDto<>());

    }

    @Test
    public void opticalFiberSection() throws  Exception{
        OpticCableInfoSectionStatisticsReq req = new OpticCableInfoSectionStatisticsReq();
        when(fiberOpticsAndCoreStatisticsDao.opticalFiberSection(req)).thenReturn(list);
        fiberOpticsAndCoreStatisticsService.opticalFiberSection(req);
        fiberOpticsAndCoreStatisticsService.opticalFiberSection(req);
    }

    @Test
    public void exportOpticalFiberSection() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        fiberOpticsAndCoreStatisticsService.exportOpticalFiberSection(new ExportDto<>());
    }

    @Test
    public void coreStatistics() {
        CoreStatisticsResp  resp = new CoreStatisticsResp();
        resp.setTotalCount(12);
        resp.setUsedCount(10);
        CoreStatisticsReq coreStatisticsReq = new CoreStatisticsReq();
        when(opticCableSectionInfoDao.queryOpticCableSectionById(coreStatisticsReq)).thenReturn(resp);
        fiberOpticsAndCoreStatisticsService.coreStatistics(coreStatisticsReq);
        when(opticCableSectionInfoDao.queryOpticCableSectionById(coreStatisticsReq)).thenReturn(null);
        fiberOpticsAndCoreStatisticsService.coreStatistics(coreStatisticsReq);
    }

    @Test
    public void exportCoreStatistics() {
        new Expectations(ExportServiceUtil.class) {
            {
                ExportServiceUtil.exportProcessing((ExportServiceUtilDto) any);
                result = resultResp;
            }
        };
        fiberOpticsAndCoreStatisticsService.exportCoreStatistics(new ExportDto<>());
    }
}