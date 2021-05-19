package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;

import java.util.Comparator;
import java.util.Date;

/**
 * <p>
 * area根据时间比较器
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class AreaCreateTimeComparator implements Comparator<AreaInfoForeignDto> {
    @Override
    public int compare(AreaInfoForeignDto o1, AreaInfoForeignDto o2) {
        Date date1 = o1.getCreateTime();
        Date date2 = o2.getCreateTime();
        long lD1;
        long lD2;
        if (date1 == null) {
            lD1 = 0;
        } else {
            lD1 = date1.getTime();
        }
        if (date2 == null) {
            lD2 = 0;
        } else {
            lD2 = date2.getTime();
        }

        if (lD1 > lD2) {
            return -1;
        } else if (lD1 < lD2) {
            return 1;
        }
        return 0;
    }
}

