package com.fiberhome.filink.rfid.dao.template;

import com.fiberhome.filink.rfid.bean.facility.BaseInfoBean;
import com.fiberhome.filink.rfid.bean.facility.PortInfoBean;
import com.fiberhome.filink.rfid.bean.template.PortCableCoreCondition;
import com.fiberhome.filink.rfid.bean.template.RealPosition;
import com.fiberhome.filink.rfid.bean.template.TemplateVO;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.template.PortCableReqDto;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.req.template.RealPortReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.template.RealPositionRspDto;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liyj
 * @date 2019/5/28
 */
public interface TemplateDao {
    /**
     * @param name
     * @return
     */
    Integer isExitsTemplateName(@Param("name") String name, @Param("templateType") Integer templateType);

    /**
     * 批量保存模版信息
     *
     * @param templateVOList 批量
     */
    void batchSaveTemplate(List<TemplateVO> templateVOList);

    /**
     * 保存模版
     *
     * @param templateVO
     */
    void saveTemplate(TemplateReqDto templateVO);

    /**
     * 保存框模板关系
     *
     * @param framePosition
     */
    void saveRealPositionFrame(List<RealPosition> framePosition);

    /**
     * 保存App 框信息
     *
     * @param addFrameList 新增的盘信息
     */
    void saveAppRealPositionFrame(List<RealRspDto> addFrameList);

    /**
     * 保存App 盘信息
     *
     * @param addDiscList 新增盘的信息
     */
    void saveAppRealPositionDisc(List<RealRspDto> addDiscList);

    /**
     * 保存箱坐标位置
     *
     * @param boxPosition
     */
    void saveRealPositionBox(List<RealPosition> boxPosition);

    /**
     * 保存盘坐标位置
     *
     * @param discPosition 盘位置坐标
     */
    void saveRealPositionDisc(List<RealPosition> discPosition);

    /**
     * 保存端口的坐标位置
     *
     * @param portPosition 端口的坐标位置
     */
    void saveRealPositionPort(List<RealPosition> portPosition);

    /**
     * 查询框模板
     *
     * @param templateVO 模板类型
     * @return List<TemplateVo> 模板信息
     */
    List<TemplateRspDto> queryFrameTemplate(TemplateVO templateVO);

    /**
     * 查询箱框盘 整套模板
     *
     * @param id 模板id
     * @return List<TemplateRspDTO>
     */
    List<TemplateRspDto> queryBoxTemplateById(String id);

    /**
     * 查询箱模板
     *
     * @param templateVO 模板类型
     * @return @link List<Template>
     */
    List<TemplateRspDto> queryBoxTemplate(TemplateVO templateVO);

    /**
     * 查询全部模版
     *
     * @param templateVO 模版实体
     * @return List<TemplateVO>
     */
    List<TemplateRspDto> queryBoardTemplate(TemplateVO templateVO);

    /**
     * 查询全部的子集模板
     *
     * @param childerList childerList
     * @return 全部的子集数据
     */
    List<TemplateRspDto> queryChildTemplate(List<String> childerList);

    /**
     * 保存模版和设施的关系
     *
     * @param templateReqDtoList 关系表
     */
    void saveRelationTemplate(List<TemplateReqDto> templateReqDtoList);

    /**
     * 查询box实景图坐标
     *
     * @param realReqDto 实景图req
     * @return 全部的坐标
     */
    List<RealRspDto> queryBoxRealPosition(RealReqDto realReqDto);

    /**
     * 查询box实景图坐标
     *
     * @param realReqDto 实景图req
     * @return 全部的坐标
     */
    List<RealPositionRspDto> queryBoxReal(RealReqDto realReqDto);

    /**
     * 查询框坐标
     *
     * @param parentId
     * @return
     */
    List<RealPositionRspDto> queryFrameRealP(List<String> parentId);

    /**
     * 查询盘坐标
     *
     * @param parentId
     * @return
     */
    List<RealPositionRspDto> queryDiscRealP(List<String> parentId);

    /**
     * 查询端口坐标
     *
     * @param parentId
     * @retu
     */
    List<RealPositionRspDto> queryPortRealP(List<String> parentId);

    /**
     * 查询框的坐标
     *
     * @param parentId 父id 集合
     * @return List<RealRspDto>
     */
    List<RealRspDto> queryFrameReal(List<String> parentId);

    /**
     * 查询盘的坐标
     *
     * @param parentId 父id 集合
     * @return List<RealRspDto>
     */
    List<RealRspDto> queryDiscReal(List<String> parentId);

    /**
     * 查询端口的坐标
     *
     * @param parentId 父id 集合
     * @return List<RealRspDto>
     */
    List<RealRspDto> queryPortReal(List<String> parentId);

