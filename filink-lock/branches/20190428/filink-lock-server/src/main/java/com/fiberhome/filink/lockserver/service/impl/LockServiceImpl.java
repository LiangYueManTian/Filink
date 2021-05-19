package com.fiberhome.filink.lockserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.filinkoceanconnectapi.bean.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectapi.bean.RegistryResult;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import com.fiberhome.filink.lockserver.bean.*;
import com.fiberhome.filink.lockserver.constant.ConstantParam;
import com.fiberhome.filink.lockserver.constant.FunctionCode;
import com.fiberhome.filink.lockserver.constant.cmd.CmdId;
import com.fiberhome.filink.lockserver.constant.cmd.FiLinkReqParamsDto;
import com.fiberhome.filink.lockserver.constant.cmd.LockOperator;
import com.fiberhome.filink.lockserver.constant.lock.LockI18n;
import com.fiberhome.filink.lockserver.constant.lock.LockResultCode;
import com.fiberhome.filink.lockserver.dao.LockDao;
import com.fiberhome.filink.lockserver.exception.*;
import com.fiberhome.filink.lockserver.service.ControlService;
import com.fiberhome.filink.lockserver.service.LockService;
import com.fiberhome.filink.lockserver.stream.CodeSender;
import com.fiberhome.filink.lockserver.util.AuthUtils;
import com.fiberhome.filink.lockserver.util.ListUtils;
import com.fiberhome.filink.lockserver.util.OperateLogUtils;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.onenetapi.bean.CreateDevice;
import com.fiberhome.filink.onenetapi.bean.CreateResult;
import com.fiberhome.filink.onenetapi.bean.DeleteDevice;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.AuthFeign;
import com.fiberhome.filink.userapi.bean.DeviceInfo;
import com.fiberhome.filink.userapi.bean.UserAuthInfo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fiberhome.filink.lockserver.constant.ConstantParam.*;
import static com.fiberhome.filink.lockserver.constant.cmd.LockOperator.OPERATE_ADD;
import static com.fiberhome.filink.lockserver.constant.cmd.LockOperator.OPERATE_UPDATE;
import static com.fiberhome.filink.lockserver.enums.PlatFormType.OceanConnect;
import static com.fiberhome.filink.lockserver.enums.PlatFormType.OneNet;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPageBean;


/**
 * 电子锁实现类
 *
 * @author CongcaiYu
 */
@Log4j
@Service
public class LockServiceImpl implements LockService {

    @Autowired
    private LockDao lockDao;

    @Autowired
    private ControlService controlService;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private CodeSender codeSender;

    @Autowired
    private AuthFeign authFeign;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private OceanConnectFeign oceanConnectFeign;

    @Autowired
    private OneNetFeign oneNetFeign;

    @Autowired
    private OperateLogUtils logUtils;


    /**
     * 根据主控id查询电子锁信息
     *
     * @param controlId 主控id
     * @return 电子锁集合
     */
    @Override
    public List<Lock> queryLockByControlId(String controlId) {
        return lockDao.queryLockByControlId(controlId);
    }

    /**
     * 根据二维码查询电子锁及主控信息
     *
     * @param qrcodeBean 二维码实体
     * @return 电子锁及主控信息
     */
    @Override
    public Result queryLockAndControlByQrcode(QrcodeBean qrcodeBean) {
        //查询锁信息
        Lock lock = queryLockByQrcode(qrcodeBean.getQrcode());
        //获得锁关联的主控id
        String controlId = lock.getControlId();
        //根据主控id获得主控信息
        ControlParam controlParam = getControlParamById(controlId);
        //获取锁关联的设施id
        String deviceId = controlParam.getDeviceId();
        //检验权限
        List<Lock> locks = queryLockByDeviceIdToCall(deviceId);
        if (ObjectUtils.isEmpty(locks)) {
            throw new FilinkAccessDenyException();
        }

        DoorForPda doorForPda = new DoorForPda();
        BeanUtils.copyProperties(lock, doorForPda);
        // 获得授权状态
        String permission = getOpenLockPermission(lock.getDeviceId(), lock.getDoorNum());
        doorForPda.setState(permission);

        ControlParamForControl controlParamForControl = new ControlParamForControl();
        BeanUtils.copyProperties(controlParam, controlParamForControl);
        //电子锁主控的锁信息
        controlParamForControl.setLockList(new ArrayList<DoorForPda>(16) {
            {
                add(doorForPda);
            }
        });

        //获得主控关联的设施信息
        DeviceInfoForLock deviceInfo = findDeviceInfoForLockByDeviceId(deviceId);
        deviceInfo.setControlList(new ArrayList<ControlParamForControl>(16) {{
            add(controlParamForControl);
        }});
        return ResultUtils.success(deviceInfo);

    }

