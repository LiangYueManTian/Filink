//package com.fiberhome.filink.system_server.service.impl;
//
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.system_server.FilinkSystemServerApplicationTests;
//import com.fiberhome.filink.system_server.dao.TestDao;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//
//
//
///**
// * desc
// *
// * @author yuanyao@wistronits.com
// * create on 2019/1/11 14:41
// */
//public class TestServiceImplTest extends FilinkSystemServerApplicationTests {
//
//    @Mock
//    TestDao testDao;
//
//    @InjectMocks
//    private TestServiceImpl testService;
//
//
//    @Before
//    public void setUp() {
//
//    }
//
//    @Test
//    public void testDb() throws Exception {
//        Result result = testService.testDb(null);
//        System.out.println(result);
//    }
//
//    @Test
//    public void testTest() {
//        com.fiberhome.filink.system_server.bean.Test test = new com.fiberhome.filink.system_server.bean.Test();
//        test.setName("test");
//        Mockito.when(testDao.selectById("123")).thenReturn(test);
//        String s = testService.testTest();
//        Assert.assertEquals(s,"test");
//    }
//
//}