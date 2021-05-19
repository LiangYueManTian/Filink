package com.fiberhome.filink.fdevice.export;


import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dto.AreaInfoDto;
import com.fiberhome.filink.fdevice.service.area.impl.AreaInfoServiceImpl;
import com.fiberhome.filink.fdevice.utils.ComparatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * <p>
 * 区域列表导出
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@Component
public class AreaExport extends AbstractExport {
    /**
     * 注入dao
     */
    @Autowired
    private AreaInfoDao areaInfoDao;
    /**
     * 注入areaInfoService实现类
     */
    @Autowired
    private AreaInfoServiceImpl areaInfoService;

    /**
     * 数据查询
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        AreaInfoServiceImpl.tlDepartmentList.set(areaInfoService.getDepartmentList());
        AreaInfoDto areaInfoDto = buildQuery(queryCondition);
        PageCondition pageCondition = queryCondition.getPageCondition();
        Integer pageSize = pageCondition.getPageSize();
        areaInfoDto.setPageSize(pageSize);
        areaInfoDto.setBeginNum(pageCondition.getBeginNum());
        List<AreaInfo> areaInfos = areaInfoDao.queryAreaListByItem(areaInfoDto);
        List<AreaInfoTree> areaInfoTreeList = areaInfoService.setDeptIdByAreaIds(areaInfos);
        for (AreaInfoTree areaInfoTree : areaInfoTreeList) {
            //设置单位名称
            areaInfoTree.setAccountabilityUnitName(new StringBuilder());
            areaInfoService.setAccountabilityUnitName(areaInfoTree.getAccountabilityUnit(), areaInfoTree.getAccountabilityUnitName());
        }
        //排序
        ComparatorUtil comparatorUtil = new ComparatorUtil();
        Collections.sort(areaInfoTreeList, comparatorUtil);
        AreaInfoServiceImpl.tlDepartmentList.remove();
        return areaInfoTreeList;
    }

    /**
     * 查询count
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        AreaInfoDto areaInfoDto = buildQuery(queryCondition);
        List<AreaInfo> areaInfoList = areaInfoDao.queryAreaListByItemCount(areaInfoDto);
        return areaInfoList.size();
    }

    /**
     * 构建查询条件
     *
     * @param queryCondition 查询条件
     * @return
     */
    private AreaInfoDto buildQuery(QueryCondition queryCondition) {
        return (AreaInfoDto) (queryCondition.getBizCondition());
    }
}
