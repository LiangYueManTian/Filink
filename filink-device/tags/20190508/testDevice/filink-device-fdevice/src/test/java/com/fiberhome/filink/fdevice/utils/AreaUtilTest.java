package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * AreaUtilTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/8
 */
@RunWith(MockitoJUnitRunner.class)
public class AreaUtilTest {

    @InjectMocks
    private AreaUtil areaUtil;

    @Test
    public void permissionsFilter() {
        List<AreaInfoForeignDto> areaInfoForeignDtoList = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaInfoForeignDto.setAreaId("wuhan");
        areaInfoForeignDto.setAreaName("wuhan");
        areaInfoForeignDtoList.add(areaInfoForeignDto);
        areaUtil.permissionsFilter(areaInfoForeignDtoList, permissions);

    }

    @Test
    public void areaTreePermissionsFilter() {
        List<AreaInfo> areaInfoList = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaId("wuhan");
        areaInfo.setAreaName("wuhan");
        areaInfoList.add(areaInfo);
        permissions.add("wuhan");
        areaUtil.areaTreePermissionsFilter(areaInfoList, permissions);
    }

    @Test
    public void areaForeignTreePermissionsFilter() {
    }
}