    /**
     * 更新电子锁状态
     *
     * @param lock 电子锁信息
     */
    @Override
    public void updateLockStatus(Lock lock) {
        lockDao.updateLockStatus(lock);

    }

    /**
     * 批量更新电子锁状态
     *
     * @param locks 电子锁集合
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateLockStatus(List<Lock> locks) {
        for (Lock lock : locks) {
            updateLockStatus(lock);
        }
        return ResultUtils.success();
    }


    /**
     * 根据二维码开锁
     *
     * @param qrcodeBean
     * @return 开锁结果
     */
    @Override
    public Result openLockByQrcode(QrcodeBean qrcodeBean) {

        //获得二维码对应的锁信息
        Lock lock = queryLockByQrcode(qrcodeBean.getQrcode());
        ControlParam controlParam = controlService.getControlParamById(lock.getControlId());
        //判断授权
        String permission = getOpenLockPermission(lock.getDeviceId(), lock.getDoorNum());
        if (FALSE.equals(permission)) {
            throw new FilinkOpenLockException("don't have permision to open lock");
        }

        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(lock.getDeviceId());

        //构造指令开锁参数
        List<FiLinkReqParamsDto> reqParamsDtoList = new ArrayList<>();
        FiLinkReqParamsDto reqParamsDto = generateOpenLockParams(controlParam, Arrays.asList(lock.getDoorNum()));
        reqParamsDtoList.add(reqParamsDto);
        codeSender.send(reqParamsDtoList);

        //记录日志
        addSingleOpenLockLog(lock, deviceInfo);

        return ResultUtils.success(LockResultCode.SUCCESS,
                I18nUtils.getString(LockI18n.OPEN_LOCK_SUCCESS));

    }

    /**
     * 开锁
     *
     * @param openLockBean 开锁参数
     * @return 操作结果
     */
    @Override
    public Result openLock(OpenLockBean openLockBean) {
        String deviceId = openLockBean.getDeviceId();
        List<String> doorNumList = openLockBean.getDoorNumList();
        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(openLockBean.getDeviceId());
        //校验门编号
        List<Lock> locks = new ArrayList<>();
        checkDoorNum(doorNumList, locks, deviceId);


        //构造指令开锁参数
        List<FiLinkReqParamsDto> reqParamsDtoList = new ArrayList<>();
        Map<String, List<Lock>> collect = locks.stream().collect(Collectors.groupingBy(Lock::getControlId));
        for (Map.Entry<String, List<Lock>> entry : collect.entrySet()) {
            List<String> doorNumListOfControl = new ArrayList<>();
            for (Lock lock : entry.getValue()) {
                String doorNum = lock.getDoorNum();
                doorNumListOfControl.add(doorNum);
            }
            ControlParam controlParam = controlService.getControlParamById(entry.getKey());
            FiLinkReqParamsDto fiLinkReqParamsDto = generateOpenLockParams(controlParam, doorNumListOfControl);
            reqParamsDtoList.add(fiLinkReqParamsDto);
        }


        codeSender.send(reqParamsDtoList);
        //记录日志
        logUtils.addMediumOperateLog(locks, FunctionCode.OPEN_LOCK_CODE, deviceInfo, LogConstants.OPT_TYPE_WEB);
        return ResultUtils.success(LockResultCode.SUCCESS,
                I18nUtils.getString(LockI18n.OPEN_LOCK_SUCCESS));
    }

    /**
     * 开锁 for pda
     *
     * @param openLockBean 开锁参数
     * @return 操作结果
     */
    @Override
    public Result openLockForPda(OpenLockBean openLockBean) {
        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(openLockBean.getDeviceId());
        //判断授权
        String permission = getOpenLockPermission(openLockBean.getDeviceId(), openLockBean.getDoorNum());
        if (FALSE.equals(permission)) {
            throw new FilinkOpenLockException("don't have persion to open lock");
        }

        //构造指令开锁参数
        List<FiLinkReqParamsDto> reqParamsDtoList = new ArrayList<>();
        //申请要开的锁
        Lock lock = queryLockByDeviceIdAndDoorNum(openLockBean.getDeviceId(), openLockBean.getDoorNum());
        ControlParam controlParam = controlService.getControlParamById(lock.getControlId());
        //生成开锁指令
        FiLinkReqParamsDto reqParamsDto = generateOpenLockParams(controlParam, Arrays.asList(lock.getDoorNum()));
        reqParamsDtoList.add(reqParamsDto);

        //发送指令
        codeSender.send(reqParamsDtoList);

        //记录日志
        addSingleOpenLockLog(lock, deviceInfo);


        return ResultUtils.success(LockResultCode.SUCCESS,
                I18nUtils.getString(LockI18n.OPEN_LOCK_SUCCESS));
    }

