package com.fiberhome.filink.picture.dao.relation;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.picture.req.DevicePicReq;
import com.fiberhome.filink.picture.resp.DevicePicResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 图片关系表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-13
 */
public interface PicRelationInfoDao extends BaseMapper<PicRelationInfo> {

    /**
     * 分页查询设施图片列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    List<DevicePicResp> imageListByPage(QueryCondition<DevicePicReq> queryCondition);

    /**
     * 查询设施图片列表总数
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    int imageCountListByPage(QueryCondition<DevicePicReq> queryCondition);

    /**
     * 批量更新图片列表isDeleted状态
     *
     * @param ids 图片ids
     * @param deviceIds 设施ids
     * @param isDeleted
     *
     * @return void
     */
    int updateImagesIsDeleteByIds(@Param("ids") Set<String> ids,@Param("deviceIds") Set<String> deviceIds,@Param("isDeleted") String isDeleted);

    /**
     * 批量保存图片列表
     *
     * @param picRelationInfos 图片列表
     * @return void
     */
    int saveImageInfos(@Param("picRelationInfos") List<PicRelationInfo> picRelationInfos);

    /**
     * 根据工单id获取图片url
     *
     * @param resource 来源
     * @param deviceId 设施id
     * @param resourceIds 来源ids
     *
     * @return List<DevicePicResp>
     */
    List<DevicePicResp> getPicUrlByResource(@Param("resource") String resource, @Param("deviceId") String deviceId,@Param("resourceIds") List<String> resourceIds);

    /**
     * 根据设施id或来源获取图片信息
     *
     * @param devicePicReq 图片请求
     *
     * @return List<DevicePicResp>
     */
    List<DevicePicResp> getPicInfoByDeviceIds(DevicePicReq devicePicReq);

    /**
     * 根据设施ids获取实景图
     *
     * @param deviceIds 设施ids
     * @param picNum 张数
     *
     * @return Result
     */
    List<DevicePicResp> getLivePicInfoByDeviceIds(@Param("deviceIds") Set<String> deviceIds,@Param("picNum") String picNum);
}
