package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

/**
 * AreaCreateTimeComparatorTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/8
 */
@RunWith(MockitoJUnitRunner.class)
public class AreaCreateTimeComparatorTest {
    @InjectMocks
    private AreaCreateTimeComparator areaCreateTimeComparator;

    @Test
    public void compare() {
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        Assert.assertEquals(0, areaCreateTimeComparator.compare(areaInfoForeignDto, areaInfoForeignDto));
        areaInfoForeignDto.setCreateTime(new Date());
        Assert.assertEquals(0, areaCreateTimeComparator.compare(areaInfoForeignDto, areaInfoForeignDto));
    }
}