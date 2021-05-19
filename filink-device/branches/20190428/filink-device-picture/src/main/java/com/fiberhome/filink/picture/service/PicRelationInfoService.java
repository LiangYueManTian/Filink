package com.fiberhome.filink.picture.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.fiberhome.filink.picture.req.BatchUploadPic;
import com.fiberhome.filink.picture.req.DevicePicReq;

import java.util.List;
import java.util.Set;


/**
 * <p>
 * 图片关系表 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-13
 */
public interface PicRelationInfoService extends IService<PicRelationInfo> {

    /**
     * 分页查询设施图片列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result imageListByPage(QueryCondition<DevicePicReq> queryCondition);

    /**
     * 批量下载图片列表
     *
     * @param exportDto 批量导出请求类
     * @return Result
     * @throws Exception
     */
    Result batchDownLoadImages(ExportDto exportDto) throws Exception;

    /**
     * 批量更新图片列表isDeleted字段
     *
     * @param picRelationInfos 图片ids
     * @param isDeleted
     * @return Result
     */
    Result updateImageIsDeletedByIds(List<PicRelationInfo> picRelationInfos, String isDeleted);

    /**
     * 批量上传图片（app）
     *
     * @param batchUploadPics 批量上传图片信息
     * @return Result
     * @throws Exception
     */
    Result uploadImages(List<BatchUploadPic> batchUploadPics) throws Exception;

    /**
     * 批量保存图片数据
     *
     * @param picRelationInfos 图片数据列表
     * @return Result
     */
    Result saveImagesData(List<PicRelationInfo> picRelationInfos);

    /**
     * 根据工单id获取图片url
     *
     * @param resource    来源
     * @param resourceId  来源id
     * @param deviceId    设施id
     * @param resourceIds 来源ids
     * @return Result
     */
    Result getPicUrlByResource(String resource, String resourceId, String deviceId, List<String> resourceIds);

    /**
     * 根据设施id或来源获取图片信息
     *
     * @param devicePicReq 图片请求
     * @return Result
     */
    Result getPicInfoByDeviceId(DevicePicReq devicePicReq);

    /**
     * 根据设施ids更新图片列表isDeleted字段
     *
     * @param deviceIds 设施id
     * @param isDeleted 逻辑删除标记
     * @return Result
     */
    Result updateImageIsDeletedByDeviceIds(Set<String> deviceIds, String isDeleted);

    /**
     * app根据设施ids获取实景图
     *
     * @param deviceIds 设施ids
     * @param picNum    张数
     * @return Result
     */
    Result getLivePicInfoByDeviceIdsForApp(Set<String> deviceIds, String picNum);

}
