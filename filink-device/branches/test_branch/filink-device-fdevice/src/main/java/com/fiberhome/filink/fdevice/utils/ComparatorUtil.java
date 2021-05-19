package com.fiberhome.filink.fdevice.utils;


import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;

import java.util.Comparator;

/**
 * <p>
 * area比较器
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class ComparatorUtil implements Comparator<AreaInfoTree> {
    @Override
    public int compare(AreaInfoTree o1, AreaInfoTree o2) {
        int level1=o1.getLevel();
        int level2=o2.getLevel();
        if(level1>level2){
            return 1;
        }else if(level1<level2){
            return -1;
        }
        return 0;
    }
}

