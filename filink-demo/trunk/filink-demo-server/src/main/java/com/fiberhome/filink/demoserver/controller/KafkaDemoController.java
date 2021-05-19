package com.fiberhome.filink.demoserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.demoserver.stream.DemoStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例项目----kafka
 *
 * @author 姚远
 */
@RestController
@RequestMapping("/kafka")
public class KafkaDemoController {

    @Autowired
    private DemoStreams demoStreams;

    @GetMapping("/pro")
    public Result producer() {
        Message<String> this_is_a_message = MessageBuilder.withPayload("THIS IS A MESSAGE").build();
        demoStreams.demeOutput().send(this_is_a_message);
        return ResultUtils.success();
    }



}
