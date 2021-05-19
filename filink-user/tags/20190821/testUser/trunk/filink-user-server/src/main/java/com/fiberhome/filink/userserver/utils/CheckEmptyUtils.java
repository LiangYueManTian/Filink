package com.fiberhome.filink.userserver.utils;

import java.util.Collection;

/**
 * @author xgong
 */
public class CheckEmptyUtils {

    public static boolean collectEmpty(Collection collection) {

        return collection != null && collection.size() > 0;
    }
}
