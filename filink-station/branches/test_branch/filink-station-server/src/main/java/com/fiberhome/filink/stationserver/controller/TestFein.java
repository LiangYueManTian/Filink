package com.fiberhome.filink.stationserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class TestFein {

    @Autowired
    private ControlFeign controlFeign;

    public static AtomicInteger success = new AtomicInteger(0);

    public static AtomicInteger fail = new AtomicInteger(0);

    @Async
    public void testFeign(ControlParam controlParam){
        try {
            Result updateResult = controlFeign.updateControlParam(controlParam);
            success.incrementAndGet();
        } catch (Exception e) {
            log.info("error : {}",e);
            fail.incrementAndGet();
        }
    }
}
