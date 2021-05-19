package com.fiberhome.filink.alarmcurrentserver.bean;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class BeanTest {

    @Test
    public void AlarmFilterTest() {
        AlarmFilter alarmFilter = new AlarmFilter();
        AlarmFilter alarmFilter1 = new AlarmFilter();
        alarmFilter.setId(alarmFilter.getId());
        alarmFilter.setTrapOid(alarmFilter.getTrapOid());
        alarmFilter.setAlarmName(alarmFilter.getAlarmName());
        alarmFilter.setAlarmNameId(alarmFilter.getAlarmNameId());
        alarmFilter.setAlarmCode(alarmFilter.getAlarmCode());
        alarmFilter.setAlarmContent(alarmFilter.getAlarmContent());
        alarmFilter.setAlarmType(alarmFilter.getAlarmType());
        alarmFilter.setAlarmSource(alarmFilter.getAlarmSource());
        alarmFilter.setAlarmSourceType(alarmFilter.getAlarmSourceType());
        alarmFilter.setAlarmSourceTypeId(alarmFilter.getAlarmSourceTypeId());
        alarmFilter.setAreaId(alarmFilter.getAreaId());
        alarmFilter.setAreaName(alarmFilter.getAreaName());
        alarmFilter.setIsOrder(alarmFilter.getIsOrder());
        alarmFilter.setAddress(alarmFilter.getAddress());
        alarmFilter.setAlarmFixedLevel(alarmFilter.getAlarmFixedLevel());
        alarmFilter.setAlarmObject(alarmFilter.getAlarmObject());
        alarmFilter.setResponsibleDepartmentId(alarmFilter.getResponsibleDepartmentId());
        alarmFilter.setResponsibleDepartment(alarmFilter.getResponsibleDepartment());
        alarmFilter.setPrompt(alarmFilter.getPrompt());
        alarmFilter.setAlarmBeginTime(alarmFilter.getAlarmBeginTime());
        alarmFilter.setAlarmNearTime(alarmFilter.getAlarmNearTime());
        alarmFilter.setAlarmSystemTime(alarmFilter.getAlarmSystemTime());
        alarmFilter.setAlarmSystemNearTime(alarmFilter.getAlarmSystemNearTime());
        alarmFilter.setAlarmContinousTime(alarmFilter.getAlarmContinousTime());
        alarmFilter.setAlarmHappenCount(alarmFilter.getAlarmHappenCount());
        alarmFilter.setAlarmCleanStatus(alarmFilter.getAlarmCleanStatus());
        alarmFilter.setAlarmCleanTime(alarmFilter.getAlarmCleanTime());
        alarmFilter.setAlarmCleanType(alarmFilter.getAlarmCleanType());
        alarmFilter.setAlarmCleanPeopleId(alarmFilter.getAlarmCleanPeopleId());
        alarmFilter.setAlarmCleanPeopleNickname(alarmFilter.getAlarmCleanPeopleNickname());
        alarmFilter.setAlarmConfirmStatus(alarmFilter.getAlarmConfirmStatus());
        alarmFilter.setAlarmConfirmTime(alarmFilter.getAlarmConfirmTime());
        alarmFilter.setAlarmConfirmPeopleId(alarmFilter.getAlarmConfirmPeopleId());
        alarmFilter.setAlarmConfirmPeopleNickname(alarmFilter.getAlarmConfirmPeopleNickname());
        alarmFilter.setExtraMsg(alarmFilter.getExtraMsg());
        alarmFilter.setAlarmProcessing(alarmFilter.getAlarmProcessing());
        alarmFilter.setRemark(alarmFilter.getRemark());
        alarmFilter.setDoorNumber(alarmFilter.getDoorNumber());
        alarmFilter.setDoorName(alarmFilter.getDoorName());
        alarmFilter.setIsPicture(alarmFilter.getIsPicture());
        alarmFilter.setControlId(alarmFilter.getControlId());
        Assert.assertNotNull(alarmFilter);
        Assert.assertNotNull(alarmFilter1);
    }

    @Test
    public void AlarmNameStatisticsReqTest() {
        AlarmNameStatisticsReq alarmNameStatisticsReq = new AlarmNameStatisticsReq();
        alarmNameStatisticsReq.setAreaId(alarmNameStatisticsReq.getAreaId());
        alarmNameStatisticsReq.setPryDoor(alarmNameStatisticsReq.getPryDoor());
        alarmNameStatisticsReq.setPryLock(alarmNameStatisticsReq.getPryLock());
        alarmNameStatisticsReq.setHumidity(alarmNameStatisticsReq.getHumidity());
        alarmNameStatisticsReq.setHighTemperature(alarmNameStatisticsReq.getHighTemperature());
        alarmNameStatisticsReq.setLowTemperature(alarmNameStatisticsReq.getLowTemperature());
        alarmNameStatisticsReq.setCommunicationInterrupt(alarmNameStatisticsReq.getCommunicationInterrupt());
        alarmNameStatisticsReq.setLeach(alarmNameStatisticsReq.getLeach());
        alarmNameStatisticsReq.setNotClosed(alarmNameStatisticsReq.getNotClosed());
        alarmNameStatisticsReq.setUnLock(alarmNameStatisticsReq.getUnLock());
        alarmNameStatisticsReq.setLean(alarmNameStatisticsReq.getLean());
        alarmNameStatisticsReq.setShake(alarmNameStatisticsReq.getShake());
        alarmNameStatisticsReq.setElectricity(alarmNameStatisticsReq.getElectricity());
        alarmNameStatisticsReq.setViolenceClose(alarmNameStatisticsReq.getViolenceClose());
        alarmNameStatisticsReq.setOrderOutOfTime(alarmNameStatisticsReq.getOrderOutOfTime());
        alarmNameStatisticsReq.setEmergencyLock(alarmNameStatisticsReq.getEmergencyLock());
        alarmNameStatisticsReq.setIllegalOpeningInnerCover(alarmNameStatisticsReq.getIllegalOpeningInnerCover());
        Assert.assertNotNull(alarmNameStatisticsReq);
    }

    @Test
    public void AlarmQueryTemplateTest() {
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        alarmQueryTemplate.setId(alarmQueryTemplate.getId());
        alarmQueryTemplate.setTemplateName(alarmQueryTemplate.getTemplateName());
        alarmQueryTemplate.setTemplateContemt(alarmQueryTemplate.getTemplateContemt());
        alarmQueryTemplate.setIsDeleted(alarmQueryTemplate.getIsDeleted());
        alarmQueryTemplate.setRemark(alarmQueryTemplate.getRemark());
        alarmQueryTemplate.setCreateTime(alarmQueryTemplate.getCreateTime());
        alarmQueryTemplate.setCreateUser(alarmQueryTemplate.getCreateUser());
        alarmQueryTemplate.setAlarmFixedLevel(alarmQueryTemplate.getAlarmFixedLevel());
        alarmQueryTemplate.setAlarmNameList(alarmQueryTemplate.getAlarmNameList());
        alarmQueryTemplate.setAlarmObjectList(alarmQueryTemplate.getAlarmObjectList());
        alarmQueryTemplate.setAreaNameList(alarmQueryTemplate.getAreaNameList());
        alarmQueryTemplate.setAddress(alarmQueryTemplate.getAddress());
        alarmQueryTemplate.setDepartmentList(alarmQueryTemplate.getDepartmentList());
        alarmQueryTemplate.setAlarmSourceTypeId(alarmQueryTemplate.getAlarmSourceTypeId());
        alarmQueryTemplate.setAlarmHappenCount(alarmQueryTemplate.getAlarmHappenCount());
        alarmQueryTemplate.setAlarmCleanStatus(alarmQueryTemplate.getAlarmCleanStatus());
        alarmQueryTemplate.setAlarmConfirmStatus(alarmQueryTemplate.getAlarmConfirmStatus());
        alarmQueryTemplate.setAlarmBeginFrontTime(alarmQueryTemplate.getAlarmBeginFrontTime());
        alarmQueryTemplate.setAlarmBeginQueenTime(alarmQueryTemplate.getAlarmBeginQueenTime());
        alarmQueryTemplate.setAlarmNearFrontTime(alarmQueryTemplate.getAlarmNearFrontTime());
        alarmQueryTemplate.setAlarmNearQueenTime(alarmQueryTemplate.getAlarmNearQueenTime());
        alarmQueryTemplate.setAlarmContinous(alarmQueryTemplate.getAlarmContinous());
        alarmQueryTemplate.setAlarmConfirmFrontTime(alarmQueryTemplate.getAlarmConfirmFrontTime());
        alarmQueryTemplate.setAlarmConfirmQueenTime(alarmQueryTemplate.getAlarmConfirmQueenTime());
        alarmQueryTemplate.setAlarmCleanFrontTime(alarmQueryTemplate.getAlarmCleanFrontTime());
        alarmQueryTemplate.setAlarmCleanQueenTime(alarmQueryTemplate.getAlarmCleanQueenTime());
        alarmQueryTemplate.setAlarmCleanPeopleNickname(alarmQueryTemplate.getAlarmCleanPeopleNickname());
        alarmQueryTemplate.setAlarmConfirmPeopleNickname(alarmQueryTemplate.getAlarmConfirmPeopleNickname());
        alarmQueryTemplate.setExtraMsg(alarmQueryTemplate.getExtraMsg());
        alarmQueryTemplate.setAlarmProcessing(alarmQueryTemplate.getAlarmProcessing());
        Assert.assertNotNull(alarmQueryTemplate);
    }

    @Test
    public void AlarmStatisticsTempTest() {
        AlarmStatisticsTemp alarmStatisticsTemp = new AlarmStatisticsTemp();
        alarmStatisticsTemp.setId(alarmStatisticsTemp.getId());
        alarmStatisticsTemp.setCode(alarmStatisticsTemp.getCode());
        alarmStatisticsTemp.setTemplateName(alarmStatisticsTemp.getTemplateName());
        alarmStatisticsTemp.setCondition(alarmStatisticsTemp.getCondition());
        alarmStatisticsTemp.setPageType(alarmStatisticsTemp.getPageType());
        alarmStatisticsTemp.setStartTime(alarmStatisticsTemp.getStartTime());
        alarmStatisticsTemp.setEndTime(alarmStatisticsTemp.getEndTime());
        alarmStatisticsTemp.setIsDeleted(alarmStatisticsTemp.getIsDeleted());
        alarmStatisticsTemp.setRemark(alarmStatisticsTemp.getRemark());
        alarmStatisticsTemp.setCreateTime(alarmStatisticsTemp.getCreateTime());
        alarmStatisticsTemp.setCreateUser(alarmStatisticsTemp.getCreateUser());
        Assert.assertNotNull(alarmStatisticsTemp);
        Assert.assertNotNull(alarmStatisticsTemp.toString());
    }

    @Test
    public void DeviceNameExportTest() {
        DeviceNameExport deviceNameExport = new DeviceNameExport();
        deviceNameExport.setRanking(deviceNameExport.getRanking());
        deviceNameExport.setDeviceName(deviceNameExport.getDeviceName());
        deviceNameExport.setAreaName(deviceNameExport.getAreaName());
        deviceNameExport.setAddress(deviceNameExport.getAddress());
        deviceNameExport.setAccountabilityUnitName(deviceNameExport.getAccountabilityUnitName());
        deviceNameExport.setStatus(deviceNameExport.getStatus());
        Assert.assertNotNull(deviceNameExport);
    }

    @Test
    public void AlarmCurrentTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString, anyString);
                result = "test";
            }
        };
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        String[] device = {"001", "030", "060", "090", "210"};
        String result;
        for (int i = 1; i < 5; i++) {
            alarmCurrent.setAlarmFixedLevel(String.valueOf(i));
            result = alarmCurrent.getTranslationAlarmFixedLevel();
            Assert.assertNotNull(result);
            alarmCurrent.setAlarmSourceTypeId(device[i]);
            result = alarmCurrent.getTranslationAlarmSourceTypeId();
            Assert.assertNotNull(result);
        }
        alarmCurrent.setAlarmSourceTypeId(device[0]);
        result = alarmCurrent.getTranslationAlarmSourceTypeId();
        Assert.assertNotNull(result);
        alarmCurrent.setAlarmBeginTime(2195969589000L);
        result = alarmCurrent.getTranslationAlarmContinousTime();
        Assert.assertTrue(StringUtils.isEmpty(result));
        alarmCurrent.setAlarmBeginTime(1564817589000L);
        alarmCurrent.setAlarmCleanTime(1564817589000L);
        result = alarmCurrent.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmCurrent.setAlarmCleanTime(1596439989000L);
        result = alarmCurrent.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmCurrent.setAlarmCleanTime(1575358389000L);
        result = alarmCurrent.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmCurrent.setAlarmCleanTime(1565249589000L);
        result = alarmCurrent.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmCurrent.setAlarmCleanTime(1564835589000L);
        result = alarmCurrent.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
    }
}
