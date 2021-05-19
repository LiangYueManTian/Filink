package com.fiberhome.filink.about.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.about.bean.About;
import com.fiberhome.filink.about.constant.AboutI18n;
import com.fiberhome.filink.about.dao.AboutDao;
import com.fiberhome.filink.about.exception.AboutSystemException;
import com.fiberhome.filink.about.service.AboutService;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-06
 */
@Service
public class AboutServiceImpl extends ServiceImpl<AboutDao, About> implements AboutService {
    /**
     * 注入关于持久层
     */
    @Autowired
    private AboutDao aboutDao;

    /**
     * 用户ID请求头
     */
    private static final String REQUEST_USER = "userId";

    /**
     * 查看关于信息
     *
     * @return 关于信息
     */
    @Override
    public Result getAbout() {
        //获取关于数据
        About about = aboutDao.getAbout();
        if (StringUtils.isEmpty(about)) {
            //查询不到，说明数据库没有初始化关于信息,报系统数据异常
            throw new AboutSystemException();
        }
        return ResultUtils.success(about);
    }


    /**
     * 根据id更新关于信息的
     *
     * @param about
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAboutById(About about) {
        //设置更新人及更新时间
        about.setUpdateUser(RequestInfoUtils.getUserId());
        about.setUpdateTime(Long.toString(System.currentTimeMillis()));
        //更新前先查询
        About about1 = aboutDao.getAbout();
        if (StringUtils.isEmpty(about1)) {
            //查不到报错，说明数据库已没有该条关于数据,报系统数据异常
            throw new AboutSystemException();
        }
        //更新关于
        Integer result = aboutDao.updateAboutById(about);
        if (null != result && result == 1) {
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AboutI18n.UPDATE_ABOUT_SUCCESS));
        } else {
            throw new AboutSystemException();
        }
    }

}
