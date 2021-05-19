package com.fiberhome.filink.workflowbusinessserver.service.impl.procinspection;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceRecordBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procinspection.ProcInspectionRecordDao;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionByPageReq;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ValidateUtils;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionRecordVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * <p>
 * 工单巡检记录 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
@Service
public class ProcInspectionRecordServiceImpl extends ServiceImpl<ProcInspectionRecordDao, ProcInspectionRecord> implements ProcInspectionRecordService {

    @Autowired
    private ProcInspectionRecordDao procInspectionRecordDao;

    @Autowired
    private UserFeign userFeign;

    /**
     * 新增巡检工单记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 12:37
     * @param procInspectionRecordList 巡检工单记录集合
     * @return 新增巡检工单记录结果
     */
    @Override
    public int insertInspectionRecordBatch(List<ProcInspectionRecord> procInspectionRecordList) {
        return procInspectionRecordDao.insertInspectionRecordBatch(procInspectionRecordList);
    }

    /**
     * 批量修改巡检记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/2 10:25
     * @param procInspectionRecordList 巡检记录信息
     * @return 修改巡检任务记录结果
     */
    @Override
    public int updateInspectionRecordBatch(List<ProcInspectionRecord> procInspectionRecordList) {
        return procInspectionRecordDao.updateInspectionRecordBatch(procInspectionRecordList);
    }

    /**
     * 根据流程编号删除关联巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 14:56
     * @param procInspectionRecord
     * @return 返回删除巡检记录
     */
    @Override
    public int deleteRecordByProcId(ProcInspectionRecord procInspectionRecord) {
        return procInspectionRecordDao.deleteRecordByProcId(procInspectionRecord);
    }

    /**
     * 根据流程编号删除关联巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 14:56
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 删除关联巡检记录
     */
    @Override
    public int logicDeleteRecordByProcIds(String isDeleted, List<String> procIds) {
        return procInspectionRecordDao.logicDeleteRecordByProcIds(isDeleted, procIds);
    }

    /**
     * 根据流程编号查询关联巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 14:56
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 返回关联巡检记录
     */
    @Override
    public List<ProcInspectionRecord> queryInspectionRecord(String isDeleted, List<String> procIds) {
        return procInspectionRecordDao.queryInspectionRecord(isDeleted, procIds);
    }

    /**
     * 查询是否存在未巡检的设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 13:36
     * @param procId 工单编号
     * @return 未巡检的设施
     */
    @Override
    public List<ProcInspectionRecord> queryNotInspectionDeviceList(String procId) {
        return procInspectionRecordDao.queryNotInspectionDeviceList(procId);
    }

    /**
     * 查询已经巡检的设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/25 9:34
     * @param procId 工单编号
     * @return 已经巡检的设施信息
     */
    @Override
    public List<ProcInspectionRecord> queryIsInspectionDeviceList(String procId) {
        return procInspectionRecordDao.queryIsInspectionDeviceList(procId);
    }

    /**
     * 查询巡检记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 18:43
     * @param queryCondition 巡检记录信息
     * @return 返回巡检记录信息
     */
    @Override
    public List<ProcInspectionRecord> queryInspectionRecordInfo(QueryCondition queryCondition) {
        return procInspectionRecordDao.queryInspectionRecordInfo(queryCondition);
    }

