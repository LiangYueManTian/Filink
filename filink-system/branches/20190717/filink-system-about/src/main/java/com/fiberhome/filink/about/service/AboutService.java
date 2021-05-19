package com.fiberhome.filink.about.service;

import com.fiberhome.filink.about.bean.About;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-06
 */
public interface AboutService extends IService<About> {

    /**
     * 查看关于信息
     * @return 关于信息
     */
    Result getAbout();

    /**
     * 根据id更新关于信息
     * @param about
     * @return  更新结果
     */
    Result updateAboutById(About about);
}
