package com.fiberhome.filink.parts.export;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.parts.dto.PartInfoDto;
import com.fiberhome.filink.parts.service.PartInfoService;
import com.fiberhome.filink.parts.service.impl.PartInfoServiceImpl;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/24 18:33
 * @Description: com.fiberhome.filink.parts.export
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PartInfoExportTest {
    @InjectMocks
    private PartInfoExport partInfoExport;

    @Mock
    QueryCondition queryCondition;

    @Mock
    private UserFeign userFeign;

    @Mock
    private PartInfoService partInfoService;

    @Test
    public void queryData() {
        new Expectations(ExportApiUtils.class) {
            {
                ExportApiUtils.getCurrentUserId();
                result = "userId";
            }
        };
        User user = mockUser();
        Object data = new ArrayList<PartInfoDto>();
        when(partInfoService.queryListByPage(queryCondition, user)).thenReturn(ResultUtils.success(data));
        List list = partInfoExport.queryData(queryCondition);
        Assert.assertEquals(list, data);

    }

    @Test
    public void queryCount() {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "userId";
            }
        };
        User user = mockUser();
        when(partInfoService.queryPartsCount(queryCondition, user)).thenReturn(5);
        Integer integer = partInfoExport.queryCount(queryCondition);
        Assert.assertEquals(integer, new Integer(5));
    }

    private User mockUser() {
        User user = new User();
        user.setId("id");
        new Expectations(PartInfoServiceImpl.class) {
            {
                PartInfoService.convertObjectToUser(any);
                result = user;
            }
        };
        when(userFeign.queryUserInfoById("userId")).thenReturn(new Result());
        return user;
    }
}