    /**
     * 根据设施id查询电子锁信息
     *
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    @Override
    public List<Lock> queryLockByDeviceIdToCall(String deviceId) {
        List<Lock> lockList = new ArrayList<>();
        try {
            authUtils.addAuth(deviceId);
        } catch (Exception e) {
            log.info("没有查看该设施的主控权限");
            return lockList;
        }
        lockList = queryLockByDeviceId(deviceId);
        return lockList;
    }

    /**
     * 根据设施id查询电子锁信息(不需要权限)
     *
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    @Override
    public List<Lock> queryLockByDeviceId(String deviceId) {
        return lockDao.queryLockByDeviceId(deviceId);
    }

    /**
     * 根据设施id查询门锁状态
     *
     * @param lockBean 锁参数
     * @return 门锁状态
     */
    @Override
    public Result queryLockStatusByDeviceId(OpenLockBean lockBean) {
        String deviceId = lockBean.getDeviceId();
        //查询门锁信息
        LockStatusForPda lockStatusForPda = queryLockAndContorlByDeviceId(deviceId);
        //指定设施的门锁信息不需要设施名称的显示
        return ResultUtils.success(lockStatusForPda.getLockList());
    }

    /**
     * 根据设施id查询电子锁锁具信息
     *
     * @param controlReq 参数对象
     * @return 电子锁锁具信息
     */
    @Override
    public Result queryLockAndControlByDeviceId(ControlReq controlReq) {
        //设施id
        String deviceId = controlReq.getDeviceId();
        //获得主控关联的设施信息
        DeviceInfoForLock deviceInfo = findDeviceInfoForLockByDeviceId(deviceId);
        //获得主控信息
        List<ControlParamForControl> controlParamForControlList = controlService.getControlParamForControlByDeviceId(deviceId);
        //获得每个主控下的门锁映射关系
        for (ControlParamForControl controlParam : controlParamForControlList) {
            List<Lock> lockList = queryLockByControlId(controlParam.getHostId());
            List copyList = ListUtils.copyList(lockList, DoorForPda.class);
            controlParam.setLockList(copyList);
        }
        deviceInfo.setControlList(controlParamForControlList);
        return ResultUtils.success(deviceInfo);
    }


    /**
     * 分页查询电子锁信息
     *
     * @param pageCondition 分页查询条件
     * @return 查询结果
     */
    @Override
    public Result lockListByPage(PageCondition pageCondition) {
        //设施的门锁状态集合
        List<LockStatusForPda> lockStatusForPdaList = new ArrayList<>();


        //构造 QueryContion
        QueryCondition<DeviceInfoDto> queryCondition = new QueryCondition<>();
        queryCondition.setPageCondition(pageCondition);

        // 获得符合数据权限的设施id集合
        List<String> userDeviceIds;
        if (ConstantParam.ADMIN.equals(RequestInfoUtils.getUserId())) {
            userDeviceIds = null;
        } else {

            try {
                userDeviceIds = authUtils.getUserDeviceIds();
            } catch (Exception e) {
                //异常，说明没有数据权限
                return ResultUtils.success(lockStatusForPdaList);
            }
        }

        //分页查到的数据
        List<String> deviceIdList = controlService.deviceIdListByPage(pageCondition, userDeviceIds);
        //所有的有主控及门锁设施id
        List<String> allDeviceList = controlService.deviceIdList();


        //查询每个设施的门锁状态信息
        for (String deviceId : deviceIdList) {
            LockStatusForPda lockStatusForPda = queryLockAndContorlByDeviceId(deviceId);
            lockStatusForPdaList.add(lockStatusForPda);
        }
        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        PageBean pageBean = myBatiesBuildPageBean(page, allDeviceList.size(), lockStatusForPdaList);
        return ResultUtils.success(pageBean);
    }

