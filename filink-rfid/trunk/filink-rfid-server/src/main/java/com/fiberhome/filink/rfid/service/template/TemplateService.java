package com.fiberhome.filink.rfid.service.template;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.template.PortCableReqDto;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.req.template.RealPortReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;

import java.util.List;

/**
 * @author yjli
 * @date 2019/06/01
 */
public interface TemplateService {
    /**
     * 新增模版
     *
     * @param templateVO templateVo
     * @return Result
     */
    Result createTemplate(TemplateReqDto templateVO);

    /**
     * 查询全部模板 根据模板类型
     *
     * @param templateVO 模板
     * @return Result
     */
    Result queryAllTemplate(TemplateReqDto templateVO);

    /**
     * 查询实景图
     *
     * @param realReqDto 实景图Req
     * @return Result
     */
    Result queryRealPosition(RealReqDto realReqDto);

    /**
     * 查询实景图根据框 id
     *
     * @param frameId 框id
     * @param isFlag  是否需要放大
     * @return Result
     */
    Result queryRealPositionByFrameId(String frameId, boolean isFlag);

    /**
     * 查询箱框的信息
     *
     * @param deviceId 设施id
     * @return Result
     */
    Result queryFormationByDeviceId(String deviceId);

    /**
     * 保存模板和设施的关系
     *
     * @param reqDto 关系
     * @return Result
     */
    Result saveDeviceAndTempRelation(TemplateReqDto reqDto);

    /**
     * 查询端口信息 通过端口id
     *
     * @param portId 端口id
     * @return Result
     */
    Result queryPortInfoByPortId(String portId);

    /**
     * 批量获取端口信息
     *
     * @param list list
     * @return List<JumpFiberInfoResp>
     */
    List<JumpFiberInfoResp> batchQueryPortInfo(List<JumpFiberInfoResp> list);

    /**
     * 通过设施id 和 类型获取值
     *
     * @param deviceId 设施id
     * @return Result
     */
    Result queryTemplateTop(String deviceId);

    /**
     * 修改端口状态
     *
     * @param reqDto 请求id
     * @return Result
     */
    Result updatePortState(RealPortReqDto reqDto);

    /**
     * 通过deviceId 去获取
     *
     * @param deviceId deviceId
     * @return Boolean
     */
    Boolean getRfIdDataAuthInfo(String deviceId);

    /**
     * 修改端口绑定的业务状态信息
     *
     * @param portId    端口id
     * @param state     修改后的状态 新增/删除 0/1
     * @param busType   属于那种业务 BusTypeEnum (0/1/2)成端、跳接、端口标签
     * @param portState 属于那种业务 端口状态 用于端口标签
     */
    void updatePortBindingState(String portId, Integer state, Integer busType, Integer portState);

    /**
     * 根据端口信息查询端口id
     *
     * @param portInfoReqDto 端口信息请求
     * @return 端口id
     */
    String queryPortIdByPortInfo(PortInfoReqDto portInfoReqDto);

    /**
     * 修改模板
     *
     * @param templateReqDtoList
     * @return
     */
    Result updateTemplate(TemplateReqDto templateReqDtoList);

    /**
     * 删除模板信息
     *
     * @param templateReqDto 模板信息
     * @return 是否修改或者删除成功
     */
    Result deleteTemplate(TemplateReqDto templateReqDto);

    /**
     * 批量修改 成端 端口状态
     *
     * @param portCableReqDtos
     */
    void batchUpdatePortCableState(List<PortCableReqDto> portCableReqDtos);

    /**
     * App 新增标签后 失败回滚
     *
     * @param deviceId 设施id
     */
    void deleteDeviceEntity(String deviceId);

    /**
     * 查询端口号
     *
     * @param portId
     * @return
     */
    String queryPortNumByPortId(String portId);
}
