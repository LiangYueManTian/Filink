package com.fiberhome.filink.about.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.about.bean.About;

/**
 * <p>
 * 关于 Mapper 接口
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-06
 */
public interface AboutDao extends BaseMapper<About> {
    /**
     * 查看关于信息
     *
     * @return 关于信息
     */
    About getAbout();


    /**
     * 根据id更新关于信息的
     *
     * @param about 关于实体
     * @return 更新结果
     */
    Integer updateAboutById(About about);


}