    /**
     * 电子锁配置数据上传(新增/更新)
     *
     * @param control 主控
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result operateControlAndLock(Control control) {
        //操作编号
        String operateCode = control.getOperateCode();
        //获得设施id
        String deviceId = control.getDeviceId();
        //检查设施是否存在
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(deviceId);
        //获得主控信息列表
        ControlParam controlParam = control.getControlParam();
        //将主控id转成大写的,小写的平台不认
        String controlId = controlParam.getHostId().toUpperCase();
        controlParam.setHostId(controlId);
        //设施主控关联的设施id
        controlParam.setDeviceId(deviceId);

        //获得主控的锁信息
        List<Lock> locks = new ArrayList<>();
        //获得单个主控的锁信息列表
        List<Lock> lockList = controlParam.getLockList();
        //获得当前时间戳
        long currentTime = System.currentTimeMillis();
        for (Lock lock : lockList) {
            if (operateCode.equals(OPERATE_ADD)) {
                //新增的时候，需要检查二维码、锁芯id是否重复
                checkQrcode(lock.getQrcode(), OPERATE_ADD, controlId);
                checkLockCode(lock.getLockCode(), OPERATE_ADD, controlId);
                //主控id不可重复
                controlService.checkControlIdReused(controlParam.getHostId());
                //设置排序字段
                lock.setRank(String.valueOf(currentTime++));
                lock.setLockId(NineteenUUIDUtils.uuid());
            } else if (operateCode.equals(OPERATE_UPDATE)) {
                //更新的时候，需要检查二维码、锁芯id是否重复
                checkQrcode(lock.getQrcode(), OPERATE_UPDATE, controlId);
                checkLockCode(lock.getLockCode(), OPERATE_UPDATE, controlId);
            }
            lock.setControlId(controlParam.getHostId());
        }


        locks.addAll(lockList);

        if (operateCode.equals(OPERATE_ADD)) {
            controlParam.setControlId(NineteenUUIDUtils.uuid());

            //配置默认值
            controlParam.setConfigValue(getConfigValue(deviceId));
            controlParam.setSyncStatus(SYNC_STATUS);
            // 平台id添加
            Result result = setPlatformId(control.getDeviceName(), controlParam);
            if (result != null && result.getCode() != 0) {
                return result;
            }
            //主控添加的约束
            checkAddConstraintOfControl(deviceId, controlParam, locks);
            try {
                //电子锁配置数据新增
                controlService.addControlParam(controlParam);
                //映射关系新增
                addLocks(locks, deviceInfo);
            } catch (Exception e) {
                //添加失败 ，删除平台设备注册信息
                deleteFromPlatForm(controlParam);
            }
        } else if (operateCode.equals(OPERATE_UPDATE)) {
            //更新主控及锁
            updateControlAndLock(controlParam, deviceInfo, locks);
        }
        return ResultUtils.success();
    }

    /**
     * 添加锁
     *
     * @param locks      锁集合
     * @param deviceInfo 设施信息
     */
    private void addLocks(List<Lock> locks, DeviceInfoDto deviceInfo) {
        lockDao.addLocks(locks);
        logUtils.addMediumOperateLog(locks, FunctionCode.ADD_LOCK_INFO, deviceInfo, LogConstants.OPT_TYPE_PDA);
    }

    /**
     * 修改锁
     *
     * @param locks      锁集合
     * @param deviceInfo 设施信息
     */
    private void updateLocks(List<Lock> locks, DeviceInfoDto deviceInfo) {
        //获取这组门锁的主控id
        String controlId = locks.get(0).getControlId();
        Integer count = lockDao.countLocks(locks, controlId);
        if (count == null || count != locks.size()) {
            throw new FilinkSensorListException();
        }
        lockDao.updateLocks(locks, controlId);
        logUtils.addMediumOperateLog(locks, FunctionCode.UPDATE_LOCK_INFO, deviceInfo, LogConstants.OPT_TYPE_PDA);
    }

