package com.fiberhome.filink.userserver.service;

import com.fiberhome.filink.bean.Result;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 数据导入服务
 *
 * @author xgong
 */
public interface ImportService {


    /**
     * * 导入用户数据
     *
     * @param file 传入的文件
     * @return 导入的结果
     * @throws InvalidFormatException，InvalidFormatException
     */
    Result importUserInfo(@RequestParam MultipartFile file)
            throws IOException, InvalidFormatException;
}
