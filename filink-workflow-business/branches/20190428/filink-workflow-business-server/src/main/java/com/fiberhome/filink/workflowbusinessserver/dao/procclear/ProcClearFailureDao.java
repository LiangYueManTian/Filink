package com.fiberhome.filink.workflowbusinessserver.dao.procclear;

import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcClearFailure;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 销障工单表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-15
 */
public interface ProcClearFailureDao extends BaseMapper<ProcClearFailure> {

    /**
     * 新增销障工单特有信息
     *
     * @param procClearFailure 销障工单基础信息
     * @return int
     */
    int addProcClearFailureSpecific(ProcClearFailure procClearFailure);

    /**
     * 修改销障工单特有信息
     *
     * @param procClearFailure 销障工单基础信息
     * @return int
     */
    int updateProcClearFailureSpecificByProcId(ProcClearFailure procClearFailure);

    /**
     * 删除/恢复销障工单特有信息
     *
     * @param procId 工单id
     * @param isDeleted 逻辑删除字段
     *
     * @return int
     */
    int updateProcClearFailureSpecificIsDeleted(@Param("procId") String procId,@Param("isDeleted") String isDeleted);


    /**
     * 批量逻辑删除销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 11:51
     * @param procBaseReq 删除销障工单参数
     * @return 批量删除销障工单
     */
    int updateProcClearFailureSpecificIsDeletedBatch(ProcBaseReq procBaseReq);

    /**
     * 获取销障工单特有信息
     *
     * @param procIds 工单ids
     * @return List<ProcClearFailure>
     */
    List<ProcClearFailure> queryProcClearFailureSpecific(@Param("procIds") Set<String> procIds);
}
