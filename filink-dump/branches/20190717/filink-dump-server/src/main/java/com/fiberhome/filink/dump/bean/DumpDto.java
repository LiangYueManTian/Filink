package com.fiberhome.filink.dump.bean;

import lombok.Data;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author Administrator
 */
@Data
public class DumpDto {

    /**
     * 转储的数据
     */
    private List dumpDataList;

    /**
     * 转储的数据量
     */
    private Integer dumpDataNum;

    /**
     * 转储的位置
     */
    private String dumpPlace;

    /**
     * 后面删除mongo数据的条件
     */
    private Query query;

    /**
     * 删除的类
     */
    private Class clazz;

    public DumpDto(List dumpDataList, Integer dumpDataNum, String dumpPlace, Query query, Class clazz) {
        this.dumpDataList = dumpDataList;
        this.dumpDataNum = dumpDataNum;
        this.dumpPlace = dumpPlace;
        this.query = query;
        this.clazz = clazz;
    }
}
