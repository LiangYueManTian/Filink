package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskDeviceDao;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.taskrelated.InspectionTaskDeviceReq;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskDeviceService;
import com.fiberhome.filink.workflowbusinessserver.constant.RequestHeaderConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.request.RequestHeaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 巡检任务设施表 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@Service
public class InspectionTaskDeviceServiceImpl extends ServiceImpl<InspectionTaskDeviceDao, InspectionTaskDevice> implements InspectionTaskDeviceService {

    @Autowired
    private InspectionTaskDeviceDao inspectionTaskDeviceDao;

    /**
     * 删除巡检任务关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 11:35
     * @param inspectionTaskDevice 删除巡检任务关联设施信息
     * @return 返回删除结果
     */
    @Override
    public int deleteInspectionTaskDevice(InspectionTaskDevice inspectionTaskDevice) {
        //返回删除结果
        int resultDeleteDevice = 0;
        if (null != inspectionTaskDevice && !StringUtils.isEmpty(inspectionTaskDevice.getInspectionTaskId())) {
            //巡检任务编号不为空时删除巡检关联设施
            resultDeleteDevice = inspectionTaskDeviceDao.deleteInspectionTaskDevice(inspectionTaskDevice);
        } else {
            //巡检任务编号为空时不删除巡检关联设施
            resultDeleteDevice = 0;
        }
        return resultDeleteDevice;
    }

    /**
     * 批量逻辑删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:59
     * @param inspectionTaskIds 巡检任务编号集合
     * @param isDelete 是否删除
     * @return int 返回巡检任务逻辑删除的结果
     */
    @Override
    public int deleteInspectionTaskDeviceBatch(List<String> inspectionTaskIds, String isDelete) {
        //返回删除结果
        int resultDeleteDevice = 0;
        if (null != inspectionTaskIds && 0 < inspectionTaskIds.size()) {
            //批量删除巡检任务关联设施
            resultDeleteDevice = inspectionTaskDeviceDao.deleteInspectionTaskDeviceBatch(inspectionTaskIds, isDelete);
        } else {
            resultDeleteDevice = 0;
        }
        return resultDeleteDevice;
    }

    /**
     *  批量逻辑删除设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 19:18
     * @param req 批量逻辑删除设施参数
     * @return 批量删除设施修改行数
     */
    @Override
    public int logicDeleteTaskDeviceBatch(InspectionTaskDeviceReq req) {
        return inspectionTaskDeviceDao.logicDeleteTaskDeviceBatch(req);
    }

    /**
     * 批量新增巡检任务关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 11:35
     * @param inspectionTaskDeviceList 批量新增设施参数信息
     * @param inspectionTaskId 巡检任务编号
     * @return 返回新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInspectionTaskDeviceBatch(List<InspectionTaskDevice> inspectionTaskDeviceList, String inspectionTaskId) {
        int resultInsertDevice = 0;
        //现在的时间
        Date nowDate = new Date();
        if (null != inspectionTaskDeviceList && 0 < inspectionTaskDeviceList.size()) {
            //批量新增关联设施
            for (InspectionTaskDevice deviceOne: inspectionTaskDeviceList) {
                //巡检任务设施编号
                deviceOne.setInspectionTaskDeviceId(NineteenUUIDUtils.uuid());
                //创建时间
                deviceOne.setCreateTime(nowDate);
                //创建用户
                deviceOne.setCreateUser(RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID));
                //巡检任务编号
                deviceOne.setInspectionTaskId(inspectionTaskId);
            }
            //新增集合的结果
            resultInsertDevice = inspectionTaskDeviceDao.insertInspectionTaskDeviceBatch(inspectionTaskDeviceList);
        }
        return resultInsertDevice;
    }

    /**
     * 查询巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:59
     * @param inspectionTaskId 巡检任务编号
     * @return List<InspectionTaskDevice> 返回巡检任务关联设施的结果
     */
    @Override
    public List<InspectionTaskDevice> queryInspectionTaskDeviceByTaskId(String inspectionTaskId) {
        return inspectionTaskDeviceDao.queryInspectionTaskDeviceByTaskId(inspectionTaskId);
    }

    /**
     * 根据巡检编号集合查询巡检任务设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 13:46
     * @param req 查询巡检任务关联设施参数
     * @return 巡检任务设施信息
     */
    @Override
    public List<InspectionTaskDevice> queryInspectionTaskDeviceForDeviceIdList(InspectionTaskDeviceReq req) {
        return inspectionTaskDeviceDao.queryInspectionTaskDeviceForDeviceIdList(req);
    }
}
