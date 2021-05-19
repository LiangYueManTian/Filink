package com.fiberhome.filink.rfid.dao.template;

import com.fiberhome.filink.rfid.bean.facility.BoardInfoBean;
import com.fiberhome.filink.rfid.bean.facility.BoxInfoBean;
import com.fiberhome.filink.rfid.bean.facility.FacilityQueryBean;
import com.fiberhome.filink.rfid.bean.facility.PortInfoBean;
import com.fiberhome.filink.rfid.bean.template.PortCableCoreCondition;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * App 端接口信息
 *
 * @author liyj
 * @date 2019/6/10
 */
public interface FacilityDao {


    /**
     * 批量插入箱架标签信息
     *
     * @param list 箱架信息
     */
    void batchSaveBoxLabelInfo(List<BoxInfoBean> list);

    /**
     * 修改框设施状态
     *
     * @param frames 框
     */
    void updateFrameDeviceState(List<RealRspDto> frames);

    /**
     * 修改盘设施状态
     *
     * @param discs 盘
     */
    void updateDiscDeviceState(List<RealRspDto> discs);

    /**
     * 批量插入盘标签信息
     *
     * @param list 盘信息
     */
    void batchSaveBoardLabelInfo(List<BoardInfoBean> list);

    /**
     * 批量插入端口标签信息
     *
     * @param list 端口信息
     */
    void batchSavePortLabelInfo(List<PortInfoBean> list);

    /**
     * 删除箱架标签信息
     *
     * @param list 箱架标签ID
     */
    void deleteBoxLabelByIds(List<String> list);

    /**
     * 删除盘标签信息
     *
     * @param list 盘标签ID
     */
    void deleteBoardLabelByIds(List<String> list);

    /**
     * 删除端口标签信息
     *
     * @param list 端口标签ID
     */
    void deletePortLabelByIds(List<String> list);

    /**
     * 更新箱架标签信息
     *
     * @param boxInfoBean 标签信息
     */
    void updateBoxLabel(BoxInfoBean boxInfoBean);

    /**
     * 更新盘标签信息
     *
     * @param boardInfoBean 标签信息
     */
    void updateBoardLabel(BoardInfoBean boardInfoBean);

    /**
     * 更新端口标签信息
     *
     * @param portInfoBean 标签信息
     */
    void updatePortLabel(PortInfoBean portInfoBean);

    /**
     * 根据设施ID查询箱架标签信息
     *
     * @param deviceId 设施ID
     * @return 箱架标签信息
     */
    List<BoxInfoBean> queryBoxLabelByDevId(String deviceId);

    /**
     * 根据框号和盘号查询盘标签信息
     *
     * @param queryBean 查询条件
     * @return 盘标签信息
     */
    List<BoardInfoBean> queryBoardLabelByFraNoAndBoaNo(FacilityQueryBean queryBean);

    /**
     * 根据框号、盘号、端口号查询端口标签信息
     *
     * @param queryBean 查询条件
     * @return 端口标签信息
     */
    List<PortInfoBean> queryPortLabelByNo(FacilityQueryBean queryBean);

    /**
     * 根据标签查询箱架信息
     *
     * @param label 标签
     * @return 箱架信息
     */
    List<BoxInfoBean> queryBoxLabelById(String label);

    /**
     * 根据标签查询盘信息
     *
     * @param label 标签
     * @return 盘信息
     */
    List<BoardInfoBean> queryBoardLabelById(String label);

    /**
     * 根据标签查询端口信息
     *
     * @param label 标签
     * @return 端口信息
     */
    List<PortInfoBean> queryPortLabelById(String label);

    /**
     * 查询端口信息
     *
     * @param realPosition 实景坐标
     * @return 端口信息
     */
    PortInfoBean queryPortLabelInfo(PortCableCoreCondition realPosition);

    /**
     * 变更箱架标签ID
     *
     * @param oldLabel 旧标签
     * @param newLabel 新标签
     */
    void replaceBoxLabel(@Param("oldLabel") String oldLabel, @Param("newLabel") String newLabel);

    /**
     * 变更盘标签ID
     *
     * @param oldLabel 旧标签
     * @param newLabel 新标签
     */
    void replaceBoardLabel(@Param("oldLabel") String oldLabel, @Param("newLabel") String newLabel);

    /**
     * 变更端口标签ID
     *
     * @param oldLabel 旧标签
     * @param newLabel 新标签
     */
    void replacePortLabel(@Param("oldLabel") String oldLabel, @Param("newLabel") String newLabel);

    /**
     * 变更盘所在箱架标签ID
     *
     * @param oldLabel 旧标签
     * @param newLabel 新标签
     */
    void replaceBoardBoxLabel(@Param("oldLabel") String oldLabel, @Param("newLabel") String newLabel);

    /**
     * 变更端口所在箱架标签ID
     *
     * @param oldLabel 旧标签
     * @param newLabel 新标签
     */
    void replacePortBoxLabel(@Param("oldLabel") String oldLabel, @Param("newLabel") String newLabel);

    /**
     * 根据id查询标签信息
     *
     * @param list 标签id
     */
    int queryBoxLabelNumByIds(List<String> list);

    /**
     * 根据id查询标签信息
     *
     * @param list 标签id
     */
    int queryBoardLabelNumByIds(List<String> list);

    /**
     * 根据id查询标签信息
     *
     * @param list 标签id
     */
    int queryPortLabelNumByIds(List<String> list);

    /**
     * 回收标签
     *
     * @param deviceId 设施id
     */
    void recoverBoxLabelByDeviceId(String deviceId);

    /**
     * 回收标签
     *
     * @param deviceId 设施id
     */
    void recoverBoardLabelByDeviceId(String deviceId);

    /**
     * 回收标签
     *
     * @param deviceId 设施id
     */
    void recoverPortLabelByDeviceId(String deviceId);
}
