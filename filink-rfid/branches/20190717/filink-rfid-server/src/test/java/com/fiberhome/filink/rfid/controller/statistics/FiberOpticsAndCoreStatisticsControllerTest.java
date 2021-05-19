package com.fiberhome.filink.rfid.controller.statistics;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticCableInfoStatisticReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticalCableSectionStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.CoreStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.OpticCableInfoSectionStatisticsReq;
import com.fiberhome.filink.rfid.service.statistics.FiberOpticsAndCoreStatisticsService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * <p>
 * 光缆及纤芯统计 控制层测试类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-27
 */
@RunWith(JMockit.class)
public class FiberOpticsAndCoreStatisticsControllerTest {

    /**
     * 被测试类
     */
    @Tested
    private FiberOpticsAndCoreStatisticsController fiberOpticsAndCoreStatisticsController;
    /**
     * Mock FiberOpticsAndCoreStatisticsService
     */
    @Injectable
    private FiberOpticsAndCoreStatisticsService fiberOpticsAndCoreStatisticsService;

    /**
     * 返回结果封装
     */
    private Result resultResp;

    /**
     * 光缆统计测试
     */
    @Test
    public void opticalFiber() {
        new Expectations() {
            {
                fiberOpticsAndCoreStatisticsService.opticalFiberStatistics();
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, fiberOpticsAndCoreStatisticsController.opticalFiber());
    }

    /**
     * 光缆统计导出测试
     */
    @Test
    public void exportOpticalFiber() {
        new Expectations() {
            {
                fiberOpticsAndCoreStatisticsService.exportOpticalFiberStatistics((ExportDto<ExportOpticCableInfoStatisticReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, fiberOpticsAndCoreStatisticsController.exportOpticalFiber(new ExportDto<>()));
    }

    /**
     * 光缆段统计测试
     */
    @Test
    public void opticalFiberSection() {
        new Expectations() {
            {
                fiberOpticsAndCoreStatisticsService.opticalFiberSection((OpticCableInfoSectionStatisticsReq) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, fiberOpticsAndCoreStatisticsController.opticalFiberSection(new OpticCableInfoSectionStatisticsReq()));
    }

    /**
     * 光缆段统计导出测试
     */
    @Test
    public void exportOpticalFiberSection() {
        new Expectations() {
            {
                fiberOpticsAndCoreStatisticsService.exportOpticalFiberSection((ExportDto<ExportOpticalCableSectionStatisticsReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, fiberOpticsAndCoreStatisticsController.exportOpticalFiberSection(new ExportDto<>()));
    }

    /**
     * 纤芯统计测试
     */
    @Test
    public void coreStatistics() {
        new Expectations() {
            {
                fiberOpticsAndCoreStatisticsService.coreStatistics((CoreStatisticsReq) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, fiberOpticsAndCoreStatisticsController.coreStatistics(new CoreStatisticsReq()));
    }

    /**
     * 纤芯统计导出测试
     */
    @Test
    public void exportCoreStatistics() {
        new Expectations() {
            {
                fiberOpticsAndCoreStatisticsService.exportCoreStatistics((ExportDto<ExportOpticalCableSectionStatisticsReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, fiberOpticsAndCoreStatisticsController.exportCoreStatistics(new ExportDto<>()));
    }
}