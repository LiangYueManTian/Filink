package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * ComparatorUtilTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/8
 */
@RunWith(MockitoJUnitRunner.class)
public class ComparatorUtilTest {

    @InjectMocks
    private ComparatorUtil comparatorUtil;

    @Test
    public void compare() {
        AreaInfoTree areaInfoTree = new AreaInfoTree();
        areaInfoTree.setLevel(1);
        AreaInfoTree areaInfoTree1 = new AreaInfoTree();
        areaInfoTree1.setLevel(2);
        comparatorUtil.compare(areaInfoTree, areaInfoTree);
        comparatorUtil.compare(areaInfoTree, areaInfoTree1);
        comparatorUtil.compare(areaInfoTree1, areaInfoTree);
    }
}