    /**
     * 删除授权
     *
     * @param deviceId    设施id
     * @param doorNumList 门编号集合
     */
    @Override
    public void deleteAuth(String deviceId, List<String> doorNumList) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setDoorId(doorNumList);
        Integer integer = authFeign.deleteAuthByDevice(deviceInfo);
        if (integer == null) {
            throw new FilinkSystemException("调用authFeign删除授权记录失败，事务回滚");
        }
    }

    /**
     * 删除锁
     *
     * @param locks         锁集合
     * @param deviceInfoDto 设施
     */
    private void deleteLocks(List<Lock> locks, DeviceInfoDto deviceInfoDto) {
        String deviceId = deviceInfoDto.getDeviceId();
        List<String> doorNumList = locks.stream().map(Lock::getDoorNum).collect(Collectors.toList());
        lockDao.deleteLockByDeviceIdAndDoorNum(deviceId, doorNumList);
        // 删除授权
        deleteAuth(deviceId, doorNumList);
        logUtils.addMediumOperateLog(locks, FunctionCode.DELETE_LOCK_INFO, deviceInfoDto, LogConstants.OPT_TYPE_PDA);
    }

    /**
     * 分页查询电子锁
     *
     * @param deviceList 设施id集合
     * @return 电子锁列表
     */
    @Override
    public List<Lock> lockListByDeviceIds(List<String> deviceList) {
        return lockDao.lockListByDeviceIds(deviceList);
    }

    /**
     * 构造pda所需的门锁状态数据结构
     */
    private void generateLockStatusForPda(LockStatusForPda lockStatusForPda, List<ControlParam> controlParamList) {
        List<LockForPda> lockForPdaList = new ArrayList<>(64);
        //查询每个主控下的门锁信息
        if (!ObjectUtils.isEmpty(controlParamList)) {
            for (ControlParam controlParam : controlParamList) {
                //根据主控id查询电子锁
                List<Lock> locks = queryLockByControlId(controlParam.getHostId());
                for (Lock lock : locks) {
                    //构造pda需要的门锁数据结构
                    LockForPda lockForPda = new LockForPda();
                    BeanUtils.copyProperties(lock, lockForPda);
                    lockForPdaList.add(lockForPda);
                }
            }
        }
        lockStatusForPda.setLockList(lockForPdaList);
    }

    /**
     * 根据设施id查询门锁状态
     *
     * @param deviceId 设施id
     * @return 门锁状态
     */
    private LockStatusForPda queryLockAndContorlByDeviceId(String deviceId) {
        LockStatusForPda lockStatusForPda = new LockStatusForPda();
        //查询设施对应的主控信息
        List<ControlParam> controlParamList = controlService.getControlInfoByDeviceIdToCall(deviceId);
        if (controlParamList == null || controlParamList.size() == 0) {
            return lockStatusForPda;
        }
        //查询设施信息
        DeviceInfoDto deviceInfo = findDeviceByDeviceId(deviceId);
        //属性复制
        BeanUtils.copyProperties(deviceInfo, lockStatusForPda);

        generateLockStatusForPda(lockStatusForPda, controlParamList);
        return lockStatusForPda;
    }


    /**
     * 校验门编号
     *
     * @param doorNumList 门编号集合
     * @param locks       电子锁集合
     * @param deviceId    设施id
     */
    private void checkDoorNum(List<String> doorNumList, List<Lock> locks, String deviceId) {
        for (String doorNum : doorNumList) {
            Lock lock = lockDao.queryLockByDeviceIdAndDoorNum(deviceId, doorNum);
            if (lock == null) {
                throw new FilinkDoorNumIsNullException("doorNum: " + doorNum + " is not exist>>>>>");
            }
            locks.add(lock);
        }
    }

    /**
     * 主控添加约束检查  pda会保证新增/更新时，所选的门编号是没被使用的
     * 验证主控约束：一台设施最多四个主控，一个主控最多四个映射，一台设施最多四个映射,同一个设施的主控名称、门名称不能重复
     */
    private void checkAddConstraintOfControl(String deviceId, ControlParam controlParam, List<Lock> locks) {
        //一台设施最多四个主控  已有的主控+这个新增的主控
        List<ControlParam> controlParamList = controlService.getControlInfoByDeviceId(deviceId);
        if (controlParamList != null) {
            if (controlParamList.size() +1> MAX_CONTROL_NUM) {
                throw new FilinkControlMaxNumException();
            }
        }
        //一台设施最多四个映射  已有的门+新加的门
        List<Lock> lockList = queryLockByDeviceId(deviceId);
        if (lockList != null) {
            if (lockList.size() + locks.size() > MAX_DOOR_NUM) {
                throw new FilinkDoorMaxNumException();
            }
        }

        //同一个设施的主控名称不能重复(新增)
        ControlParam control = controlService.getControlByDeviceIdAndControlName(controlParam);
        if (control != null) {
            throw new FilinkControlNameResuedException();
        }

        //同一个设施的门名称不能重复(新增)
        if (!StringUtils.isEmpty(lockList)) {
            List<String> newDoorNameList = locks.stream().map(Lock::getDoorName).collect(Collectors.toList());
            List<String> oldDoorNameList = lockList.stream().map(Lock::getDoorName).collect(Collectors.toList());
            oldDoorNameList.retainAll(newDoorNameList);
            if (oldDoorNameList.size() > 0) {
                throw new FilinkDeviceDoorNameResuedException();
            }
        }


    }

    /**
     * 主控更新约束检查
     * 验证主控约束：一个主控最多四个映射，一台设施最多四个映射(后者满足，前者一定满足)
     * 同一个设施的主控名称不能重复
     */
    private void checkUpdateConstraintOfControl(ControlParam controlParam) {
        //同一个设施的主控名称不能重复(更新)
        ControlParam control = controlService.getControlByDeviceIdAndControlName(controlParam);
        if (control != null && !control.getHostId().equals(controlParam.getHostId())) {
            throw new FilinkControlNameResuedException();
        }

    }

    /**
     * 根据设施id和门编号查询电子锁信息
     *
     * @param deviceId 设施id
     * @param doorNum  门编号
     * @return 电子锁信息
     */
    @Override
    public Lock queryLockByDeviceIdAndDoorNum(String deviceId, String doorNum) {
        Lock lock = lockDao.queryLockByDeviceIdAndDoorNum(deviceId, doorNum);
        if (lock == null) {
            throw new FilinkControlIsNullException();
        }
        return lock;
    }

    /**
     * 根据设施id查询设施信息
     *
     * @param deviceId 设施id
     * @return 设施信息
     */
    private DeviceInfoDto findDeviceByDeviceId(String deviceId) {
        DeviceInfoDto deviceInfoDto = deviceFeign.getDeviceById(deviceId);
        if (deviceInfoDto == null || deviceInfoDto.getDeviceId() == null) {
            throw new FilinkDeviceIsNullException("设施为空");
        }
        return deviceInfoDto;
    }

    /**
     * 根据设施id查询设施默认配置
     *
     * @param deviceId 设施id
     * @return 配置参数
     */
    private String getConfigValue(String deviceId) {
        String defaultParams = deviceFeign.getDefaultParams(deviceId);
        if (StringUtils.isEmpty(defaultParams)) {
            throw new FilinkConfigValueIsNullException();
        }
        return defaultParams;
    }

    /**
     * 根据设施id查询锁具所需的设施信息
     *
     * @param deviceId 设施id
     * @return deviceInfoForLock
     */
    private DeviceInfoForLock findDeviceInfoForLockByDeviceId(String deviceId) {
        DeviceInfoForLock deviceInfoForLock = new DeviceInfoForLock();
        BeanUtils.copyProperties(findDeviceByDeviceId(deviceId), deviceInfoForLock);
        return deviceInfoForLock;
    }

    /**
     * 根据二维码查询电子锁信息
     *
     * @param qrcode 二维码
     * @return 锁信息
     */
    @Override
    public Lock queryLockByQrcode(String qrcode) {
        //根据二维码获得锁信息
        Lock lock = lockDao.queryLockByQrcode(qrcode);
        if (lock == null) {
            //二维码不存在
            throw new FilinkQrcodeErrorException();
        }
        return lock;
    }

    /**
     * 根据主控id查询主控信息
     *
     * @param controlId 主控id
     * @return 主控信息
     */
    private ControlParam getControlParamById(String controlId) {
        //根据主控id获得主控信息
        ControlParam controlParam = controlService.getControlParamById(controlId);
        return controlParam;
    }


    /**
     * 构造指令开锁参数
     *
     * @param controlParam 主控参数
     * @param doorNumList  门编号集合
     * @return 开锁指令参数
     */
    private FiLinkReqParamsDto generateOpenLockParams(ControlParam controlParam, List<String> doorNumList) {
        //构造指令开锁参数
        FiLinkReqParamsDto reqParamsDto = new FiLinkReqParamsDto();
        reqParamsDto.setToken(RequestInfoUtils.getToken());
        reqParamsDto.setCmdId(CmdId.OPEN_LOCK);
        reqParamsDto.setEquipmentId(controlParam.getHostId());
        reqParamsDto.setAppId(controlParam.getProductId());
        //获得设备id
        reqParamsDto.setPhoneId(authUtils.getPhoneId());
        reqParamsDto.setAppKey(authUtils.getAppKey());
        reqParamsDto.setHardwareVersion(controlParam.getHardwareVersion());
        reqParamsDto.setSoftwareVersion(controlParam.getSoftwareVersion());
        reqParamsDto.setImei(controlParam.getImei());
        reqParamsDto.setPlateFormId(controlParam.getPlatformId());
        reqParamsDto.setPlateForm(controlParam.getCloudPlatform());

        List<Map<String, Object>> lockMaps = new ArrayList<>();
        for (String doorNum : doorNumList) {
            Map<String, Object> lockMap = new HashMap<>(64);
            lockMap.put(DOOR_NUM, doorNum);
            lockMap.put(OPERATE, LockOperator.LOCK_ON);
            lockMaps.add(lockMap);
        }
        Map<String, Object> params = new HashMap<>(64);
        params.put(PARAMS, lockMaps);
        reqParamsDto.setParams(params);
        return reqParamsDto;
    }

    /**
     * 记录PDA的开锁日志
     *
     * @param lock       电子锁信息
     * @param deviceInfo 设施
     */
    private void addSingleOpenLockLog(Lock lock, DeviceInfoDto deviceInfo) {
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        //记录日志
        logUtils.addMediumOperateLog(locks, FunctionCode.OPEN_LOCK_CODE, deviceInfo, LogConstants.OPT_TYPE_PDA);
    }

    /**
     * 检验二维码字符串是否重复
     *
     * @param qrcode    二维码
     * @param type      上传类型
     * @param controlId 主控id
     */
    private void checkQrcode(String qrcode, String type, String controlId) {
        if (!StringUtils.isEmpty(qrcode)) {
            Lock lockInfo = lockDao.queryLockByQrcode(qrcode);
            List<Lock> lockList = queryLockByControlId(controlId);
            List<String> qrcodeList = lockList.stream().map(Lock::getQrcode).collect(Collectors.toList());

            if (type.equals(OPERATE_ADD)) {
                if (lockInfo != null) {
                    //二维码已被使用
                    throw new FilinkQrcodeReusedException();
                }
            } else if (type.equals(OPERATE_UPDATE)) {
                if (lockInfo != null && !qrcodeList.contains(qrcode)) {
                    //二维码已被使用
                    throw new FilinkQrcodeReusedException();
                }
            }

        }
    }

    /**
     * 检验锁芯id是否重复
     *
     * @param lockCode  锁芯id
     * @param type      上传类型
     * @param controlId 主控id
     */
    private void checkLockCode(String lockCode, String type, String controlId) {
        if (!StringUtils.isEmpty(lockCode)) {
            Lock lockInfo = lockDao.queryLockByLockCode(lockCode);
            List<Lock> lockList = queryLockByControlId(controlId);
            List<String> lockCodeList = lockList.stream().map(Lock::getLockCode).collect(Collectors.toList());

            if (type.equals(OPERATE_ADD)) {
                if (lockInfo != null) {
                    //锁芯id已被使用
                    throw new FilinkLockCodeReusedException();
                }
            } else if (type.equals(OPERATE_UPDATE)) {
                if (lockInfo != null && !lockCodeList.contains(lockCode)) {
                    //锁芯id已被使用
                    throw new FilinkLockCodeReusedException();
                }
            }

        }
    }

    /**
     * 获取开锁权限
     *
     * @param deviceId 设施id
     * @param doorNum  门编号
     * @return TRUE - 有权限  FALSE -无权限
     */
    private String getOpenLockPermission(String deviceId, String doorNum) {
        String userId = RequestInfoUtils.getUserId();
        if (userId.equals(ADMIN)) {
            return TRUE;
        }
        String permission;
        UserAuthInfo userAuthInfo = new UserAuthInfo();
        userAuthInfo.setDeviceId(deviceId);
        userAuthInfo.setDoorId(doorNum);
        userAuthInfo.setUserId(userId);
        boolean b = authFeign.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
        if (b) {
            permission = TRUE;
        } else {
            permission = FALSE;
        }
        return permission;
    }

    /**
     * 设施主控注册的平台id
     *
     * @param deviceName   设施名称
     * @param controlParam 主控参数
     * @return 结果
     */
    private Result setPlatformId(String deviceName, ControlParam controlParam) {
        Result result = null;
        String cloudPlatform = controlParam.getCloudPlatform();
        if (!ObjectUtils.isEmpty(cloudPlatform)) {
            if (cloudPlatform.equals(OceanConnect.getCode())) {
                OceanDevice oceanDevice = new OceanDevice();
                oceanDevice.setAppId(controlParam.getProductId());
                oceanDevice.setImsi(controlParam.getImsi());
                oceanDevice.setNodeId(controlParam.getImei());
                oceanDevice.setVerifyCode(controlParam.getImei());
                result = oceanConnectFeign.registryDevice(oceanDevice);
                if (0 != result.getCode()) {
                    return result;
                }
                RegistryResult registryResult = JSON.parseObject(JSON.toJSONString(result.getData()), RegistryResult.class);
                controlParam.setPlatformId(registryResult.getDeviceId());
            } else if (cloudPlatform.equals(OneNet.getCode())) {
                //平台注册使用的设备名称
                String title = deviceName + CONNECTOR + controlParam.getHostName();
                CreateDevice createDevice = new CreateDevice(title, controlParam.getImei(), controlParam.getImsi(), controlParam.getProductId());
                result = oneNetFeign.createDevice(createDevice);
                if (0 != result.getCode()) {
                    return result;
                }
                CreateResult createResult = JSON.parseObject(JSON.toJSONString(result.getData()), CreateResult.class);
                controlParam.setPlatformId(createResult.getDeviceId());
            }
        }
        return result;
    }

    /**
     * 删除平台注册记录
     *
     * @param controlParam 主控参数
     */
    @Override
    public void deleteFromPlatForm(ControlParam controlParam) {
        String cloudPlatform = controlParam.getCloudPlatform();
        Result result = null;
        if (!ObjectUtils.isEmpty(cloudPlatform)) {
            if (cloudPlatform.equals(OceanConnect.getCode())) {
                OceanDevice oceanDevice = new OceanDevice();
                oceanDevice.setAppId(controlParam.getProductId());
                oceanDevice.setDeviceId(controlParam.getPlatformId());
                result = oceanConnectFeign.deleteDevice(oceanDevice);
                if (result.getCode() != 0) {
                    throw new FilinkSystemException("删除oceanConnect平台注册记录失败，事务回滚");
                }
            }
        } else if (cloudPlatform.equals(OneNet.getCode())) {
            DeleteDevice deleteDevice = new DeleteDevice(controlParam.getPlatformId(),
                    controlParam.getProductId());
            result = oneNetFeign.deleteDevice(deleteDevice);
            if (result.getCode() != 0) {
                throw new FilinkSystemException("删除oneNet平台注册记录失败，事务回滚");
            }

        }

    }

    /**
     * 更新主控及锁
     *
     * @param controlParam 主控信息
     * @param deviceInfo   设施信息
     * @param locks        锁信息
     */
    private void updateControlAndLock(ControlParam controlParam, DeviceInfoDto deviceInfo, List<Lock> locks) {
        String deviceId = deviceInfo.getDeviceId();
        //主控更新的约束
        checkUpdateConstraintOfControl(controlParam);
        //更新电子锁配置数据
        controlService.updateControlParam(controlParam, LogConstants.OPT_TYPE_PDA);
        //要更新的映射关系
        List<Lock> updateLockList = new ArrayList<>();
        //要新增的映射关系
        List<Lock> addLockList = new ArrayList<>();
        //要删除的映射关系
        List<Lock> abandonDoorNums = new ArrayList<>();
        //pda上传的门编号集合
        List<String> doorNumList = new ArrayList<>();
        //设施原有的映射
        List<Lock> oldLockList = queryLockByDeviceId(deviceId);
        //设施该主控下的原有映射
        List<Lock> oldLockListWithTheControl = oldLockList.stream().filter((Lock lock) -> lock.getControlId().equals(controlParam.getHostId())).collect(Collectors.toList());
        //设施其他主控下的原有映射
        List<Lock> oldLockListWithoutTheControl = new ArrayList<>(oldLockList);
        oldLockListWithoutTheControl.removeAll(oldLockListWithTheControl);
        //设施原有映射的门编号集合
        List<String> oldDoorNumList = oldLockList.stream().map(Lock::getDoorNum).collect(Collectors.toList());
        //获得当前时间戳
        long currentTime = System.currentTimeMillis();
        for (Lock lock : locks) {
            doorNumList.add(lock.getDoorNum());
            //这里pda会保证该主控所选的门编号不能是其他主控已用的门编号，否则需要使用oldLockListWithTheControl取出的门编号集合
            if (oldDoorNumList.contains(lock.getDoorNum())) {
                updateLockList.add(lock);
            } else {
                //设置排序字段
                lock.setRank(String.valueOf(currentTime++));
                lock.setLockId(NineteenUUIDUtils.uuid());
                addLockList.add(lock);
            }
        }
        for (Lock lock : oldLockListWithTheControl) {
            //pda上传的门编号集合不包括设施该主控原有映射的门编号，表示门编号对应记录要被删除
            if (doorNumList.contains(lock.getDoorNum())) {
                continue;
            }
            abandonDoorNums.add(lock);
        }

        //一个设施最多四个门   其他主控的门+该主控要更新的门+该主控要加的门
        if (oldLockListWithoutTheControl.size()+updateLockList.size() + addLockList.size() > MAX_DOOR_NUM) {
            throw new FilinkDoorMaxNumException();
        }

        //同一个设施的门名称不能重复 addDoorNameList与updateDoorNameList
        //该主控要加的映射
        List<String> addDoorNameList = addLockList.stream().map(Lock::getDoorName).collect(Collectors.toList());
        //该主控要更新的映射
        List<String> updateDoorNameList = updateLockList.stream().map(Lock::getDoorName).collect(Collectors.toList());
        //设施该主控的门名称集合
        List<String> newDoorNameList = new ArrayList<>(addDoorNameList);
        newDoorNameList.addAll(updateDoorNameList);
        //设施其他主控的门名称集合
        List<String> oldDoorNameList =  oldLockListWithoutTheControl.stream().map(Lock::getDoorName).collect(Collectors.toList());
        oldDoorNameList.retainAll(newDoorNameList);
        if (oldDoorNameList.size() > 0) {
            throw new FilinkDeviceDoorNameResuedException();
        }
        //更新已存在的映射关系
        if (updateLockList != null && updateLockList.size() > 0) {
            updateLocks(updateLockList, deviceInfo);
        }


        //删除不需要的映射关系
        if (abandonDoorNums != null && abandonDoorNums.size() > 0) {
            deleteLocks(abandonDoorNums, deviceInfo);
        }

        //更新的时候，新加的门锁映射关系，需要添加到映射表
        if (addLockList != null && addLockList.size() > 0) {

            //添加映射关系
            addLocks(addLockList, deviceInfo);
        }
    }

}
