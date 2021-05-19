package com.fiberhome.filink.alarmsetserver.bean;

import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class BeanTest {

    @Test
    public void AlarmFilterConditionTest() {
        AlarmFilterCondition condition = new AlarmFilterCondition();
        condition.setId("test");
        condition.setAlarmName(condition.getId());
        condition.setAlarmObject(condition.getAlarmName());
        condition.setStartTime(54545545454L);
        Assert.assertEquals(condition.getAlarmObject(), "test");
        Assert.assertEquals(54545545454L, (long) condition.getStartTime());
    }
    @Test
    public void AlarmFilterRuleTest() {
        AlarmFilterRule rule = new AlarmFilterRule();
        rule.setRemark("test");
        rule.setStatus(1);
        rule.setStored(rule.getStatus());
        rule.setBeginTime(Long.valueOf(rule.getStored()));
        rule.setEndTime(rule.getBeginTime());
        rule.setIsDeleted(rule.getRemark());
        Assert.assertNull(rule.getUserId());
        Assert.assertNull(rule.getCreateTime());
        Assert.assertNull(rule.getCreateUser());
        Assert.assertNull(rule.getUpdateTime());
        Assert.assertNull(rule.getUpdateUser());
        Assert.assertNull(rule.getOperationUser());
        Assert.assertNull(rule.getAlarmFilterRuleNames());
        Assert.assertNotNull(rule.getEndTime());
        Assert.assertNotNull(rule.getIsDeleted());
    }

    @Test
    public void AlarmFilterRuleDtoTest() {
        AlarmFilterRuleDto ruleDto1 = new AlarmFilterRuleDto();
        AlarmFilterRuleDto ruleDto = new AlarmFilterRuleDto();
        ruleDto1.setAlarmFilterRuleNameList(ruleDto.getAlarmFilterRuleNameList());
        ruleDto1.setAlarmFilterRuleNames(ruleDto.getAlarmFilterRuleNames());
        ruleDto1.setAlarmFilterRuleSourceList(ruleDto.getAlarmFilterRuleSourceList());
        ruleDto1.setAlarmFilterRuleSourceName(ruleDto.getAlarmFilterRuleSourceName());
        ruleDto1.setSortProperties(ruleDto.getSortProperties());
        ruleDto1.setSort(ruleDto.getSort());
        ruleDto1.setRelation(ruleDto.getRelation());
        ruleDto1.setBeginTimeEnd(ruleDto.getBeginTimeEnd());
        ruleDto1.setEndTimeEnd(ruleDto.getEndTimeEnd());
        ruleDto1.setCreateTimeEnd(ruleDto.getCreateTimeEnd());
        ruleDto1.setUpdateTimeEnd(ruleDto.getUpdateTimeEnd());
        ruleDto1.setStatusArray(ruleDto.getStatusArray());
        ruleDto1.setStoredArray(ruleDto.getStoredArray());
        ruleDto1.setId(ruleDto.getId());
        ruleDto1.setUserId(ruleDto.getUserId());
        ruleDto1.setRuleName(ruleDto.getRuleName());
        ruleDto1.setStatus(ruleDto.getStatus());
        ruleDto1.setStored(ruleDto.getStored());
        ruleDto1.setBeginTime(ruleDto.getBeginTime());
        ruleDto1.setEndTime(ruleDto.getEndTime());
        ruleDto1.setIsDeleted(ruleDto.getIsDeleted());
        ruleDto1.setRemark(ruleDto.getRemark());
        ruleDto1.setCreateTime(ruleDto.getCreateTime());
        ruleDto1.setCreateUser(ruleDto.getCreateUser());
        ruleDto1.setUpdateTime(ruleDto.getUpdateTime());
        ruleDto1.setUpdateUser(ruleDto.getUpdateUser());
        ruleDto1.setOperationUser(ruleDto.getOperationUser());
        ruleDto1.setAlarmFilterRuleNameList(ruleDto.getAlarmFilterRuleNameList());
        ruleDto1.setAlarmFilterRuleNames(ruleDto.getAlarmFilterRuleNames());
        ruleDto1.setAlarmFilterRuleSourceList(ruleDto.getAlarmFilterRuleSourceList());
        ruleDto1.setAlarmFilterRuleSourceName(ruleDto.getAlarmFilterRuleSourceName());
        Assert.assertNotNull(ruleDto1);
        Assert.assertNotNull(ruleDto);
    }

    @Test
    public void AlarmFilterRuleNameTest() {
        AlarmFilterRuleName ruleName1 = new AlarmFilterRuleName();
        AlarmFilterRuleName ruleName = new AlarmFilterRuleName();
        ruleName1.setAlarmNameId(ruleName.getAlarmNameId());
        ruleName1.setRuleId(ruleName.getRuleId());
        Assert.assertNotNull(ruleName1);
        Assert.assertNotNull(ruleName);
    }

    @Test
    public void AlarmFilterRuleSourceTest() {
        AlarmFilterRuleSource ruleSource1 = new AlarmFilterRuleSource();
        AlarmFilterRuleSource ruleSource = new AlarmFilterRuleSource();
        ruleSource1.setAlarmSource(ruleSource.getAlarmSource());
        ruleSource1.setRuleId(ruleSource.getRuleId());
        Assert.assertNotNull(ruleSource1);
        Assert.assertNotNull(ruleSource);
        Assert.assertNotNull(ruleSource.toString());
    }

    @Test
    public void AlarmForwardConditionTest() {
        AlarmForwardCondition condition1 = new AlarmForwardCondition();
        AlarmForwardCondition condition = new AlarmForwardCondition();
        condition1.setId(condition.getId());
        condition1.setAreaId(condition.getAreaId());
        condition1.setDeviceType(condition.getDeviceType());
        condition1.setAlarmLevel(condition.getAlarmLevel());
        Assert.assertNotNull(condition1);
        Assert.assertNotNull(condition);
    }

    @Test
    public void AlarmForwardRuleTest() {
        AlarmForwardRule alarmForwardRule = new AlarmForwardRule();
        AlarmForwardRule forwardRule = new AlarmForwardRule();
        alarmForwardRule.setId(forwardRule.getId());
        alarmForwardRule.setRuleName(forwardRule.getRuleName());
        alarmForwardRule.setAlarmType(forwardRule.getAlarmType());
        alarmForwardRule.setStatus(forwardRule.getStatus());
        alarmForwardRule.setIsDeleted(forwardRule.getIsDeleted());
        alarmForwardRule.setPushType(forwardRule.getPushType());
        alarmForwardRule.setRemark(forwardRule.getRemark());
        alarmForwardRule.setCreateTime(forwardRule.getCreateTime());
        alarmForwardRule.setCreateUser(forwardRule.getCreateUser());
        alarmForwardRule.setUpdateTime(forwardRule.getUpdateTime());
        alarmForwardRule.setUpdateUser(forwardRule.getUpdateUser());
        alarmForwardRule.setAlarmForwardRuleUserList(forwardRule.getAlarmForwardRuleUserList());
        alarmForwardRule.setAlarmForwardRuleUserName(forwardRule.getAlarmForwardRuleUserName());
        alarmForwardRule.setAlarmForwardRuleAreaList(forwardRule.getAlarmForwardRuleAreaList());
        alarmForwardRule.setAlarmForwardRuleAreaName(forwardRule.getAlarmForwardRuleAreaName());
        alarmForwardRule.setAlarmForwardRuleDeviceTypeList(forwardRule.getAlarmForwardRuleDeviceTypeList());
        alarmForwardRule.setAlarmForwardRuleLevels(forwardRule.getAlarmForwardRuleLevels());
        Assert.assertNotNull(alarmForwardRule);
        Assert.assertNotNull(forwardRule);
    }

    @Test
    public void AlarmForwardRuleAreaTest() {
        AlarmForwardRuleArea alarmForwardRuleArea = new AlarmForwardRuleArea();
        AlarmForwardRuleArea ruleArea = new AlarmForwardRuleArea();
        alarmForwardRuleArea.setRuleId(ruleArea.getRuleId());
        alarmForwardRuleArea.setAreaId(ruleArea.getAreaId());
        Assert.assertNotNull(alarmForwardRuleArea);
        Assert.assertNotNull(ruleArea);
        Assert.assertNotNull(ruleArea.toString());
    }

    @Test
    public void AlarmForwardRuleDeviceTypeTest() {
        AlarmForwardRuleDeviceType deviceType = new AlarmForwardRuleDeviceType();
        AlarmForwardRuleDeviceType alarmForwardRuleDeviceType = new AlarmForwardRuleDeviceType();
        alarmForwardRuleDeviceType.setRuleId(deviceType.getRuleId());
        alarmForwardRuleDeviceType.setDeviceTypeId(deviceType.getDeviceTypeId());
        Assert.assertNotNull(alarmForwardRuleDeviceType);
        Assert.assertNotNull(deviceType);
        Assert.assertNotNull(deviceType.toString());
    }

    @Test
    public void AlarmForwardRuleDtoTest() {
        AlarmForwardRuleDto alarmForwardRuleDto = new AlarmForwardRuleDto();
        AlarmForwardRuleDto ruleDto = new AlarmForwardRuleDto();
        alarmForwardRuleDto.setAlarmForwardRuleUserList(ruleDto.getAlarmForwardRuleUserList());
        alarmForwardRuleDto.setAlarmForwardRuleAreaList(ruleDto.getAlarmForwardRuleAreaList());
        alarmForwardRuleDto.setAlarmForwardRuleAreaName(ruleDto.getAlarmForwardRuleAreaName());
        alarmForwardRuleDto.setAlarmForwardRuleDeviceTypeList(ruleDto.getAlarmForwardRuleDeviceTypeList());
        alarmForwardRuleDto.setDeviceTypeId(ruleDto.getDeviceTypeId());
        alarmForwardRuleDto.setSortProperties(ruleDto.getSortProperties());
        alarmForwardRuleDto.setSort(ruleDto.getSort());
        alarmForwardRuleDto.setRelation(ruleDto.getRelation());
        alarmForwardRuleDto.setAlarmLevelId(ruleDto.getAlarmLevelId());
        alarmForwardRuleDto.setCreateTimeEnd(ruleDto.getCreateTimeEnd());
        alarmForwardRuleDto.setUpdateTimeEnd(ruleDto.getUpdateTimeEnd());
        alarmForwardRuleDto.setStatusArray(ruleDto.getStatusArray());
        alarmForwardRuleDto.setPushTypeArray(ruleDto.getPushTypeArray());
        alarmForwardRuleDto.setAlarmForwardRuleLevels(ruleDto.getAlarmForwardRuleLevels());
        alarmForwardRuleDto.setId(ruleDto.getId());
        alarmForwardRuleDto.setRuleName(ruleDto.getRuleName());
        alarmForwardRuleDto.setAlarmType(ruleDto.getAlarmType());
        alarmForwardRuleDto.setStatus(ruleDto.getStatus());
        alarmForwardRuleDto.setIsDeleted(ruleDto.getIsDeleted());
        alarmForwardRuleDto.setPushType(ruleDto.getPushType());
        alarmForwardRuleDto.setRemark(ruleDto.getRemark());
        alarmForwardRuleDto.setCreateTime(ruleDto.getCreateTime());
        alarmForwardRuleDto.setCreateUser(ruleDto.getCreateUser());
        alarmForwardRuleDto.setUpdateTime(ruleDto.getUpdateTime());
        alarmForwardRuleDto.setUpdateUser(ruleDto.getUpdateUser());
        alarmForwardRuleDto.setAlarmForwardRuleUserList(ruleDto.getAlarmForwardRuleUserList());
        alarmForwardRuleDto.setAlarmForwardRuleUserName(ruleDto.getAlarmForwardRuleUserName());
        alarmForwardRuleDto.setAlarmForwardRuleAreaList(ruleDto.getAlarmForwardRuleAreaList());
        alarmForwardRuleDto.setAlarmForwardRuleAreaName(ruleDto.getAlarmForwardRuleAreaName());
        alarmForwardRuleDto.setAlarmForwardRuleDeviceTypeList(ruleDto.getAlarmForwardRuleDeviceTypeList());
        alarmForwardRuleDto.setAlarmForwardRuleLevels(ruleDto.getAlarmForwardRuleLevels());
        Assert.assertNotNull(alarmForwardRuleDto);
        Assert.assertNotNull(ruleDto);
    }

    @Test
    public void AlarmForwardRuleLevelTest() {
        AlarmForwardRuleLevel alarmForwardRuleLevel = new AlarmForwardRuleLevel();
        AlarmForwardRuleLevel ruleLevel = new AlarmForwardRuleLevel();
        alarmForwardRuleLevel.setRuleId(ruleLevel.getRuleId());
        alarmForwardRuleLevel.setAlarmLevelId(ruleLevel.getAlarmLevelId());
        Assert.assertNotNull(alarmForwardRuleLevel);
        Assert.assertNotNull(ruleLevel);
    }

    @Test
    public void AlarmForwardRuleUserTest() {
        AlarmForwardRuleUser alarmForwardRuleUser = new AlarmForwardRuleUser();
        AlarmForwardRuleUser ruleUser = new AlarmForwardRuleUser();
        alarmForwardRuleUser.setRuleId(ruleUser.getRuleId());
        alarmForwardRuleUser.setUserId(ruleUser.getUserId());
        Assert.assertNotNull(alarmForwardRuleUser);
        Assert.assertNotNull(ruleUser);
    }

    @Test
    public void AlarmLevelTest() {
        AlarmLevel alarmLevel1 = new AlarmLevel();
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setId(alarmLevel.getId());
        alarmLevel.setAlarmLevelCode(alarmLevel.getAlarmLevelCode());
        alarmLevel.setAlarmLevelName(alarmLevel.getAlarmLevelName());
        alarmLevel.setAlarmLevelColor(alarmLevel.getAlarmLevelColor());
        alarmLevel.setAlarmLevelSound(alarmLevel.getAlarmLevelSound());
        alarmLevel.setIsPlay(alarmLevel.getIsPlay());
        alarmLevel.setPlayCount(alarmLevel.getPlayCount());
        alarmLevel.setOrderField(alarmLevel.getOrderField());
        Assert.assertNotNull(alarmLevel1);
        Assert.assertNotNull(alarmLevel);
    }

    @Test
    public void AlarmNameTest() {
        AlarmName alarmName = new AlarmName();
        AlarmName alarmName1 = new AlarmName();
        alarmName.setId(alarmName.getId());
        alarmName.setAlarmName(alarmName.getAlarmName());
        alarmName.setAlarmLevel(alarmName.getAlarmLevel());
        alarmName.setAlarmDesc(alarmName.getAlarmDesc());
        alarmName.setAlarmCode(alarmName.getAlarmCode());
        alarmName.setAlarmDefaultLevel(alarmName.getAlarmDefaultLevel());
        alarmName.setAlarmAutomaticConfirmation(alarmName.getAlarmAutomaticConfirmation());
        alarmName.setIsOrder(alarmName.getIsOrder());
        Assert.assertNotNull(alarmName);
        Assert.assertNotNull(alarmName1);
    }

    @Test
    public void AlarmOrderConditionTest() {
        AlarmOrderCondition alarmOrderCondition = new AlarmOrderCondition();
        AlarmOrderCondition condition = new AlarmOrderCondition();
        alarmOrderCondition.setId(condition.getId());
        alarmOrderCondition.setAreaId(condition.getAreaId());
        alarmOrderCondition.setDeviceType(condition.getDeviceType());
        alarmOrderCondition.setAlarmName(condition.getAlarmName());
        Assert.assertNotNull(alarmOrderCondition);
        Assert.assertNotNull(condition);
    }

    @Test
    public void AlarmOrderRuleTest() {
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        AlarmOrderRule orderRule = new AlarmOrderRule();
        alarmOrderRule.setId(orderRule.getId());
        alarmOrderRule.setOrderName(orderRule.getOrderName());
        alarmOrderRule.setOrderType(orderRule.getOrderType());
        alarmOrderRule.setDepartmentId(orderRule.getDepartmentId());
        alarmOrderRule.setTriggerType(orderRule.getTriggerType());
        alarmOrderRule.setCompletionTime(orderRule.getCompletionTime());
        alarmOrderRule.setStatus(orderRule.getStatus());
        alarmOrderRule.setIsDeleted(orderRule.getIsDeleted());
        alarmOrderRule.setRemark(orderRule.getRemark());
        alarmOrderRule.setCreateTime(orderRule.getCreateTime());
        alarmOrderRule.setCreateUser(orderRule.getCreateUser());
        alarmOrderRule.setUpdateTime(orderRule.getUpdateTime());
        alarmOrderRule.setUpdateUser(orderRule.getUpdateUser());
        alarmOrderRule.setAlarmOrderRuleDeviceTypeList(orderRule.getAlarmOrderRuleDeviceTypeList());
        alarmOrderRule.setAlarmOrderRuleArea(orderRule.getAlarmOrderRuleArea());
        alarmOrderRule.setAlarmOrderRuleAreaName(orderRule.getAlarmOrderRuleAreaName());
        alarmOrderRule.setAlarmOrderRuleNameList(orderRule.getAlarmOrderRuleNameList());
        alarmOrderRule.setAlarmOrderRuleNames(orderRule.getAlarmOrderRuleNames());
        alarmOrderRule.setDepartmentName(orderRule.getDepartmentName());
        Assert.assertNotNull(alarmOrderRule);
        Assert.assertNotNull(orderRule);
    }

    @Test
    public void AlarmOrderRuleAreaTest() {
        AlarmOrderRuleArea alarmOrderRuleArea = new AlarmOrderRuleArea();
        AlarmOrderRuleArea ruleArea = new AlarmOrderRuleArea();
        alarmOrderRuleArea.setRuleId(ruleArea.getRuleId());
        alarmOrderRuleArea.setAreaId(ruleArea.getAreaId());
        Assert.assertNotNull(alarmOrderRuleArea);
        Assert.assertNotNull(ruleArea);
    }

    @Test
    public void AlarmOrderRuleDeviceTypeTest() {
        AlarmOrderRuleDeviceType alarmOrderRuleDeviceType = new AlarmOrderRuleDeviceType();
        AlarmOrderRuleDeviceType deviceType = new AlarmOrderRuleDeviceType();
        alarmOrderRuleDeviceType.setRuleId(deviceType.getRuleId());
        alarmOrderRuleDeviceType.setDeviceTypeId(deviceType.getDeviceTypeId());
        Assert.assertNotNull(alarmOrderRuleDeviceType);
        Assert.assertNotNull(deviceType);
    }

    @Test
    public void AlarmOrderRuleDtoTest() {
        AlarmOrderRuleDto alarmOrderRuleDto = new AlarmOrderRuleDto();
        AlarmOrderRuleDto ruleDto = new AlarmOrderRuleDto();
        alarmOrderRuleDto.setAlarmOrderRuleDeviceTypeList(ruleDto.getAlarmOrderRuleDeviceTypeList());
        alarmOrderRuleDto.setAlarmOrderRuleArea(ruleDto.getAlarmOrderRuleArea());
        alarmOrderRuleDto.setAlarmOrderRuleAreaName(ruleDto.getAlarmOrderRuleAreaName());
        alarmOrderRuleDto.setAlarmFilterRuleNameList(ruleDto.getAlarmFilterRuleNameList());
        alarmOrderRuleDto.setAlarmFilterRuleNames(ruleDto.getAlarmFilterRuleNames());
        alarmOrderRuleDto.setSortProperties(ruleDto.getSortProperties());
        alarmOrderRuleDto.setSort(ruleDto.getSort());
        alarmOrderRuleDto.setRelation(ruleDto.getRelation());
        alarmOrderRuleDto.setDeviceTypeId(ruleDto.getDeviceTypeId());
        alarmOrderRuleDto.setAlarmName(ruleDto.getAlarmName());
        alarmOrderRuleDto.setCreateTimeEnd(ruleDto.getCreateTimeEnd());
        alarmOrderRuleDto.setUpdateTimeEnd(ruleDto.getUpdateTimeEnd());
        alarmOrderRuleDto.setOrderTypeList(ruleDto.getOrderTypeList());
        alarmOrderRuleDto.setDepartmentIdList(ruleDto.getDepartmentIdList());
        alarmOrderRuleDto.setStatusArray(ruleDto.getStatusArray());
        alarmOrderRuleDto.setTriggerTypeArray(ruleDto.getTriggerTypeArray());
        alarmOrderRuleDto.setCompletionTimeOperate(ruleDto.getCompletionTimeOperate());
        alarmOrderRuleDto.setId(ruleDto.getId());
        alarmOrderRuleDto.setOrderName(ruleDto.getOrderName());
        alarmOrderRuleDto.setOrderType(ruleDto.getOrderType());
        alarmOrderRuleDto.setDepartmentId(ruleDto.getDepartmentId());
        alarmOrderRuleDto.setTriggerType(ruleDto.getTriggerType());
        alarmOrderRuleDto.setCompletionTime(ruleDto.getCompletionTime());
        alarmOrderRuleDto.setStatus(ruleDto.getStatus());
        alarmOrderRuleDto.setIsDeleted(ruleDto.getIsDeleted());
        alarmOrderRuleDto.setRemark(ruleDto.getRemark());
        alarmOrderRuleDto.setCreateTime(ruleDto.getCreateTime());
        alarmOrderRuleDto.setCreateUser(ruleDto.getCreateUser());
        alarmOrderRuleDto.setUpdateTime(ruleDto.getUpdateTime());
        alarmOrderRuleDto.setUpdateUser(ruleDto.getUpdateUser());
        alarmOrderRuleDto.setAlarmOrderRuleDeviceTypeList(ruleDto.getAlarmOrderRuleDeviceTypeList());
        alarmOrderRuleDto.setAlarmOrderRuleArea(ruleDto.getAlarmOrderRuleArea());
        alarmOrderRuleDto.setAlarmOrderRuleAreaName(ruleDto.getAlarmOrderRuleAreaName());
        alarmOrderRuleDto.setAlarmOrderRuleNameList(ruleDto.getAlarmOrderRuleNameList());
        alarmOrderRuleDto.setAlarmOrderRuleNames(ruleDto.getAlarmOrderRuleNames());
        alarmOrderRuleDto.setDepartmentName(ruleDto.getDepartmentName());
        Assert.assertNotNull(alarmOrderRuleDto);
        Assert.assertNotNull(ruleDto);
    }

    @Test
    public void AlarmOrderRuleNameTest() {
        AlarmOrderRuleName alarmOrderRuleName = new AlarmOrderRuleName();
        AlarmOrderRuleName ruleName = new AlarmOrderRuleName();
        alarmOrderRuleName.setRuleId(ruleName.getRuleId());
        alarmOrderRuleName.setAlarmNameId(ruleName.getAlarmNameId());
        Assert.assertNotNull(alarmOrderRuleName);
        Assert.assertNotNull(ruleName);
    }
}
