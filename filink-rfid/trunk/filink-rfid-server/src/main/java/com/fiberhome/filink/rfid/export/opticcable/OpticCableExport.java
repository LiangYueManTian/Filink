package com.fiberhome.filink.rfid.export.opticcable;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableInfoDao;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 光缆列表导出类
 *
 * @author chaofanrong@wistronits.com
 */
@Component
public class OpticCableExport extends AbstractExport {

    @Autowired
    private OpticCableInfoDao opticCableInfoDao;

    @Override
    protected List queryData(QueryCondition queryCondition) {
        List<OpticCableInfoResp> resultList = opticCableInfoDao.opticCableListByPage(queryCondition);
        return resultList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        Integer count = opticCableInfoDao.opticCableListTotal(queryCondition);
        return count;
    }

}
