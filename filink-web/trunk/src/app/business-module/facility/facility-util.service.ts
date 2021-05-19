/**
 * Created by xiaoconghu on 2019/1/15.
 */
import {Result} from '../../shared-module/entity/result';
import {AreaService} from '../../core-module/api-service/facility/area-manage';
import {UserService} from '../../core-module/api-service/user/user-manage';
import {Injectable} from '@angular/core';
import {DeviceStatusCode} from './facility.config';

@Injectable()
export class FacilityUtilService {

  constructor(private $areaService: AreaService,
              private $userService: UserService) {
  }

  /**
   * 获取设施状态图标样式
   * param {string} deviceStatus
   * returns {{iconClass: string; colorClass: string}}
   */
  public static getFacilityDeviceStatusClassName(deviceStatus: string): { iconClass: string, colorClass: string } {
    let iconClass = '', colorClass = '';
    switch (deviceStatus) {
      case DeviceStatusCode.NORMAL:
        iconClass = 'fiLink-normal';
        colorClass = 'facility-normal-c';
        break;
      case DeviceStatusCode.ALARM:
        iconClass = 'fiLink-alarm';
        colorClass = 'facility-alarm-c';
        break;
      case DeviceStatusCode.UNKNOWN:
        iconClass = 'fiLink-unknown';
        colorClass = 'facility-unknown-c';
        break;
      case DeviceStatusCode.OFFLINE:
        iconClass = 'fiLink-lost';
        colorClass = 'facility-offline-c';
        break;
      case DeviceStatusCode.OUT_OF_CONTACT:
        iconClass = 'fiLink-serious';
        colorClass = 'facility-lost-c';
    }
    return {iconClass, colorClass};
  }

  /**
   * 获取区域列表信息
   * returns {Promise<any>}
   */
  getArea() {
    return new Promise((resolve, reject) => {
      this.$areaService.selectForeignAreaInfoForPageCollection([]).subscribe((result: Result) => {
        const data = result.data || [];
        resolve(data);
      });
    });

  }

  /**
   * 获取单位列表信息
   * returns {Promise<any>}
   */
  getDept() {
    return new Promise((resolve, reject) => {
      this.$userService.queryTotalDept().subscribe((result: Result) => {
        const data = result.data || [];
        resolve(data);
      });
    });

  }

  /**
   * 递归设置区域的被选状态
   * param data
   * param parentId
   */
  public setAreaNodesStatus(data, parentId, areaId?) {
    data.forEach(areaNode => {
      // 选中父亲
      areaNode.checked = areaNode.areaId === parentId;
      // 自己不让选 没权限不让选
      areaNode.chkDisabled = (areaNode.areaId === areaId) || (areaNode.hasPermissions === false);
      // 如果是已经选中可以选
      if (areaNode.checked) {
        areaNode.chkDisabled = false;
      }
      if (areaNode.children) {
        this.setAreaNodesStatus(areaNode.children, parentId, areaId);
      }
    });
  }

  /**
   * 中式区域需求变更 区域选择默认有权限
   * param data
   * param parentId
   * param areaId
   */
  public setAreaNodesStatusUnlimited(data, parentId, areaId?) {
    data.forEach(areaNode => {
      // 选中父亲
      areaNode.checked = areaNode.areaId === parentId;
      // 自己不让选
      areaNode.chkDisabled = areaNode.areaId === areaId;
      // 如果是已经选中可以选
      if (areaNode.checked) {
        areaNode.chkDisabled = false;
      }
      if (areaNode.children) {
        this.setAreaNodesStatusUnlimited(areaNode.children, parentId, areaId);
      }
    });
  }

  /**
   * 递归设置区域的被选状态(多选)
   * param data
   * param selectData
   */
  public setAreaNodesMultiStatus(data, selectData) {
    data.forEach(areaNode => {
      // // 选中父亲
      // areaNode.checked = areaNode.areaId === parentId;
      // // 自己不让选 没权限不让选
      // areaNode.chkDisabled = (areaNode.areaId === areaId) || (areaNode.hasPermissions === false);
      // // 如果是已经选中可以选
      // if (areaNode.checked) {
      //   areaNode.chkDisabled = false;
      // }
      // if (areaNode.children) {
      //   this.setAreaNodesStatus(areaNode.children, parentId, areaId);
      // }
      // 从被选择的数组中找到当前项
      const index = selectData.findIndex(item => areaNode.areaId === item);
      // 如果找到了 checked 为true
      areaNode.checked = !(index === -1);
      if (areaNode.checked) {
        areaNode.chkDisabled = false;
      }
      if (areaNode.children) {
        this.setAreaNodesMultiStatus(areaNode.children, selectData);
      }
    });
  }

  /**
   * 递归设置责任单位的被选状态
   * param data
   * param selectData
   */
  public setTreeNodesStatus(data, selectData: string[], bol?) {
    if (!bol) {
      data.forEach(treeNode => {
        // 从被选择的数组中找到当前项
        const index = selectData.findIndex(item => treeNode.id === item);
        // 如果找到了 checked 为true
        treeNode.checked = !(index === -1);
        if (treeNode.childDepartmentList) {
          this.setTreeNodesStatus(treeNode.childDepartmentList, selectData);
        }
      });
    } else {
      data.forEach(treeNode => {
        // 从被选择的数组中找到当前项
        const index = selectData.findIndex(item => treeNode.deviceId === item);
        // 如果找到了 checked 为true
        treeNode.checked = !(index === -1);
        if (treeNode.childDepartmentList) {
          this.setTreeNodesStatus(treeNode.childDepartmentList, selectData, true);
        }
      });
    }
  }

  /**
   * 告警转工单 和 告警远程通知 使用到了
   * 递归勾选传入的数据
   * param data
   * param parentId
   * param areaId
   */
  public setAlarmAreaNodesStatus(data, parentId, areaId?) {
    data.forEach(areaNode => {
      areaId.forEach(item => {
        // 选中父亲
        areaNode.checked = areaNode.areaId === parentId;
        // 自己不让选
        areaNode.chkDisabled = (areaNode.areaId === item) || (areaNode.hasPermissions === false);
        if (areaNode.children) {
          this.setAreaNodesStatus(areaNode.children, parentId, item);
        }
      });
    });
  }


}
