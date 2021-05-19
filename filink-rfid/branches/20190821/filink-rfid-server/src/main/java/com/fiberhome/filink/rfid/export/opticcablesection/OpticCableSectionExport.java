package com.fiberhome.filink.rfid.export.opticcablesection;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 光缆段列表导出类
 *
 * @author chaofanrong@wistronits.com
 */
@Component
public class OpticCableSectionExport extends AbstractExport {

    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    @Autowired
    @Lazy
    private OpticCableSectionInfoService opticCableSectionInfoService;

    @Override
    protected List queryData(QueryCondition queryCondition) {
        List<OpticCableSectionInfoResp> resultList = opticCableSectionInfoDao.selectOpticCableSection(queryCondition);
        resultList = opticCableSectionInfoService.assemblyOpticCableSectionInfoResp(resultList);
        return resultList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        Integer count = opticCableSectionInfoDao.opticCableSectionByIdTotal(queryCondition);
        return count;
    }

}
