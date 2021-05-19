package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.userserver.bean.AuthDevice;
import com.fiberhome.filink.userserver.dao.AuthDeviceDao;
import com.fiberhome.filink.userserver.service.AuthDeviceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 统一授权临时授权和设施对应的中间表 服务实现类
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@Service
public class AuthDeviceServiceImpl extends ServiceImpl<AuthDeviceDao, AuthDevice> implements AuthDeviceService {

}
