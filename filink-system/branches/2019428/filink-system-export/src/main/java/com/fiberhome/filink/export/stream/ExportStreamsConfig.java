package com.fiberhome.filink.export.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 绑定通道
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:52
 */
@EnableBinding(ExportStreams.class)
public class ExportStreamsConfig {
}
