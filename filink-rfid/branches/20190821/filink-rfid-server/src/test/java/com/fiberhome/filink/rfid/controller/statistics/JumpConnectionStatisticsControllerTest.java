package com.fiberhome.filink.rfid.controller.statistics;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionInCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionOutCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.jumpconnection.JumpConnectionStatisticsReq;
import com.fiberhome.filink.rfid.service.statistics.JumpConnectionStatisticsService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ONT设施跳接关系 控制层测试类
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/27
 */
@RunWith(JMockit.class)
public class JumpConnectionStatisticsControllerTest {
    /**
     * 被测试类
     */
    @Tested
    private JumpConnectionStatisticsController jumpConnectionStatisticsController;
    /**
     * Mock JumpConnectionStatisticsService
     */
    @Injectable
    private JumpConnectionStatisticsService jumpConnectionStatisticsService;
    /**
     * 返回结果封装
     */
    private Result resultResp;

    @Test
    public void jumpConnectionInCabinet() {
        new Expectations() {
            {
                jumpConnectionStatisticsService.inCabinet((JumpConnectionStatisticsReq)any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, jumpConnectionStatisticsController.jumpConnectionInCabinet(new JumpConnectionStatisticsReq()));
    }

    @Test
    public void exportJumpConnectionInCabinet() {
        new Expectations() {
            {
                jumpConnectionStatisticsService.exportInCabinet((ExportDto<ExportJumpConnectionInCabinetReq>)any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, jumpConnectionStatisticsController.exportJumpConnectionInCabinet(new ExportDto<>()));

    }

    @Test
    public void jumpConnectionOutCabinet() {
        new Expectations() {
            {
                jumpConnectionStatisticsService.outCabinet((JumpConnectionStatisticsReq)any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, jumpConnectionStatisticsController.jumpConnectionOutCabinet(new JumpConnectionStatisticsReq()));

    }

    @Test
    public void exportJumpConnectionOutCabinet() {
        new Expectations() {
            {
                jumpConnectionStatisticsService.exportOutCabinet((ExportDto<ExportJumpConnectionOutCabinetReq>)any);
                result = resultResp;
            }
        };
        Assert.assertEquals(resultResp, jumpConnectionStatisticsController.exportJumpConnectionOutCabinet(new ExportDto<>()));

    }
}