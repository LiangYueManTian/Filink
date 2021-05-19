package com.fiberhome.filink.workflowbusinessserver.service.procinspection;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceRecordBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionByPageReq;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 工单巡检记录 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
public interface ProcInspectionRecordService extends IService<ProcInspectionRecord> {

    /**
     * 新增巡检工单记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 12:37
     * @param procInspectionRecordList 巡检工单记录集合
     * @return 新增巡检工单记录结果
     */
    int insertInspectionRecordBatch(@Param("list") List<ProcInspectionRecord> procInspectionRecordList);

    /**
     * 批量修改巡检记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/2 10:25
     * @param procInspectionRecordList 巡检记录信息
     * @return 修改巡检任务记录结果
     */
    int updateInspectionRecordBatch(@Param("list") List<ProcInspectionRecord> procInspectionRecordList);

    /**
     * 根据流程编号删除关联巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 14:56
     * @param procInspectionRecord
     * @return 返回删除巡检记录
     */
    int deleteRecordByProcId(ProcInspectionRecord procInspectionRecord);

    /**
     * 根据流程编号删除关联巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 14:56
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 删除关联巡检记录
     */
    int logicDeleteRecordByProcIds(@Param("isDeleted") String isDeleted, @Param("list") List<String> procIds);

    /**
     * 根据流程编号查询关联巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 14:56
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 返回关联巡检记录
     */
    List<ProcInspectionRecord> queryInspectionRecord(@Param("isDeleted") String isDeleted, @Param("list") List<String> procIds);


    /**
     * 查询是否存在未巡检的设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 13:36
     * @param procId 工单编号
     * @return 未巡检的设施
     */
    List<ProcInspectionRecord> queryNotInspectionDeviceList(String procId);

    /**
     * 查询已经巡检的设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/25 9:34
     * @param procId 工单编号
     * @return 已经巡检的设施信息
     */
    List<ProcInspectionRecord> queryIsInspectionDeviceList(String procId);

    /**
     * 查询巡检记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 18:43
     * @param queryCondition 巡检记录信息
     * @return 返回巡检记录信息
     */
    List<ProcInspectionRecord> queryInspectionRecordInfo(QueryCondition queryCondition);

    /**
     * 根据工单编号查询巡检记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 18:38
     * @param procIds 工单编号集合
     * @return 返回巡检记录信息
     */
    List<ProcInspectionRecordVo> queryInspectionRecordByProcIds(List<String> procIds);

    /**
     * 查询已完成的巡检信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 19:05
     * @param queryCondition 已完成的巡检信息的参数
     * @return 返回已完成巡检信息的结果
     */
    Result queryInspectionRecordByCondition(QueryCondition<CompleteInspectionByPageReq> queryCondition);

    /**
     * 查询巡检记录个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 18:43
     * @param procInspectionRecord 巡检记录信息
     * @return 返回巡检记录结果
     */
    int queryInspectionRecordInfoCount(QueryCondition procInspectionRecord);

    /**
     * 查询关联设施记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 19:58
     * @param req 关联设施参数
     * @param isDeleted 是否删除
     * @return 查询关联设施信息
     */
    List<ProcInspectionRecord> selectRelatedDeviceRecordList(ProcRelatedDeviceListForDeviceIdsReq req, String isDeleted);


    /**
     * 逻辑删除关联设施记录
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 22:10
     * @param req 逻辑删除参数
     * @return 逻辑删除关联设施
     */
    int logicDeleteRelatedDeviceRecord(LogicDeleteRelatedDeviceRecordBatch req);
}
