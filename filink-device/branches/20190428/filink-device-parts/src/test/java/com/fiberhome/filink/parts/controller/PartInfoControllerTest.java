package com.fiberhome.filink.parts.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.parts.bean.PartInfo;
import com.fiberhome.filink.parts.service.PartInfoService;
import com.fiberhome.filink.parts.constant.PartsResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import org.junit.After;
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
 * @Author: zl
 * @Date: 2019/3/13 16:49
 * @Description: com.fiberhome.filink.parts.controller
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PartInfoControllerTest {

    @InjectMocks
    private PartInfoController partInfoController;

    @Mock
    private PartInfoService partInfoService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkDeviceName() {
        PartInfo partInfo = new PartInfo();
        partInfo.setPartId("1");
        partInfo.setPartName("2");

        PartInfo partInfo1 = null;

        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = "demo";
            }
        };

        when(partInfoService.checkPartsName("1", "2")).thenReturn(true);
        when(partInfoService.checkPartsName("2", "2")).thenReturn(false);

        Result result = partInfoController.checkDeviceName(partInfo);
        Result result1 = partInfoController.checkDeviceName(partInfo1);

        partInfo.setPartId("2");
        Result result2 = partInfoController.checkDeviceName(partInfo);
        Assert.assertTrue(result.getCode() == PartsResultCode.PARTS_NAME_SAME);
        Assert.assertTrue(result1.getCode() == PartsResultCode.PARTS_PARAM_ERROR);
        Assert.assertTrue(result2.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void addPart() {
        PartInfo partInfo = new PartInfo();
        when(partInfoService.addPart(partInfo)).thenReturn(ResultUtils.success());
        Result result = partInfoController.addPart(partInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateParts() {
        PartInfo partInfo = new PartInfo();
        when(partInfoService.updateParts(partInfo)).thenReturn(ResultUtils.success());
        Result result = partInfoController.updateParts(partInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deletePartsByIds() {
        String[] str = {"!"};
        when(partInfoService.deletePartsByIds(any())).thenReturn(new Result());
        Result result = partInfoController.deletePartsByIds(str);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void findPartsById() throws Exception {
        when(partInfoService.findPartsById(any())).thenReturn(new Result());
        Result result = partInfoController.findPartsById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void partListByPage() throws Exception {
        when(partInfoService.queryListByPage(any(),any())).thenReturn(new Result());
        Result result = partInfoController.partListByPage(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }
}
