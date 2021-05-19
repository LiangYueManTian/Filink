package com.fiberhome.filink.rfid.controller.statistics;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportMeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortTopNumberReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.DiscPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.FramePortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.JumpFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.MeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.service.statistics.OdnFacilityResourcesStatisticsService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ONT设施资源统计 控制层测试类
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/27
 */
@RunWith(JMockit.class)
public class OdnFacilityResourcesStatisticsControllerTest {
    /**
     * 被测试类
     */
    @Tested
    private OdnFacilityResourcesStatisticsController odnFacilityResourcesStatisticsController;
    /**
     * Mock OdnFacilityResourcesStatisticsService
     */
    @Injectable
    private OdnFacilityResourcesStatisticsService odnFacilityResourcesStatisticsService;
    /**
     * Mock OdnFacilityResourcesStatisticsService
     */
    @Injectable
    private TemplateService templateService;
    /**
     * 返回结果封装
     */
    private Result resultResp;

    /**
     * 跳纤侧端口统计测试
     */
    @Test
    public void jumpFiberPortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.jumpFiberPortStatistics((JumpFiberPortStatisticsReq) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.jumpFiberPortStatistics(new JumpFiberPortStatisticsReq()));

    }

    /**
     * 跳纤侧端口统计测试测试
     */
    @Test
    public void exportJumpFiberPortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.exportJumpFiberPortStatistics((ExportDto<ExportPortStatisticsReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.exportJumpFiberPortStatistics(new ExportDto<>()));
    }

    @Test
    public void meltFiberPortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.meltFiberPortStatistics((MeltFiberPortStatisticsReq) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.meltFiberPortStatistics(new MeltFiberPortStatisticsReq()));
    }

    @Test
    public void exportMeltFiberPortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.exportMeltFiberPortStatistics((ExportDto<ExportMeltFiberPortStatisticsReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.exportMeltFiberPortStatistics(new ExportDto<>()));
    }

    @Test
    public void discPortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.discPortStatistics((DiscPortStatisticsReq) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.discPortStatistics(new DiscPortStatisticsReq()));

    }

    @Test
    public void exportDiscPortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.exportDiscPortStatistics((ExportDto<ExportPortStatisticsReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.exportDiscPortStatistics(new ExportDto<>()));

    }

    @Test
    public void framePortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.framePortStatistics((FramePortStatisticsReq) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.framePortStatistics(new FramePortStatisticsReq()));

    }

    @Test
    public void exportFramePortStatistics() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.exportFramePortStatistics((ExportDto<ExportPortStatisticsReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.exportFramePortStatistics(new ExportDto<>()));

    }

    @Test
    public void exportPortTopNumber() {
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.portTopNumber((ExportDto<ExportPortTopNumberReq>) any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.exportPortTopNumber(new ExportDto<>()));

    }

    @Test
    public void devicePortStatistics() {
        new Expectations() {
            {
                templateService.getRfIdDataAuthInfo(anyString);
                result = false;
            }
        };
        Assert.assertEquals(null, odnFacilityResourcesStatisticsController.devicePortStatistics("xxx").getData());

        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.devicePortStatistics(anyString);
                result = resultResp;
            }
            {
                templateService.getRfIdDataAuthInfo(anyString);
                result = true;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.devicePortStatistics("xxx"));

    }

    @Test
    public void deviceUsePortStatistics() {
        new Expectations() {
            {
                templateService.getRfIdDataAuthInfo(anyString);
                result = false;
            }
        };
        Assert.assertEquals(null, odnFacilityResourcesStatisticsController.deviceUsePortStatistics("xxx").getData());
        new Expectations() {
            {
                odnFacilityResourcesStatisticsService.deviceUsePortStatistics(anyString);
                result = resultResp;
            }
            {
                templateService.getRfIdDataAuthInfo(anyString);
                result = true;
            }
        };
        Assert.assertEquals(resultResp, odnFacilityResourcesStatisticsController.deviceUsePortStatistics("xxx"));

    }
}