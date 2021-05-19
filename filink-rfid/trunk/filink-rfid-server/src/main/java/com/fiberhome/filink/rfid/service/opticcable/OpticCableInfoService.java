package com.fiberhome.filink.rfid.service.opticcable;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableInfo;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.rfid.req.opticcable.InsertOpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.UpdateOpticCableInfoReq;

/**
 * <p>
 * 光缆信息表 服务类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public interface OpticCableInfoService extends IService<OpticCableInfo> {

    /**
     * 分页查询光缆列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result opticCableListByPage(QueryCondition<OpticCableInfoReq> queryCondition);

    /**
     * 新增光缆
     *
     * @param insertOpticCableInfoReq 新增光缆请求
     * @return Result
     */
    Result addOpticCable(InsertOpticCableInfoReq insertOpticCableInfoReq);

    /**
     * 根据id获取光缆信息
     *
     * @param id 光缆id
     * @return Result
     */
    Result queryOpticCableById(String id);

    /**
     * 修改光缆
     *
     * @param updateOpticCableInfoReq 修改光缆请求
     * @return Result
     */
    Result updateOpticCableById(UpdateOpticCableInfoReq updateOpticCableInfoReq);

    /**
     * 删除光缆
     *
     * @param id 删除光缆id
     *
     * @return Result
     */
    Result deleteOpticCableById(String id);

    /**
     * 校验光缆名字
     *
     * @param id 光缆id
     * @param name 光缆名字
     *
     * @return Result
     */
    Boolean checkOpticCableName(String id,String name);

    /**
     * app请求所有光缆的信息
     *
     * @return Result
     */
    Result getOpticCableListForApp();

    /**
     * 光缆列表导出
     *
     * @param exportDto 光缆列表导出请求
     *
     * @return Result
     */
    Result exportOpticCableList(ExportDto exportDto);
}
