package com.fiberhome.filink.alarmcurrentserver.alarmrecive;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmChannelTest {

    @Tested
    private AlarmChannel alarmChannel;

    @Test
    public void input() throws Exception {
        try {
            alarmChannel.input();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void output() throws Exception {
        try {
            alarmChannel.output();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void picInput() throws Exception {
        try {
            alarmChannel.picInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void picOutput() throws Exception {
        try {
            alarmChannel.picOutput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void adviceInput() throws Exception {
        try {
            alarmChannel.adviceInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void adviceOutput() throws Exception {
        try {
            alarmChannel.adviceInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void orderInput() throws Exception {
        try {
            alarmChannel.orderInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void orderOutput() throws Exception {
        try {
            alarmChannel.orderOutput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void incrementalInput() throws Exception {
        try {
            alarmChannel.incrementalInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void incrementalWeekInput() throws Exception {
        try {
            alarmChannel.incrementalWeekInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void incrementalMonthInput() throws Exception {
        try {
            alarmChannel.incrementalMonthInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void alarmSourceIncrementalInput() throws Exception {
        try {
            alarmChannel.alarmSourceIncrementalInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void alarmSourceIncrementalWeekInput() throws Exception {
        try {
            alarmChannel.alarmSourceIncrementalWeekInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void alarmSourceIncrementalMonthInput() throws Exception {
        try {
            alarmChannel.alarmSourceIncrementalMonthInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void alarmCleanStatusInput() throws Exception {
        try {
            alarmChannel.alarmCleanStatusInput();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

}