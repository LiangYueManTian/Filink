package com.fiberhome.filink.boxhead.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.boxhead.bean.BoxHead;
import com.fiberhome.filink.boxhead.constant.BoxHeadI18n;
import com.fiberhome.filink.boxhead.constant.BoxHeadResultCode;
import com.fiberhome.filink.boxhead.dao.BoxHeadDao;
import com.fiberhome.filink.boxhead.service.BoxHeadService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wanzhao zhang
 * @since 2019-05-21
 */
@Service
public class BoxHeadServiceImpl extends ServiceImpl<BoxHeadDao, BoxHead> implements BoxHeadService {
    /**
     * 注入逻辑层
     */
    @Autowired
    private BoxHeadDao boxHeadDao;

    @Autowired
    private UserFeign userFeign;

    /**
     * 字段user_id
     */
    private static final String USER_ID = "user_id";

    /**
     * 根据用户id查询表头栏设置
     *
     * @return 查询结果
     */
    @Override
    public List<BoxHead> queryBoxHead() {
        //用户id
        String userId = RequestInfoUtils.getUserId();
        //构建查询条件
        EntityWrapper<BoxHead> wrapper = new EntityWrapper<>();
        wrapper.eq(USER_ID, userId);
        return boxHeadDao.selectList(wrapper);
    }

    /**
     * 保存表头设置
     *
     * @param boxHead
     * @return 操作结果
     */
    @Override
    public Result saveBoxHead(BoxHead boxHead) {
        //赋值userId
        boxHead.setUserId(RequestInfoUtils.getUserId());
        //根据用户和菜单查询一条表头栏
        BoxHead boxHeadDto = boxHeadDao.selectOneByUserAndMenu(boxHead);
        if (boxHeadDto == null) {
            //没有查到，说明数据库没有这个表头栏的记录，执行新增
            boxHead.setId(NineteenUUIDUtils.uuid());
            boxHeadDao.insert(boxHead);
        } else {
            //查到，说明数据有这个表头栏的记录，执行更新
            boxHeadDto.setCustom(boxHead.getCustom());
            boxHeadDao.updateById(boxHeadDto);
        }
        return ResultUtils.success(BoxHeadResultCode.SUCCESS, I18nUtils.getSystemString(BoxHeadI18n.BOX_HEAD_SAVE_SUCCESS));
    }
}
