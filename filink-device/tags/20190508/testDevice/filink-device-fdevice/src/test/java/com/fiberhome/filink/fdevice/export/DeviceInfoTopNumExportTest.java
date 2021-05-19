package com.fiberhome.filink.fdevice.export;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.dto.DeviceInfoForTopNum;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhaoliang
 * @Date: 2019/7/2 16:04
 * @Description: com.fiberhome.filink.fdevice.export
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoTopNumExportTest {
    @InjectMocks
    private DeviceInfoTopNumExport deviceInfoTopNumExport;

    @Test
    public void queryData() {
        List<Object> objectList = new ArrayList<>();
        DeviceInfoForTopNum deviceInfoForTopNum = new DeviceInfoForTopNum();
        deviceInfoForTopNum.setRanking(1);
        objectList.add(JSON.toJSON(deviceInfoForTopNum));
        QueryCondition condition = new QueryCondition();
        new Expectations(ExportApiUtils.class) {
            {
                ExportApiUtils.getObjList(condition);
                result = objectList;
            }
        };
        List list = deviceInfoTopNumExport.queryData(condition);
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void queryCount() {
        new Expectations(ExportApiUtils.class) {
            {
                ExportApiUtils.getObjectList();
                result = new ArrayList<>();
            }
        };
        Integer integer = deviceInfoTopNumExport.queryCount(new QueryCondition());
        Assert.assertTrue(integer == 0);

    }
}
