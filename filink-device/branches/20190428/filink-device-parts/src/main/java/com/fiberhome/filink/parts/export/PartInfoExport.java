package com.fiberhome.filink.parts.export;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.parts.dao.PartInfoDao;
import com.fiberhome.filink.parts.dto.PartInfoDto;
import com.fiberhome.filink.parts.service.PartInfoService;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配件列表导出类
 *
 * @Author: zl
 * @Date: 2019/4/9 11:21
 * @Description: com.fiberhome.filink.parts.export
 * @version: 1.0
 */
@Component
public class PartInfoExport extends AbstractExport {
    @Autowired
    private PartInfoService partInfoService;

    @Autowired
    private PartInfoDao partInfoDao;

    @Autowired
    private UserFeign userFeign;

    @Override
    protected List queryData(QueryCondition queryCondition) {
        //获取用户
        Object userObject = userFeign.queryUserInfoById(ExportApiUtils.getCurrentUserId()).getData();
        User user = PartInfoService.convertObjectToUser(userObject);
        Result result = partInfoService.queryListByPage(queryCondition, user);
        List<PartInfoDto> dtoList = (List<PartInfoDto>) result.getData();
        return dtoList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        //获取用户
        Object userObject = userFeign.queryUserInfoById(RequestInfoUtils.getUserId()).getData();
        User user = PartInfoService.convertObjectToUser(userObject);
        return partInfoService.queryPartsCount(queryCondition, user);
    }

}
