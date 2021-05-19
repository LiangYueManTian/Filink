package com.fiberhome.filink.oss.config;


import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 导入FastDFS-Client组件
 *
 * @author chaofang@wistronits.com
 * @since 2019/1/22
 */
@Configuration
@Import(FdfsClientConfig.class)
public class FdfsConfiguration {
}
