package com.fiberhome.filink.rfid.export.jumpfiber;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * JumpFiberExportTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(MockitoJUnitRunner.class)
public class JumpFiberExportTest {

    @InjectMocks
    private JumpFiberExport jumpFiberExport;
    @Mock
    private JumpFiberInfoDao jumpFiberInfoDao;

    @Mock
    private TemplateService templateService;

    /**
     * 远程调用设施服务
     */
    @Mock
    private DeviceFeign deviceFeign;

    private QueryCondition<QueryJumpFiberInfoReq> queryCondition = new QueryCondition<>();
    private QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
    private List<JumpFiberInfoResp> jumpFiberInfoRespList = new ArrayList<>();

    @Before
    public void setUp() {
        queryJumpFiberInfoReq.setDeviceId("sfdgffhgfdgfsfdsf");
        queryJumpFiberInfoReq.setBoxSide(0);
        queryJumpFiberInfoReq.setFrameNo("1");
        queryJumpFiberInfoReq.setDiscSide(0);
        queryJumpFiberInfoReq.setDiscNo("1");
        queryJumpFiberInfoReq.setPortNo("1");
        queryJumpFiberInfoReq.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        queryCondition.setBizCondition(queryJumpFiberInfoReq);
        JumpFiberInfoResp jumpFiberInfoResp = new JumpFiberInfoResp();
        jumpFiberInfoResp.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp.setBoxSide(0);
        jumpFiberInfoResp.setFrameNo("1");
        jumpFiberInfoResp.setDiscSide(0);
        jumpFiberInfoResp.setDiscNo("1");
        jumpFiberInfoResp.setPortNo("1");

        jumpFiberInfoResp.setOppositeDeviceId("sfdgffhgfdgfsfdsf");

        jumpFiberInfoRespList.add(jumpFiberInfoResp);
    }

    @Test
    public void assemblyJumpFiberInfoResp(){
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("sfdgffhgfdgfsfdsf");
        deviceInfoDtoList.add(deviceInfoDto);

        Set<String> deviceIds = new HashSet<>();
        String[] deviceArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceArray);
        when(deviceFeign.getDeviceByIds(any())).thenReturn(deviceInfoDtoList);

        List<JumpFiberInfoResp> jumpFiberInfoRespList = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp = new JumpFiberInfoResp();
        jumpFiberInfoResp.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp.setBoxSide(0);
        jumpFiberInfoResp.setFrameNo("1");
        jumpFiberInfoResp.setDiscSide(0);
        jumpFiberInfoResp.setDiscNo("1");
        jumpFiberInfoResp.setPortNo("1");

        jumpFiberInfoResp.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespList.add(jumpFiberInfoResp);

        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq.setDeviceId("sfdgffhgfdgfsfdsf");
        queryJumpFiberInfoReq.setBoxSide(0);
        queryJumpFiberInfoReq.setFrameNo("1");
        queryJumpFiberInfoReq.setDiscSide(0);
        queryJumpFiberInfoReq.setDiscNo("1");
        queryJumpFiberInfoReq.setPortNo("1");
        queryJumpFiberInfoReq.setOppositeDeviceId("sfdgffhgfdgfsfdsf");

        //组装本端及对端数据
        jumpFiberInfoRespList = JumpFiberInfoResp.assemblyJumpFiberInfoThisAndOpposite(jumpFiberInfoRespList,queryJumpFiberInfoReq);

        when(templateService.batchQueryPortInfo(any())).thenReturn(jumpFiberInfoRespList);

        when(templateService.queryPortIdByPortInfo(any())).thenReturn("fdsafdsfdsfdsaf");

        when(templateService.queryPortNumByPortId(any())).thenReturn("1A-1-1A");

        List<JumpFiberInfoResp> jumpFiberInfoRespList1 = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp1 = new JumpFiberInfoResp();
        jumpFiberInfoResp1.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp1.setBoxSide(0);
        jumpFiberInfoResp1.setFrameNo("1");
        jumpFiberInfoResp1.setDiscSide(0);
        jumpFiberInfoResp1.setDiscNo("1");
        jumpFiberInfoResp1.setPortNo("1");

        jumpFiberInfoResp1.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespList1.add(jumpFiberInfoResp1);

        QueryJumpFiberInfoReq queryJumpFiberInfoReq1 = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq1.setDeviceId("sfdgffhgfdgfsfdsf");
        queryJumpFiberInfoReq1.setBoxSide(0);
        queryJumpFiberInfoReq1.setFrameNo("1");
        queryJumpFiberInfoReq1.setDiscSide(0);
        queryJumpFiberInfoReq1.setDiscNo("1");
        queryJumpFiberInfoReq1.setPortNo("1");

        queryJumpFiberInfoReq1.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        List<JumpFiberInfoResp> jumpFiberInfoRespList3 = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp3 = new JumpFiberInfoResp();
        jumpFiberInfoResp3.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp3.setBoxSide(0);
        jumpFiberInfoResp3.setFrameNo("1");
        jumpFiberInfoResp3.setDiscSide(0);
        jumpFiberInfoResp3.setDiscNo("1");
        jumpFiberInfoResp3.setPortNo("1");

        jumpFiberInfoResp3.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespList3.add(jumpFiberInfoResp3);

        jumpFiberExport.assemblyJumpFiberInfoResp(jumpFiberInfoRespList1,queryJumpFiberInfoReq1);
    }

    @Test
    public void queryData() {
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(any())).thenReturn(jumpFiberInfoRespList);
//        when(jumpFiberExport.assemblyJumpFiberInfoResp(jumpFiberInfoRespList, queryJumpFiberInfoReq)).thenReturn(jumpFiberInfoRespList);
        assemblyJumpFiberInfoResp();
        Assert.assertNotEquals(jumpFiberInfoRespList, jumpFiberExport.queryData(queryCondition));
    }

    @Test
    public void queryCount() {
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq)).thenReturn(jumpFiberInfoRespList);
//        when(jumpFiberExport.assemblyJumpFiberInfoResp(jumpFiberInfoRespList, queryJumpFiberInfoReq)).thenReturn(jumpFiberInfoRespList);
        assemblyJumpFiberInfoResp();
        Assert.assertTrue(1 == jumpFiberExport.queryCount(queryCondition));
    }
}