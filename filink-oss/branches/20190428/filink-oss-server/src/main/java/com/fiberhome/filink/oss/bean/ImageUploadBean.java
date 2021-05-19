package com.fiberhome.filink.oss.bean;

import com.fiberhome.filink.oss.constant.ImageExtensionEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * <p>
 *     图片上传实体类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/25
 */
@Data
public class ImageUploadBean {
    /**
     * 16进制文件内容
     */
    private String fileHexData;
    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 校验参数
     * @return true 参数错误 false参数校验正确
     */
    public boolean check() {
        return StringUtils.isEmpty(fileHexData) || StringUtils.isEmpty(fileExtension)
                || !ImageExtensionEnum.containExtension(fileExtension);
    }
}
