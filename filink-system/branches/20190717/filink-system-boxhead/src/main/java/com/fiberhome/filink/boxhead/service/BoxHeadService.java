package com.fiberhome.filink.boxhead.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.boxhead.bean.BoxHead;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wanzhao zhang
 * @since 2019-05-21
 */
public interface BoxHeadService extends IService<BoxHead> {
    /**
     * 根据用户id查询表头栏设置
     *
     * @return 查询结果
     */
    List<BoxHead> queryBoxHead();

    /**
     * 保存表头设置
     *
     * @param boxHead
     * @return 操作结果
     */
    Result saveBoxHead(@RequestBody BoxHead boxHead);
}
