package com.fiberhome.filink.lockserver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class LockTestController {

    public static AtomicInteger count = new AtomicInteger(0);

    @GetMapping("/count")
    public String queryCount() {
        int i = count.get();
        String str = "当前服务停留请求数" + i;
        return str;
    }

}
