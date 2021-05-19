package com.fiberhome.filink.workflowbusinessserver.dao.procinspection;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.resp.procinspection.ProcInspectionResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 巡检工单表 Mapper 接口
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
public interface ProcInspectionDao extends BaseMapper<ProcInspection> {

    /**
     * 根据流程编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param procId 流程编号
     * @return 巡检工单详情
     */
    ProcInspection selectInspectionProcByProcId(@Param("procId") String procId);

    /**
     * 根据流程编号查询巡检工单集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param procIds 工单编号集合
     * @return 巡检工单
     */
    List<ProcInspection> selectInspectionProcByProcIds(@Param("procIds") Set<String> procIds);


    /**
     * 根据工单编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 19:47
     * @param req 工单参数
     * @return 巡检工单记录信息
     */
    List<ProcInspection> selectInspectionProcForProcIds(ProcBaseReq req);

    /**
     * 根据流程编号删除巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 删除巡检工单结果
     */
    int logicDeleteProcInspection(@Param("isDeleted")String isDeleted ,@Param("list")List<String> procIds);

    /**
     * 批量修改巡检设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 22:55
     * @param list 巡检工单集合
     * @return 修改结果
     */
    int updateProcInspectionDeviceCountBatch(@Param("list") List<ProcInspection> list);

    /**
     * 查询巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:17
     * @param queryCondition 查询对象
     * @return 巡检工单集合
     */
    List<ProcInspection> queryProcInspectionInfo(QueryCondition queryCondition);

    /**
     * 查询巡检工单个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:17
     * @param queryCondition 查询对象
     * @return 巡检工单个数
     */
    int queryProcInspectionInfoCount(QueryCondition queryCondition);

    /**
     * 根据巡检工单编号查询巡检工单完成数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 16:50
     * @param procInspection 巡检任务
     * @return 查询巡检工单完成数量
     */
    ProcInspectionResp queryProcInspectionByProcInspectionId(ProcInspection procInspection);
}
