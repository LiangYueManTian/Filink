package com.fiberhome.filink.rfid.export.jumpfiber;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 跳接列表导出类
 *
 * @author chaofanrong@wistronits.com
 */
@Component
@Slf4j
public class JumpFiberExport extends AbstractExport {

    @Autowired
    private JumpFiberInfoDao jumpFiberInfoDao;

    /**
     * 注入模板接口
     */
    @Autowired
    TemplateService templateService;

    /**
     * 远程调用设施服务
     */
    @Autowired
    private DeviceFeign deviceFeign;

    @Override
    protected List queryData(QueryCondition queryCondition) {
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = (QueryJumpFiberInfoReq)queryCondition.getBizCondition();
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);
        jumpFiberInfoRespList = this.assemblyJumpFiberInfoResp(jumpFiberInfoRespList,queryJumpFiberInfoReq);
        return jumpFiberInfoRespList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = (QueryJumpFiberInfoReq)queryCondition.getBizCondition();
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);
        jumpFiberInfoRespList = this.assemblyJumpFiberInfoResp(jumpFiberInfoRespList,queryJumpFiberInfoReq);
        Integer count = jumpFiberInfoRespList.size();
        return count;
    }

    /*-------------------------------------组装跳接信息公共方法start-------------------------------------*/
    /**
     * 组装跳接信息
     *
     * @param jumpFiberInfoRespList 跳接信息列表
     * @param queryJumpFiberInfoReq 跳接信息请求
     *
     * @return jumpFiberInfoRespList 跳接信息列表
     */
    @SuppressWarnings("all")
    public List<JumpFiberInfoResp> assemblyJumpFiberInfoResp(List<JumpFiberInfoResp> jumpFiberInfoRespList,QueryJumpFiberInfoReq queryJumpFiberInfoReq){
        List<JumpFiberInfoResp> jumpFiberInfoRespListResult = new ArrayList<>();
        //远程获取设施信息
        Set<String> deviceIds = new HashSet<>();
        for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespList){
            deviceIds.add(jumpFiberInfoResp.getDeviceId());
            deviceIds.add(jumpFiberInfoResp.getOppositeDeviceId());
        }
        String[] deviceArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceArray);
        Map<String, DeviceInfoDto> deviceInfoMap = new HashMap<>(64);
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);

        //组装本端及对端数据
        jumpFiberInfoRespListResult = JumpFiberInfoResp.assemblyJumpFiberInfoThisAndOpposite(jumpFiberInfoRespList,queryJumpFiberInfoReq);

        //获取设施信息
        if (!ObjectUtils.isEmpty(deviceInfoDtoList)){
            log.info("导出对端端口获取设施信息成功");
            for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList){
                deviceInfoMap.put(deviceInfoDto.getDeviceId(),deviceInfoDto);
            }

            for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespListResult){
                jumpFiberInfoResp.setDeviceName(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceName());
                jumpFiberInfoResp.setDeviceType(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceType());
                jumpFiberInfoResp.setDeviceTypeName(JumpFiberInfoResp.getDeviceTypeName(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceType()));

                jumpFiberInfoResp.setOppositeDeviceName(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceName());
                jumpFiberInfoResp.setOppositeDeviceType(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceType());
                jumpFiberInfoResp.setOppositeDeviceTypeName(JumpFiberInfoResp.getDeviceTypeName(deviceInfoMap.get(jumpFiberInfoResp.getDeviceId()).getDeviceType()));
            }

            //获取对端模板标签信息
            List<JumpFiberInfoResp> jumpFiberInfoRespListTemp = templateService.batchQueryPortInfo(jumpFiberInfoRespListResult);
            if (!ObjectUtils.isEmpty(jumpFiberInfoRespListTemp)){
                log.info("导出对端端口获取模板信息成功");
                jumpFiberInfoRespListResult = jumpFiberInfoRespListTemp;
            }

            //获取对端端口号
            for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespListResult){
                //获取对端端口id
                PortInfoReqDto oppositePortInfoReqDto = new PortInfoReqDto();
                oppositePortInfoReqDto.setDeviceId(jumpFiberInfoResp.getOppositeDeviceId());
                oppositePortInfoReqDto.setBoxSide(jumpFiberInfoResp.getOppositeBoxSide());
                oppositePortInfoReqDto.setFrameNo(jumpFiberInfoResp.getOppositeFrameNo());
                oppositePortInfoReqDto.setDiscSide(jumpFiberInfoResp.getOppositeDiscSide());
                oppositePortInfoReqDto.setDiscNo(jumpFiberInfoResp.getOppositeDiscNo());
                oppositePortInfoReqDto.setPortNo(jumpFiberInfoResp.getOppositePortNo());
                String oppositePortId = templateService.queryPortIdByPortInfo(oppositePortInfoReqDto);
                jumpFiberInfoResp.setOppositePortNo(templateService.queryPortNumByPortId(oppositePortId));
            }
        }
        return jumpFiberInfoRespListResult;
    }
    /*-------------------------------------组装跳接信息公共方法end-------------------------------------*/

}
