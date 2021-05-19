package com.fiberhome.filink.rfid.utils;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author liyj
 * @date 2019/7/29
 */
@RunWith(JMockit.class)
public class OperateUtilTest {
    @Tested
    private OperateUtil operateUtil;

    @Test
    public void getOperateValue() throws Exception {
        String operateValue = operateUtil.getOperateValue(null);
        Assert.assertTrue("".equals(operateValue));

        String operateValue1 = operateUtil.getOperateValue("gt");
        Assert.assertTrue(">".equals(operateValue1));

        String operateValue2 = operateUtil.getOperateValue("gte");
        Assert.assertTrue(">=".equals(operateValue2));

        String operateValue3 = operateUtil.getOperateValue("lt");
        Assert.assertTrue("<".equals(operateValue3));

        String operateValue4 = operateUtil.getOperateValue("lte");
        Assert.assertTrue("<=".equals(operateValue4));

        String operateValue5 = operateUtil.getOperateValue("eq");
        Assert.assertTrue("=".equals(operateValue5));

        String operateValue6 = operateUtil.getOperateValue("eq1");
        Assert.assertTrue("=".equals(operateValue6));


    }

}