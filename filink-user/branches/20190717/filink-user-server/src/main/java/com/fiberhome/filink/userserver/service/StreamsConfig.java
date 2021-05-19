package com.fiberhome.filink.userserver.service;

import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author xuangong
 * 监听UserStream
 */
@EnableBinding(UserStream.class)
public class StreamsConfig {
}