    /**
     * 根据工单编号查询巡检记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 18:38
     * @param procIds 工单编号集合
     * @return 返回巡检记录信息
     */
    @Override
    public List<ProcInspectionRecordVo> queryInspectionRecordByProcIds(List<String> procIds) {
        if (!ObjectUtils.isEmpty(procIds)) {
            //根据工单编号查询巡检记录信息
            List<ProcInspectionRecord> recordList = procInspectionRecordDao.queryInspectionRecordInfoByProcIds(procIds);
            List<ProcInspectionRecordVo> procInspectionRecordVoList = this.procInspectionRecordVoList(recordList);
            return procInspectionRecordVoList;
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * 查询已完成的巡检信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 19:05
     * @param queryCondition 已完成的巡检信息的参数
     * @return 返回已完成巡检信息的结果
     */
    @Override
    public Result queryInspectionRecordByCondition(QueryCondition<CompleteInspectionByPageReq> queryCondition) {

        //校验方法参数
        if (null != ValidateUtils.checkQueryConditionParam(queryCondition)) {
            return ValidateUtils.checkQueryConditionParam(queryCondition);
        }

        String sortField = "inspectionTime";
        String sortRule = "desc";
        //获取查询条件过滤之后的条件
        queryCondition = ValidateUtils.filterQueryCondition(queryCondition, sortField, sortRule);

        //分页条件
        //设置分页beginNum
        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);
        // 构造分页条件
        Page page =  myBatiesBuildPage(queryCondition);
        //转义字符
        queryCondition = CompleteInspectionByPageReq.getParsePageReq(queryCondition);

        //巡检已完工记录
        List<ProcInspectionRecord> recordList = this.queryInspectionRecordInfo(queryCondition);

        List<ProcInspectionRecordVo> procInspectionRecordVoList = this.procInspectionRecordVoList(recordList);
        //返回数量
        int count = this.queryInspectionRecordInfoCount(queryCondition);
        // 构造返回结果
        PageBean pageBean = myBatiesBuildPageBean(page, count, procInspectionRecordVoList);
        return ResultUtils.success(pageBean);
    }

    /**
     * 返回巡检记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 10:12
     * @param recordList 记录集合
     * @return 返回巡检记录信息
     */
    public List<ProcInspectionRecordVo> procInspectionRecordVoList(List<ProcInspectionRecord> recordList) {
        List<String> userList = new ArrayList<>();
        List<ProcInspectionRecordVo> procInspectionRecordVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(recordList)) {
            ProcInspectionRecordVo recordVo;
            for (ProcInspectionRecord recordOne : recordList) {
                recordVo = new ProcInspectionRecordVo();
                BeanUtils.copyProperties(recordOne, recordVo);

                //巡检时间
                Date inspectionDate = recordOne.getInspectionTime();
                if (!ObjectUtils.isEmpty(inspectionDate)) {
                    Long inspectionTime = inspectionDate.getTime();
                    recordVo.setInspectionTime(inspectionTime);
                }
                if (!ObjectUtils.isEmpty(recordOne.getUpdateUser())) {
                    userList.add(recordOne.getUpdateUser());
                }
                procInspectionRecordVoList.add(recordVo);
            }
            //用户map
            Map<String, String> userMap = this.getUserMap(userList);
            //将用户名称加到巡检记录返回类中
            procInspectionRecordVoList = this.setInfoToInspectionRecordVo(userMap, procInspectionRecordVoList);
        }

        return procInspectionRecordVoList;
    }

    /**
     * 获取用户map
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 11:31
     * @param userList 用户集合
     * @return 返回用户map
     */
    public Map<String, String> getUserMap(List<String> userList) {
        Map<String, String> userMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(userList)) {
            Object userInfoObject = userFeign.queryUserByIdList(userList);
            List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userInfoObject), User.class);
            //添加用户map
            if (!ObjectUtils.isEmpty(userInfoList)) {
                for (User userInfoOne : userInfoList) {
                    userMap.put(userInfoOne.getId(), userInfoOne.getUserName());
                }
            }
        }
        return userMap;
    }

    /**
     * 页面显示的巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 11:32
     * @param userMap 用户map
     * @param procInspectionRecordVoList 巡检记录参数
     * @return 返回页面显示的巡检记录信息
     */
    public List<ProcInspectionRecordVo> setInfoToInspectionRecordVo(Map<String, String> userMap ,List<ProcInspectionRecordVo> procInspectionRecordVoList) {
        if (!ObjectUtils.isEmpty(procInspectionRecordVoList)) {
            for (ProcInspectionRecordVo procInspectionRecordVo : procInspectionRecordVoList) {
                if (!StringUtils.isEmpty(procInspectionRecordVo.getUpdateUser())) {
                    procInspectionRecordVo.setUpdateUserName(userMap.get(procInspectionRecordVo.getUpdateUser()));
                }
            }
        }
        return procInspectionRecordVoList;
    }

    /**
     * 查询巡检记录个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 18:43
     * @param queryCondition 巡检记录信息
     * @return 返回巡检记录结果
     */
    @Override
    public int queryInspectionRecordInfoCount(QueryCondition queryCondition) {
        return procInspectionRecordDao.queryInspectionRecordInfoCount(queryCondition);
    }

    /**
     * 查询关联设施记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 19:58
     * @param req 关联设施参数
     * @return 查询关联设施信息
     */
    @Override
    public List<ProcInspectionRecord> selectRelatedDeviceRecordList(ProcRelatedDeviceListForDeviceIdsReq req, String isDeleted) {
        ProcRelatedDeviceListForDeviceIdsReq deviceIdReq = new ProcRelatedDeviceListForDeviceIdsReq();
        deviceIdReq = req;
        deviceIdReq.setIsDeleted(isDeleted);
        return procInspectionRecordDao.selectRelatedDeviceRecordList(req);
    }

    /**
     * 逻辑删除关联设施记录
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 22:10
     * @param req 逻辑删除参数
     * @return 逻辑删除关联设施
     */
    @Override
    public int logicDeleteRelatedDeviceRecord(LogicDeleteRelatedDeviceRecordBatch req) {
        return procInspectionRecordDao.logicDeleteRelatedDeviceRecord(req);
    }
}