    /**
     * 查询框的坐标 根据 id
     *
     * @param frameId 框id
     * @return RealReqDto
     */
    RealRspDto queryFrameRealPositionById(String frameId);

    /**
     * 根据设施id获取设施rfid标签id
     * 通过端口id
     *
     * @param deviceIdList 设施id数组
     * @return List<Map < String, String>>
     */
    List<BaseInfoBean> getDeviceRfidById(@Param("deviceIdList") List<String> deviceIdList);

    /**
     * 查询设施信息
     *
     * @param deviceId 设施id
     * @return TemplateReqDto
     */
    List<TemplateReqDto> queryFacilityInfoByCondition(String deviceId);

    /**
     * 获取端口所属信息
     *
     * @param portId 主键id
     * @return RealPosition
     */
    PortCableCoreCondition getPortRealById(String portId);

    /**
     * 通过端口id 查询端口号 sql 拼接
     *
     * @param portId 端口id
     * @return 端口号
     */
    String queryPortNumByPortId(String portId);

    /**
     * 获取对端信息
     *
     * @param jumpFiberInfoResp
     * @return PortInfoBean portBean
     */
    PortInfoBean batchQueryPortInfo(JumpFiberInfoResp jumpFiberInfoResp);

    /**
     * 修改关系表中的模板id  根据设施id 更改
     *
     * @param templateReqDto
     */
    void updateRelation(TemplateReqDto templateReqDto);

    /**
     * 获取箱模板的坐标 通过deviceId
     *
     * @param deviceId deviceId
     * @return RealRspDto
     */
    List<RealRspDto> queryRealPositionByDeviceId(String deviceId);

    /**
     * 查询框的坐标信息 通过deviceId 和类型
     *
     * @param deviceId topRsp
     * @return 坐标图
     */
    List<RealRspDto> queryFrameRealPosition(String deviceId);

    /**
     * 查询盘的坐标信息 通过deviceId 和类型
     *
     * @param deviceId topRsp
     * @return 坐标图
     */
    List<RealRspDto> queryDiscRealPosition(String deviceId);

    /**
     * 修改框的信息
     *
     * @param frame 框的信息
     */
    void updateAppFrameInfo(List<RealPosition> frame);

    /**
     * 修改盘的信息
     *
     * @param disc 盘的信息
     */
    void updateAppDiscInfo(List<RealPosition> disc);

    /**
     * 修改端口的信息
     *
     * @param port 端口的信息
     */
    void updateAppPortInfo(List<RealPosition> port);

    /**
     * 实景图 修改端口状态
     *
     * @param reqDto 请求数据
     */
    void updatePortState(RealPortReqDto reqDto);

    /**
     * 实景图修改盘的状态
     *
     * @param reqDto
     */
    void updateDiscState(RealPortReqDto reqDto);

    /**
     * 修改端口绑定的业务信息
     *
     * @param reqDto
     */
    void updateBusBindingPortState(RealPortReqDto reqDto);

    /**
     * 修改盘绑定的业务状态
     *
     * @param reqDto
     */
    void updateBusBindingDiscState(RealPortReqDto reqDto);

    /**
     * 查询同一个盘下的所有端口信息
     *
     * @param portId 端口id
     * @return 同一个盘下的所有盘信息
     */
    List<RealRspDto> querySameDiscPort(String portId);

    /**
     * 根据端口信息查询端口id
     *
     * @param portInfoReqDto 端口信息请求
     * @return 端口id
     */
    String queryPortIdByPortInfo(PortInfoReqDto portInfoReqDto);

    /**
     * 查询箱模板是否被占用
     *
     * @param boxTemplateId 箱模板id
     * @return 总数
     */
    Integer queryTemplateExistsById(List<String> boxTemplateId);

    /**
     * 查询设施id  是否已经建立联系关系
     *
     * @param deviceId 设施id
     * @return 是否存在
     */
    Integer queryDeviceExistsById(String deviceId);

    /**
     * 修改模板信息
     *
     * @param templateReqDtos 模板信息
     */
    void updateTemplate(TemplateReqDto templateReqDtos);

    /**
     * 删除模板
     *
     * @param templateId 模板id
     */
    void deleteTemplateById(String templateId);

    /**
     * 通过模板类型来查询模板数据
     *
     * @param type 模板类型
     * @return 模板数据
     */
    List<TemplateRspDto> queryTemplateByType(Integer type);

    /**
     * 批量修改成端端口信息
     *
     * @param portCableReqDtos
     */
    void batchUpdatePortCableState(List<PortCableReqDto> portCableReqDtos);

    /**
     * 删除设施实体
     *
     * @param deviceId
     */
    void deleteDeviceEntity(String deviceId);

    /**
     * 删除 端口
     *
     * @param list
     */
    void deletePort(List<String> list);


}
