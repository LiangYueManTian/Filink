package com.fiberhome.filink.alarmhistoryserver.test;


import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistoryEs;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Component
@Slf4j
public class DtoEnd {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RestHighLevelClient client;

    private static final int AN_INT = 10001;

    /**
     * 设施
     * @return 设施
     */
    private List<String[]> addDeviceData() {
        List<String[]> deviceList = new ArrayList<>();
        String[] deviceStr1 =  {"1LiimDp7mKAsyOX6EDh", "告警测试数据人井001",  "030", "湖北省武汉市武昌区东安路11号"};
        String[] deviceStr2 =  {"yJ4NXy578EoL10F7yzo", "告警测试数据人井002",  "030", "湖北省武汉市江汉区新华路245号"};
        String[] deviceStr3 =  {"0AOYSrbIKZP2io0OOgu", "告警测试数据人井003",  "030", "湖北省武汉市洪山区珞瑜路152号"};
        String[] deviceStr4 =  {"iZtz6aKCBDVCabvWNS3", "告警测试数据人井004",  "030", "湖北省武汉市江夏区华泰街"};
        String[] deviceStr5 =  {"D4KKR5zU2sPkN9KKprG", "告警测试数据人井005",  "030", "湖北省武汉市青山区和平大道1240"};
        String[] deviceStr6 =  {"IpFbsUBOtzCeBS7bufL", "告警测试数据光交001",  "001", "湖北省武汉市武昌区中南商圈民主路741号"};
        String[] deviceStr7 =  {"ift1qjN4SKXpVuwyBTJ", "告警测试数据光交002",  "001", "湖北省武汉市江汉区淮海路"};
        String[] deviceStr8 =  {"tkofeiJQhyQix2qXWzU", "告警测试数据光交003",  "001", "湖北省武汉市洪山区康福路47号"};
        String[] deviceStr9 =  {"TqhtVpV9XV5k0Ppz5JE", "告警测试数据光交004",  "001", "湖北省武汉市江夏区黄家湖东路"};
        String[] deviceStr10 = {"bEKB4diEmO5tdQqP9iB", "告警测试数据光交005",  "001", "湖北省武汉市青山区辽阳街4号"};
        String[] deviceStr11 = {"Niaczod0VGtjsQLjde4", "告警测试数据室外柜001", "210", "湖北省武汉市武昌区星海路"};
        String[] deviceStr12 = {"HxIuufJWPD0K9jKn2ie", "告警测试数据室外柜002", "210", "湖北省武汉市江汉区汉口站东路"};
        String[] deviceStr13 = {"gM9miA780ir8vX2t4Ox", "告警测试数据室外柜003", "210", "湖北省武汉市洪山区民族大道165号"};
        String[] deviceStr14 = {"nD0Og05ZddLYbvfkQYH", "告警测试数据室外柜004", "210", "湖北省武汉市江夏区江夏大道42"};
        String[] deviceStr15 = {"MlWagmgfuaTJV9n3XAR", "告警测试数据室外柜005", "210", "湖北省武汉市青山区工业路7"};
        deviceList.add(deviceStr1);
        deviceList.add(deviceStr2);
        deviceList.add(deviceStr3);
        deviceList.add(deviceStr4);
        deviceList.add(deviceStr5);
        deviceList.add(deviceStr6);
        deviceList.add(deviceStr7);
        deviceList.add(deviceStr8);
        deviceList.add(deviceStr9);
        deviceList.add(deviceStr10);
        deviceList.add(deviceStr11);
        deviceList.add(deviceStr12);
        deviceList.add(deviceStr13);
        deviceList.add(deviceStr14);
        deviceList.add(deviceStr15);
        return deviceList;
    }
    /**
     * 区域
     * @return 区域
     */
    private List<String[]> addAreaData() {
        List<String[]> areaList = new ArrayList<>();
        String[] areaStr1 =  {"4K59ul8KAVcOmFbhqin", "告警测试数据区域001"};
        String[] areaStr2 =  {"neD8yCXPaajpxWZQYNr", "告警测试数据区域002"};
        String[] areaStr3 =  {"PdIF1ZmmlGkm9STOt7N", "告警测试数据区域003"};
        String[] areaStr4 =  {"Fz0VfKbB89GXtWNjgaF", "告警测试数据区域004"};
        String[] areaStr5 =  {"0bF1QwLSRA0xIA7GW6o", "告警测试数据区域005"};
        String[] areaStr6 =  {"4K59ul8KAVcOmFbhqin", "告警测试数据区域001"};
        String[] areaStr7 =  {"neD8yCXPaajpxWZQYNr", "告警测试数据区域002"};
        String[] areaStr8 =  {"PdIF1ZmmlGkm9STOt7N", "告警测试数据区域003"};
        String[] areaStr9 =  {"Fz0VfKbB89GXtWNjgaF", "告警测试数据区域004"};
        String[] areaStr10 = {"0bF1QwLSRA0xIA7GW6o", "告警测试数据区域005"};
        String[] areaStr11 = {"4K59ul8KAVcOmFbhqin", "告警测试数据区域001"};
        String[] areaStr12 = {"neD8yCXPaajpxWZQYNr", "告警测试数据区域002"};
        String[] areaStr13 = {"PdIF1ZmmlGkm9STOt7N", "告警测试数据区域003"};
        String[] areaStr14 = {"Fz0VfKbB89GXtWNjgaF", "告警测试数据区域004"};
        String[] areaStr15 = {"0bF1QwLSRA0xIA7GW6o", "告警测试数据区域005"};
        areaList.add(areaStr1);
        areaList.add(areaStr2);
        areaList.add(areaStr3);
        areaList.add(areaStr4);
        areaList.add(areaStr5);
        areaList.add(areaStr6);
        areaList.add(areaStr7);
        areaList.add(areaStr8);
        areaList.add(areaStr9);
        areaList.add(areaStr10);
        areaList.add(areaStr11);
        areaList.add(areaStr12);
        areaList.add(areaStr13);
        areaList.add(areaStr14);
        areaList.add(areaStr15);
        return areaList;
    }
    /**
     * 责任单位
     * @return 责任单位
     */
    private List<String[]> addDepartmentData() {
        List<String[]> departmentList = new ArrayList<>();
        String[] departmentStr1 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr2 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr3 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr4 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr5 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr6 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr7 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr8 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr9 =  {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr10 = {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr11 = {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr12 = {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr13 = {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr14 = {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        String[] departmentStr15 = {"JZZxrcf36VdttblWqQx", "告警测试数据单位"};
        departmentList.add(departmentStr1);
        departmentList.add(departmentStr2);
        departmentList.add(departmentStr3);
        departmentList.add(departmentStr4);
        departmentList.add(departmentStr5);
        departmentList.add(departmentStr6);
        departmentList.add(departmentStr7);
        departmentList.add(departmentStr8);
        departmentList.add(departmentStr9);
        departmentList.add(departmentStr10);
        departmentList.add(departmentStr11);
        departmentList.add(departmentStr12);
        departmentList.add(departmentStr13);
        departmentList.add(departmentStr14);
        departmentList.add(departmentStr15);
        return departmentList;
    }
    /**
     * 告警名称
     * @return 告警名称
     */
    private List<String[]> addAlarmData() {
        List<String[]> alarmList = new ArrayList<>();
        String[] alarmStr1 =  {"001", "撬门", "pryDoor"};
        String[] alarmStr2 =  {"002", "撬锁", "pryLock"};
        String[] alarmStr3 =  {"003", "湿度", "humidity"};
        String[] alarmStr4 =  {"004", "高温", "highTemperature"};
        String[] alarmStr5 =  {"005", "低温", "lowTemperature"};
        String[] alarmStr6 =  {"006", "通信中断", "communicationInterrupt"};
        String[] alarmStr7 =  {"007", "水浸", "leach"};
        String[] alarmStr8 =  {"008", "未关门", "notClosed"};
        String[] alarmStr10 = {"009", "未关锁", "unLock"};
        String[] alarmStr11 = {"010", "倾斜", "lean"};
        String[] alarmStr12 = {"011", "震动", "shake"};
        String[] alarmStr13 = {"012", "电量", "electricity"};
        String[] alarmStr14 = {"013", "非法开门", "violenceClose"};
        String[] alarmStr15 = {"016", "应急开锁告警", "emergencyLock"};
        String[] alarmStr16 = {"017", "非法开盖（内盖）", "IllegalOpeningInnerCover"};
        alarmList.add(alarmStr1);
        alarmList.add(alarmStr2);
        alarmList.add(alarmStr3);
        alarmList.add(alarmStr4);
        alarmList.add(alarmStr5);
        alarmList.add(alarmStr6);
        alarmList.add(alarmStr7);
        alarmList.add(alarmStr8);
        alarmList.add(alarmStr10);
        alarmList.add(alarmStr11);
        alarmList.add(alarmStr12);
        alarmList.add(alarmStr13);
        alarmList.add(alarmStr14);
        alarmList.add(alarmStr15);
        alarmList.add(alarmStr16);
        return alarmList;
    }
    /**
     * 时间
     * @return 时间
     */
    private List<Long[]> addTimeData() {
        List<Long[]> timeList = new ArrayList<>();
        Long[] timeStr1 =  {1564628771000L, 1564653971000L, 1L};
        Long[] timeStr2 =  {1564715171000L, 1564736771000L, 1L};
        Long[] timeStr3 =  {1564797971000L, 1564833971000L, 8L};
        Long[] timeStr4 =  {1564628771000L, 1564736771000L, 2L};
        Long[] timeStr5 =  {1564715171000L, 1564833971000L, 1L};
        Long[] timeStr6 =  {1564628771000L, 1565057171000L, 30L};
        Long[] timeStr7 =  {1564628771000L, 1565402771000L, 61L};
        Long[] timeStr8 =  {1564628771000L, 1565402771000L, 2L};
        Long[] timeStr9 =  {1564797971000L, 1565402771000L, 6L};
        Long[] timeStr10 = {1564628771000L, 1565057171000L, 3L};
        Long[] timeStr11 = {1564628771000L, 1565661971000L, 10L};
        timeList.add(timeStr1);
        timeList.add(timeStr2);
        timeList.add(timeStr3);
        timeList.add(timeStr4);
        timeList.add(timeStr5);
        timeList.add(timeStr6);
        timeList.add(timeStr7);
        timeList.add(timeStr8);
        timeList.add(timeStr9);
        timeList.add(timeStr10);
        timeList.add(timeStr11);
        return timeList;
    }

    public void addAlarmHistory() {
        List<AlarmHistory> list = new ArrayList<>();
        DtoEnd alarmData = new DtoEnd();
        Random r = new Random();
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 1; i < AN_INT; i++) {
            AlarmHistory alarmHistory = new AlarmHistory();
            //id >uuid 19位
            alarmHistory.setId(NineteenUUIDUtils.uuid());

            setDeviceAreaDepartment(alarmData, r, alarmHistory);

            List<String[]> alarmList = alarmData.addAlarmData();
            int rCount;
            if ("030".equals(alarmHistory.getAlarmSourceTypeId())) {
                rCount = r.nextInt(alarmList.size());
            } else {
                rCount = r.nextInt(alarmList.size() - 1);
            }
            alarmHistory.setAlarmNameId(alarmList.get(rCount)[0]);
            alarmHistory.setAlarmName(alarmList.get(rCount)[1]);
            alarmHistory.setAlarmCode(alarmList.get(rCount)[2]);

            List<Long[]> timeList = alarmData.addTimeData();
            rCount = r.nextInt(timeList.size());
            alarmHistory.setAlarmBeginTime(timeList.get(rCount)[0]);
            alarmHistory.setAlarmNearTime(timeList.get(rCount)[1]);
            alarmHistory.setAlarmSystemTime(timeList.get(rCount)[0]);
            alarmHistory.setAlarmSystemNearTime(timeList.get(rCount)[1]);

            alarmHistory.setTrapOid("1");
            alarmHistory.setAlarmContent("方法");
            alarmHistory.setAlarmType(1);
            alarmHistory.setPrompt("1");
            alarmHistory.setIsOrder(r.nextBoolean());

            alarmHistory.setAlarmFixedLevel(String.valueOf(r.nextInt(4) + 1));
            alarmHistory.setAlarmHappenCount(r.nextInt(9) + 1);
            alarmHistory.setAlarmCleanType(1);

            long currentTimeMillis = System.currentTimeMillis();
            alarmHistory.setAlarmCleanStatus(r.nextInt(1) + 1);
            alarmHistory.setAlarmCleanTime(currentTimeMillis);
            alarmHistory.setAlarmCleanPeopleId("1");
            alarmHistory.setAlarmCleanPeopleNickname("admin");
            alarmHistory.setAlarmConfirmStatus(1);
            alarmHistory.setAlarmConfirmTime(currentTimeMillis);
            alarmHistory.setAlarmConfirmPeopleId("1");
            alarmHistory.setAlarmConfirmPeopleNickname("admin");
            alarmHistory.setExtraMsg("有新的告警信息，请尽快处理");
            alarmHistory.setAlarmProcessing("有新的告警信息，请尽快处理");
            alarmHistory.setRemark("有新的告警信息，请尽快处理");

            AlarmHistoryEs alarmHistoryEs = transToAlarmHistoryEs(alarmHistory);
            IndexRequest indexRequest = new IndexRequest("filink_alarm", "alarm_history", alarmHistoryEs.getId());
            indexRequest.source(JSONObject.toJSONString(alarmHistoryEs), XContentType.JSON);
            bulkRequest.add(indexRequest);

            list.add(alarmHistory);
        }
        mongoTemplate.insertAll(list);
        log.info("*****插入mongo数据****");
        try {
            client.bulk(bulkRequest);
            log.info("*****插入es数据****");
        } catch (IOException e) {
            log.error("batch insert elasticsearch error", e);
        }
    }

    public void addAlarmCurrent() {
        List<AlarmCurrent> list = new ArrayList<>();
        DtoEnd alarmData = new DtoEnd();
        Random r = new Random();
        for (int i = 1; i < AN_INT; i++) {
            AlarmCurrent alarmCurrent = new AlarmCurrent();
            //id >uuid 19位
            alarmCurrent.setId(NineteenUUIDUtils.uuid());

            setDeviceAreaDepartment2(alarmData, r, alarmCurrent);

            List<String[]> alarmList = alarmData.addAlarmData();
            int rCount;
            if ("030".equals(alarmCurrent.getAlarmSourceTypeId())) {
                rCount = r.nextInt(alarmList.size());
            } else {
                rCount = r.nextInt(alarmList.size() - 1);
            }
            alarmCurrent.setAlarmNameId(alarmList.get(rCount)[0]);
            alarmCurrent.setAlarmName(alarmList.get(rCount)[1]);
            alarmCurrent.setAlarmCode(alarmList.get(rCount)[2]);

            List<Long[]> timeList = alarmData.addTimeData();
            rCount = r.nextInt(timeList.size());
            alarmCurrent.setAlarmBeginTime(timeList.get(rCount)[0]);
            alarmCurrent.setAlarmNearTime(timeList.get(rCount)[1]);
            alarmCurrent.setAlarmSystemTime(timeList.get(rCount)[0]);
            alarmCurrent.setAlarmSystemNearTime(timeList.get(rCount)[1]);

            alarmCurrent.setTrapOid("1");
            alarmCurrent.setAlarmContent("方法");
            alarmCurrent.setAlarmType(1);
            alarmCurrent.setPrompt("1");
            alarmCurrent.setIsOrder(r.nextBoolean());

            alarmCurrent.setAlarmFixedLevel(String.valueOf(r.nextInt(4) + 1));
            alarmCurrent.setAlarmHappenCount(r.nextInt(9) + 1);
            alarmCurrent.setAlarmCleanType(1);

            alarmCurrent.setAlarmCleanStatus(3);
            alarmCurrent.setAlarmCleanTime(null);
            alarmCurrent.setAlarmCleanPeopleId(null);
            alarmCurrent.setAlarmCleanPeopleNickname(null);
            alarmCurrent.setAlarmConfirmStatus(2);
            alarmCurrent.setAlarmConfirmTime(null);
            alarmCurrent.setAlarmConfirmPeopleId(null);
            alarmCurrent.setAlarmConfirmPeopleNickname(null);
            alarmCurrent.setExtraMsg("有新的告警信息，请尽快处理");
            alarmCurrent.setAlarmProcessing("有新的告警信息，请尽快处理");
            alarmCurrent.setRemark("有新的告警信息，请尽快处理");
            list.add(alarmCurrent);
        }
        mongoTemplate.insertAll(list);
        log.info("*****插入mongo数据****");
    }

    private void setDeviceAreaDepartment2(DtoEnd alarmData, Random r, AlarmCurrent alarmCurrent) {
        List<String[]> deviveList = alarmData.addDeviceData();
        int rCount = r.nextInt(deviveList.size());
        alarmCurrent.setAlarmSource(deviveList.get(rCount)[0]);
        alarmCurrent.setAlarmObject(deviveList.get(rCount)[1]);
        alarmCurrent.setAlarmSourceTypeId(deviveList.get(rCount)[2]);
        alarmCurrent.setAddress(deviveList.get(rCount)[3]);

        List<String[]> departmentList = alarmData.addDepartmentData();
        alarmCurrent.setResponsibleDepartmentId(departmentList.get(rCount)[0]);
        alarmCurrent.setResponsibleDepartment(departmentList.get(rCount)[1]);

        List<String[]> areaList = alarmData.addAreaData();
        alarmCurrent.setAreaId(areaList.get(rCount)[0]);
        alarmCurrent.setAreaName(areaList.get(rCount)[1]);
    }




    private void setDeviceAreaDepartment(DtoEnd alarmData, Random r, AlarmHistory alarmHistory) {
        List<String[]> deviveList = alarmData.addDeviceData();
        int rCount = r.nextInt(deviveList.size());
        alarmHistory.setAlarmSource(deviveList.get(rCount)[0]);
        alarmHistory.setAlarmObject(deviveList.get(rCount)[1]);
        alarmHistory.setAlarmSourceTypeId(deviveList.get(rCount)[2]);
        alarmHistory.setAddress(deviveList.get(rCount)[3]);

        List<String[]> areaList = alarmData.addAreaData();
        alarmHistory.setAreaId(areaList.get(rCount)[0]);
        alarmHistory.setAreaName(areaList.get(rCount)[1]);

        List<String[]> departmentList = alarmData.addDepartmentData();
        alarmHistory.setResponsibleDepartmentId(departmentList.get(rCount)[0]);
        alarmHistory.setResponsibleDepartment(departmentList.get(rCount)[1]);
    }

    /**
     * 历史告警转换es
     * @param alarmHistory 历史告警
     * @return es历史告警
     */
    private AlarmHistoryEs transToAlarmHistoryEs(AlarmHistory alarmHistory) {
        AlarmHistoryEs alarmHistoryEs = new AlarmHistoryEs();
        alarmHistoryEs.setId(alarmHistory.getId());
        alarmHistoryEs.setTrapOid(alarmHistory.getTrapOid());
        alarmHistoryEs.setAlarmName(alarmHistory.getAlarmName());
        alarmHistoryEs.setAlarmNameId(alarmHistory.getAlarmNameId());
        alarmHistoryEs.setAlarmCode(alarmHistory.getAlarmCode());
        alarmHistoryEs.setAlarmContent(alarmHistory.getAlarmContent());
        alarmHistoryEs.setAlarmType(alarmHistory.getAlarmType());
        alarmHistoryEs.setAlarmSource(alarmHistory.getAlarmSource());
        alarmHistoryEs.setAlarmSourceType(alarmHistory.getAlarmSourceType());
        alarmHistoryEs.setAlarmSourceTypeId(alarmHistory.getAlarmSourceTypeId());
        alarmHistoryEs.setAreaId(alarmHistory.getAreaId());
        alarmHistoryEs.setAreaName(alarmHistory.getAreaName());
        alarmHistoryEs.setIsOrder(alarmHistory.getIsOrder());
        alarmHistoryEs.setAddress(alarmHistory.getAddress());
        alarmHistoryEs.setAlarmFixedLevel(alarmHistory.getAlarmFixedLevel());
        alarmHistoryEs.setAlarmObject(alarmHistory.getAlarmObject());
        alarmHistoryEs.setResponsibleDepartmentId(alarmHistory.getResponsibleDepartmentId());
        alarmHistoryEs.setResponsibleDepartment(alarmHistory.getResponsibleDepartment());
        alarmHistoryEs.setPrompt(alarmHistory.getPrompt());
        alarmHistoryEs.setAlarmBeginTime(alarmHistory.getAlarmBeginTime());
        alarmHistoryEs.setAlarmNearTime(alarmHistory.getAlarmNearTime());
        alarmHistoryEs.setAlarmSystemTime(alarmHistory.getAlarmSystemTime());
        alarmHistoryEs.setAlarmSystemNearTime(alarmHistory.getAlarmSystemNearTime());
        alarmHistoryEs.setAlarmContinousTime(alarmHistory.getAlarmContinousTime());
        alarmHistoryEs.setAlarmHappenCount(alarmHistory.getAlarmHappenCount());
        alarmHistoryEs.setAlarmCleanStatus(alarmHistory.getAlarmCleanStatus());
        alarmHistoryEs.setAlarmCleanTime(alarmHistory.getAlarmCleanTime());
        alarmHistoryEs.setAlarmCleanType(alarmHistory.getAlarmCleanType());
        alarmHistoryEs.setAlarmCleanPeopleId(alarmHistory.getAlarmCleanPeopleId());
        alarmHistoryEs.setAlarmCleanPeopleNickname(alarmHistory.getAlarmCleanPeopleNickname());
        alarmHistoryEs.setAlarmConfirmStatus(alarmHistory.getAlarmConfirmStatus());
        alarmHistoryEs.setAlarmConfirmTime(alarmHistory.getAlarmConfirmTime());
        alarmHistoryEs.setAlarmConfirmPeopleId(alarmHistory.getAlarmConfirmPeopleId());
        alarmHistoryEs.setAlarmConfirmPeopleNickname(alarmHistory.getAlarmConfirmPeopleNickname());
        alarmHistoryEs.setExtraMsg(alarmHistory.getExtraMsg());
        alarmHistoryEs.setAlarmProcessing(alarmHistory.getAlarmProcessing());
        alarmHistoryEs.setRemark(alarmHistory.getRemark());
        alarmHistoryEs.setDoorNumber(alarmHistory.getDoorNumber());
        alarmHistoryEs.setDoorName(alarmHistory.getDoorName());
        alarmHistoryEs.setIsPicture(alarmHistory.getIsPicture());
        alarmHistoryEs.setControlId(alarmHistory.getControlId());
        return alarmHistoryEs;
    }
}
