package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;
import com.fiberhome.filink.lockserver.bean.QrCodeBean;
import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import com.fiberhome.filink.lockserver.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 电子锁控制层
 *
 * @author CongcaiYu
 */
@RestController
@RequestMapping("lock")
public class LockController {

    @Autowired
    private LockService lockService;


    /**
     * 批量更新电子锁状态
     *
     * @param locks 电子锁信息
     * @return 操作结果
     */
    @PutMapping("/batchUpdate")
    public Result batchUpdateLockStatus(@RequestBody List<Lock> locks) {
        if (locks == null
                || locks.size() == 0) {
            throw new FiLinkLockException("lock is null >>>>");
        }
        return lockService.batchUpdateLockStatus(locks);
    }


    /**
     * 根据主控id查询电子锁信息
     *
     * @param controlId 设施id
     * @return 结果
     */
    @GetMapping("/{controlId}")
    public Result queryLockByControlId(@PathVariable String controlId) {
        if (StringUtils.isEmpty(controlId)) {
            throw new FiLinkLockException("controlId is empty");
        }
        List<Lock> locks = lockService.queryLockByControlId(controlId);
        return ResultUtils.success(locks);
    }


    /**
     * 开锁
     *
     * @param openLockBean 开锁请求参数
     * @return 操作结果
     */
    @PostMapping("/openLock")
    public Result openLock(@RequestBody OpenLockBean openLockBean) {
        //参数校验
        openLockBean.checkOpenLockBean();
        return lockService.openLock(openLockBean);
    }

    /**
     * 开锁 for pda
     *
     * @param openLockBean 开锁请求参数
     * @return 操作结果
     */
    @PostMapping("/pda/openLock")
    public Result openLockForPda(@RequestBody OpenLockBean openLockBean) {
        //参数校验
        openLockBean.checkOpenLockBeanForPda();
        return lockService.openLockForPda(openLockBean);
    }


    /**
     * 根据二维码查询电子锁及主控信息
     *
     * @param qrCodeBean 二维码实体
     * @return 电子锁及主控信息
     */
    @PostMapping("/qrCode/query")
    public Result queryLockAndControlByQrCode(@RequestBody QrCodeBean qrCodeBean) {
        //参数校验
        qrCodeBean.checkQrCode();
        return lockService.queryLockAndControlByQrCode(qrCodeBean);
    }

    /**
     * 根据设施id查询电子锁锁具信息
     *
     * @param controlReq 参数对象
     * @return 电子锁锁具信息
     */
    @PostMapping("/deviceId/query")
    public Result queryLockAndControlByDeviceId(@RequestBody ControlReq controlReq) {
        //检验设施id
        controlReq.checkDeviceIdIsNull();
        return lockService.queryLockAndControlByDeviceId(controlReq);
    }


    /**
     * 根据二维码开锁
     *
     * @param qrCodeBean 二维码实体
     * @return 开锁结果
     */
    @PostMapping("/qrCode/open")
    public Result openLockByQrCode(@RequestBody QrCodeBean qrCodeBean) {
        //参数校验
        qrCodeBean.checkQrCode();
        return lockService.openLockByQrCode(qrCodeBean);
    }

    /**
     * 根据设施id查询门锁状态
     *
     * @param lockBean
     * @return 门锁状态
     */
    @PostMapping("/status")
    public Result queryLockStatusByDeviceId(@RequestBody OpenLockBean lockBean) {
        //参数校验
        checkDeviceIdIsNull(lockBean);
        return lockService.queryLockStatusByDeviceId(lockBean);
    }

    /**
     * 分页查询电子锁信息
     *
     * @param pageCondition 分页查询条件
     * @return
     */
    @PostMapping("/status/list")
    public Result lockListByPage(@RequestBody PageCondition pageCondition) {
        //参数校验
        if (pageCondition == null
                || StringUtils.isEmpty(pageCondition.getPageNum())
                || StringUtils.isEmpty(pageCondition.getPageSize())) {
            throw new FiLinkLockException("query params is null>>>>>>>");
        }
        return lockService.lockListByPage(pageCondition);
    }

    /**
     * 根据设施id和门编号查询电子锁信息
     *
     * @param lock 参数
     * @return 电子锁信息
     */
    @PostMapping("/feign/queryLockByDeviceIdAndDoorNum")
    public Lock queryLockByDeviceIdAndDoorNum(@RequestBody Lock lock) {
        lock.checkDeviceAndDoor();
        return lockService.queryLockByDeviceIdAndDoorNum(lock.getDeviceId(), lock.getDoorNum());
    }


    /**
     * 电子锁配置数据上传(新增/更新)
     *
     * @param control
     * @return 操作结果
     */
    @PostMapping("/operate")
    public Result operateControlAndLock(@RequestBody Control control) {
        //参数校验
        control.checkParams();
        return lockService.operateControlAndLock(control);
    }

    /**
     * 根据设施id查询电子锁
     *
     * @param deviceId 设施id
     * @return 查询结果
     */
    @GetMapping("/feign/getLock/{deviceId}")
    public List<Lock> queryLockByDeviceId(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkLockException("deviceId is Null");
        }
        return lockService.queryLockByDeviceId(deviceId);
    }

    /**
     * 根据设施id查询电子锁
     *
     * @param deviceId 设施id
     * @return 查询结果
     */
    @GetMapping("/getLock/{deviceId}")
    public Result queryLockInfoByDeviceId(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkLockException("deviceId is Null");
        }
        return ResultUtils.success(lockService.queryLockByDeviceIdToCall(deviceId));
    }

    /**
     * 查询电子锁列表
     *
     * @param deviceList 设施集合
     * @return 电子锁列表
     */
    @PostMapping("/feign/device/lockList")
    public List<Lock> lockListByDeviceIds(@RequestBody List<String> deviceList) {
        if (deviceList == null || deviceList.size() == 0) {
            throw new FiLinkLockException("deviceList is Null");
        }
        return lockService.lockListByDeviceIds(deviceList);
    }

    /**
     * 查询电子锁设施ID
     * @return 设施ID
     */
    @GetMapping("/feign/device/deviceIdListOfLock")
    public List<String> queryDeviceIdListOfLock() {
        return lockService.queryDeviceIdListOfLock();
    }

    /**
     * 根据二维码查询电子锁信息
     *
     * @param qrCode 二维码
     * @return 锁信息
     */
    @GetMapping("/feign/queryLockByQrCode/{qrCode}")
    public Lock queryLockByQrCode(@PathVariable String qrCode) {
        if (StringUtils.isEmpty(qrCode)) {
            throw new FiLinkLockException("qrCode is null >>>>");
        }
        return lockService.queryLockByQrCode(qrCode);
    }

    /**
     * 检查参数设施id是否为空
     *
     * @param lockBean
     */
    private void checkDeviceIdIsNull(OpenLockBean lockBean) {
        if (lockBean == null || StringUtils.isEmpty(lockBean.getDeviceId())) {
            throw new FiLinkLockException("deviceId is null>>>>>>>");
        }
    }

}
