package com.fiberhome.filink.picture.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 批量上传图片请求
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-19
 */
@Data
public class BatchUploadPicReq {

    /**
     * 图片列表
     */
    private List<MultipartFile> pics;

    /**
     * 关联信息列表
     */
    private String batchUploadPics;
}
